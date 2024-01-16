package story.characters.specs;

import story.characters.*;

import static story.ArrayUtil.getRandomObjectFromArray;

public class Student extends Humanoid implements Talkable {

    private static final String[] DEFAULT_NAMES = {"Петров", "Сидоров", "Иванов", "Допсников", "Джавадзе", "Питонов"};

    public static final String[] PHRASES = {
            "Кря-кря!\n Я уточка!",
            "Курс на ПСЖ!\n Банзааай!",
            "Хочу ром!\n Стипендии не хватает..."};

    public Student(String name, Genders gender, double health, Races race, Alignments temper) {
        super(name, gender, health, race, temper);
    }

    public Student() {
        super(getRandomObjectFromArray(DEFAULT_NAMES));
    }

    public Student(String name) {
        super(name);
    }

    public String getRandomSaying() {
        int randIndex = (int) Math.round(Math.random() * (PHRASES.length - 1));
        return getSaying(PHRASES[randIndex]);
    }
}
