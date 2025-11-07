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

package org.dpdns.securechatpro.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val isOutgoing: Boolean,
    val timestamp: Long,
    val hash: String? = null
)
