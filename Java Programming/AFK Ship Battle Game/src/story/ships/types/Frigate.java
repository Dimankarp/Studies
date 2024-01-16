package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Frigate extends Ship {

    private static final String[] DEFAULT_NAMES = {"Котоктулху", "Её Величество", "Дрындноут", "Фригасе", "Красин", "Титан-ик"};

    public Frigate(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(SPRUCE, HULL),
                new Part(SPRUCE, DECK, "Upper Deck", 1.5),
                new Part(SPRUCE, DECK, "Upper-Middle Deck", 1.2),
                new Part(SPRUCE, DECK, "Lower-Middle Deck", 1),
                new Part(SPRUCE, DECK, "Lower Deck", 0.9),
                new Part(SPRUCE, HOLD, 3),
                new Part(SPRUCE, MAINMAST, "Front Main Mast", 2.5),
                new Part(SPRUCE, MAINMAST, "Back Main Mast", 2.5),
                new Part(SPRUCE, FOREMAST, 1.5),
                new Part(SPRUCE, BUSHPRIT, 2),
                new Part(SPRUCE, TRAPDOOR, 1.5),
                new Part(SPRUCE, KEEL, 2),
                new Part(ROSEWOOD, CAP_CABIN, 1.5),
                new Part(ROSEWOOD, STEER_WHEEL, 1.25),
                new Part(FABRIC, SAILS, 12),
                new Part(FABRIC, SAILS, 12),
                new Part(FABRIC, SAILS, 12),
                new Part(FABRIC, FORE_SAILS, 6),
                new Part(FABRIC, BACK_SAILS, 6),
                new Weapon(IRON, 310, 0.6, "Upper Canon Row", 1.25),
                new Weapon(IRON, 310, 0.6, "Upper-Middle Canon Row", 1.25),
                new Weapon(IRON, 310, 0.55, "Lower-Middle Canon Row", 1.25),
                new Weapon(IRON, 310, 0.5, "Lower Canon Row", 1.25),
                new Weapon(SPRUCE, 200, 0.8, "Rifle Row", 0.45),
                new Weapon(STEEL, 1000, 0.2, "Bomb Launcher", 0.45),

        });
    }

    public Frigate() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
