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
        super(name, coins, districts, 1.0);
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        IPlayer enderPlayer = playerCanEndGame().get(0);
        // If the bot estimates that the ender player has the best chances to get the stealing or destroying power, we kill that character
        if (enderPlayer != null) {
            Character enderPlayerCharacter = characterEstimation(enderPlayer, Game.defaultCharacterList());
            if (enderPlayerCharacter.getAction().contains(Action.STEAL) || enderPlayerCharacter.getAction().contains(Action.DESTROY))
                return enderPlayerCharacter;
        }

        // If the bot is in first place, he must kill the characters that could destroy one of his districts
        SimpleEntry<IPlayer, Integer> firstPlayer = playerWithMaxAttribute(iPlayer -> iPlayer.getBuiltDistricts().size());
        if (firstPlayer.getKey().equals(this)) {
            var annoyingCharacter = characterList.stream().filter(character -> character.getAction().contains(Action.DESTROY)).findFirst();
            if (annoyingCharacter.isPresent()) return annoyingCharacter.get();
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

    /**
     * This method returns the players that can end the current game
     *
     * @return the player List
     */
    public List<IPlayer> playerCanEndGame() {
        List<IPlayer> resPlayers = new ArrayList<>();
        for (IPlayer player : getPlayers()) {
            if (player.getBuiltDistricts().size() + 1 == Game.DISTRICT_NUMBER_TO_END) resPlayers.add(player);
        }
        return resPlayers;
    }

    /**
     * This method returns the potential maximum amount of coins a player can have by stealing someone else
     */
    private int potentialAmountOfCoins() {
        int potentialMaxCoins = 0;
        for (int i = 0; i < getPlayers().size() - 1; i++) {
            for (int j = 1; j < getPlayers().size(); j++) {
                if (getPlayers().get(i).equals(this) || getPlayers().get(j).equals(this)) continue;
                int currentRes = getPlayers().get(i).getCoins() + getPlayers().get(j).getCoins();
                if (currentRes >= potentialMaxCoins) potentialMaxCoins = currentRes;
            }
        }
        return potentialMaxCoins;
    }
}
