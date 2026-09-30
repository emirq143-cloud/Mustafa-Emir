package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChildProfileEntity
import com.example.data.local.RecentActivityEntity
import com.example.data.local.StickerItemEntity
import com.example.data.repository.KidsRepository
import com.example.util.SoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

sealed class KidsScreen {
    object Home : KidsScreen()
    object BalloonPop : KidsScreen()
    object Piano : KidsScreen()
    object MemoryMatch : KidsScreen()
    object Drawing : KidsScreen()
    object Puzzle : KidsScreen()
    object Rocket : KidsScreen()
    object PetAndAlbum : KidsScreen()
}

data class CelebrationInfo(
    val title: String,
    val message: String,
    val starsAwarded: Int,
    val emoji: String
)

// Puzzle Models
data class PuzzleTheme(
    val id: String,
    val name: String,
    val mainEmoji: String,
    val badge: String,
    val pieceEmojis: List<String>, // 4 pieces
    val pieceColors: List<Long>,
    val rewardStars: Int = 15
)

data class PuzzlePiece(
    val id: Int, // 0..3
    val emoji: String,
    val colorHex: Long,
    val isPlaced: Boolean = false
)

data class PuzzleSlot(
    val slotIndex: Int, // 0..3
    val expectedPieceId: Int,
    val placedPieceId: Int? = null,
    val isCorrect: Boolean = false
)

// Rocket Star Collector Models
data class FallingStarItem(
    val id: Long,
    val emoji: String,
    val points: Int,
    val laneIndex: Int = 2,
    val xRatio: Float, // 0.14f to 0.86f
    val yRatio: Float = -0.05f, // 0.0f (top) to 1.1f (bottom)
    val speed: Float = 0.008f,
    val isCaught: Boolean = false,
    val isMissed: Boolean = false,
    val isSuper: Boolean = false
)

data class CatchToastEffect(
    val id: Long,
    val points: Int,
    val emoji: String,
    val xRatio: Float
)

// Balloon Model for Balloon Pop Game
data class BalloonItem(
    val id: Long,
    val emoji: String,
    val colorHex: Long,
    val xRatio: Float, // 0.1f to 0.9f
    val speed: Float,  // vertical speed
    val points: Int,
    val isGolden: Boolean = false,
    val isPopped: Boolean = false
)

// Memory Card Model
data class PlacedSticker(
    val id: Long,
    val emoji: String,
    val title: String,
    val xPercent: Float,
    val yPercent: Float
)

data class MemoryCard(
    val id: Int,
    val pairKey: String,
    val emoji: String,
    val name: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

enum class MemoryDifficulty(val pairsCount: Int, val label: String, val starsReward: Int) {
    EASY(2, "Minik (4 Kart)", 10),
    MEDIUM(4, "Orta (8 Kart)", 20),
    CHAMPION(6, "Şampiyon (12 Kart)", 35)
}

class KidsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KidsRepository

    private val _currentScreen = MutableStateFlow<KidsScreen>(KidsScreen.Home)
    val currentScreen: StateFlow<KidsScreen> = _currentScreen.asStateFlow()

    private val _profile = MutableStateFlow(ChildProfileEntity())
    val profile: StateFlow<ChildProfileEntity> = _profile.asStateFlow()

    private val _stickers = MutableStateFlow<List<StickerItemEntity>>(emptyList())
    val stickers: StateFlow<List<StickerItemEntity>> = _stickers.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<RecentActivityEntity>>(emptyList())
    val recentActivities: StateFlow<List<RecentActivityEntity>> = _recentActivities.asStateFlow()

    private val _celebration = MutableStateFlow<CelebrationInfo?>(null)
    val celebration: StateFlow<CelebrationInfo?> = _celebration.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    // ------------------------------------------------------------------------
    // BALLOON POP GAME STATE
    // ------------------------------------------------------------------------
    private val _balloonScore = MutableStateFlow(0)
    val balloonScore: StateFlow<Int> = _balloonScore.asStateFlow()

    private val _balloonCombo = MutableStateFlow(0)
    val balloonCombo: StateFlow<Int> = _balloonCombo.asStateFlow()

    private val _balloons = MutableStateFlow<List<BalloonItem>>(emptyList())
    val balloons: StateFlow<List<BalloonItem>> = _balloons.asStateFlow()

    private val _balloonTimeRemaining = MutableStateFlow(45)
    val balloonTimeRemaining: StateFlow<Int> = _balloonTimeRemaining.asStateFlow()

    private val _isBalloonGameActive = MutableStateFlow(false)
    val isBalloonGameActive: StateFlow<Boolean> = _isBalloonGameActive.asStateFlow()

    private var balloonLoopJob: Job? = null

    // ------------------------------------------------------------------------
    // PIANO GAME STATE
    // ------------------------------------------------------------------------
    enum class PianoMode { NOTES, ANIMALS }

    private val _pianoMode = MutableStateFlow(PianoMode.NOTES)
    val pianoMode: StateFlow<PianoMode> = _pianoMode.asStateFlow()

    // Song guide: Twinkle Twinkle / Daha Dün Annemizin note indices:
    // C, C, G, G, A, A, G | F, F, E, E, D, D, C (0,0, 4,4, 5,5, 4, 3,3, 2,2, 1,1, 0)
    val nurseryMelodyNotes = listOf(0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0)
    val nurseryMelodyNames = listOf("Do", "Do", "Sol", "Sol", "La", "La", "Sol", "Fa", "Fa", "Mi", "Mi", "Re", "Re", "Do")

    private val _guidedStepIndex = MutableStateFlow(0)
    val guidedStepIndex: StateFlow<Int> = _guidedStepIndex.asStateFlow()

    private val _isGuideActive = MutableStateFlow(false)
    val isGuideActive: StateFlow<Boolean> = _isGuideActive.asStateFlow()

    // ------------------------------------------------------------------------
    // MEMORY MATCH GAME STATE
    // ------------------------------------------------------------------------
    private val _memoryCards = MutableStateFlow<List<MemoryCard>>(emptyList())
    val memoryCards: StateFlow<List<MemoryCard>> = _memoryCards.asStateFlow()

    private val _memoryDifficulty = MutableStateFlow(MemoryDifficulty.EASY)
    val memoryDifficulty: StateFlow<MemoryDifficulty> = _memoryDifficulty.asStateFlow()

    private val _memoryMoves = MutableStateFlow(0)
    val memoryMoves: StateFlow<Int> = _memoryMoves.asStateFlow()

    private val _memoryPairsFound = MutableStateFlow(0)
    val memoryPairsFound: StateFlow<Int> = _memoryPairsFound.asStateFlow()

    private var firstSelectedCardIndex: Int? = null
    private var isEvaluatingCards = false

    // ------------------------------------------------------------------------
    // PET & ALBUM STATE
    // ------------------------------------------------------------------------
    private val _petBounceTrigger = MutableStateFlow(0)
    val petBounceTrigger: StateFlow<Int> = _petBounceTrigger.asStateFlow()

    private val _petLastActionText = MutableStateFlow("Tonton seninle oynamaya hazır! 🥰")
    val petLastActionText: StateFlow<String> = _petLastActionText.asStateFlow()

    private val _selectedPetAccessory = MutableStateFlow<String?>(null) // "🎀", "🕶️", "🎩", "👑"
    val selectedPetAccessory: StateFlow<String?> = _selectedPetAccessory.asStateFlow()

    private val _placedStickers = MutableStateFlow<List<PlacedSticker>>(emptyList())
    val placedStickers: StateFlow<List<PlacedSticker>> = _placedStickers.asStateFlow()

    // ------------------------------------------------------------------------
    // PUZZLE GAME STATE
    // ------------------------------------------------------------------------
    val puzzleThemes = listOf(
        PuzzleTheme(
            id = "dino",
            name = "Sevimli Dinozor",
            mainEmoji = "🦕",
            badge = "Tarih Öncesi",
            pieceEmojis = listOf("🦕", "🌴", "🥚", "🌋"),
            pieceColors = listOf(0xFF4CAF50, 0xFF8BC34A, 0xFFFFB74D, 0xFFFF7043),
            rewardStars = 15
        ),
        PuzzleTheme(
            id = "giraffe",
            name = "Neşeli Zürafa",
            mainEmoji = "🦒",
            badge = "Afrika Savanası",
            pieceEmojis = listOf("🦒", "🌿", "☀️", "🦋"),
            pieceColors = listOf(0xFFFFB300, 0xFF81C784, 0xFFFFD54F, 0xFF4DD0E1),
            rewardStars = 15
        ),
        PuzzleTheme(
            id = "unicorn",
            name = "Sihirli Tekboynuz",
            mainEmoji = "🦄",
            badge = "Masal Diyarı",
            pieceEmojis = listOf("🦄", "🌈", "⭐", "🌸"),
            pieceColors = listOf(0xFFBA68C8, 0xFF64B5F6, 0xFFFFD54F, 0xFFF06292),
            rewardStars = 20
        ),
        PuzzleTheme(
            id = "space",
            name = "Uzay Macerası",
            mainEmoji = "🚀",
            badge = "Galaksi",
            pieceEmojis = listOf("🚀", "🪐", "🛸", "🌟"),
            pieceColors = listOf(0xFFFF5252, 0xFF7C4DFF, 0xFF00E5FF, 0xFFFFD700),
            rewardStars = 20
        ),
        PuzzleTheme(
            id = "panda",
            name = "Tatlı Panda",
            mainEmoji = "🐼",
            badge = "Bambu Ormanı",
            pieceEmojis = listOf("🐼", "🎋", "🍎", "🌺"),
            pieceColors = listOf(0xFF78909C, 0xFF66BB6A, 0xFFEF5350, 0xFFEC407A),
            rewardStars = 15
        ),
        PuzzleTheme(
            id = "sea",
            name = "Deniz Dünyası",
            mainEmoji = "🐬",
            badge = "Mavi Okyanus",
            pieceEmojis = listOf("🐬", "🐠", "🐚", "🐙"),
            pieceColors = listOf(0xFF29B6F6, 0xFFFF7043, 0xFFFFCA28, 0xFFAB47BC),
            rewardStars = 15
        )
    )

    private val _selectedPuzzleTheme = MutableStateFlow(puzzleThemes.first())
    val selectedPuzzleTheme: StateFlow<PuzzleTheme> = _selectedPuzzleTheme.asStateFlow()

    private val _puzzleSlots = MutableStateFlow<List<PuzzleSlot>>(emptyList())
    val puzzleSlots: StateFlow<List<PuzzleSlot>> = _puzzleSlots.asStateFlow()

    private val _puzzleTrayPieces = MutableStateFlow<List<PuzzlePiece>>(emptyList())
    val puzzleTrayPieces: StateFlow<List<PuzzlePiece>> = _puzzleTrayPieces.asStateFlow()

    private val _selectedPieceId = MutableStateFlow<Int?>(null)
    val selectedPieceId: StateFlow<Int?> = _selectedPieceId.asStateFlow()

    private val _isPuzzleCompleted = MutableStateFlow(false)
    val isPuzzleCompleted: StateFlow<Boolean> = _isPuzzleCompleted.asStateFlow()

    // ------------------------------------------------------------------------
    // ROCKET STAR COLLECTOR STATE
    // ------------------------------------------------------------------------
    private val _rocketX = MutableStateFlow(0.5f) // 0.1f .. 0.9f
    val rocketX: StateFlow<Float> = _rocketX.asStateFlow()

    private val _fallingStars = MutableStateFlow<List<FallingStarItem>>(emptyList())
    val fallingStars: StateFlow<List<FallingStarItem>> = _fallingStars.asStateFlow()

    private val _rocketScore = MutableStateFlow(0)
    val rocketScore: StateFlow<Int> = _rocketScore.asStateFlow()

    private val _starsCollectedCount = MutableStateFlow(0)
    val starsCollectedCount: StateFlow<Int> = _starsCollectedCount.asStateFlow()

    private val _isRocketActive = MutableStateFlow(false)
    val isRocketActive: StateFlow<Boolean> = _isRocketActive.asStateFlow()

    private val _rocketTimeRemaining = MutableStateFlow(40)
    val rocketTimeRemaining: StateFlow<Int> = _rocketTimeRemaining.asStateFlow()

    private val _lastCatchEffect = MutableStateFlow<CatchToastEffect?>(null)
    val lastCatchEffect: StateFlow<CatchToastEffect?> = _lastCatchEffect.asStateFlow()

    companion object {
        val ROCKET_LANES = listOf(0.14f, 0.32f, 0.50f, 0.68f, 0.86f)
    }

    private var rocketGameJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = KidsRepository(db.kidsDao())

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }

        viewModelScope.launch {
            repository.profile.collectLatest { p ->
                if (p != null) _profile.value = p
            }
        }

        viewModelScope.launch {
            repository.stickers.collectLatest { s ->
                _stickers.value = s
            }
        }

        viewModelScope.launch {
            repository.recentActivities.collectLatest { a ->
                _recentActivities.value = a
            }
        }

        resetMemoryGame()
        initPuzzle(puzzleThemes.first())
    }

    // ------------------------------------------------------------------------
    // NAVIGATION & GENERAL
    // ------------------------------------------------------------------------
    fun navigateTo(screen: KidsScreen) {
        SoundPlayer.playTap()
        _currentScreen.value = screen
        if (screen == KidsScreen.BalloonPop && !_isBalloonGameActive.value) {
            startBalloonGame()
        } else if (screen != KidsScreen.BalloonPop) {
            stopBalloonGame()
        }

        if (screen == KidsScreen.Rocket && !_isRocketActive.value) {
            startRocketGame()
        } else if (screen != KidsScreen.Rocket) {
            stopRocketGame()
        }
    }

    fun toggleSound() {
        val newState = !_soundEnabled.value
        _soundEnabled.value = newState
        SoundPlayer.isMuted = !newState
    }

    fun dismissCelebration() {
        _celebration.value = null
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val awarded = repository.claimDailyBonus()
            if (awarded > 0) {
                SoundPlayer.playCheer()
                _celebration.value = CelebrationInfo(
                    title = "Günün Hediyesi!",
                    message = "Tebrikler! Bugünün 25 Parlak Yıldızını kazandın! ⭐",
                    starsAwarded = awarded,
                    emoji = "🌟"
                )
            } else {
                SoundPlayer.playSparkle()
                _celebration.value = CelebrationInfo(
                    title = "Yıldızların Zaten Toplandı!",
                    message = "Bugünkü günlük yıldızını aldın. Yarın tekrar gel!",
                    starsAwarded = 0,
                    emoji = "☀️"
                )
            }
        }
    }

    // ------------------------------------------------------------------------
    // BALLOON POPPING GAME
    // ------------------------------------------------------------------------
    fun startBalloonGame() {
        _balloonScore.value = 0
        _balloonCombo.value = 0
        _balloonTimeRemaining.value = 45
        _isBalloonGameActive.value = true
        _balloons.value = emptyList()

        balloonLoopJob?.cancel()
        balloonLoopJob = viewModelScope.launch {
            var balloonIdGen = 1L
            val balloonColors = listOf(
                0xFFFF6B6B, 0xFFFFD166, 0xFF4D96FF, 0xFF6BCB77, 0xFFFF85A1, 0xFF9D4EDD, 0xFFFF9F1C
            )
            val balloonEmojis = listOf("🎈", "🐻", "🐱", "🐰", "⭐", "🚀", "🦄", "🌈")

            // 4 distinct horizontal lanes so balloons never overlap horizontally
            val lanes = listOf(0.10f, 0.36f, 0.62f, 0.86f)
            var lastLaneIndex = -1

            var secondCounter = 0
            var spawnTimer = 1100 // Spawn first balloon immediately

            while (_isBalloonGameActive.value && _balloonTimeRemaining.value > 0) {
                delay(100)
                secondCounter += 100
                spawnTimer += 100

                // 1 second countdown
                if (secondCounter >= 1000) {
                    secondCounter = 0
                    _balloonTimeRemaining.value -= 1
                    if (_balloonTimeRemaining.value <= 0) break
                }

                // Smooth, non-cluttered spawn: exactly 1 balloon at a time, max 4 active on screen
                val activeUnpopped = _balloons.value.filter { !it.isPopped }
                if (spawnTimer >= 1200 && activeUnpopped.size < 4) {
                    spawnTimer = 0

                    val availableLanes = lanes.indices.filter { it != lastLaneIndex }
                    val chosenLane = availableLanes.random()
                    lastLaneIndex = chosenLane
                    val xRatio = lanes[chosenLane]

                    val isGolden = Random.nextInt(7) == 0
                    val emoji = if (isGolden) "👑" else balloonEmojis.random()
                    val color = if (isGolden) 0xFFFFD700 else balloonColors.random()
                    val points = if (isGolden) 50 else 10
                    val speed = 1.0f

                    val newBalloon = BalloonItem(
                        id = balloonIdGen++,
                        emoji = emoji,
                        colorHex = color,
                        xRatio = xRatio,
                        speed = speed,
                        points = points,
                        isGolden = isGolden
                    )

                    _balloons.value = activeUnpopped + newBalloon
                }
            }

            // Game over
            _isBalloonGameActive.value = false
            val finalScore = _balloonScore.value
            val earnedStars = maxOf(5, finalScore / 15)

            repository.recordBalloonGame(finalScore, earnedStars)
            repository.reducePetHungerAfterGame()
            SoundPlayer.playCheer()
            _celebration.value = CelebrationInfo(
                title = "Oyun Bitti! Harikasın!",
                message = "Toplanan Puan: $finalScore\nKazandığın Yıldız: +$earnedStars ⭐",
                starsAwarded = earnedStars,
                emoji = "🎈"
            )
        }
    }

    fun stopBalloonGame() {
        _isBalloonGameActive.value = false
        balloonLoopJob?.cancel()
    }

    fun popBalloon(balloonId: Long) {
        val currentList = _balloons.value
        val balloon = currentList.find { it.id == balloonId && !it.isPopped } ?: return

        SoundPlayer.playPop()
        _balloonCombo.value += 1
        val comboMultiplier = if (_balloonCombo.value >= 5) 2 else 1
        _balloonScore.value += balloon.points * comboMultiplier

        _balloons.value = currentList.map {
            if (it.id == balloonId) it.copy(isPopped = true) else it
        }
    }

    fun removeBalloon(balloonId: Long) {
        _balloons.value = _balloons.value.filterNot { it.id == balloonId }
    }

    // ------------------------------------------------------------------------
    // PIANO & ANIMAL CHORUS
    // ------------------------------------------------------------------------
    fun setPianoMode(mode: PianoMode) {
        _pianoMode.value = mode
        SoundPlayer.playTap()
    }

    fun toggleSongGuide() {
        SoundPlayer.playSparkle()
        _isGuideActive.value = !_isGuideActive.value
        _guidedStepIndex.value = 0
    }

    fun playPianoKey(keyIndex: Int) {
        if (_pianoMode.value == PianoMode.NOTES) {
            SoundPlayer.playPianoNote(keyIndex)
        } else {
            val animalNames = listOf("DOG", "CAT", "DUCK", "COW", "FROG", "LION", "SHEEP", "CAT")
            val animal = animalNames.getOrElse(keyIndex) { "DOG" }
            SoundPlayer.playAnimalSound(animal)
        }

        // Check if matching song guide
        if (_isGuideActive.value) {
            val expectedKey = nurseryMelodyNotes.getOrNull(_guidedStepIndex.value)
            if (expectedKey == keyIndex) {
                val nextStep = _guidedStepIndex.value + 1
                if (nextStep >= nurseryMelodyNotes.size) {
                    // Song completed!
                    _guidedStepIndex.value = 0
                    viewModelScope.launch {
                        SoundPlayer.playCheer()
                        val stars = 15
                        repository.addStars(stars, "Neşeli Şarkı Başarıyla Çalındı! 🎶", "🎵")
                        _celebration.value = CelebrationInfo(
                            title = "Müzisyen Yıldız!",
                            message = "Harika çaldın! Şarkıyı tamamladın ve 15 Yıldız kazandın! 🎵",
                            starsAwarded = stars,
                            emoji = "🎹"
                        )
                    }
                } else {
                    _guidedStepIndex.value = nextStep
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // MEMORY MATCH GAME
    // ------------------------------------------------------------------------
    fun setMemoryDifficulty(diff: MemoryDifficulty) {
        _memoryDifficulty.value = diff
        resetMemoryGame()
    }

    fun resetMemoryGame() {
        val diff = _memoryDifficulty.value
        _memoryMoves.value = 0
        _memoryPairsFound.value = 0
        firstSelectedCardIndex = null
        isEvaluatingCards = false

        val animalsPool = listOf(
            Pair("lion", "🦁"),
            Pair("panda", "🐼"),
            Pair("rabbit", "🐰"),
            Pair("fox", "🦊"),
            Pair("dolphin", "🐬"),
            Pair("monkey", "🐵"),
            Pair("koala", "🐨"),
            Pair("cat", "🐱")
        ).shuffled().take(diff.pairsCount)

        val cards = mutableListOf<MemoryCard>()
        var cardId = 1
        for (item in animalsPool) {
            cards.add(MemoryCard(id = cardId++, pairKey = item.first, emoji = item.second, name = item.first))
            cards.add(MemoryCard(id = cardId++, pairKey = item.first, emoji = item.second, name = item.first))
        }
        _memoryCards.value = cards.shuffled()
    }

    fun onCardClicked(cardIndex: Int) {
        if (isEvaluatingCards) return
        val currentCards = _memoryCards.value
        val card = currentCards.getOrNull(cardIndex) ?: return

        if (card.isFaceUp || card.isMatched) return

        SoundPlayer.playTap()

        // Flip the card face up
        _memoryCards.value = currentCards.mapIndexed { i, c ->
            if (i == cardIndex) c.copy(isFaceUp = true) else c
        }

        val firstIndex = firstSelectedCardIndex
        if (firstIndex == null) {
            firstSelectedCardIndex = cardIndex
        } else {
            // Second card chosen
            _memoryMoves.value += 1
            val firstCard = currentCards[firstIndex]
            isEvaluatingCards = true

            if (firstCard.pairKey == card.pairKey) {
                // Match!
                viewModelScope.launch {
                    delay(300)
                    SoundPlayer.playMatchCard()
                    _memoryCards.value = _memoryCards.value.map {
                        if (it.pairKey == card.pairKey) it.copy(isMatched = true) else it
                    }
                    val newPairs = _memoryPairsFound.value + 1
                    _memoryPairsFound.value = newPairs
                    firstSelectedCardIndex = null
                    isEvaluatingCards = false

                    if (newPairs >= _memoryDifficulty.value.pairsCount) {
                        // Game Won!
                        delay(400)
                        val reward = _memoryDifficulty.value.starsReward
                        repository.recordMemoryWin(_memoryMoves.value, reward)
                        repository.reducePetHungerAfterGame()
                        SoundPlayer.playCheer()
                        _celebration.value = CelebrationInfo(
                            title = "Tebrikler Zeki Dostum!",
                            message = "Bütün çiftleri ${_memoryMoves.value} hamlede buldun!\n+$reward Parlak Yıldız Kazandın! ⭐",
                            starsAwarded = reward,
                            emoji = "🏆"
                        )
                    }
                }
            } else {
                // Not match -> flip back
                viewModelScope.launch {
                    delay(800)
                    _memoryCards.value = _memoryCards.value.mapIndexed { i, c ->
                        if (i == firstIndex || i == cardIndex) c.copy(isFaceUp = false) else c
                    }
                    firstSelectedCardIndex = null
                    isEvaluatingCards = false
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // DRAWING CANVAS
    // ------------------------------------------------------------------------
    fun saveDrawingMasterpiece() {
        viewModelScope.launch {
            SoundPlayer.playCheer()
            val reward = 10
            repository.recordDrawingSaved(reward)
            _celebration.value = CelebrationInfo(
                title = "Harika Bir Sanat Eseri!",
                message = "Resmin sihirli panoya asıldı! +$reward Yıldız kazandın! 🎨✨",
                starsAwarded = reward,
                emoji = "🎨"
            )
        }
    }

    // ------------------------------------------------------------------------
    // PET & STICKER ROOM
    // ------------------------------------------------------------------------
    fun petMascot() {
        viewModelScope.launch {
            _petBounceTrigger.value += 1
            val currentLove = _profile.value.petLove
            SoundPlayer.playPetGiggle()
            if (currentLove >= 100) {
                _petLastActionText.value = "Tonton sana sarıldı! 'Seni çok seviyorum!' 🥰 (Sevgi %100)"
            } else {
                val (stars, awarded) = repository.petMascot()
                if (awarded) {
                    val newLove = (_profile.value.petLove + 10).coerceAtMost(100)
                    _petLastActionText.value = "Tonton kıkır kıkır güldü! Gıdı gıdı! 🥰 (+1 ⭐, Sevgi %$newLove)"
                } else {
                    _petLastActionText.value = "Tonton sana sıcacık sarıldı! 💕"
                }
            }
        }
    }

    fun feedPet(foodEmoji: String) {
        viewModelScope.launch {
            val currentHunger = _profile.value.petHunger
            if (currentHunger >= 100) {
                SoundPlayer.playPop()
                _petLastActionText.value = "Tonton: 'Çok doydum, göbüşüm doldu! Teşekkürler!' 😋 (Tokluk %100)"
            } else {
                SoundPlayer.playSparkle()
                _petBounceTrigger.value += 1
                val (stars, awarded) = repository.feedPet(foodEmoji)
                if (awarded) {
                    val newHunger = (_profile.value.petHunger + 25).coerceAtMost(100)
                    _petLastActionText.value = "Tonton $foodEmoji yedi! 'Nefis!' (+3 ⭐, Tokluk %$newHunger) 😋"
                } else {
                    _petLastActionText.value = "Tonton çok tok! Hadi biraz oyun oynayalım! 🎈"
                }
            }
        }
    }

    fun selectPetAccessory(accessoryEmoji: String?) {
        SoundPlayer.playSparkle()
        _selectedPetAccessory.value = if (_selectedPetAccessory.value == accessoryEmoji) null else accessoryEmoji
        _petBounceTrigger.value += 1
        _petLastActionText.value = if (_selectedPetAccessory.value != null) {
            "Tonton yeni aksesuarını taktı: ${_selectedPetAccessory.value}! Çok havalı! ✨"
        } else {
            "Tonton şapkasını çıkardı. Rahatladı! 🐻"
        }
    }

    fun placeStickerOnScene(sticker: StickerItemEntity) {
        if (!sticker.isUnlocked) return
        SoundPlayer.playSparkle()
        val randomX = (15..80).random() / 100f
        val randomY = (20..75).random() / 100f
        val newItem = PlacedSticker(
            id = System.currentTimeMillis() + (0..999).random(),
            emoji = sticker.emoji,
            title = sticker.title,
            xPercent = randomX,
            yPercent = randomY
        )
        _placedStickers.value = (_placedStickers.value + newItem).takeLast(8)
        _petLastActionText.value = "${sticker.title} çıkartması sahneye yerleştirildi! 🎨"
    }

    fun removePlacedSticker(stickerId: Long) {
        SoundPlayer.playPop()
        _placedStickers.value = _placedStickers.value.filterNot { it.id == stickerId }
    }

    fun clearPlacedStickers() {
        SoundPlayer.playWhoosh()
        _placedStickers.value = emptyList()
        _petLastActionText.value = "Sihirli sahne temizlendi. Yeni çıkartmalar ekleyebilirsin! ✨"
    }

    fun unlockSticker(sticker: StickerItemEntity) {
        if (sticker.isUnlocked) return
        viewModelScope.launch {
            if (_profile.value.totalStars >= sticker.starCost) {
                val success = repository.unlockSticker(sticker.id, sticker.starCost)
                if (success) {
                    SoundPlayer.playCheer()
                    _celebration.value = CelebrationInfo(
                        title = "Yeni Çıkartma Açıldı!",
                        message = "${sticker.title} çıkartması albümüne eklendi! 🌟",
                        starsAwarded = 0,
                        emoji = sticker.emoji
                    )
                }
            } else {
                SoundPlayer.playPop()
                _petLastActionText.value = "Bu çıkartma için daha fazla yıldıza ihtiyacın var! ⭐"
            }
        }
    }

    // ------------------------------------------------------------------------
    // PUZZLE GAME LOGIC
    // ------------------------------------------------------------------------
    fun initPuzzle(theme: PuzzleTheme) {
        _selectedPuzzleTheme.value = theme
        _isPuzzleCompleted.value = false
        _selectedPieceId.value = null

        _puzzleSlots.value = (0..3).map { index ->
            PuzzleSlot(
                slotIndex = index,
                expectedPieceId = index,
                placedPieceId = null,
                isCorrect = false
            )
        }

        _puzzleTrayPieces.value = (0..3).map { index ->
            PuzzlePiece(
                id = index,
                emoji = theme.pieceEmojis[index],
                colorHex = theme.pieceColors[index],
                isPlaced = false
            )
        }.shuffled()
    }

    fun selectPuzzleTheme(theme: PuzzleTheme) {
        SoundPlayer.playTap()
        initPuzzle(theme)
    }

    fun selectTrayPiece(pieceId: Int) {
        SoundPlayer.playTap()
        if (_selectedPieceId.value == pieceId) {
            _selectedPieceId.value = null
        } else {
            _selectedPieceId.value = pieceId
        }
    }

    fun onSlotClicked(slotIndex: Int) {
        val selectedId = _selectedPieceId.value
        if (selectedId != null) {
            placePiece(selectedId, slotIndex)
        } else {
            val slot = _puzzleSlots.value.find { it.slotIndex == slotIndex }
            if (slot?.placedPieceId != null && !slot.isCorrect) {
                SoundPlayer.playPop()
                val unplacedId = slot.placedPieceId
                _puzzleSlots.value = _puzzleSlots.value.map {
                    if (it.slotIndex == slotIndex) it.copy(placedPieceId = null, isCorrect = false) else it
                }
                _puzzleTrayPieces.value = _puzzleTrayPieces.value.map {
                    if (it.id == unplacedId) it.copy(isPlaced = false) else it
                }
            }
        }
    }

    fun autoSnapPiece(pieceId: Int) {
        val targetSlot = _puzzleSlots.value.find { it.expectedPieceId == pieceId && !it.isCorrect }
            ?: _puzzleSlots.value.find { it.placedPieceId == null }

        if (targetSlot != null) {
            placePiece(pieceId, targetSlot.slotIndex)
        }
    }

    private fun placePiece(pieceId: Int, slotIndex: Int) {
        val slot = _puzzleSlots.value.find { it.slotIndex == slotIndex } ?: return
        val piece = _puzzleTrayPieces.value.find { it.id == pieceId } ?: return
        val isCorrect = (pieceId == slot.expectedPieceId)

        if (isCorrect) {
            SoundPlayer.playSnap()
        } else {
            SoundPlayer.playTap()
        }

        val previousPieceId = slot.placedPieceId

        _puzzleSlots.value = _puzzleSlots.value.map {
            if (it.slotIndex == slotIndex) {
                it.copy(placedPieceId = pieceId, isCorrect = isCorrect)
            } else {
                it
            }
        }

        _puzzleTrayPieces.value = _puzzleTrayPieces.value.map {
            when (it.id) {
                pieceId -> it.copy(isPlaced = true)
                previousPieceId -> it.copy(isPlaced = false)
                else -> it
            }
        }

        _selectedPieceId.value = null

        val allSolved = _puzzleSlots.value.all { it.isCorrect }
        if (allSolved && !_isPuzzleCompleted.value) {
            _isPuzzleCompleted.value = true
            val theme = _selectedPuzzleTheme.value
            viewModelScope.launch {
                delay(300)
                SoundPlayer.playCheer()
                repository.recordPuzzleSolved(theme.name, theme.rewardStars)
                repository.reducePetHungerAfterGame()
                _celebration.value = CelebrationInfo(
                    title = "Harikasın! Yapboz Bitti!",
                    message = "${theme.name} yapbozunu ustalıkla tamamladın! +${theme.rewardStars} Parlak Yıldız kazandın! 🧩🌟",
                    starsAwarded = theme.rewardStars,
                    emoji = theme.mainEmoji
                )
            }
        }
    }

    fun magicHelpPuzzle() {
        val unsolvedSlot = _puzzleSlots.value.find { !it.isCorrect } ?: return
        val correctPiece = _puzzleTrayPieces.value.find { it.id == unsolvedSlot.expectedPieceId } ?: return
        SoundPlayer.playSparkle()
        placePiece(correctPiece.id, unsolvedSlot.slotIndex)
    }

    fun resetCurrentPuzzle() {
        SoundPlayer.playTap()
        initPuzzle(_selectedPuzzleTheme.value)
    }

    // ------------------------------------------------------------------------
    // ROCKET STAR COLLECTOR GAME LOGIC
    // ------------------------------------------------------------------------
    fun setRocketPosition(xRatio: Float) {
        _rocketX.value = xRatio.coerceIn(0.12f, 0.88f)
    }

    fun moveRocketBy(deltaRatio: Float) {
        val current = _rocketX.value
        val newTarget = if (deltaRatio > 0) {
            // Next lane to the right
            ROCKET_LANES.firstOrNull { it > current + 0.05f } ?: ROCKET_LANES.last()
        } else {
            // Next lane to the left
            ROCKET_LANES.lastOrNull { it < current - 0.05f } ?: ROCKET_LANES.first()
        }
        _rocketX.value = newTarget
    }

    fun startRocketGame() {
        _isRocketActive.value = true
        _rocketScore.value = 0
        _starsCollectedCount.value = 0
        _rocketTimeRemaining.value = 40
        _fallingStars.value = emptyList()
        _rocketX.value = 0.50f
        _lastCatchEffect.value = null

        rocketGameJob?.cancel()
        rocketGameJob = viewModelScope.launch {
            val starEmojis = listOf(
                Pair("⭐", 5),
                Pair("⭐", 5),
                Pair("🌟", 10),
                Pair("🍓", 5),
                Pair("🍦", 10),
                Pair("💎", 20),
                Pair("🌈", 15)
            )

            var timerCounter = 0
            var spawnCooldown = 1000 // Quick initial star
            var lastLaneIndex = -1
            var catchCooldownTicks = 0

            while (_isRocketActive.value && _rocketTimeRemaining.value > 0) {
                delay(25) // ~40 FPS smooth game tick
                timerCounter += 25

                // 1. One second countdown
                if (timerCounter >= 1000) {
                    timerCounter = 0
                    val remaining = _rocketTimeRemaining.value - 1
                    _rocketTimeRemaining.value = remaining
                    if (remaining <= 0) {
                        endRocketGame()
                        break
                    }
                }

                if (catchCooldownTicks > 0) {
                    catchCooldownTicks--
                }

                val curRocketX = _rocketX.value
                val currentList = _fallingStars.value
                val nextList = mutableListOf<FallingStarItem>()
                var newlyCaughtStar: FallingStarItem? = null

                // 2. Advance falling positions & precise single-star collision check
                for (item in currentList) {
                    if (item.isCaught) {
                        // Caught star briefly travels slightly with rocket before disappearing
                        val newY = item.yRatio + 0.003f
                        if (newY < 0.88f) {
                            nextList.add(item.copy(yRatio = newY))
                        }
                        continue
                    }

                    val newY = item.yRatio + item.speed

                    // CRITICAL: If the star has fallen past the rocket nose (altitude > 0.83f)
                    // It is immediately marked as MISSED. It can NEVER be caught from behind!
                    if (newY > 0.83f) {
                        if (newY < 1.05f) {
                            nextList.add(item.copy(yRatio = newY, isMissed = true))
                        }
                        // Off-screen items (>= 1.05f) are dropped naturally
                        continue
                    }

                    // Precise nose cone collision check:
                    // Must hit the rocket nose (0.76f .. 0.83f) with direct horizontal alignment (<= 0.075f)
                    val inCatchAltitude = newY in 0.76f..0.83f
                    val horizontalDist = abs(item.xRatio - curRocketX)
                    val isDirectHit = horizontalDist <= 0.075f

                    if (inCatchAltitude && isDirectHit && !item.isMissed && catchCooldownTicks == 0 && newlyCaughtStar == null) {
                        // Catch ONLY this single star!
                        val caught = item.copy(yRatio = newY, isCaught = true)
                        newlyCaughtStar = caught
                        nextList.add(caught)
                    } else {
                        nextList.add(item.copy(yRatio = newY))
                    }
                }

                // If a star was caught this tick:
                if (newlyCaughtStar != null) {
                    catchCooldownTicks = 8 // 200ms lock: prevents vacuuming multiple items at once
                    SoundPlayer.playSparkle()
                    _rocketScore.value += newlyCaughtStar.points
                    _starsCollectedCount.value += 1
                    _lastCatchEffect.value = CatchToastEffect(
                        id = System.currentTimeMillis(),
                        points = newlyCaughtStar.points,
                        emoji = newlyCaughtStar.emoji,
                        xRatio = newlyCaughtStar.xRatio
                    )
                }

                // 3. Spawning: exactly ONE item at a time, spaced generously
                spawnCooldown += 25
                val activeStars = nextList.filter { !it.isCaught && !it.isMissed }
                val hasStarAboveHalf = activeStars.any { it.yRatio < 0.50f }

                // Only spawn if no star is in top half of screen, total active on screen < 2, and spawn interval met
                if (!hasStarAboveHalf && activeStars.size < 2 && spawnCooldown >= 1400) {
                    spawnCooldown = 0
                    var chosenLane = Random.nextInt(ROCKET_LANES.size)
                    if (chosenLane == lastLaneIndex) {
                        chosenLane = (chosenLane + 1 + Random.nextInt(ROCKET_LANES.size - 1)) % ROCKET_LANES.size
                    }
                    lastLaneIndex = chosenLane

                    val pick = starEmojis.random()
                    val newItem = FallingStarItem(
                        id = System.currentTimeMillis() + Random.nextLong(1000),
                        emoji = pick.first,
                        points = pick.second,
                        laneIndex = chosenLane,
                        xRatio = ROCKET_LANES[chosenLane],
                        yRatio = -0.05f,
                        speed = 0.0075f + Random.nextFloat() * 0.0025f,
                        isSuper = (pick.first == "🌟" || pick.first == "💎" || pick.first == "🌈")
                    )
                    nextList.add(newItem)
                }

                _fallingStars.value = nextList
            }
        }
    }

    fun catchStarItem(starId: Long) {
        val star = _fallingStars.value.find { it.id == starId && !it.isCaught && !it.isMissed } ?: return
        SoundPlayer.playSparkle()
        _rocketScore.value += star.points
        _starsCollectedCount.value += 1
        _fallingStars.value = _fallingStars.value.map {
            if (it.id == starId) it.copy(isCaught = true) else it
        }
    }

    fun missStarItem(starId: Long) {
        _fallingStars.value = _fallingStars.value.filterNot { it.id == starId }
    }

    fun clearLastCatchEffect() {
        _lastCatchEffect.value = null
    }

    fun stopRocketGame() {
        _isRocketActive.value = false
        rocketGameJob?.cancel()
    }

    private fun endRocketGame() {
        stopRocketGame()
        val score = _rocketScore.value
        val collected = _starsCollectedCount.value
        val starsReward = maxOf(8, score / 6)

        viewModelScope.launch {
            SoundPlayer.playCheer()
            repository.recordRocketAdventure(collected, starsReward)
            repository.reducePetHungerAfterGame()
            _celebration.value = CelebrationInfo(
                title = "Harika Uçuş!",
                message = "Roketinle gökyüzünde tam $collected ödül topladın! Skorun: $score! +$starsReward Parlak Yıldız kazandın! 🚀⭐",
                starsAwarded = starsReward,
                emoji = "🚀"
            )
        }
    }
}
