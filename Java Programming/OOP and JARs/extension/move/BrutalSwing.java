package extension.move;
import ru.ifmo.se.pokemon.*;

  public class BrutalSwing extends PhysicalMove{

	public BrutalSwing()
	{
	 super(Type.DARK, 60D, 100D);
	}

	@Override
	protected String describe(){
		return "агрессивно качается из стороны в сторону";
	}

}