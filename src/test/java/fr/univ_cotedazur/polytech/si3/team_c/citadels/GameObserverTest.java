package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Bishop;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.King;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Merchant;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Warlord;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.Harbor;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.Temple;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.TheKeep;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameObserverTest {
    @Test
    void canDestroyTest() {
        Game game = new Game();
        GameObserver gameObserver = new GameObserver(game);
        Bot warlordBot = new Bot("warlordBot", 20, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bot2 = new Bot("bot 2", 10, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Merchant()) ? new Merchant() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bot3 = new Bot("bot 3", 10, List.of(new TheKeep())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new King()) ? new King() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }
        };
        Bot bishopBot = new Bot("bishopBot", 10, List.of(new Harbor(), new Temple())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Bishop()) ? new Bishop() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }
        };
        game.addPlayer(warlordBot);
        game.addPlayer(bot2);
        game.addPlayer(bot3);
        game.addPlayer(bishopBot);
        game.characterSelectionTurn();
        assertFalse(gameObserver.playerCanDestroyOthers(warlordBot));
        assertFalse(gameObserver.playerCanDestroyOthers(bot2));
        game.playerTurn(warlordBot);
        game.playerTurn(bot3); // bot3 builds a non-destroyable district
        assertFalse(gameObserver.playerCanDestroyOthers(warlordBot));
        assertFalse(gameObserver.playerCanDestroyOthers(bot2));
        game.playerTurn(bishopBot);
        assertFalse(gameObserver.playerCanDestroyOthers(warlordBot));// bishopBot can't get attacked
        game.playerTurn(bot2);
        assertTrue(gameObserver.playerCanDestroyOthers(warlordBot));
        game.addPlayer(bishopBot);
    }
}
