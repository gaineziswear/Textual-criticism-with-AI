package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "manuscript_records")
data class ManuscriptRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val originalText: String,
    val sourceDescription: String,
    val timestamp: Long = System.currentTimeMillis(),
    val excludedLanguages: String = "", // Comma-separated language IDs
    val analysisSummary: String = ""
)

@Entity(tableName = "scholar_notes")
data class ScholarNoteRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val manuscriptId: Long,
    val token: String,
    val wordIndex: Int,
    val noteType: String, // "Scribal Anomaly", "Cognate Link", "Variant Reading", "Methodology"
    val content: String,
    val authorAgent: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "agent_chat_records")
data class AgentChatRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentType: String, // "INTERACTIVE_DEEP_ANALYSIS" | "SEMITIC_TEXTUAL_CRITIC"
    val userPrompt: String,
    val agentResponse: String,
    val contextToken: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface GlossaDao {
    @Query("SELECT * FROM manuscript_records ORDER BY timestamp DESC")
    fun getAllManuscripts(): Flow<List<ManuscriptRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscript(record: ManuscriptRecord): Long

    @Query("DELETE FROM manuscript_records WHERE id = :id")
    suspend fun deleteManuscript(id: Long)

    @Query("SELECT * FROM scholar_notes WHERE manuscriptId = :manuscriptId ORDER BY timestamp ASC")
    fun getNotesForManuscript(manuscriptId: Long): Flow<List<ScholarNoteRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ScholarNoteRecord): Long

    @Query("SELECT * FROM agent_chat_records ORDER BY timestamp ASC")
    fun getAllChatRecords(): Flow<List<AgentChatRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRecord(chat: AgentChatRecord): Long
}

@Database(
    entities = [ManuscriptRecord::class, ScholarNoteRecord::class, AgentChatRecord::class],
    version = 1,
    exportSchema = false
)
abstract class GlossaDatabase : RoomDatabase() {
    abstract fun glossaDao(): GlossaDao
}
