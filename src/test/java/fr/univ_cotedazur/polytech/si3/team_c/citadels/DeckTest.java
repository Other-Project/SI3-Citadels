package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest {
    Deck deck = new Deck();

    @Test
    void draw() {
        assertEquals(List.of(deck.getFirst()), deck.draw(1));
    }
}