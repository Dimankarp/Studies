package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.Ship;
import story.ships.Weapon;

public class HumanSnipering extends Spell {

    private Weapon[] improvedWeapons;

    public HumanSnipering() {
    }

    @Override
    public void onCast(Ship caster, Ship target) {
        improvedWeapons = (Weapon[]) caster.getWeaponParts();
        for (Weapon item : improvedWeapons) {
            item.setAttackBuff(-item.getAttack() * 0.2);
            item.setAccuracyBuff(0.95);
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
        return String.format("%s gives a command %s\n%s with less gunpowder!\n", caster.toString(), Colorer.Colorize("for a precise", Colorer.Colors.WHITE_BOLD_BRIGHT), Colorer.Colorize("fire", Colorer.Colors.WHITE_BOLD_BRIGHT));
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return String.format("%s's crew now %s!\n", caster.toString(), Colorer.Colorize("fires normally", Colorer.Colors.WHITE_BRIGHT));

    }
}
