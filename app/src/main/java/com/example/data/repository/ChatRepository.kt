package com.example.data.repository

import com.example.data.model.ChatMessage
import com.example.data.model.ChatThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatRepository {

    private val _threads = MutableStateFlow<List<ChatThread>>(initialThreads)
    val threads: StateFlow<List<ChatThread>> = _threads.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(initialMessagesMap)
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun getMessagesForThread(threadId: String): List<ChatMessage> {
        return _messages.value[threadId] ?: emptyList()
    }

    suspend fun sendMessage(threadId: String, text: String, mediaType: ChatMessage.MediaType = ChatMessage.MediaType.TEXT) {
        val currentList = _messages.value[threadId] ?: emptyList()
        val userMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            threadId = threadId,
            senderName = "Saya",
            text = text,
            timestamp = "10:25",
            isFromUser = true,
            isRead = true,
            mediaType = mediaType
        )

        val updatedMap = _messages.value.toMutableMap()
        updatedMap[threadId] = currentList + userMsg
        _messages.value = updatedMap

        // Update thread last message
        _threads.value = _threads.value.map { thread ->
            if (thread.threadId == threadId) {
                thread.copy(lastMessage = text, timestamp = "10:25")
            } else thread
        }

        // Simulate Admin Reply
        _isTyping.value = true
        delay(1500)
        _isTyping.value = false

        val adminReplyText = when {
            text.contains("kredit", ignoreCase = true) -> "Halo Kak! Untuk pengajuan kredit unit ini, DP mulai 15% dengan proses approval cepat 1x24 jam. Tim kami siap bantu survei dokumen Kak."
            text.contains("booking", ignoreCase = true) -> "Siap Kak, booking fee unit sebesar Rp 2.000.000 sudah dikunci dan unit akan kami tahan khusus untuk Kakak!"
            text.contains("pajak", ignoreCase = true) -> "Dokumen STNK, BPKB & Pajak unit ini 100% lengkap dan siap balik nama di area Kalteng/Kalsel Kak."
            else -> "Terima kasih telah menghubungi Admin MRB (Mitra Roda Borneo)! Unit masih sangat ready dan siap kami jadwalkan untuk Test Drive di showroom Sampit Kak."
        }

        val adminMsg = ChatMessage(
            id = "msg-admin-${System.currentTimeMillis()}",
            threadId = threadId,
            senderName = "Admin MRB",
            text = adminReplyText,
            timestamp = "10:26",
            isFromUser = false,
            isRead = true
        )

        val mapWithReply = _messages.value.toMutableMap()
        mapWithReply[threadId] = (mapWithReply[threadId] ?: emptyList()) + adminMsg
        _messages.value = mapWithReply

        _threads.value = _threads.value.map { thread ->
            if (thread.threadId == threadId) {
                thread.copy(lastMessage = adminReplyText, timestamp = "10:26")
            } else thread
        }
    }

    companion object {
        val initialThreads = listOf(
            ChatThread(
                threadId = "thread-1",
                adminName = "Admin MRB",
                carName = "Toyota Innova Reborn 2.4 G",
                carPrice = "Rp 395.000.000",
                lastMessage = "Betul Kak, unit masih ada dan siap untuk di Test Drive di Showroom Sampit.",
                timestamp = "20:31",
                unreadCount = 0,
                isOnline = true
            ),
            ChatThread(
                threadId = "thread-2",
                adminName = "Admin MRB",
                carName = "Toyota Rush 1.5 GR Sport",
                carPrice = "Rp 235.000.000",
                lastMessage = "Hai! Unit Toyota Rush GR Sport ini sangat terawat dan bebas dari bekas banjir/laka.",
                timestamp = "Kemarin",
                unreadCount = 1,
                isOnline = true
            )
        )

        val initialMessagesMap = mapOf(
            "thread-1" to listOf(
                ChatMessage(
                    id = "m1",
                    threadId = "thread-1",
                    senderName = "Saya",
                    text = "Halo Admin MRB, apakah unit Toyota Innova Reborn 2.4 G ini masih tersedia?",
                    timestamp = "20:29",
                    isFromUser = true
                ),
                ChatMessage(
                    id = "m2",
                    threadId = "thread-1",
                    senderName = "Admin MRB",
                    text = "Halo Kak! Betul Kak, unit masih ada dan siap untuk di Test Drive di Showroom Sampit. Garansi mesin 1 tahun gratis dari MRB!",
                    timestamp = "20:31",
                    isFromUser = false
                )
            ),
            "thread-2" to listOf(
                ChatMessage(
                    id = "m3",
                    threadId = "thread-2",
                    senderName = "Admin MRB",
                    text = "Hai! Unit Toyota Rush GR Sport ini sangat terawat dan bebas dari bekas banjir/laka.",
                    timestamp = "Kemarin",
                    isFromUser = false
                )
            )
        )
    }
}
