package story.characters;

import story.Colorer.Colors;
import story.spells.*;

public enum Races {
    HUMAN("Human", Colors.WHITE_BOLD_BRIGHT, new HumanSnipering()),
    ORC("Orc", Colors.GREEN_UNDERLINED, new OrcRage()),
    ELF("Elf", Colors.CYAN_UNDERLINED, new ElvenDistraction()),
    GOBLIN("Goblin", Colors.GREEN_BOLD_BRIGHT, new GoblinFixing()),
    VAMPIRE("Vampire", Colors.RED_UNDERLINED, new VampiricEmpower()),
    UNDEAD("Undead", Colors.WHITE_UNDERLINED, new UndeadResurrection());

    private String adj;

    private Colors color;

    private Spell raceSpell;

    public String getAdjective() {
        return adj;
    }

    public Colors getColor() {
        return color;
    }

    public Spell getSpell() {
        return raceSpell;
    }

    Races(String adj, Colors col, Spell spl) {
        this.adj = adj;
        this.color = col;
        this.raceSpell = spl;
    }
}
