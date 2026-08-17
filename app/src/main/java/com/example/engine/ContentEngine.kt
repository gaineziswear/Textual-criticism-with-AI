package com.example.engine

import com.example.model.ContentStatus
import com.example.model.ContentVerificationResult
import com.example.model.EvidenceStatus
import com.example.model.StructuredWordAnalysis
import com.example.model.VideoScript

object ContentEngine {
    fun generateTikTokScript(analysis: StructuredWordAnalysis, durationSeconds: Int = 60, brandName: String = "VIA VERUM"): VideoScript {
        val certaintyWord = when (analysis.lexicalEvidence) {
            EvidenceStatus.ATTESTED -> "attested"
            EvidenceStatus.COMPARATIVE -> "comparative"
            EvidenceStatus.RECONSTRUCTED -> "reconstructed"
            EvidenceStatus.HYPOTHETICAL -> "possible"
            EvidenceStatus.UNCERTAIN -> "uncertain"
        }
        val narration = "Before translating ${analysis.surfaceForm}, we identify the script, broad family, language candidate, and root evidence. The result is $certaintyWord, with ${analysis.confidence.label.lowercase()} confidence: ${analysis.literalGlossEn}."
        val verification = verifyContent(narration, analysis)
        return VideoScript(
            title = "What can we responsibly say about ${analysis.surfaceForm}?",
            hook = "This ancient word should not be translated before it is investigated.",
            narration = narration,
            onScreenText = listOf("Evidence before assertion", "Linguistics before translation", "Confidence: ${analysis.confidence.label}"),
            visualSuggestions = listOf("Show the input word, then reveal script → family → language → root → gloss as separate layers."),
            manuscriptVisual = "Use the actual submitted word only; generated manuscript imagery must be labelled reconstruction.",
            bRoll = listOf("Close-up of text analysis workspace", "Highlighted word card", "Evidence matrix panel"),
            aiImagePrompt = "Labelled reconstruction image: ancient manuscript workspace, close-up of ${analysis.script} text, documentary lighting, not an actual manuscript witness.",
            transition = "Cut from hook to layered research trace after 3 seconds; show evidence panel before conclusion.",
            cta = "Follow $brandName for deeper textual investigations.",
            verification = verification
        )
    }

    fun verifyContent(content: String, analysis: StructuredWordAnalysis): ContentVerificationResult {
        val unsupported = mutableListOf<String>()
        val qualifications = mutableListOf<String>()
        val lower = content.lowercase()
        if (analysis.lexicalEvidence != EvidenceStatus.ATTESTED && ("proven" in lower || "definitely" in lower)) {
            unsupported.add("Non-attested evidence was inflated into proof/certainty.")
        }
        if (analysis.lexicalEvidence == EvidenceStatus.RECONSTRUCTED && "ancient word" in lower) {
            qualifications.add("Reconstructed forms must not be presented as directly attested ancient words.")
        }
        if (analysis.confidence != com.example.model.Confidence.HIGH && "certain" in lower) {
            qualifications.add("Low/medium/uncertain research needs explicit qualification.")
        }
        val status = when {
            unsupported.isNotEmpty() -> ContentStatus.UNSUPPORTED
            qualifications.isNotEmpty() -> ContentStatus.REQUIRES_QUALIFICATION
            else -> ContentStatus.SUPPORTED
        }
        return ContentVerificationResult(
            status = status,
            findings = listOf("Content checked against evidence status ${analysis.lexicalEvidence.label} and confidence ${analysis.confidence.label}."),
            unsupportedClaims = unsupported,
            requiredQualifications = qualifications
        )
    }
}
