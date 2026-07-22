package com.example.data.model

data class ChatMessage(
    val id: String,
    val threadId: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isFromUser: Boolean,
    val isRead: Boolean = true,
    val mediaType: MediaType = MediaType.TEXT,
    val mediaUrl: String? = null
) {
    enum class MediaType {
        TEXT, IMAGE, LOCATION, VOICE
    }
}

data class ChatThread(
    val threadId: String,
    val adminName: String = "Admin MRB",
    val carName: String,
    val carPrice: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true
)
