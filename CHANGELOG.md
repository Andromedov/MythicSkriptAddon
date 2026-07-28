# Changelog

All notable changes to this project will be documented in this file.

## [1.3.0] – The Function Update (2026-07-29)

### Added

- **Named `skfunction` parameters:** MythicMobs mechanic fields are now mapped to
  Skript function parameters by name, so a function can receive custom values
  directly from its skill configuration.
- **Runtime placeholder resolution:** Custom parameter values support MythicMobs
  placeholders and are resolved when the skill is cast, including target-aware
  values for entity and location targets.
- **Repeated parameter types:** Functions may declare multiple parameters of the
  same type, including automatically supplied `skilldata`, `entity`, `player`,
  and `location` values as well as multiple named configuration parameters.
- **Supported custom parameter types:** Added support for `text`, `number`,
  `boolean`, and `colordata` parameters in `skfunction` mechanics.
- **Color data type:** Added the Skript `colordata` type with support for
  `#RRGGBB`, `#AARRGGBB`, `R,G,B`, and Bukkit dye-color names.

### Changed

- `SkillMetadata`, `entity`, and `location` parameters continue to be supplied
  automatically by MythicMobs, while custom parameters are read from fields with
  matching names in the `skfunction` configuration.
- `skfunction` now validates function parameters during mechanic creation and
  reports missing or unsupported configuration values through Skript warnings.
- Invalid custom values return `INVALID_CONFIG` instead of invoking the Skript
  function with incomplete or incorrectly typed arguments.
- `skfunction` now runs synchronously and validates concrete entity subtypes, so
  a `player` parameter only receives an actual player target.
- Separated the legacy `skriptskill` event mechanic from the direct `skfunction`
  mechanic to prevent one registration from overwriting the other.
- Updated the supported platform range to Paper 1.21.4 – 26.2 with Java 21,
  Skript 2.16.0, and MythicMobs 5.12.0.
- Updated the plugin metadata API version to 1.21.4 and the release workflow game
  range to `>=1.21.4 <=26.2`.

### Removed

- Removed the unused `src/main/resources/version.txt` resource.
- Removed the obsolete `wiki/ActiveMob.md` page.
- Removed legacy `javax.annotation.Nullable` usages from the item integration
  classes and placeholder implementation.

## [1.2.0] – The Placeholder Update (2026-07-11)

### Added
- New MythicMobs placeholder: `<skript.variable_name>` — reads the value of a global Skript variable directly inside any MythicMobs config field that supports placeholders (messages, display names, numeric attributes, conditions, etc.).
- List variable support via Skript's native `::` separator: `<skript.my_list::1>` returns a single index, `<skript.my_list::*>` returns all values as a comma-separated string.

### Changed
- Cleaned up some code and comments. Nothing out of the ordinary.

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
