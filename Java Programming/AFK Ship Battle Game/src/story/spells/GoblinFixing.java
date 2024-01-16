package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.Part;
import story.ships.Ship;
import story.ships.Weapon;

import static story.ArrayUtil.getRandomObjectFromArray;

public class GoblinFixing extends Spell {

    private Part healedPart;
    private boolean success;

    public GoblinFixing(){}

    @Override
    public void onCast(Ship caster, Ship target) {
        healedPart = (Part) getRandomObjectFromArray(caster.getParts());
        double appliedHealing = healedPart.getPartPoints() - (healedPart.getMaxHealth() * Math.round(Math.random()*0.3 - 0.2 + 0.4));
        success = appliedHealing < 0;
        //Устанавливает здоровье на 50% - Да, вот такой скилл! Это может и выхилить, и к чертям всё потопить. Крутой геймдизайн?
        healedPart.applyDamage(appliedHealing);
    }

    @Override
    public void onFinish(Ship caster, Ship target) {
    }

    @Override
    public String onCastDescribe(Creature caster) {
        String successMessage = success ? "and, thankfully, succeeds" : "but fails dramatically";
        return String.format("%s reading %s \n%s %s\n%s!\n", caster.toString(),Colorer.Colorize(" Goblin's ship manual", Colorer.Colors.GREEN_UNDERLINED), Colorer.Colorize("tries to fix", Colorer.Colors.GREEN_UNDERLINED), healedPart.toString(), successMessage);
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return "";

    }
}
