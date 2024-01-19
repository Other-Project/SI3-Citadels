package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck extends ArrayDeque<District> {

    public Deck(List<District> cards) {
        if (!cards.isEmpty()) {
            cards = new ArrayList<>(cards);
            Collections.shuffle(cards);
            addAll(cards);
        } else addAll(getAllDistrict());
    }

    public Deck() {
        this(List.of());
    }

    /**
     * Constructor of the class to create the Deck with all the district cards
     */
    private List<District> getAllDistrict() {
        List<District> deck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i < 2) {
                deck.add(new Cathedral());
                deck.add(new Palace());
                deck.add(new TownHall());
                deck.add(new Fortress());
                deck.add(new TheKeep());
            }
            if (i < 3) {
                deck.add(new Monastery());
                deck.add(new Temple());
                deck.add(new TradingPost());
                deck.add(new Docks());
                deck.add(new Harbor());
                deck.add(new WatchTower());
                deck.add(new Prison());
                deck.add(new Battlefield());
            }
            if (i < 4) {
                deck.add(new Church());
                deck.add(new Castle());
                deck.add(new Market());
            }
            deck.add(new Manor());
            deck.add(new Tavern());
        }
        deck.add(new SchoolOfMagic());
        deck.add(new HauntedCity());
        deck.add(new Laboratory());
        deck.add(new Smithy());
        deck.add(new Observatory());
        deck.add(new University());
        deck.add(new Graveyard());
        deck.add(new Library());
        deck.add(new DragonGate());
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Method for drawing a certain number of cards from the deck
     *
     * @param cardToDraw : number of cards to draw
     * @return the List of drawn cards
     */
    public List<District> draw(int cardToDraw) {
        List<District> drawnCards = new ArrayList<>();
        for (int i = 0; i < cardToDraw && !isEmpty(); i++) {
            drawnCards.add(pop());
        }
        return drawnCards;
    }
}