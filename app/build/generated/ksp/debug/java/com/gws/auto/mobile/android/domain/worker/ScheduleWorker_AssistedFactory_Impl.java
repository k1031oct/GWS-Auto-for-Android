package com.gws.auto.mobile.android.domain.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ScheduleWorker_AssistedFactory_Impl implements ScheduleWorker_AssistedFactory {
  private final ScheduleWorker_Factory delegateFactory;

  ScheduleWorker_AssistedFactory_Impl(ScheduleWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ScheduleWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ScheduleWorker_AssistedFactory> create(
      ScheduleWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScheduleWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ScheduleWorker_AssistedFactory> createFactoryProvider(
      ScheduleWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScheduleWorker_AssistedFactory_Impl(delegateFactory));
  }
}
