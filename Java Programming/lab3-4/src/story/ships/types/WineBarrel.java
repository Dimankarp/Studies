package story.ships.types;

import story.characters.Creature;
import story.characters.Humanoid;
import story.characters.Races;
import story.characters.specs.Mother;
import story.ships.*;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class WineBarrel extends Ship {

    private static final String[] DEFAULT_NAMES = {"Шардане", "Бордо", "Хоббит-влезет"};

    public WineBarrel(String name) {
        super(name);
        this.setParts(new Part[]{
                new Part(ROSEWOOD, HULL, "Barrel", 0.05)
        });
    }

    public WineBarrel() {
        this((String) getRandomObjectFromArray(DEFAULT_NAMES));
    }

    @Override
    public void assignCaptain(Creature captain) throws WrongCaptainException {
        if (captain instanceof Humanoid hum && hum.getRace() == Races.GOBLIN) {
            super.setCaptain(captain);
        }
        throw new GoblinExpectedException("Only Goblins can fit into a wine barrel. Or Hobbits.");
    }

}
