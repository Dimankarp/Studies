package extension.pokemon;
import ru.ifmo.se.pokemon.*;
import extension.move.*;


public class Zekrom extends Pokemon{

	public Zekrom()
	{
		setType(Type.DRAGON,Type.ELECTRIC);
		setStats(100D, 150D, 120D, 120D, 100D, 90D);
		setMove(new BrutalSwing(), new Confide(), new Roost(), new FlashCannon());
	}

	public Zekrom(String name, int level)
	{
		super(name, level);

		setType(Type.DRAGON,Type.ELECTRIC);
		setStats(100D, 150D, 120D, 120D, 100D, 90D);
		setMove(new BrutalSwing(), new Confide(), new Roost(), new FlashCannon());
	}

}