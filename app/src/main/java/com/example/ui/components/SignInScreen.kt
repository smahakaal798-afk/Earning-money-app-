package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.formatPhoneToE164
import com.example.auth.isValidOtpCode
import com.example.auth.onGoogleSignInClicked
import com.example.auth.sendPhoneOtp
import com.example.auth.verifyPhoneOtpCode
import com.example.ui.theme.BorderViolet
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidObsidian
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignInScreen(
    onAuthSuccess: () -> Unit,
    onOpenDownloadCta: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var isCheckingAutoSignIn by remember { mutableStateOf(true) }
    var isGoogleSigningIn by remember { mutableStateOf(false) }
    var isPhoneBusy by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Phone OTP state
    var countryCodeInput by remember { mutableStateOf("+91") }
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var sentPhoneE164 by remember { mutableStateOf<String?>(null) }
    var resendCooldownSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(resendCooldownSeconds) {
        if (resendCooldownSeconds > 0) {
            delay(1000L)
            resendCooldownSeconds -= 1
        }
    }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isCheckingAutoSignIn = false
                onAuthSuccess()
            },
            onUnauthenticated = {
                isCheckingAutoSignIn = false
            },
            scope = scope
        )
    }

    val anyAuthInProgress = isGoogleSigningIn || isPhoneBusy || isCheckingAutoSignIn

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(VoidObsidian, DeepIndigo, VoidObsidian)
                )
            )
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_vault_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.22f)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top VIP Neon Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = NeonGold.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY, WIN & CLOUD SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Card with Emblem & Game Preview Chips
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardSlate.copy(alpha = 0.94f),
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(NeonGold, BorderViolet, EmeraldMint))
                ),
                tonalElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = VoidObsidian,
                        border = BorderStroke(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(listOf(NeonGold, EmeraldMint))
                        ),
                        shadowElevation = 12.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_spinmint_logo),
                            contentDescription = "SpinMint Emblem",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SPINMINT",
                        style = MaterialTheme.typography.headlineLarge,
                        color = NeonGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fast, simple & secure rewards arcade",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Login with Mobile Number (OTP) or Google to unlock 5 daily spins and cloud-sync your Mint Wallet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoginGameChip("Lucky Spin", NeonGold)
                        LoginGameChip("Ludo", BrightEmerald)
                        LoginGameChip("Fruit Slots", NeonMagenta)
                        LoginGameChip("Teen Patti", NeonCyan)
                        LoginGameChip("21 & Mines", NeonGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = RoseAlert.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, RoseAlert.copy(alpha = 0.65f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = RoseAlert,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Mobile Number OTP Login Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = CardSlate.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, BorderViolet)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PhoneAndroid,
                            contentDescription = null,
                            tint = BrightEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (verificationId == null) "Login with Mobile Number" else "Enter 6-Digit OTP",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (verificationId == null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = countryCodeInput,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(3)
                                    countryCodeInput = "+$digits"
                                },
                                label = { Text("Code") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .width(86.dp)
                                    .testTag("country_code_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrightEmerald,
                                    unfocusedBorderColor = BorderViolet,
                                    focusedLabelColor = BrightEmerald,
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { raw ->
                                    phoneInput = raw.filter { it.isDigit() || it == '+' }.take(15)
                                    errorMessage = null
                                },
                                label = { Text("Mobile Number") },
                                placeholder = { Text("9876543210") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("phone_number_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrightEmerald,
                                    unfocusedBorderColor = BorderViolet,
                                    focusedLabelColor = BrightEmerald,
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val formatted = formatPhoneToE164(countryCodeInput, phoneInput)
                                if (formatted == null) {
                                    errorMessage = "Enter a valid mobile number (8–15 digits)."
                                    return@Button
                                }
                                errorMessage = null
                                isPhoneBusy = true
                                sendPhoneOtp(
                                    context = context,
                                    phoneNumberE164 = formatted,
                                    scope = scope,
                                    onAutoVerified = {
                                        isPhoneBusy = false
                                        onAuthSuccess()
                                    },
                                    onCodeSent = { verId, token ->
                                        isPhoneBusy = false
                                        verificationId = verId
                                        resendToken = token
                                        sentPhoneE164 = formatted
                                        resendCooldownSeconds = 60
                                    },
                                    onError = { err ->
                                        isPhoneBusy = false
                                        errorMessage = err
                                    }
                                )
                            },
                            enabled = !anyAuthInProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("send_otp_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldMint,
                                contentColor = VoidObsidian,
                                disabledContainerColor = EmeraldMint.copy(alpha = 0.45f),
                                disabledContentColor = VoidObsidian
                            )
                        ) {
                            if (isPhoneBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = VoidObsidian,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sending OTP…", fontWeight = FontWeight.ExtraBold)
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Sms,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send OTP on Mobile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else {
                        AnimatedVisibility(visible = sentPhoneE164 != null) {
                            Text(
                                text = "OTP sent to ${sentPhoneE164.orEmpty()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrightEmerald,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { raw ->
                                otpInput = raw.filter { it.isDigit() }.take(6)
                                errorMessage = null
                            },
                            label = { Text("6-Digit OTP Code") },
                            placeholder = { Text("123456") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = BorderViolet,
                                focusedLabelColor = NeonGold,
                                unfocusedLabelColor = TextSecondary,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val verId = verificationId ?: return@Button
                                if (!isValidOtpCode(otpInput)) {
                                    errorMessage = "Please enter the 6-digit OTP code."
                                    return@Button
                                }
                                errorMessage = null
                                isPhoneBusy = true
                                verifyPhoneOtpCode(
                                    verificationId = verId,
                                    otpCode = otpInput,
                                    scope = scope,
                                    onAuthSuccess = {
                                        isPhoneBusy = false
                                        onAuthSuccess()
                                    },
                                    onError = { err ->
                                        isPhoneBusy = false
                                        errorMessage = err
                                    }
                                )
                            },
                            enabled = !anyAuthInProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("verify_otp_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGold,
                                contentColor = VoidObsidian
                            )
                        ) {
                            if (isPhoneBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = VoidObsidian,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verifying OTP…", fontWeight = FontWeight.ExtraBold)
                            } else {
                                Text(
                                    text = "Verify OTP & Login",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    verificationId = null
                                    resendToken = null
                                    otpInput = ""
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("change_phone_button")
                            ) {
                                Text("Change Number", color = NeonCyan, fontWeight = FontWeight.SemiBold)
                            }

                            TextButton(
                                onClick = {
                                    val phone = sentPhoneE164 ?: return@TextButton
                                    errorMessage = null
                                    isPhoneBusy = true
                                    sendPhoneOtp(
                                        context = context,
                                        phoneNumberE164 = phone,
                                        scope = scope,
                                        resendToken = resendToken,
                                        onAutoVerified = {
                                            isPhoneBusy = false
                                            onAuthSuccess()
                                        },
                                        onCodeSent = { newVerId, newToken ->
                                            isPhoneBusy = false
                                            verificationId = newVerId
                                            resendToken = newToken
                                            resendCooldownSeconds = 60
                                        },
                                        onError = { err ->
                                            isPhoneBusy = false
                                            errorMessage = err
                                        }
                                    )
                                },
                                enabled = resendCooldownSeconds == 0 && !anyAuthInProgress,
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("resend_otp_button")
                            ) {
                                Text(
                                    text = if (resendCooldownSeconds > 0) "Resend in ${resendCooldownSeconds}s" else "Resend OTP",
                                    color = if (resendCooldownSeconds == 0) BrightEmerald else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderViolet)
                Text(
                    text = "  OR  ",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderViolet)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Modern High-Contrast Google Continue Pill Button
            Button(
                onClick = {
                    errorMessage = null
                    isGoogleSigningIn = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isGoogleSigningIn = false
                            onAuthSuccess()
                        },
                        onAuthError = { err ->
                            isGoogleSigningIn = false
                            errorMessage = err
                        },
                        scope = scope,
                        onAuthCancelled = {
                            isGoogleSigningIn = false
                        }
                    )
                },
                enabled = !anyAuthInProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("google_sign_in_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = VoidObsidian,
                    disabledContainerColor = CardSlate,
                    disabledContentColor = TextSecondary
                )
            ) {
                if (isGoogleSigningIn || isCheckingAutoSignIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = if (isGoogleSigningIn) VoidObsidian else NeonGold,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isCheckingAutoSignIn) "Checking Account…" else "Connecting Google…",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = NeonGold
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = null,
                                tint = VoidObsidian,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Download & Start Earning CTA trigger on Login screen
            OutlinedButton(
                onClick = onOpenDownloadCta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("signin_download_cta_button"),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.2.dp, EmeraldMint)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = null,
                    tint = BrightEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download & Share APK",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrightEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = BrightEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Firebase Auth (Mobile + Google) • Author: ${stringResource(R.string.app_author_handle)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "You can customize your 3x3 Login Pattern or 4-Digit PIN inside Security Vault",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LoginGameChip(label: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
