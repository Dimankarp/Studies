package extension.move;
import ru.ifmo.se.pokemon.*;

public class Swagger extends StatusMove{

	public Swagger()
	{
	 super(Type.NORMAL, 0, 85D);
	}


	@Override
	protected void applyOppEffects(Pokemon p){;
		Effect e = new Effect().chance(1).turns(-1).stat(Stat.ATTACK, 2);
		e.confuse(p);
		p.addEffect(e);
	}

	@Override
	protected String describe(){
		return "выводит противника из себя, вводя его в замешательство";
	}

}