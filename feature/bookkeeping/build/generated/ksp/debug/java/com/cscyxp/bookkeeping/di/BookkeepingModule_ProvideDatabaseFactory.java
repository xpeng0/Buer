package com.cscyxp.bookkeeping.di;

import android.content.Context;
import com.cscyxp.bookkeeping.db.BookkeepingDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BookkeepingModule_ProvideDatabaseFactory implements Factory<BookkeepingDatabase> {
  private final Provider<Context> contextProvider;

  private BookkeepingModule_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BookkeepingDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static BookkeepingModule_ProvideDatabaseFactory create(Provider<Context> contextProvider) {
    return new BookkeepingModule_ProvideDatabaseFactory(contextProvider);
  }

  public static BookkeepingDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(BookkeepingModule.INSTANCE.provideDatabase(context));
  }
}
