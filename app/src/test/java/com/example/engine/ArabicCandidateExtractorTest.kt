package com.example.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArabicCandidateExtractorTest {

    @Test
    fun extractsContentWordsAndSkipsCommonFunctionWords() {
        val candidates = ArabicCandidateExtractor.extract(
            "لا يؤمن أحدكم حتى يحب لأخيه ما يحب لنفسه"
        )

        assertTrue(candidates.any { it.surface == "يؤمن" })
        assertTrue(candidates.any { it.surface == "يحب" })
        assertTrue(candidates.none { it.surface == "لا" })
        assertTrue(candidates.none { it.surface == "ما" })
    }

    @Test
    fun candidateIndicesRemainStableAgainstOriginalTokenization() {
        val text = "لا يؤمن أحدكم حتى يحب لأخيه ما يحب لنفسه"
        val tokens = ArabicBaselineNormalizer.tokenize(text)
        val candidates = ArabicCandidateExtractor.extract(text)

        assertEquals(tokens.size, 10)
        assertTrue(candidates.all { it.startToken in tokens.indices })
        assertTrue(candidates.all { it.endToken in tokens.indices })
        assertTrue(candidates.all { it.startToken <= it.endToken })
    }
}
