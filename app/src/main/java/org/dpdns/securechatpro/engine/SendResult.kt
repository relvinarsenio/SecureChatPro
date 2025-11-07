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

package org.dpdns.securechatpro.engine

import android.content.Context
import androidx.core.text.htmlEncode
import org.dpdns.securechatpro.crypto.RSAUtil
import org.dpdns.securechatpro.util.LogUtil
import java.security.interfaces.RSAPublicKey

sealed class SendResult {
    data class Success(val logPath: String) : SendResult()
    data class Error(val message: String) : SendResult()
}

class ChatEngine(private val context: Context) {
    private val keys by lazy { RSAUtil.getOrCreateKeyPair512() }

    fun sendMessage(plainInput: String): SendResult {
        val invalid = listOf('<', '>', '\'', '"', ';')
        if (plainInput.isBlank()) return SendResult.Error("Pesan kosong")
        if (plainInput.any { it in invalid }) return SendResult.Error("Input mengandung karakter berbahaya (<, >, ', \" , ;)")

        val encoded = plainInput.htmlEncode()
        val plainBytes = encoded.toByteArray(Charsets.UTF_8)

        // Validasi panjang untuk RSA PKCS#1 v1.5: max = ukuran kunci (byte) - 11
        val kBytes = (((keys.publicKey as RSAPublicKey).modulus.bitLength()) + 7) / 8
        val maxPkcs1 = kBytes - 11
        if (plainBytes.size > maxPkcs1) {
            return SendResult.Error("Pesan terlalu panjang (maks $maxPkcs1 byte setelah encoding)")
        }

        return try {
            val cipherBytes = RSAUtil.encrypt(keys.publicKey, plainBytes)
            LogUtil.appendLogBinary(context, "OK", cipherBytes)
            SendResult.Success(LogUtil.logPath(context))
        } catch (e: Exception) {
            LogUtil.appendLog(context, "ERROR", "Encrypt failed: ${e.message}")
            SendResult.Error("Gagal enkripsi: ${e.message}")
        }
    }
}
