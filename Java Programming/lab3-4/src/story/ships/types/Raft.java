package story.ships.types;

import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.ships.Materials.*;
import static story.ships.PartTypes.*;

public class Raft extends Ship {

    private static final String[] DEFAULT_NAMES = {"Бревно", "Дверь", "Нестабильный", "Чудо", "Тина"};
    public Raft(String name){
        super(name);
        this.setParts(new Part[]{
                new Part(SPRUCE, DECK, "Raft", 0.1),
                new Weapon(OAK, 15, 0.7, "Spear Launcher", 0.1),
        });
    }

    public Raft()
    {
        this((String)getRandomObjectFromArray(DEFAULT_NAMES));
    }
}
