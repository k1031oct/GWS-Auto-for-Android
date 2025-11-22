package com.gws.auto.mobile.android;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
import com.gws.auto.mobile.android.data.local.db.AppDatabase;
import com.gws.auto.mobile.android.data.local.db.HistoryDao;
import com.gws.auto.mobile.android.data.local.db.ScheduleDao;
import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao;
import com.gws.auto.mobile.android.data.local.db.TagDao;
import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao;
import com.gws.auto.mobile.android.data.remote.ChatApiService;
import com.gws.auto.mobile.android.data.remote.OutlookApiService;
import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl;
import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository;
import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import com.gws.auto.mobile.android.data.repository.TagRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowFolderRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import com.gws.auto.mobile.android.di.ApiModule_ProvideFirebaseAuthFactory;
import com.gws.auto.mobile.android.di.ApiModule_ProvideOkHttpClientFactory;
import com.gws.auto.mobile.android.di.ApiModule_ProvideOutlookApiServiceFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideHistoryDaoFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideScheduleDaoFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideSearchHistoryDaoFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideTagDaoFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideWorkflowDaoFactory;
import com.gws.auto.mobile.android.di.DatabaseModule_ProvideWorkflowFolderDaoFactory;
import com.gws.auto.mobile.android.domain.engine.LocalWorkflowEngine;
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor;
import com.gws.auto.mobile.android.domain.engine.ModuleExecutorProvider;
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine;
import com.gws.auto.mobile.android.domain.engine.modules.CalendarCreateEventModule;
import com.gws.auto.mobile.android.domain.engine.modules.ChatPostModule;
import com.gws.auto.mobile.android.domain.engine.modules.CopyPasteSheetValuesModule;
import com.gws.auto.mobile.android.domain.engine.modules.CreateGmailDraftModule;
import com.gws.auto.mobile.android.domain.engine.modules.DefineVariableModule;
import com.gws.auto.mobile.android.domain.engine.modules.DriveCopyFileModule;
import com.gws.auto.mobile.android.domain.engine.modules.DriveCreateFolderModule;
import com.gws.auto.mobile.android.domain.engine.modules.DriveMoveFileModule;
import com.gws.auto.mobile.android.domain.engine.modules.DuplicateSpreadsheetModule;
import com.gws.auto.mobile.android.domain.engine.modules.GetRelativeDateModule;
import com.gws.auto.mobile.android.domain.engine.modules.GmailSendEmailModule;
import com.gws.auto.mobile.android.domain.engine.modules.LogMessageModule;
import com.gws.auto.mobile.android.domain.engine.modules.OutlookSendEmailModule;
import com.gws.auto.mobile.android.domain.engine.modules.SheetsAppendRowModule;
import com.gws.auto.mobile.android.domain.engine.modules.SheetsClearValuesModule;
import com.gws.auto.mobile.android.domain.engine.modules.SheetsCreateNewModule;
import com.gws.auto.mobile.android.domain.engine.modules.SheetsSetValueModule;
import com.gws.auto.mobile.android.domain.engine.modules.SlackPostModule;
import com.gws.auto.mobile.android.domain.engine.modules.ToastNotificationModule;
import com.gws.auto.mobile.android.domain.service.CalendarApiService;
import com.gws.auto.mobile.android.domain.service.DriveApiService;
import com.gws.auto.mobile.android.domain.service.GmailApiService;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
import com.gws.auto.mobile.android.domain.service.HistoryCsvExporter;
import com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer;
import com.gws.auto.mobile.android.domain.service.SheetsApiService;
import com.gws.auto.mobile.android.ui.announcement.AnnouncementFragment;
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel;
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.dashboard.DashboardFragment;
import com.gws.auto.mobile.android.ui.dashboard.DashboardViewModel;
import com.gws.auto.mobile.android.ui.dashboard.DashboardViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.filepicker.FilePickerActivity;
import com.gws.auto.mobile.android.ui.filepicker.FilePickerViewModel;
import com.gws.auto.mobile.android.ui.filepicker.FilePickerViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.filepicker.FilePickerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.filepicker.FilePickerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.history.HistoryFragment;
import com.gws.auto.mobile.android.ui.history.HistoryViewModel;
import com.gws.auto.mobile.android.ui.history.HistoryViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.history.HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.history.HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.schedule.ScheduleFragment;
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsActivity;
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsViewModel;
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.schedule.ScheduleViewModel;
import com.gws.auto.mobile.android.ui.schedule.ScheduleViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.schedule.ScheduleViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.schedule.ScheduleViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.search.SearchFragment;
import com.gws.auto.mobile.android.ui.search.SearchViewModel;
import com.gws.auto.mobile.android.ui.search.SearchViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.search.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.search.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.settings.MainSettingsFragment;
import com.gws.auto.mobile.android.ui.settings.MainSettingsFragment_MembersInjector;
import com.gws.auto.mobile.android.ui.settings.SettingsActivity;
import com.gws.auto.mobile.android.ui.settings.about.AboutAppFragment;
import com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment;
import com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment_MembersInjector;
import com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment;
import com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment_MembersInjector;
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment;
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementViewModel;
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel;
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.wizard.LocaleFragment;
import com.gws.auto.mobile.android.ui.wizard.WeekStartFragment;
import com.gws.auto.mobile.android.ui.wizard.WizardActivity;
import com.gws.auto.mobile.android.ui.wizard.WizardViewModel;
import com.gws.auto.mobile.android.ui.wizard.WizardViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.wizard.WizardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.wizard.WizardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.workflow.WorkflowFragment;
import com.gws.auto.mobile.android.ui.workflow.WorkflowFragment_MembersInjector;
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel;
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.gws.auto.mobile.android.ui.workflow.editor.ModuleSettingsDialogFragment;
import com.gws.auto.mobile.android.ui.workflow.editor.ModuleSettingsDialogFragment_MembersInjector;
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity;
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorViewModel;
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorViewModel_HiltModules;
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class DaggerMainApplication_HiltComponents_SingletonC {
  private DaggerMainApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MainApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MainApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MainApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MainApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MainApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MainApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MainApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MainApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MainApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MainApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MainApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public void injectAnnouncementFragment(AnnouncementFragment announcementFragment) {
    }

    @Override
    public void injectDashboardFragment(DashboardFragment dashboardFragment) {
    }

    @Override
    public void injectHistoryFragment(HistoryFragment historyFragment) {
    }

    @Override
    public void injectScheduleFragment(ScheduleFragment scheduleFragment) {
    }

    @Override
    public void injectSearchFragment(SearchFragment searchFragment) {
    }

    @Override
    public void injectMainSettingsFragment(MainSettingsFragment mainSettingsFragment) {
      injectMainSettingsFragment2(mainSettingsFragment);
    }

    @Override
    public void injectAboutAppFragment(AboutAppFragment aboutAppFragment) {
    }

    @Override
    public void injectAccountConnectionsFragment(
        AccountConnectionsFragment accountConnectionsFragment) {
      injectAccountConnectionsFragment2(accountConnectionsFragment);
    }

    @Override
    public void injectAppSettingsFragment(AppSettingsFragment appSettingsFragment) {
      injectAppSettingsFragment2(appSettingsFragment);
    }

    @Override
    public void injectTagManagementFragment(TagManagementFragment tagManagementFragment) {
    }

    @Override
    public void injectLocaleFragment(LocaleFragment localeFragment) {
    }

    @Override
    public void injectWeekStartFragment(WeekStartFragment weekStartFragment) {
    }

    @Override
    public void injectWorkflowFragment(WorkflowFragment workflowFragment) {
      injectWorkflowFragment2(workflowFragment);
    }

    @Override
    public void injectModuleSettingsDialogFragment(
        ModuleSettingsDialogFragment moduleSettingsDialogFragment) {
      injectModuleSettingsDialogFragment2(moduleSettingsDialogFragment);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }

    @CanIgnoreReturnValue
    private MainSettingsFragment injectMainSettingsFragment2(MainSettingsFragment instance) {
      MainSettingsFragment_MembersInjector.injectAuth(instance, singletonCImpl.provideFirebaseAuthProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private AccountConnectionsFragment injectAccountConnectionsFragment2(
        AccountConnectionsFragment instance2) {
      AccountConnectionsFragment_MembersInjector.injectAuth(instance2, singletonCImpl.provideFirebaseAuthProvider.get());
      AccountConnectionsFragment_MembersInjector.injectAuthorizer(instance2, singletonCImpl.googleApiAuthorizerProvider.get());
      return instance2;
    }

    @CanIgnoreReturnValue
    private AppSettingsFragment injectAppSettingsFragment2(AppSettingsFragment instance3) {
      AppSettingsFragment_MembersInjector.injectSettingsRepository(instance3, singletonCImpl.settingsRepositoryProvider.get());
      AppSettingsFragment_MembersInjector.injectHistoryRepository(instance3, singletonCImpl.historyRepositoryProvider.get());
      return instance3;
    }

    @CanIgnoreReturnValue
    private WorkflowFragment injectWorkflowFragment2(WorkflowFragment instance4) {
      WorkflowFragment_MembersInjector.injectWorkflowEngine(instance4, singletonCImpl.bindWorkflowEngineProvider.get());
      return instance4;
    }

    @CanIgnoreReturnValue
    private ModuleSettingsDialogFragment injectModuleSettingsDialogFragment2(
        ModuleSettingsDialogFragment instance5) {
      ModuleSettingsDialogFragment_MembersInjector.injectGoogleApiAuthorizer(instance5, singletonCImpl.googleApiAuthorizerProvider.get());
      return instance5;
    }
  }

  private static final class ViewCImpl extends MainApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MainApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public void injectSignInActivity(SignInActivity signInActivity) {
    }

    @Override
    public void injectSplashActivity(SplashActivity splashActivity) {
      injectSplashActivity2(splashActivity);
    }

    @Override
    public void injectFilePickerActivity(FilePickerActivity filePickerActivity) {
    }

    @Override
    public void injectScheduleSettingsActivity(ScheduleSettingsActivity scheduleSettingsActivity) {
    }

    @Override
    public void injectSettingsActivity(SettingsActivity settingsActivity) {
    }

    @Override
    public void injectWizardActivity(WizardActivity wizardActivity) {
    }

    @Override
    public void injectWorkflowEditorActivity(WorkflowEditorActivity workflowEditorActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(12).put(AnnouncementViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AnnouncementViewModel_HiltModules.KeyModule.provide()).put(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide()).put(FilePickerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FilePickerViewModel_HiltModules.KeyModule.provide()).put(HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HistoryViewModel_HiltModules.KeyModule.provide()).put(ScheduleSettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ScheduleSettingsViewModel_HiltModules.KeyModule.provide()).put(ScheduleViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ScheduleViewModel_HiltModules.KeyModule.provide()).put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide()).put(TagManagementViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TagManagementViewModel_HiltModules.KeyModule.provide()).put(ThemeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ThemeViewModel_HiltModules.KeyModule.provide()).put(WizardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WizardViewModel_HiltModules.KeyModule.provide()).put(WorkflowEditorViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WorkflowEditorViewModel_HiltModules.KeyModule.provide()).put(WorkflowViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WorkflowViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectHistoryRepository(instance, singletonCImpl.historyRepositoryProvider.get());
      MainActivity_MembersInjector.injectSettingsRepository(instance, singletonCImpl.settingsRepositoryProvider.get());
      MainActivity_MembersInjector.injectGoogleApiAuthorizer(instance, singletonCImpl.googleApiAuthorizerProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SplashActivity injectSplashActivity2(SplashActivity instance2) {
      SplashActivity_MembersInjector.injectSettingsRepository(instance2, singletonCImpl.settingsRepositoryProvider.get());
      return instance2;
    }
  }

  private static final class ViewModelCImpl extends MainApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AnnouncementViewModel> announcementViewModelProvider;

    Provider<DashboardViewModel> dashboardViewModelProvider;

    Provider<FilePickerViewModel> filePickerViewModelProvider;

    Provider<HistoryViewModel> historyViewModelProvider;

    Provider<ScheduleSettingsViewModel> scheduleSettingsViewModelProvider;

    Provider<ScheduleViewModel> scheduleViewModelProvider;

    Provider<SearchViewModel> searchViewModelProvider;

    Provider<TagManagementViewModel> tagManagementViewModelProvider;

    Provider<ThemeViewModel> themeViewModelProvider;

    Provider<WizardViewModel> wizardViewModelProvider;

    Provider<WorkflowEditorViewModel> workflowEditorViewModelProvider;

    Provider<WorkflowViewModel> workflowViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    WorkflowFolderRepository workflowFolderRepository() {
      return new WorkflowFolderRepository(singletonCImpl.workflowFolderDao());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.announcementViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.filePickerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.scheduleSettingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.scheduleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.tagManagementViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.themeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.wizardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.workflowEditorViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.workflowViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(12).put(AnnouncementViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (announcementViewModelProvider))).put(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (dashboardViewModelProvider))).put(FilePickerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (filePickerViewModelProvider))).put(HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (historyViewModelProvider))).put(ScheduleSettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (scheduleSettingsViewModelProvider))).put(ScheduleViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (scheduleViewModelProvider))).put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (searchViewModelProvider))).put(TagManagementViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (tagManagementViewModelProvider))).put(ThemeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (themeViewModelProvider))).put(WizardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (wizardViewModelProvider))).put(WorkflowEditorViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (workflowEditorViewModelProvider))).put(WorkflowViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (workflowViewModelProvider))).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel
          return (T) new AnnouncementViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.gws.auto.mobile.android.ui.dashboard.DashboardViewModel
          return (T) new DashboardViewModel(singletonCImpl.historyRepositoryProvider.get(), singletonCImpl.workflowRepositoryProvider.get());

          case 2: // com.gws.auto.mobile.android.ui.filepicker.FilePickerViewModel
          return (T) new FilePickerViewModel(singletonCImpl.driveApiService());

          case 3: // com.gws.auto.mobile.android.ui.history.HistoryViewModel
          return (T) new HistoryViewModel(singletonCImpl.historyRepositoryProvider.get(), new HistoryCsvExporter());

          case 4: // com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsViewModel
          return (T) new ScheduleSettingsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bindScheduleRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.workflowRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 5: // com.gws.auto.mobile.android.ui.schedule.ScheduleViewModel
          return (T) new ScheduleViewModel(singletonCImpl.bindScheduleRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.googleApiAuthorizerProvider.get());

          case 6: // com.gws.auto.mobile.android.ui.search.SearchViewModel
          return (T) new SearchViewModel(singletonCImpl.tagRepositoryProvider.get(), singletonCImpl.searchHistoryRepositoryProvider.get());

          case 7: // com.gws.auto.mobile.android.ui.settings.tag.TagManagementViewModel
          return (T) new TagManagementViewModel(singletonCImpl.tagRepositoryProvider.get());

          case 8: // com.gws.auto.mobile.android.ui.theme.ThemeViewModel
          return (T) new ThemeViewModel(singletonCImpl.settingsRepositoryProvider.get());

          case 9: // com.gws.auto.mobile.android.ui.wizard.WizardViewModel
          return (T) new WizardViewModel(singletonCImpl.settingsRepositoryProvider.get());

          case 10: // com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorViewModel
          return (T) new WorkflowEditorViewModel(singletonCImpl.workflowRepositoryProvider.get(), singletonCImpl.bindWorkflowEngineProvider.get());

          case 11: // com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
          return (T) new WorkflowViewModel(singletonCImpl.workflowRepositoryProvider.get(), viewModelCImpl.workflowFolderRepository(), singletonCImpl.searchHistoryRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MainApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MainApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MainApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<AppDatabase> provideAppDatabaseProvider;

    Provider<HistoryRepository> historyRepositoryProvider;

    Provider<SettingsRepository> settingsRepositoryProvider;

    Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

    Provider<FirebaseAuth> provideFirebaseAuthProvider;

    Provider<CalendarApiService> calendarApiServiceProvider;

    Provider<CalendarCreateEventModule> calendarCreateEventModuleProvider;

    Provider<ChatApiService> chatApiServiceProvider;

    Provider<ChatPostModule> chatPostModuleProvider;

    Provider<CopyPasteSheetValuesModule> copyPasteSheetValuesModuleProvider;

    Provider<DuplicateSpreadsheetModule> duplicateSpreadsheetModuleProvider;

    Provider<SheetsAppendRowModule> sheetsAppendRowModuleProvider;

    Provider<SheetsClearValuesModule> sheetsClearValuesModuleProvider;

    Provider<SheetsCreateNewModule> sheetsCreateNewModuleProvider;

    Provider<SheetsSetValueModule> sheetsSetValueModuleProvider;

    Provider<CreateGmailDraftModule> createGmailDraftModuleProvider;

    Provider<GmailSendEmailModule> gmailSendEmailModuleProvider;

    Provider<DriveCopyFileModule> driveCopyFileModuleProvider;

    Provider<DriveCreateFolderModule> driveCreateFolderModuleProvider;

    Provider<DriveMoveFileModule> driveMoveFileModuleProvider;

    Provider<MicrosoftApiAuthorizer> microsoftApiAuthorizerProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<OutlookApiService> provideOutlookApiServiceProvider;

    Provider<OutlookSendEmailModule> outlookSendEmailModuleProvider;

    Provider<SlackPostModule> slackPostModuleProvider;

    Provider<DefineVariableModule> defineVariableModuleProvider;

    Provider<GetRelativeDateModule> getRelativeDateModuleProvider;

    Provider<LogMessageModule> logMessageModuleProvider;

    Provider<ToastNotificationModule> toastNotificationModuleProvider;

    Provider<WorkflowRepository> workflowRepositoryProvider;

    Provider<LocalWorkflowEngine> localWorkflowEngineProvider;

    Provider<WorkflowEngine> bindWorkflowEngineProvider;

    Provider<ScheduleRepositoryImpl> scheduleRepositoryImplProvider;

    Provider<ScheduleRepository> bindScheduleRepositoryProvider;

    Provider<TagRepository> tagRepositoryProvider;

    Provider<SearchHistoryRepository> searchHistoryRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);
      initialize2(applicationContextModuleParam);

    }

    HistoryDao historyDao() {
      return DatabaseModule_ProvideHistoryDaoFactory.provideHistoryDao(provideAppDatabaseProvider.get());
    }

    SheetsApiService sheetsApiService() {
      return new SheetsApiService(googleApiAuthorizerProvider.get());
    }

    DriveApiService driveApiService() {
      return new DriveApiService(googleApiAuthorizerProvider.get());
    }

    GmailApiService gmailApiService() {
      return new GmailApiService(googleApiAuthorizerProvider.get());
    }

    Map<String, javax.inject.Provider<ModuleExecutor>> mapOfStringAndProviderOfModuleExecutor() {
      return ImmutableMap.<String, javax.inject.Provider<ModuleExecutor>>builderWithExpectedSize(19).put("calendar_create_event", ((Provider) (calendarCreateEventModuleProvider))).put("chat_post", ((Provider) (chatPostModuleProvider))).put("copy_paste_sheet_values", ((Provider) (copyPasteSheetValuesModuleProvider))).put("duplicate_spreadsheet", ((Provider) (duplicateSpreadsheetModuleProvider))).put("sheets_append_row", ((Provider) (sheetsAppendRowModuleProvider))).put("sheets_clear_values", ((Provider) (sheetsClearValuesModuleProvider))).put("sheets_create_new", ((Provider) (sheetsCreateNewModuleProvider))).put("sheets_set_value", ((Provider) (sheetsSetValueModuleProvider))).put("create_gmail_draft", ((Provider) (createGmailDraftModuleProvider))).put("gmail_send_email", ((Provider) (gmailSendEmailModuleProvider))).put("drive_copy_file", ((Provider) (driveCopyFileModuleProvider))).put("drive_create_folder", ((Provider) (driveCreateFolderModuleProvider))).put("drive_move_file", ((Provider) (driveMoveFileModuleProvider))).put("outlook_send_email", ((Provider) (outlookSendEmailModuleProvider))).put("slack_post", ((Provider) (slackPostModuleProvider))).put("define_variable", ((Provider) (defineVariableModuleProvider))).put("get_relative_date", ((Provider) (getRelativeDateModuleProvider))).put("log_message", ((Provider) (logMessageModuleProvider))).put("toast_notification", ((Provider) (toastNotificationModuleProvider))).build();
    }

    ModuleExecutorProvider moduleExecutorProvider() {
      return new ModuleExecutorProvider(mapOfStringAndProviderOfModuleExecutor());
    }

    WorkflowDao workflowDao() {
      return DatabaseModule_ProvideWorkflowDaoFactory.provideWorkflowDao(provideAppDatabaseProvider.get());
    }

    ScheduleDao scheduleDao() {
      return DatabaseModule_ProvideScheduleDaoFactory.provideScheduleDao(provideAppDatabaseProvider.get());
    }

    TagDao tagDao() {
      return DatabaseModule_ProvideTagDaoFactory.provideTagDao(provideAppDatabaseProvider.get());
    }

    SearchHistoryDao searchHistoryDao() {
      return DatabaseModule_ProvideSearchHistoryDaoFactory.provideSearchHistoryDao(provideAppDatabaseProvider.get());
    }

    WorkflowFolderDao workflowFolderDao() {
      return DatabaseModule_ProvideWorkflowFolderDaoFactory.provideWorkflowFolderDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.historyRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<HistoryRepository>(singletonCImpl, 0));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 2));
      this.googleApiAuthorizerProvider = DoubleCheck.provider(new SwitchingProvider<GoogleApiAuthorizer>(singletonCImpl, 3));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 4));
      this.calendarApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<CalendarApiService>(singletonCImpl, 7));
      this.calendarCreateEventModuleProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.chatApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ChatApiService>(singletonCImpl, 9));
      this.chatPostModuleProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.copyPasteSheetValuesModuleProvider = new SwitchingProvider<>(singletonCImpl, 10);
      this.duplicateSpreadsheetModuleProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.sheetsAppendRowModuleProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.sheetsClearValuesModuleProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.sheetsCreateNewModuleProvider = new SwitchingProvider<>(singletonCImpl, 14);
      this.sheetsSetValueModuleProvider = new SwitchingProvider<>(singletonCImpl, 15);
      this.createGmailDraftModuleProvider = new SwitchingProvider<>(singletonCImpl, 16);
      this.gmailSendEmailModuleProvider = new SwitchingProvider<>(singletonCImpl, 17);
      this.driveCopyFileModuleProvider = new SwitchingProvider<>(singletonCImpl, 18);
      this.driveCreateFolderModuleProvider = new SwitchingProvider<>(singletonCImpl, 19);
      this.driveMoveFileModuleProvider = new SwitchingProvider<>(singletonCImpl, 20);
      this.microsoftApiAuthorizerProvider = DoubleCheck.provider(new SwitchingProvider<MicrosoftApiAuthorizer>(singletonCImpl, 23));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 24));
      this.provideOutlookApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<OutlookApiService>(singletonCImpl, 22));
      this.outlookSendEmailModuleProvider = new SwitchingProvider<>(singletonCImpl, 21);
      this.slackPostModuleProvider = new SwitchingProvider<>(singletonCImpl, 25);
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final ApplicationContextModule applicationContextModuleParam) {
      this.defineVariableModuleProvider = new SwitchingProvider<>(singletonCImpl, 26);
      this.getRelativeDateModuleProvider = new SwitchingProvider<>(singletonCImpl, 27);
      this.logMessageModuleProvider = new SwitchingProvider<>(singletonCImpl, 28);
      this.toastNotificationModuleProvider = new SwitchingProvider<>(singletonCImpl, 29);
      this.workflowRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<WorkflowRepository>(singletonCImpl, 30));
      this.localWorkflowEngineProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.bindWorkflowEngineProvider = DoubleCheck.provider((Provider) (localWorkflowEngineProvider));
      this.scheduleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 31);
      this.bindScheduleRepositoryProvider = DoubleCheck.provider((Provider) (scheduleRepositoryImplProvider));
      this.tagRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TagRepository>(singletonCImpl, 32));
      this.searchHistoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SearchHistoryRepository>(singletonCImpl, 33));
    }

    @Override
    public void injectMainApplication(MainApplication mainApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.gws.auto.mobile.android.data.repository.HistoryRepository
          return (T) new HistoryRepository(singletonCImpl.historyDao());

          case 1: // com.gws.auto.mobile.android.data.local.db.AppDatabase
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.gws.auto.mobile.android.data.repository.SettingsRepository
          return (T) new SettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
          return (T) new GoogleApiAuthorizer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.google.firebase.auth.FirebaseAuth
          return (T) ApiModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 5: // com.gws.auto.mobile.android.domain.engine.LocalWorkflowEngine
          return (T) new LocalWorkflowEngine(singletonCImpl.moduleExecutorProvider(), singletonCImpl.historyRepositoryProvider.get(), singletonCImpl.workflowRepositoryProvider.get());

          case 6: // com.gws.auto.mobile.android.domain.engine.modules.CalendarCreateEventModule
          return (T) new CalendarCreateEventModule(singletonCImpl.calendarApiServiceProvider.get());

          case 7: // com.gws.auto.mobile.android.domain.service.CalendarApiService
          return (T) new CalendarApiService(singletonCImpl.googleApiAuthorizerProvider.get());

          case 8: // com.gws.auto.mobile.android.domain.engine.modules.ChatPostModule
          return (T) new ChatPostModule(singletonCImpl.chatApiServiceProvider.get());

          case 9: // com.gws.auto.mobile.android.data.remote.ChatApiService
          return (T) new ChatApiService(singletonCImpl.googleApiAuthorizerProvider.get());

          case 10: // com.gws.auto.mobile.android.domain.engine.modules.CopyPasteSheetValuesModule
          return (T) new CopyPasteSheetValuesModule(singletonCImpl.sheetsApiService());

          case 11: // com.gws.auto.mobile.android.domain.engine.modules.DuplicateSpreadsheetModule
          return (T) new DuplicateSpreadsheetModule(singletonCImpl.driveApiService());

          case 12: // com.gws.auto.mobile.android.domain.engine.modules.SheetsAppendRowModule
          return (T) new SheetsAppendRowModule(singletonCImpl.sheetsApiService());

          case 13: // com.gws.auto.mobile.android.domain.engine.modules.SheetsClearValuesModule
          return (T) new SheetsClearValuesModule(singletonCImpl.sheetsApiService());

          case 14: // com.gws.auto.mobile.android.domain.engine.modules.SheetsCreateNewModule
          return (T) new SheetsCreateNewModule(singletonCImpl.sheetsApiService());

          case 15: // com.gws.auto.mobile.android.domain.engine.modules.SheetsSetValueModule
          return (T) new SheetsSetValueModule(singletonCImpl.sheetsApiService());

          case 16: // com.gws.auto.mobile.android.domain.engine.modules.CreateGmailDraftModule
          return (T) new CreateGmailDraftModule(singletonCImpl.gmailApiService());

          case 17: // com.gws.auto.mobile.android.domain.engine.modules.GmailSendEmailModule
          return (T) new GmailSendEmailModule(singletonCImpl.gmailApiService());

          case 18: // com.gws.auto.mobile.android.domain.engine.modules.DriveCopyFileModule
          return (T) new DriveCopyFileModule(singletonCImpl.driveApiService());

          case 19: // com.gws.auto.mobile.android.domain.engine.modules.DriveCreateFolderModule
          return (T) new DriveCreateFolderModule(singletonCImpl.driveApiService());

          case 20: // com.gws.auto.mobile.android.domain.engine.modules.DriveMoveFileModule
          return (T) new DriveMoveFileModule(singletonCImpl.driveApiService());

          case 21: // com.gws.auto.mobile.android.domain.engine.modules.OutlookSendEmailModule
          return (T) new OutlookSendEmailModule(singletonCImpl.provideOutlookApiServiceProvider.get());

          case 22: // com.gws.auto.mobile.android.data.remote.OutlookApiService
          return (T) ApiModule_ProvideOutlookApiServiceFactory.provideOutlookApiService(singletonCImpl.microsoftApiAuthorizerProvider.get(), singletonCImpl.provideOkHttpClientProvider.get());

          case 23: // com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer
          return (T) new MicrosoftApiAuthorizer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 24: // okhttp3.OkHttpClient
          return (T) ApiModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 25: // com.gws.auto.mobile.android.domain.engine.modules.SlackPostModule
          return (T) new SlackPostModule(singletonCImpl.provideOkHttpClientProvider.get());

          case 26: // com.gws.auto.mobile.android.domain.engine.modules.DefineVariableModule
          return (T) new DefineVariableModule();

          case 27: // com.gws.auto.mobile.android.domain.engine.modules.GetRelativeDateModule
          return (T) new GetRelativeDateModule();

          case 28: // com.gws.auto.mobile.android.domain.engine.modules.LogMessageModule
          return (T) new LogMessageModule();

          case 29: // com.gws.auto.mobile.android.domain.engine.modules.ToastNotificationModule
          return (T) new ToastNotificationModule(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 30: // com.gws.auto.mobile.android.data.repository.WorkflowRepository
          return (T) new WorkflowRepository(singletonCImpl.workflowDao());

          case 31: // com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
          return (T) new ScheduleRepositoryImpl(singletonCImpl.scheduleDao(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.calendarApiServiceProvider.get(), singletonCImpl.googleApiAuthorizerProvider.get());

          case 32: // com.gws.auto.mobile.android.data.repository.TagRepository
          return (T) new TagRepository(singletonCImpl.tagDao());

          case 33: // com.gws.auto.mobile.android.data.repository.SearchHistoryRepository
          return (T) new SearchHistoryRepository(singletonCImpl.searchHistoryDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
