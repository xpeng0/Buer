package com.cscyxp.fitness.home.vm;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FitnessViewModel_Factory implements Factory<FitnessViewModel> {
  @Override
  public FitnessViewModel get() {
    return newInstance();
  }

  public static FitnessViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FitnessViewModel newInstance() {
    return new FitnessViewModel();
  }

  private static final class InstanceHolder {
    static final FitnessViewModel_Factory INSTANCE = new FitnessViewModel_Factory();
  }
}
