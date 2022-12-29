package story.characters;

import story.spells.Spell;

import java.util.ArrayList;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.Colorer.Colorize;

public class Humanoid extends Creature {

    static private final String[] DEFAULT_NAMES = {"Гуманоид", "Человекоподобное", "А-ля Человек", "Humanus"};
    private Races race;

    private Alignments temper;

    private ArrayList<Spell> knownSpells = new ArrayList<Spell>();

    public Races getRace() {
        return race;
    }

    public Alignments getTemper() {
        return temper;
    }

    public Spell[] getSpells() {
        Spell[] spls = new Spell[knownSpells.size()];
        spls = knownSpells.toArray(spls);
        return spls;
    }

    public void addSpell(Spell spell) {
        knownSpells.add(spell);
    }

    public Humanoid(String name, Genders gender, double health, Races race, Alignments temper) {
        super(name, gender, health);
        this.race = race;
        this.temper = temper;
        addSpell(this.race.getSpell());
    }

    public Humanoid() {
        super(getRandomObjectFromArray(DEFAULT_NAMES));
        this.race = getRandomObjectFromArray(Races.values());
        this.temper = getRandomObjectFromArray(Alignments.values());
        addSpell(this.race.getSpell());
    }

    public Humanoid(Races race) {
        super(getRandomObjectFromArray(DEFAULT_NAMES));
        this.race = race;
        this.temper = getRandomObjectFromArray(Alignments.values());
        addSpell(this.race.getSpell());
    }

    public Humanoid(String name) {
        super(name);
        this.race = getRandomObjectFromArray(Races.values());
        this.temper = getRandomObjectFromArray(Alignments.values());
        addSpell(this.race.getSpell());
    }


    @Override
    public String toString() {
        return Colorize(String.format("%s %s %s", race.getAdjective(), this.getClass().getSimpleName(), this.getName()), race.getColor());
    }
}
