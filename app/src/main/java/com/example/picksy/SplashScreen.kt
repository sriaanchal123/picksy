package com.example.picksy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(onGetStartedClick: () -> Unit) {
    // Background colors: Thoda zyada dark aur saturated green shades
    val topGreen = Color(0xFF43A047)   // Darker Green (Green 600)
    val bottomGreen = Color(0xFFC8E6C9) // Light Green (Green 100) instead of White
    val themeGreen = Color(0xFF1B5E20) // Deep Green for button contrast

    // Image bitmap for Canvas
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.splashscreen)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topGreen, bottomGreen)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Canvas image ko background ke saath blend karne ke liye
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1f)
            ) {
                // 1. Image draw karna with BlendMode.Multiply
                // Multiply mode image ko darker background ke sath naturally blend karta hai
                drawImage(
                    image = imageBitmap,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                    blendMode = BlendMode.Multiply
                )

                // 2. Halka sa dark green tint subjects par apply kiya hai
                drawRect(
                    color = themeGreen.copy(alpha = 0.05f),
                    size = size,
                    blendMode = BlendMode.SrcAtop
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Button image ke just niche

            // Get Started button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
