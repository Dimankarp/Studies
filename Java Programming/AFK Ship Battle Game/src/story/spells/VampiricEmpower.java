package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;

public class VampiricEmpower extends Spell {

    private Weapon enhancedWeapon;

    public VampiricEmpower() {
    }

    @Override
    public void onCast(Ship caster, Ship target) {
        Part[] weaps = caster.getWeaponParts();
        if (weaps.length < 1) {
            return;
        }
        enhancedWeapon = (Weapon) getRandomObjectFromArray(weaps);
        enhancedWeapon.setAttackBuff(4 * enhancedWeapon.getPartPoints());
        enhancedWeapon.setAccuracyBuff(0.5);
        enhancedWeapon.applyDamage(0.25 * enhancedWeapon.getPartPoints());
    }

    @Override
    public void onFinish(Ship caster, Ship target) {
        enhancedWeapon.setAttackBuff(0);
        enhancedWeapon.setAccuracyBuff(0);
    }

    @Override
    public String onCastDescribe(Creature caster) {
        return String.format("%s drinks some %s from\nthe crew of %s\n and empowers it!\n", caster.toString(), Colorer.Colorize("blood", Colorer.Colors.RED_UNDERLINED), enhancedWeapon.toString());
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return String.format("%s is %s!\n", enhancedWeapon.toString(), Colorer.Colorize("no longer empowered", Colorer.Colors.WHITE_BRIGHT));

    }
}
