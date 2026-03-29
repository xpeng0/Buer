package com.cscyxp.buer

import app.cash.turbine.test
import com.cscyxp.buer.utils.TimeHelper
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

private const val TAG = "AddViewModelTest"
@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest {

    // 模拟Android main线程  防止测试环境没有Android环境报错
    private val testDispatcher = UnconfinedTestDispatcher()
    private val transactionRepository = mockk<TransactionRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)
    private val timeHelper = mockk<TimeHelper>(relaxed = true)

    fun createViewmodel(): AddViewModel {
        return AddViewModel(transactionRepository, categoryRepository, timeHelper)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectedCategoryState - initial value should be null`() = runTest {
        val viewModel = createViewmodel()
        viewModel.selectedCategoryState.test {
            // 验证初始值
            val initVal = awaitItem()
            initVal.page.shouldBe(-1)
            initVal.grid.shouldBe(-1)
            initVal.category.shouldBe(null)
        }
    }

    @Test
    fun `onTagClick - should emit state correctly`() = runTest {
        val viewModel = createViewmodel()
        val page = 3
        val grid = 5
        val category = Category(id = 1L, name = "", type = 0, icon = "")
        viewModel.selectedCategoryState.test {
            awaitItem() // 初始值
            viewModel.onTagClick(page, grid, category)
            val state = awaitItem()
            state.page.shouldBe(page)
            state.grid.shouldBe(grid)
            state.category.shouldBe(category)

            // 重复点击
            viewModel.onTagClick(page, grid, category)
            val repeatState = awaitItem()
            repeatState.page.shouldBe(-1)
            repeatState.grid.shouldBe(-1)
            repeatState.category.shouldBe(null)

        }
    }

    @Test
    fun `onSonCategoryClick - should emit state correctly`() = runTest {
        val viewModel = createViewmodel()
        val page = 3
        val grid = 5
        val category = Category(id = 1L, name = "", type = 0, icon = "")
        val sonCategory = Category(id = 2L, name = "", type = 0, icon = "")
        viewModel.selectedCategoryState.test {
            awaitItem() // 初始值
            viewModel.onTagClick(page, grid, category)
            val state = awaitItem()
            state.page.shouldBe(page)
            state.grid.shouldBe(grid)
            state.category.shouldBe(category)

            // 点击子类
            viewModel.onSonCategoryClick(sonCategory)
            val state2 = awaitItem()
            state2.page.shouldBe(page)
            state2.grid.shouldBe(grid)
            state2.category.shouldBe(sonCategory)

        }
    }

    @Test
    fun `date - initial value should be null and emit correctly`() = runTest {
        val viewModel = createViewmodel()
        val testDate = LocalDate.now().minusDays(1)
        val utc = testDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        viewModel.date.test {
            awaitItem().shouldBe(null) // 初始值
            viewModel.onDateCheckListener(utc)
            awaitItem().shouldBe(testDate)
        }
    }

    @Test
    fun `getCheckDate - should get checked date correctly`() = runTest {
        val viewModel = createViewmodel()
        val current = 100L
        every { timeHelper.currentTimeMillis() } returns current
        // 默认所选时间戳为当前时间
        viewModel.getCheckDate().shouldBe(current)

        // 日期选昨天 时间戳应当是昨天最后一刻
        val testDate = LocalDate.now().minusDays(1)
        val utc = testDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        viewModel.onDateCheckListener(utc)
        viewModel.getCheckDate().shouldBe(testDate.toEndOfDay(ZoneId.systemDefault()))
    }

    @Test
    fun `handleKeyAction - should handle date key correctly`() = runTest {
        val viewModel = createViewmodel()
        viewModel.openDatePicker.test {
            viewModel.handleKeyAction(KeyAction.DATE)
            awaitItem().shouldBe(AddViewModel.OPEN_DATE_PICKER)
        }
    }

    @Test
    fun `handleKeyAction - should handle ok key correctly`() = runTest {
        val viewModel = createViewmodel()
        val currentTimeMillis = 100L
        every { timeHelper.currentTimeMillis() } returns currentTimeMillis
        val actions = "100.99[ok]".toKeyActions()
        val category = Category(id = 1L, name = "", type = 0, icon = "")
        val transaction = Transaction(amount = 100.99, categoryId = 1, date = currentTimeMillis)

        viewModel.back.test {
            viewModel.onTagClick(0, 0, category)
            actions.forEach {
                viewModel.handleKeyAction(it)
            }
            coVerify(exactly = 1) { transactionRepository.addTransaction(transaction) }
            awaitItem().shouldBe(AddViewModel.BACK)
        }
    }

    @Test
    fun `handleKeyAction - should add category 0 when not select category`() = runTest {
        val viewModel = createViewmodel()
        val currentTimeMillis = 100L
        every { timeHelper.currentTimeMillis() } returns currentTimeMillis
        val actions = "100.99[ok]".toKeyActions()
        val category = Category(id = 1L, name = "", type = 0, icon = "")
        val transaction = Transaction(amount = 100.99, categoryId = 0, date = currentTimeMillis)

        viewModel.back.test {
            actions.forEach {
                viewModel.handleKeyAction(it)
            }
            coVerify(exactly = 1) { transactionRepository.addTransaction(transaction) }
            awaitItem().shouldBe(AddViewModel.BACK)
        }
    }


    @ParameterizedTest
    @MethodSource("provideKeyActionInput")
    fun `handleKeyAction - should display input correctly`(description: String, actions: List<KeyAction>, expected: String) =
        runTest {
            val viewModel = createViewmodel()
            println("handleKeyAction - should display input correctly: actions: $actions")
            actions.forEach {
                viewModel.handleKeyAction(it)
            }
            viewModel.amount.value.shouldBe(expected)
        }

    companion object {
        @JvmStatic
        fun provideKeyActionInput(): List<Arguments> {
            return listOf(
                Arguments.of("自动补0", ".66".toKeyActions(), "0.66"),
                Arguments.of("超过两位小数", "1.256789".toKeyActions(), "1.25"),
                Arguments.of("多个小数点", "1...5".toKeyActions(), "1.5"),
                Arguments.of("多个小数点", "1.5.4.5".toKeyActions(), "1.54"),
                Arguments.of("正常删除", "23[del]4".toKeyActions(), "24"),
                Arguments.of("前置删除", "[del][del]264".toKeyActions(), "264"),
            )
        }
    }

}