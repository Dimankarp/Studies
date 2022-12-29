package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Barque extends Ship {

    private static final String[] DEFAULT_NAMES = {"Баркас", "Барсик", "Стрекоза", "Ёж", "Кварк", "Аврорушка", "Безгвоздный"};

    public Barque(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(SPRUCE, HULL, 0.6),
                new Part(SPRUCE, DECK, "Deck", 0.9),
                new Part(SPRUCE, HOLD, 0.8),
                new Part(MAPLE, MAINMAST, 0.8),
                new Part(MAPLE, FOREMAST, 0.6),
                new Part(MAPLE, BUSHPRIT),
                new Part(BIRCH, TRAPDOOR),
                new Part(ROSEWOOD, STEER_WHEEL),
                new Part(FABRIC, SAILS, "Central Sail", 4),
                new Part(FABRIC, FORE_SAILS, 1.5),
                new Part(FABRIC, BACK_SAILS, 1.5),
                new Weapon(IRON, 160, 0.6, "Canon Row"),
                new Weapon(SPRUCE, 90, 0.75, "Small Rifle Row"),
        });
    }

    public Barque() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
