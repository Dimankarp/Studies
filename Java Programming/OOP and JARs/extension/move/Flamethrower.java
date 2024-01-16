package extension.move;
import ru.ifmo.se.pokemon.*;

 public class Flamethrower extends SpecialMove{

	public Flamethrower()
	{
	 super(Type.FIRE, 90D, 100D);
	}

	@Override
	protected void applyOppEffects(Pokemon p){
		Effect e = new Effect().chance(0.1).condition(Status.BURN);
		p.addEffect(e);
	}

	@Override
	protected String describe(){
		return "выжигает цель интенсивными потоками пламени";
	}

}