package com.gws.auto.mobile.android.domain.service

/**
 * Defines the API scopes required for Microsoft Graph API services.
 * These constants are used to request permissions during the OAuth2 flow.
 */
object MicrosoftScope {

    /**
     * Grants permission to send mail on behalf of the signed-in user.
     * Does not grant permission to read or modify mail.
     */
    const val MAIL_SEND = "https://graph.microsoft.com/Mail.Send"

    /**
     * Grants permission to read the user's basic profile information.
     * This is often required for calls to the `/me` endpoint.
     */
    const val USER_READ = "https://graph.microsoft.com/User.Read"

    /**
     * Grants permission to maintain access to data the user has given the app access to.
     * Allows the app to get a new access token when the current one expires.
     */
    const val OFFLINE_ACCESS = "offline_access"
}
