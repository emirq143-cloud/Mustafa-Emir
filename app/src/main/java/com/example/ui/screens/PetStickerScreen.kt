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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
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
import com.example.viewmodel.PlacedSticker
import kotlin.math.roundToInt

data class PetAccessoryItem(
    val emoji: String,
    val name: String,
    val requiredStickers: Int
)

@Composable
fun PetStickerScreen(
    viewModel: KidsViewModel,
    profile: ChildProfileEntity,
    stickers: List<StickerItemEntity>,
    petBounceTrigger: Int,
    petLastActionText: String,
    selectedAccessory: String?,
    placedStickers: List<PlacedSticker>,
    stars: Int,
    soundEnabled: Boolean
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val unlockedStickersCount = stickers.count { it.isUnlocked }

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

    val accessories = listOf(
        PetAccessoryItem("🎀", "Sevimli Fiyonk", 2),
        PetAccessoryItem("🕶️", "Güneş Gözlüğü", 4),
        PetAccessoryItem("🧢", "Gezgin Şapkası", 6),
        PetAccessoryItem("👑", "Altın Kral Tacı", 8),
        PetAccessoryItem("🚀", "Astronot Kaskı", 10)
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
            .background(Color(0xFFFFFBEB))
            .testTag("pet_sticker_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Tonton & Sihirli Park",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        // Custom Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = SkyBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SkyBlue,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    SoundPlayer.playTap()
                    selectedTab = 0
                },
                text = {
                    Text(
                        "🐻 Tonton & Giydir",
                        fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    SoundPlayer.playTap()
                    selectedTab = 1
                },
                text = {
                    Text(
                        "🎨 Sihirli Park",
                        fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = {
                    SoundPlayer.playTap()
                    selectedTab = 2
                },
                text = {
                    Text(
                        "📖 Albüm ($unlockedStickersCount)",
                        fontWeight = if (selectedTab == 2) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // TAB 0: Tonton Care & Wardrobe
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Status Bars (Tokluk & Sevgi)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Tokluk Barı
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Restaurant,
                                                    contentDescription = null,
                                                    tint = OceanBlue,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Tokluk",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DarkText
                                                )
                                            }
                                            Text(
                                                text = if (profile.petHunger >= 100) "Tok 😋" else "%${profile.petHunger}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = OceanBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { profile.petHunger / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = OceanBlue,
                                            trackColor = Color(0xFFE2E8F0)
                                        )
                                    }

                                    // Sevgi Barı
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    tint = CoralRed,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Sevgi",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DarkText
                                                )
                                            }
                                            Text(
                                                text = if (profile.petLove >= 100) "Mutlu 🥰" else "%${profile.petLove}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = CoralRed
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { profile.petLove / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = CoralRed,
                                            trackColor = Color(0xFFE2E8F0)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Interactive Mascot with Dynamic Accessory
                                Box(
                                    modifier = Modifier.size(160.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .size(136.dp)
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
                                            Text("🐻", fontSize = 70.sp)
                                        }
                                    }

                                    // Equipped accessory overlay on top of Tonton's head
                                    if (selectedAccessory != null) {
                                        Text(
                                            text = selectedAccessory,
                                            fontSize = 42.sp,
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-4).dp)
                                                .scale(petScale)
                                        )
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

                                // Pet button
                                Button(
                                    onClick = { viewModel.petMascot() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BubblegumPink),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("tickle_pet_btn")
                                ) {
                                    Text(
                                        text = if (profile.petLove >= 100) "Gıdı Gıdı Yap! 🥰" else "Gıdı Gıdı Yap! 🥰 (+1 ⭐)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Food Tray
                                Text(
                                    text = "Tonton'u Besle:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

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
                                                Text(food.first, fontSize = 30.sp)
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

                    // Tonton's Wardrobe (Aksesuarlar & Kıyafetler)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "👔 Tonton'un Gardırobu",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = DarkText
                                    )
                                    Text(
                                        text = "Albümden Açılır",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OceanBlue
                                    )
                                }

                                Text(
                                    text = "Çıkartma topladıkça Tonton'a özel şapkalar ve taçlar açılır!",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(accessories) { acc ->
                                        val isUnlocked = unlockedStickersCount >= acc.requiredStickers
                                        val isEquipped = selectedAccessory == acc.emoji

                                        Surface(
                                            modifier = Modifier
                                                .size(width = 90.dp, height = 110.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable(enabled = isUnlocked) {
                                                    viewModel.selectPetAccessory(acc.emoji)
                                                },
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isEquipped) SunnyYellow.copy(alpha = 0.3f) else if (isUnlocked) Color(0xFFF8FAFC) else Color(0xFFF1F5F9),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isEquipped) 2.dp else 1.dp,
                                                color = if (isEquipped) SunshineDark else if (isUnlocked) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                if (isUnlocked) {
                                                    Text(acc.emoji, fontSize = 32.sp)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = acc.name,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkText,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = if (isEquipped) "Giyildi ✓" else "Giy",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (isEquipped) SunshineDark else OceanBlue
                                                    )
                                                } else {
                                                    Icon(
                                                        Icons.Default.Lock,
                                                        contentDescription = null,
                                                        tint = Color(0xFF94A3B8),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "${acc.requiredStickers} Çıkartma",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF94A3B8),
                                                        textAlign = TextAlign.Center
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
            }

            1 -> {
                // TAB 1: Interactive Sticker Playground Canvas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Canvas Header Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎨 Sihirli Çıkartma Parkı",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )
                            Text(
                                text = "Çıkartmalarına dokun, parkına yerleştir!",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        if (placedStickers.isNotEmpty()) {
                            Button(
                                onClick = { viewModel.clearPlacedStickers() },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralRed.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Temizle", tint = CoralRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Temizle", color = CoralRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Playground Stage Box
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFBAE6FD), // Sky blue
                                        Color(0xFFE0F2FE),
                                        Color(0xFF86EFAC), // Soft grass green
                                        Color(0xFF4ADE80)
                                    )
                                )
                            )
                            .border(3.dp, Color(0xFF7DD3FC), RoundedCornerShape(24.dp))
                    ) {
                        val canvasW = constraints.maxWidth
                        val canvasH = constraints.maxHeight

                        // Decorative background sun & cloud
                        Text("☀️", fontSize = 42.sp, modifier = Modifier.offset(x = 16.dp, y = 16.dp))
                        Text("☁️", fontSize = 34.sp, modifier = Modifier.offset(x = 180.dp, y = 24.dp))
                        Text("🌸", fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 20.dp, y = (-12).dp))
                        Text("🌼", fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-24).dp, y = (-12).dp))
                        Text("🍄", fontSize = 18.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-10).dp))

                        // Render Placed Stickers
                        if (placedStickers.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White.copy(alpha = 0.75f)
                                ) {
                                    Text(
                                        text = "Aşağıdaki çıkartmalarından birine dokun,\nparkına eklensin! ✨",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DarkText,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        } else {
                            placedStickers.forEach { item ->
                                val posX = (item.xPercent * (canvasW - 140)).roundToInt()
                                val posY = (item.yPercent * (canvasH - 140)).roundToInt()

                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(posX, posY) }
                                        .size(54.dp)
                                        .clickable {
                                            SoundPlayer.playSparkle()
                                            viewModel.removePlacedSticker(item.id)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier.size(46.dp),
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.85f),
                                        shadowElevation = 4.dp
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(item.emoji, fontSize = 28.sp)
                                        }
                                    }
                                    // Mini remove badge
                                    Surface(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.TopEnd),
                                        shape = CircleShape,
                                        color = CoralRed
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Quick-Picker for Unlocked Stickers
                    Text(
                        text = "Açtığın Çıkartmalar (Parka Ekle):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val unlockedList = stickers.filter { it.isUnlocked }
                    if (unlockedList.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "Henüz açtığın çıkartma yok! 'Albüm' sekmesinden veya oyunlardan yıldız kazanarak çıkartma açabilirsin! 🌟",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(unlockedList) { stk ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            viewModel.placeStickerOnScene(stk)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SunnyYellow),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(stk.emoji, fontSize = 28.sp)
                                        Text(
                                            text = "+ Parka Ekle",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MintGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: Sticker Album & Shop
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📖 Koleksiyon Durumu",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = DarkText
                                    )
                                    Text(
                                        text = "$unlockedStickersCount / ${stickers.size} Çıkartma Açıldı",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OceanBlue
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SunnyYellow.copy(alpha = 0.25f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⭐", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$stars Yıldız",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = SunshineDark
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Açtığın her çıkartmayı Sihirli Park'ına ekleyebilirsin!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Stickers Grid
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(stickers) { sticker ->
                                Surface(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable {
                                            if (sticker.isUnlocked) {
                                                viewModel.placeStickerOnScene(sticker)
                                            } else if (profile.totalStars >= sticker.starCost) {
                                                viewModel.unlockSticker(sticker)
                                            }
                                        }
                                        .testTag("sticker_${sticker.id}"),
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (sticker.isUnlocked) Color.White else Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (sticker.isUnlocked) 2.dp else 1.dp,
                                        color = if (sticker.isUnlocked) SunnyYellow else Color(0xFFCBD5E1)
                                    ),
                                    shadowElevation = if (sticker.isUnlocked) 3.dp else 0.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (sticker.isUnlocked) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(sticker.emoji, fontSize = 34.sp)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = sticker.title,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DarkText,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MintGreen.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "Parka Ekle 🎨",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MintGreen,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            val canUnlock = profile.totalStars >= sticker.starCost
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Kilitli",
                                                    tint = Color(0xFF94A3B8),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (canUnlock) SunnyYellow else Color(0xFFE2E8F0)
                                                ) {
                                                    Text(
                                                        text = "${sticker.starCost} ⭐",
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        fontSize = 10.sp,
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
                        }
                    }
                }
            }
        }
    }
}
