package com.droid.mydnd.data

import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.model.Campaign
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.FeatureItem
import com.droid.mydnd.model.InventoryItem
import com.droid.mydnd.model.NpcItem
import com.droid.mydnd.model.QuestItem
import com.droid.mydnd.model.RechargeType
import com.droid.mydnd.model.ResourceCounter
import com.droid.mydnd.model.SessionNote
import com.droid.mydnd.model.SpellItem

object PreloadedData {

    val valerosFighter = CharacterSheet(
        id = "char-valeros-fighter",
        name = "Valeros Stormblade",
        characterClass = "Fighter (Battle Master) 5",
        race = "Human (Variant)",
        level = 5,
        xp = 6500,
        background = "Soldier",
        alignment = "Neutral Good",
        maxHp = 49,
        currentHp = 49,
        tempHp = 0,
        armorClass = 18, // Plate armor
        initiativeModifier = 1,
        speed = 30,
        passivePerception = 13,
        proficiencyBonus = 3,
        hitDiceTotal = "5d10",
        hitDiceRemaining = 5,
        abilityScores = mapOf(
            "STR" to 18,
            "DEX" to 13,
            "CON" to 16,
            "INT" to 10,
            "WIS" to 12,
            "CHA" to 9
        ),
        savingThrowProficiencies = setOf("STR", "CON"),
        skillProficiencies = setOf("Athletics", "Intimidation", "Perception", "Survival"),
        resources = listOf(
            ResourceCounter(name = "Action Surge", current = 1, max = 1, recharge = RechargeType.SHORT_REST),
            ResourceCounter(name = "Second Wind (1d10+5)", current = 1, max = 1, recharge = RechargeType.SHORT_REST),
            ResourceCounter(name = "Superiority Dice (d8)", current = 4, max = 4, recharge = RechargeType.SHORT_REST),
            ResourceCounter(name = "Indomitable", current = 0, max = 1, recharge = RechargeType.LONG_REST)
        ),
        actions = listOf(
            ActionItem(
                id = "act-val-1",
                name = "Greatsword +1",
                type = ActionType.MELEE_ATTACK,
                attackBonus = 8, // +4 str + 3 prof + 1 weapon
                damageFormula = "2d6+5",
                damageType = "Slashing (Magical)",
                range = "5 ft",
                description = "Two-handed heavy blade forged in mithral.",
                isPinned = true
            ),
            ActionItem(
                id = "act-val-2",
                name = "Heavy Crossbow",
                type = ActionType.RANGED_ATTACK,
                attackBonus = 4, // +1 dex + 3 prof
                damageFormula = "1d10+1",
                damageType = "Piercing",
                range = "100/400 ft",
                description = "Loaded with steel-tipped bolts.",
                isPinned = true
            ),
            ActionItem(
                id = "act-val-3",
                name = "Trip Attack (Maneuver)",
                type = ActionType.SPECIAL_ACTION,
                attackBonus = 8,
                damageFormula = "1d8",
                damageType = "Bonus Damage",
                description = "Add superiority die to damage; target must make STR Save (DC 15) or fall Prone.",
                isPinned = true
            ),
            ActionItem(
                id = "act-val-4",
                name = "Second Wind",
                type = ActionType.BONUS_ACTION,
                damageFormula = "1d10+5",
                damageType = "Healing",
                description = "Regain 1d10 + 5 hit points as a bonus action once per short rest.",
                isPinned = true,
                currentUses = 1,
                maxUses = 1,
                recharge = RechargeType.SHORT_REST
            ),
            ActionItem(
                id = "act-val-5",
                name = "Riposte",
                type = ActionType.REACTION,
                damageFormula = "1d8",
                damageType = "Slashing",
                description = "When a creature misses you with a melee attack, spend 1 superiority die to strike back.",
                isPinned = true
            )
        ),
        inventory = listOf(
            InventoryItem(name = "Greatsword +1", quantity = 1, weight = 6.0, isEquipped = true),
            InventoryItem(name = "Plate Armor", quantity = 1, weight = 65.0, isEquipped = true),
            InventoryItem(name = "Heavy Crossbow", quantity = 1, weight = 18.0, isEquipped = true),
            InventoryItem(name = "Crossbow Bolts", quantity = 20, weight = 1.5),
            InventoryItem(name = "Potion of Greater Healing", quantity = 2, weight = 1.0),
            InventoryItem(name = "Explorer's Pack", quantity = 1, weight = 50.0)
        ),
        features = listOf(
            FeatureItem(name = "Extra Attack", source = "Fighter 5", description = "You can attack twice whenever you take the Attack action."),
            FeatureItem(name = "Action Surge", source = "Fighter 2", description = "Push yourself to take one additional action on your turn."),
            FeatureItem(name = "Combat Superiority", source = "Battle Master", description = "4 superiority dice (d8) for Trip, Riposte, and Menacing Attack."),
            FeatureItem(name = "Great Weapon Fighting", source = "Fighting Style", description = "Reroll 1 or 2 on damage dice for two-handed weapons.")
        ),
        notes = "Hails from the northern marches. Bound by an oath to protect the party's spellcasters at all costs."
    )

    val lyraWizard = CharacterSheet(
        id = "char-lyra-wizard",
        name = "Lyra Nightbreeze",
        characterClass = "Wizard (School of Evocation) 5",
        race = "High Elf",
        level = 5,
        xp = 6500,
        background = "Sage",
        alignment = "Chaotic Good",
        maxHp = 32,
        currentHp = 32,
        tempHp = 0,
        armorClass = 13, // 16 with Mage Armor
        initiativeModifier = 3,
        speed = 30,
        passivePerception = 14,
        proficiencyBonus = 3,
        hitDiceTotal = "5d6",
        hitDiceRemaining = 5,
        abilityScores = mapOf(
            "STR" to 8,
            "DEX" to 16,
            "CON" to 14,
            "INT" to 18,
            "WIS" to 12,
            "CHA" to 10
        ),
        savingThrowProficiencies = setOf("INT", "WIS"),
        skillProficiencies = setOf("Arcana", "History", "Investigation", "Perception"),
        spellSlotsTotal = mapOf(1 to 4, 2 to 3, 3 to 2),
        spellSlotsUsed = mapOf(1 to 0, 2 to 0, 3 to 0),
        spellSaveDc = 15, // 8 + 3 prof + 4 int
        spellAttackBonus = 7, // 3 prof + 4 int
        resources = listOf(
            ResourceCounter(name = "Arcane Recovery (3 levels)", current = 1, max = 1, recharge = RechargeType.SHORT_REST),
            ResourceCounter(name = "Sculpt Spells Safe Pockets", current = 4, max = 4, recharge = RechargeType.LONG_REST)
        ),
        actions = listOf(
            ActionItem(
                id = "act-lyra-1",
                name = "Fire Bolt",
                type = ActionType.SPELL_ATTACK,
                attackBonus = 7,
                damageFormula = "2d10",
                damageType = "Fire",
                range = "120 ft",
                description = "Hurl a mote of fire. Targets ignite if flammable.",
                isPinned = true
            ),
            ActionItem(
                id = "act-lyra-2",
                name = "Ray of Frost",
                type = ActionType.SPELL_ATTACK,
                attackBonus = 7,
                damageFormula = "2d8",
                damageType = "Cold",
                range = "60 ft",
                description = "Frigid beam reduces target speed by 10 ft until your next turn.",
                isPinned = true
            ),
            ActionItem(
                id = "act-lyra-3",
                name = "Shield",
                type = ActionType.REACTION,
                description = "+5 to AC until start of your next turn and nullifies Magic Missile.",
                isPinned = true,
                spellLevel = 1
            ),
            ActionItem(
                id = "act-lyra-4",
                name = "Misty Step",
                type = ActionType.BONUS_ACTION,
                range = "30 ft",
                description = "Teleport up to 30 feet to an unoccupied space you can see.",
                isPinned = true,
                spellLevel = 2
            ),
            ActionItem(
                id = "act-lyra-5",
                name = "Dagger of Venom",
                type = ActionType.MELEE_ATTACK,
                attackBonus = 6,
                damageFormula = "1d4+3",
                damageType = "Piercing",
                range = "20/60 ft",
                description = "Silvered light dagger.",
                isPinned = false
            )
        ),
        spells = listOf(
            SpellItem(
                name = "Fire Bolt",
                level = 0,
                school = "Evocation",
                castingTime = "1 Action",
                range = "120 ft",
                components = "V, S",
                description = "Ranged spell attack doing 2d10 fire damage.",
                damageOrEffect = "2d10 Fire",
                isPinned = true
            ),
            SpellItem(
                name = "Mage Hand",
                level = 0,
                school = "Conjuration",
                castingTime = "1 Action",
                range = "30 ft",
                components = "V, S",
                description = "Spectral hand manipulates objects up to 10 lbs.",
                isPinned = true
            ),
            SpellItem(
                name = "Magic Missile",
                level = 1,
                school = "Evocation",
                castingTime = "1 Action",
                range = "120 ft",
                components = "V, S",
                description = "3 darts hit automatically for 1d4+1 force damage each.",
                damageOrEffect = "3x (1d4+1) Force",
                isPinned = true
            ),
            SpellItem(
                name = "Shield",
                level = 1,
                school = "Abjuration",
                castingTime = "1 Reaction",
                range = "Self",
                components = "V, S",
                description = "+5 AC including triggering attack.",
                damageOrEffect = "+5 AC",
                isPinned = true
            ),
            SpellItem(
                name = "Mage Armor",
                level = 1,
                school = "Abjuration",
                castingTime = "1 Action",
                range = "Touch",
                components = "V, S, M",
                duration = "8 hours",
                description = "Target AC becomes 13 + Dex modifier (AC 16).",
                isPinned = false
            ),
            SpellItem(
                name = "Misty Step",
                level = 2,
                school = "Conjuration",
                castingTime = "1 Bonus Action",
                range = "Self (30 ft)",
                components = "V",
                description = "Teleport up to 30 ft.",
                isPinned = true
            ),
            SpellItem(
                name = "Scorching Ray",
                level = 2,
                school = "Evocation",
                castingTime = "1 Action",
                range = "120 ft",
                components = "V, S",
                description = "Create 3 rays of fire. Make ranged spell attack for each: 2d6 fire damage.",
                damageOrEffect = "3x 2d6 Fire",
                isPinned = true
            ),
            SpellItem(
                name = "Fireball",
                level = 3,
                school = "Evocation",
                castingTime = "1 Action",
                range = "150 ft (20-ft radius)",
                components = "V, S, M",
                description = "DEX Save DC 15. Each creature takes 8d6 fire damage on failure, half on success. Sculpt Spells protects allies!",
                damageOrEffect = "8d6 Fire",
                saveDcType = "DEX (DC 15)",
                isPinned = true
            ),
            SpellItem(
                name = "Counterspell",
                level = 3,
                school = "Abjuration",
                castingTime = "1 Reaction",
                range = "60 ft",
                components = "S",
                description = "Interrupt a creature casting a spell of 3rd level or lower.",
                isPinned = true
            )
        ),
        inventory = listOf(
            InventoryItem(name = "Spellbook (Leather bound with gold leaf)", quantity = 1, weight = 3.0),
            InventoryItem(name = "Arcane Focus (Crystal wand)", quantity = 1, weight = 1.0, isEquipped = true),
            InventoryItem(name = "Scholar's Robes", quantity = 1, weight = 4.0, isEquipped = true),
            InventoryItem(name = "Component Pouch", quantity = 1, weight = 2.0, isEquipped = true),
            InventoryItem(name = "Scroll of Detect Magic", quantity = 2, weight = 0.5)
        ),
        features = listOf(
            FeatureItem(name = "Sculpt Spells", source = "Evocation 2", description = "When casting an evocation spell that affects allies, you can protect up to 1 + spell level creatures so they take no damage on a successful save."),
            FeatureItem(name = "Arcane Recovery", source = "Wizard 1", description = "Once per day after a short rest, regain spell slots up to half your wizard level (3 slots)."),
            FeatureItem(name = "Fey Ancestry", source = "Elf", description = "Advantage on saving throws against being charmed, and magic cannot put you to sleep.")
        ),
        notes = "Apprentice to the High Arcanist of Silverymoon. Seeking fragments of ancient Netherese spell scrolls."
    )

    val theronRogue = CharacterSheet(
        id = "char-theron-rogue",
        name = "Theron Shadowcloak",
        characterClass = "Rogue (Arcane Trickster) 5",
        race = "Wood Elf",
        level = 5,
        xp = 6500,
        background = "Criminal",
        alignment = "Chaotic Neutral",
        maxHp = 38,
        currentHp = 38,
        tempHp = 0,
        armorClass = 16, // Studded leather (12) + Dex (4)
        initiativeModifier = 4,
        speed = 35,
        passivePerception = 17,
        proficiencyBonus = 3,
        hitDiceTotal = "5d8",
        hitDiceRemaining = 5,
        abilityScores = mapOf(
            "STR" to 10,
            "DEX" to 18,
            "CON" to 14,
            "INT" to 14,
            "WIS" to 12,
            "CHA" to 10
        ),
        savingThrowProficiencies = setOf("DEX", "INT"),
        skillProficiencies = setOf("Acrobatics", "Deception", "Insight", "Perception", "Sleight of Hand", "Stealth"),
        skillExpertise = setOf("Stealth", "Sleight of Hand"),
        spellSlotsTotal = mapOf(1 to 3),
        spellSlotsUsed = mapOf(1 to 0),
        spellSaveDc = 13,
        spellAttackBonus = 5,
        resources = listOf(
            ResourceCounter(name = "Sneak Attack Dice (3d6)", current = 1, max = 1, recharge = RechargeType.NONE),
            ResourceCounter(name = "Uncanny Dodge", current = 1, max = 1, recharge = RechargeType.NONE)
        ),
        actions = listOf(
            ActionItem(
                id = "act-ther-1",
                name = "Rapier + Sneak Attack",
                type = ActionType.MELEE_ATTACK,
                attackBonus = 7, // +4 dex + 3 prof
                damageFormula = "1d8+4",
                damageType = "Piercing (+3d6 Sneak Attack)",
                range = "5 ft",
                description = "Finesse blade. Add +3d6 Sneak Attack if you have advantage or an ally is within 5 ft.",
                isPinned = true
            ),
            ActionItem(
                id = "act-ther-2",
                name = "Shortbow",
                type = ActionType.RANGED_ATTACK,
                attackBonus = 7,
                damageFormula = "1d6+4",
                damageType = "Piercing",
                range = "80/320 ft",
                description = "Silent composite wood bow.",
                isPinned = true
            ),
            ActionItem(
                id = "act-ther-3",
                name = "Cunning Action (Dash/Disengage/Hide)",
                type = ActionType.BONUS_ACTION,
                description = "Take Dash, Disengage, or Hide action as a bonus action on each of your turns.",
                isPinned = true
            ),
            ActionItem(
                id = "act-ther-4",
                name = "Uncanny Dodge",
                type = ActionType.REACTION,
                description = "When an attacker you can see hits you with an attack, use reaction to halve the damage.",
                isPinned = true
            )
        ),
        spells = listOf(
            SpellItem(name = "Mage Hand Legerdemain", level = 0, school = "Conjuration", castingTime = "1 Action", range = "30 ft", description = "Invisible spectral hand can pick locks, pocket items, or disarm traps at range.", isPinned = true),
            SpellItem(name = "Minor Illusion", level = 0, school = "Illusion", castingTime = "1 Action", range = "30 ft", description = "Create sound or image of an object.", isPinned = true),
            SpellItem(name = "Disguise Self", level = 1, school = "Illusion", castingTime = "1 Action", range = "Self", duration = "1 hour", description = "Make yourself appear different.", isPinned = true),
            SpellItem(name = "Tasha's Hideous Laughter", level = 1, school = "Enchantment", castingTime = "1 Action", range = "30 ft", description = "Target falls prone laughing on WIS Save DC 13.", isPinned = true)
        ),
        inventory = listOf(
            InventoryItem(name = "Fine Rapier", quantity = 1, weight = 2.0, isEquipped = true),
            InventoryItem(name = "Studded Leather Armor", quantity = 1, weight = 13.0, isEquipped = true),
            InventoryItem(name = "Thieves' Tools (Masterwork)", quantity = 1, weight = 1.0, isEquipped = true),
            InventoryItem(name = "Cloak of Elvenkind", quantity = 1, weight = 1.0, isEquipped = true),
            InventoryItem(name = "Smoke Pellets", quantity = 3, weight = 0.5)
        ),
        features = listOf(
            FeatureItem(name = "Sneak Attack (3d6)", source = "Rogue 1", description = "Once per turn, deal extra 3d6 damage to one creature you hit with advantage or ally proximity."),
            FeatureItem(name = "Cunning Action", source = "Rogue 2", description = "Bonus action to Dash, Disengage, or Hide."),
            FeatureItem(name = "Uncanny Dodge", source = "Rogue 5", description = "Halve damage from an attack using your reaction."),
            FeatureItem(name = "Mage Hand Legerdemain", source = "Arcane Trickster", description = "Invisible Mage Hand controls thieves' tools and sleight of hand at 30 ft.")
        ),
        notes = "Contact in Neverwinter underworld. Specializes in recovering stolen relics from corrupt nobles."
    )

    val brotherGarethCleric = CharacterSheet(
        id = "char-gareth-cleric",
        name = "Brother Gareth",
        characterClass = "Cleric (Life Domain) 5",
        race = "Hill Dwarf",
        level = 5,
        xp = 6500,
        background = "Acolyte",
        alignment = "Lawful Good",
        maxHp = 43,
        currentHp = 43,
        tempHp = 0,
        armorClass = 18, // Chain mail (16) + Shield (2)
        initiativeModifier = 0,
        speed = 25,
        passivePerception = 16,
        proficiencyBonus = 3,
        hitDiceTotal = "5d8",
        hitDiceRemaining = 5,
        abilityScores = mapOf(
            "STR" to 14,
            "DEX" to 10,
            "CON" to 16,
            "INT" to 10,
            "WIS" to 18,
            "CHA" to 12
        ),
        savingThrowProficiencies = setOf("WIS", "CHA"),
        skillProficiencies = setOf("History", "Insight", "Medicine", "Religion"),
        spellSlotsTotal = mapOf(1 to 4, 2 to 3, 3 to 2),
        spellSlotsUsed = mapOf(1 to 0, 2 to 0, 3 to 0),
        spellSaveDc = 15, // 8 + 3 prof + 4 wis
        spellAttackBonus = 7, // 3 prof + 4 wis
        resources = listOf(
            ResourceCounter(name = "Channel Divinity: Preserve Life", current = 2, max = 2, recharge = RechargeType.SHORT_REST),
            ResourceCounter(name = "Turn Undead", current = 1, max = 1, recharge = RechargeType.SHORT_REST)
        ),
        actions = listOf(
            ActionItem(
                id = "act-gar-1",
                name = "Warhammer",
                type = ActionType.MELEE_ATTACK,
                attackBonus = 5, // +2 str + 3 prof
                damageFormula = "1d8+2",
                damageType = "Bludgeoning",
                range = "5 ft",
                description = "Blessed dwarven warhammer with radiant runes.",
                isPinned = true
            ),
            ActionItem(
                id = "act-gar-2",
                name = "Sacred Flame",
                type = ActionType.SPELL_ATTACK,
                damageFormula = "2d8",
                damageType = "Radiant",
                range = "60 ft",
                description = "Target makes DEX Save DC 15 or takes 2d8 radiant damage. Ignores cover.",
                isPinned = true
            ),
            ActionItem(
                id = "act-gar-3",
                name = "Spiritual Weapon",
                type = ActionType.BONUS_ACTION,
                attackBonus = 7,
                damageFormula = "1d8+4",
                damageType = "Force",
                range = "60 ft",
                description = "Summon glowing spiritual mace as bonus action. Strike for 1d8+4 force.",
                isPinned = true,
                spellLevel = 2
            ),
            ActionItem(
                id = "act-gar-4",
                name = "Cure Wounds",
                type = ActionType.SPECIAL_ACTION,
                damageFormula = "1d8+7",
                damageType = "Healing (Disciple of Life)",
                range = "Touch",
                description = "Heals 1d8 + 4 WIS + 3 Disciple of Life = 1d8+7 HP.",
                isPinned = true,
                spellLevel = 1
            )
        ),
        spells = listOf(
            SpellItem(name = "Sacred Flame", level = 0, school = "Evocation", castingTime = "1 Action", range = "60 ft", damageOrEffect = "2d8 Radiant", saveDcType = "DEX (DC 15)", isPinned = true),
            SpellItem(name = "Guidance", level = 0, school = "Divination", castingTime = "1 Action", range = "Touch", damageOrEffect = "+1d4 to Check", isConcentration = true, isPinned = true),
            SpellItem(name = "Bless", level = 1, school = "Enchantment", castingTime = "1 Action", range = "30 ft", duration = "1 min", isConcentration = true, damageOrEffect = "+1d4 Attack & Saves (3 targets)", isPinned = true),
            SpellItem(name = "Healing Word", level = 1, school = "Evocation", castingTime = "1 Bonus Action", range = "60 ft", damageOrEffect = "1d4+7 Healing", isPinned = true),
            SpellItem(name = "Spiritual Weapon", level = 2, school = "Evocation", castingTime = "1 Bonus Action", range = "60 ft", damageOrEffect = "1d8+4 Force", isPinned = true),
            SpellItem(name = "Spirit Guardians", level = 3, school = "Conjuration", castingTime = "1 Action", range = "Self (15 ft)", duration = "10 min", isConcentration = true, damageOrEffect = "3d8 Radiant", saveDcType = "WIS (DC 15)", isPinned = true)
        ),
        inventory = listOf(
            InventoryItem(name = "Warhammer", quantity = 1, weight = 2.0, isEquipped = true),
            InventoryItem(name = "Chain Mail", quantity = 1, weight = 55.0, isEquipped = true),
            InventoryItem(name = "Shield (Emblazoned with Sun symbol)", quantity = 1, weight = 6.0, isEquipped = true),
            InventoryItem(name = "Holy Symbol of Pelor", quantity = 1, weight = 1.0, isEquipped = true),
            InventoryItem(name = "Healer's Kit (10 uses)", quantity = 1, weight = 3.0)
        ),
        features = listOf(
            FeatureItem(name = "Disciple of Life", source = "Life Domain", description = "Healing spells restore additional HP equal to 2 + spell's level."),
            FeatureItem(name = "Preserve Life", source = "Channel Divinity", description = "Heal injured creatures within 30 ft up to 5x cleric level (25 HP pool)."),
            FeatureItem(name = "Dwarven Resilience", source = "Dwarf", description = "Advantage on saving throws against poison and resistance against poison damage.")
        ),
        notes = "Devoted to Pelor the Sunfather. Never leaves an injured soul behind."
    )

    val strahdCampaign = Campaign(
        id = "camp-strahd",
        title = "Curse of Strahd - The Mists of Barovia",
        dungeonMaster = "Matthew Mercer",
        description = "Trapped in the mist-shrouded valley of Barovia, the party must defeat Count Strahd von Zarovich in Castle Ravenloft.",
        characterIds = listOf("char-valeros-fighter", "char-lyra-wizard", "char-theron-rogue", "char-gareth-cleric"),
        sessionNotes = listOf(
            SessionNote(
                id = "note-str-1",
                campaignId = "camp-strahd",
                title = "Session 12: Battle at the Old Bonegrinder",
                date = "Oct 10, 2026",
                content = "Fought the night hag coven! Valeros held the door while Lyra sculpted a Fireball. Found pastry ingredients and mysterious children bones in the upper attic. Morgantha escaped in ethereal form.",
                isPinnedToDashboard = true,
                tags = listOf("Combat", "Hags", "Barovia")
            ),
            SessionNote(
                id = "note-str-2",
                campaignId = "camp-strahd",
                title = "Madame Eva's Tarokka Clues",
                date = "Oct 4, 2026",
                content = "Sunsword is hidden in the flooded crypts beneath Castle Ravenloft. Holy Symbol of Ravenkind rests in the Abbey of Saint Markovia in Krezk. Enemy of Strahd: Rudolph van Richten disguised as Rictavio in Vallaki!",
                isPinnedToDashboard = true,
                tags = listOf("Lore", "Tarokka", "Quests")
            ),
            SessionNote(
                id = "note-str-3",
                campaignId = "camp-strahd",
                title = "Vampire Spawn Combat Tactics",
                date = "Sep 28, 2026",
                content = "IMPORTANT FOR COMBAT: Vampire spawn regenerate 10 HP per round unless damaged by radiant damage or running water! Gareth MUST keep Spirit Guardians active. Running water deals 20 acid damage!",
                isPinnedToDashboard = true,
                tags = listOf("Tactics", "Combat", "Weaknesses")
            )
        ),
        quests = listOf(
            QuestItem(title = "Retrieve the Sunsword", description = "In the flooded crypts of Castle Ravenloft beneath the tombs of the ancient kings.", isCompleted = false, reward = "Sunsword Legendary Weapon"),
            QuestItem(title = "Escort Ireena Kolyana to Krezk", description = "Protect Ireena from Strahd's bat swarms and reach the fortified abbey of Krezk.", isCompleted = false, reward = "Safe sanctuary"),
            QuestItem(title = "Cleanse the Wizard of Wines Winery", description = "Drive the evil druids and needle blights from the Martikov family vineyard.", isCompleted = true, reward = "Allied Keepers of the Feather (Wereravens)")
        ),
        npcs = listOf(
            NpcItem(name = "Count Strahd von Zarovich", role = "Darklord of Barovia", location = "Castle Ravenloft", notes = "Master vampire, archmage. Ancient, powerful, arrogant.", isHostile = true),
            NpcItem(name = "Ireena Kolyana", role = "Nobleward of Barovia", location = "Traveling with Party", notes = "Target of Strahd's obsession. Competent with a rapier.", isHostile = false),
            NpcItem(name = "Rudolph van Richten", role = "Legendary Monster Hunter", location = "Vallaki (Blue Water Inn)", notes = "Disguised as Rictavio the flamboyant half-elf carnival ringmaster.", isHostile = false),
            NpcItem(name = "Madame Eva", role = "Vistani Seer", location = "Tser Pool Encampment", notes = "Dealt the Tarokka cards predicting the artifacts and our destined ally.", isHostile = false)
        )
    )

    val lostMineCampaign = Campaign(
        id = "camp-lostmine",
        title = "The Lost Mine of Phandelver",
        dungeonMaster = "Brennan Lee Mulligan",
        description = "Reclaiming the ancient Forge of Spells and defeating Nezznar the Black Spider in Wave Echo Cave.",
        characterIds = listOf("char-valeros-fighter", "char-theron-rogue"),
        sessionNotes = listOf(
            SessionNote(
                id = "note-lm-1",
                campaignId = "camp-lostmine",
                title = "Wave Echo Cave Entrance Found",
                date = "Oct 8, 2026",
                content = "Gundren gave us the map! Beware the echoing sound of the rhythmic waves underground. Black Spider has bugbear and doppelganger guards patrolling the fungal caverns.",
                isPinnedToDashboard = true,
                tags = listOf("Map", "Dungeon", "Combat")
            )
        ),
        quests = listOf(
            QuestItem(title = "Defeat the Black Spider", description = "Confront Nezznar in the Temple of Dumathoin deep inside Wave Echo Cave.", isCompleted = false, reward = "10% share in Wave Echo Cave mine"),
            QuestItem(title = "Clear the Redbrand Ruffians", description = "Raid the Redbrand Hideout under Tresendar Manor in Phandalin.", isCompleted = true, reward = "Town gratitude and 100 gp")
        ),
        npcs = listOf(
            NpcItem(name = "Gundren Rockseeker", role = "Dwarf Miner Patron", location = "Stonehill Inn, Phandalin", notes = "Discovered Wave Echo Cave alongside his brothers Nundro and Tharden.", isHostile = false),
            NpcItem(name = "Nezznar the Black Spider", role = "Drow Mage Mastermind", location = "Wave Echo Cave", notes = "Seeking the Forge of Spells to forge shadowy drow artifacts.", isHostile = true)
        )
    )
}
