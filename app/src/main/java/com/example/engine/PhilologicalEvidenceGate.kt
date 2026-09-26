package com.example.engine

import com.example.model.EvidenceStatus
import com.example.model.PhilologicalClassification
import com.example.model.PhilologicalEvidenceRecord
import com.example.model.VerificationStatus

/**
 * Deterministic gate between AI-generated candidates and evidence-bearing claims.
 *
 * The engine deliberately refuses to promote an unverified lexical similarity
 * to borrowing. Model confidence is retained as confidence, not probability.
 */
object PhilologicalEvidenceGate {

    fun gate(
        record: PhilologicalEvidenceRecord
    ): PhilologicalEvidenceRecord {
        val verified = record.verificationStatus == VerificationStatus.VERIFIED
        val chronologySpecified = record.chronology.isNotBlank()
        val directionalitySpecified = record.directionality.isNotBlank()
        val hasSource = record.sources.isNotEmpty()

        if (!verified || !chronologySpecified || !directionalitySpecified || !hasSource) {
            val downgraded = when {
                record.verificationStatus == VerificationStatus.NOT_VERIFIED &&
                    record.nativeForm.isNotBlank() &&
                    record.arabicSpan.isNotBlank() ->
                    PhilologicalClassification.ORTHOGRAPHIC_SIMILARITY_ONLY
                else -> PhilologicalClassification.UNCERTAIN
            }

            return record.copy(
                classification = downgraded,
                humanVerificationRequired = true
            )
        }

        return record.copy(
            humanVerificationRequired = true
        )
    }

    fun evidenceStatus(record: PhilologicalEvidenceRecord): EvidenceStatus =
        when (record.verificationStatus) {
            VerificationStatus.VERIFIED -> EvidenceStatus.ATTESTED
            VerificationStatus.PARTIALLY_VERIFIED -> EvidenceStatus.COMPARATIVE
            VerificationStatus.NOT_VERIFIED -> EvidenceStatus.HYPOTHETICAL
        }
}
