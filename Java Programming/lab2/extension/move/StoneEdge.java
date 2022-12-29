package extension.move;
import ru.ifmo.se.pokemon.*;
import java.lang.Math;

public class StoneEdge extends PhysicalMove{

	public StoneEdge()
	{
	 super(Type.ROCK, 100D, 80D);
	}

	@Override
	protected double calcCriticalHit(Pokemon att, Pokemon def)
	{
		double prob = (att.getStat(Stat.SPEED)/512) * 4;

		return prob > Math.random() ? 2 : 1;
	}

	@Override
	protected String describe(){
		return "хорошо прицелившись, с разбегу пронзает противника каменной заточкой";
	}

}