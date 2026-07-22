package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.ChatThread
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbCardBackground
import com.example.ui.theme.MrbGold
import com.example.ui.theme.MrbGoldOutline
import com.example.ui.theme.MrbSurfaceVariant
import com.example.ui.theme.MrbTextMuted
import com.example.ui.theme.MrbTextWhite
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    onOpenThread: (String) -> Unit
) {
    val threads by viewModel.chatRepository.threads.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chat Admin MRB",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MrbTextWhite
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF262214),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
                ) {
                    Text(
                        text = "Online 24/7",
                        color = MrbGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(threads) { thread ->
                    ChatThreadCard(
                        thread = thread,
                        onClick = { onOpenThread(thread.threadId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatThreadCard(
    thread: ChatThread,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MrbCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // MRB Logo Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MrbGold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MRB",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = thread.adminName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MrbTextWhite
                    )
                    Text(
                        text = thread.timestamp,
                        fontSize = 11.sp,
                        color = MrbTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = thread.carName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MrbGold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = thread.lastMessage,
                    fontSize = 12.sp,
                    color = MrbTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MrbGold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ChatRoomScreen(
    threadId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val messagesMap by viewModel.chatRepository.messages.collectAsState()
    val isTyping by viewModel.chatRepository.isTyping.collectAsState()
    val messages = messagesMap[threadId] ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MrbBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Top Bar Header
            Surface(
                color = Color(0xFF141414),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MrbTextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MrbGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MRB",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Admin MRB Sampit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MrbTextWhite
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTyping) "sedang mengetik..." else "Online",
                                fontSize = 11.sp,
                                color = if (isTyping) MrbGold else MrbTextMuted
                            )
                        }
                    }
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(message = msg)
                }
            }

            // Input Bar
            Surface(
                color = Color(0xFF141414),
                border = androidx.compose.foundation.BorderStroke(1.dp, MrbGoldOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Attachment Quick Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage(threadId, "📍 Mengirimkan lokasi saat ini (Sampit, Kalteng)", ChatMessage.MediaType.LOCATION)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MrbGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Lokasi", fontSize = 11.sp, color = MrbGold)
                        }

                        Row(
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage(threadId, "📷 Foto dokumen / KTP terlampir", ChatMessage.MediaType.IMAGE)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = MrbGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Kirim Foto", fontSize = 11.sp, color = MrbGold)
                        }

                        Row(
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage(threadId, "🎙️ [Pesan Suara 0:12]", ChatMessage.MediaType.VOICE)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = MrbGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Voice Note", fontSize = 11.sp, color = MrbGold)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ketik pesan ke Admin MRB...", color = MrbTextMuted, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MrbCardBackground,
                                unfocusedContainerColor = MrbCardBackground,
                                focusedBorderColor = MrbGold,
                                unfocusedBorderColor = MrbSurfaceVariant,
                                focusedTextColor = MrbTextWhite,
                                unfocusedTextColor = MrbTextWhite
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendChatMessage(threadId, inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .background(MrbGold, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) Color(0xFF2E2612) else MrbCardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) MrbGold else MrbGoldOutline),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text(
                        text = message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MrbGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = MrbTextWhite,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${message.timestamp} ${if (isUser) "✓✓" else ""}",
                    fontSize = 10.sp,
                    color = MrbTextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
