package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.viewmodel.KidsViewModel
import com.example.viewmodel.PuzzlePiece
import com.example.viewmodel.PuzzleSlot
import com.example.viewmodel.PuzzleTheme

@Composable
fun PuzzleScreen(
    viewModel: KidsViewModel,
    selectedTheme: PuzzleTheme,
    slots: List<PuzzleSlot>,
    trayPieces: List<PuzzlePiece>,
    selectedPieceId: Int?,
    isCompleted: Boolean,
    stars: Int,
    soundEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .testTag("puzzle_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Sevimli Yapboz 🧩",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() }
        )

        // Theme Selector Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(viewModel.puzzleThemes) { theme ->
                val isSelected = theme.id == selectedTheme.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.selectPuzzleTheme(theme) }
                        .testTag("puzzle_theme_${theme.id}"),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) SkyBlue else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) SkyBlue else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (isSelected) 4.dp else 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = theme.mainEmoji, fontSize = 20.sp)
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) Color.White else DarkText
                        )
                    }
                }
            }
        }

        // Instructions banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("👇", fontSize = 16.sp)
                Text(
                    text = if (isCompleted) "Tebrikler! Yapboz Tamamlandı! 🎉" else "Parçayı seç ve doğru kareye yerleştir!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) MintGreen else Color(0xFF64748B)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SunnyYellow.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "+${selectedTheme.rewardStars} ⭐",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = DarkText
                )
            }
        }

        // Main Puzzle Board (2x2 Grid)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .aspectRatio(1.05f)
                .testTag("puzzle_board_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top Row (slots 0 and 1)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PuzzleSlotView(
                            modifier = Modifier.weight(1f),
                            slotIndex = 0,
                            slot = slots.getOrNull(0),
                            theme = selectedTheme,
                            onSlotClick = { viewModel.onSlotClicked(0) }
                        )
                        PuzzleSlotView(
                            modifier = Modifier.weight(1f),
                            slotIndex = 1,
                            slot = slots.getOrNull(1),
                            theme = selectedTheme,
                            onSlotClick = { viewModel.onSlotClicked(1) }
                        )
                    }

                    // Bottom Row (slots 2 and 3)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PuzzleSlotView(
                            modifier = Modifier.weight(1f),
                            slotIndex = 2,
                            slot = slots.getOrNull(2),
                            theme = selectedTheme,
                            onSlotClick = { viewModel.onSlotClicked(2) }
                        )
                        PuzzleSlotView(
                            modifier = Modifier.weight(1f),
                            slotIndex = 3,
                            slot = slots.getOrNull(3),
                            theme = selectedTheme,
                            onSlotClick = { viewModel.onSlotClicked(3) }
                        )
                    }
                }
            }
        }

        // Action Controls (Magic Hint & Reset)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.resetCurrentPuzzle() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("reset_puzzle_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Yeniden Başla", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Yeniden Başla", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.magicHelpPuzzle() },
                enabled = !isCompleted,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CandyPurple),
                modifier = Modifier.testTag("magic_hint_btn")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Sihirli Yardım", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sihirli Yardım ✨", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Piece Tray at Bottom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("puzzle_pieces_tray"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF1F5F9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🧩 Yapboz Parçaların",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )
                    Text(
                        text = "Dokun ve Tahtaya Yerleştir",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    trayPieces.forEach { piece ->
                        TrayPieceItem(
                            piece = piece,
                            isSelected = selectedPieceId == piece.id,
                            onPieceClick = {
                                viewModel.selectTrayPiece(piece.id)
                            },
                            onPieceDoubleClick = {
                                viewModel.autoSnapPiece(piece.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleSlotView(
    modifier: Modifier = Modifier,
    slotIndex: Int,
    slot: PuzzleSlot?,
    theme: PuzzleTheme,
    onSlotClick: () -> Unit
) {
    val isPlaced = slot?.placedPieceId != null
    val isCorrect = slot?.isCorrect == true
    val pieceId = slot?.placedPieceId

    val pieceColorHex = if (pieceId != null && pieceId in theme.pieceColors.indices) {
        theme.pieceColors[pieceId]
    } else {
        0xFFEEEEEE
    }
    val pieceEmoji = if (pieceId != null && pieceId in theme.pieceEmojis.indices) {
        theme.pieceEmojis[pieceId]
    } else {
        theme.pieceEmojis.getOrElse(slotIndex) { "❓" }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSlotClick)
            .testTag("puzzle_slot_$slotIndex"),
        shape = RoundedCornerShape(20.dp),
        color = if (isPlaced) Color(pieceColorHex) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCorrect) 3.dp else 2.dp,
            color = if (isCorrect) MintGreen else if (isPlaced) CoralRed else Color(0xFFCBD5E1)
        ),
        shadowElevation = if (isPlaced) 3.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaced) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pieceEmoji,
                        fontSize = 46.sp
                    )
                }

                if (isCorrect) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(MintGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Doğru",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                // Ghost silhouette of what fits here
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = theme.pieceEmojis.getOrElse(slotIndex) { "" },
                        fontSize = 36.sp,
                        modifier = Modifier.scale(0.85f),
                        color = Color.Black.copy(alpha = 0.12f)
                    )
                    Text(
                        text = "Buraya Koy",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrayPieceItem(
    piece: PuzzlePiece,
    isSelected: Boolean,
    onPieceClick: () -> Unit,
    onPieceDoubleClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_piece")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "piece_scale"
    )

    if (piece.isPlaced) {
        // Show empty placeholder in tray
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE2E8F0).copy(alpha = 0.5f))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("✔️", fontSize = 20.sp, color = Color.Gray.copy(alpha = 0.5f))
        }
    } else {
        Surface(
            modifier = Modifier
                .size(68.dp)
                .scale(pulseScale)
                .clip(RoundedCornerShape(18.dp))
                .clickable { onPieceClick() }
                .testTag("tray_piece_${piece.id}"),
            shape = RoundedCornerShape(18.dp),
            color = Color(piece.colorHex),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) SkyBlue else Color.White.copy(alpha = 0.8f)
            ),
            shadowElevation = if (isSelected) 8.dp else 3.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = piece.emoji,
                    fontSize = 34.sp
                )
            }
        }
    }
}
