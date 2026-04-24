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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import com.meshlink.android.data.local.entity.DeviceEntity
import com.meshlink.android.ui.theme.MeshBlack
import com.meshlink.android.ui.theme.MeshDarkBlue
import com.meshlink.android.ui.theme.MeshGreen
import com.meshlink.android.ui.theme.MeshGrey
import androidx.compose.ui.tooling.preview.Preview
import com.meshlink.android.ui.theme.MeshLinkTheme
import com.meshlink.android.ui.viewmodel.MeshViewModel

@Composable
fun PeersScreen(viewModel: MeshViewModel, onDeviceClick: (String, String) -> Unit) {
    val allDevices by viewModel.nearbyDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    
    // Filter logic:
    // 1. Must not be 'SELF'
    // 2. Must not be simulator data (starts with ML-SIM)
    // 3. Prefer showing only devices seen in the last 2 minutes if they are 'Unknown'
    val now = System.currentTimeMillis()
    val devices = allDevices.filter { 
        it.deviceId != "SELF" && 
        !it.deviceId.startsWith("ML-SIM") &&
        (it.name != "Unknown Device" || (now - it.lastSeen) < 120000)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeshBlack)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Nearby Devices", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (isScanning) "Searching for peers..." else "Ready to scan",
                    color = MeshGrey,
                    fontSize = 14.sp
                )
            }
            
            Button(
                onClick = { viewModel.startDiscovery() },
                enabled = !isScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MeshGreen,
                    disabledContainerColor = MeshGreen.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MeshBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Scan", color = MeshBlack, fontWeight = FontWeight.Bold)
                }
            }
        }

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

        if (devices.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No devices found yet", color = MeshGrey)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Tip: Ensure emulators are 'Paired' in Android Settings",
                        color = MeshGrey.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                items(devices) { device ->
                    DeviceRow(device, onDeviceClick)
                }
            }
        }
    }
}

@Composable
fun DeviceRow(device: DeviceEntity, onClick: (String, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MeshDarkBlue, RoundedCornerShape(12.dp))
            .clickable { onClick(device.deviceId, device.name) }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(Color(device.color), CircleShape))
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(device.deviceId, color = MeshGrey, fontSize = 12.sp)
        }
        Text("${device.rssi} dBm", color = MeshGreen, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun PeersScreenPreview() {
    MeshLinkTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MeshBlack)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nearby Devices", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Ready to scan", color = MeshGrey, fontSize = 14.sp)
                }
                
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = MeshGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Scan", color = MeshBlack, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

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
                items(emptyList<DeviceEntity>()) { device ->
                    DeviceRow(device, { _, _ -> })
                }
            }
        }
    }
}

