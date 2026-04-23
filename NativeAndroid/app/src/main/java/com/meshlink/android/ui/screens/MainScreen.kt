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
import com.meshlink.android.ui.theme.MeshBlack
import com.meshlink.android.ui.theme.MeshDarkBlue
import com.meshlink.android.ui.theme.MeshGreen
import com.meshlink.android.ui.theme.MeshGrey

data class MockChat(val id: String, val name: String, val msg: String, val time: String, val unread: Int)

@Composable
fun MainScreen(onChatClick: (String, String) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val mockChats = listOf(
        MockChat("1", "Sarah", "Hey! Are you coming to the...", "10:24 AM", 2),
        MockChat("2", "Team Alpha", "Meeting point updated.", "09:11 AM", 3),
        MockChat("3", "Mike", "Got the supplies.", "Yesterday", 0)
    )

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
            0 -> ChatListScreen(padding, mockChats, onChatClick)
            1 -> Box(modifier = Modifier.padding(padding)) { PeersScreen() }
            else -> Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Coming Soon", color = MeshGrey)
            }
        }
    }
}

@Composable
fun ChatListScreen(padding: PaddingValues, chats: List<MockChat>, onChatClick: (String, String) -> Unit) {
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
                Text("${chats.size} peers around you", color = MeshGrey, fontSize = 14.sp)
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

        LazyColumn {
            items(chats) { chat ->
                ChatRow(chat, onChatClick)
            }
        }
    }
}

@Composable
fun ChatRow(chat: MockChat, onChatClick: (String, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
            .clickable { onChatClick(chat.id, chat.name) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).background(Color(0xFF4F5E7B), CircleShape))
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(chat.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(chat.msg, color = MeshGrey, fontSize = 14.sp, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(chat.time, color = MeshGrey, fontSize = 12.sp)
            if (chat.unread > 0) {
                Surface(color = MeshGreen, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(top = 5.dp)) {
                    Text(chat.unread.toString(), color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(onChatClick = { _, _ -> })
}
