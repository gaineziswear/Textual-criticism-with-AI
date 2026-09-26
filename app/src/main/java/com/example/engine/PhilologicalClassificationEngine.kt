package com.example.engine

import com.example.model.PhilologicalClassification
import com.example.model.PhilologicalEvidenceRecord
import com.example.model.VerificationStatus

/**
 * Conservative classification helper.
 *
 * Similarity alone never produces a borrowing classification.
 */
object PhilologicalClassificationEngine {

    fun classify(record: PhilologicalEvidenceRecord): PhilologicalClassification {
        if (record.verificationStatus == VerificationStatus.NOT_VERIFIED) {
            return if (record.nativeForm.isNotBlank() && record.arabicSpan.isNotBlank()) {
                PhilologicalClassification.ORTHOGRAPHIC_SIMILARITY_ONLY
            } else {
                PhilologicalClassification.UNCERTAIN
            }
        }

        val strongComparativeEvidence =
            record.semanticMatch.isNotBlank() &&
            record.phonologicalMatch.isNotBlank() &&
            record.morphologicalMatch.isNotBlank() &&
            record.chronology.isNotBlank() &&
            record.directionality.isNotBlank() &&
            record.alternativeArabicOrProtoSemiticExplanation.isNotBlank()

        if (!strongComparativeEvidence) {
            return PhilologicalClassification.UNCERTAIN
        }

        return when (record.verificationStatus) {
            VerificationStatus.VERIFIED -> PhilologicalClassification.POSSIBLE_BORROWING
            VerificationStatus.PARTIALLY_VERIFIED -> PhilologicalClassification.UNCERTAIN
            VerificationStatus.NOT_VERIFIED -> PhilologicalClassification.UNCERTAIN
        }
    }
}
