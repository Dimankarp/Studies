package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;


public class Cubone extends Pokemon{

	public Cubone()
	{
		setType(Type.GROUND);
		setStats(50D, 50D, 95D, 40D, 50D, 35D);
		setMove(new Headbutt(), new Swagger(), new Facade());
	}

	public Cubone(String name, int level)
	{
		super(name, level);

		setType(Type.GROUND);
		setStats(50D, 50D, 95D, 40D, 50D, 35D);
		setMove(new Headbutt(), new Swagger(), new Facade());
		//Headbutt requires 12
	}

}