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


@Entity(tableName = "users")
data class UserRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val displayName: String = "Researcher", val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "projects")
data class ProjectRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val userId: Long? = null, val title: String, val createdAt: Long = System.currentTimeMillis(), val updatedAt: Long = System.currentTimeMillis())

@Entity(tableName = "documents")
data class DocumentRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val projectId: Long, val title: String, val originalText: String, val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "passages")
data class PassageRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val documentId: Long, val label: String, val text: String, val lineNumber: Int? = null, val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "words")
data class WordRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val passageId: Long, val position: Int, val surface: String, val normalized: String, val script: String)

@Entity(tableName = "word_analyses")
data class WordAnalysisRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val wordId: Long, val analysisVersion: String, val provider: String, val model: String, val confidence: String, val evidenceStatus: String, val inputHash: String, val createdAt: Long = System.currentTimeMillis(), val metadataJson: String = "{}")

@Entity(tableName = "languages")
data class LanguageRecord(@PrimaryKey val id: String, val canonicalName: String, val familyBranch: String, val scriptName: String)

@Entity(tableName = "dialects")
data class DialectRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val languageId: String, val name: String, val historicalStage: String? = null)

@Entity(tableName = "roots")
data class RootRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val languageId: String?, val form: String, val evidenceStatus: String, val confidence: String)

@Entity(tableName = "cognates")
data class CognateRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val rootId: Long, val language: String, val form: String, val meaning: String, val evidenceStatus: String)

@Entity(tableName = "sources")
data class SourceRecord(@PrimaryKey val id: String, val title: String, val author: String? = null, val publication: String? = null, val date: String? = null, val language: String? = null, val sourceType: String, val url: String? = null, val page: String? = null, val edition: String? = null, val reference: String? = null, val reliability: String = "unverified")

@Entity(tableName = "manuscripts")
data class ManuscriptEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val date: String? = null, val provenance: String? = null, val scriptType: String? = null, val sourceId: String? = null)

@Entity(tableName = "witnesses")
data class WitnessRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val manuscriptId: Long?, val siglum: String, val text: String, val sourceId: String? = null)

@Entity(tableName = "variants")
data class VariantRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val passageId: Long?, val position: Int, val variantType: String, val significanceScore: Int, val status: String, val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "textual_analyses")
data class TextualAnalysisRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val variantId: Long?, val confidence: String, val status: String, val assessment: String, val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "agent_runs")
data class AgentRunRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val agentName: String, val status: String, val startedAt: Long = System.currentTimeMillis(), val completedAt: Long? = null)

@Entity(tableName = "agent_results")
data class AgentResultRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val agentRunId: Long, val confidence: String, val evidenceStatus: String, val resultJson: String)

@Entity(tableName = "research_findings")
data class ResearchFindingRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val projectId: Long, val title: String, val claim: String, val confidence: String, val evidenceStatus: String, val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "content_projects")
data class ContentProjectRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val researchFindingId: Long, val title: String, val status: String = "Draft", val createdAt: Long = System.currentTimeMillis(), val editedAt: Long? = null)

@Entity(tableName = "social_posts")
data class SocialPostRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val contentProjectId: Long, val platform: String, val status: String, val body: String, val sourcesJson: String = "[]")

@Entity(tableName = "video_scripts")
data class VideoScriptRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val contentProjectId: Long, val durationSeconds: Int, val script: String, val visualInstructions: String, val verificationStatus: String)

@Entity(tableName = "brands")
data class BrandRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String = "VIA VERUM", val description: String = "Responsible public textual investigation", val logo: String? = null, val colors: String = "{}", val voice: String = "serious, accessible, evidence-based", val cta: String = "Follow VIA VERUM for deeper textual investigations.", val socialHandles: String = "{}")

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
    entities = [
        UserRecord::class, ProjectRecord::class, DocumentRecord::class, PassageRecord::class,
        WordRecord::class, WordAnalysisRecord::class, LanguageRecord::class, DialectRecord::class,
        RootRecord::class, CognateRecord::class, SourceRecord::class, ManuscriptEntity::class,
        WitnessRecord::class, VariantRecord::class, TextualAnalysisRecord::class, AgentRunRecord::class,
        AgentResultRecord::class, ResearchFindingRecord::class, ContentProjectRecord::class,
        SocialPostRecord::class, VideoScriptRecord::class, BrandRecord::class,
        ManuscriptRecord::class, ScholarNoteRecord::class, AgentChatRecord::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GlossaDatabase : RoomDatabase() {
    abstract fun glossaDao(): GlossaDao
}
