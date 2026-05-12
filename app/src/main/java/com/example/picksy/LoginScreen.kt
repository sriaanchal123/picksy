package com.example.picksy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var mobileNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val topGreen = Color(0xFF43A047)
    val themeGreen = Color(0xFF1B5E20)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(topGreen, Color.White)))
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Logo inside a clean white Circle
            Surface(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.Green, CircleShape),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(180.dp) // Adjusted size for better fit
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (!isOtpSent) "Welcome to Picksy" else "Verification",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeGreen
                    )
                    Text(
                        text = if (!isOtpSent) "Enter mobile to start shopping" else "Enter OTP sent to your number",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isOtpSent) {
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { if (it.length <= 10) mobileNumber = it },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("+91 9876543210") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeGreen,
                                focusedLabelColor = themeGreen
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 4) otp = it },
                            label = { Text("OTP (Use 1234)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeGreen,
                                focusedLabelColor = themeGreen
                            )
                        )
                    }

                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (!isOtpSent) {
                                if (mobileNumber.length == 10) {
                                    isOtpSent = true
                                    errorText = ""
                                } else {
                                    errorText = "Please enter a valid 10-digit number"
                                }
                            } else {
                                if (otp == "1234") {
                                    onLoginSuccess()
                                } else {
                                    errorText = "Incorrect OTP. Please use 1234"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (!isOtpSent) "Get OTP" else "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
