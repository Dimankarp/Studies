package story.characters;

import story.Colorer;
import story.Describable;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.Colorer.Colorize;

public abstract class Creature implements Describable {

    static private final String[] DEFAULT_NAMES = {"Сущность", "Нечто", "Что-то", "Хрень какая-та"};
    private double health;

    private String name;

    private Genders gender;

    public double getHealth() {
        return health;
    }

    public Genders getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public Creature(String name, Genders gender, double health) {
        this.health = health;
        this.name = name;
        this.gender = gender;
    }

    public Creature() {
        this(getRandomObjectFromArray(DEFAULT_NAMES), (Genders) getRandomObjectFromArray(Genders.values()), 100);
    }

    public Creature(String name) {
        this(name, getRandomObjectFromArray(Genders.values()), 100);
    }

    @Override
    public String toString() {
        return "Creature" + Colorize(name, Colorer.Colors.GREEN_BOLD_BRIGHT);
    }


}
