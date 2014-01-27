/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */
package edu.liang;

import edu.liang.GenericPoint;
import java.math.BigDecimal;
import java.lang.reflect.Array;
import java.lang.Math;

/**
 * This class is in charge of Arithmetic operations between Generic numbers.
 * Since we don't know the exact type of the Generic in java, we have to do the operations one by one.
 * This class implements types of double float and int, Since float and int can be both converted to Double. 
 * Operations consists add, subtract, MIN, and MAX.
 */

public class Arithmetic{

    /*
    * Convert Generictype to a concrete type by Java Reflection.
    * 
    * @return the concrete distance of the type.
    */
    public static <I, O> O convert(I input, Class<O> outputClass) throws Exception {
        return input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString());
    }

    /*
    * Do addition between two numbers.
    * 
    * @return the addition result.
    */
    public static < Coord extends Comparable<? super Coord>> double add(Coord a, Coord b)
    {

        String aString, bString;
        try{
            aString = convert (a , String.class  );
            bString = convert (b , String.class  );
        } catch (Exception e){
            System.out.println("Something Wrong in add conversion ");
            return 1.00;
        }

        double ret = Double.parseDouble(aString)+ Double.parseDouble(bString);
        return ret;
    }
    
    /*
    * Do subtraction between two numbers.
    * 
    * @return the subtract result.
    */
    public static < Coord extends Comparable<? super Coord>> double subtract(Coord a, Coord b)
    {
        String aString, bString;
        try{
            aString = convert (a , String.class  );
            bString = convert (b , String.class  );
        } catch (Exception e){

            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }
        double ret = Double.parseDouble(aString)- Double.parseDouble(bString);
        return ret;
    }
    
    /*
    * Do Min value between two numbers.
    * 
    * @return the Min result.
    */
    public static < Coord extends Comparable<? super Coord>> double MIN(Coord a, Coord b)
    {
        String aString, bString;
        try{
            aString = convert (a , String.class  );
            bString = convert (b , String.class  );
        } catch (Exception e){
            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }

        double ret = Math.min(Double.parseDouble(aString), Double.parseDouble(bString));
        return ret;
    }
    
    /*
    * Do MAX between two numbers.
    * 
    * @return the Max result.
    */
    public static < Coord extends Comparable<? super Coord>> double MAX(Coord a, Coord b)
    {
        String aString, bString;
        try{
            aString = convert (a , String.class  );
            bString = convert (b , String.class  );
        } catch (Exception e){
            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }
		
        double ret = Math.max(Double.parseDouble(aString), Double.parseDouble(bString));
        return ret;
    }
}
