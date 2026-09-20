package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KidsDao {

    @Query("SELECT * FROM child_profile WHERE id = 1")
    fun getProfile(): Flow<ChildProfileEntity?>

    @Query("SELECT * FROM child_profile WHERE id = 1")
    suspend fun getProfileSync(): ChildProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ChildProfileEntity)

    @Query("SELECT * FROM stickers ORDER BY isUnlocked DESC, starCost ASC")
    fun getAllStickers(): Flow<List<StickerItemEntity>>

    @Query("SELECT COUNT(*) FROM stickers")
    suspend fun getStickersCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStickers(stickers: List<StickerItemEntity>)

    @Query("UPDATE stickers SET isUnlocked = 1 WHERE id = :id")
    suspend fun unlockSticker(id: String)

    @Query("SELECT * FROM recent_activities ORDER BY timestamp DESC LIMIT 10")
    fun getRecentActivities(): Flow<List<RecentActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: RecentActivityEntity)
}
