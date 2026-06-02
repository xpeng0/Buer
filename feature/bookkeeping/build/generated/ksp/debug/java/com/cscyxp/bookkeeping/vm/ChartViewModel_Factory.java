package com.cscyxp.bookkeeping.vm;

import com.cscyxp.bookkeeping.data.repository.TransactionRepository;
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
public final class ChartViewModel_Factory implements Factory<ChartViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private ChartViewModel_Factory(Provider<TransactionRepository> transactionRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  @Override
  public ChartViewModel get() {
    return newInstance(transactionRepositoryProvider.get());
  }

  public static ChartViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new ChartViewModel_Factory(transactionRepositoryProvider);
  }

  public static ChartViewModel newInstance(TransactionRepository transactionRepository) {
    return new ChartViewModel(transactionRepository);
  }
}
