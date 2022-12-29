package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.Ship;
import story.ships.Weapon;

public class UndeadResurrection extends Spell {

    private Weapon[] healedWeapons;

    public UndeadResurrection() {
    }

    @Override
    public void onCast(Ship caster, Ship target) {
        healedWeapons = (Weapon[]) caster.getWeaponParts();
        for (Weapon item : healedWeapons) {
            item.applyDamage(-item.getMaxHealth() * (Math.random() * 0.02 + 0.03));
        }
    }

    @Override
    public void onFinish(Ship caster, Ship target) {

    }

    @Override
    public String onCastDescribe(Creature caster) {
        return String.format("%s raises hand and\n some of the fallen crew %s!\n", caster.toString(), Colorer.Colorize("rises from the dead", Colorer.Colors.WHITE_UNDERLINED));
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return "";

    }
}
