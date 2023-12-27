package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
