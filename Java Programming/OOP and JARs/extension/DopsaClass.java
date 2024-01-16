package extension;
import ru.ifmo.se.pokemon.*;
import extension.pokemon.*;


public class DopsaClass{

	public static void main(String[] args)
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {System.out.println("Ах! Я убит! Меня убили! Ах! Юзер, за что?!");}));
		Battle b = new Battle();
		Pokemon p1 = new Zekrom("С.Клименков", 4);
		Pokemon p2 = new Cubone("А.Письмак", 20);
		Pokemon p3 = new Marowak("П.Балакшин", 25);
		Pokemon p4 = new Litwick("Студент-контрактник", 10);
		Pokemon p5 = new Lampent("Студент-с-одной-тройкой", 11);
		Pokemon p6 = new Chandelure("Студент-прошу-ПСЖ", 13);
		b.addAlly(p4);
		b.addAlly(p5);
		b.addAlly(p6);
		b.addFoe(p1);
		b.addFoe(p2);
		b.addFoe(p3);
		b.go();

	}


}