package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Canoe extends Ship {

    private static final String[] DEFAULT_NAMES = {"Тцотль", "Мтилт", "Америка", "Ладья", "Краснокожья"};//Синтезирую индейские слова

    public Canoe(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(OAK, HULL, "Canoe Hull", 0.2),
                new Part(OAK, DECK, "Benches", 0.4),
                new Weapon(OAK, 30, 0.7, "Small Row of Rifles", 0.2)
        });
    }

    public Canoe() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
