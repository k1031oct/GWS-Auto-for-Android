package com.gws.auto.mobile.android.domain.service;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = ScheduleWorker.class
)
public interface ScheduleWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.gws.auto.mobile.android.domain.service.ScheduleWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(ScheduleWorker_AssistedFactory factory);
}
