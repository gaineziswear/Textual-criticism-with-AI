package com.example.engine

data class ArabicToken(
    val index: Int,
    val surface: String,
    val normalized: String,
    val consonantalSkeleton: String
)

/**
 * Conservative Arabic normalization for comparative search.
 *
 * This is not a morphological analyzer and does not infer roots. It removes
 * presentation-level marks and normalizes common orthographic variants while
 * preserving the original surface form for citation.
 */
object ArabicBaselineNormalizer {

    private val removableMarks = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
    private val whitespace = Regex("\\s+")

    fun normalize(text: String): String =
        text
            .replace(removableMarks, "")
            .replace('ٱ', 'ا')
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ى', 'ي')
            .replace('ؤ', 'و')
            .replace('ئ', 'ي')
            .replace('ـ'.toString(), "")
            .trim()

    fun tokenize(text: String): List<ArabicToken> {
        if (text.isBlank()) return emptyList()

        // Preserve the citation surface while normalizing only the comparison form.
        // This prevents normalization (e.g. ؤ → و) from destroying the original witness.
        return whitespace.split(text.trim())
            .filter { it.isNotBlank() }
            .mapIndexed { index, surface ->
                val normalized = normalize(surface)
                ArabicToken(
                    index = index,
                    surface = surface,
                    normalized = normalized,
                    consonantalSkeleton = consonantalSkeleton(normalized)
                )
            }
    }

    private fun consonantalSkeleton(token: String): String =
        token
            .replace("ا", "")
            .replace("و", "")
            .replace("ي", "")
            .replace("ى", "")
            .replace("ة", "ه")
}
