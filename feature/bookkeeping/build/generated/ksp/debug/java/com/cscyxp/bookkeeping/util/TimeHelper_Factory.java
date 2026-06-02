package com.cscyxp.bookkeeping.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.time.Clock;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TimeHelper_Factory implements Factory<TimeHelper> {
  private final Provider<Clock> clockProvider;

  private TimeHelper_Factory(Provider<Clock> clockProvider) {
    this.clockProvider = clockProvider;
  }

  @Override
  public TimeHelper get() {
    return newInstance(clockProvider.get());
  }

  public static TimeHelper_Factory create(Provider<Clock> clockProvider) {
    return new TimeHelper_Factory(clockProvider);
  }

  public static TimeHelper newInstance(Clock clock) {
    return new TimeHelper(clock);
  }
}
