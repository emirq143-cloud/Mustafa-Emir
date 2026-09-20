package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalStars: Int = 30, // Starting gift stars
    val balloonHighScore: Int = 0,
    val memoryGamesWon: Int = 0,
    val drawingsCount: Int = 0,
    val petHunger: Int = 75,
    val petLove: Int = 80,
    val mascotName: String = "Tonton",
    val selectedAvatar: String = "🐻",
    val lastDailyBonusDay: Long = 0L
)

@Entity(tableName = "stickers")
data class StickerItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val emoji: String,
    val category: String, // "Sevimli Hayvanlar", "Sihirli Doğa", "Uzay & Eğlence"
    val starCost: Int,
    val isUnlocked: Boolean = false
)

@Entity(tableName = "recent_activities")
data class RecentActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val starsEarned: Int,
    val iconEmoji: String,
    val timestamp: Long = System.currentTimeMillis()
)
