package com.example.agent

import com.example.model.ScribalAnomaly
import com.example.model.ScholarlyCertainty
import com.example.model.SemiticCognate
import com.example.model.TextualCriticReport
import com.example.model.WordGloss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SemiticTextualCriticAgent {

    private val semiticLanguageIds = setOf(
        "syriac", "aramaic", "hebrew", "phoenician", "nabatean", "palmyrene",
        "ugaritic", "akkadian", "babylonian", "assyrian", "arabic", "ethiopic",
        "mandaean", "south_arabian", "himyaritic", "thamudic", "lihyanite",
        "edomitish", "moabitish", "judeo_tunisian", "mehri", "umani"
    )

    fun isApplicableTo(languageId: String?): Boolean {
        if (languageId == null) return false
        return languageId in semiticLanguageIds
    }

    suspend fun analyzeTextCritical(
        wordGloss: WordGloss,
        contextWords: List<WordGloss>
    ): TextualCriticReport = withContext(Dispatchers.Default) {
        val token = wordGloss.cleanedToken
        val langId = wordGloss.selectedLanguage?.id ?: "unknown"

        val anomalies = mutableListOf<ScribalAnomaly>()
        val cognates = mutableListOf<SemiticCognate>()
        var certainty = ScholarlyCertainty.SCHOLARLY_CONSENSUS

        // 1. Paleographic & Scribal Error Detection
        // Check for Visual Confusability
        if (token.contains('ד') || token.contains('ר')) {
            val alt = token.replace('ד', 'ר').replace("ר", "ד")
            anomalies.add(
                ScribalAnomaly(
                    errorType = "Paleographic Confusability (ד / ר)",
                    description = "In square Aramaic and Hebrew scripts, Dalet (ד) and Resh (ר) differ only by the right-angle corner versus rounded shoulder, a primary source of scribal misreading.",
                    possibleAlternativeReading = alt,
                    plausibility = "Attested Scribal Pattern"
                )
            )
        }
        if (token.contains('ו') || token.contains('י')) {
            val alt = token.replace('ו', 'י')
            anomalies.add(
                ScribalAnomaly(
                    errorType = "Paleographic Confusability (ו / י)",
                    description = "In Qumran (DSS) and Second Temple scripts, Waw (ו) and Yod (י) are frequently indistinguishable in ductus and stroke length.",
                    possibleAlternativeReading = alt,
                    plausibility = "Frequent Scribal Variation"
                )
            )
        }
        if (token.contains('ܒ') || token.contains('ܟ')) {
            anomalies.add(
                ScribalAnomaly(
                    errorType = "Syriac Paleographic Confusability (ܒ / ܟ)",
                    description = "In Estrangela and Serto scripts, Beth (ܒ) and Kaph (ܟ) share lower horizontal bases and can be confused in damaged codices.",
                    possibleAlternativeReading = token.replace('ܒ', 'ܟ'),
                    plausibility = "Plausible in damaged Vorlage"
                )
            )
        }

        // Check for Dittography / Haplography
        for (i in 0 until token.length - 1) {
            if (token[i] == token[i + 1]) {
                anomalies.add(
                    ScribalAnomaly(
                        errorType = "Potential Dittography",
                        description = "Adjacent identical graphemes '${token[i]}${token[i]}' may indicate accidental scribal duplication of the letter.",
                        possibleAlternativeReading = token.removeRange(i, i + 1),
                        plausibility = "Plausible"
                    )
                )
                break
            }
        }

        // Check context for Homoioteleuton (skipping between identical endings)
        val prevWord = contextWords.getOrNull(wordGloss.index - 1)
        val nextWord = contextWords.getOrNull(wordGloss.index + 1)
        if (prevWord != null && prevWord.cleanedToken.takeLast(2) == token.takeLast(2)) {
            anomalies.add(
                ScribalAnomaly(
                    errorType = "Potential Parablepsis / Homoioteleuton Environment",
                    description = "Preceding token #${prevWord.index + 1} and current token share the suffix ending '${token.takeLast(2)}', creating conditions where a copyist's eye could skip clauses.",
                    possibleAlternativeReading = "Check corresponding parallel witnesses for omitted lines",
                    plausibility = "Attested Scribal Hazard"
                )
            )
        }

        // 2. Comparative Semitic Sound Laws & Cognates
        when {
            token.contains("שלם") || token.contains("ܫܠܡ") || token.contains("𐤔𐤋𐤌") || token.contains("𐩪𐩡𐩣") -> {
                cognates.add(SemiticCognate("Proto-Semitic", "*š-l-m", "Intactness, peace, restitution", "*š-l-m"))
                cognates.add(SemiticCognate("Biblical Hebrew", "שָׁלוֹם (šālôm)", "Peace, wholeness", "*š-l-m"))
                cognates.add(SemiticCognate("Imperial Aramaic", "שְׁלָמָא (šəlāmā)", "Peace, greetings", "*š-l-m"))
                cognates.add(SemiticCognate("Classical Syriac", "ܫܠܡܐ (šlāmā)", "Peace, salute", "*š-l-m"))
                cognates.add(SemiticCognate("Ugaritic", "𐎌𐎍𐎎 (šlm)", "Soundness, tribute", "*š-l-m"))
                cognates.add(SemiticCognate("Ancient South Arabian", "𐩪𐩡𐩣 (slm)", "Security, submission", "*š-l-m"))
                cognates.add(SemiticCognate("Classical Arabic", "سَلَام (salām)", "Peace, safety", "*š-l-m"))
                cognates.add(SemiticCognate("Akkadian", "šalāmu", "To be intact, well", "*š-l-m"))
                certainty = ScholarlyCertainty.ESTABLISHED_FACT
            }
            token.contains("מלך") || token.contains("ܡܠܟ") || token.contains("𐤌𐤋𐤊") || token.contains("𐎎𐎍𐎋") || token.contains("𐩣𐩡𐩫") -> {
                cognates.add(SemiticCognate("Proto-Semitic", "*m-l-k", "To rule, possess sovereignty", "*m-l-k"))
                cognates.add(SemiticCognate("Biblical Hebrew", "מֶלֶךְ (melek)", "King, ruler", "*m-l-k"))
                cognates.add(SemiticCognate("Aramaic / Syriac", "מַלְכָּא / ܫܠܡܐ", "The king, sovereign", "*m-l-k"))
                cognates.add(SemiticCognate("Ugaritic", "𐎎𐎍𐎋 (mlk)", "King, deity epithet", "*m-l-k"))
                cognates.add(SemiticCognate("Phoenician / Moabite", "𐤌𐤋𐤊 (mlk)", "King (Mesha inscription)", "*m-l-k"))
                cognates.add(SemiticCognate("Classical Arabic", "مَلِك (malik)", "Monarch, sovereign", "*m-l-k"))
                cognates.add(SemiticCognate("Ge'ez", "መልእክ (mal'ək)", "Messenger / ruler agent", "*m-l-k"))
                certainty = ScholarlyCertainty.ESTABLISHED_FACT
            }
            token.contains("אלה") || token.contains("ܐܠܗ") || token.contains("𐎛𐎍") || token.contains("𐤀𐤋") -> {
                cognates.add(SemiticCognate("Proto-Semitic", "*'-l(-h)", "Deity, god", "*'-l"))
                cognates.add(SemiticCognate("Biblical Hebrew", "אֱלֹהִים / אֵל", "God, deities", "*'-l"))
                cognates.add(SemiticCognate("Aramaic / Syriac", "אֱלָהָא / ܐܠܗܐ", "God, the Lord", "*'-l"))
                cognates.add(SemiticCognate("Ugaritic", "𐎛𐎍 ('il)", "El, supreme god of the pantheon", "*'-l"))
                cognates.add(SemiticCognate("Phoenician", "𐤀𐤋 ('l)", "God, divine entity", "*'-l"))
                cognates.add(SemiticCognate("Classical Arabic", "إِلٰه / اَللّٰه ('ilāh / Allāh)", "Deity / The God", "*'-l"))
                cognates.add(SemiticCognate("Akkadian", "ilu / iltum", "God / Goddess", "*'-l"))
                certainty = ScholarlyCertainty.ESTABLISHED_FACT
            }
            else -> {
                cognates.add(
                    SemiticCognate(
                        language = "Comparative Semitic Analysis",
                        form = token,
                        meaning = "Triconsonantal root morphology under evaluation",
                        protoSemiticRoot = "*-*-*"
                    )
                )
            }
        }

        val commentary = buildString {
            appendLine("### Textual-Critic Examination: 「${wordGloss.rawToken}」")
            appendLine("- **Language Branch**: ${wordGloss.selectedLanguage?.canonicalName ?: "Semitic continuum"}")
            appendLine("- **Transmission Status**: **${certainty.label}**")
            appendLine("- **Attested Anomalies**: ${if (anomalies.isEmpty()) "No paleographic defects detected in isolated token" else "${anomalies.size} scribal considerations identified"}")
            appendLine()
            appendLine("#### Philological Delimitation:")
            appendLine("This analysis is strictly confined to textual transmission, Vorlage recensions, and graphemic variations. It makes no theological assertions and reports minority variants as descriptive scholarly hypotheses.")
        }

        TextualCriticReport(
            wordIndex = wordGloss.index,
            token = token,
            primaryReading = wordGloss.literalGlossEn,
            potentialErrors = anomalies,
            comparativeRoots = cognates,
            transmissionStatus = certainty,
            criticCommentary = commentary
        )
    }
}
