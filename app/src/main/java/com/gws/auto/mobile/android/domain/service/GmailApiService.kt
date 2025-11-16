package com.gws.auto.mobile.android.domain.service

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Draft
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.inject.Inject
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GmailApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private suspend fun getService(): Gmail {
        val credential = authorizer.getCredential(listOf(GmailScopes.GMAIL_COMPOSE))
        return Gmail.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    suspend fun createDraft(to: String, subject: String, body: String): Draft = withContext(Dispatchers.IO) {
        val mimeMessage = createMimeMessage(to, null, null, subject, body)
        val rawMessage = createRawMessage(mimeMessage)
        val draft = Draft().setMessage(rawMessage)
        getService().users().drafts().create("me", draft).execute()
    }

    suspend fun sendEmail(to: String, cc: String?, bcc: String?, subject: String, body: String): Message = withContext(Dispatchers.IO) {
        val mimeMessage = createMimeMessage(to, cc, bcc, subject, body)
        val rawMessage = createRawMessage(mimeMessage)
        getService().users().messages().send("me", rawMessage).execute()
    }

    private fun createMimeMessage(to: String, cc: String?, bcc: String?, subject: String, body: String): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress("me"))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        if (!cc.isNullOrBlank()) {
            email.addRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(cc))
        }
        if (!bcc.isNullOrBlank()) {
            email.addRecipients(javax.mail.Message.RecipientType.BCC, InternetAddress.parse(bcc))
        }
        email.subject = subject
        email.setText(body)
        return email
    }

    private fun createRawMessage(mimeMessage: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        mimeMessage.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.raw = encodedEmail
        return message
    }
}
