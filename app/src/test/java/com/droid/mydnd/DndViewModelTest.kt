package com.droid.mydnd

import com.droid.mydnd.data.DndRepositoryImpl
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.model.RollType
import com.droid.mydnd.ui.DndViewModel
import com.droid.mydnd.ui.NavigationTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DndViewModelTest {

    private lateinit var viewModel: DndViewModel
    private lateinit var repository: DndRepositoryImpl

    @Before
    fun setUp() {
        repository = DndRepositoryImpl()
        viewModel = DndViewModel(repository)
    }

    @Test
    fun initialState_hasDefaultTabAndActiveEntities() {
        assertEquals(NavigationTab.COMBAT, viewModel.selectedTab.value)
        assertNotNull(viewModel.activeCharacter.value)
        assertEquals("Valeros Stormblade", viewModel.activeCharacter.value?.name)
        assertNotNull(viewModel.activeCampaign.value)
    }

    @Test
    fun selectTab_updatesTab() {
        viewModel.selectTab(NavigationTab.SPELLBOOK)
        assertEquals(NavigationTab.SPELLBOOK, viewModel.selectedTab.value)
    }

    @Test
    fun rollD20_updatesLastRollAndHistory() {
        viewModel.setAdvantage(RollType.ADVANTAGE)
        viewModel.rollD20(4, "Stealth Check")

        val last = viewModel.lastRoll.value
        assertNotNull(last)
        assertEquals("Stealth Check", last?.label)
        assertEquals(RollType.ADVANTAGE, last?.rollType)
        assertTrue(viewModel.diceHistory.value.isNotEmpty())
    }

    @Test
    fun takeDamageAndHeal_reflectsInActiveCharacter() {
        val initialHp = viewModel.activeCharacter.value?.currentHp ?: 0
        viewModel.takeDamage(15)
        assertEquals(initialHp - 15, viewModel.activeCharacter.value?.currentHp)

        viewModel.heal(10)
        assertEquals(initialHp - 5, viewModel.activeCharacter.value?.currentHp)
    }

    @Test
    fun spellSlots_useAndRestore_updatesState() {
        viewModel.selectCharacter("char-lyra-wizard")
        assertEquals("Lyra Nightbreeze", viewModel.activeCharacter.value?.name)

        viewModel.useSpellSlot(1)
        assertEquals(1, viewModel.activeCharacter.value?.spellSlotsUsed?.get(1))

        viewModel.restoreSpellSlot(1)
        assertEquals(0, viewModel.activeCharacter.value?.spellSlotsUsed?.get(1))
    }

    @Test
    fun addSessionNote_addsToActiveCampaign() {
        viewModel.addSessionNote("Secret Trapdoor", "Hidden beneath the wine barrel.", true)
        val note = viewModel.activeCampaign.value?.sessionNotes?.find { it.title == "Secret Trapdoor" }
        assertNotNull(note)
        assertTrue(note?.isPinnedToDashboard == true)
    }

    @Test
    fun addCustomAction_addsToActiveCharacter() {
        viewModel.addCustomAction(
            name = "Dragon Breath Potion",
            type = ActionType.ITEM,
            attackBonus = null,
            damageFormula = "4d6",
            damageType = "Fire",
            range = "15 ft cone",
            description = "Drink to exhale fire in a 15-ft cone.",
            isPinned = true
        )

        val action = viewModel.activeCharacter.value?.actions?.find { it.name == "Dragon Breath Potion" }
        assertNotNull(action)
        assertTrue(action?.isPinned == true)
        assertTrue(viewModel.activeCharacter.value?.pinnedActionIds?.contains(action?.id) == true)
    }

    @Test
    fun importArchetype_selectsNewCharacter() {
        viewModel.importArchetype("gareth")
        val active = viewModel.activeCharacter.value
        assertNotNull(active)
        assertTrue(active?.name?.contains("Brother Gareth (Copy)") == true)
        assertEquals(NavigationTab.COMBAT, viewModel.selectedTab.value)
    }
}
