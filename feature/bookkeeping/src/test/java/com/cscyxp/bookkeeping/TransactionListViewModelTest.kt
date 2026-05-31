package com.cscyxp.bookkeeping

import app.cash.turbine.test
import com.cscyxp.bookkeeping.data.repository.CategoryRepository
import com.cscyxp.bookkeeping.data.repository.TransactionRepository
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.DailyTransaction
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.domain.TransactionFilter
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
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {
    private val transactionRepository = mockk<TransactionRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)
    private val timeHelper = mockk<TimeHelper>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    val year = 1111
    val month = 1
    val category = Category(id = 1L, name = "随机", type = 0, icon = "")
    val filter = TransactionFilter(month = month, year = year, category = category)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    fun createViewModel(): TransactionListViewModel {
        return TransactionListViewModel(transactionRepository, categoryRepository, timeHelper)
    }

    @Test
    fun `todayExpend - should calculate expense sum correctly`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<Transaction>())
        every {
            transactionRepository.getTransactionsFlowByFilter(any(), any())
        } returns fakeFlow

        val viewModel = createViewModel()

        viewModel.todayExpend.test {
            assertEquals("0.00", awaitItem())

            val generateTransactions = TransactionGenerator.generateRandomTransactions(incomeCount = 10, expenseCount = 20)
            fakeFlow.emit(generateTransactions.transactions)

            awaitItem().shouldBe(
                expected = generateTransactions.totalExpense,
                message = "todayExpend should calculate expense sum correctly"
            )
        }
    }

    @Test
    fun `todayExpend - should use correct time args`() {
        val startTime = 1000L
        val endTime = 2000L
        every { timeHelper.getTodayStartTimeMillis() } returns startTime
        every { timeHelper.getTodayEndTimeMillis() } returns endTime

        createViewModel()

        verify(exactly = 1) {
            transactionRepository.getTransactionsFlowByFilter(startTime, endTime)
        }
    }

    @Test
    fun `monthExpend - should calculate expense sum correctly`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<Transaction>())
        every {
            transactionRepository.getTransactionsFlowByFilter(any(), any())
        } returns fakeFlow

        val viewModel = createViewModel()

        viewModel.monthExpend.test {
            assertEquals("0.00", awaitItem())

            val generateTransactions = TransactionGenerator.generateRandomTransactions(incomeCount = 10, expenseCount = 20)
            fakeFlow.emit(generateTransactions.transactions)

            awaitItem().shouldBe(
                expected = generateTransactions.totalExpense,
                message = "monthExpend should calculate expense sum correctly"
            )
        }
    }

    @Test
    fun `monthExpend - should use correct time args`() {
        val startTime = 3000L
        val endTime = 4000L
        every { timeHelper.getTodayStartTimeMillis() } returns startTime
        every { timeHelper.getTodayEndTimeMillis() } returns endTime

        createViewModel()

        verify(exactly = 1) {
            transactionRepository.getTransactionsFlowByFilter(startTime, endTime)
        }
    }

    @Test
    fun `homeUiState - should emit initial empty state`() = runTest {
        val fakeData = TransactionGenerator.generateDailyTransactions()

        every {
            transactionRepository.getDailyTransactionsFlowByFilter(any(), any(), any())
        } returns flow {
            delay(100)
            emit(fakeData.dailyTransactions)
        }

        val viewModel = createViewModel()

        viewModel.homeUiState.test {
            val initState = awaitItem()
            initState.expenseSumStr.shouldBe("0.00")
            initState.incomeSumStr.shouldBe("0.00")
            initState.balanceStr.shouldBe("0.00")
            initState.dailyTransactions.shouldBe(emptyList())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `homeUiState - should calculate sum correctly`() = runTest {
        val fakeFlow = MutableStateFlow(emptyList<DailyTransaction>())

        every {
            transactionRepository.getDailyTransactionsFlowByFilter(any(), any(), any())
        } returns fakeFlow

        val viewModel = createViewModel()

        viewModel.homeUiState.test {
            awaitItem()

            val generateDailyTransactions = TransactionGenerator.generateDailyTransactions()
            fakeFlow.emit(generateDailyTransactions.dailyTransactions)

            val homeUiState = awaitItem()
            homeUiState.incomeSumStr.shouldBe(generateDailyTransactions.totalIncome)
            homeUiState.expenseSumStr.shouldBe(generateDailyTransactions.totalExpense)
            homeUiState.balanceStr.shouldBe(generateDailyTransactions.balance)
            homeUiState.dailyTransactions.shouldBe(expected = generateDailyTransactions.dailyTransactions, isPrint = false)
        }
    }

    @Test
    fun `homeUiState - should pass filter to repository correctly`() = runTest {
        val startMonth = 1000L
        val endMonth = 2000L
        every { timeHelper.getMonthStartTimeMillis(year, month) } returns startMonth
        every { timeHelper.getMonthEndTimeMillis(year, month) } returns endMonth

        val viewModel = createViewModel()

        viewModel.homeUiState.test {
            awaitItem()
            viewModel.updateFilter(filter)

            verify(exactly = 1) {
                timeHelper.getMonthStartTimeMillis(year, month)
                timeHelper.getMonthEndTimeMillis(year, month)
                transactionRepository.getDailyTransactionsFlowByFilter(startMonth, endMonth, categoryId = 1L)
            }
        }
    }

    @Test
    fun `updateFilter - should update filter correctly`() {
        val viewModel = createViewModel()

        viewModel.updateFilter(filter)

        viewModel.getMonth().shouldBe(month)
        viewModel.getYear().shouldBe(year)
        viewModel.getCategory().shouldBe(category)
    }

    @Test
    fun `set filter - should invoke updateFilter correctly`() {
        val viewModel = createViewModel()
        val filter = viewModel.filter.value
        val newYear = 2222
        val newMonth = 2
        val newCategory = category.copy(id = 2L)

        viewModel.setMonth(newMonth)
        viewModel.filter.value.shouldBe(filter.copy(month = newMonth))

        viewModel.setYear(newYear)
        viewModel.filter.value.shouldBe(filter.copy(month = newMonth, year = newYear))

        viewModel.setCategory(newCategory)
        viewModel.filter.value.shouldBe(filter.copy(month = newMonth, year = newYear, category = newCategory))
    }

    @Test
    fun `filter - the initial value should be current time`() = runTest {
        val currentMonth = LocalDate.now().monthValue
        val currentYear = LocalDate.now().year
        val viewmodel = createViewModel()

        viewmodel.filter.test {
            val initItem = awaitItem()
            initItem.year.shouldBe(currentYear)
            initItem.month.shouldBe(currentMonth)
            initItem.category.shouldBe(null)
        }

        viewmodel.homeUiState.test {
            awaitItem()

            verify(exactly = 1) {
                timeHelper.getMonthStartTimeMillis(currentYear, currentMonth)
                timeHelper.getMonthEndTimeMillis(currentYear, currentMonth)
            }
        }
    }

    @Test
    fun `categoryFilter - should emit type correctly`() = runTest {
        val viewModel = createViewModel()

        viewModel.categoryFilter.test {
            val initialValue = awaitItem()
            initialValue.shouldBe(Category.TYPE_EXPAND)

            viewModel.setCategoryDialogFilterType(Category.TYPE_INCOME)

            awaitItem().shouldBe(Category.TYPE_INCOME)
        }
    }

    @Test
    fun `topCategoriesByFilter - should emit categories correctly`() = runTest {
        val fakeFlow = MutableStateFlow<List<Category>>(emptyList())
        val fakeCategories = CategoryGenerator.generatorRandomCategories(count = 10)
        every {
            categoryRepository.getTopCategories(any())
        } returns fakeFlow

        val viewModel = createViewModel()

        viewModel.topCategoriesByFilter.test {
            awaitItem()

            fakeFlow.emit(fakeCategories)
            awaitItem().shouldBe(fakeCategories, isPrint = false)
        }
    }

    @Test
    fun `topCategoriesByFilter - should pass type to repository correctly`() = runTest {
        val expendCategory = CategoryGenerator.generatorRandomCategories(count = 10, type = Category.TYPE_EXPAND)
        val incomeCategory = CategoryGenerator.generatorRandomCategories(count = 10, type = Category.TYPE_INCOME)

        every { categoryRepository.getTopCategories(Category.TYPE_EXPAND) } returns flowOf(expendCategory)
        every { categoryRepository.getTopCategories(Category.TYPE_INCOME) } returns flowOf(incomeCategory)

        val viewModel = createViewModel()
        val type = Category.TYPE_INCOME

        viewModel.topCategoriesByFilter.test {
            awaitItem()

            viewModel.setCategoryDialogFilterType(type)

            awaitItem().shouldBe(incomeCategory)
            verify(exactly = 1) {
                categoryRepository.getTopCategories(type)
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
}
