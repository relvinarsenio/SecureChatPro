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

package org.dpdns.securechatpro.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

object RSAUtil {
    data class KeyBundle(
        val publicKey: PublicKey,
        val privateKey: PrivateKey
    )

    @Volatile
    private var cachedKeyPair: KeyBundle? = null

    fun generateKeyPair512(): KeyBundle {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(512)
        val kp: KeyPair = kpg.generateKeyPair()
        return KeyBundle(kp.public, kp.private)
    }

    fun getOrCreateKeyPair512(): KeyBundle {
        cachedKeyPair?.let { return it }
        synchronized(this) {
            cachedKeyPair?.let { return it }
            val generated = generateKeyPair512()
            cachedKeyPair = generated
            return generated
        }
    }

    fun encrypt(publicKey: PublicKey, message: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(message)
    }
}
