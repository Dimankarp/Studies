package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;


public class Marowak extends Cubone{

	public Marowak()
	{
		setType(Type.GROUND);
		setStats(60D, 80D, 110D, 50D, 80D, 45D);
		addMove(new StoneEdge());
	}

	public Marowak(String name, int level)
	{
		super(name, level);

		setType(Type.GROUND);
		setStats(60D, 80D, 110D, 50D, 80D, 45D);
		addMove(new StoneEdge());
	}

}