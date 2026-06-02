package com.cscyxp.fitness.workout.vm;

import androidx.lifecycle.SavedStateHandle;
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
public final class ActiveWorkoutViewModel_Factory implements Factory<ActiveWorkoutViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private ActiveWorkoutViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ActiveWorkoutViewModel get() {
    return newInstance(savedStateHandleProvider.get());
  }

  public static ActiveWorkoutViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ActiveWorkoutViewModel_Factory(savedStateHandleProvider);
  }

  public static ActiveWorkoutViewModel newInstance(SavedStateHandle savedStateHandle) {
    return new ActiveWorkoutViewModel(savedStateHandle);
  }
}
