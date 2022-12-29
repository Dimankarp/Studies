package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;


public class Lampent extends Litwick{

	public Lampent()
	{
		setType(Type.GHOST, Type.FIRE);
		setStats(60D, 40D, 60D, 95D, 60D, 55D);
		addMove(new Minimize());

	}

	public Lampent(String name, int level)
	{
		super(name, level);
		setType(Type.GHOST, Type.FIRE);
		setStats(60D, 40D, 60D, 95D, 60D, 55D);
		addMove(new Minimize());
	}

}