package dev.ewio.discordWhitelistAPI.service

import dev.ewio.discordWhitelistAPI.DiscordWhitelistAPI
import org.bukkit.Bukkit

class WhitelistService(
    val plugin: DiscordWhitelistAPI
) {

    fun isPlayerNameValid(name: String): Boolean {
        val regex = "^[a-zA-Z0-9_]{3,16}$".toRegex()
        return regex.matches(name)
    }

    fun addPlayerToWhitelist(name: String) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val offlinePlayer = Bukkit.getOfflinePlayer(name)
            offlinePlayer.isWhitelisted = true

        })
    }

    fun removePlayerFromWhitelist(name: String) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val offlinePlayer = Bukkit.getOfflinePlayer(name)
            offlinePlayer.isWhitelisted = false
        })
    }

    fun isPlayerWhitelisted(name: String): Boolean {
        val offlinePlayer = Bukkit.getOfflinePlayer(name)
        return offlinePlayer.isWhitelisted
    }

}