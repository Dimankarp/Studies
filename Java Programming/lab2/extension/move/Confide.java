package extension.move;
import ru.ifmo.se.pokemon.*;

public class Confide extends StatusMove{

	public Confide()
	{
	 super(Type.NORMAL, 0, -1);
	}



	/*В покемонопедиях в поле "точность" указано N/A, что я интерпретирую
	 как стопроцентное попадание. Видимо, секреты точно доходят до слушателей.*/
	@Override
	protected boolean checkAccuracy(Pokemon att, Pokemon def){ return true;}

	@Override
	protected void applyOppEffects(Pokemon p){
		p.setMod(Stat.SPECIAL_ATTACK, -1);
	}

	@Override
	protected String describe(){
		return "рассказывает свой потаённый секретик, шокируя противника и лишая его концентрации";
	}

}