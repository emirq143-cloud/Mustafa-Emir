package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.KidsCelebrationModal
import com.example.ui.screens.BalloonPopScreen
import com.example.ui.screens.DrawingCanvasScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryMatchScreen
import com.example.ui.screens.PetStickerScreen
import com.example.ui.screens.PianoScreen
import com.example.ui.screens.PuzzleScreen
import com.example.ui.screens.RocketGameScreen
import com.example.ui.theme.DarkText
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel

data class KidsNavItem(
    val title: String,
    val icon: ImageVector,
    val targetScreen: KidsScreen,
    val testTag: String
)

@Composable
fun MainScreen(
    viewModel: KidsViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val stickers by viewModel.stickers.collectAsStateWithLifecycle()
    val recentActivities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val celebration by viewModel.celebration.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()

    // Balloon state
    val balloons by viewModel.balloons.collectAsStateWithLifecycle()
    val balloonScore by viewModel.balloonScore.collectAsStateWithLifecycle()
    val balloonCombo by viewModel.balloonCombo.collectAsStateWithLifecycle()
    val balloonTimeRemaining by viewModel.balloonTimeRemaining.collectAsStateWithLifecycle()
    val isBalloonActive by viewModel.isBalloonGameActive.collectAsStateWithLifecycle()

    // Piano state
    val pianoMode by viewModel.pianoMode.collectAsStateWithLifecycle()
    val isGuideActive by viewModel.isGuideActive.collectAsStateWithLifecycle()
    val guidedStepIndex by viewModel.guidedStepIndex.collectAsStateWithLifecycle()

    // Memory match state
    val memoryCards by viewModel.memoryCards.collectAsStateWithLifecycle()
    val memoryDifficulty by viewModel.memoryDifficulty.collectAsStateWithLifecycle()
    val memoryMoves by viewModel.memoryMoves.collectAsStateWithLifecycle()
    val memoryPairsFound by viewModel.memoryPairsFound.collectAsStateWithLifecycle()

    // Pet state
    val petBounceTrigger by viewModel.petBounceTrigger.collectAsStateWithLifecycle()
    val petLastActionText by viewModel.petLastActionText.collectAsStateWithLifecycle()
    val selectedPetAccessory by viewModel.selectedPetAccessory.collectAsStateWithLifecycle()
    val placedStickers by viewModel.placedStickers.collectAsStateWithLifecycle()

    // Puzzle state
    val selectedPuzzleTheme by viewModel.selectedPuzzleTheme.collectAsStateWithLifecycle()
    val puzzleSlots by viewModel.puzzleSlots.collectAsStateWithLifecycle()
    val puzzleTrayPieces by viewModel.puzzleTrayPieces.collectAsStateWithLifecycle()
    val selectedPieceId by viewModel.selectedPieceId.collectAsStateWithLifecycle()
    val isPuzzleCompleted by viewModel.isPuzzleCompleted.collectAsStateWithLifecycle()

    // Rocket state
    val rocketX by viewModel.rocketX.collectAsStateWithLifecycle()
    val fallingStars by viewModel.fallingStars.collectAsStateWithLifecycle()
    val rocketScore by viewModel.rocketScore.collectAsStateWithLifecycle()
    val starsCollectedCount by viewModel.starsCollectedCount.collectAsStateWithLifecycle()
    val isRocketActive by viewModel.isRocketActive.collectAsStateWithLifecycle()
    val rocketTimeRemaining by viewModel.rocketTimeRemaining.collectAsStateWithLifecycle()

    val navItems = listOf(
        KidsNavItem("Park", Icons.Default.Home, KidsScreen.Home, "nav_home"),
        KidsNavItem("Balon", Icons.Default.AutoAwesome, KidsScreen.BalloonPop, "nav_balloon"),
        KidsNavItem("Piyano", Icons.Default.MusicNote, KidsScreen.Piano, "nav_piano"),
        KidsNavItem("Yapboz", Icons.Default.Extension, KidsScreen.Puzzle, "nav_puzzle"),
        KidsNavItem("Roket", Icons.Default.RocketLaunch, KidsScreen.Rocket, "nav_rocket"),
        KidsNavItem("Tonton", Icons.Default.Favorite, KidsScreen.PetAndAlbum, "nav_pet")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.targetScreen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.targetScreen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SkyBlue,
                            selectedTextColor = SkyBlue,
                            indicatorColor = SkyBlue.copy(alpha = 0.15f),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is KidsScreen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        profile = profile,
                        recentActivities = recentActivities,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.BalloonPop -> {
                    BalloonPopScreen(
                        viewModel = viewModel,
                        balloons = balloons,
                        score = balloonScore,
                        combo = balloonCombo,
                        timeRemaining = balloonTimeRemaining,
                        isActive = isBalloonActive,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.Piano -> {
                    PianoScreen(
                        viewModel = viewModel,
                        pianoMode = pianoMode,
                        isGuideActive = isGuideActive,
                        guidedStepIndex = guidedStepIndex,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.MemoryMatch -> {
                    MemoryMatchScreen(
                        viewModel = viewModel,
                        cards = memoryCards,
                        difficulty = memoryDifficulty,
                        moves = memoryMoves,
                        pairsFound = memoryPairsFound,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.Drawing -> {
                    DrawingCanvasScreen(
                        viewModel = viewModel,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.Puzzle -> {
                    PuzzleScreen(
                        viewModel = viewModel,
                        selectedTheme = selectedPuzzleTheme,
                        slots = puzzleSlots,
                        trayPieces = puzzleTrayPieces,
                        selectedPieceId = selectedPieceId,
                        isCompleted = isPuzzleCompleted,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.Rocket -> {
                    RocketGameScreen(
                        viewModel = viewModel,
                        rocketX = rocketX,
                        fallingStars = fallingStars,
                        score = rocketScore,
                        starsCollected = starsCollectedCount,
                        timeRemaining = rocketTimeRemaining,
                        isActive = isRocketActive,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
                is KidsScreen.PetAndAlbum -> {
                    PetStickerScreen(
                        viewModel = viewModel,
                        profile = profile,
                        stickers = stickers,
                        petBounceTrigger = petBounceTrigger,
                        petLastActionText = petLastActionText,
                        selectedAccessory = selectedPetAccessory,
                        placedStickers = placedStickers,
                        stars = profile.totalStars,
                        soundEnabled = soundEnabled
                    )
                }
            }
        }
    }

    // Celebration Dialog Popup
    celebration?.let { cel ->
        KidsCelebrationModal(
            title = cel.title,
            message = cel.message,
            starsAwarded = cel.starsAwarded,
            emoji = cel.emoji,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }
}
