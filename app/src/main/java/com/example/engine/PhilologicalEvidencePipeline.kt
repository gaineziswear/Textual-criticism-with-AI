package com.example.engine

import com.example.model.PhilologicalCandidateSpan
import com.example.model.PhilologicalClassification
import com.example.model.PhilologicalEvidenceRecord
import com.example.model.SourceReference
import com.example.model.VerificationStatus

data class CandidateEvidenceSeed(
    val candidate: PhilologicalCandidateSpan,
    val language: String,
    val historicalStage: String = "unspecified",
    val source: SourceReference? = null
)

/**
 * Converts candidate spans into evidence records without inventing evidence.
 *
 * Until a source adapter returns a verified lexical/textual attestation, the
 * resulting record remains NOT_VERIFIED and cannot be promoted to borrowing.
 */
object PhilologicalEvidencePipeline {

    fun seed(
        arabicText: String,
        languageIds: List<String>,
        maxCandidates: Int = 24
    ): List<CandidateEvidenceSeed> {
        val candidates = ArabicCandidateExtractor.extract(arabicText, maxCandidates)
        return languageIds
            .distinct()
            .flatMap { language ->
                candidates.map { candidate ->
                    CandidateEvidenceSeed(
                        candidate = candidate,
                        language = language,
                        source = PhilologicalSourceRegistry.forLanguage(language).firstOrNull()
                    )
                }
            }
    }

    fun toUnverifiedRecord(seed: CandidateEvidenceSeed): PhilologicalEvidenceRecord {
        val candidate = seed.candidate
        val sources = seed.source?.let { listOf(it) } ?: emptyList()

        return PhilologicalEvidenceRecord(
            arabicSpan = candidate.surface,
            normalizedArabic = candidate.normalized,
            arabicBaseline = "Candidate span only; Arabic lexical baseline verification required",
            candidateLanguage = seed.language,
            historicalStage = seed.historicalStage,
            nativeForm = "",
            transliteration = "",
            lexicalSource = seed.source?.title,
            attestation = null,
            dateOrPeriod = null,
            semanticMatch = "",
            phonologicalMatch = "",
            morphologicalMatch = "",
            chronology = "",
            directionality = "",
            alternativeArabicOrProtoSemiticExplanation = "",
            classification = PhilologicalClassification.UNCERTAIN,
            verificationStatus = VerificationStatus.NOT_VERIFIED,
            confidence = 0f,
            humanVerificationRequired = true,
            sources = sources
        )
    }
}
