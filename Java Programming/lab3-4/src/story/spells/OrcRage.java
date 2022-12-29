package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.Ship;
import story.ships.Weapon;

public class OrcRage extends Spell {

    private Weapon[] improvedWeapons;

    public OrcRage() {
    }

    @Override
    public void onCast(Ship caster, Ship target) {
        improvedWeapons = (Weapon[]) caster.getWeaponParts();
        for (Weapon item : improvedWeapons) {
            item.setAttackBuff(item.getAttack() * 2);
            item.setAccuracyBuff(item.getAccuracy() * -0.5);
        }
    }

    @Override
    public void onFinish(Ship caster, Ship target) {

        for (Weapon item : improvedWeapons) {
            item.setAttackBuff(0);
            item.setAccuracyBuff(0);
        }
    }

    @Override
    public String onCastDescribe(Creature caster) {
        return String.format("%s %s the crew\nof the ship's weapons!\n", caster.toString(), Colorer.Colorize("roars loudly enraging", Colorer.Colors.GREEN_UNDERLINED));
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return String.format("%s's weapons\ncrews are %s!\n", caster.toString(), Colorer.Colorize("no longer enraged", Colorer.Colors.WHITE_BRIGHT));

    }
}
