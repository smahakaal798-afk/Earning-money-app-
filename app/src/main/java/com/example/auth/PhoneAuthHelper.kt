package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

private const val PHONE_AUTH_TAG = "PhoneAuth"

private tailrec fun Context.findPhoneAuthActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findPhoneAuthActivity()
        else -> null
    }
}

/**
 * Formats a user-entered country code and mobile number into E.164 format (e.g. "+919876543210").
 * Returns null if the resulting number does not have between 8 and 15 digits.
 */
fun formatPhoneToE164(countryCode: String, localNumber: String): String? {
    val trimmedLocal = localNumber.trim()
    val rawCombined = if (trimmedLocal.startsWith("+")) {
        trimmedLocal
    } else {
        val cleanCodeDigits = countryCode.filter { it.isDigit() }.ifBlank { "91" }
        val cleanLocalDigits = trimmedLocal.filter { it.isDigit() }.trimStart('0')
        "+$cleanCodeDigits$cleanLocalDigits"
    }
    val digitsOnly = rawCombined.removePrefix("+").filter { it.isDigit() }
    if (digitsOnly.length !in 8..15) return null
    return "+$digitsOnly"
}

fun isValidOtpCode(otp: String): Boolean {
    val cleaned = otp.trim()
    return cleaned.length == 6 && cleaned.all { it.isDigit() }
}

private fun mapPhoneAuthExceptionMessage(e: Exception): String {
    if (e is FirebaseAuthInvalidCredentialsException) {
        return "Invalid mobile number or OTP code. Please check and try again."
    }
    if (e is FirebaseTooManyRequestsException) {
        return "Too many OTP attempts. Please wait a minute and try again."
    }
    if (e is FirebaseAuthException) {
        val code = e.errorCode
        if (code == "ERROR_OPERATION_NOT_ALLOWED" ||
            code == "ERROR_MISSING_CLIENT_IDENTIFIER" ||
            e.message?.contains("operation-not-allowed", ignoreCase = true) == true
        ) {
            return "Phone OTP login is not enabled on Firebase Console yet. Please enable 'Phone' under Firebase Console > Authentication > Sign-in method, or use Continue with Google."
        }
    }
    val msg = e.localizedMessage.orEmpty()
    if (msg.contains("BILLING_NOT_ENABLED", ignoreCase = true)) {
        return "SMS OTP requires Firebase Console Phone Auth setup (or add a Test Phone Number in Firebase Console > Authentication > Phone)."
    }
    return msg.ifBlank { "Mobile verification failed. Please try again or use Google Sign-In." }
}

fun sendPhoneOtp(
    context: Context,
    phoneNumberE164: String,
    scope: CoroutineScope,
    resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    onAutoVerified: () -> Unit,
    onCodeSent: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
    onError: (String) -> Unit
) {
    val activity = context.findPhoneAuthActivity()
    if (activity == null) {
        onError("Unable to start mobile verification from current screen.")
        return
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            scope.launch {
                try {
                    Firebase.auth.signInWithCredential(credential).await()
                    onAutoVerified()
                } catch (e: Exception) {
                    Log.e(PHONE_AUTH_TAG, "Auto phone verification failed", e)
                    onError(mapPhoneAuthExceptionMessage(e))
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(PHONE_AUTH_TAG, "Phone verification failed: ${e.message}", e)
            onError(mapPhoneAuthExceptionMessage(e))
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            onCodeSent(verificationId, token)
        }
    }

    val builder = PhoneAuthOptions.newBuilder(Firebase.auth)
        .setPhoneNumber(phoneNumberE164)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)

    if (resendToken != null) {
        builder.setForceResendingToken(resendToken)
    }

    try {
        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    } catch (e: Exception) {
        Log.e(PHONE_AUTH_TAG, "verifyPhoneNumber threw exception", e)
        onError(mapPhoneAuthExceptionMessage(e))
    }
}

fun verifyPhoneOtpCode(
    verificationId: String,
    otpCode: String,
    scope: CoroutineScope,
    onAuthSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (!isValidOtpCode(otpCode)) {
        onError("Please enter the 6-digit OTP code.")
        return
    }
    scope.launch {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode.trim())
            Firebase.auth.signInWithCredential(credential).await()
            onAuthSuccess()
        } catch (e: Exception) {
            Log.w(PHONE_AUTH_TAG, "OTP sign-in failed", e)
            onError(mapPhoneAuthExceptionMessage(e))
        }
    }
}
