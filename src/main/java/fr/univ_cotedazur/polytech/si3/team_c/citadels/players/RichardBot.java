package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class RichardBot extends Bot {
    private boolean playCombo;
    public RichardBot(String name) {
        this(name, 0, Collections.emptyList());
        playCombo = false;
    }

    public RichardBot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
        playCombo = false;
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        // If a player will build his penultimate district, we kill the character that gives the crown
        List<IPlayer> willBuildPenultimate = betterPlayerWillBuildPenultimateDistrict();
        if (willBuildPenultimate.size() == 1 && willBuildPenultimate.get(0).hasCrown() && !willBuildPenultimate.contains(this)) {
            for (Character character : characterList) {
                if (character.startTurnAction().equals(Action.GET_CROWN)) return character;
            }
        }

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
            if (playCombo) {
                if (hasCrown()) {
                    if (enderPlayer.getHandSize() == 0) {
                        // Case 3
                        // The ender player will try to steal cards, so we kill a character that can exchange cards with players
                        var characterToKill = characterList.stream().filter(character -> character.getAction().contains(Action.EXCHANGE_PLAYER)).findFirst();
                        if (characterToKill.isPresent()) return characterToKill.get();
                    } else {
                        // Case 2
                        // The bot need to kill any character except characters that can destroy districts
                        var characterToKill = characterList.stream().filter(character -> !character.getAction().contains(Action.DESTROY)).findFirst();
                        if (characterToKill.isPresent()) return characterToKill.get();
                    }
                } else {
                    var characterToKill = characterList.stream().filter(character -> !character.canHaveADistrictDestroyed()).findFirst();
                    if (characterToKill.isPresent()) return characterToKill.get();
                }
                Character enderPlayerCharacter = characterEstimation(enderPlayer, CharacterManager.defaultCharacterList());
                if (enderPlayerCharacter.getAction().contains(Action.STEAL)) return enderPlayerCharacter;
                playCombo = false;
            } else {
                // Here, the ender player must be 1st or 2nd to choose a character
                var characterToKill = characterList.stream().filter(
                        character -> !character.canHaveADistrictDestroyed()
                                || character.getAction().contains(Action.DESTROY)
                                || character.getAction().contains(Action.STEAL)).findAny();
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
            playCombo = true;
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
                        else if (character.getAction().contains(Action.EXCHANGE_PLAYER) && getHandDistricts().size() <= 2)
                            return 550;
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

        List<IPlayer> playerBuildPenultimate = betterPlayerWillBuildPenultimateDistrict();
        if (playerBuildPenultimate.size() == 1) {
            if (character.startTurnAction().equals(Action.GET_CROWN)) {
                return 150;
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

        // This part is dedicated to block any player that can attempt a final rush
        if (hasCrown()
                && (playerCanAttemptFinalRush(character)
                || playerCanAttemptFinalRush(characterManager) && character.getAction().contains(Action.KILL))) {
            /* If a player can attempt a final rush with the given character or with another and the given character can kill,
            he must take the given character */
            return 100;
        }
        if (!hasCrown()
                && playerCanAttemptFinalRush(characterManager) && character.getAction().contains(Action.KILL)) {
            // If the bot is not first, at this state he must take the character that can kill
            return 100;
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
                    && player.getBuiltDistricts().size() >= getNumberOfDistrictsToEnd() - character.numberOfDistrictToBuild()
                    && player.getBuiltDistricts().size() <= 5)
                return true;
        }
        return false;
    }

    /**
     * Detects if a player is on the verge of building his penultimate district
     *
     */
    private List<IPlayer> betterPlayerWillBuildPenultimateDistrict() {
        List<IPlayer> players = getPlayers();
        players.add(this);
        return players.stream().filter(player ->
                player.getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2
                        && player.getBuiltDistricts().size() >= this.getBuiltDistricts().size()).toList();
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
        willBuildPenultimate.remove(this);
        if (!willBuildPenultimate.isEmpty()) {
            Optional<District> districtToDestroy = willBuildPenultimate.stream()
                    .flatMap(player -> player.getDestroyableDistricts().stream().filter(district -> district.getCost() - 1 <= getCoins()))
                    .min(Comparator.comparingInt(District::getCost));
            if (districtToDestroy.isPresent())
                return new SimpleEntry<>(willBuildPenultimate.get(0), districtToDestroy.get());
        }

        return super.destroyDistrict(players);
    }

    @Override
    public IPlayer playerToExchangeCards(List<IPlayer> players) {
        if (playCombo) return playerCanEndGame().get(0);
        return super.playerToExchangeCards(players);
    }
}