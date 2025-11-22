package com.gws.auto.mobile.android.di

import android.content.Context
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
import com.gws.auto.mobile.android.data.remote.CalendarApiService
import com.gws.auto.mobile.android.data.remote.ChatApiService
import com.gws.auto.mobile.android.data.remote.OutlookApiService
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies, such as API services and HTTP clients.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

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
    fun provideCalendarApiService(authorizer: GoogleApiAuthorizer): CalendarApiService {
        return CalendarApiService(authorizer)
    }

    @Provides
    @Singleton
    fun provideChatApiService(authorizer: GoogleApiAuthorizer): ChatApiService {
        return ChatApiService(authorizer)
    }

    @Provides
    @Singleton
    fun provideOutlookApiService(authorizer: MicrosoftApiAuthorizer, httpClient: OkHttpClient): OutlookApiService {
        return OutlookApiService(authorizer, httpClient)
    }
}
