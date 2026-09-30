package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.viewmodel.FallingStarItem
import com.example.viewmodel.KidsViewModel
import kotlinx.coroutines.delay
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
    val lastCatchEffect by viewModel.lastCatchEffect.collectAsStateWithLifecycle()

    // Automatically clear catch toast effect after a brief time
    LaunchedEffect(lastCatchEffect?.id) {
        if (lastCatchEffect != null) {
            delay(750)
            viewModel.clearLastCatchEffect()
        }
    }

    // Twinkling background star effect
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_twinkle")
    val twinkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )
    val noseGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nose_glow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B0F19),
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
                .padding(horizontal = 16.dp, vertical = 6.dp),
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
                    Text("⭐", fontSize = 16.sp)
                    Text(
                        text = "$score Puan",
                        fontWeight = FontWeight.Black,
                        color = SunnyYellow,
                        fontSize = 15.sp
                    )
                }
            }

            // Stars Collected Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CandyPurple.copy(alpha = 0.35f),
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
                color = if (timeRemaining <= 10) CoralRed.copy(alpha = 0.45f) else SkyBlue.copy(alpha = 0.25f),
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
                            Color(0xFF080C14),
                            Color(0xFF131138),
                            Color(0xFF241442)
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

            // 1. Subtle Guide Lanes (5 well-spaced vertical corridors)
            KidsViewModel.ROCKET_LANES.forEach { laneX ->
                val px = (laneX * arenaWidth).roundToInt()
                Box(
                    modifier = Modifier
                        .offset { IntOffset(px - 1, 0) }
                        .width(2.dp)
                        .height((arenaHeight * 0.82f).dp)
                        .alpha(0.08f)
                        .background(Color.White)
                )
            }

            // Decorative background celestial stars
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.12f).dp, y = (arenaHeight * 0.10f).dp)
                    .scale(twinkleAlpha)
            ) { Text("✨", fontSize = 14.sp, color = Color.White.copy(alpha = twinkleAlpha)) }
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.82f).dp, y = (arenaHeight * 0.22f).dp)
                    .scale(twinkleAlpha)
            ) { Text("🌟", fontSize = 16.sp, color = Color.White.copy(alpha = twinkleAlpha)) }
            Box(
                modifier = Modifier
                    .offset(x = (arenaWidth * 0.40f).dp, y = (arenaHeight * 0.38f).dp)
                    .scale(twinkleAlpha)
            ) { Text("✨", fontSize = 12.sp, color = Color.White.copy(alpha = twinkleAlpha)) }

            // 2. Falling Items (Real-time synced from ViewModel game engine)
            fallingStars.forEach { item ->
                key(item.id) {
                    val itemX = (item.xRatio * arenaWidth).roundToInt()
                    val itemY = (item.yRatio * arenaHeight).roundToInt()

                    // Fading alpha if missed and dropping past the rocket
                    val itemAlpha = when {
                        item.isCaught -> 0.95f
                        item.isMissed -> (1.0f - (item.yRatio - 0.83f) * 4.5f).coerceIn(0.0f, 1.0f)
                        else -> 1.0f
                    }

                    val itemScale = if (item.isCaught) 1.25f else 1.0f

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(itemX - 26, itemY - 26) }
                            .size(52.dp)
                            .alpha(itemAlpha)
                            .scale(itemScale)
                            .testTag("falling_star_${item.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = when {
                                item.isCaught -> SunnyYellow.copy(alpha = 0.5f)
                                item.isSuper -> SunnyYellow.copy(alpha = 0.35f)
                                else -> Color.White.copy(alpha = 0.18f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (item.isSuper || item.isCaught) SunnyYellow else Color.White.copy(alpha = 0.45f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (item.isCaught) "✨" else item.emoji,
                                    fontSize = if (item.isSuper) 28.sp else 24.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Floating Score Toast Notification when a star is caught
            androidx.compose.animation.AnimatedVisibility(
                visible = lastCatchEffect != null,
                enter = fadeIn() + scaleIn() + slideInVertically(initialOffsetY = { 20 }),
                exit = fadeOut() + scaleOut() + slideOutVertically(targetOffsetY = { -20 }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (arenaHeight * 0.68f).dp)
            ) {
                lastCatchEffect?.let { effect ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BrightOrange,
                        border = androidx.compose.foundation.BorderStroke(2.dp, SunnyYellow),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(effect.emoji, fontSize = 18.sp)
                            Text(
                                text = "+${effect.points} Harika! ⭐",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // 4. The Rocket Player at the Bottom
            val rocketPixelX = (rocketX * arenaWidth).roundToInt()
            val rocketPixelY = (arenaHeight * 0.79f).roundToInt()

            Column(
                modifier = Modifier
                    .offset { IntOffset(rocketPixelX - 42, rocketPixelY) }
                    .size(84.dp)
                    .testTag("rocket_avatar"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rocket Nose Cone Capture Glow Zone
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(noseGlowScale)
                        .clip(CircleShape)
                        .background(SkyBlue.copy(alpha = 0.5f))
                )

                // Rocket Body
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.22f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyBlue)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🚀", fontSize = 38.sp)
                    }
                }

                // Rocket Flame Engine
                Box(
                    modifier = Modifier
                        .scale(flameScale)
                        .padding(top = 1.dp)
                ) {
                    Text("🔥", fontSize = 16.sp)
                }
            }

            // 5. Game Over overlay when round concludes
            if (!isActive && timeRemaining <= 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
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

        // Bottom Left/Right Directional Controls for Children (Snapped by Lane)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Button (Snaps to previous star lane)
            Button(
                onClick = {
                    com.example.util.SoundPlayer.playTap()
                    viewModel.moveRocketBy(-1f)
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

            // Right Button (Snaps to next star lane)
            Button(
                onClick = {
                    com.example.util.SoundPlayer.playTap()
                    viewModel.moveRocketBy(1f)
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
