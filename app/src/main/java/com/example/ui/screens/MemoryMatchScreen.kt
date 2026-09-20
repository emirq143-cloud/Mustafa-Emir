package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.DarkText
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SunshineDark
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel
import com.example.viewmodel.MemoryCard
import com.example.viewmodel.MemoryDifficulty

@Composable
fun MemoryMatchScreen(
    viewModel: KidsViewModel,
    cards: List<MemoryCard>,
    difficulty: MemoryDifficulty,
    moves: Int,
    pairsFound: Int,
    stars: Int,
    soundEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9)) // Soft mint pastel
            .testTag("memory_match_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Hayvan Eşleştirmece",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        // Difficulty Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MemoryDifficulty.values().forEach { diff ->
                val isSelected = difficulty == diff
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MintGreen else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.setMemoryDifficulty(diff) }
                        .testTag("memory_diff_${diff.name.lowercase()}"),
                    shadowElevation = if (isSelected) 3.dp else 1.dp
                ) {
                    Text(
                        text = diff.label,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else DarkText
                    )
                }
            }
        }

        // Stats & Restart Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎯", fontSize = 18.sp)
                    Text(
                        text = "Bulunan: $pairsFound / ${difficulty.pairsCount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔄", fontSize = 18.sp)
                    Text(
                        text = "Hamle: $moves",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = OceanBlue
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SunnyYellow.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.resetMemoryGame() }
                        .testTag("memory_reset_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Yeniden Başlat",
                            tint = DarkText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Cards Grid
        val columns = when (difficulty) {
            MemoryDifficulty.EASY -> 2
            MemoryDifficulty.MEDIUM -> 4
            MemoryDifficulty.CHAMPION -> 4
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            itemsIndexed(cards) { index, card ->
                MemoryCardItem(
                    card = card,
                    onClick = { viewModel.onCardClicked(index) }
                )
            }
        }
    }
}

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "card_flip"
    )

    val isFrontVisible = rotation > 90f

    val cardBorder = if (card.isMatched) {
        androidx.compose.foundation.BorderStroke(3.dp, MintGreen)
    } else if (card.isFaceUp) {
        androidx.compose.foundation.BorderStroke(3.dp, SkyBlue)
    } else {
        androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.5f))
    }

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = !card.isFaceUp && !card.isMatched,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag("memory_card_${card.id}"),
        shape = RoundedCornerShape(20.dp),
        border = cardBorder,
        shadowElevation = 4.dp
    ) {
        if (isFrontVisible) {
            // Card Face (Animal visible)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(
                        if (card.isMatched) {
                            Color(0xFFE8F5E9)
                        } else {
                            Color.White
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = card.emoji,
                        fontSize = 42.sp
                    )
                    if (card.isMatched) {
                        Text(
                            text = "Eşleşti! ⭐",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LimeGreen
                        )
                    }
                }
            }
        } else {
            // Card Back (Cover pattern)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(SkyBlue, OceanBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🐾", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
