package com.droid.mydnd

import com.droid.mydnd.data.DndRepositoryImpl
import com.droid.mydnd.model.Combatant
import com.droid.mydnd.model.RollType
import com.droid.mydnd.model.SessionNote
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DndRepositoryTest {

    private lateinit var repository: DndRepositoryImpl

    @Before
    fun setUp() {
        repository = DndRepositoryImpl()
    }

    @Test
    fun initialRepository_hasPreloadedCharactersAndCampaigns() {
        val chars = repository.characters.value
        val camps = repository.campaigns.value
        val activeChar = repository.activeCharacter.value
        val activeCamp = repository.activeCampaign.value

        assertEquals(4, chars.size)
        assertEquals(2, camps.size)
        assertNotNull(activeChar)
        assertNotNull(activeCamp)
        assertEquals("Valeros Stormblade", activeChar?.name)
        assertEquals("Curse of Strahd - The Mists of Barovia", activeCamp?.title)
    }

    @Test
    fun selectCharacter_updatesActiveCharacter() {
        val wizardId = "char-lyra-wizard"
        repository.selectCharacter(wizardId)

        assertEquals("Lyra Nightbreeze", repository.activeCharacter.value?.name)
    }

    @Test
    fun takeDamageAndHeal_updatesActiveCharacterHp() {
        val initialHp = repository.activeCharacter.value?.currentHp ?: 0
        repository.takeDamage(10)
        assertEquals(initialHp - 10, repository.activeCharacter.value?.currentHp)

        repository.heal(5)
        assertEquals(initialHp - 5, repository.activeCharacter.value?.currentHp)
    }

    @Test
    fun sessionNote_addAndPin() {
        val activeCampId = repository.activeCampaign.value?.id ?: ""
        val note = SessionNote(
            campaignId = activeCampId,
            title = "Secret Crypt Lever",
            date = "Oct 11, 2026",
            content = "Pull the lion head lever to open the sarcophagus secret stairs.",
            isPinnedToDashboard = true
        )

        repository.addSessionNote(note)

        val camp = repository.activeCampaign.value
        val foundNote = camp?.sessionNotes?.find { it.title == "Secret Crypt Lever" }
        assertNotNull(foundNote)
        assertTrue(foundNote?.isPinnedToDashboard == true)

        // Toggle pin
        repository.togglePinSessionNote(foundNote!!.id)
        val updatedNote = repository.activeCampaign.value?.sessionNotes?.find { it.id == foundNote.id }
        assertEquals(false, updatedNote?.isPinnedToDashboard)
    }

    @Test
    fun combatant_turnAdvancement() {
        val initialFirst = repository.combatants.value.first()
        assertTrue(initialFirst.isCurrentTurn)

        repository.nextCombatTurn()
        val second = repository.combatants.value[1]
        assertTrue(second.isCurrentTurn)
    }

    @Test
    fun diceRolling_recordsInHistory() {
        val roll = repository.rollD20(RollType.NORMAL, 5, "Sword Attack")
        val history = repository.diceHistory.value

        assertEquals(1, history.size)
        assertEquals(roll.total, history.first().total)
        assertEquals("Sword Attack", history.first().label)
    }

    @Test
    fun importArchetype_createsDistinctCopy() {
        val initialCount = repository.characters.value.size
        val copy = repository.importArchetype("lyra")

        assertEquals(initialCount + 1, repository.characters.value.size)
        assertTrue(copy.name.contains("Lyra Nightbreeze (Copy)"))
        assertEquals(copy.id, repository.activeCharacter.value?.id)
    }
}
