package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Galleon extends Ship {

    private static final String[] DEFAULT_NAMES = {"Не он", "Пухлый", "Смольный", "Галл", "Морж"};

    public Galleon(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(SPRUCE, HULL),
                new Part(SPRUCE, DECK, "Upper Deck"),
                new Part(MAPLE, DECK, "Middle Deck", 0.9),
                new Part(MAPLE, DECK, "Lower Deck", 0.8),
                new Part(SPRUCE, HOLD, 2),
                new Part(SPRUCE, MAINMAST),
                new Part(SPRUCE, FOREMAST, 0.9),
                new Part(SPRUCE, BUSHPRIT),
                new Part(MAPLE, TRAPDOOR),
                new Part(SPRUCE, KEEL),
                new Part(BIRCH, CAP_CABIN),
                new Part(ROSEWOOD, STEER_WHEEL),
                new Part(FABRIC, SAILS, 6),
                new Part(FABRIC, SAILS, 6),
                new Part(FABRIC, FORE_SAILS, 3),
                new Part(FABRIC, BACK_SAILS, 3),
                new Weapon(IRON, 280, 0.6, "Upper Canon Row"),
                new Weapon(IRON, 280, 0.6, "Middle Canon Row"),
                new Weapon(IRON, 280, 0.6, "Lower Canon Row"),
                new Weapon(SPRUCE, 150, 0.8, "Rifle Row", 0.3),
                new Weapon(STEEL, 450, 0.2, "Brummbar", 0.4),

        });
    }

    public Galleon() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
