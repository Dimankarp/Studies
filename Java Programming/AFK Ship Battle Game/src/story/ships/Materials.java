package story.ships;

import static story.Colorer.*;

public enum Materials {
    OAK(100, "Oak", Colors.CYAN_BOLD),
    BIRCH(90, "Birch", Colors.WHITE_BOLD),
    MAPLE(150, "Maple", Colors.CYAN_BRIGHT),
    ROSEWOOD(130, "Rosewood", Colors.RED_BRIGHT),
    SPRUCE(170, "Spruce", Colors.PURPLE_BRIGHT),
    IRON(300, "Iron", Colors.GREEN_BOLD),
    BRONZE(400, "Bronze", Colors.YELLOW_BOLD_BRIGHT),
    STEEL(600, "Steel", Colors.BLACK_BRIGHT),
    GOLD(200, "Golden", Colors.YELLOW_BOLD),
    GLASS(10, "Glass", Colors.WHITE),
    FABRIC(5, "Fabric", Colors.WHITE_BRIGHT);

    private double durability;

    private String adjective;

    private Colors color;

    public double getDurability() {
        return durability;
    }

    public String toString() {
        return Colorize(adjective, color);
    }

    Materials(double dur, String adj, Colors color) {

        durability = dur;
        adjective = adj;
        this.color = color;
    }

}
