package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;

import java.util.Comparator;
import java.util.List;

public class Game {
    private List<Player> playerList;
    private List<Character> characterList;

    public void setDefaultCharacterList() {
        characterList = List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord());
    }

    public void characterSelectionTurn() {
        setDefaultCharacterList();
        for (Player player : playerList) {
            characterList.remove(player.pickCharacter(this.characterList));
        }
    }

    public void gameTurn() {
        characterSelectionTurn();
        playerList.sort(Comparator.comparing(player -> player.getCharacter().orElseThrow()));
    }

    public static void main(String... args) {

    }
}
