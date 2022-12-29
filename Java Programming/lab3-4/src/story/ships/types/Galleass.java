package story.ships.types;

import story.characters.Creature;
import story.characters.specs.Mother;
import story.ships.*;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Galleass extends Ship {

    private static final String[] DEFAULT_NAMES = {"Маменькин", "Гордый", "Нерпа", "Айсберг", "Тупонос", "Голди"};

    public Galleass(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(SPRUCE, HULL, 0.8),
                new Part(SPRUCE, DECK, "Upper Deck"),
                new Part(SPRUCE, DECK, "Lower Deck", 0.9),
                new Part(SPRUCE, HOLD),
                new Part(MAPLE, MAINMAST),
                new Part(MAPLE, FOREMAST, 0.8),
                new Part(MAPLE, BUSHPRIT),
                new Part(BIRCH, TRAPDOOR),
                new Part(ROSEWOOD, STEER_WHEEL),
                new Part(FABRIC, SAILS, "Central Sail", 5),
                new Part(FABRIC, FORE_SAILS, 2),
                new Part(FABRIC, BACK_SAILS, 2),
                new Weapon(GOLD, 300, 0.8, "Gatling Gun", 0.4),
                new Weapon(IRON, 250, 0.6, "Upper Canon Row"),
                new Weapon(IRON, 250, 0.6, "Lower Canon Row"),
                new Weapon(SPRUCE, 60, 0.2, "Ram", 0.2)
        });
    }

    public Galleass() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }

    @Override
    public void assignCaptain(Creature captain) throws WrongCaptainException {
        if (captain instanceof Mother) {
            super.setCaptain(captain);
        }
        throw new MotherExpectedException("Canonically Galiasses are commanded by Mothers.");
    }


}
