package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.BorderViolet
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidObsidian

const val SPINMINT_SHARE_URL = "https://ais-pre-rlcg4467odmlj6vrrgfob7-233578170241.asia-southeast1.run.app"

@Composable
fun DownloadCtaModal(
    onDismiss: () -> Unit,
    onActionFeedback: (String, String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val promoMessage = buildString {
        appendLine("SpinMint — Download & Start Earning")
        appendLine("Fast, simple, and secure way to earn rewards from your phone!")
        appendLine()
        appendLine("• Lucky Spin Wheel + Tap-to-Earn + Scratch Cards")
        appendLine("• Ludo, Fruit Slots, Teen Patti, Andar Bahar, Blackjack 21 & more")
        appendLine("• Cloud synced with Google Sign-In & 4-digit PIN Vault")
        appendLine()
        append("Download Now: $SPINMINT_SHARE_URL")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = DeepIndigo,
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(NeonGold, EmeraldMint))
            ),
            tonalElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = EmeraldMint.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, EmeraldMint.copy(alpha = 0.55f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VerifiedUser,
                                contentDescription = null,
                                tint = BrightEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "SPINMINT ANDROID",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrightEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close download popup",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = CircleShape,
                    color = CardSlate,
                    border = BorderStroke(2.dp, NeonGold)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_spinmint_logo),
                        contentDescription = "SpinMint Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.cta_download_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeonGold,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.cta_download_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardSlate,
                    border = BorderStroke(1.dp, BorderViolet)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Spin, Ludo, Fruit Slots & Cards",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instant Google login • Cloud wallet & PIN Vault",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(SPINMINT_SHARE_URL))
                        val opened = runCatching {
                            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(SPINMINT_SHARE_URL)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(viewIntent)
                            true
                        }.getOrDefault(false)
                        if (opened) {
                            onActionFeedback("Opening Download Page", "App link also copied to clipboard")
                        } else {
                            onActionFeedback("Download Link Copied!", SPINMINT_SHARE_URL)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("download_cta_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldMint,
                        contentColor = VoidObsidian
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.cta_download_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(promoMessage))
                        val shared = runCatching {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "SpinMint — Download & Start Earning")
                                putExtra(Intent.EXTRA_TEXT, promoMessage)
                            }
                            val chooser = Intent.createChooser(sendIntent, "Share SpinMint").apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(chooser)
                            true
                        }.getOrDefault(false)
                        if (shared) {
                            onActionFeedback("Share Sheet Opened", "Promo message & link copied too")
                        } else {
                            onActionFeedback("Promo Message Copied!", "Ready to paste in WhatsApp or Telegram")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_app_cta_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, NeonGold)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.cta_share_button),
                        color = NeonGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
