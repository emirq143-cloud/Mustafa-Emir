package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BubblegumPink
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SunshineDark
import com.example.viewmodel.BalloonItem
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BalloonPopScreen(
    viewModel: KidsViewModel,
    balloons: List<BalloonItem>,
    score: Int,
    combo: Int,
    timeRemaining: Int,
    isActive: Boolean,
    stars: Int,
    soundEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFFE0F7FA),
                        Color(0xFFFFF9C4)  // Warm grass/sunshine tone
                    )
                )
            )
            .testTag("balloon_pop_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Balon Patlat!",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        // Status Panel: Time, Score, Combo
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⏳", fontSize = 20.sp)
                    Text(
                        text = "$timeRemaining s",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (timeRemaining <= 10) CoralRed else DarkText
                    )
                }

                // Combo Badge
                if (combo >= 3) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralRed
                    ) {
                        Text(
                            text = "🔥 x${if (combo >= 5) "2" else "1.5"} KOMBO!",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // Score
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎈", fontSize = 20.sp)
                    Text(
                        text = "$score Puan",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = OceanBlue
                    )
                }
            }
        }

        // Active Playing Sky Area
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val screenHeight = maxHeight
            val screenWidth = maxWidth

            if (!isActive && timeRemaining == 0) {
                // Game Over Overlay Card
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .testTag("balloon_game_over_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Harika Patlattın!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toplam Puan: $score",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = CoralRed
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.startBalloonGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("balloon_replay_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tekrar Oyna! 🎈", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }

            // Render Floating Balloons with isolated keys and clean hitboxes
            for (balloon in balloons) {
                key(balloon.id) {
                    FloatingBalloon(
                        balloon = balloon,
                        screenHeightDp = screenHeight.value,
                        screenWidthDp = screenWidth.value,
                        onPop = { viewModel.popBalloon(balloon.id) },
                        onFinished = { viewModel.removeBalloon(balloon.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingBalloon(
    balloon: BalloonItem,
    screenHeightDp: Float,
    screenWidthDp: Float,
    onPop: () -> Unit,
    onFinished: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    val popScale = remember { Animatable(1.0f) }
    val popAlpha = remember { Animatable(1.0f) }

    val yAnim = remember { Animatable(screenHeightDp + 50f) }

    // Steady and pleasant upward floating
    LaunchedEffect(balloon.id) {
        val durationMs = 5600
        yAnim.animateTo(
            targetValue = -130f,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
        onFinished()
    }

    // Balloon pop burst animation
    LaunchedEffect(balloon.isPopped) {
        if (balloon.isPopped) {
            launch {
                popScale.animateTo(1.45f, tween(150, easing = FastOutSlowInEasing))
            }
            launch {
                popAlpha.animateTo(0f, tween(150))
            }
            delay(160)
            onFinished()
        }
    }

    // Precise placement according to lane to prevent any multi-touch collision
    val safeWidth = (screenWidthDp - 85f).coerceAtLeast(120f)
    val xPos = (safeWidth * balloon.xRatio).coerceIn(12f, safeWidth)

    Box(
        modifier = Modifier
            .offset(x = xPos.dp, y = yAnim.value.dp)
            .size(76.dp)
            .scale(popScale.value)
            .alpha(popAlpha.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isClicked && !balloon.isPopped
            ) {
                if (!isClicked && !balloon.isPopped) {
                    isClicked = true
                    onPop()
                }
            }
            .testTag("balloon_${balloon.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (balloon.isPopped) {
            // Delightful pop burst sparkles
            Text("✨", fontSize = 38.sp)
        } else {
            // Balloon Body
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                color = Color(balloon.colorHex),
                border = androidx.compose.foundation.BorderStroke(
                    3.dp,
                    if (balloon.isGolden) Color(0xFFFFD700) else Color.White.copy(alpha = 0.7f)
                ),
                shadowElevation = 5.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = balloon.emoji,
                        fontSize = 32.sp
                    )
                }
            }

            // Small tied knot at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .size(10.dp),
                shape = RoundedCornerShape(2.dp),
                color = Color(balloon.colorHex)
            ) {}
        }
    }
}
