package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class RichardBot extends Bot {
    public RichardBot(String name) {
        this(name, 0, Collections.emptyList());
    }

    public RichardBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        // If the bot is in first place, he must kill the characters that could destroy one of his districts
        SimpleEntry<IPlayer, Integer> firstPlayer = playerWithMaxAttribute(iPlayer -> iPlayer.getBuiltDistricts().size());
        firstPlayer = firstPlayer.getValue() < this.getBuiltDistricts().size() ?
                new SimpleEntry<>(this, this.getBuiltDistricts().size()) : firstPlayer;
        if (firstPlayer.getKey().equals(this)) {
            var annoyingCharacter = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }

        List<IPlayer> enderPlayers = playerCanEndGame();
        IPlayer enderPlayer = enderPlayers.isEmpty() ? null : enderPlayers.get(0);
        // If the bot estimates that the ender player has the best chances to get the stealing power, we kill that character */
        if (enderPlayer != null) {
            Character enderPlayerCharacter = characterEstimation(enderPlayer, CharacterManager.defaultCharacterList());
            if (enderPlayerCharacter.getAction().contains(Action.STEAL)) return enderPlayerCharacter;
            else if (hasCrown()) {
                /* If we're here, it means that the bot must kill a character that can destroy,
                or a character that can't get his districts destroyed */
                var characterToKill = characterList.stream().filter(
                        character -> character.getAction().contains(Action.DESTROY)
                                || !character.canHaveADistrictDestroyed()).findFirst();
                if (characterToKill.isPresent()) return characterToKill.get();
            } else {
                var characterToKill = characterList.stream().filter(character -> !character.canHaveADistrictDestroyed()).findFirst();
                if (characterToKill.isPresent()) return characterToKill.get();
            }
        }

        // With this condition, a player cannot get too rich by stealing someone
        int maxCoins = playerWithMaxAttribute(IPlayer::getCoins).getValue();
        // Here, Richard didn't think about the extreme case, where there's one rich player and all the other don't have any coin
        if (potentialAmountOfCoins() >= 6 && potentialAmountOfCoins() > maxCoins) {
            var annoyingCharacter = characterList.stream().filter(character -> character.getAction().contains(Action.STEAL)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }

        // If a player will build his penultimate district, we kill the character that gives the crown
        List<IPlayer> willBuildPenultimate = betterPlayerWillBuildPenultimateDistrict();
        if (willBuildPenultimate.size() == 1 && willBuildPenultimate.get(0).hasCrown()) {
            for (Character character : characterList) {
                if (character.getAction().contains(Action.GET_CROWN)) return character;
            }
        }
        return super.chooseCharacterToKill(characterList);
    }

    @Override
    protected double characterProfitability(Character character, CharacterManager characterManager) {
        // If the bot can end the game, he will try to take a character that avoids another player to destroy one of his districts
        if (canEnd() &&
                (character.getAction().contains(Action.KILL)
                        || !character.canHaveADistrictDestroyed()
                        || character.getAction().contains(Action.DESTROY)))
            return (1.0 / character.getTurn()) * 1000;

        List<IPlayer> gameEnder = playerCanEndGame();
        if (gameEnder.size() == 1) {
            List<Character> comboCharacters = containsComboCharacters(characterManager);
            switch (comboCharacters.size()) {
                case 3:
                    /* We need first to destroy, and in a second time kill a character that can't have districts destroyed,
                    so that's why we don't choose him in this case */
                    if (character.getAction().contains(Action.DESTROY)) return 800;
                    else if (character.getAction().contains(Action.KILL)) return 700;
                    else if (!character.canHaveADistrictDestroyed()) return -1;
                    break;
                case 2:
                    if (comboCharacters.stream().filter(character1 -> !character1.canHaveADistrictDestroyed()).findFirst().isEmpty()) {
                        if (character.getAction().contains(Action.KILL)) return 750;
                        else if (character.getAction().contains(Action.DESTROY)) return 650;
                    } else if (comboCharacters.stream().filter(character1 -> character1.getAction().contains(Action.DESTROY)).findFirst().isEmpty()) {
                        if (character.getAction().contains(Action.KILL)) return 650;
                        else if (character.getAction().contains(Action.EXCHANGE_PLAYER)) return 550;
                    } else if (comboCharacters.stream().filter(character1 -> character1.getAction().contains(Action.KILL)).findFirst().isEmpty()) {
                        if (character.getAction().contains(Action.DESTROY)) return 400;
                        else if (!character.canHaveADistrictDestroyed()) return 300;
                    }
                    break;
                default:
                    // Too many characters are missing, so we must take first characters to play
                    return (1.0 / character.getTurn()) * 1000;

            }
        }

        if (playerCanAttemptFinalRush(character)) {
            // If a player can attend a final rush with character, we will take that character
            return hasCrown() && !character.getAction().contains(Action.KILL) ? 100 : 80;
        }

        List<IPlayer> playerBuildPenultimate = betterPlayerWillBuildPenultimateDistrict();
        if (playerBuildPenultimate.size() == 1) {
            if (character.getAction().contains(Action.GET_CROWN)) {
                return 50;
            } else if (character.getAction().contains(Action.KILL)) {
                return 40;
            } else if (character.getAction().contains(Action.DESTROY)) {
                return 30;
            } else if (!character.canHaveADistrictDestroyed()) {
                return 20;
            } else if (character.getAction().contains(Action.EXCHANGE_PLAYER)) {
                return 15;
            } else if (character.getAction().contains(Action.STEAL)) {
                return 10;
            }

        }

        return super.characterProfitability(character, characterManager);
    }

    private List<Character> containsComboCharacters(CharacterManager characterManager) {
        List<Character> res = new ArrayList<>();
        for (Character character : characterManager.getAvailableCharacters()) {
            if (character.getAction().contains(Action.KILL)
                    || character.getAction().contains(Action.DESTROY)
                    || !character.canHaveADistrictDestroyed()) {
                res.add(character);
            }
        }
        return res;
    }

    /**
     * This method returns the players that can end the current game
     *
     * @return the player List
     */
    public List<IPlayer> playerCanEndGame() {
        List<IPlayer> resPlayers = new ArrayList<>();
        for (IPlayer player : getPlayers()) {
            if (player.getBuiltDistricts().size() + 1 >= getNumberOfDistrictsToEnd()) resPlayers.add(player);
        }
        return resPlayers;
    }

    /**
     * This method returns the potential maximum amount of coins a player can have by stealing someone else
     */
    private int potentialAmountOfCoins() {
        int potentialMaxCoins = 0;
        var players = getPlayers();
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = 1; j < players.size(); j++) {
                int currentRes = players.get(i).getCoins() + players.get(j).getCoins();
                if (currentRes >= potentialMaxCoins) potentialMaxCoins = currentRes;
            }
        }
        return potentialMaxCoins;
    }

    /**
     * Detects if a player can attempt a final rush given all the characters except the visible ones
     *
     * @param characterManager to determine the characters to process
     */
    private boolean playerCanAttemptFinalRush(CharacterManager characterManager) {
        for (Character character : characterManager.charactersList().stream()
                .filter(character -> !characterManager.getVisible().contains(character)).toList()) {
            if (playerCanAttemptFinalRush(character)) return true;
        }
        return false;
    }

    /**
     * Detects if a player can attend a final rush with the given character
     */
    private boolean playerCanAttemptFinalRush(Character character) {
        for (IPlayer player : getPlayers()) {
            if (player.getCoins() >= 4
                    && getNumberOfDistrictsToEnd() - player.getBuiltDistricts().size() != 1
                    && player.getHandSize() >= character.numberOfDistrictToBuild() - (character.startTurnAction().equals(Action.BEGIN_DRAW) ? 2 : 0)
                    && player.getBuiltDistricts().size() >= getNumberOfDistrictsToEnd() - character.numberOfDistrictToBuild())
                return true;
        }
        return false;
    }

    /**
     * Detects if a player is on the verge of building his penultimate district
     *
     */
    private List<IPlayer> betterPlayerWillBuildPenultimateDistrict() {
        return getPlayers().stream().filter(player ->
                player.getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2
                        && player.getBuiltDistricts().size() > this.getBuiltDistricts().size()).toList();
    }

    /**
     * Detects if the current bot can end the game
     */
    private boolean canEnd() {
        for (District district : getHandDistricts()) {
            if (district.getCost() <= getCoins() && getBuiltDistricts().size() == getNumberOfDistrictsToEnd())
                return true;
        }
        return false;
    }

    @Override
    public SimpleEntry<IPlayer, District> destroyDistrict(List<IPlayer> players) {
        /* We try to block the player that will build his penultimate district as the warlord,
        so we try to destroy the smallest district to block him */
        List<IPlayer> willBuildPenultimate = betterPlayerWillBuildPenultimateDistrict();
        if (!willBuildPenultimate.isEmpty()) {
            Optional<District> districtToDestroy = willBuildPenultimate.stream()
                    .flatMap(player -> player.getDestroyableDistricts().stream().filter(district -> district.getCost() - 1 <= getCoins()))
                    .min(Comparator.comparingInt(District::getCost));
            if (districtToDestroy.isPresent())
                return new SimpleEntry<>(willBuildPenultimate.get(0), districtToDestroy.get());
        }

        return super.destroyDistrict(players);
    }
}
