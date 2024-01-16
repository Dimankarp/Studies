package extension.move;
import ru.ifmo.se.pokemon.*;

public class FlashCannon extends SpecialMove{

	public FlashCannon()
	{
	 super(Type.STEEL, 80D, 100D);
	}

	@Override
	protected void applyOppEffects(Pokemon p){
		Effect e = new Effect().chance(0.1).turns(-1).stat(Stat.SPECIAL_DEFENSE, -1);
		
		p.addEffect(e);
	}

	@Override
	protected String describe(){
		return "собирает всю свою световую энергию и нежданно-негаданно выпускает её в лоб противника";
	}

}