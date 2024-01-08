package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to have a view of the game from a player's perspective and make decisions based on all available informations
 */
public class GameObserver {
    Game game;

    public GameObserver(Game game) {
        this.game = game;
    }

    public Map<String, Integer> getCoins() {
        Map<String, Integer> coins = new HashMap<>();
        for (Player p : game.getPlayerList()) coins.put(p.getName(), p.getCoins());
        return coins;
    }

    public Map<String, List<District>> getBuiltDistrict() {
        Map<String, List<District>> districtsBuilt = new HashMap<>();
        for (Player p : game.getPlayerList()) districtsBuilt.put(p.getName(), p.getBuiltDistricts());
        return districtsBuilt;
    }

    public Map<String, Integer> getCardsNumber() {
        Map<String, Integer> numberCards = new HashMap<>();
        for (Player p : game.getPlayerList()) numberCards.put(p.getName(), p.getHandDistricts().size());
        return numberCards;
    }

    public int getPlayersNumber() {
        return game.getPlayerList().size();
    }

    public List<Player> getPlayerList() {
        return game.getPlayerList();
    }

    public Map<String, Character> getCharacters() {
        Map<String, Character> res = new HashMap<>();
        for (Player player : game.getPlayerList()) {
            res.put(player.getName(), player.getCharacter().orElseThrow());
        }
        return res;
    }


    /**
     * returns true if a player is able to destroy at least a district of other players
     */
    public boolean playerCanDestroyOthers(Player player) {
        Map<String, Character> characterList = getCharacters();
        for (Map.Entry<String, List<District>> mapEntry : getBuiltDistrict().entrySet()) {
            if (!characterList.get(mapEntry.getKey()).canHaveADistrictDestroyed() || mapEntry.getKey().equals(player.getName())) {
                continue;
            }// If a player can't get attacked, he is not put in the map
            mapEntry.setValue(mapEntry.getValue().stream()
                    .filter(district -> district.getCost() - 1 <= player.getCoins()).filter(District::isDestructible).toList());
            if (!mapEntry.getValue().isEmpty()) return true;
            // If we're here, it means it exists at least one district that can get destroyed
        }
        return false;
    }
}
