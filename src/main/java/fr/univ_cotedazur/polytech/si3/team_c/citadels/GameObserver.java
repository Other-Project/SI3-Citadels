package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObserver {
    private int playersNumber;
    private final Map<String, Integer> coins;
    private final Map<String, Integer> cardsNumber;
    private final Map<String, List<District>> districtsBuilt;

    public GameObserver() {
        playersNumber = 0;
        coins = new HashMap<>();
        cardsNumber = new HashMap<>();
        districtsBuilt = new HashMap<>();
    }

    public void addPlayer(Player p) {
        playersNumber++;
        coins.put(p.getName(), p.getCoins());
        cardsNumber.put(p.getName(), p.getHandDistricts().size());
        districtsBuilt.put(p.getName(), p.getBuiltDistricts());
    }

    public Map<String, Integer> getCoins() {
        return coins;
    }

    public Map<String, List<District>> getBuiltDistrict() {
        return districtsBuilt;
    }

    public Map<String, Integer> getCardsNumber() {
        return cardsNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public void actualise(Player p) {
        if (coins.containsKey(p.getName())) {
            coins.replace(p.getName(), p.getCoins());
            cardsNumber.replace(p.getName(), p.getHandDistricts().size());
            districtsBuilt.replace(p.getName(), p.getBuiltDistricts());
        } else {
            playersNumber++;
            coins.put(p.getName(), p.getCoins());
            cardsNumber.put(p.getName(), p.getHandDistricts().size());
            districtsBuilt.put(p.getName(), p.getBuiltDistricts());
        }
    }
}
