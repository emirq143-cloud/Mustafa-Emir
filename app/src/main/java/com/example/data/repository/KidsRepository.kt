package com.example.data.repository

import com.example.data.local.ChildProfileEntity
import com.example.data.local.KidsDao
import com.example.data.local.RecentActivityEntity
import com.example.data.local.StickerItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class KidsRepository(private val kidsDao: KidsDao) {

    val profile: Flow<ChildProfileEntity?> = kidsDao.getProfile()
    val stickers: Flow<List<StickerItemEntity>> = kidsDao.getAllStickers()
    val recentActivities: Flow<List<RecentActivityEntity>> = kidsDao.getRecentActivities()

    suspend fun initializeDefaultDataIfNeeded() {
        val existingProfile = kidsDao.getProfileSync()
        if (existingProfile == null) {
            kidsDao.insertOrUpdateProfile(
                ChildProfileEntity(
                    id = 1,
                    totalStars = 40, // Welcome gift
                    balloonHighScore = 0,
                    memoryGamesWon = 0,
                    drawingsCount = 0,
                    petHunger = 80,
                    petLove = 85,
                    mascotName = "Tonton",
                    selectedAvatar = "🐻"
                )
            )

            kidsDao.insertActivity(
                RecentActivityEntity(
                    title = "Oyun Parkına Hoş Geldin!",
                    starsEarned = 40,
                    iconEmoji = "🎉"
                )
            )
        }

        if (kidsDao.getStickersCount() == 0) {
            val initialStickers = listOf(
                StickerItemEntity("star_1", "Parlak Yıldız", "⭐", "Sihirli Doğa", 0, isUnlocked = true),
                StickerItemEntity("balloon_1", "Uçan Balon", "🎈", "Uzay & Eğlence", 0, isUnlocked = true),
                StickerItemEntity("bear_1", "Sevimli Ayıcık", "🧸", "Sevimli Hayvanlar", 0, isUnlocked = true),
                StickerItemEntity("rainbow_1", "Neşeli Gökkuşağı", "🌈", "Sihirli Doğa", 25, isUnlocked = false),
                StickerItemEntity("rocket_1", "Uzay Roketi", "🚀", "Uzay & Eğlence", 40, isUnlocked = false),
                StickerItemEntity("unicorn_1", "Sihirli Tekboynuz", "🦄", "Sihirli Doğa", 50, isUnlocked = false),
                StickerItemEntity("panda_1", "Tatlı Panda", "🐼", "Sevimli Hayvanlar", 65, isUnlocked = false),
                StickerItemEntity("icecream_1", "Renkli Dondurma", "🍦", "Uzay & Eğlence", 80, isUnlocked = false),
                StickerItemEntity("crown_1", "Kral Tacı", "👑", "Sihirli Doğa", 100, isUnlocked = false),
                StickerItemEntity("dolphin_1", "Güler Yüzlü Yunus", "🐬", "Sevimli Hayvanlar", 120, isUnlocked = false),
                StickerItemEntity("dino_1", "Minik Dino", "🦕", "Sevimli Hayvanlar", 150, isUnlocked = false),
                StickerItemEntity("wand_1", "Yıldızlı Asa", "🪄", "Sihirli Doğa", 200, isUnlocked = false)
            )
            kidsDao.insertStickers(initialStickers)
        }
    }

    suspend fun addStars(amount: Int, reason: String, iconEmoji: String): Boolean {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val newStars = currentProfile.totalStars + amount
        kidsDao.insertOrUpdateProfile(currentProfile.copy(totalStars = newStars))
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = reason,
                starsEarned = amount,
                iconEmoji = iconEmoji
            )
        )
        return true
    }

    suspend fun unlockSticker(stickerId: String, cost: Int): Boolean {
        val currentProfile = kidsDao.getProfileSync() ?: return false
        if (currentProfile.totalStars < cost) return false

        val updatedProfile = currentProfile.copy(
            totalStars = currentProfile.totalStars - cost
        )
        kidsDao.insertOrUpdateProfile(updatedProfile)
        kidsDao.unlockSticker(stickerId)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Yeni Çıkartma Açıldı!",
                starsEarned = -cost,
                iconEmoji = "🎁"
            )
        )
        return true
    }

    suspend fun feedPet(foodEmoji: String): Pair<Int, Boolean> {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        if (currentProfile.petHunger >= 100) {
            return Pair(0, false)
        }
        val newHunger = (currentProfile.petHunger + 25).coerceAtMost(100)
        val newLove = (currentProfile.petLove + 8).coerceAtMost(100)
        val starsBonus = 3
        val updated = currentProfile.copy(
            petHunger = newHunger,
            petLove = newLove,
            totalStars = currentProfile.totalStars + starsBonus
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Tonton $foodEmoji yedi! (Tokluk: %$newHunger)",
                starsEarned = starsBonus,
                iconEmoji = foodEmoji
            )
        )
        return Pair(starsBonus, true)
    }

    suspend fun petMascot(): Pair<Int, Boolean> {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        if (currentProfile.petLove >= 100) {
            return Pair(0, false)
        }
        val newLove = (currentProfile.petLove + 10).coerceAtMost(100)
        val starsBonus = 1
        val updated = currentProfile.copy(
            petLove = newLove,
            totalStars = currentProfile.totalStars + starsBonus
        )
        kidsDao.insertOrUpdateProfile(updated)
        return Pair(starsBonus, true)
    }

    suspend fun reducePetHungerAfterGame() {
        val currentProfile = kidsDao.getProfileSync() ?: return
        val newHunger = (currentProfile.petHunger - 15).coerceAtLeast(40)
        kidsDao.insertOrUpdateProfile(currentProfile.copy(petHunger = newHunger))
    }

    suspend fun recordBalloonGame(score: Int, starsEarned: Int) {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val newHighScore = maxOf(currentProfile.balloonHighScore, score)
        val updated = currentProfile.copy(
            balloonHighScore = newHighScore,
            totalStars = currentProfile.totalStars + starsEarned
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Balon Patlatmaca: $score Puan!",
                starsEarned = starsEarned,
                iconEmoji = "🎈"
            )
        )
    }

    suspend fun recordMemoryWin(moves: Int, starsEarned: Int) {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val updated = currentProfile.copy(
            memoryGamesWon = currentProfile.memoryGamesWon + 1,
            totalStars = currentProfile.totalStars + starsEarned
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Hafıza Kartı Oyunu Kazanıldı!",
                starsEarned = starsEarned,
                iconEmoji = "🃏"
            )
        )
    }

    suspend fun recordDrawingSaved(starsEarned: Int) {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val updated = currentProfile.copy(
            drawingsCount = currentProfile.drawingsCount + 1,
            totalStars = currentProfile.totalStars + starsEarned
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Sihirli Resim Çizildi!",
                starsEarned = starsEarned,
                iconEmoji = "🎨"
            )
        )
    }

    suspend fun recordPuzzleSolved(puzzleName: String, starsEarned: Int) {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val updated = currentProfile.copy(
            totalStars = currentProfile.totalStars + starsEarned
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "$puzzleName Yapbozu Tamamlandı!",
                starsEarned = starsEarned,
                iconEmoji = "🧩"
            )
        )
    }

    suspend fun recordRocketAdventure(starsCollected: Int, starsEarned: Int) {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val updated = currentProfile.copy(
            totalStars = currentProfile.totalStars + starsEarned
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Roket ile $starsCollected Yıldız Toplandı!",
                starsEarned = starsEarned,
                iconEmoji = "🚀"
            )
        )
    }

    suspend fun claimDailyBonus(): Int {
        val currentProfile = kidsDao.getProfileSync() ?: ChildProfileEntity()
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toLong()
        if (currentProfile.lastDailyBonusDay == currentDay) {
            return 0 // Already claimed today
        }
        val bonus = 25
        val updated = currentProfile.copy(
            lastDailyBonusDay = currentDay,
            totalStars = currentProfile.totalStars + bonus
        )
        kidsDao.insertOrUpdateProfile(updated)
        kidsDao.insertActivity(
            RecentActivityEntity(
                title = "Günün Yıldız Hediyesi!",
                starsEarned = bonus,
                iconEmoji = "⭐"
            )
        )
        return bonus
    }
}
