package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.viewmodel.FallingStarItem
import com.example.viewmodel.KidsViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun RocketGameScreen(
    viewModel: KidsViewModel,
    rocketX: Float,
    fallingStars: List<FallingStarItem>,
    score: Int,
    starsCollected: Int,
    timeRemaining: Int,
    isActive: Boolean,
    stars: Int,
    soundEnabled: Boolean
) {
    // Local animation state for falling items (smooth fall on screen)
    val itemProgressMap = remember { mutableStateListOf<Pair<Long, Float>>() }

    // Twinkling background star effect
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_twinkle")
    val twinkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81)
                    )
                )
            )
            .testTag("rocket_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Yıldız Avcısı Roket 🚀",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() }
        )

        // Game Stats Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⭐", fontSize = 18.sp)
                    Text(
                        text = "$score Puan",
                        fontWeight = FontWeight.Black,
                        color = SunnyYellow,
                        fontSize = 16.sp
                    )
                }
            }

            // Stars Collected Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CandyPurple.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CandyPurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Toplanan:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$starsCollected",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }

            // Time Remaining Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (timeRemaining <= 10) CoralRed.copy(alpha = 0.4f) else SkyBlue.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⏳", fontSize = 14.sp)
                    Text(
                        text = "${timeRemaining}s",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (timeRemaining <= 10) CoralRed else Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Sky & Space Playing Arena
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF090D16),
                            Color(0xFF1E1B4B),
                            Color(0xFF2E1065)
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newRatio = change.position.x / size.width
                        viewModel.setRocketPosition(newRatio)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val tapRatio = offset.x / size.width
                        viewModel.setRocketPosition(tapRatio)
                    }
                }
                .testTag("rocket_play_arena")
        ) {
            val arenaWidth = constraints.maxWidth.toFloat()
            val arenaHeight = constraints.maxHeight.toFloat()

            // Decorative background stars
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.15f).dp, y = (arenaHeight * 0.1f).dp)
                    .scale(twinkleAlpha)
            ) { Text("✨", fontSize = 14.sp, color = Color.White.copy(alpha = twinkleAlpha)) }
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.8f).dp, y = (arenaHeight * 0.25f).dp)
                    .scale(twinkleAlpha)
            ) { Text("🌟", fontSize = 16.sp, color = Color.White.copy(alpha = twinkleAlpha)) }
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.35f).dp, y = (arenaHeight * 0.45f).dp)
                    .scale(twinkleAlpha)
            ) { Text("✨", fontSize = 12.sp, color = Color.White.copy(alpha = twinkleAlpha)) }

            // Falling items
            fallingStars.forEach { item ->
                // Local fall tracking
                var localProgress by remember(item.id) { mutableStateOf(0f) }

                LaunchedEffect(item.id, isActive) {
                    if (!isActive) return@LaunchedEffect
                    while (localProgress < 1.0f && isActive) {
                        delay(25)
                        localProgress += item.speed
                        // Precise collision check when item reaches rocket nose cone (altitude 0.76f to 0.86f)
                        if (localProgress in 0.76f..0.86f) {
                            val dist = abs(item.xRatio - rocketX)
                            // Realistic direct hit: rocket must be positioned right under the falling item
                            if (dist < 0.085f) {
                                viewModel.catchStarItem(item.id)
                                break
                            }
                        }
                    }
                    if (localProgress >= 1.0f) {
                        // Cleared bottom without hitting the rocket -> Missed!
                        viewModel.missStarItem(item.id)
                    }
                }

                val itemX = (item.xRatio * arenaWidth).roundToInt()
                val itemY = (localProgress * arenaHeight * 0.85f).roundToInt()

                Box(
                    modifier = Modifier
                        .offset { IntOffset(itemX - 28, itemY) }
                        .size(56.dp)
                        .testTag("falling_star_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow background
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = if (item.isSuper) SunnyYellow.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (item.isSuper) SunnyYellow else Color.White.copy(alpha = 0.4f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.emoji,
                                fontSize = if (item.isSuper) 30.sp else 26.sp
                            )
                        }
                    }
                }
            }

            // The Rocket Player at the Bottom
            val rocketPixelX = (rocketX * arenaWidth).roundToInt()
            val rocketPixelY = (arenaHeight * 0.82f).roundToInt()

            Column(
                modifier = Modifier
                    .offset { IntOffset(rocketPixelX - 44, rocketPixelY) }
                    .size(88.dp)
                    .testTag("rocket_avatar"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rocket Emoji
                Surface(
                    modifier = Modifier.size(68.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyBlue)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🚀", fontSize = 42.sp)
                    }
                }
                // Rocket Flame
                Box(
                    modifier = Modifier
                        .scale(flameScale)
                        .padding(top = 2.dp)
                ) {
                    Text("🔥", fontSize = 18.sp)
                }
            }

            // Game Over overlay if time runs out
            if (!isActive && timeRemaining <= 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 54.sp)
                        Text(
                            text = "Harika Uçuş!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$starsCollected Ödül Topladın! Skorun: $score",
                            color = SunnyYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.startRocketGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("restart_rocket_btn")
                        ) {
                            Text("Tekrar Uç! 🚀", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Bottom Left/Right Touch Controls for Small Children
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Button
            Button(
                onClick = {
                    com.example.util.SoundPlayer.playTap()
                    viewModel.moveRocketBy(-0.17f)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .size(width = 100.dp, height = 52.dp)
                    .testTag("rocket_left_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Sola Git",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Center Restart / Pause
            IconButton(
                onClick = {
                    if (isActive) viewModel.stopRocketGame() else viewModel.startRocketGame()
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .testTag("rocket_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.Refresh else Icons.Default.PlayArrow,
                    contentDescription = "Oyna",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Right Button
            Button(
                onClick = {
                    com.example.util.SoundPlayer.playTap()
                    viewModel.moveRocketBy(0.17f)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .size(width = 100.dp, height = 52.dp)
                    .testTag("rocket_right_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Sağa Git",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
