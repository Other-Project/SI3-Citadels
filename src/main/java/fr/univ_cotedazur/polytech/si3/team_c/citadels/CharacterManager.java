package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.Player;

import java.util.*;
import java.util.function.Predicate;

public class CharacterManager {
    private Map<Character, Player> characterPlayerMap;
    private List<Character> availableCharacters;
    private List<Character> visible;
    private List<Character> hidden;
    private final List<Character> characters;
    private final Random random;
    private final int playerCount;

    public CharacterManager(int playerCount, Random random) {
        this(playerCount, random, defaultCharacterList());
    }

    public CharacterManager(int playerCount, Random random, List<Character> characters) {
        this.playerCount = playerCount;
        this.random = random;
        this.hidden = new ArrayList<>();
        this.visible = new ArrayList<>();
        characterPlayerMap = new HashMap<>();
        this.characters = characters;
        this.availableCharacters = new ArrayList<>(charactersList());
    }

    /**
     * Sets all the values for the character selection turn
     */
    protected void generate() {
        characterPlayerMap = new HashMap<>();
        this.availableCharacters = new ArrayList<>(charactersList());
        setHiddenDiscard();
        setVisibleDiscard();
    }

    public List<Character> getAvailableCharacters() {
        return availableCharacters;
    }

    /**
     * Gets the default list of characters
     */
    public static List<Character> defaultCharacterList() {
        return new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord()));
    }

    /**
     * Returns the list of characters in relation to the number of players
     */
    public List<Character> charactersList() {
        return new ArrayList<>(characters.stream()
                .filter(character -> character.minimumNumberOfPlayers() <= playerCount)
                .toList());
    }

    /**
     * @param character the character to be checked
     * @return true if the character has been chosen
     */
    protected boolean characterIsChosen(Character character) {
        return characterPlayerMap.containsKey(character);
    }

    /**
     * Gets the discard list by the possible characters and the number of characters to remove
     *
     * @param charactersCount    number of characters to take in the possibleCharacters
     * @param characterPredicate gives information on whether the character can be chosen
     */
    protected List<Character> getDiscardList(int charactersCount, Predicate<Character> characterPredicate) {
        List<Character> selectedCharacters = new ArrayList<>();
        List<Character> possibleCharacters = new ArrayList<>(getAvailableCharacters().stream()
                .filter(characterPredicate)
                .toList());
        for (; charactersCount > 0; charactersCount--) {
            Character selectedCharacter = possibleCharacters.get(random.nextInt(possibleCharacters.size()));
            selectedCharacters.add(selectedCharacter);
            possibleCharacters.remove(selectedCharacter);
            getAvailableCharacters().remove(selectedCharacter);
        }
        return selectedCharacters;
    }

    /**
     * @return the hidden discard size
     */
    private int getHiddenDiscardSize() {
        return (playerCount >= 3 && playerCount <= 7) ? 1 : 0;
    }

    /**
     * @return the visible discard size
     */
    private int getVisibleDiscardSize() {
        return switch (playerCount) {
            case 3, 4 -> 2;
            case 5 -> 1;
            default -> 0;
        };
    }

    /**
     * Sets the hidden discard
     */
    protected void setHiddenDiscard() {
        hidden = getDiscardList(getHiddenDiscardSize(), character -> true);
    }

    /**
     * Sets the visible discard
     */
    protected void setVisibleDiscard() {
        visible = getDiscardList(getVisibleDiscardSize(), character -> character.canBePlacedInVisibleDiscard() && getAvailableCharacters().contains(character));
    }

    /**
     * Add the player and his character in characterPlayerMap
     *
     * @param character the player's character
     * @param player    the player
     */
    protected void addPlayerCharacter(Player player, Character character) {
        characterPlayerMap.put(character, player);
        getAvailableCharacters().remove(character);
    }

    /**
     * @param character the character inspected
     * @return the character who corresponds to the player
     */
    protected Player getPlayer(Character character) {
        return characterPlayerMap.get(character);
    }

    public List<Character> getVisible() {
        return visible;
    }

    protected List<Character> getHidden() {
        return hidden;
    }

    /**
     * @return the possible characters to choose for the player
     */
    public List<Character> possibleCharactersToChoose() {
        List<Character> possibleCharacters = getAvailableCharacters();
        // In a game with one available character, the last player needs to choose between hidden discard and the remaining character
        if (getAvailableCharacters().size() == 1)
            possibleCharacters.add(hidden.get(0));
        return possibleCharacters;
    }

    public String toString() {
        StringBuilder discardDisplay = new StringBuilder();
        if (!hidden.isEmpty()) {
            discardDisplay.append("The following characters have been placed in the hidden discard : ").append(hidden);
            if (!visible.isEmpty()) discardDisplay.append("\n");
        }
        if (!visible.isEmpty())
            discardDisplay.append("The following characters have been placed in the visible discard : ").append(visible);
        if (discardDisplay.isEmpty()) discardDisplay.append("There are no characters in the discard");
        return discardDisplay.toString();
    }
}
