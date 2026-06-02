package com.cscyxp.bookkeeping

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.cscyxp.bookkeeping.data.repository.CategoryRepository
import com.cscyxp.bookkeeping.data.repository.TransactionRepository
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.DailyTransaction
import com.cscyxp.bookkeeping.domain.TransactionFilter
import com.cscyxp.bookkeeping.ui.state.TransactionListUiState
import com.cscyxp.bookkeeping.util.TimeHelper
import com.cscyxp.bookkeeping.utils.CategoryGenerator
import com.cscyxp.bookkeeping.utils.TransactionGenerator
import com.cscyxp.bookkeeping.vm.TransactionListViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {
    private val transactionRepository = mockk<TransactionRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)
    private val timeHelper = mockk<TimeHelper>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val year = 1111
    private val month = 1
    private val category = Category(id = 1L, name = "随机", type = 0, icon = "")
    private val filter = TransactionFilter(month = month, year = year, category = category)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every {
            transactionRepository.getDailyTransactionsFlowByFilter(any(), any(), any())
        } returns flowOf(emptyList())
        every { categoryRepository.getTopCategories(any()) } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): TransactionListViewModel {
        return TransactionListViewModel(transactionRepository, categoryRepository, timeHelper)
    }

    @Test
    fun `uiState - should emit loading before content`() = runTest {
        every {
            transactionRepository.getDailyTransactionsFlowByFilter(any(), any(), any())
        } returns flow {
            delay(100)
            emit(emptyList())
        }

        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem().shouldBe(TransactionListUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState - should calculate sum correctly`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<DailyTransaction>())
        every {
            transactionRepository.getDailyTransactionsFlowByFilter(any(), any(), any())
        } returns fakeFlow

        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitContent()

            val generateDailyTransactions = TransactionGenerator.generateDailyTransactions()
            fakeFlow.emit(generateDailyTransactions.dailyTransactions)

            val content = awaitContent()
            content.incomeSumStr.shouldBe(generateDailyTransactions.totalIncome)
            content.expenseSumStr.shouldBe(generateDailyTransactions.totalExpense)
            content.balanceStr.shouldBe(generateDailyTransactions.balance)
            content.dailyTransactions.shouldBe(
                expected = generateDailyTransactions.dailyTransactions,
                isPrint = false
            )
        }
    }

    @Test
    fun `uiState - should pass filter to repository correctly`() = runTest {
        val startMonth = 1000L
        val endMonth = 2000L
        every { timeHelper.getMonthStartTimeMillis(year, month) } returns startMonth
        every { timeHelper.getMonthEndTimeMillis(year, month) } returns endMonth

        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitContent()
            viewModel.updateFilter(filter)
            val content = awaitContent()

            content.filter.shouldBe(filter)
            verify(exactly = 1) {
                timeHelper.getMonthStartTimeMillis(year, month)
                timeHelper.getMonthEndTimeMillis(year, month)
                transactionRepository.getDailyTransactionsFlowByFilter(startMonth, endMonth, categoryId = 1L)
            }
        }
    }

    @Test
    fun `set filter - should update uiState filter correctly`() = runTest {
        val viewModel = createViewModel()
        val currentYear = LocalDate.now().year
        val newMonth = 2
        val newCategory = category.copy(id = 2L)

        viewModel.uiState.test {
            awaitContent()

            viewModel.setMonth(newMonth)
            awaitContent().filter.shouldBe(TransactionFilter(month = newMonth, year = currentYear))

            viewModel.setYear(year)
            awaitContent().filter.shouldBe(TransactionFilter(month = newMonth, year = year))

            viewModel.setCategory(newCategory)
            awaitContent().filter.shouldBe(TransactionFilter(month = newMonth, year = year, category = newCategory))
        }
    }

    @Test
    fun `category dialog filter - should emit categories by type`() = runTest {
        val expendCategories = CategoryGenerator.generatorRandomCategories(count = 10, type = Category.TYPE_EXPAND)
        val incomeCategories = CategoryGenerator.generatorRandomCategories(count = 10, type = Category.TYPE_INCOME)

        every { categoryRepository.getTopCategories(Category.TYPE_EXPAND) } returns flowOf(expendCategories)
        every { categoryRepository.getTopCategories(Category.TYPE_INCOME) } returns flowOf(incomeCategories)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitContent().topCategories.shouldBe(expendCategories, isPrint = false)

            viewModel.setCategoryDialogFilterType(Category.TYPE_INCOME)
            val content = awaitContent()

            content.categoryDialogFilterType.shouldBe(Category.TYPE_INCOME)
            content.topCategories.shouldBe(incomeCategories, isPrint = false)
            verify(exactly = 1) {
                categoryRepository.getTopCategories(Category.TYPE_INCOME)
            }
        }
    }

    @Test
    fun `updateTransaction - should pass transaction to repository update correctly`() = runTest {
        val viewModel = createViewModel()
        val fakeTransaction = TransactionGenerator.generateRandomTransaction()
        viewModel.updateTransaction(fakeTransaction)

        coVerify(exactly = 1) {
            transactionRepository.updateTransaction(fakeTransaction)
        }
    }

    private suspend fun ReceiveTurbine<TransactionListUiState>.awaitContent(): TransactionListUiState.Content {
        while (true) {
            when (val item = awaitItem()) {
                is TransactionListUiState.Content -> return item
                is TransactionListUiState.Error -> error(item.message)
                TransactionListUiState.Loading -> Unit
            }
        }
    }
}
