/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.api

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.file.FileManager.PRETTY_GSON
import net.ccbluex.liquidbounce.utils.misc.HttpUtils.post

import net.ccbluex.liquidbounce.utils.misc.HttpUtils.request
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import org.apache.http.HttpEntity

/**
 * LiquidBounce Client API
 *
 * This represents all API endpoints of the LiquidBounce API for the usage on the client.
 */
object ClientApi {

    /**
     * For many people the SSL certificate is not being accepted because of outdated Java or odd computer settings.
     * This is why we use a non-SSL endpoint since we don't handle any sensitive data.
     *
     * DO NOT CHANGE THIS ENDPOINT TO SSL.
     */
    private const val API_ENDPOINT = "http://nossl.api.liquidbounce.net/api/v1"

    /**
     * This makes sense because we want forks to be able to use this API and not only the official client.
     * It also allows us to use API endpoints for legacy on other branches.
     */
    private const val HARD_CODED_BRANCH = "legacy"

    fun requestNewestBuildEndpoint(branch: String = HARD_CODED_BRANCH, release: Boolean = false) = endpointRequest<Build>("version/newest/$branch${if (release) "/release" else "" }")

    /**
     * Request endpoint and parse JSON to data class
     */
    private inline fun <reified T> endpointRequest(endpoint: String): T = parse(textEndpointRequest(endpoint))

    /**
     * Parse JSON to data class
     */
    private inline fun <reified T> parse(json: String): T = PRETTY_GSON.fromJson(json, T::class.java)

    /**
     * User agent
     * LiquidBounce/<version> (<commit>, <branch>, <build-type>, <platform>)
     */
    private val ENDPOINT_AGENT = "${LiquidBounce.CLIENT_NAME}/${LiquidBounce.clientVersionText} (${LiquidBounce.clientCommit}, ${LiquidBounce.clientBranch}, ${if (LiquidBounce.IN_DEV) "dev" else "release"}, ${System.getProperty("os.name")})"

    /**
     * Session token
     *
     * This is used to identify the client in one session
     */
    private val SESSION_TOKEN = RandomUtils.randomString(16)

    /**
     * Request to endpoint with custom agent and session token
     */
    private fun textEndpointRequest(endpoint: String): String {
        val (response, code) = request(
            "$API_ENDPOINT/$endpoint",
            method = "GET",
            agent = ENDPOINT_AGENT,
            headers = arrayOf("X-Session-Token" to SESSION_TOKEN)
        )

        if (code == 200) return response else error(response)
    }

    private fun textEndpointPost(endpoint: String, entity: () -> HttpEntity) = post(
        "$API_ENDPOINT/$endpoint",
        agent = ENDPOINT_AGENT,
        headers = arrayOf("X-Session-Token" to SESSION_TOKEN),
        entity = entity
    )
}
