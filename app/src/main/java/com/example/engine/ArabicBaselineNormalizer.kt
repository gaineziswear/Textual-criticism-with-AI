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

    fun tokenize(text: String): List<ArabicToken> =
        whitespace.split(normalize(text))
            .filter { it.isNotBlank() }
            .mapIndexed { index, token ->
                ArabicToken(
                    index = index,
                    surface = token,
                    normalized = token,
                    consonantalSkeleton = consonantalSkeleton(token)
                )
            }

    private fun consonantalSkeleton(token: String): String =
        token
            .replace("ا", "")
            .replace("و", "")
            .replace("ي", "")
            .replace("ى", "")
            .replace("ة", "ه")
}
