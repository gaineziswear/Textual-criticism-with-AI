package com.example.model

enum class ScriptTier(val tierNumber: Int, val description: String) {
    TIER_1(1, "Unique Unicode Script (Decisive match)"),
    TIER_2(2, "Shared with 1-2 close relatives"),
    TIER_3(3, "Shared large script cluster / Cuneiform / Perso-Arabic"),
    TIER_4(4, "Fragmentary corpus / Epigraphic hypothesis")
}

enum class Confidence(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    UNCERTAIN("Uncertain")
}

data class AncientLanguage(
    val id: String,
    val canonicalName: String,
    val scriptTier: ScriptTier,
    val scriptName: String,
    val familyBranch: String,
    val notes: String,
    val fallbackLanguageIds: List<String> = emptyList(),
    val sampleCharacters: String = ""
)

object LanguageCatalog {
    val ALL_53_LANGUAGES: List<AncientLanguage> = listOf(
        AncientLanguage(
            id = "syriac",
            canonicalName = "Syriac",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Syriac",
            familyBranch = "Northwest Semitic (Aramaic)",
            notes = "Syriac script, literary dialect of Aramaic",
            fallbackLanguageIds = listOf("aramaic"),
            sampleCharacters = "ܫܠܡܐ"
        ),
        AncientLanguage(
            id = "aramaic",
            canonicalName = "Aramaic",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Imperial Aramaic / Square Hebrew / Syriac",
            familyBranch = "Northwest Semitic",
            notes = "No single script — Imperial Aramaic, later square Hebrew script or Syriac",
            fallbackLanguageIds = listOf("syriac", "hebrew"),
            sampleCharacters = "שלמא"
        ),
        AncientLanguage(
            id = "hebrew",
            canonicalName = "Hebrew",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Hebrew square script",
            familyBranch = "Canaanite (Northwest Semitic)",
            notes = "Biblical and Epigraphic Hebrew in square script",
            fallbackLanguageIds = listOf("aramaic", "phoenician"),
            sampleCharacters = "שלום"
        ),
        AncientLanguage(
            id = "greek",
            canonicalName = "Greek",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Greek",
            familyBranch = "Hellenic (Indo-European isolate in catalog)",
            notes = "Ancient/Koine/Byzantine Greek. Sole representative of branch here.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "λόγος"
        ),
        AncientLanguage(
            id = "ethiopic",
            canonicalName = "Ethiopic (Ge'ez)",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Ge'ez",
            familyBranch = "South Semitic (Ethiosemitic)",
            notes = "Classical Ge'ez liturgical language",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "ሰላም"
        ),
        AncientLanguage(
            id = "pahlavi",
            canonicalName = "Pahlavi",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Inscriptional / Psalter Pahlavi / Transliteration",
            familyBranch = "Middle Iranian",
            notes = "Middle Persian. Inscriptional/Psalter have Unicode blocks; Book Pahlavi in transliteration",
            fallbackLanguageIds = listOf("avestic", "parthian"),
            sampleCharacters = "𐭯𐭠𐭫𐭮𐭩"
        ),
        AncientLanguage(
            id = "persian",
            canonicalName = "Persian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Perso-Arabic",
            familyBranch = "Iranian (Indo-Iranian)",
            notes = "Perso-Arabic script, shares the Arabic-script cluster",
            fallbackLanguageIds = listOf("pahlavi", "old_persian"),
            sampleCharacters = "پارس"
        ),
        AncientLanguage(
            id = "akkadian",
            canonicalName = "Akkadian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Mesopotamian Cuneiform",
            familyBranch = "East Semitic",
            notes = "Cuneiform, shared sign inventory with Sumerian/Elamite/Hittite/Hurrian",
            fallbackLanguageIds = listOf("babylonian", "assyrian"),
            sampleCharacters = "𒀀𒈾"
        ),
        AncientLanguage(
            id = "south_arabian",
            canonicalName = "S. Arabian (South Arabian)",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Ancient South Arabian (Musnad)",
            familyBranch = "Old South Arabian (Semitic)",
            notes = "Old/Ancient South Arabian Musnad script, shared with Himyaritic",
            fallbackLanguageIds = listOf("himyaritic"),
            sampleCharacters = "𐩪𐩡𐩣"
        ),
        AncientLanguage(
            id = "armenian",
            canonicalName = "Armenian",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Armenian",
            familyBranch = "Armenian (Indo-European isolate in catalog)",
            notes = "Classical Grabar Armenian script",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "լոյս"
        ),
        AncientLanguage(
            id = "phoenician",
            canonicalName = "Phoenician",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Phoenician",
            familyBranch = "Canaanite (Northwest Semitic)",
            notes = "Phoenician script; conventionally also used for Moabitish/Edomitish",
            fallbackLanguageIds = listOf("hebrew", "moabitish", "edomitish"),
            sampleCharacters = "𐤔𐤋𐤌"
        ),
        AncientLanguage(
            id = "avestic",
            canonicalName = "Avestic",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Avestan",
            familyBranch = "Old Iranian",
            notes = "Avestan script, shared with Pazand",
            fallbackLanguageIds = listOf("old_persian", "pazand"),
            sampleCharacters = "𐬀𐬵𐬎𐬭𐬋"
        ),
        AncientLanguage(
            id = "mandaean",
            canonicalName = "Mandaean",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Mandaic",
            familyBranch = "Eastern Aramaic (Semitic)",
            notes = "Classical Mandaic script and liturgical language",
            fallbackLanguageIds = listOf("syriac", "aramaic"),
            sampleCharacters = "ࡄࡉࡉࡀ"
        ),
        AncientLanguage(
            id = "sanskrit",
            canonicalName = "Sanskrit",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Devanagari / Brahmi",
            familyBranch = "Indo-Aryan",
            notes = "Vedic and Classical Sanskrit",
            fallbackLanguageIds = listOf("pali"),
            sampleCharacters = "धर्म"
        ),
        AncientLanguage(
            id = "pazand",
            canonicalName = "Pazand",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Avestan script",
            familyBranch = "Middle Iranian",
            notes = "Middle Persian written in Avestan letters",
            fallbackLanguageIds = listOf("pahlavi", "avestic"),
            sampleCharacters = "𐬰𐬀𐬭𐬀𐬚𐬎𐬱𐬙𐬭𐬀"
        ),
        AncientLanguage(
            id = "nabatean",
            canonicalName = "Nabatean",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Nabataean",
            familyBranch = "Northwest Semitic (Aramaic)",
            notes = "Nabataean script, direct ancestor of Arabic script",
            fallbackLanguageIds = listOf("aramaic", "arabic"),
            sampleCharacters = "𐢝𐢑𐢞"
        ),
        AncientLanguage(
            id = "safaite",
            canonicalName = "Safaite",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Old North Arabian",
            familyBranch = "Old North Arabian (Semitic)",
            notes = "Old North Arabian script shared with Thamudic/Lihyanite",
            fallbackLanguageIds = listOf("thamudic", "lihyanite", "arabic"),
            sampleCharacters = "𐪐𐪑𐪒"
        ),
        AncientLanguage(
            id = "palmyrene",
            canonicalName = "Palmyrene",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Palmyrene",
            familyBranch = "Northwest Semitic (Aramaic)",
            notes = "Palmyrene script (Aramaic-derived dialect)",
            fallbackLanguageIds = listOf("aramaic"),
            sampleCharacters = "𐡠𐡡𐡢"
        ),
        AncientLanguage(
            id = "ugaritic",
            canonicalName = "Ras Shamra (Ugaritic)",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Ugaritic Cuneiform",
            familyBranch = "Northwest Semitic",
            notes = "Ugaritic cuneiform alphabetic block, distinct from Mesopotamian",
            fallbackLanguageIds = listOf("phoenician", "hebrew"),
            sampleCharacters = "𐎁𐎓𐎍"
        ),
        AncientLanguage(
            id = "coptic",
            canonicalName = "Coptic",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Coptic",
            familyBranch = "Egyptian (Afroasiatic)",
            notes = "Final stage of the Egyptian language in Greek-derived Coptic script",
            fallbackLanguageIds = listOf("egyptian"),
            sampleCharacters = "ⲛⲟⲩⲧⲉ"
        ),
        AncientLanguage(
            id = "latin",
            canonicalName = "Latin",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Latin",
            familyBranch = "Italic (Indo-European isolate in catalog)",
            notes = "Classical and Medieval Latin. Sole representative of Italic branch here.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "veritas"
        ),
        AncientLanguage(
            id = "berber",
            canonicalName = "Berber",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Tifinagh / Latin / Arabic",
            familyBranch = "Berber (Afroasiatic)",
            notes = "Tifinagh, Latin, or Arabic script depending on era",
            fallbackLanguageIds = listOf("arabic"),
            sampleCharacters = "ⴰⵎⴰⵣⵉⵖ"
        ),
        AncientLanguage(
            id = "egyptian",
            canonicalName = "Egyptian",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Egyptian Hieroglyphic",
            familyBranch = "Egyptian (Afroasiatic)",
            notes = "Hieroglyphic Unicode block; earlier stage of Coptic",
            fallbackLanguageIds = listOf("coptic"),
            sampleCharacters = "𓊹"
        ),
        AncientLanguage(
            id = "babylonian",
            canonicalName = "Babylonian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Mesopotamian Cuneiform",
            familyBranch = "East Semitic",
            notes = "Dialect of Akkadian — cuneiform, not script-distinguishable",
            fallbackLanguageIds = listOf("akkadian", "assyrian"),
            sampleCharacters = "𒀭𒀫𒌓"
        ),
        AncientLanguage(
            id = "assyrian",
            canonicalName = "Assyrian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Mesopotamian Cuneiform",
            familyBranch = "East Semitic",
            notes = "Dialect of Akkadian (ancient). Distinct from modern Neo-Aramaic",
            fallbackLanguageIds = listOf("akkadian", "babylonian"),
            sampleCharacters = "𒀭𒀸𒋩"
        ),
        AncientLanguage(
            id = "sumerian",
            canonicalName = "Sumerian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Mesopotamian Cuneiform",
            familyBranch = "Language Isolate",
            notes = "Cuneiform language isolate. No genetic relatives.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "𒈗"
        ),
        AncientLanguage(
            id = "arabic",
            canonicalName = "Arabic (non-Qur'anic dialect borrowing)",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic",
            familyBranch = "Central Semitic",
            notes = "Arabic script with dialectal and regional substrate",
            fallbackLanguageIds = listOf("nabatean", "safaite", "thamudic", "lihyanite", "umani", "judeo_tunisian"),
            sampleCharacters = "كتاب"
        ),
        AncientLanguage(
            id = "old_persian",
            canonicalName = "Old Persian",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Old Persian Cuneiform",
            familyBranch = "Old Iranian",
            notes = "Old Persian cuneiform block (Achaemenid)",
            fallbackLanguageIds = listOf("avestic"),
            sampleCharacters = "𐏋"
        ),
        AncientLanguage(
            id = "parthian",
            canonicalName = "Parthian",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Inscriptional Parthian",
            familyBranch = "Middle Iranian",
            notes = "Inscriptional Parthian script (Arsacid)",
            fallbackLanguageIds = listOf("pahlavi", "sogdian"),
            sampleCharacters = "𐭀𐭁𐭂"
        ),
        AncientLanguage(
            id = "sogdian",
            canonicalName = "Sogdian",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Sogdian / Old Sogdian",
            familyBranch = "Eastern Iranian",
            notes = "Silk Road Iranian lingua franca in Sogdian script",
            fallbackLanguageIds = listOf("parthian", "old_persian"),
            sampleCharacters = "𐼰𐼱𐼲"
        ),
        AncientLanguage(
            id = "elamite",
            canonicalName = "Elamite",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Elamite Cuneiform",
            familyBranch = "Language Isolate",
            notes = "Elamite cuneiform isolate. Not related to Elymaic.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "𒁹𒀭"
        ),
        AncientLanguage(
            id = "hurrian",
            canonicalName = "Hurrian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Mesopotamian Cuneiform",
            familyBranch = "Hurro-Urartian (isolate in catalog)",
            notes = "Written in Akkadian cuneiform, partially understood",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "𒄷𒌨𒊑"
        ),
        AncientLanguage(
            id = "hittite",
            canonicalName = "Hittite",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Hittite Cuneiform",
            familyBranch = "Anatolian (isolate in catalog)",
            notes = "Anatolian Indo-European in cuneiform. Sole Anatolian branch here.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "𒉈𒅆𒇷"
        ),
        AncientLanguage(
            id = "himyaritic",
            canonicalName = "Himyaritic",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Ancient South Arabian (Musnad)",
            familyBranch = "Old South Arabian",
            notes = "Old South Arabian continuum shared with S. Arabian",
            fallbackLanguageIds = listOf("south_arabian"),
            sampleCharacters = "𐩢𐩣𐩺𐩧"
        ),
        AncientLanguage(
            id = "thamudic",
            canonicalName = "Thamudic",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Old North Arabian",
            familyBranch = "Old North Arabian",
            notes = "Old North Arabian script continuum with Safaitic/Lihyanite",
            fallbackLanguageIds = listOf("safaite", "lihyanite"),
            sampleCharacters = "𐪀𐪁𐪂"
        ),
        AncientLanguage(
            id = "lihyanite",
            canonicalName = "Lihyanite",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Old North Arabian (Dadanitic)",
            familyBranch = "Old North Arabian",
            notes = "Dadanitic / Lihyanite epigraphic corpus",
            fallbackLanguageIds = listOf("safaite", "thamudic"),
            sampleCharacters = "𐪓𐪔𐪕"
        ),
        AncientLanguage(
            id = "afghan",
            canonicalName = "Afghan (Pashto)",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Perso-Arabic (Pashto)",
            familyBranch = "Eastern Iranian",
            notes = "Perso-Arabic script with Pashto extensions",
            fallbackLanguageIds = listOf("persian", "baluchi"),
            sampleCharacters = "پښتو"
        ),
        AncientLanguage(
            id = "baluchi",
            canonicalName = "Baluchi",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic / Latin",
            familyBranch = "Northwestern Iranian",
            notes = "Historically oral, modern Arabic or Latin script",
            fallbackLanguageIds = listOf("persian", "afghan"),
            sampleCharacters = "بلوچی"
        ),
        AncientLanguage(
            id = "beja",
            canonicalName = "Beja",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic script",
            familyBranch = "Cushitic (Afroasiatic)",
            notes = "Historically unwritten Cushitic language",
            fallbackLanguageIds = listOf("bilin"),
            sampleCharacters = "بجا"
        ),
        AncientLanguage(
            id = "bilin",
            canonicalName = "Bilin",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Ge'ez script",
            familyBranch = "Cushitic (Agaw)",
            notes = "Cushitic language written in Ge'ez script",
            fallbackLanguageIds = listOf("beja"),
            sampleCharacters = "ብሊን"
        ),
        AncientLanguage(
            id = "bishari",
            canonicalName = "Bishari",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic script",
            familyBranch = "Cushitic (Beja group)",
            notes = "Closely related Beja group dialect",
            fallbackLanguageIds = listOf("beja"),
            sampleCharacters = "بشاري"
        ),
        AncientLanguage(
            id = "edomitish",
            canonicalName = "Edomitish",
            scriptTier = ScriptTier.TIER_4,
            scriptName = "Phoenician / Paleo-Hebrew",
            familyBranch = "Canaanite (Semitic)",
            notes = "Canaanite dialect close to Hebrew/Phoenician; handful of inscriptions",
            fallbackLanguageIds = listOf("hebrew", "moabitish", "phoenician"),
            sampleCharacters = "𐤀𐤃𐤌"
        ),
        AncientLanguage(
            id = "georgian",
            canonicalName = "Georgian",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Georgian (Asomtavruli / Mkhedruli)",
            familyBranch = "Kartvelian (isolate in catalog)",
            notes = "Old Georgian in Asomtavruli/Nuskhuri. Sole Kartvelian branch here.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "ქართული"
        ),
        AncientLanguage(
            id = "judeo_tunisian",
            canonicalName = "Judeo-Tunisian",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Hebrew square script",
            familyBranch = "Judeo-Arabic (Semitic)",
            notes = "Judeo-Arabic dialect traditionally written in Hebrew letters",
            fallbackLanguageIds = listOf("arabic", "hebrew"),
            sampleCharacters = "יהוד-ערבי"
        ),
        AncientLanguage(
            id = "mehri",
            canonicalName = "Mehri",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic / South Arabian",
            familyBranch = "Modern South Arabian (Semitic)",
            notes = "Modern South Arabian close to ancient epigraphic South Arabian",
            fallbackLanguageIds = listOf("south_arabian", "himyaritic"),
            sampleCharacters = "مهري"
        ),
        AncientLanguage(
            id = "moabitish",
            canonicalName = "Moabitish",
            scriptTier = ScriptTier.TIER_4,
            scriptName = "Phoenician script (Mesha Stele)",
            familyBranch = "Canaanite (Semitic)",
            notes = "Canaanite dialect, primarily the Mesha Stele corpus",
            fallbackLanguageIds = listOf("hebrew", "edomitish", "phoenician"),
            sampleCharacters = "𐤌𐤔𐤏"
        ),
        AncientLanguage(
            id = "norse",
            canonicalName = "Norse",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Runic (Younger Futhark) / Latin",
            familyBranch = "North Germanic",
            notes = "Old Norse: Runic for early inscriptions, Latin for manuscripts",
            fallbackLanguageIds = listOf("norse"),
            sampleCharacters = "ᚱᚢᚾᚨᛦ"
        ),
        AncientLanguage(
            id = "pali",
            canonicalName = "Pali",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Brahmi / Devanagari / Sinhala / Latin",
            familyBranch = "Middle Indo-Aryan",
            notes = "Theravada Buddhist canon language",
            fallbackLanguageIds = listOf("sanskrit"),
            sampleCharacters = "धम्म"
        ),
        AncientLanguage(
            id = "sinhalese",
            canonicalName = "Sinhalese",
            scriptTier = ScriptTier.TIER_1,
            scriptName = "Sinhala",
            familyBranch = "Insular Indo-Aryan",
            notes = "Epigraphic and Classical Sinhala script",
            fallbackLanguageIds = listOf("pali"),
            sampleCharacters = "සිංහල"
        ),
        AncientLanguage(
            id = "slavonic",
            canonicalName = "Slavonic (Old Church)",
            scriptTier = ScriptTier.TIER_2,
            scriptName = "Glagolitic / Early Cyrillic",
            familyBranch = "Slavic (Indo-European isolate in catalog)",
            notes = "Glagolitic in earliest manuscripts, Cyrillic later. Sole Slavic branch here.",
            fallbackLanguageIds = emptyList(),
            sampleCharacters = "ⰔⰎⰑⰂⰑ"
        ),
        AncientLanguage(
            id = "turki",
            canonicalName = "Turki (Chagatai)",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Perso-Arabic",
            familyBranch = "Karluk Turkic",
            notes = "Classical Chagatai / Central Asian literary Turkic",
            fallbackLanguageIds = listOf("turkish"),
            sampleCharacters = "تورکی"
        ),
        AncientLanguage(
            id = "turkish",
            canonicalName = "Turkish (Ottoman)",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Ottoman Turkish (Arabic script)",
            familyBranch = "Oghuz Turkic",
            notes = "Ottoman Turkish in Arabic script / Modern in Latin",
            fallbackLanguageIds = listOf("turki"),
            sampleCharacters = "عثمانلیجه"
        ),
        AncientLanguage(
            id = "umani",
            canonicalName = "Umani",
            scriptTier = ScriptTier.TIER_3,
            scriptName = "Arabic script",
            familyBranch = "Arabian Arabic Dialect",
            notes = "Omani Arabic dialect with historical maritime contact vocabulary",
            fallbackLanguageIds = listOf("arabic"),
            sampleCharacters = "عماني"
        )
    )

    private val languageMap: Map<String, AncientLanguage> by lazy {
        ALL_53_LANGUAGES.associateBy { it.id }
    }

    fun getById(id: String): AncientLanguage? = languageMap[id]

    fun getByName(name: String): AncientLanguage? {
        val lower = name.trim().lowercase()
        return ALL_53_LANGUAGES.firstOrNull {
            it.canonicalName.lowercase() == lower ||
            it.id == lower ||
            it.canonicalName.lowercase().contains(lower)
        }
    }

    fun getFallbacksFor(languageId: String): List<AncientLanguage> {
        val lang = getById(languageId) ?: return emptyList()
        return lang.fallbackLanguageIds.mapNotNull { getById(it) }
    }
}
