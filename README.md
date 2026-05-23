# MythicSkriptAddon 🐉

[![Paper](https://img.shields.io/badge/Paper-1.21.x-brightgreen.svg)](https://papermc.io/)
[![MythicMobs](https://img.shields.io/badge/MythicMobs-5.12.0-orange.svg)](https://mythiccraft.io/)
[![Skript](https://img.shields.io/badge/Skript-2.15.2-blue.svg)](https://github.com/SkriptLang/Skript)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful bridge between **MythicMobs** and **Skript**. This addon allows you to create custom mechanics, conditions, targeters, and drops for MythicMobs using purely Skript syntax. It also gives you full control over ActiveMobs and MythicSpawners directly from your scripts.

## 🌟 Key Features

- **Custom Mechanics (`skfunction`)**: Call Skript functions directly from MythicMobs skills.
- **Custom Conditions**: Create complex checks on the Skript side for targets and locations.
- **Custom Targeters**: Select targets using Skript logic and pass them back to MythicMobs.
- **Custom Drops**: Manage item drops and messages using Skript functions.
- **Full Mob Control**: Modify health, armor, damage, Threat Tables, factions, and Stances of ActiveMobs on the fly.
- **Spawner Integration**: Control MythicSpawners (warmups, cooldowns, activation, changing mob types) via Skript.

## 📦 Requirements

- **Server**: Spigot / Paper 1.21+
- **MythicMobs**: 5.12.0 or newer
- **Skript**: 2.15.2 or newer
- **Java**: 21+

## 🚀 Quick Start (Example)

You can easily trigger a Skript function as a mechanic in your MythicMobs `Skills.yml`:

**In MythicMobs skills file (Skills.yml):**
```yaml
SkriptSkillExample:
  Skills:
  - skfunction{name=my_custom_skill} @target ~onInteract
```

**In your script file (.sk):**
```shell
function my_custom_skill(data: skilldata, target: entity, loc: location) :: boolean:
    broadcast "MythicMob %caster of {_data}% used a skill on %{_target}%!"
    apply glowing to {_target} for 5 seconds
    return true
```

## 📖 Documentation & Syntax
A comprehensive list of expressions, effects, events, and a detailed guide 
on working with `skfunction` can be found on our [Wiki page](https://github.com/Andromedov/MythicSkriptAddon/wiki).

## 📜 Changelog
All changes, updates to new API versions, and bug fixes are documented in the [CHANGELOG.md](CHANGELOG.md) file.

## 📄 License
This project is licensed under the [Apache License 2.0](LICENSE).
Original concept and early versions by [BerndiVader](https://github.com/BerndiVader/MythicSkriptAddon).