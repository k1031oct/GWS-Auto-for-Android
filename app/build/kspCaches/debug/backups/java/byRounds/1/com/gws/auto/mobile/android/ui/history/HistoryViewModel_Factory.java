package com.gws.auto.mobile.android.ui.history;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.domain.service.HistoryCsvExporter;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<HistoryCsvExporter> csvExporterProvider;

  private HistoryViewModel_Factory(Provider<HistoryRepository> historyRepositoryProvider,
      Provider<HistoryCsvExporter> csvExporterProvider) {
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.csvExporterProvider = csvExporterProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(historyRepositoryProvider.get(), csvExporterProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<HistoryCsvExporter> csvExporterProvider) {
    return new HistoryViewModel_Factory(historyRepositoryProvider, csvExporterProvider);
  }

  public static HistoryViewModel newInstance(HistoryRepository historyRepository,
      HistoryCsvExporter csvExporter) {
    return new HistoryViewModel(historyRepository, csvExporter);
  }
}
