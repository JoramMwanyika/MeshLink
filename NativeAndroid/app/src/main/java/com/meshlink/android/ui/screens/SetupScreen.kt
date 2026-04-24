package com.meshlink.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(onContinue: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeshBlack)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Create Your Identity",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "This helps others recognize you in the mesh network.",
            color = MeshGrey,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 10.dp, bottom = 40.dp)
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MeshGreen, CircleShape)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("John Doe", color = MeshGrey) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MeshDarkBlue, RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MeshDarkBlue,
                unfocusedContainerColor = MeshDarkBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MeshDarkBlue, RoundedCornerShape(12.dp))
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Identity will be generated on Continue", color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onContinue(name) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MeshGreen,
                disabledContainerColor = MeshGreen.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                color = MeshBlack,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
