package story.characters.specs;

import story.characters.*;

import static story.ArrayUtil.getRandomObjectFromArray;

public class Pirate extends Humanoid implements Talkable {

    private static final String[] DEFAULT_NAMES = {"Лысая Борода", "Рыжий Де Морган", "Сэр Друйк", "Др. Ливзи", "Джон Кремнев", "Билли Косточкин", "Джим Орлов", "Джон Злато"};
    public final String[] PHRASES = {
            "Каррамба!",
            "Тысяча чертей!",
            "На абордаж!",
            "Слово ром и\n слово смерть для вас\n означают одно и то же!",
            "Я порву тебя!",
            "А где КОК?!",
            "Я скормлю\n тебя акулам!"};

    public Pirate(String name, Genders gender, double health, Races race, Alignments temper) {
        super(name, gender, health, race, temper);
    }

    public Pirate() {
        super(getRandomObjectFromArray(DEFAULT_NAMES));
    }

    public Pirate(String name) {
        super(name);
    }

    public String getRandomSaying() {
        int randIndex = (int) Math.round(Math.random() * (PHRASES.length - 1));
        return getSaying(PHRASES[randIndex]);
    }

}
