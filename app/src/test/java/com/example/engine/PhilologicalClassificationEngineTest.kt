package com.example.engine

import com.example.model.PhilologicalClassification
import com.example.model.PhilologicalEvidenceRecord
import com.example.model.VerificationStatus
import org.junit.Test
import org.junit.Assert.assertEquals

class PhilologicalClassificationEngineTest {

    @Test
    fun unverifiedSimilarityCannotBecomeBorrowing() {
        val record = sample(VerificationStatus.NOT_VERIFIED)
        assertEquals(
            PhilologicalClassification.ORTHOGRAPHIC_SIMILARITY_ONLY,
            PhilologicalClassificationEngine.classify(record)
        )
    }

    @Test
    fun incompleteVerifiedEvidenceRemainsUncertain() {
        val record = sample(VerificationStatus.VERIFIED).copy(
            directionality = ""
        )
        assertEquals(
            PhilologicalClassification.UNCERTAIN,
            PhilologicalClassificationEngine.classify(record)
        )
    }

    private fun sample(status: VerificationStatus) =
        PhilologicalEvidenceRecord(
            arabicSpan = "مثال",
            normalizedArabic = "مثال",
            arabicBaseline = "Arabic lexical baseline required",
            candidateLanguage = "Aramaic",
            historicalStage = "historical stage required",
            nativeForm = "example",
            transliteration = "example",
            lexicalSource = "CAL",
            attestation = "specific lexical/textual attestation",
            dateOrPeriod = "date required",
            semanticMatch = "documented semantic overlap",
            phonologicalMatch = "regular correspondence assessment",
            morphologicalMatch = "morphological comparison",
            chronology = "candidate attested before/within relevant chronology",
            directionality = "directionality assessment",
            alternativeArabicOrProtoSemiticExplanation = "competing explanation assessed",
            classification = PhilologicalClassification.UNCERTAIN,
            verificationStatus = status,
            confidence = 0.5f
        )
}
