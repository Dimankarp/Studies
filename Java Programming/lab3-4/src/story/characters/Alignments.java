package story.characters;

/*
Alignment - this is how tempers called
in Baldur's Gate. Love It
 */
public enum Alignments {
    LAWFUL_GOOD("Lawful Good"),
    NEUTRAL_GOOD("Neutral Good"),
    CHAOTIC_GOOD("Chaotic Good"),
    LAWFUL_NEUTRAL("Lawful Neutral"),
    NEUTRAL("Neutral"),
    CHAOTIC_NEUTRAL("Chaotic Neutral"),
    LAWFUL_EVIL("Lawful Evil"),
    NEUTRAL_EVIL("Neutral Evil"),
    CHAOTIC_EVIL("Chaotic Evil");

    private String adj;

    public String getAdjective() {
        return adj;
    }

    Alignments(String adj) {
        this.adj = adj;
    }
}
