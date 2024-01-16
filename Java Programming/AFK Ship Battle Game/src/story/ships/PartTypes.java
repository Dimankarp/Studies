package story.ships;

public enum PartTypes {
    HULL(5, "Hull"),
    MAINMAST(2, "Main mast"),
    FOREMAST(1.5, "Front mast"),
    CAP_CABIN(0.5, "Captain's cabin"),
    DECK(2, "Deck"),
    KEEL(1, "Keel"),
    FORE_SAILS(0.3, "Front sails"),
    BACK_SAILS(0.3, "Back sails"),
    SAILS(2, "Main sails"),
    BUSHPRIT(0.3, "Bushprit"),
    STEER_WHEEL(0.2, "Steering wheel"),
    TRAPDOOR(0.1, "Trapdoor"),
    HOLD(0.5, "Hold"),
    CANON_ROW(1, "Row of Canons");


    private double partCoeff;
    private String name;

    PartTypes(double coeff, String name) {
        partCoeff = coeff;
        this.name = name;
    }

    public double getCoeff() {
        return partCoeff;
    }

    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
