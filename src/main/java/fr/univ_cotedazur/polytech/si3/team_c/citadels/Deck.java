package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<District> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i < 2) {
                cards.add(new Cathedral());
                cards.add(new Palace());
                cards.add(new TownHall());
                cards.add(new Fortress());
                cards.add(new TheKeep());
            }
            if (i < 3) {
                cards.add(new Monastery());
                cards.add(new Temple());
                cards.add(new TradingPost());
                cards.add(new Docks());
                cards.add(new Harbor());
                cards.add(new WatchTower());
                cards.add(new Prison());
            }
            if (i < 4) {
                cards.add(new Church());
                cards.add(new Castle());
                cards.add(new Market());
            }
            cards.add(new Manor());
            cards.add(new Tavern());
        }
        cards.add(new SchoolOfMagic());
        cards.add(new HauntedCity());
        cards.add(new Laboratory());
        cards.add(new Smithy());
        cards.add(new Observatory());
        cards.add(new University());
        cards.add(new Graveyard());
        cards.add(new Library());
        cards.add(new DragonGate());
    }

    public List<District> getDeck() {
        return cards;
    }
}