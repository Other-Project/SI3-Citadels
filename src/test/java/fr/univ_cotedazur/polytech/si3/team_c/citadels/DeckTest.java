package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest {
    Deck deck = new Deck();

    @Test
    void getDeck() {
        assertEquals(65, deck.getDeck().size());
    }
}