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

package org.dpdns.securechatpro.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WA_PrimaryGreen,
    onPrimary = Color.White,
    secondary = WA_SecondaryTeal,
    tertiary = WA_Accent,
    background = WA_DarkChatBackground,
    surface = WA_DarkChatBackground,
    onBackground = WA_DarkTextPrimary,
    onSurface = WA_DarkTextPrimary,
    surfaceVariant = WA_DarkBubbleIncoming,
    onSurfaceVariant = WA_DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = WA_PrimaryGreen,
    onPrimary = Color.White,
    secondary = WA_SecondaryTeal,
    tertiary = WA_Accent,
    background = WA_ChatBackground,
    surface = WA_ChatBackground,
    onBackground = WA_TextPrimary,
    onSurface = WA_TextPrimary,
    surfaceVariant = WA_BubbleIncoming,
    onSurfaceVariant = WA_TextPrimary
)

@Composable
fun SecureChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}