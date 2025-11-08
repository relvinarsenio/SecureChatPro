/*******************************************************************************
 * Copyright (c) 2025 Alfie Ardinata
 *
 * The contents of this file are subject to the Common Development
 * and Distribution License ("CDDL") Version 1.1 (the "License").
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/CDDL-1.1
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 ******************************************************************************/

package org.dpdns.securechatpro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dpdns.securechatpro.about.AboutInfo
import org.dpdns.securechatpro.engine.SendResult
import org.dpdns.securechatpro.model.ChatMessage
import org.dpdns.securechatpro.ui.theme.SecureChatTheme
import org.dpdns.securechatpro.ui.theme.WA_Accent
import org.dpdns.securechatpro.ui.theme.WA_BannerBg
import org.dpdns.securechatpro.ui.theme.WA_BannerText
import org.dpdns.securechatpro.ui.theme.WA_BubbleIncoming
import org.dpdns.securechatpro.ui.theme.WA_BubbleOutgoing
import org.dpdns.securechatpro.ui.theme.WA_ChatBackground
import org.dpdns.securechatpro.ui.theme.WA_DarkBubbleIncoming
import org.dpdns.securechatpro.ui.theme.WA_DarkBubbleOutgoing
import org.dpdns.securechatpro.ui.theme.WA_DarkChatBackground
import org.dpdns.securechatpro.ui.theme.WA_DarkDivider
import org.dpdns.securechatpro.ui.theme.WA_DarkTextPrimary
import org.dpdns.securechatpro.ui.theme.WA_DarkTextSecondary
import org.dpdns.securechatpro.ui.theme.WA_PrimaryGreen
import org.dpdns.securechatpro.ui.theme.WA_TextPrimary
import org.dpdns.securechatpro.ui.theme.WA_TextSecondary
import org.dpdns.securechatpro.ui.theme.WA_TickBlue
import org.dpdns.securechatpro.ui.theme.WA_WarnAmber
import org.dpdns.securechatpro.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12+ SplashScreen API (safe on lower versions via dependency)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecureChatTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChatScreen()
                }
            }
        }
    }
}

private fun timeText(ts: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val isDark = isSystemInDarkTheme()
    val isOut = msg.isOutgoing
    val bg = if (isOut) {
        if (isDark) WA_DarkBubbleOutgoing else WA_BubbleOutgoing
    } else {
        if (isDark) WA_DarkBubbleIncoming else WA_BubbleIncoming
    }
    val textPrimary = if (isDark) WA_DarkTextPrimary else WA_TextPrimary
    val textSecondary = if (isDark) WA_DarkTextSecondary else WA_TextSecondary
    val alignment = if (isOut) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isOut)
        RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    else
        RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 320.dp)
        ) {
            SelectionContainer { Text(text = msg.text, color = textPrimary) }
            Spacer(Modifier.height(4.dp))
            if (isOut) {
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Read",
                        tint = WA_TickBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = timeText(msg.timestamp),
                        color = textSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                Row(
                    modifier = Modifier.align(Alignment.Start),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeText(msg.timestamp),
                        color = textSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var showAbout by remember { mutableStateOf(false) }
    val vm: ChatViewModel = viewModel()
    val messages = vm.messages
    val listState = rememberLazyListState()
    val isDark = isSystemInDarkTheme()
    val chatBg = if (isDark) WA_DarkChatBackground else WA_ChatBackground
    val inputBg = if (isDark) WA_DarkBubbleIncoming else WA_BubbleIncoming
    val bannerBg = if (isDark) WA_DarkDivider else WA_BannerBg
    val bannerText = if (isDark) WA_DarkTextPrimary else WA_BannerText
    val context = LocalContext.current

    // Auto-scroll to bottom when message count changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            runCatching { listState.animateScrollToItem(messages.lastIndex) }
        }
    }

    Scaffold(
        containerColor = chatBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WA_PrimaryGreen,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_icon),
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                                    .clip(shape = RoundedCornerShape(2)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Secure Chat",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF42A5F5),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                        .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Online",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.95f)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* call */ }) { Icon(Icons.Filled.Call, contentDescription = "Call") }
                    IconButton(
                        onClick = { vm.toggleExfil() },
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                    ) {
                        val tint = if (vm.exfilToggle) WA_WarnAmber else Color.White
                        Icon(
                            imageVector = Icons.Outlined.BugReport,
                            contentDescription = if (vm.exfilToggle) "Exfiltration enabled" else "Exfiltration disabled",
                            tint = tint
                        )
                    }
                    IconButton(onClick = { showAbout = !showAbout }) { Icon(Icons.Outlined.Info, contentDescription = "About") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .imePadding()
                .background(chatBg)
        ) {
            AnimatedVisibility(visible = vm.exfilToggle) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bannerBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BugReport,
                        contentDescription = null,
                        tint = WA_WarnAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Exfiltration enabled",
                        color = bannerText,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = { vm.toggleExfil() },
                        label = { Text("Matikan") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isDark) WA_DarkBubbleIncoming else Color.White,
                            labelColor = bannerText
                        )
                    )
                }
            }
            // Message list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                state = listState
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(msg)
                }
            }

            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = vm.input,
                    onValueChange = { vm.input = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 54.dp, max = 120.dp),
                    placeholder = { Text("Type a message", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = false,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        IconButton(onClick = { /* emoji */ }, modifier = Modifier.padding(start = 3.dp)) {
                            Icon(Icons.Outlined.EmojiEmotions, contentDescription = "Emoji")
                        }
                    },
                    trailingIcon = {
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* attach */ }, modifier = Modifier.padding(end = 3.dp)) {
                                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        when (val result = vm.onSend()) {
                            is SendResult.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                            is SendResult.Success -> Unit
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (vm.exfilToggle) WA_WarnAmber else WA_Accent)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }

        if (showAbout) {
            AboutDialog(onClose = { showAbout = !showAbout })
        }
    }
}

@Composable
private fun AboutDialog(onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.size(72.dp)
                    )
                }
                Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                InfoRow(icon = Icons.Outlined.Person, label = "Nama", value = AboutInfo.NAMA)
                InfoRow(icon = Icons.Outlined.School, label = "Kelas", value = AboutInfo.KELAS)
                InfoRow(icon = Icons.Outlined.Fingerprint, label = "NPM", value = AboutInfo.NPM)
                InfoRow(icon = Icons.Outlined.Book, label = "Mata Kuliah", value = AboutInfo.MATA_KULIAH)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = WA_PrimaryGreen)
                    Text(AboutInfo.DESKRIPSI)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onClose) { Text("Tutup") }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = WA_PrimaryGreen)
        Text("$label:", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
