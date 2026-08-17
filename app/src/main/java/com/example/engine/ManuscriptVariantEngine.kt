package com.example.engine

import com.example.model.AgentTraceStep
import com.example.model.Confidence
import com.example.model.ManuscriptComparisonResult
import com.example.model.ManuscriptWitnessInput
import com.example.model.TraceStatus
import com.example.model.VariantType
import com.example.model.VariantUnit

object ManuscriptVariantEngine {
    fun compare(witnesses: List<ManuscriptWitnessInput>): ManuscriptComparisonResult {
        require(witnesses.size >= 2) { "At least two witnesses are required for variant comparison." }
        val tokenized = witnesses.associate { it.siglum to LinguisticPipeline.tokenize(it.text) }
        val max = tokenized.values.maxOfOrNull { it.size } ?: 0
        val units = (0 until max).map { pos ->
            val readings = witnesses.associate { it.siglum to tokenized[it.siglum]?.getOrNull(pos) }
            val present = readings.values.filterNotNull()
            val distinct = present.toSet()
            val type = classify(readings)
            VariantUnit(
                position = pos,
                readingsByWitness = readings,
                variantType = type,
                possibleScribalPhenomena = phenomenaFor(type, present),
                significanceScore = score(type, readings),
                confidence = if (type == VariantType.AGREEMENT) Confidence.HIGH else Confidence.MEDIUM
            )
        }
        val variantCount = units.count { it.variantType != VariantType.AGREEMENT }
        return ManuscriptComparisonResult(
            witnesses = witnesses,
            alignedUnits = units,
            assessment = if (variantCount == 0) {
                "No variation detected in simple word alignment. This is not proof of originality."
            } else {
                "$variantCount variant unit(s) detected. Scribal explanations are hypotheses, not automatic corrections."
            },
            trace = listOf(
                AgentTraceStep("Witnesses tokenized", TraceStatus.COMPLETE, "${witnesses.size} witnesses aligned by word position."),
                AgentTraceStep("Variants classified", TraceStatus.COMPLETE, "Detected additions, omissions, substitutions, and possible orthographic variation."),
                AgentTraceStep("Originality not inferred", TraceStatus.COMPLETE, "Significance score is not presented as proof of earliest reading.")
            )
        )
    }

    private fun classify(readings: Map<String, String?>): VariantType {
        val values = readings.values
        if (values.any { it == null } && values.any { it != null }) return VariantType.OMISSION
        val present = values.filterNotNull()
        if (present.toSet().size <= 1) return VariantType.AGREEMENT
        if (present.map { normalizeMatres(it) }.toSet().size == 1) return VariantType.ORTHOGRAPHIC
        return VariantType.SUBSTITUTION
    }

    private fun normalizeMatres(reading: String): String = reading.replace("ו", "").replace("י", "")

    private fun phenomenaFor(type: VariantType, readings: List<String>): List<String> = when (type) {
        VariantType.OMISSION -> listOf("possible omission", "possible haplography", "possible homoioteleuton/homoeoarcton if endings/beginnings repeat")
        VariantType.ADDITION -> listOf("possible interpolation", "possible marginal gloss incorporation", "possible expansion")
        VariantType.SUBSTITUTION -> listOf("possible lexical variant", "possible phonetic substitution", "possible visually similar characters")
        VariantType.TRANSPOSITION -> listOf("possible transposition", "possible harmonization")
        VariantType.ORTHOGRAPHIC -> listOf("possible spelling normalization", "possible matres lectionis variation")
        VariantType.AGREEMENT -> emptyList()
    } + if (readings.any { hasRepeatedAdjacent(it) }) listOf("possible dittography environment") else emptyList()

    private fun hasRepeatedAdjacent(reading: String): Boolean = reading.zipWithNext().any { it.first == it.second }

    private fun score(type: VariantType, readings: Map<String, String?>): Int = when (type) {
        VariantType.AGREEMENT -> 0
        VariantType.ORTHOGRAPHIC -> 20
        VariantType.OMISSION, VariantType.ADDITION -> 65
        VariantType.SUBSTITUTION -> 75
        VariantType.TRANSPOSITION -> 80
    }.coerceAtMost(100)
}
