package com.cscyxp.bookkeeping.vm;

import com.cscyxp.bookkeeping.data.repository.CategoryRepository;
import com.cscyxp.bookkeeping.data.repository.TransactionRepository;
import com.cscyxp.bookkeeping.util.TimeHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class TransactionListViewModel_Factory implements Factory<TransactionListViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<TimeHelper> timeHelperProvider;

  private TransactionListViewModel_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<TimeHelper> timeHelperProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.timeHelperProvider = timeHelperProvider;
  }

  @Override
  public TransactionListViewModel get() {
    return newInstance(transactionRepositoryProvider.get(), categoryRepositoryProvider.get(), timeHelperProvider.get());
  }

  public static TransactionListViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<TimeHelper> timeHelperProvider) {
    return new TransactionListViewModel_Factory(transactionRepositoryProvider, categoryRepositoryProvider, timeHelperProvider);
  }

  public static TransactionListViewModel newInstance(TransactionRepository transactionRepository,
      CategoryRepository categoryRepository, TimeHelper timeHelper) {
    return new TransactionListViewModel(transactionRepository, categoryRepository, timeHelper);
  }
}
