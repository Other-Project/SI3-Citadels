package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public enum Colors {
    NONE("⚪"), BLUE("🔵"), YELLOW("🟡"), GREEN("🟢"), RED("🔴"), PURPLE("🟣");

    private final String representation;

    Colors(String emoji) {
        this.representation = emoji;
    }

    @Override
    public String toString() {
        return representation;
    }
}
