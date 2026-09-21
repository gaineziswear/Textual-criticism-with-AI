package com.example.model

data class PhilologicalCandidateSpan(
    val startToken: Int,
    val endToken: Int,
    val surface: String,
    val normalized: String,
    val reason: String,
    val priority: Int
)
