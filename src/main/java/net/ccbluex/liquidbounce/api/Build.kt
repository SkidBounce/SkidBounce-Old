/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.api

import com.google.gson.annotations.SerializedName

/**
 * Data classes for the API
 */
data class Build(
    @SerializedName("build_id")
    val buildId: Int,
    @SerializedName("commit_id")
    val commitId: String,
    val branch: String,
    @SerializedName("lb_version")
    val lbVersion: String,
    @SerializedName("mc_version")
    val mcVersion: String,
    val release: Boolean,
    val date: String,
    val message: String,
    val url: String
)
