package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tense_scores")
data class TenseScore(
    @PrimaryKey val tenseId: String,
    val tenseName: String,
    val correctCount: Int = 0,
    val attemptedCount: Int = 0
)

@Entity(tableName = "user_stats")
data class UserStatsRecord(
    @PrimaryKey val id: Int = 1,
    val totalScore: Int = 0,
    val streakCount: Int = 0,
    val highestStreak: Int = 0,
    val lastPracticeTimestamp: Long = 0L
)

@Dao
interface TenseDao {
    @Query("SELECT * FROM tense_scores")
    fun getAllScoresFlow(): Flow<List<TenseScore>>

    @Query("SELECT * FROM tense_scores WHERE tenseId = :tenseId")
    suspend fun getScoreById(tenseId: String): TenseScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: TenseScore)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStatsRecord?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsDirect(): UserStatsRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsRecord)

    @Query("DELETE FROM tense_scores")
    suspend fun clearTenseScores()

    @Query("DELETE FROM user_stats")
    suspend fun clearUserStats()
}

@Database(entities = [TenseScore::class, UserStatsRecord::class], version = 1, exportSchema = false)
abstract class TenseDatabase : RoomDatabase() {
    abstract fun tenseDao(): TenseDao

    companion object {
        @Volatile
        private var INSTANCE: TenseDatabase? = null

        fun getDatabase(context: Context): TenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TenseDatabase::class.java,
                    "tense_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class TenseRepository(private val tenseDao: TenseDao) {
    val allTenseScores: Flow<List<TenseScore>> = tenseDao.getAllScoresFlow()
    val userStats: Flow<UserStatsRecord?> = tenseDao.getUserStatsFlow()

    suspend fun updateScore(tenseId: String, tenseName: String, isCorrect: Boolean) {
        val existing = tenseDao.getScoreById(tenseId) ?: TenseScore(tenseId, tenseName)
        val updated = existing.copy(
            correctCount = existing.correctCount + if (isCorrect) 1 else 0,
            attemptedCount = existing.attemptedCount + 1
        )
        tenseDao.insertScore(updated)

        // Also update overall stats and check streaks
        val currentStats = tenseDao.getUserStatsDirect() ?: UserStatsRecord()
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val twoDaysMs = 48 * 60 * 60 * 1000L

        var newStreak = currentStats.streakCount
        if (isCorrect) {
            if (currentStats.lastPracticeTimestamp == 0L) {
                newStreak = 1
            } else {
                val diff = now - currentStats.lastPracticeTimestamp
                if (diff in oneDayMs until twoDaysMs) {
                    newStreak += 1
                } else if (diff >= twoDaysMs) {
                    newStreak = 1 // streak broken, reset to 1
                }
                // If they practice multiple times a day, streak stays the same
            }
        }

        val updatedStats = currentStats.copy(
            totalScore = currentStats.totalScore + if (isCorrect) 1 else 0,
            streakCount = newStreak,
            highestStreak = maxOf(currentStats.highestStreak, newStreak),
            lastPracticeTimestamp = now
        )
        tenseDao.insertUserStats(updatedStats)
    }

    suspend fun resetAllStats() {
        tenseDao.clearTenseScores()
        tenseDao.clearUserStats()
        // reinsert default stats
        tenseDao.insertUserStats(UserStatsRecord())
    }
}
