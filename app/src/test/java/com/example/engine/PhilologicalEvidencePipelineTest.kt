package com.example.engine

import com.example.model.PhilologicalClassification
import com.example.model.VerificationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhilologicalEvidencePipelineTest {

    @Test
    fun seedsOnlySelectedLanguagesAndKeepsCandidatesUnverified() {
        val seeds = PhilologicalEvidencePipeline.seed(
            arabicText = "لا يؤمن أحدكم حتى يحب لأخيه ما يحب لنفسه",
            languageIds = listOf("Aramaic", "Aramaic", "Safaitic")
        )

        assertTrue(seeds.isNotEmpty())
        assertEquals(setOf("Aramaic", "Safaitic"), seeds.map { it.language }.toSet())

        val record = PhilologicalEvidencePipeline.toUnverifiedRecord(seeds.first())
        assertEquals(VerificationStatus.NOT_VERIFIED, record.verificationStatus)
        assertEquals(PhilologicalClassification.UNCERTAIN, record.classification)
        assertEquals(0f, record.confidence)
        assertTrue(record.humanVerificationRequired)
    }

    @Test
    fun sourceRegistryIsProvenanceOnlyUntilAttestationIsRetrieved() {
        val seeds = PhilologicalEvidencePipeline.seed(
            arabicText = "يحب",
            languageIds = listOf("Aramaic")
        )

        val record = PhilologicalEvidencePipeline.toUnverifiedRecord(seeds.first())

        assertTrue(record.sources.isNotEmpty())
        assertTrue(record.attestation == null)
        assertEquals(VerificationStatus.NOT_VERIFIED, record.verificationStatus)
    }
}
