package com.pixelized.ordiscord.model.world

data class Alert(
        val activation: Long,
        val expiry: Long,
        val missionType: String,
        val faction: String,
        val minEnemyLevel: String,
        val maxEnemyLevel: String,
        val credits: Int,
        val reward: List<String> = listOf()
)