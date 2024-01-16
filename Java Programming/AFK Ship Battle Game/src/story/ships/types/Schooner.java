package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Schooner extends Ship {

    private static final String[] DEFAULT_NAMES = {"Селёдка", "Карасик", "Лещ", "Сайра", "Минтай", "Омулёк"};

    public Schooner(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(OAK, HULL, 0.5),
                new Part(OAK, DECK),
                new Part(OAK, MAINMAST, 0.8),
                new Part(OAK, FOREMAST, 0.6),
                new Part(BIRCH, STEER_WHEEL),
                new Part(FABRIC, SAILS, 3),
                new Part(FABRIC, FORE_SAILS, 2),
                new Weapon(IRON, 150, 0.6, "Small Canon Row", 0.5),

        });
    }

    public Schooner() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
