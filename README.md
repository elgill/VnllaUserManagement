# VnllaUserManagement

VnllaUserManagement is a Minecraft plugin designed to manage player information, stats, and various administrative tasks.

## Features

* Display player stats
* Check information about a player
* Teleport to a player's last known location
* Give a player a vote
* Change a player's group
* Alt detection
* And more!

## Commands

| Command       | Description                                          | Usage                            | Permission                  |
|---------------|------------------------------------------------------|----------------------------------|-----------------------------|
| /stats        | Displays player stats to player                      | /stats                           |                             |
| /status       | Displays info about a player in chat to the executor | /status [player]                 | VnllaPlayerInfo.seestatus   |
| /statusip     | Displays accounts associated with a given IP         | /statusip [ip]                   | VnllaPlayerInfo.seestatusip |
| /lastlocation | Teleports to a player's last known location          | /lastlocation [uuid]             | VnllaPlayerInfo.seestatus   |
| /givevote     | Gives a player a vote                                | /givevote [uuid]                 | VnllaPlayerInfo.givevote    |
| /forge        | Adds colored title and lore to an item               | /forge [color] "[name]" "[lore]" | VnllaPlayerInfo.seestatus   |
| /group        | Changes a player's group                             | /group [player] [group]          | VnllaPlayerInfo.group       |
| /wipeip       | Wipes all IPs from a user in the alt database        | /wipeip [player]                 | VnllaPlayerInfo.wipeip      |

## Permissions

| Permission                   | Description                                                                                       | Default |
|------------------------------|---------------------------------------------------------------------------------------------------|---------|
| VnllaPlayerInfo.wipeip       | Allows user to wipe a player's stored IP from the alt detector database                           | op      |
| VnllaPlayerInfo.seestatus    | Allows user to use /status, displaying IPs, UUID, and ban/alt information if the player is banned | op      |
| VnllaPlayerInfo.seestatusip  | Allows user to use /statusip, returning all users associated with the specified IP address        | false   |
| VnllaPlayerInfo.seestatusalt | Allows user to see known alts in /status, regardless of whether the user is banned                | false   |
| VnllaPlayerInfo.givevote     | Allows to give a vote to a player                                                                 | op      |
| VnllaPlayerInfo.group        | Allows to change player group                                                                     | false   |

## Installation

1. Download the latest release of the VnllaUserManagement plugin.
2. Place the downloaded .jar file into your server's `plugins` folder.
3. Restart your server to load the plugin.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE.md) file for details
