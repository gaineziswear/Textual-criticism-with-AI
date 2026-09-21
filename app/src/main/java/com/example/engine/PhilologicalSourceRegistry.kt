package com.example.engine

import com.example.model.SourceReference

/**
 * Source registry for evidence-backed philological work.
 *
 * This registry contains source metadata only. It does not imply that a
 * candidate is attested. A candidate becomes evidence-bearing only after
 * an adapter resolves it to a concrete lexical/textual record.
 */
object PhilologicalSourceRegistry {

    val aramaic = SourceReference(
        id = "cal",
        title = "Comprehensive Aramaic Lexicon",
        publication = "Hebrew Union College / Johns Hopkins University project",
        language = "Aramaic",
        sourceType = "Corpus + citation-based lexicon",
        url = "https://cal.huc.edu/",
        reliability = "primary scholarly resource; verify individual entry"
    )

    val preIslamicArabianInscriptions = SourceReference(
        id = "dasi",
        title = "Digital Archive for the Study of pre-Islamic Arabian Inscriptions",
        language = "Ancient North Arabian / South Arabian epigraphy",
        sourceType = "Epigraphic corpus",
        url = "https://dasi.cnr.it/",
        reliability = "primary/curated corpus; verify inscription record"
    )

    val sources: List<SourceReference> = listOf(
        aramaic,
        preIslamicArabianInscriptions
    )

    fun forLanguage(languageId: String): List<SourceReference> =
        when (languageId.lowercase()) {
            "aramaic", "syriac", "classical-syriac", "imperial-aramaic",
            "biblical-aramaic", "jewish-babylonian-aramaic",
            "nabataean-aramaic" -> listOf(aramaic)
            "safaitic", "hismaic", "dadanitic", "ancient-north-arabian",
            "south-arabian" -> listOf(preIslamicArabianInscriptions)
            else -> emptyList()
        }
}
