package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (firstPlayer.getKey().equals(this)) {
            var annoyingCharacter = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
        }

        List<IPlayer> enderPlayers = playerCanEndGame();
        IPlayer enderPlayer = enderPlayers.isEmpty() ? null : enderPlayers.get(0);
        // If the bot estimates that the ender player has the best chances to get the stealing or destroying power, we kill that character
        if (enderPlayer != null) {
            Character enderPlayerCharacter = characterEstimation(enderPlayer, CharacterManager.defaultCharacterList());
            if (enderPlayerCharacter.getAction().contains(Action.STEAL) || enderPlayerCharacter.getAction().contains(Action.DESTROY))
                return enderPlayerCharacter;
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
        if (playerCanAttemptFinalRush(character)) {
            return 50;
        }
        if (character.getAction().contains(Action.KILL) && playerCanAttemptFinalRush(characterManager)) {
            return 40;
        }

        return super.characterProfitability(character, characterManager);
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
    private List<IPlayer> playerWillBuildPenultimateDistrict() {
        return getPlayers().stream().filter(player -> player.getBuiltDistricts().size() == getNumberOfDistrictsToEnd() - 2).toList();
    }
}
