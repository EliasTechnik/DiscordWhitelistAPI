package dev.ewio.discordWhitelistAPI.api

import com.fasterxml.jackson.databind.SerializationFeature
import dev.ewio.discordWhitelistAPI.api.endpoints.WhitelistRequest
import dev.ewio.discordWhitelistAPI.service.WhitelistService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.plugins.origin
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import java.util.logging.Logger


class Engine(
    val apiPort: Int,
    val apiKey: String,
    val service: WhitelistService,
    private val allowedIps: Set<String> = setOf("127.0.0.1", "::1"),
    val logger: Logger
){
    private var serverEngine: NettyApplicationEngine? = null

    init{
        serverEngine = embeddedServer(Netty, environment = applicationEngineEnvironment {
            log = org.slf4j.helpers.NOPLogger.NOP_LOGGER

            connector {
                // nur localhost!
                host = "0.0.0.0"
                port = apiPort
            }
            module {
                // IP-Whitelist vor jeglicher Routenverarbeitung prüfen
                intercept(ApplicationCallPipeline.Plugins) {
                    val remote = call.request.origin.remoteHost
                    if (allowedIps.isNotEmpty() && remote !in allowedIps) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "forbidden"))
                        logger.info("Blocked API request from unauthorized IP: $remote")
                        finish()
                    }
                }

                install(ContentNegotiation) {
                    jackson {
                        configure(SerializationFeature.INDENT_OUTPUT, true)
                    }
                }
                routing {
                    post("/whitelist/add") {
                        if (!checkApiKey(call)) return@post
                        val req = call.receive<WhitelistRequest>()
                        val name = req.name
                        if (!service.isPlayerNameValid(name)) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "invalid name"))
                            return@post
                        }

                        service.addPlayerToWhitelist(name)
                        logger.info("Added player '$name' to whitelist via API.")

                        call.respond(mapOf("status" to "ok"))
                    }

                    post("/whitelist/remove") {
                        if (!checkApiKey(call)) return@post
                        val req = call.receive<WhitelistRequest>()
                        val name = req.name
                        if (!service.isPlayerNameValid(name)) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "invalid name"))
                            return@post
                        }

                        service.removePlayerFromWhitelist(name)
                        logger.info("Removed player '$name' from whitelist via API.")

                        call.respond(mapOf("status" to "ok"))
                    }

                    get("/whitelist/{name}") {
                        if (!checkApiKey(call)) return@get
                        val name = call.parameters["name"]!!
                        if (!service.isPlayerNameValid(name)) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "invalid name"))
                            return@get
                        }

                        call.respond(mapOf(
                            "name" to name,
                            "whitelisted" to service.isPlayerWhitelisted(name)
                        ))
                    }
                }
            }
        }).start(wait = false)
    }

    fun stop(){
        serverEngine?.stop()
    }

    fun isRunning(): Boolean {
        return serverEngine != null
    }

    private suspend fun checkApiKey(call: ApplicationCall): Boolean {
        val providedKey = call.request.headers["X-API-KEY"]
        if (providedKey == apiKey && apiKey != "") {
            return true
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "invalid API key"))
            return false
        }
    }

}