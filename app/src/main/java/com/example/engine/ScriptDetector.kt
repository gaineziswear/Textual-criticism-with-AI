package com.example.engine

import com.example.model.AncientLanguage
import com.example.model.LanguageCatalog
import com.example.model.ScriptTier

data class ScriptDetectionResult(
    val scriptName: String,
    val scriptTier: ScriptTier,
    val rawCandidateLanguages: List<AncientLanguage>,
    val filteredCandidateLanguages: List<AncientLanguage>,
    val fallbackCandidates: List<AncientLanguage> = emptyList(),
    val isPrimaryDecisive: Boolean = false,
    val isFallbackRequired: Boolean = false
)

object ScriptDetector {

    fun detectScript(word: String, excludedLanguageIds: Set<String> = emptySet()): ScriptDetectionResult {
        val clean = word.trim().filter { !it.isWhitespace() && it != '.' && it != ',' && it != ';' && it != ':' && it != '!' && it != '?' && it != '"' && it != '\'' }
        if (clean.isEmpty()) {
            return ScriptDetectionResult(
                scriptName = "Unknown",
                scriptTier = ScriptTier.TIER_3,
                rawCandidateLanguages = emptyList(),
                filteredCandidateLanguages = emptyList()
            )
        }

        // Check codepoints
        var hasSyriac = false
        var hasHebrew = false
        var hasGreek = false
        var hasGeez = false
        var hasArabic = false
        var hasCuneiform = false
        var hasUgaritic = false
        var hasOldPersian = false
        var hasPhoenician = false
        var hasMusnad = false // South Arabian
        var hasOldNorthArabian = false // Safaitic/Thamudic/Lihyanite
        var hasAvestan = false
        var hasMandaic = false
        var hasNabataean = false
        var hasPalmyrene = false
        var hasImperialAramaic = false
        var hasParthian = false
        var hasPahlavi = false
        var hasSogdian = false
        var hasCoptic = false
        var hasDevanagari = false
        var hasSinhala = false
        var hasGeorgian = false
        var hasGlagolitic = false
        var hasCyrillic = false
        var hasRunic = false
        var hasBrahmi = false
        var hasHieroglyphic = false
        var hasTifinagh = false
        var hasLatin = false

        var i = 0
        while (i < clean.length) {
            val cp = clean.codePointAt(i)
            when {
                cp in 0x0700..0x074F || cp in 0x0860..0x086F -> hasSyriac = true
                cp in 0x0590..0x05FF || cp in 0xFB1D..0xFB4F -> hasHebrew = true
                cp in 0x0370..0x03FF || cp in 0x1F00..0x1FFF -> hasGreek = true
                cp in 0x1200..0x137F || cp in 0x1380..0x139F || cp in 0x2D80..0x2DDF || cp in 0xAB00..0xAB2F -> hasGeez = true
                cp in 0x0600..0x06FF || cp in 0x0750..0x077F || cp in 0x08A0..0x08FF || cp in 0xFB50..0xFDFF || cp in 0xFE70..0xFEFF -> hasArabic = true
                cp in 0x12000..0x123FF || cp in 0x12400..0x1247F -> hasCuneiform = true
                cp in 0x10380..0x1039F -> hasUgaritic = true
                cp in 0x103A0..0x103DF -> hasOldPersian = true
                cp in 0x10900..0x1091F -> hasPhoenician = true
                cp in 0x10A60..0x10A7F -> hasMusnad = true
                cp in 0x10A80..0x10A9F -> hasOldNorthArabian = true
                cp in 0x10800..0x1083F -> hasAvestan = true
                cp in 0x0840..0x085F -> hasMandaic = true
                cp in 0x10880..0x108AF -> hasNabataean = true
                cp in 0x10860..0x1087F -> hasPalmyrene = true
                cp in 0x10840..0x1085F -> hasImperialAramaic = true
                cp in 0x10B40..0x10B5F -> hasParthian = true
                cp in 0x10B60..0x10B7F || cp in 0x10B80..0x10BAF -> hasPahlavi = true
                cp in 0x10F30..0x10F6F || cp in 0x10F00..0x10F2F -> hasSogdian = true
                cp in 0x2C80..0x2CFF || (cp in 0x03E2..0x03EF) -> hasCoptic = true
                cp in 0x0900..0x097F || cp in 0xA8E0..0xA8FF -> hasDevanagari = true
                cp in 0x0D80..0x0DFF -> hasSinhala = true
                cp in 0x10A0..0x10FF || cp in 0x2D00..0x2D2F -> hasGeorgian = true
                cp in 0x2C00..0x2C5F -> hasGlagolitic = true
                cp in 0x0400..0x04FF || cp in 0x0500..0x052F -> hasCyrillic = true
                cp in 0x16A0..0x16FF -> hasRunic = true
                cp in 0x11000..0x1107F -> hasBrahmi = true
                cp in 0x13000..0x1342F -> hasHieroglyphic = true
                cp in 0x2D30..0x2D7F -> hasTifinagh = true
                cp in 0x0041..0x005A || cp in 0x0061..0x007A || cp in 0x00C0..0x024F -> hasLatin = true
            }
            i += Character.charCount(cp)
        }

        val rawCandidates = mutableListOf<AncientLanguage>()
        var scriptName = "Unidentified Script"
        var tier = ScriptTier.TIER_3

        when {
            hasSyriac -> {
                scriptName = "Syriac Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("syriac")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("aramaic")?.let { rawCandidates.add(it) }
            }
            hasUgaritic -> {
                scriptName = "Ugaritic Cuneiform"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("ugaritic")?.let { rawCandidates.add(it) }
            }
            hasOldPersian -> {
                scriptName = "Old Persian Cuneiform"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("old_persian")?.let { rawCandidates.add(it) }
            }
            hasGreek -> {
                scriptName = "Greek Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("greek")?.let { rawCandidates.add(it) }
            }
            hasCoptic -> {
                scriptName = "Coptic Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("coptic")?.let { rawCandidates.add(it) }
            }
            hasMandaic -> {
                scriptName = "Mandaic Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("mandaean")?.let { rawCandidates.add(it) }
            }
            hasNabataean -> {
                scriptName = "Nabataean Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("nabatean")?.let { rawCandidates.add(it) }
            }
            hasPalmyrene -> {
                scriptName = "Palmyrene Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("palmyrene")?.let { rawCandidates.add(it) }
            }
            hasParthian -> {
                scriptName = "Inscriptional Parthian"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("parthian")?.let { rawCandidates.add(it) }
            }
            hasSogdian -> {
                scriptName = "Sogdian Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("sogdian")?.let { rawCandidates.add(it) }
            }
            hasGeorgian -> {
                scriptName = "Georgian Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("georgian")?.let { rawCandidates.add(it) }
            }
            hasSinhala -> {
                scriptName = "Sinhala Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("sinhalese")?.let { rawCandidates.add(it) }
            }
            hasPhoenician -> {
                scriptName = "Phoenician / Canaanite Script"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("phoenician")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("moabitish")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("edomitish")?.let { rawCandidates.add(it) }
            }
            hasMusnad -> {
                scriptName = "Ancient South Arabian (Musnad)"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("south_arabian")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("himyaritic")?.let { rawCandidates.add(it) }
            }
            hasOldNorthArabian -> {
                scriptName = "Old North Arabian Script"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("safaite")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("thamudic")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("lihyanite")?.let { rawCandidates.add(it) }
            }
            hasAvestan -> {
                scriptName = "Avestan Script"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("avestic")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("pazand")?.let { rawCandidates.add(it) }
            }
            hasPahlavi -> {
                scriptName = "Pahlavi Script"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("pahlavi")?.let { rawCandidates.add(it) }
            }
            hasHieroglyphic -> {
                scriptName = "Egyptian Hieroglyphs"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("egyptian")?.let { rawCandidates.add(it) }
            }
            hasRunic -> {
                scriptName = "Runic Futhark"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("norse")?.let { rawCandidates.add(it) }
            }
            hasGlagolitic || hasCyrillic -> {
                scriptName = if (hasGlagolitic) "Glagolitic Script" else "Early Cyrillic Script"
                tier = ScriptTier.TIER_2
                LanguageCatalog.getById("slavonic")?.let { rawCandidates.add(it) }
            }
            hasHebrew || hasImperialAramaic -> {
                scriptName = "Hebrew Square / Aramaic Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("hebrew")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("aramaic")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("judeo_tunisian")?.let { rawCandidates.add(it) }
            }
            hasGeez -> {
                scriptName = "Ge'ez Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("ethiopic")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("bilin")?.let { rawCandidates.add(it) }
            }
            hasDevanagari || hasBrahmi -> {
                scriptName = if (hasDevanagari) "Devanagari Script" else "Brahmi Script"
                tier = ScriptTier.TIER_1
                LanguageCatalog.getById("sanskrit")?.let { rawCandidates.add(it) }
                LanguageCatalog.getById("pali")?.let { rawCandidates.add(it) }
            }
            hasCuneiform -> {
                scriptName = "Mesopotamian Cuneiform"
                tier = ScriptTier.TIER_3
                listOf("akkadian", "babylonian", "assyrian", "sumerian", "elamite", "hurrian", "hittite")
                    .mapNotNull { LanguageCatalog.getById(it) }
                    .forEach { rawCandidates.add(it) }
            }
            hasArabic -> {
                scriptName = "Arabic / Perso-Arabic Script"
                tier = ScriptTier.TIER_3
                listOf("arabic", "persian", "turkish", "turki", "afghan", "baluchi", "beja", "bishari", "mehri", "umani")
                    .mapNotNull { LanguageCatalog.getById(it) }
                    .forEach { rawCandidates.add(it) }
            }
            hasTifinagh -> {
                scriptName = "Tifinagh Script"
                tier = ScriptTier.TIER_3
                LanguageCatalog.getById("berber")?.let { rawCandidates.add(it) }
            }
            hasLatin -> {
                scriptName = "Latin Script"
                tier = ScriptTier.TIER_3
                listOf("latin", "norse", "berber", "baluchi", "pali", "turkish")
                    .mapNotNull { LanguageCatalog.getById(it) }
                    .forEach { rawCandidates.add(it) }
            }
            else -> {
                scriptName = "Indeterminate Script"
                tier = ScriptTier.TIER_3
                rawCandidates.addAll(LanguageCatalog.ALL_53_LANGUAGES)
            }
        }

        // Filter out excluded languages
        val filtered = rawCandidates.filter { it.id !in excludedLanguageIds }

        // Determine if primary is decisive or fallback is required
        val isDecisive = (tier == ScriptTier.TIER_1 && filtered.size == 1 && rawCandidates.size == 1)
        val fallbackRequired = filtered.isEmpty() && rawCandidates.isNotEmpty()

        // Compute fallback candidates if filtered candidate list became empty
        val fallbackCandidates = mutableListOf<AncientLanguage>()
        if (fallbackRequired || filtered.isEmpty()) {
            rawCandidates.forEach { candidate ->
                LanguageCatalog.getFallbacksFor(candidate.id).forEach { fb ->
                    if (fb.id !in excludedLanguageIds && fb !in fallbackCandidates) {
                        fallbackCandidates.add(fb)
                    }
                }
            }
        }

        return ScriptDetectionResult(
            scriptName = scriptName,
            scriptTier = tier,
            rawCandidateLanguages = rawCandidates,
            filteredCandidateLanguages = filtered,
            fallbackCandidates = fallbackCandidates,
            isPrimaryDecisive = isDecisive,
            isFallbackRequired = fallbackRequired
        )
    }
}
