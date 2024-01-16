package extension.move;
import ru.ifmo.se.pokemon.*;

public class Minimize extends StatusMove{

	public Minimize()
	{
	 super(Type.NORMAL, 0, -1);
	}


	@Override
	protected boolean checkAccuracy(Pokemon att, Pokemon def){ return true;}

	@Override
	protected void applySelfEffects(Pokemon p){;
		p.setMod(Stat.EVASION, 2);
	}

	@Override
	protected String describe(){
		return "резко сжимается, уменьшая свой силуэт";
	}

}