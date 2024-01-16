package extension.move;
import ru.ifmo.se.pokemon.*;

 public class Facade extends PhysicalMove{

	public Facade()
	{
	 super(Type.NORMAL, 70D, 100D);
	}

	/*Извращаемся по полной, основной код метода взят
	из декомпилированного DamageMove.class*/

	@Override
	protected double calcBaseDamage(final Pokemon pokemon, final Pokemon pokemon2) {
        Status cond = pokemon.getCondition();
		double finalPower = cond == Status.BURN || 
		 		  		 	cond == Status.PARALYZE||
		 		  		 	cond == Status.POISON ?  2 * this.power : this.power;

        return (0.4 * pokemon.getLevel() + 2.0) * finalPower / 150.0;
    }


	/*
	Просто кусок кода, который можно было бы использовать, если бы 
	у calcAttDefFactor в PhysicalMove не было модификатора final

	Это решение будет работать, если сменить Physical на Specialmove

	@Override
	protected double calcAttDefFactor(Pokemon att, Pokemon def){
		Status cond = att.getCondition();
		double attStat = cond == Status.BURN || 
		 		  		 cond == Status.PARALYZE||
		 		  		 cond == Status.POISON ?  2 * att.getStat(Stat.ATTACK) : att.getStat(Stat.ATTACK);
		return attStat/def.getStat(Stat.DEFENSE);
	}	
	*/
	@Override
	protected String describe(){
		return "опрокидывает на противника кусок фасада с ближайшего здания";
	}

}