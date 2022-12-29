package story.characters.specs;

import story.characters.*;

import static story.ArrayUtil.getRandomObjectFromArray;

public class Mother extends Humanoid implements Talkable {

    private static final String[] DEFAULT_NAMES = {"Галина", "Зина", "Ольга", "Авдотья", "Марина", "Маргарита", "Наташа"};
    public final String[] PHRASES = {
            "Теперь и \nшторм не страшен!",
            "Ах ты!",
            "Да ну тебя!",
            "Шапку надел,\n волк морской?"};

    public Mother(String name, Genders gender, double health, Races race, Alignments temper) {
        super(name, gender, health, race, temper);
    }

    public Mother() {
        super(getRandomObjectFromArray(DEFAULT_NAMES));
    }

    public Mother(String name) {
        super(name);
    }

    public String getRandomSaying() {
        int randIndex = (int) Math.round(Math.random() * (PHRASES.length - 1));
        return getSaying(PHRASES[randIndex]);
    }

}
