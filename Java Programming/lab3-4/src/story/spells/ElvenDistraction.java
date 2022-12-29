package story.spells;

import story.Colorer;
import story.characters.Creature;
import story.ships.*;

public class ElvenDistraction extends Spell {

    private Part distractionPart;
    private Part[] origParts;

    public ElvenDistraction() {
    }


    //Плохо, что я не захотел использовать коллекции с самого начала. Такую мурню нагородил...
    @Override
    public void onCast(Ship caster, Ship target) {
        distractionPart = new Part(Materials.FABRIC, PartTypes.SAILS, "Distraction Screen", 0.1);
        origParts = caster.getParts();
        Part[] prts = new Part[origParts.length + 1];
        for (int i = 0; i < origParts.length; i++) {
            prts[i] = origParts[i];
        }
        prts[prts.length - 1] = distractionPart;

        caster.assignParts(prts);
    }

    @Override
    public void onFinish(Ship caster, Ship target) {
        caster.assignParts(origParts);
    }

    @Override
    public String onCastDescribe(Creature caster) {
        return String.format("%s worships %s\nand a magical %s appears!\n", caster.toString(), Colorer.Colorize("Elven Gods", Colorer.Colors.CYAN_UNDERLINED), distractionPart.toString());
    }

    @Override
    public String onFinishDescribe(Creature caster) {
        return String.format("The %s %s!\n", distractionPart.toString(), Colorer.Colorize("suddenly disappears", Colorer.Colors.WHITE_BRIGHT));

    }
}
