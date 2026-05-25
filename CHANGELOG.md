# Changelog

All notable changes to this project will be documented in this file.

## [1.1.0] – The Item Update (2026-05-25)

### Added
- New expression: `[the ]mythic[ ]type of %itemstack%` — retrieves the MythicMobs type name of an item.
- New expression: `[the ]amount of mythic[ ]item %string% (in|of) %player%['s inventory]` — counts how many of a specific mythic item a player has.
- New effect: `give mythic[ ]item %string% [with amount %-number%] to %players%` — gives a mythic item to one or more players (drops leftovers if inventory is full).
- New effect: `drop mythic[ ]item %string% [with amount %-number%] at %locations%` — drops a mythic item at one or more locations.
- New effect: `take [amount %-number%] mythic[ ]item %string% from %players%` — removes a specific mythic item from player inventories.
- New condition: `%itemstack% is[n't] [a ]mythic[ ]item %string%` — checks if an item matches a specific mythic type.
- New condition: `%player% (has|does(n't| not) have) mythic[ ]item %string% [with amount %-number%]` — checks if a player has enough of a specific mythic item.
- Negation support (`isn't`, `does not have`) for all mythic item conditions.
- Metrics: Added anonymous plugin tracking via [bStats](https://bstats.org/plugin/bukkit/MythicSkriptAddon/31569) to help monitor plugin usage and statistics.

### Changed
- `ItemStackisMythicItem` migrated from legacy NBT access to `Utils.itemManager`, improved null safety.
- Introduced `MythicItemHelper` utility class to centralize item generation and amount parsing.
- Added `Skript.warning()` when an unknown MythicItem name is used in effects.

## [1.0.0] – The Initial Release (2026-05-22 / 2026-05-23)

### Added / Changed

* **Global Refactoring:** The code has been completely rewritten to support the new Skript arguments API (2.14+ / 2.15+).
* **Dependency Updates:** Project updated to Java 21. Added support for Paper 1.21.x, MythicMobs 5.12.0, and Skript 2.15.2.
* Improved type safety checks for arguments (`SkillMetadata`, `Location`, `Entity`) in the `SkriptfunctionMechanic`.
* Fixed potential `ArrayIndexOutOfBoundsException` bugs in condition classes (e.g., `CompareEntityLocationCondition`).
* Fixed an argument overwriting bug in the `ItemDrop` class.
* Created a [wiki page](https://github.com/Andromedov/MythicSkriptAddon/wiki) for the plugin.

## [0.99.7] – 2024-04-12

### Fixed

* Fixed the `displayname of activemob` expression.

### Added

* New effect: `set display of [activemob] %activemob% to %string%` to change a mob's display name.

## [0.99.6] – 2024-02-29

### Fixed

* Fixed potential `NullPointerException`s in `MythicItem` expressions.
* Target Java version was temporarily downgraded to 16 for older server compatibility.

### Added

* New class: `mythicplayer`.

## [0.99.5] – 2024-02-09

### Changed

* Compiled with Skript 2.8.2.

### Added

* Condition: `%itemstack% is a mythicitem`.
* Expressions: `[create ]itemstack for mythicitem [named ]%string%`, `[get ]mythicitem [for ]name %string%`, `[get ]itemstack for %mythicitem%`.
* New event-value for the `on mythicmob lootdrop` event: `event-lootbag`.

## [0.99.4] – 2024-01-31

### Fixed

* Fixed `NPE` (NullPointerException) in all custom mechanics.

## [0.99.3] – 2024-01-27

### Changed

* Updated to work with MythicMobs 5.5.1.

## Older Updates (Archive)

* **[0.99.2]**: Updated to work with MythicMobs 5.0.2.
* **[0.99.g]**: Fixed compatibility issues with MythicMobs 4.12.0 and Skript 2.8.0.
* **[0.99.e]**: Added the `mythicitem` class and the `itemstack of mythicitem %string%` expression.
* **[0.99.d]**: Added support for `drop skriptfunction` (including the `dropdata` class, effects, and expressions for drop metadata).
* **[0.99.a]**: Added the `on mythicmob lootdrop` event and the `lootbag` class. Updated to MythicMobs 4.11.0. Added the `skriptfunction` mechanic.
* **[0.93.a]**: Target selectors now support custom targeters (`customtargeters`).
* **[0.89.a]**: Added the `convert %entity% into mythicmob...` expression and the `remove mythic from activemob...` effect.
* **[0.84.a]**: Added the `SkillTargeter` class and target expressions (`targetentities of %entity% for targeter %skilltargeter%`).
* **[0.83.a]**: Added `skriptspawncondition` for use with MythicMobs RandomSpawners. Conditions now default to `meet = true`.
