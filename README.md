# Coordinate Manager

A Fabric-based Minecraft mod for capturing, organizing, and managing coordinates.

<a target="_blank" href="https://modrinth.com/mod/coordinate-manager"><img alt="modrinth" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg"></a>

## Features
- **Capture Modes:**
   - **Instant**: Save coordinate immediately without prompts.
   - **Pop-up**: Show a dialog to set a name and adjust coordinate values before saving.
- **Capture Cooldown**: Prevent spam by setting a cooldown (below `250ms` disables it).
- **Coordinate Thumbnail**: Save a thumbnail of your current view when capturing a coordinate (disabled by default).
- **Precise Coordinates**: Saves coordinate values as decimals for accuracy (disabled by default).
- **Auto-Copy on Capture**: Automatically copies the saved coordinates to the clipboard when capturing a coordinate (disabled by default).
- **Coordinate History Screen**: View, copy, edit, and manage saved coordinates in-game.
- **Copy Coordinates**: Copy any coordinate entry to clipboard for easy sharing.
- **Set Favorited Coordinates**: Protect favorited coordinates from accidental deletion.
- **Search Functionality**: Find saved coordinates by name or coordinate values.
- **Bulk Actions**:
   - **Favorite**: Toggle favorite status for multiple entries.
   - **Delete**: Remove multiple selected coordinates at once.
- **Delete Options**:
  - **Delete Individual Coordinates**: Remove specific entries.
  - **Clear World/Server Coordinates**: Delete all unfavorited coordinates from the current world or server.

## Keybindings

Customizable via the in-game controls menu.

- **Capture Coordinate**: `[F9]` Save the current position.
- **Open History**: `[F10]` Open coordinate history screen.

## Configuration

Configurable via [Cloth Config API](https://modrinth.com/mod/cloth-config).

| Setting | Description | Default |
| - | - | - |
| Capture Mode | Determines whether coordinates are saved instantly or with a pop-up for editing. | Instant |
| Enable Thumbnail | Captures a thumbnail when saving a coordinate. | Disabled |
| Auto-Copy on Capture | Automatically copies the saved coordinates to the clipboard upon capture. | Disabled |
| Capture Cooldown | Adds a delay between captures to prevent spam (set below 250ms to disable). | 250ms |
| Precise Coordinate | Saves coordinates with decimal accuracy for better precision. | Disabled |

## TODO
- Improve accessibility (e.g., narration).
- Unique identifier for world/server (Currently relying on world names and server addresses).
- Support more languages? [PRs](https://github.com/AnOrdinaryPeople/coordinate-manager/pulls) welcome.
- Implement CI/CD?
- Add support for more loaders?
- Support Minecraft versions below **1.21.4**?
- Modify a very slight unaligned search and panels? Might be major issue if users have OCD /s
- Refactor codebase structure for best practices? ¯\\\_(ツ)\_/¯
