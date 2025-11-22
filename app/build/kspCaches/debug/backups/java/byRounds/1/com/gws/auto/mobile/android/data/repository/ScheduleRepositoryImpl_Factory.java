package com.gws.auto.mobile.android.data.repository;

import android.content.Context;
import com.gws.auto.mobile.android.data.local.db.ScheduleDao;
import com.gws.auto.mobile.android.domain.service.CalendarApiService;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ScheduleRepositoryImpl_Factory implements Factory<ScheduleRepositoryImpl> {
  private final Provider<ScheduleDao> scheduleDaoProvider;

  private final Provider<Context> contextProvider;

  private final Provider<CalendarApiService> calendarApiServiceProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleRepositoryImpl_Factory(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<Context> contextProvider, Provider<CalendarApiService> calendarApiServiceProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleDaoProvider = scheduleDaoProvider;
    this.contextProvider = contextProvider;
    this.calendarApiServiceProvider = calendarApiServiceProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepositoryImpl get() {
    return newInstance(scheduleDaoProvider.get(), contextProvider.get(), calendarApiServiceProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static ScheduleRepositoryImpl_Factory create(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<Context> contextProvider, Provider<CalendarApiService> calendarApiServiceProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleRepositoryImpl_Factory(scheduleDaoProvider, contextProvider, calendarApiServiceProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepositoryImpl newInstance(ScheduleDao scheduleDao, Context context,
      CalendarApiService calendarApiService, GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleRepositoryImpl(scheduleDao, context, calendarApiService, googleApiAuthorizer);
  }
}
