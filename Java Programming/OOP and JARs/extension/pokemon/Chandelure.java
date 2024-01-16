package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;

public class Chandelure extends Lampent{

	public Chandelure(){}
	
	public Chandelure(String name, int level)
	{
		super(name, level);
	}

	{
		setType(Type.GHOST, Type.FIRE);
		setStats(60D, 55D, 90D, 145D, 90D, 80D);
	}

}