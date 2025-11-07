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

package org.dpdns.securechatpro.util

import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtil {
    private const val LOG_FILE = "security_log.txt"

    fun deviceName(): String =
        (Build.MANUFACTURER + " " + Build.MODEL).trim()

    fun timestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return sdf.format(Date())
    }

    private fun writeBytes(file: File, bytes: ByteArray) {
        file.parentFile?.mkdirs()
        FileOutputStream(file, true).use { fos ->
            fos.write(bytes)
        }
    }

    private fun writeLine(file: File, line: String) = writeBytes(file, line.toByteArray())

    private fun externalFile(context: Context): File? = context.getExternalFilesDir(null)?.let { File(it, LOG_FILE) }
    private fun internalFile(context: Context): File = File(context.filesDir, LOG_FILE)

    // Prefer Android/data (app-specific external). Fallback to internal if unavailable.
    fun appendLog(context: Context, status: String, message: String) {
        val line = "[${timestamp()}] | [${deviceName()}] | [$status] | [$message]\n"
        val target = externalFile(context) ?: internalFile(context)
        writeLine(target, line)
    }

    // Binary logging with same target selection
    fun appendLogBinary(context: Context, status: String, messageBytes: ByteArray) {
        val prefix = "[${timestamp()}] | [${deviceName()}] | [$status] | ["
        val suffix = "]\n"
        val target = externalFile(context) ?: internalFile(context)
        writeBytes(target, prefix.toByteArray())
        writeBytes(target, messageBytes)
        writeBytes(target, suffix.toByteArray())
    }

    // Return full path (Android/data preferred, else internal)
    fun logPath(context: Context): String = (externalFile(context) ?: internalFile(context)).absolutePath
}
