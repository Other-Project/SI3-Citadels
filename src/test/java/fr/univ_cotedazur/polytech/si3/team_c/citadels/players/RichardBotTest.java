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

import static org.junit.jupiter.api.Assertions.*;

class RichardBotTest {

    Game game;

    RichardBot richardBot1, richardBot2, richardBot3, richardBot4;
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

        bot1 = new Bot("Bot 1") {
            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                setCharacter(new Thief());
                return new Thief();
            }
        };
        bot2 = new Bot("Bot 2") {
            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                setCharacter(new Warlord());
                return new Warlord();
            }
        };

        //NO CONDITION VERIFIED
        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        game.playerInitialization();
        game.characterSelectionTurn();
        assertTrue(richardBot1.charactersNotToKill(listCharacter).contains(new Warlord()));
        assertTrue(richardBot1.charactersNotToKill(listCharacter).contains(new Thief()));
        game.getPlayerList().forEach(Player::resetPlayer);

        //JUST ONE WARLORD CONDITION, I AM THE FIRST PLAYER DUE TO POINTS VERIFIED
        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        listCharacter.clear();
        listCharacter.addAll(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        game.playerInitialization();
        game.characterSelectionTurn();
        richardBot1.gainCoins(6);
        richardBot1.buildDistrict(richardBot1.getHandDistricts().get(0), 0);
        richardBot1.pay(richardBot1.getCoins());
        assertEquals(List.of(new Thief()), richardBot1.charactersNotToKill(listCharacter));
        game.getPlayerList().forEach(Player::resetPlayer);

        //JUST ONE THIEF CONDITION, I DON'T WANT ONE PLAYER COULD SIMPLY BECOME RICH
        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        listCharacter.clear();
        listCharacter.addAll(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        game.playerInitialization();
        game.characterSelectionTurn();
        bot1.gainCoins(2);
        bot2.gainCoins(3);
        assertEquals(List.of(new Warlord()), richardBot1.charactersNotToKill(listCharacter));
        game.getPlayerList().forEach(Player::resetPlayer);

        //TWO CONDITION VERIFIED, ONE FOR THE THIEF AND ONE FOR THE WARLORD, FUSION OF TEH TWO TEST OVER
        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        game.playerInitialization();
        listCharacter.clear();
        listCharacter.addAll(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        game.characterSelectionTurn();
        bot1.gainCoins(2);
        bot2.gainCoins(3);
        richardBot1.gainCoins(6);
        richardBot1.buildDistrict(richardBot1.getHandDistricts().get(0), 0);
        richardBot1.pay(richardBot1.getCoins());
        assertEquals(List.of(), richardBot1.charactersNotToKill(listCharacter));
        game.getPlayerList().forEach(Player::resetPlayer);


        //JUST ONE WARLORD CONDITION, I AM SURE THAT THE WARLORD HAS BEEN TAKE BY SOMEBODY COULD WIN
        game = new Game(3, characterManager, richardBot1, bot1, bot2);
        game.playerInitialization();
        listCharacter.clear();
        listCharacter.addAll(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        bot2.setCrown();
        bot2.gainCoins(30);
        List<District> districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Fortress(), new WatchTower(), new Monastery(), new Manor());
        districts.forEach(district -> bot2.addDistrictToHand(district));
        districts.forEach(district -> bot2.buildDistrict(district, 0));
        game.characterSelectionTurn();
        assertEquals(List.of(new Thief()), richardBot1.charactersNotToKill(listCharacter));
        game.getPlayerList().forEach(Player::resetPlayer);

        //JUST ONE THIEF CONDITION, I AM SURE THAT THE THIEF HAS BEEN TAKE BY SOMEBODY COULD WIN
        game = new Game(3, characterManager, bot1, richardBot1, bot2);
        game.playerInitialization();
        listCharacter.clear();
        listCharacter.addAll(List.of(new Thief(), new Assassin(), new Warlord(), new Architect()));
        bot1.setCrown();
        bot1.gainCoins(30);
        districts.forEach(district -> bot1.addDistrictToHand(district));
        districts.forEach(district -> bot1.buildDistrict(district, 0));
        game.characterSelectionTurn();
        assertEquals(List.of(new Warlord()), richardBot1.charactersNotToKill(listCharacter));
        game.getPlayerList().forEach(Player::resetPlayer);
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

            @Override
            public List<Character> getAvailableCharacters() {
                return listCharacter;
            }

            @Override
            protected void setHiddenDiscard() {
            }

            @Override
            protected void setVisibleDiscard() {
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1");
        richardBot2 = new RichardBot("Richard Bot 2");
        richardBot3 = new RichardBot("Richard Bot 3");
        richardBot4 = new RichardBot("Richard Bot 4");
        game = new Game(3, characterManager, richardBot1, richardBot2, richardBot3, richardBot4);
        game.playerInitialization();
        richardBot1.setCrown();

        List<District> districts = List.of(new Laboratory(), new Church(), new HauntedCity(), new Castle());
        districts.forEach(district -> richardBot2.addDistrictToHand(district));

        richardBot3.gainCoins(42);
        districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Church(), new Castle());
        districts.forEach(district -> richardBot3.addDistrictToHand(district));
        richardBot3.getHandDistricts().forEach(district -> richardBot3.buildDistrict(district, 0));
        richardBot3.removeFromHand(richardBot3.getHandDistricts());

        ///1
        game.characterSelectionTurn();
        assertEquals(new Warlord(), richardBot1.getCharacter().orElseThrow());
        assertEquals(new Assassin(), richardBot2.getCharacter().orElseThrow());
        assertEquals(new Bishop(), richardBot2.chooseCharacterToKill(game.getCharactersToInteractWith()));

        ///2
        listCharacter.clear();
        listCharacter.addAll(CharacterManager.defaultCharacterList());
        listCharacter.remove(new Bishop());
        game.characterSelectionTurn();
        assertEquals(new Assassin(), richardBot1.getCharacter().orElseThrow());
        assertEquals(List.of(new Warlord()), richardBot1.charactersNotToKill(game.getCharactersToInteractWith()));
        assertEquals(new Warlord(), richardBot2.getCharacter().orElseThrow());


        //3 Part 1
        listCharacter.clear();
        listCharacter.addAll(CharacterManager.defaultCharacterList());
        listCharacter.remove(new Warlord());
        game.characterSelectionTurn();
        assertEquals(new Assassin(), richardBot1.getCharacter().orElseThrow());
        assertEquals(new Magician(), richardBot1.chooseCharacterToKill(listCharacter));
        listCharacter.remove(new Assassin());
        assertNotEquals(new Magician(), richardBot2.getCharacter().orElseThrow());

        //4 Part
        listCharacter.clear();
        listCharacter.addAll(CharacterManager.defaultCharacterList());
        listCharacter.remove(new Assassin());
        game.characterSelectionTurn();
        assertEquals(new Warlord(), richardBot1.getCharacter().orElseThrow());
        listCharacter.remove(new Warlord());
        assertEquals(new Bishop(), richardBot2.getCharacter().orElseThrow());
    }

    @Test
    void LastTurnPart2Test() {
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

            @Override
            public List<Character> getAvailableCharacters() {
                return listCharacter;
            }

            @Override
            protected void setHiddenDiscard() {
            }

            @Override
            protected void setVisibleDiscard() {
            }
        };
        richardBot1 = new RichardBot("Richard Bot 1");
        richardBot2 = new RichardBot("Richard Bot 2");
        richardBot3 = new RichardBot("Richard Bot 3");
        richardBot4 = new RichardBot("Richard Bot 4");
        game = new Game(3, characterManager, richardBot1, richardBot2, richardBot3, richardBot4);
        game.playerInitialization();
        richardBot1.setCrown();

        richardBot3.gainCoins(42);
        List<District> districts = List.of(new DragonGate(), new University(), new Tavern(), new Battlefield(), new Prison(), new Church(), new Castle());
        districts.forEach(district -> richardBot3.addDistrictToHand(district));
        richardBot3.getHandDistricts().forEach(district -> richardBot3.buildDistrict(district, 0));
        richardBot3.removeFromHand(richardBot3.getHandDistricts());

        //3 Part 2
        listCharacter.remove(new Warlord());
        game.characterSelectionTurn();
        assertEquals(new Assassin(), richardBot1.getCharacter().orElseThrow());
        assertNotEquals(new Magician(), richardBot1.chooseCharacterToKill(listCharacter));
        listCharacter.remove(new Assassin());
        assertEquals(new Magician(), richardBot2.getCharacter().orElseThrow());
        assertEquals(richardBot3, richardBot2.playerToExchangeCards(richardBot2.getPlayersWithYou()));

    }
}
