package me.andromedov.mythicskript;

import me.andromedov.mythicskript.classes.MobItem;
import me.andromedov.mythicskript.classes.MythicDrops;
import me.andromedov.mythicskript.conditions.*;
import me.andromedov.mythicskript.conditions.mythicitem.IsSpecificMythicItem;
import me.andromedov.mythicskript.effects.*;
import me.andromedov.mythicskript.effects.mobitems.*;
import me.andromedov.mythicskript.effects.mythicspawner.*;
import me.andromedov.mythicskript.events.skript.MythicSkriptConditionEvent;
import me.andromedov.mythicskript.events.skript.MythicSkriptSkillEvent;
import me.andromedov.mythicskript.events.skript.MythicSkriptSpawnEvent;
import me.andromedov.mythicskript.events.skript.MythicSkriptSpawnerSpawnEvent;
import me.andromedov.mythicskript.expressions.*;
import me.andromedov.mythicskript.expressions.dropmetadata.GetAmount;
import me.andromedov.mythicskript.expressions.dropmetadata.GetDropper;
import me.andromedov.mythicskript.expressions.dropmetadata.GetGenerations;
import me.andromedov.mythicskript.expressions.drops.GetAllDrops;
import me.andromedov.mythicskript.expressions.drops.GetLootBagItems;
import me.andromedov.mythicskript.expressions.drops.GetLootBagOthers;
import me.andromedov.mythicskript.expressions.event.*;
import me.andromedov.mythicskript.expressions.mythicitem.GetMythicItemByName;
import me.andromedov.mythicskript.expressions.mythicitem.GetMythicTypeOfItem;
import me.andromedov.mythicskript.expressions.mythicitem.ItemStackForMythicItemByName;
import me.andromedov.mythicskript.expressions.mythicitem.MakeMythicItemReal;
import me.andromedov.mythicskript.expressions.mythicmob.GetAllMythicMobs;
import me.andromedov.mythicskript.expressions.mythicmob.GetEntityType;
import me.andromedov.mythicskript.expressions.mythicmob.GetMythicMobByName;
import me.andromedov.mythicskript.expressions.mythicspawner.*;
import me.andromedov.mythicskript.expressions.skillmetadata.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.classes.*;
import me.andromedov.mythicskript.conditions.*;
import me.andromedov.mythicskript.conditions.mythicitem.ItemStackisMythicItem;
import me.andromedov.mythicskript.effects.*;
import me.andromedov.mythicskript.effects.conditions.SetConditionMeet;
import me.andromedov.mythicskript.effects.dropmetadata.SetAmount;
import me.andromedov.mythicskript.effects.mobitems.*;
import me.andromedov.mythicskript.effects.mythicspawner.*;
import me.andromedov.mythicskript.events.BukkitEvents;
import me.andromedov.mythicskript.events.skript.*;
import me.andromedov.mythicskript.expressions.*;
import me.andromedov.mythicskript.expressions.drops.*;
import me.andromedov.mythicskript.expressions.event.*;
import me.andromedov.mythicskript.expressions.mythicitem.*;
import me.andromedov.mythicskript.expressions.mythicmob.*;
import me.andromedov.mythicskript.expressions.mythicspawner.*;
import me.andromedov.mythicskript.expressions.skillmetadata.*;
import me.andromedov.mythicskript.functions.Functions;

// --- Skript ---
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;

// --- MythicMobs ---
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobLootDropEvent;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

public class MythicSkriptModule implements AddonModule {

    @Override
    public String name() {
        return "MythicSkriptModule";
    }

    @Override
    public void init(SkriptAddon addon) {
        registerClasses();
    }

    @Override
    public void load(SkriptAddon addon) {
        new BukkitEvents();
        new Functions();

        Utils.init();

        SyntaxRegistry registry = addon.syntaxRegistry();

        registerConditions(registry);
        registerEffects(registry);
        registerExpressions(registry);
        registerEvents(registry);
        registerEventValues(addon);
    }

    /**
     * Registers multiple classes with custom parsers
     */
    private void registerClasses() {
        Classes.registerClass(new ClassInfo<>(ActivePlayer.class, "activeplayer")
                .name("activeplayer").user("activeplayer")
                .defaultExpression(new EventValueExpression<>(ActivePlayer.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(ActivePlayer o, int f) { return o.toString(); }
                    @Override public String toVariableNameString(ActivePlayer o) { return o.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(MythicItem.class, "mythicitem")
                .name("mythicitem").user("mythicitem")
                .defaultExpression(new EventValueExpression<>(MythicItem.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(MythicItem o, int f) { return o.toString(); }
                    @Override public String toVariableNameString(MythicItem o) { return o.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(DropMetadata.class, "dropdata")
                .name("dropdata").user("dropdata")
                .defaultExpression(new EventValueExpression<>(DropMetadata.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(DropMetadata d, int f) { return d.toString(); }
                    @Override public String toVariableNameString(DropMetadata d) { return d.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(LootBag.class, "lootbag")
                .name("lootbag").user("lootbag")
                .defaultExpression(new EventValueExpression<>(LootBag.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(LootBag lb, int f) { return lb.toString(); }
                    @Override public String toVariableNameString(LootBag lb) { return lb.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(SkillMetadata.class, "skilldata")
                .name("skilldata").user("skilldata")
                .defaultExpression(new EventValueExpression<>(SkillMetadata.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(SkillMetadata d, int f) { return d.toString(); }
                    @Override public String toVariableNameString(SkillMetadata d) { return d.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(MythicMob.class, "mythicmob")
                .name("mythicmob").user("mythicmob")
                .defaultExpression(new EventValueExpression<>(MythicMob.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(MythicMob mm, int f) { return mm.getInternalName(); }
                    @Override public String toVariableNameString(MythicMob mm) { return mm.getInternalName(); }
                }));

        Classes.registerClass(new ClassInfo<>(ActiveMob.class, "activemob")
                .name("activemob").user("activemob")
                .defaultExpression(new EventValueExpression<>(ActiveMob.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(ActiveMob am, int f) { return am.getType().getInternalName(); }
                    @Override public String toVariableNameString(ActiveMob am) { return am.getUniqueId().toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(MythicSpawner.class, "mythicspawner")
                .name("mythicspawner").user("mythicspawner")
                .defaultExpression(new EventValueExpression<>(MythicSpawner.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(MythicSpawner ms, int f) { return ms != null ? ms.getInternalName() : "null"; }
                    @Override public String toVariableNameString(MythicSpawner ms) { return ms.getName(); }
                }));

        Classes.registerClass(new ClassInfo<>(MythicDrops.class, "mobdrop")
                .name("mobdrop").user("mobdrop")
                .defaultExpression(new EventValueExpression<>(MythicDrops.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(MythicDrops d, int f) { return Integer.toString(d.getDrops().size()); }
                    @Override public String toVariableNameString(MythicDrops d) { return d.getDrops().toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(MobItem.class, "mobitem")
                .name("mobitem").user("mobitem")
                .defaultExpression(new EventValueExpression<>(MobItem.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override public String toString(MobItem i, int f) { return i.getItem().toString(); }
                    @Override public String toVariableNameString(MobItem i) { return i.toString(); }
                }));

        Classes.registerClass(new ClassInfo<>(SkillTargeter.class, "skilltargeter")
                .name("skilltargeter").user("skilltargeter")
                .defaultExpression(new EventValueExpression<>(SkillTargeter.class))
                .parser(new Parser<>() {
                    @Override public boolean canParse(ParseContext ctx) { return false; }
                    @Override
                    public String toString(SkillTargeter t, int f) {
                        return t.getClass().getSimpleName();
                    }
                    @Override public String toVariableNameString(SkillTargeter t) { return t.toString(); }
                }));
    }

    private void registerConditions(SyntaxRegistry registry) {
        cond(registry, ConditionEntityIsActiveMob.class,
                "%entity% [is ]instanceof activemob",
                "%entity% is [an ]activemob");

        cond(registry, ActiveMobIsDead.class,
                "activemob %activemob% isdead");

        cond(registry, HasThreatTable.class,
                "activemob %activemob% has threattable");

        cond(registry, ConditionAmHasCustomSpawner.class,
                "activemob %activemob% has mythicspawner");

        cond(registry, ConditionSpawnerContainsMob.class,
                "mythicspawner %mythicspawner% contains activemob %activemob%");

        cond(registry, ActiveMobHasImmunityTable.class,
                "activemob %activemob% has immunitytable");

        cond(registry, ItemStackisMythicItem.class,
                "%itemstack% is [a ]mythic[ ]item");

        cond(registry, IsSpecificMythicItem.class,
                "%itemstack% is [a ]mythic[ ]item %string%");
    }

    private void registerEffects(SyntaxRegistry registry) {
        // --- ActiveMob ---
        eff(registry, SetDisplayname.class,   "set display of [activemob] %activemob% to %string%");
        eff(registry, SetLastAggroCause.class, "set lastaggro of activemob %activemob% to %entity%");
        eff(registry, DropCombat.class,        "dropcombat for activemob %activemob%");
        eff(registry, SetTarget.class,         "set %entity% to new target of activemob %activemob%");
        eff(registry, SetFaction.class,        "set faction of activemob %activemob% to %string%");
        eff(registry, SetStance.class,         "set stance of activemob %activemob% to %string%");
        eff(registry, SetLevel.class,          "set level of activemob %activemob% to %number%");
        eff(registry, SetPlayerKills.class,    "set kills of activemob %activemob% to %number%");

        eff(registry, SetOwner.class,
                "set owner of activemob %activemob% to %entity%",
                "set owner of activemob %activemob% to %string% by uuid");

        eff(registry, SendSignal.class,        "send signal %string% to activemob %activemob% with trigger %entity%");
        eff(registry, RemoveMob.class,         "remove activemob %activemob%");
        eff(registry, SetHealth.class,         "set health of activemob %activemob% to %number%");
        eff(registry, SetMaxHealth.class,      "set maxhealth of activemob %activemob% to %number%");

        eff(registry, MakeMobCastSkill.class,
                "make activemob %activemob% cast skill %string% with trigger %entity% at target %entity%",
                "make activemob %activemob% cast skill %string% with trigger %entity% at location %location%");

        eff(registry, MakePlayerCastSkill.class,
                "make player %entity% cast skill %string% with trigger %entity% at entity %entity% with delay %number% and repeat %number%",
                "make player %entity% cast skill %string% with trigger %entity% at location %location% with delay %number% and repeat %number%",
                "make player %entity% cast skill %string% with trigger %entity% at self with delay %number% and repeat %number%");

        eff(registry, ModThreatofEntity.class,
                "inc threat of %entity% by %number% from activemob %activemob%",
                "dec threat of %entity% by %number% from activemob %activemob%");

        eff(registry, RemoveThreatEntity.class,  "remove threat of %entity% from activemob %activemob%");
        eff(registry, ClearThreatTable.class,    "clear threattable of activemob %activemob%");
        eff(registry, SetDamage.class,           "set damage of activemob %activemob% to %number%");
        eff(registry, SetKnockbackResist.class,  "set knockbackresist of activemob %activemob% to %number%");
        eff(registry, SetArmor.class,            "set armor of activemob %activemob% to %number%");
        eff(registry, SetSpeed.class,            "set speed of activemob %activemob% to %number%");
        eff(registry, SetAttackSpeed.class,      "set attackspeed of activemob %activemob% to %number%");
        eff(registry, SetFollowRange.class,      "set followrange of activemob %activemob% to %number%");

        eff(registry, TriggerSkill.class,
                "trigger %string% for activemob %activemob%",
                "trigger %string% for activemob %activemob% with triggerentity %entity%");

        eff(registry, RemoveMythicFromEntity.class, "remove mythic from activemob %activemob%");

        // --- MythicSpawner ---
        eff(registry, ActivateMythicSpawner.class,
                "activate mythicspawner %mythicspawner%",
                "deactivate mythicspawner %mythicspawner%");

        eff(registry, CooldownMythicSpawner.class,
                "set cooldown of mythicspawner %mythicspawner% to %number%",
                "set remainingcooldown of mythicspawner %mythicspawner% to %number%");

        eff(registry, WarmupMythicSpawner.class,
                "set warmup of mythicspawner %mythicspawner% to %number%",
                "set remainingwarmup of mythicspawner %mythicspawner% to %number%");

        eff(registry, SetMobTypeOfSpawner.class,  "set mobtype of mythicspawner %mythicspawner% to %string%");
        eff(registry, SetMovLevelofSpawner.class, "set moblevel of mythicspawner %mythicspawner% to %number%");
        eff(registry, MakeSpawnerSpawn.class,     "make mythicspawner %mythicspawner% spawn");
        eff(registry, AttachMobToSpawner.class,   "attach activemob %activemob% to mythicspawner %mythicspawner%");

        // --- LootBag / MobItems ---
        eff(registry, SetPhysicalLootForLootBag.class, "set [physical] loot [for] [lootbag] %lootbag% to [(%-itemstack%|%-itemstacks%)]");
        eff(registry, SetOtherLootForLootBag.class,    "set [other] loot [for] [lootbag] %lootbag% to [(%-string%|%-strings%)]");

        eff(registry, RemoveMobItem.class,
                "remove mobitem %mobitem% from mobdrop %mobdrop%",
                "clear mobdrop %mobdrop%");

        eff(registry, ChangeMaterialOfMobItem.class, "set material of mobitem %mobitem% to %string%");
        eff(registry, AddItemToMobDrop.class,        "add item %itemstack% to mobdrop %mobdrop%");

        // --- DropMetadata ---
        eff(registry, SetAmount.class, "set amount[ for ][dropdata] %dropdata% to %number%");

        // --- SkriptConditionEvent ---
        eff(registry, SetConditionMeet.class, "set condition meet to %boolean%");
    }

    // ===================================================================
    //  EXPRESSIONS
    // ===================================================================
    private void registerExpressions(SyntaxRegistry registry) {
        // === ActiveMob ===
        expr(registry, SpawnMythicMob.class,      ActiveMob.class,
                "spawn mythicmob %string% at location %location% in world %object%");
        expr(registry, GetActiveMobs.class,        ActiveMob.class,
                "all activemobs in world %string%", "all activemobs");
        expr(registry, GetActiveMob.class,         ActiveMob.class,
                "activemob of %entity%", "activemob instance %entity%");
        expr(registry, GetMobByUUID.class,         ActiveMob.class,  "activemob by uuid %string%");
        expr(registry, GetEntityOfMob.class,       Entity.class,     "entity of activemob %activemob%");
        expr(registry, GetLocation.class,          Location.class,   "location of activemob %activemob%");
        expr(registry, GetWorld.class,             World.class,      "world of activemob %activemob%");
        expr(registry, GetLastAggro.class,         Entity.class,     "lastaggro of activemob %activemob%");
        expr(registry, GetTopThreat.class,         Entity.class,     "toptarget of activemob %activemob%");
        expr(registry, GetUUID.class,              String.class,     "uuid of activemob %activemob%");
        expr(registry, GetHealth.class,            Number.class,     "health of activemob %activemob%");
        expr(registry, GetMaxHealth.class,         Number.class,     "maxhealth of activemob %activemob%");
        expr(registry, GetMythicMobConfig.class,   String.class,     "mlc %string% of activemob %activemob%");
        expr(registry, GetFaction.class,           String.class,     "faction of activemob %activemob%");
        expr(registry, GetStance.class,            String.class,     "stance of activemob %activemob%");
        expr(registry, GetLevel.class,             Number.class,     "level of activemob %activemob%");
        expr(registry, GetPlayerKills.class,       Number.class,     "playerkills of activemob %activemob%");
        expr(registry, GetSignal.class,            String.class,     "lastsignal of activemob %activemob%");
        expr(registry, GetDisplayName.class,       String.class,     "displayname of activemob %activemob%");
        expr(registry, GetMobType.class,           String.class,     "mobtype of activemob %activemob%");
        expr(registry, GetOwner.class,             Entity.class,     "owner of activemob %activemob%");
        expr(registry, GetOwnerUUID.class,         String.class,     "owneruuid of activemob %activemob%");
        expr(registry, GetThreatTable.class,       Entity.class,     "get threattable of activemob %activemob%");
        expr(registry, GetThreatValueOf.class,     Number.class,     "get threatvalue of %entity% from activemob %activemob%");
        expr(registry, GetTargetSelector.class,    SkillTargeter.class, "mythicmobs targeter %string%");
        expr(registry, GetEntitiesFromSelector.class, Entity.class,  "targetentities of %entity% for targeter %skilltargeter%");
        expr(registry, GetLocationsFromSelector.class, Location.class,"targetlocations of %entity% for targeter %skilltargeter%");
        expr(registry, ConvertToMythicMob.class,   ActiveMob.class,
                "convert %entity% into mythicmob %string% with level %number%");

        // === MythicSpawner ===
        expr(registry, GetMythicSpawnerByActiveMob.class, MythicSpawner.class, "mythicspawner of activemob %activemob%");
        expr(registry, GetMythicSpawnerByName.class,      MythicSpawner.class, "mythicspawner of name %string%");
        expr(registry, SpawnerName.class,      String.class,    "name of mythicspawner %mythicspawner%");
        expr(registry, SpawnerLocation.class,  Location.class,  "location of mythicspawner %mythicspawner%");
        expr(registry, GetSpawnerWorld.class,  World.class,     "world of mythicspawner %mythicspawner%");
        expr(registry, SpawnerCooldown.class,  Number.class,
                "cooldown of mythicspawner %mythicspawner%",
                "remainingcooldown of mythicspawner %mythicspawner%");
        expr(registry, SpawnerWarmup.class,    Number.class,
                "warmup of mythicspawner %mythicspawner%",
                "remainingwarmup of mythicspawner %mythicspawner%");
        expr(registry, GetMythicSpawners.class,    MythicSpawner.class,
                "all mythicspawners in world %string%", "all mythicspawners");
        expr(registry, GetMaxMobsFromSpawner.class, Number.class,
                "number of activemobs from mythicspawner %mythicspawner%",
                "number of maxmobs from mythicspawner %mythicspawner%");
        expr(registry, GetAllMobsFromSpawner.class, ActiveMob.class, "all activemobs of mythicspawner %mythicspawner%");
        expr(registry, MobtypeOfSpawner.class,     String.class,    "mobtype of mythicspawner %mythicspawner%");
        expr(registry, GetMovLevelOfSpawner.class, Number.class,    "moblevel of mythicspawner %mythicspawner%");

        // === MobDrops / MobItems / LootBag ===
        expr(registry, GetLootBagOthers.class,  String.class,    "other drop[s] [of] [lootbag] %lootbag%");
        expr(registry, GetAllDrops.class,       MobItem.class,   "all items of mobdrop %mobdrop%");
        expr(registry, GetLootBagItems.class,   ItemStack.class, "physical drop[s] [of] [lootbag] %lootbag%");

        // === MythicItem ===
        expr(registry, ItemStackForMythicItemByName.class, ItemStack.class,
                "[create ]itemstack for mythicitem [named ]%string%");
        expr(registry, GetMythicItemByName.class, MythicItem.class, "[get ]mythicitem [for ]name %string%");
        expr(registry, MakeMythicItemReal.class, ItemStack.class,
                "[get ]itemstack for %mythicitem%",
                "[create ]itemstack for %mythicitem% with amount %number%");

        // === MythicMob types ===
        expr(registry, GetAllMythicMobs.class,   MythicMob.class,   "all mythicmob types");
        expr(registry, GetMythicMobByName.class, MythicMob.class,   "mythicmob with name %string%");
        expr(registry, GetEntityType.class,      EntityType.class,  "entitytype of mythicmob %mythicmob%");

        // === SkillMetadata ===
        expr(registry, GetCaster.class,
                Entity.class,   "[get] caster [of] [skilldata] %skilldata%");
        expr(registry, GetCause.class,
                String.class,   "[get] cause [of] [skilldata] %skilldata%");
        expr(registry, GetEntityTargets.class,   Entity.class,
                "[get] entitytargets [of] [skilldata] %skilldata%");
        expr(registry, GetTargetLocations.class, Location.class,
                "[get] locationtargets [of] [skilldata] %skilldata%");
        expr(registry, GetOriginLocation.class,  Location.class,
                "[get] origin [of] [skilldata] %skilldata%");
        expr(registry, GetPower.class,           Float.class,
                "[get] power [of] [skilldata] %skilldata%");
        expr(registry, GetTrigger.class,
                Entity.class,   "[get] trigger [entity] [of] [skilldata] %skilldata%");

        // === DropMetadata ===
        expr(registry, GetDropper.class,
                Entity.class,   "[get] dropper [of] [dropdata] %dropdata%");
        expr(registry, me.andromedov.mythicskript.expressions.dropmetadata.GetCaster.class,
                Entity.class,   "[get] caster [of] [dropdata] %dropdata%");
        expr(registry, me.andromedov.mythicskript.expressions.dropmetadata.GetCause.class,
                Entity.class,   "[get] cause [of] [dropdata] %dropdata%");
        expr(registry, me.andromedov.mythicskript.expressions.dropmetadata.GetTrigger.class,
                Entity.class,   "[get] trigger [of] [dropdata] %dropdata%");
        expr(registry, GetAmount.class,
                Float.class,    "[get] amount [of] [dropdata] %dropdata%");
        expr(registry, GetGenerations.class,
                Integer.class,  "[get] generations [of] [dropdata] %dropdata%");

        // === Skill event expressions ===
        expr(registry, DeathEventAttacker.class,  Entity.class,  "event-killer");
        expr(registry, EventTarget.class,         Entity.class,  "skill-target");
        expr(registry, EventTrigger.class,        Entity.class,  "skill-trigger");
        expr(registry, EventSkillName.class,      String.class,  "skill-name");
        expr(registry, EventSkillArgs.class,      String.class,  "skill-args");
        expr(registry, TargetType.class,          String.class,  "skill-targettype");

        // === Condition event expressions ===
        expr(registry, ConditionActiveMob.class,    ActiveMob.class, "condition-activemob");
        expr(registry, ConditionEntity.class,       Entity.class,    "condition-entity");
        expr(registry, ConditionTargetEntity.class, Entity.class,    "condition-targetentity");
        expr(registry, ConditionLocation.class,     Location.class,  "condition-location");
        expr(registry, ConditionTargetLocation.class, Location.class,"condition-targetlocation");
        expr(registry, ConditionName.class,         String.class,    "condition-name");
        expr(registry, ConditionArgs.class,         String.class,    "condition-args");
        expr(registry, MeetCondtion.class,          Boolean.class,   "condition-meet");
    }

    // ===================================================================
    //  EVENTS
    // ===================================================================
    private void registerEvents(SyntaxRegistry registry) {
        // MythicMob Loot Drop
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicMob Loot Drop")
                        .addPattern("mythicmob lootdrop [event]")
                        .addEvent(MythicMobLootDropEvent.class)
                        .build());

        // MythicMob Spawn
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicMob Spawn")
                        .addPattern("mythicmob spawnevent")
                        .addEvent(MythicSkriptSpawnEvent.class)
                        .build());

        // MythicMob Death
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicMob Death")
                        .addPattern("mythicmob deathevent")
                        .addEvent(MythicMobDeathEvent.class)
                        .build());

        // MythicSpawner Spawn
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicSpawner Spawn")
                        .addPattern("mythicspawner spawnevent")
                        .addEvent(MythicSkriptSpawnerSpawnEvent.class)
                        .build());

        // Skript Skill Event
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicMobs Skript Skill")
                        .addPattern("mythicmobs skriptskillevent")
                        .addEvent(MythicSkriptSkillEvent.class)
                        .build());

        // Skript Condition Event
        registry.register(BukkitSyntaxInfos.Event.KEY,
                BukkitSyntaxInfos.Event.builder(SimpleEvent.class, "MythicMobs Skript Condition")
                        .addPattern("mythicmobs skriptconditionevent")
                        .addEvent(MythicSkriptConditionEvent.class)
                        .build());
    }

    // ===================================================================
    //  EVENT VALUES
    // ===================================================================
    private void registerEventValues(SkriptAddon addon) {
        EventValueRegistry evr = addon.registry(EventValueRegistry.class);

        // MythicMobLootDropEvent
        evr.register(EventValue.simple(MythicMobLootDropEvent.class, ActiveMob.class,
                MythicMobLootDropEvent::getMob));
        evr.register(EventValue.simple(MythicMobLootDropEvent.class, LootBag.class,
                MythicMobLootDropEvent::getDrops));

        // MythicSkriptSpawnEvent
        evr.register(EventValue.simple(MythicSkriptSpawnEvent.class, ActiveMob.class,
                MythicSkriptSpawnEvent::getActiveMob));
        evr.register(EventValue.simple(MythicSkriptSpawnEvent.class, Entity.class,
                MythicSkriptSpawnEvent::getEntity));

        // MythicMobDeathEvent
        evr.register(EventValue.simple(MythicMobDeathEvent.class, ActiveMob.class,
                MythicMobDeathEvent::getMob));
        evr.register(EventValue.simple(MythicMobDeathEvent.class, Entity.class,
                MythicMobDeathEvent::getEntity));
        evr.register(EventValue.simple(MythicMobDeathEvent.class, Location.class,
                e -> e.getEntity().getLocation()));
        evr.register(EventValue.simple(MythicMobDeathEvent.class, MythicDrops.class,
                e -> new MythicDrops(e.getDrops())));

        // MythicSkriptSpawnerSpawnEvent
        evr.register(EventValue.simple(MythicSkriptSpawnerSpawnEvent.class, MythicSpawner.class,
                MythicSkriptSpawnerSpawnEvent::getMs));
        evr.register(EventValue.simple(MythicSkriptSpawnerSpawnEvent.class, ActiveMob.class,
                MythicSkriptSpawnerSpawnEvent::getAm));

        // MythicSkriptSkillEvent
        evr.register(EventValue.simple(MythicSkriptSkillEvent.class, Entity.class,
                e -> e.getCaster().getEntity().getBukkitEntity()));
        evr.register(EventValue.simple(MythicSkriptSkillEvent.class, Location.class,
                MythicSkriptSkillEvent::getTargetLocation));
    }


    private <E extends ch.njol.skript.lang.Condition>
    void cond(SyntaxRegistry registry, Class<E> cls, String... patterns) {
        var builder = SyntaxInfo.builder(cls);
        for (String p : patterns) builder.addPattern(p);
        registry.register(SyntaxRegistry.CONDITION, builder.build());
    }

    private <E extends ch.njol.skript.lang.Effect>
    void eff(SyntaxRegistry registry, Class<E> cls, String... patterns) {
        var builder = SyntaxInfo.builder(cls);
        for (String p : patterns) builder.addPattern(p);
        registry.register(SyntaxRegistry.EFFECT, builder.build());
    }

    private <E extends ch.njol.skript.lang.Expression<T>, T>
    void expr(SyntaxRegistry registry, Class<E> cls, Class<T> returnType, String... patterns) {
        var builder = DefaultSyntaxInfos.Expression.builder(cls, returnType);
        for (String p : patterns) builder.addPattern(p);
        registry.register(SyntaxRegistry.EXPRESSION, builder.build());
    }
}