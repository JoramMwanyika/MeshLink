package com.meshlink.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.meshlink.android.data.local.dto.RecentChat
import com.meshlink.android.data.local.entity.MessageEntity
import com.meshlink.android.ui.theme.MeshBlack
import com.meshlink.android.ui.theme.MeshGreen
import com.meshlink.android.ui.theme.MeshGrey
import com.meshlink.android.ui.theme.MeshLinkTheme
import com.meshlink.android.ui.viewmodel.MeshViewModel

@Composable
fun MainScreen(viewModel: MeshViewModel, onChatClick: (String, String) -> Unit) {
    val recentChats by viewModel.recentChats.collectAsState()
    
    MainContent(
        recentChats = recentChats,
        onChatClick = onChatClick,
        peersContent = { PeersScreen(viewModel, onChatClick) },
        getPeerName = { id -> viewModel.getDeviceName(id) }
    )
}

@Composable
fun MainContent(
    recentChats: List<RecentChat>,
    onChatClick: (String, String) -> Unit,
    peersContent: @Composable () -> Unit,
    getPeerName: (String) -> String
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF050B14)) {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Text("Chats") }, label = { Text("Chats") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Text("Peers") }, label = { Text("Peers") })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Text("Map") }, label = { Text("Map") })
                NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = { Text("Profile") }, label = { Text("Profile") })
            }
        },
        containerColor = MeshBlack
    ) { padding ->
        when (selectedTab) {
            0 -> ChatListScreen(padding, recentChats, onChatClick, getPeerName)
            1 -> Box(modifier = Modifier.padding(padding)) { peersContent() }
            else -> Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Coming Soon", color = MeshGrey)
            }
        }
    }
}

@Composable
fun ChatListScreen(padding: PaddingValues, chats: List<RecentChat>, onChatClick: (String, String) -> Unit, getPeerName: (String) -> String) {
    Column(modifier = Modifier.padding(padding).padding(20.dp)) {
        Text("MeshLink", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131B2A), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text("You are connected", color = MeshGreen, fontWeight = FontWeight.Bold)
                Text("Messaging active", color = MeshGrey, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recent Chats", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Surface(
                shape = CircleShape,
                color = MeshGreen,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No recent chats", color = MeshGrey)
            }
        } else {
            LazyColumn {
                items(chats) { chat ->
                    ChatRow(chat, onChatClick)
                }
            }
        }
    }
}

@Composable
fun ChatRow(chat: RecentChat, onChatClick: (String, String) -> Unit) {
    val peerId = chat.peerId
    val peerName = chat.peerName ?: "Peer $peerId"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
            .clickable { onChatClick(peerId, peerName) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).background(Color(0xFF4F5E7B), CircleShape))
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(peerName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(chat.lastMessage.content, color = MeshGrey, fontSize = 14.sp, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            // Format timestamp in a real app
            Text("Just now", color = MeshGrey, fontSize = 12.sp)
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MeshLinkTheme {
        MainContent(
            recentChats = emptyList(),
            onChatClick = { _, _ -> },
            peersContent = { Text("Peers Screen Placeholder", color = Color.White) },
            getPeerName = { "Preview Peer" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    MeshLinkTheme {
        ChatListScreen(
            padding = PaddingValues(0.dp),
            chats = emptyList(),
            onChatClick = { _, _ -> },
            getPeerName = { "Preview Peer" }
        )
    }
}
