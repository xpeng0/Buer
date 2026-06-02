package com.cscyxp.bookkeeping.di;

import com.cscyxp.bookkeeping.data.dao.CategoryDao;
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
public final class BookkeepingModule_ProvideCategoryDaoFactory implements Factory<CategoryDao> {
  private final Provider<BookkeepingDatabase> dbProvider;

  private BookkeepingModule_ProvideCategoryDaoFactory(Provider<BookkeepingDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CategoryDao get() {
    return provideCategoryDao(dbProvider.get());
  }

  public static BookkeepingModule_ProvideCategoryDaoFactory create(
      Provider<BookkeepingDatabase> dbProvider) {
    return new BookkeepingModule_ProvideCategoryDaoFactory(dbProvider);
  }

  public static CategoryDao provideCategoryDao(BookkeepingDatabase db) {
    return Preconditions.checkNotNullFromProvides(BookkeepingModule.INSTANCE.provideCategoryDao(db));
  }
}
