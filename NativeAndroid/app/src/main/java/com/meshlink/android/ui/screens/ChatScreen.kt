package com.meshlink.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshlink.android.ui.theme.MeshBlack
import com.meshlink.android.ui.theme.MeshDarkBlue
import com.meshlink.android.ui.theme.MeshGreen
import com.meshlink.android.ui.theme.MeshGrey

data class MockMessage(val id: String, val text: String, val isMe: Boolean, val time: String, val status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(peerId: String, peerName: String, onBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(
        MockMessage("1", "Hey! Are you coming to the meeting point?", false, "10:20 AM", ""),
        MockMessage("2", "Yes, on my way. 5 mins.", true, "10:21 AM", "delivered")
    ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(peerName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Online (2m away)", color = MeshGreen, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MeshBlack)
            )
        },
        containerColor = MeshBlack
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(msg)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Type a message...", color = MeshGrey) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MeshDarkBlue,
                        unfocusedContainerColor = MeshDarkBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(25.dp)
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messages.add(MockMessage(System.currentTimeMillis().toString(), inputText, true, "Now", "pending"))
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(45.dp)
                        .background(MeshGreen, RoundedCornerShape(25.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: MockMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (msg.isMe) MeshGreen else MeshDarkBlue,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (msg.isMe) 16.dp else 4.dp,
                bottomEnd = if (msg.isMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    color = if (msg.isMe) Color.Black else Color.White,
                    fontSize = 16.sp
                )
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = msg.time,
                        color = if (msg.isMe) Color.Black.copy(alpha = 0.6f) else MeshGrey,
                        fontSize = 11.sp
                    )
                    if (msg.isMe) {
                        Text(
                            text = if (msg.status == "delivered") "✓✓" else "✓",
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
