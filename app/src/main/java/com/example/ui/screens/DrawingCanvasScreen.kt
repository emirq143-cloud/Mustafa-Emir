package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BubblegumPink
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.util.SoundPlayer
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel

data class DrawStroke(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

data class DrawStamp(
    val emoji: String,
    val offset: Offset
)

enum class DrawTool { BRUSH, STAMP, ERASER }

@Composable
fun DrawingCanvasScreen(
    viewModel: KidsViewModel,
    stars: Int,
    soundEnabled: Boolean
) {
    val strokes = remember { mutableStateListOf<DrawStroke>() }
    val stamps = remember { mutableStateListOf<DrawStamp>() }

    var currentTool by remember { mutableStateOf(DrawTool.BRUSH) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF5252)) }
    var strokeWidth by remember { mutableFloatStateOf(16f) }
    var selectedStamp by remember { mutableStateOf("⭐") }

    var currentPath by remember { mutableStateOf<Path?>(null) }

    val paletteColors = listOf(
        Color(0xFFFF5252), // Red
        Color(0xFFFF9800), // Orange
        Color(0xFFFFD600), // Yellow
        Color(0xFF4CAF50), // Green
        Color(0xFF00BCD4), // Cyan
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63), // Pink
        Color(0xFF263238)  // Dark Slate
    )

    val stampEmojis = listOf("⭐", "💖", "🐾", "🌈", "👑", "🌸", "🍦", "🚀", "🦄", "🧸")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5))
            .testTag("drawing_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Sihirli Boyama",
            stars = stars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            showBackButton = true,
            onBackClick = { viewModel.navigateTo(KidsScreen.Home) }
        )

        // Tools Strip: Fırça, Damgalar, Silgi, Temizle, Kaydet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
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
                // Brush vs Stamp vs Eraser
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentTool == DrawTool.BRUSH) SkyBlue else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                SoundPlayer.playTap()
                                currentTool = DrawTool.BRUSH
                            }
                            .testTag("tool_brush")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🖌️", fontSize = 16.sp)
                            Text(
                                "Fırça",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (currentTool == DrawTool.BRUSH) Color.White else DarkText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentTool == DrawTool.STAMP) CandyPurple else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                SoundPlayer.playTap()
                                currentTool = DrawTool.STAMP
                            }
                            .testTag("tool_stamp")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✨", fontSize = 16.sp)
                            Text(
                                "Damga",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (currentTool == DrawTool.STAMP) Color.White else DarkText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentTool == DrawTool.ERASER) CoralRed else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                SoundPlayer.playTap()
                                currentTool = DrawTool.ERASER
                            }
                            .testTag("tool_eraser")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🧹", fontSize = 16.sp)
                            Text(
                                "Silgi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (currentTool == DrawTool.ERASER) Color.White else DarkText
                            )
                        }
                    }
                }

                // Clear & Done
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable {
                                SoundPlayer.playPop()
                                strokes.clear()
                                stamps.clear()
                            }
                            .testTag("clear_canvas_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🗑️", fontSize = 18.sp)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.saveDrawingMasterpiece()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_drawing_btn")
                    ) {
                        Text("Sergile ⭐", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }

        // Second Strip: Colors or Stamps
        if (currentTool == DrawTool.BRUSH) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(paletteColors) { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) DarkText else Color.White,
                                shape = CircleShape
                            )
                            .clickable {
                                SoundPlayer.playTap()
                                selectedColor = color
                            }
                    )
                }

                // Brush Size Toggles
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        listOf(Pair("İnce", 8f), Pair("Orta", 16f), Pair("Kalın", 28f)).forEach { sizePair ->
                            val isSel = strokeWidth == sizePair.second
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) SkyBlue else Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { strokeWidth = sizePair.second }
                            ) {
                                Text(
                                    text = sizePair.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else DarkText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (currentTool == DrawTool.STAMP) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stampEmojis) { emoji ->
                    val isSelected = selectedStamp == emoji
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) SunnyYellow else Color.White,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, DarkText) else null,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable {
                                SoundPlayer.playTap()
                                selectedStamp = emoji
                            },
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Drawing Surface
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(3.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentTool, selectedColor, strokeWidth, selectedStamp) {
                        if (currentTool == DrawTool.STAMP) {
                            detectTapGestures { offset ->
                                SoundPlayer.playPop()
                                stamps.add(DrawStamp(emoji = selectedStamp, offset = offset))
                            }
                        } else {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val path = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    currentPath = path
                                    val colorToUse = if (currentTool == DrawTool.ERASER) Color.White else selectedColor
                                    val widthToUse = if (currentTool == DrawTool.ERASER) strokeWidth * 2 else strokeWidth
                                    strokes.add(DrawStroke(path, colorToUse, widthToUse))
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    // Trigger recomposition by refreshing list
                                    if (strokes.isNotEmpty()) {
                                        val last = strokes.last()
                                        strokes[strokes.lastIndex] = last.copy()
                                    }
                                },
                                onDragEnd = {
                                    currentPath = null
                                }
                            )
                        }
                    }
            ) {
                // Draw strokes
                for (stroke in strokes) {
                    drawPath(
                        path = stroke.path,
                        color = stroke.color,
                        style = Stroke(
                            width = stroke.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw stamps with Android text paint
                val textPaint = android.graphics.Paint().apply {
                    textSize = 90f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                for (stamp in stamps) {
                    drawContext.canvas.nativeCanvas.drawText(
                        stamp.emoji,
                        stamp.offset.x,
                        stamp.offset.y + 35f,
                        textPaint
                    )
                }
            }

            if (strokes.isEmpty() && stamps.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎨", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Parmağınla çizmeye başla!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
