package extension.move;
import ru.ifmo.se.pokemon.*;

 public class DoubleTeam extends StatusMove{

	public DoubleTeam()
	{
	 super(Type.NORMAL, 0, -1);
	}


	@Override
	protected boolean checkAccuracy(Pokemon att, Pokemon def){ return true;}

	@Override
	protected void applySelfEffects(Pokemon p){;
		p.setMod(Stat.EVASION, 1);
	}

	@Override
	protected String describe(){
		return "бегает так быстро, что аж в глазах рябит - не попасть";
	}

}