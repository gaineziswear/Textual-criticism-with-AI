package com.example.data

data class SampleManuscript(
    val title: String,
    val tradition: String,
    val scriptName: String,
    val originalText: String,
    val description: String,
    val recommendedTestWord: String = ""
)

object SampleManuscripts {
    val ALL_SAMPLES = listOf(
        SampleManuscript(
            title = "Dead Sea Scrolls / Biblical Hebrew",
            tradition = "Northwest Semitic",
            scriptName = "Hebrew Square Script",
            originalText = "בראשית ברא אלהים את השמים ואת הארץ",
            description = "Genesis 1:1 in classical Tiberian / DSS square orthography.",
            recommendedTestWord = "בראשית"
        ),
        SampleManuscript(
            title = "Syriac Peshitta (Gospel of John 1:1)",
            tradition = "Aramaic Continuum",
            scriptName = "Syriac Estrangela Script",
            originalText = "ܒܪܫܝܬ ܐܝܬܘܗܝ ܗܘܐ ܡܠܬܐ ܘܗܘ ܡܠܬܐ ܐܝܬܘܗܝ ܗܘܐ ܠܘܬ ܐܠܗܐ",
            description = "Classical Syriac text from the 5th-century Peshitta manuscript tradition.",
            recommendedTestWord = "ܡܠܬܐ"
        ),
        SampleManuscript(
            title = "Mesha Stele (Moabite Inscription)",
            tradition = "Canaanite (Tier 4 Epigraphic)",
            scriptName = "Phoenician / Paleo-Hebrew Script",
            originalText = "אנך משע בן כמשית מלך מאב",
            description = "9th century BCE victory monument of King Mesha of Moab. Tests Tier-4 Canaanite dialect classification.",
            recommendedTestWord = "משע"
        ),
        SampleManuscript(
            title = "Ugaritic Baal Cycle Tablet (KTU 1.2)",
            tradition = "Northwest Semitic Cuneiform",
            scriptName = "Ugaritic Alphabetic Cuneiform",
            originalText = "𐎁𐎓𐎍 𐎛𐎍 𐎎𐎍𐎋 𐎌𐎍𐎎",
            description = "Ras Shamra mythological tablet in Ugaritic 30-sign alphabetic cuneiform.",
            recommendedTestWord = "𐎁𐎓𐎍"
        ),
        SampleManuscript(
            title = "Sabaean Royal Inscription (Musnad)",
            tradition = "Old South Arabian",
            scriptName = "Ancient South Arabian (Musnad)",
            originalText = "𐩪𐩡𐩣 𐩣𐩡𐩫 𐩪𐩨𐩱 𐩥𐩵𐩧𐩺𐩵𐩬",
            description = "Musnad inscription from Marib Kingdom of Saba and Dhu Raydan.",
            recommendedTestWord = "𐩪𐩡𐩣"
        ),
        SampleManuscript(
            title = "Classical Greek Codex Sinaiticus",
            tradition = "Hellenic (Catalog Isolate)",
            scriptName = "Greek Uncial Script",
            originalText = "Ἐν ἀρχῇ ἦν ὁ λόγος καὶ ὁ λόγος ἦν πρὸς τὸν θεόν",
            description = "4th-century uncial manuscript witness in Koine Greek.",
            recommendedTestWord = "λόγος"
        ),
        SampleManuscript(
            title = "Coptic Gnostic Codex (Nag Hammadi)",
            tradition = "Late Egyptian",
            scriptName = "Coptic Script",
            originalText = "ⲛⲟⲩⲧⲉ ⲛⲧⲉ ⲡⲟⲩⲟⲉⲓⲛ ⲙⲛ ⲧⲙⲉ",
            description = "Sahidic Coptic manuscript fragment testing Egyptian root fallback.",
            recommendedTestWord = "ⲛⲟⲩⲧⲉ"
        ),
        SampleManuscript(
            title = "Sanskrit Vedic Taittiriya",
            tradition = "Indo-Aryan",
            scriptName = "Devanagari Script",
            originalText = "सत्यं वद धर्मं चर स्वाध्यायान्मा प्रमदः",
            description = "Classical Sanskrit moral injunctions testing Sanskrit-Pali relations.",
            recommendedTestWord = "धर्मं"
        ),
        SampleManuscript(
            title = "Akkadian Royal Inscription of Ashurbanipal",
            tradition = "East Semitic Cuneiform",
            scriptName = "Mesopotamian Cuneiform",
            originalText = "𒀭 𒈗 𒀀𒈾 𒁕 𒊑 𒅖",
            description = "Mesopotamian cuneiform royal monument testing Akkadian-Babylonian-Assyrian cluster.",
            recommendedTestWord = "𒈗"
        )
    )
}
