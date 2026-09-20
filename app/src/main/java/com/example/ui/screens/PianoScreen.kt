package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SunshineDark
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PianoScreen(
    viewModel: KidsViewModel,
    pianoMode: KidsViewModel.PianoMode,
    isGuideActive: Boolean,
    guidedStepIndex: Int,
    stars: Int,
    soundEnabled: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    val pianoKeys = listOf(
        Triple("Do", Color(0xFFFF5252), "🐶"), // C4
        Triple("Re", Color(0xFFFF7A00), "🐱"), // D4
        Triple("Mi", Color(0xFFFFD600), "🐮"), // E4
        Triple("Fa", Color(0xFF00E676), "🦆"), // F4
        Triple("Sol", Color(0xFF00B0FF), "🦁"), // G4
        Triple("La", Color(0xFF2979FF), "🐸"), // A4
        Triple("Si", Color(0xFFAA00FF), "🐑"), // B4
        Triple("İnce Do", Color(0xFFFF4081), "🐱") // C5
    )

    val targetKeyIndex = if (isGuideActive) {
        viewModel.nurseryMelodyNotes.getOrNull(guidedStepIndex)
    } else null

    val infiniteTransition = rememberInfiniteTransition(label = "guide_bounce")
    val guideScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "guide_star"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("piano_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Sihirli Piyano",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        // Control Strip: Mode Selector & Song Guide Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Toggle: Notes vs Animals
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (pianoMode == KidsViewModel.PianoMode.NOTES) SkyBlue else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.setPianoMode(KidsViewModel.PianoMode.NOTES) }
                            .testTag("piano_mode_notes")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🎹", fontSize = 16.sp)
                            Text(
                                "Piyano",
                                fontWeight = FontWeight.Bold,
                                color = if (pianoMode == KidsViewModel.PianoMode.NOTES) Color.White else DarkText,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (pianoMode == KidsViewModel.PianoMode.ANIMALS) MintGreen else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.setPianoMode(KidsViewModel.PianoMode.ANIMALS) }
                            .testTag("piano_mode_animals")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🐶", fontSize = 16.sp)
                            Text(
                                "Hayvanlar",
                                fontWeight = FontWeight.Bold,
                                color = if (pianoMode == KidsViewModel.PianoMode.ANIMALS) Color.White else DarkText,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Song Guide Toggle
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isGuideActive) SunnyYellow else Color(0xFFF1F5F9),
                    border = if (isGuideActive) androidx.compose.foundation.BorderStroke(2.dp, SunshineDark) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.toggleSongGuide() }
                        .testTag("piano_song_guide_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🎶", fontSize = 16.sp)
                        Text(
                            text = if (isGuideActive) "Rehber Açık" else "Şarkı Öğren",
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkText,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Song Guide Banner (if active)
        if (isGuideActive) {
            val currentNoteName = viewModel.nurseryMelodyNames.getOrNull(guidedStepIndex) ?: ""
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = SunnyYellow.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SunshineDark)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⭐", fontSize = 20.sp, modifier = Modifier.scale(guideScale))
                        Text(
                            text = "Şimdi bas: $currentNoteName",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = DarkText
                        )
                    }
                    Text(
                        text = "Adım ${guidedStepIndex + 1}/${viewModel.nurseryMelodyNotes.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Full Interactive Rainbow Piano Keyboard
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                pianoKeys.forEachIndexed { index, item ->
                    val isTarget = isGuideActive && targetKeyIndex == index
                    var isPressed by remember { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isPressed = true
                                viewModel.playPianoKey(index)
                                coroutineScope.launch {
                                    delay(150)
                                    isPressed = false
                                }
                            }
                            .testTag("piano_key_$index"),
                        shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
                        color = if (isPressed) item.second.copy(alpha = 0.7f) else item.second,
                        shadowElevation = if (isPressed) 1.dp else 6.dp,
                        border = if (isTarget) {
                            androidx.compose.foundation.BorderStroke(4.dp, Color.White)
                        } else {
                            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                // Top Guide Indicator or Key icon
                                if (isTarget) {
                                    Text(
                                        text = "👇",
                                        fontSize = 24.sp,
                                        modifier = Modifier.scale(guideScale)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.35f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("●", color = Color.White, fontSize = 10.sp)
                                    }
                                }

                                // Middle Emoji (Animal or Note)
                                Text(
                                    text = if (pianoMode == KidsViewModel.PianoMode.ANIMALS) item.third else "🎵",
                                    fontSize = 24.sp
                                )

                                // Bottom Note Name
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = item.first,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
