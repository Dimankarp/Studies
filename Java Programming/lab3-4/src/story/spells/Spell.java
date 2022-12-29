package story.spells;

import story.characters.Creature;
import story.ships.Ship;

public abstract class Spell {

    public abstract void onCast(Ship caster, Ship target);

    public abstract void onFinish(Ship caster, Ship target);

    public abstract String onCastDescribe(Creature caster);

    public abstract String onFinishDescribe(Creature caster);

}
