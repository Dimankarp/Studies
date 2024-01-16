package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;


public class Litwick extends Pokemon{

	public Litwick()
	{
		setType(Type.GHOST, Type.FIRE);
		setStats(50D, 30D, 55D, 65D, 55D, 20D);
		setMove(new DoubleTeam(), new Flamethrower());

	}

	public Litwick(String name, int level)
	{
		super(name, level);

		setType(Type.GHOST, Type.FIRE);
		setStats(50D, 30D, 55D, 65D, 55D, 20D);
		setMove(new DoubleTeam(), new Flamethrower());
	}

}