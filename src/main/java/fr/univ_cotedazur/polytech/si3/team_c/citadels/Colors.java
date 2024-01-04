package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public enum Colors {
    NONE("⚪\033[0;37m"), BLUE("🔵\033[0;36m"), YELLOW("🟡\033[0;33m"), GREEN("🟢\033[0;32m"), RED("🔴\033[0;31m"), PURPLE("🟣\033[0;35m");

    private final String representation;

    Colors(String emoji) {
        this.representation = emoji;
    }

    @Override
    public String toString() {
        return representation;
    }
}
