package com.cscyxp.bookkeeping.di;

import com.cscyxp.bookkeeping.data.dao.TransactionDao;
import com.cscyxp.bookkeeping.db.BookkeepingDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class BookkeepingModule_ProvideTransactionDaoFactory implements Factory<TransactionDao> {
  private final Provider<BookkeepingDatabase> dbProvider;

  private BookkeepingModule_ProvideTransactionDaoFactory(Provider<BookkeepingDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TransactionDao get() {
    return provideTransactionDao(dbProvider.get());
  }

  public static BookkeepingModule_ProvideTransactionDaoFactory create(
      Provider<BookkeepingDatabase> dbProvider) {
    return new BookkeepingModule_ProvideTransactionDaoFactory(dbProvider);
  }

  public static TransactionDao provideTransactionDao(BookkeepingDatabase db) {
    return Preconditions.checkNotNullFromProvides(BookkeepingModule.INSTANCE.provideTransactionDao(db));
  }
}
