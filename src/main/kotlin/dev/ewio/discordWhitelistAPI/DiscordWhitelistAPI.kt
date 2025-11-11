package dev.ewio.discordWhitelistAPI

import dev.ewio.discordWhitelistAPI.api.Engine
import dev.ewio.discordWhitelistAPI.service.WhitelistService
import org.bukkit.plugin.java.JavaPlugin

class DiscordWhitelistAPI : JavaPlugin() {

    val service = WhitelistService(this)
    lateinit var engine: Engine

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()

        val apiPort = config.getInt("port", 8080)
        val apiKey = config.getString("api-key") ?: ""
        val allowedIpList: Set<String> = config.getStringList("allowed-ips").ifEmpty { listOf("127.0.0.1", "::1") }.toSet()

        if(apiKey.isBlank()) {
            val newApiKey = generateApiKey()
            config.set("api-key", newApiKey)
            saveConfig()
            logger.info("Generated new API key: $newApiKey")
        }

        engine = Engine(
            apiPort = apiPort,
            apiKey = config.getString("api-key")!!,
            service = service,
            allowedIps = allowedIpList,
            logger = logger
        )

        if(engine.isRunning()){
            logger.info("Discord Whitelist API is running and listening at http://127.0.0.1:$apiPort (Allowed IPs: ${allowedIpList.joinToString(",")})")
        } else {
            logger.severe("Failed to start Discord Whitelist API on port $apiPort")
        }

        //check if whitelist is enabled
        if(!server.hasWhitelist()) {
            logger.warning("Server whitelist is disabled! In order for this plugin to function you need to enable it in server.properties.")
        }
    }

    private fun generateApiKey(): String {
        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..64)
            .map { charset.random() }
            .joinToString("")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        engine.stop()
    }
}
