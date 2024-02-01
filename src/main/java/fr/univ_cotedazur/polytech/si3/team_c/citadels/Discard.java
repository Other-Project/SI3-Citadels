package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Discard {
    private final List<Character> visible;
    private final List<Character> hidden;
    private final Random random;

    public Discard() {
        this.visible = new ArrayList<>();
        this.hidden = new ArrayList<>();
        this.random = null;
    }

    public Discard(int playerNumber, List<Character> availableCharacters) {
        random = new Random();
        SimpleEntry<Integer, Integer> discardNumbers = discardValues(playerNumber);
        hidden = getDiscardList(discardNumbers.getKey(), availableCharacters, availableCharacters);
        List<Character> availableVisibleDiscard = new ArrayList<>(availableCharacters.stream()
                .filter(character -> character.canBePlacedInVisibleDiscard() && availableCharacters.contains(character))
                .toList());
        visible = getDiscardList(discardNumbers.getValue(), availableVisibleDiscard, availableCharacters);
    }

    public List<Character> getHidden() {
        return hidden;
    }

    public List<Character> getVisible() {
        return visible;
    }

    /**
     * Gets the discard list by the possible characters and the number of character to remove
     *
     * @param charactersCount    number of characters to take in the possibleCharacters
     * @param possibleCharacters the characters that can be chosen
     */
    public List<Character> getDiscardList(int charactersCount, List<Character> possibleCharacters, List<Character> availableCharacters) {
        List<Character> selectedCharacters = new ArrayList<>();
        for (; charactersCount > 0; charactersCount--) {
            Character selectedCharacter = possibleCharacters.get(random.nextInt(possibleCharacters.size()));
            selectedCharacters.add(selectedCharacter);
            possibleCharacters.remove(selectedCharacter);
            availableCharacters.remove(selectedCharacter);
        }
        return selectedCharacters;
    }

    /**
     * @return the hidden and visible discard character counts
     */
    private SimpleEntry<Integer, Integer> discardValues(int playerNumber) {
        int hiddenCount;
        int visibleCount;
        switch (playerNumber) {
            case 3, 4 -> {
                visibleCount = 2;
                hiddenCount = 1;
            }
            case 6, 7 -> {
                visibleCount = 0;
                hiddenCount = 1;
            }
            case 5 -> {
                visibleCount = 1;
                hiddenCount = 1;
            }
            default -> {
                visibleCount = 0;
                hiddenCount = 0;
            }
        }
        return new SimpleEntry<>(hiddenCount, visibleCount);
    }

    public String toString() {
        StringBuilder discardDisplay = new StringBuilder();
        if (!hidden.isEmpty()) {
            discardDisplay.append("The following characters have been placed in the hidden discard : ").append(hidden);
            if (!visible.isEmpty()) discardDisplay.append("\n");
        }
        if (!visible.isEmpty())
            discardDisplay.append("The following characters have been placed in the visible discard : ").append(visible);
        return discardDisplay.toString();
    }
}
