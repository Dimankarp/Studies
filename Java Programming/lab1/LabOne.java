import java.util.Random;
import static java.lang.Math.*; //
public class LabOne{

   public static void main(String[] args){


      //Создадим-ка одномерный массив и заполним его чисилками
      short[] a = new short[15];
      for(short i = 5; i<=19;i++) a[i-5] = i;


      /*Вызываем мистера Рандом, чтобы он наколдовал нам число от 0.0 до 1.0.
      Интересно, почему нельзя было написать нормальный метод получения
       псевдослучайных чисел с границами и прочими блэкджеками?.*/ 
      Random MrRandom = new Random();
      double[] x = new double[11];
      //Хнык-хнык...
      for(int i = 0; i < x.length; i++) x[i] = MrRandom.nextDouble()*(12+7)-7; 


      //Условие ещё и массивы одинаковыми именами просит называть 
      //- полный фарш!
      double[][] anotherA = new double[15][11]; 

      //Длина наибольшего элемента в anotherA, потребуется для красивой
      // таблички-вывода.
      int maxNumLen = 0; 

      for(int i = 0; i < anotherA.length; i++){
         for(int j = 0; j < anotherA[0].length; j++){
            double tempX = x[j];
            anotherA[i][j] = switch (a[i]){
            case 13 -> firstFormula(tempX, i, j);
            case 6, 7, 12, 14, 15, 16, 18 -> secondFormula(tempX, i, j);
            default -> thirdFormula(tempX, i, j);
            };

            //Находим самое длинное число, чтобы сделать красивый вывод
            if(String.valueOf(anotherA[i][j]).length() > maxNumLen) maxNumLen 
            = String.valueOf(anotherA[i][j]).length();

         }
      }




     for(int i = 0; i < anotherA.length; i++){
      for(int j = 0; j < anotherA[0].length; j++){
          if (Double.isNaN(anotherA[i][j]))
          {
            System.out.printf("%" + -maxNumLen + "s", '\u263A');
          }
         else
         {
            System.out.printf("%" + -maxNumLen + ".5f", anotherA[i][j]);
         }
      }
      System.out.println();
     }
   }

   //Ряд и столбец только для дебаггинга
   private static double firstFormula(double x, int row, int col) 
   {

      double sinTerm = sin(x);
      double atanTerm = atan(sinTerm);
      double result = exp(atanTerm);
      return result;
   }

   private static double secondFormula(double x, int row, int col) 
   {

      double asinTerm = asin( PI/4 * (x+2.5)/19);
      double divTerm = PI/asinTerm;
      double result = pow(divTerm, 3);
      return result;
   }

   private static double thirdFormula(double x, int row, int col)
   {

      double divTerm = 2/ ( exp(0.5) + pow(4*(2/3+x),3) );
      double result = log(divTerm);
      return result;
   }

}