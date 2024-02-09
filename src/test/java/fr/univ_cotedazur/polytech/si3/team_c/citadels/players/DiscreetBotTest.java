package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.CharacterManager;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Game;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Bishop;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.King;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DiscreetBotTest {
    @Test
    void tryFinalRushTest() {
        DiscreetBot discreetBot1 = new DiscreetBot("discreetBot1", 50, List.of(new Temple(), new Church(),
                new Docks(), new WatchTower(), new Tavern(), new University(), new DragonGate())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                Character best = characterManager.possibleCharactersToChoose().contains(new King()) ? new King() : characterManager.possibleCharactersToChoose().get(0);
                setCharacter(best);
                return best;
            } // The bot won't pick the architect
        };
        Game game = new Game(1, discreetBot1);
        game.characterSelectionTurn();
        for (int i = 1; i <= 5; i++) {
            game.playerTurn(discreetBot1);
        } // The bot builds 5 districts;
        assertTrue(discreetBot1.tryFinalRush());
        discreetBot1.pay(discreetBot1.getCoins());
        assertFalse(discreetBot1.tryFinalRush());
    }

    @Test
    void differenceWithFirstPlayerTest() {
        DiscreetBot discreetBot = new DiscreetBot("discreetBot", 50, List.of(new Temple(), new WatchTower())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                Character best = characterManager.possibleCharactersToChoose().contains(new King()) ? new King() : characterManager.possibleCharactersToChoose().get(0);
                setCharacter(best);
                return best;
            } // The bot won't pick the architect
        };
        Bot bot = new Bot("bot", 50, List.of(new Docks(), new Church(), new Harbor(), new University())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(CharacterManager characterManager) {
                Character best = characterManager.possibleCharactersToChoose().contains(new Bishop()) ? new Bishop() : characterManager.possibleCharactersToChoose().get(0);
                setCharacter(best);
                return best;
            } // The bot won't pick the architect
        };
        Game game = new Game(2, discreetBot, bot);
        discreetBot.setPlayers(() -> List.of(bot));
        game.characterSelectionTurn();
        game.playerTurn(bot);
        assertEquals(1, discreetBot.differenceOfDistrictsWithFirst());
        game.playerTurn(bot);
        assertEquals(2, discreetBot.differenceOfDistrictsWithFirst());
        game.playerTurn(bot);
        assertEquals(3, discreetBot.differenceOfDistrictsWithFirst());
        game.playerTurn(bot);
        assertEquals(4, discreetBot.differenceOfDistrictsWithFirst());
        game.playerTurn(discreetBot);
        assertEquals(3, discreetBot.differenceOfDistrictsWithFirst());
        game.playerTurn(discreetBot);
        assertEquals(2, discreetBot.differenceOfDistrictsWithFirst());
    }
}
