package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.domain.service.CalendarApiService;
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
public final class CalendarCreateEventModule_Factory implements Factory<CalendarCreateEventModule> {
  private final Provider<CalendarApiService> calendarApiServiceProvider;

  private CalendarCreateEventModule_Factory(
      Provider<CalendarApiService> calendarApiServiceProvider) {
    this.calendarApiServiceProvider = calendarApiServiceProvider;
  }

  @Override
  public CalendarCreateEventModule get() {
    return newInstance(calendarApiServiceProvider.get());
  }

  public static CalendarCreateEventModule_Factory create(
      Provider<CalendarApiService> calendarApiServiceProvider) {
    return new CalendarCreateEventModule_Factory(calendarApiServiceProvider);
  }

  public static CalendarCreateEventModule newInstance(CalendarApiService calendarApiService) {
    return new CalendarCreateEventModule(calendarApiService);
  }
}
