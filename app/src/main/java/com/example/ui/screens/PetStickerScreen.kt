package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChildProfileEntity
import com.example.data.local.StickerItemEntity
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
import com.example.util.SoundPlayer
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel

@Composable
fun PetStickerScreen(
    viewModel: KidsViewModel,
    profile: ChildProfileEntity,
    stickers: List<StickerItemEntity>,
    petBounceTrigger: Int,
    petLastActionText: String,
    stars: Int,
    soundEnabled: Boolean
) {
    val petScale by animateFloatAsState(
        targetValue = if (petBounceTrigger % 2 == 1) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "pet_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val heartPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_scale"
    )

    val foods = listOf(
        Pair("🍎", "Elma"),
        Pair("🥕", "Havuç"),
        Pair("🍦", "Dondurma"),
        Pair("🧁", "Kek"),
        Pair("🥛", "Süt")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB)) // Soft sunny yellow background
            .testTag("pet_sticker_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Sevimli Tonton & Albüm",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pet Interaction Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🐻 Ayıcık Tonton",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BubblegumPink.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = CoralRed,
                                        modifier = Modifier.size(16.dp).scale(heartPulse)
                                    )
                                    Text(
                                        text = "%${profile.petLove} Sevgi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CoralRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Big Interactive Mascot (Tap to Pet / Tickle)
                        Surface(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(petScale)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.petMascot() }
                                .testTag("pet_mascot_avatar"),
                            shape = CircleShape,
                            color = SunnyYellow.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(3.dp, SunshineDark)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🐻", fontSize = 72.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = petLastActionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanBlue,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pet button: "Gıdı Gıdı Yap! 🥰"
                        Button(
                            onClick = { viewModel.petMascot() },
                            colors = ButtonDefaults.buttonColors(containerColor = BubblegumPink),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("tickle_pet_btn")
                        ) {
                            Text("Gıdı Gıdı Yap! 🥰 (+2 ⭐)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Food Tray Label
                        Text(
                            text = "Tonton'u Besle (Lezzetli Yiyecekler):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Food Items Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            foods.forEach { food ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { viewModel.feedPet(food.first) }
                                        .testTag("feed_${food.second}"),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF1F5F9),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(food.first, fontSize = 32.sp)
                                        Text(
                                            food.second,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sticker Album Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 Sihirli Çıkartma Albümü",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = DarkText,
                        fontSize = 20.sp
                    )
                    val unlockedCount = stickers.count { it.isUnlocked }
                    Text(
                        text = "$unlockedCount / ${stickers.size}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OceanBlue
                    )
                }
            }

            // Sticker Grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(stickers) { sticker ->
                        StickerGridCard(
                            sticker = sticker,
                            canUnlock = profile.totalStars >= sticker.starCost,
                            onUnlock = { viewModel.unlockSticker(sticker) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StickerGridCard(
    sticker: StickerItemEntity,
    canUnlock: Boolean,
    onUnlock: () -> Unit
) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !sticker.isUnlocked && canUnlock) { onUnlock() }
            .testTag("sticker_${sticker.id}"),
        shape = RoundedCornerShape(20.dp),
        color = if (sticker.isUnlocked) Color.White else Color(0xFFF1F5F9),
        border = if (sticker.isUnlocked) {
            androidx.compose.foundation.BorderStroke(2.dp, SunnyYellow)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
        },
        shadowElevation = if (sticker.isUnlocked) 3.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (sticker.isUnlocked) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(sticker.emoji, fontSize = 38.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = sticker.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Kilitli",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (canUnlock) SunnyYellow else Color(0xFFE2E8F0)
                    ) {
                        Text(
                            text = "${sticker.starCost} ⭐",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canUnlock) DarkText else Color(0xFF64748B)
                        )
                    }
                    if (canUnlock) {
                        Text(
                            text = "Aç!",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MintGreen
                        )
                    }
                }
            }
        }
    }
}
