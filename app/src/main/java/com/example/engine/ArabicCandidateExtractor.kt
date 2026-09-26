package com.example.engine

import com.example.model.PhilologicalCandidateSpan

/**
 * Conservative candidate extraction for comparative philology.
 *
 * This stage does not claim foreign origin. It only selects Arabic spans that
 * deserve external lexical/corpus lookup. Function words and punctuation are
 * deprioritized; content words and multi-token spans are retained.
 */
object ArabicCandidateExtractor {

    private val punctuation = Regex("[،؛,.!?؟:;()\\[\\]{}«»“”\\\"'ـ]+")
    private val functionWords = setOf(
        "و", "ف", "ثم", "أو", "أم", "بل", "لكن", "أن", "إن", "إنما",
        "ما", "لا", "لم", "لن", "لمّا", "من", "في", "على", "عن", "إلى",
        "حتى", "ب", "ك", "ل", "ال", "هذا", "هذه", "ذلك", "تلك", "هو",
        "هي", "هم", "هن", "أنا", "نحن", "أنت", "أنتم", "كان", "كانت"
    )

    fun extract(text: String, maxCandidates: Int = 24): List<PhilologicalCandidateSpan> {
        require(maxCandidates > 0) { "maxCandidates must be positive" }

        val tokens = ArabicBaselineNormalizer.tokenize(text)
        if (tokens.isEmpty()) return emptyList()

        val singletons = tokens
            .filter { isLexicalCandidate(it.normalized) }
            .map { token ->
                PhilologicalCandidateSpan(
                    startToken = token.index,
                    endToken = token.index,
                    surface = token.surface,
                    normalized = token.normalized,
                    reason = "content-word lexical lookup candidate",
                    priority = lexicalPriority(token.normalized)
                )
            }

        val phrases = tokens
            .zipWithNext()
            .mapNotNull { (left, right) ->
                if (isLexicalCandidate(left.normalized) && isLexicalCandidate(right.normalized)) {
                    PhilologicalCandidateSpan(
                        startToken = left.index,
                        endToken = right.index,
                        surface = "${left.surface} ${right.surface}",
                        normalized = "${left.normalized} ${right.normalized}",
                        reason = "adjacent content-word phrase candidate",
                        priority = (lexicalPriority(left.normalized) + lexicalPriority(right.normalized)) / 2 + 1
                    )
                } else null
            }

        return (phrases + singletons)
            .distinctBy { "${it.startToken}:${it.endToken}" }
            .sortedWith(compareByDescending<PhilologicalCandidateSpan> { it.priority }.thenBy { it.startToken })
            .take(maxCandidates)
    }

    private fun isLexicalCandidate(token: String): Boolean {
        val cleaned = token.replace(punctuation, "")
        if (cleaned.length < 2) return false
        if (cleaned in functionWords) return false
        return cleaned.any { it in '\u0621'..'\u064A' }
    }

    private fun lexicalPriority(token: String): Int {
        var score = 1
        if (token.length >= 5) score += 1
        if (token.any { it in setOf('ء', 'ؤ', 'ئ', 'ة', 'ى', 'آ', 'إ', 'أ', 'ٱ') }) score += 1
        return score
    }
}
