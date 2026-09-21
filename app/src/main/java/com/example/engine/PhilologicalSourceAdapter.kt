package com.example.engine

import com.example.model.SourceReference

data class LexicalEvidenceLookup(
    val query: String,
    val language: String,
    val source: SourceReference,
    val lookupUrl: String,
    val verificationRequired: Boolean = true
)

/**
 * Builds reproducible lookup targets for approved external lexical resources.
 *
 * It deliberately returns lookup metadata rather than pretending to have
 * retrieved or verified a lexical entry. Retrieval must be performed by a
 * source-specific adapter and preserved with its locator/provenance.
 */
object PhilologicalSourceAdapter {

    fun calLookup(query: String): LexicalEvidenceLookup {
        val encoded = java.net.URLEncoder.encode(query.trim(), Charsets.UTF_8.name())
        val source = PhilologicalSourceRegistry.aramaic
        return LexicalEvidenceLookup(
            query = query.trim(),
            language = "Aramaic",
            source = source,
            lookupUrl = "https://cal.huc.edu/browseSKEYheaders.php?first3=$encoded"
        )
    }

    fun dasiLookup(query: String): LexicalEvidenceLookup {
        val source = PhilologicalSourceRegistry.preIslamicArabianInscriptions
        return LexicalEvidenceLookup(
            query = query.trim(),
            language = "Pre-Islamic Arabian epigraphy",
            source = source,
            lookupUrl = source.url ?: "https://dasi.cnr.it/"
        )
    }
}
