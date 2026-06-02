package com.cscyxp.bookkeeping.data.repository;

import com.cscyxp.bookkeeping.data.dao.TransactionDao;
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
public final class TransactionRepository_Factory implements Factory<TransactionRepository> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private TransactionRepository_Factory(Provider<TransactionDao> transactionDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public TransactionRepository get() {
    return newInstance(transactionDaoProvider.get());
  }

  public static TransactionRepository_Factory create(
      Provider<TransactionDao> transactionDaoProvider) {
    return new TransactionRepository_Factory(transactionDaoProvider);
  }

  public static TransactionRepository newInstance(TransactionDao transactionDao) {
    return new TransactionRepository(transactionDao);
  }
}
