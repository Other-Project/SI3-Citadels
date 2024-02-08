package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RichardBotTest {

    Game game;

    RichardBot richardBot1, richardBot2;
    Bot bot1, bot2;

    CharacterManager characterManager;

    @BeforeEach
    void setUp() {
        bot1 = new Bot("Bot 1");
        bot2 = new Bot("Bot 2");
    }

    @Test
    void assassinateStrategyTest() {
        List<Character> listCharacter = new ArrayList<>(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        characterManager = new CharacterManager(3, new Random(), listCharacter) {
            @Override
            public List<Character> getAvailableCharacters() {
                return super.getAvailableCharacters();
            }

            @Override
            public List<Character> possibleCharactersToChoose() {
                return listCharacter;
            }

            @Override
            public List<Character> charactersList() {
                return listCharacter;
            }

            @Override
            protected void setHiddenDiscard() {
            }

            @Override
            protected void setVisibleDiscard() {
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1") {
            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                setCharacter(new Assassin());
                return new Assassin();
            }
        };

        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        game.playerInitialization();
        bot2.setCrown();
        richardBot1.gainCoins(3);
        bot1.gainCoins(2);
        bot2.gainCoins(3);
        game.characterSelectionTurn();
        listCharacter.remove(new Assassin());
        assertEquals(List.of(new Warlord()), richardBot1.removeCharacters(listCharacter));
        bot2.pay(4);
        assertEquals(List.of(new Thief(), new Warlord()), richardBot1.removeCharacters(listCharacter));

        richardBot1.gainCoins(6);
        richardBot1.buildDistrict(richardBot1.getHandDistricts().get(0), 0);
        richardBot1.pay(richardBot1.getCoins());
        assertEquals(List.of(new Thief()), richardBot1.removeCharacters(listCharacter));

        bot2.gainCoins(30);
        List<District> districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Fortress(), new WatchTower(), new Monastery(), new Manor());
        districts.forEach(district -> bot2.addDistrictToHand(district));
        districts.forEach(district -> bot2.buildDistrict(district, 0));
        assertEquals(List.of(), richardBot1.removeCharacters(listCharacter));
    }

    @Test
    void architectStrategyTest() {
        List<Character> listCharacter = new ArrayList<>(CharacterManager.defaultCharacterList());
        characterManager = new CharacterManager(4, new Random(), listCharacter) {
            @Override
            public List<Character> possibleCharactersToChoose() {
                return listCharacter;
            }

            @Override
            public List<Character> charactersList() {
                return listCharacter;
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1");
        richardBot2 = new RichardBot("Richard Bot 2");
        game = new Game(4, characterManager, richardBot1, bot1, bot2, richardBot2);
        game.playerInitialization();
        bot2.gainCoins(30);
        List<District> districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison());
        districts.forEach(district -> bot2.addDistrictToHand(district));
        districts.forEach(district -> bot2.buildDistrict(district, 0));
        richardBot2.setCrown();
        assertEquals(new Assassin(), richardBot2.pickCharacter(characterManager));
        richardBot2.resetCrown();
        richardBot1.setCrown();
        listCharacter.remove(new Assassin());
        assertEquals(new Architect(), richardBot1.pickCharacter(characterManager));
    }

    @Test
    void kingStrategyTest() {
        List<Character> listCharacter = new ArrayList<>(CharacterManager.defaultCharacterList());
        characterManager = new CharacterManager(3, new Random(), listCharacter) {
            @Override
            public List<Character> possibleCharactersToChoose() {
                return listCharacter;
            }

            @Override
            public List<Character> charactersList() {
                return listCharacter;
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1") {
            @Override
            List<Character> getPossibleCharacters(IPlayer iPlayer) {
                return new ArrayList<>(List.of(new King(), new Merchant(), new Architect()));
            }
        };
        richardBot2 = new RichardBot("Richard Bot 2");
        game = new Game(3, characterManager, richardBot1, bot1, bot2, richardBot2);
        game.playerInitialization();

        richardBot2.gainCoins(22);
        List<District> districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Manor());
        districts.forEach(district -> richardBot2.addDistrictToHand(district));
        districts.forEach(district -> richardBot2.buildDistrict(district, 0));
        assertEquals(new King(), richardBot1.pickCharacter(characterManager));
        assertEquals(new King(), richardBot2.pickCharacter(characterManager));
        richardBot2.setCrown();
        listCharacter.remove(new King());
        assertEquals(new Assassin(), richardBot1.pickCharacter(characterManager));
        assertEquals(new King(), richardBot1.chooseCharacterToKill(List.of(new Assassin(), new King())));
        listCharacter.remove(new Assassin());
        assertEquals(new Warlord(), richardBot1.pickCharacter(characterManager));
        listCharacter.remove(new Warlord());
        assertEquals(new Bishop(), richardBot1.pickCharacter(characterManager));
    }

    @Test
    void LastTurnTest() {
        List<Character> listCharacter = new ArrayList<>(CharacterManager.defaultCharacterList());
        characterManager = new CharacterManager(3, new Random(), listCharacter) {
            @Override
            public List<Character> possibleCharactersToChoose() {
                return listCharacter;
            }

            @Override
            public List<Character> charactersList() {
                return listCharacter;
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1");
        richardBot2 = new RichardBot("Richard Bot 2");
        game = new Game(3, characterManager, richardBot1, richardBot2, bot1, bot2);
        game.playerInitialization();
        richardBot1.setCrown();

        List<District> districts = List.of(new Laboratory(), new Church(), new HauntedCity(), new Castle());
        districts.forEach(district -> richardBot2.addDistrictToHand(district));

        bot1.gainCoins(30);
        districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Fortress(), new WatchTower());
        districts.forEach(district -> bot1.addDistrictToHand(district));
        districts.forEach(district -> bot1.buildDistrict(district, 0));

        ///1
        assertEquals(new Warlord(), richardBot1.pickCharacter(characterManager));
        listCharacter.remove(new Warlord());
        assertEquals(new Assassin(), richardBot2.pickCharacter(characterManager));
        listCharacter.remove(new Assassin());
        assertEquals(new Bishop(), richardBot2.chooseCharacterToKill(listCharacter));

        ///2
        listCharacter.clear();
        listCharacter.addAll(CharacterManager.defaultCharacterList());
        listCharacter.remove(new Bishop());
        /*assertEquals(new Assassin(), richardBot1.pickCharacter(characterManager));
        listCharacter.remove(new Assassin());
        assertEquals(List.of(new Warlord()), richardBot1.removeCharacters(listCharacter));
        assertEquals(new Magician(), richardBot1.chooseCharacterToKill(listCharacter));
        listCharacter.remove(new Assassin());
        assertEquals(new Magician(), richardBot2.pickCharacter(characterManager));

        listCharacter.add(new Warlord());
        assertEquals(new Warlord(), richardBot1.pickCharacter(characterManager));
        listCharacter.remove(new Warlord());
        assertEquals(new Bishop(), richardBot2.pickCharacter(characterManager));

        listCharacter.remove(new Bishop());
        listCharacter.add(new Warlord());
        listCharacter.add(new Assassin());
        assertEquals(new Assassin(), richardBot1.pickCharacter(characterManager));
        assertNotEquals(new Warlord(), richardBot1.chooseCharacterToKill(listCharacter));
        listCharacter.remove(new Assassin());
        assertEquals(new Warlord(), richardBot2.pickCharacter(characterManager));*/
    }
}
