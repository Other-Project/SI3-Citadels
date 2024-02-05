package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Assassin;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Magician;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Merchant;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Warlord;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.Bot;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CharacterManagerTest {
    @Test
    void characterConstructorTest() {
        Character magician = new Magician();
        Character merchant = new Merchant();

        CharacterManager characterManager = new CharacterManager(3, new Random(), List.of(new Assassin(), magician, merchant, new Warlord()));

        assertEquals(List.of(new Magician(), new Merchant(), new Warlord()), characterManager.charactersList());

        characterManager.generate();
        assertEquals(2, characterManager.getVisible().size());
        assertEquals(1, characterManager.getHidden().size());

        characterManager.addPlayerCharacter(new Bot("bot1"), magician);
        characterManager.addPlayerCharacter(new Bot("bot2"), merchant);

        assertFalse(characterManager.getAvailableCharacters().contains(magician));
        assertFalse(characterManager.possibleCharactersToChoose().contains(merchant));
    }
}