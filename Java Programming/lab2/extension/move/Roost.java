package extension.move;
import ru.ifmo.se.pokemon.*;

public class Roost extends StatusMove{

	public Roost()
	{
	 super(Type.NORMAL, 0, -1);
	}


	@Override
	protected boolean checkAccuracy(Pokemon att, Pokemon def){ return true;}

	@Override
	protected void applySelfEffects(Pokemon p){;
		p.setMod(Stat.HP, -(int)(p.getStat(Stat.HP)/2));
	}

	@Override
	protected String describe(){
		return "пикирует на землю, отдыхает, пьёт чай и зализывает раны";
	}

}