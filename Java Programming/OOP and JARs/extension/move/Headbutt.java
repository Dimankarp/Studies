package extension.move;
import ru.ifmo.se.pokemon.*;
import java.lang.Math;

public class Headbutt extends PhysicalMove{ 

	public Headbutt()
	{
	 super(Type.NORMAL, 70D, 100D);
	}

	@Override
	protected void applyOppEffects(Pokemon p){
		if( 0.3 > Math.random()){Effect.flinch(p);}
	}

	@Override
	protected String describe(){
		return "выпячивает голову вперёд и таранит противника макушкой";
	}

}