package com.gws.auto.mobile.android.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.local.db.AppDatabase
import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao
import com.gws.auto.mobile.android.data.local.db.TagDao
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
import com.gws.auto.mobile.android.domain.service.CalendarApiService
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWorkflowDao(appDatabase: AppDatabase): WorkflowDao {
        return appDatabase.workflowDao()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    fun provideScheduleDao(appDatabase: AppDatabase): ScheduleDao {
        return appDatabase.scheduleDao()
    }

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    fun provideWorkflowFolderDao(appDatabase: AppDatabase): WorkflowFolderDao {
        return appDatabase.workflowFolderDao()
    }

    @Provides
    fun provideTagDao(appDatabase: AppDatabase): TagDao {
        return appDatabase.tagDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(
                Scope(DriveScopes.DRIVE),
                Scope(SheetsScopes.SPREADSHEETS),
                Scope(GmailScopes.GMAIL_COMPOSE),
                Scope(CalendarScopes.CALENDAR)
            )
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        scheduleDao: ScheduleDao,
        @ApplicationContext context: Context,
        calendarApiService: CalendarApiService,
        googleApiAuthorizer: GoogleApiAuthorizer
    ): ScheduleRepository {
        return ScheduleRepositoryImpl(scheduleDao, context, calendarApiService, googleApiAuthorizer)
    }

    @Provides
    @Singleton
    fun provideCalendarApiService(authorizer: GoogleApiAuthorizer): CalendarApiService {
        return CalendarApiService(authorizer)
    }
}
