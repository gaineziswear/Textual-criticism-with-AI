package com.example

import com.example.engine.ContentEngine
import com.example.engine.LinguisticPipeline
import com.example.engine.ManuscriptVariantEngine
import com.example.engine.WordAnalysisAgent
import com.example.model.Confidence
import com.example.model.ContentStatus
import com.example.model.EvidenceStatus
import com.example.model.ManuscriptWitnessInput
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlossaResearchEngineTest {
    @Test
    fun `language detection keeps shared-script candidates instead of script-only proof`() = runTest {
        val result = LinguisticPipeline.processWord(0, "שלום", "שלום", emptySet())
        assertEquals("Hebrew Square / Aramaic Script", result.detectedScript)
        assertTrue(result.candidateLanguages.map { it.id }.contains("hebrew"))
        assertTrue(result.candidateLanguages.map { it.id }.contains("aramaic"))
    }

    @Test
    fun `excluded languages never reach candidate list`() = runTest {
        val result = LinguisticPipeline.processWord(0, "שלום", "שלום", setOf("hebrew"))
        assertFalse(result.candidateLanguages.map { it.id }.contains("hebrew"))
    }

    @Test
    fun `fallback is labelled comparative and confidence capped low`() = runTest {
        val result = LinguisticPipeline.processWord(0, "שלום", "שלום", setOf("hebrew", "aramaic", "judeo_tunisian"))
        assertTrue(result.isFallbackMatch)
        assertEquals(Confidence.LOW, result.confidence)
        assertTrue(result.fallbackDetails!!.contains("not directly attested"))
        val structured = WordAnalysisAgent.analyze(result)
        assertEquals(EvidenceStatus.COMPARATIVE, structured.etymology.evidenceStatus)
        assertEquals(Confidence.LOW, structured.etymology.confidence)
    }

    @Test
    fun `structured word analysis records broad family before literal gloss`() = runTest {
        val result = LinguisticPipeline.processWord(0, "מלך", "מלך", emptySet())
        val structured = WordAnalysisAgent.analyze(result)
        assertTrue(structured.broadLanguageFamily.contains("Semitic"))
        assertTrue(structured.trace.indexOfFirst { it.label == "Broad family considered" } < structured.trace.indexOfFirst { it.label == "Literal gloss generated" })
    }

    @Test
    fun `manuscript variant engine detects omission and does not infer originality`() {
        val comparison = ManuscriptVariantEngine.compare(
            listOf(
                ManuscriptWitnessInput("A", "word1 word2 word3"),
                ManuscriptWitnessInput("B", "word1 word3")
            )
        )
        assertTrue(comparison.alignedUnits.any { it.variantType.name == "SUBSTITUTION" || it.variantType.name == "OMISSION" })
        assertTrue(comparison.assessment.contains("not automatic corrections") || comparison.trace.any { it.detail.contains("not presented as proof") })
    }

    @Test
    fun `content verification prevents possible from becoming proven`() = runTest {
        val result = LinguisticPipeline.processWord(0, "אבגד", "אבגד", emptySet())
        val structured = WordAnalysisAgent.analyze(result).copy(lexicalEvidence = EvidenceStatus.HYPOTHETICAL, confidence = Confidence.LOW)
        val verification = ContentEngine.verifyContent("This is definitely proven by the manuscript.", structured)
        assertEquals(ContentStatus.UNSUPPORTED, verification.status)
        assertTrue(verification.unsupportedClaims.isNotEmpty())
    }

    @Test
    fun `tiktok script includes visual direction and verification`() = runTest {
        val result = LinguisticPipeline.processWord(0, "λόγος", "λόγος", emptySet())
        val script = ContentEngine.generateTikTokScript(WordAnalysisAgent.analyze(result))
        assertTrue(script.visualSuggestions.isNotEmpty())
        assertTrue(script.manuscriptVisual.contains("actual submitted word"))
        assertTrue(script.verification.findings.isNotEmpty())
    }
}
