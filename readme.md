# DiscordWhitelistAPI

This simple plugin provides an API for whitelisting players through a simple HTTP interface which could be connected to a Discord bot.

# Where to get it
You can download the latest version of the plugin from the [Releases](https://github.com/EliasTechnik/DiscordWhitelistAPI/releases) page.

# How it works
The plugin starts a simple HTTP server on a configurable port. The server listens for incoming requests and processes them. To increase security only requests with a valid API key and from localhost are accepted. The API-Key is generated on the first start of the plugin and can be found in the config file. To change the API-Key, just edit the config file and restart the server. If the API-Key is blank a new one will be generated on startup. For the plugin to function properly the whitelist must be enabled on the server.

# API
The API provides three endpoints:
- `/whitelist/add` - Adds a player to the whitelist. Requires a POST request with  the player's name.
- `/whitelist/remove` - Removes a player from the whitelist. Requires a POST request with the player's name.
- `/whitelist/{player}` - Checks if a player is whitelisted. Requires ```{player}``` to be replaced with the player's name.

Each request must contain the API-Key in the header as `X-API-KEY`.

# Testing the API
You can use the `testAPI.py` script provided in the repository to test the API. Make sure to update the `API_KEY` variable in the script with the API key from your config file before running it.
