package com.cscyxp.bookkeeping.data.repository;

import com.cscyxp.bookkeeping.data.dao.CategoryDao;
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
public final class CategoryRepository_Factory implements Factory<CategoryRepository> {
  private final Provider<CategoryDao> daoProvider;

  private CategoryRepository_Factory(Provider<CategoryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CategoryRepository get() {
    return newInstance(daoProvider.get());
  }

  public static CategoryRepository_Factory create(Provider<CategoryDao> daoProvider) {
    return new CategoryRepository_Factory(daoProvider);
  }

  public static CategoryRepository newInstance(CategoryDao dao) {
    return new CategoryRepository(dao);
  }
}
