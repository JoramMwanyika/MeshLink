package com.meshlink.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

data class Peer(val id: String, val name: String, val distance: String)

@Composable
fun PeersScreen() {
    val peers = listOf(
        Peer("ML-A102-C304", "Alex", "2 m"),
        Peer("ML-BFBC-7D6E", "Priya", "4 m"),
        Peer("ML-3C2B-1A0F", "Sam", "6 m"),
        Peer("ML-6D3D-4C9B", "Ravi", "8 m")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeshBlack)
            .padding(20.dp)
    ) {
        Text("Nearby Devices", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Scanning for nearby peers...", color = MeshGrey, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(30.dp))

        // Radar Visual Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(180.dp).border(1.dp, MeshGreen.copy(alpha = 0.2f), CircleShape))
            Box(modifier = Modifier.size(120.dp).border(1.dp, MeshGreen.copy(alpha = 0.4f), CircleShape))
            Box(modifier = Modifier.size(60.dp).border(1.dp, MeshGreen.copy(alpha = 0.6f), CircleShape))
            Box(modifier = Modifier.size(10.dp).background(MeshGreen, CircleShape))
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            items(peers) { peer ->
                PeerRow(peer)
            }
        }
    }
}

@Composable
fun PeerRow(peer: Peer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MeshDarkBlue, RoundedCornerShape(12.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(MeshGreen, CircleShape))
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(peer.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(peer.id, color = MeshGrey, fontSize = 12.sp)
        }
        Text(peer.distance, color = MeshGreen, fontWeight = FontWeight.Bold)
    }
}
import androidx.compose.foundation.shape.RoundedCornerShape
