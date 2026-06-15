package com.tradinganalytics.patterns.library

data class PatternDefinition(
    val patternId: String,
    val name: String,
    val category: String,
    val description: String,
    val detectionRules: DetectionRules,
    val confidenceFormula: String,
    val riskRating: String
)

data class DetectionRules(
    val minSequenceLength: Int,
    val requiredConditions: List<String>,
    val weightage: Map<String, Double>
)
