package com.liang.jsi;

import com.liang.jsi.GenericPoint;
import java.math.BigDecimal;
import java.lang.reflect.Array;
import java.lang.Math;

public class Arithmetic{

    public static <I, O> O convert(I input, Class<O> outputClass) throws Exception {
        return input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString());
    }

    public static < Coord extends Comparable<? super Coord>> double add(Coord a, Coord b)
    {

        String a_string, b_string;
        try{
            a_string = convert (a , String.class  );
            b_string = convert (b , String.class  );
        } catch (Exception e){

            System.out.println("Something Wrong in add conversion ");
            return 1.00;
        }

        double ret = Double.parseDouble(a_string)+ Double.parseDouble(b_string);
        return ret;
    }

    public static < Coord extends Comparable<? super Coord>> double subtract(Coord a, Coord b)
    {

        String a_string, b_string;
        try{
            a_string = convert (a , String.class  );
            b_string = convert (b , String.class  );
        } catch (Exception e){

            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }

        double ret = Double.parseDouble(a_string)- Double.parseDouble(b_string);
        return ret;
    }

    public static < Coord extends Comparable<? super Coord>> double MIN(Coord a, Coord b)
    {

        String a_string, b_string;
        try{
            a_string = convert (a , String.class  );
            b_string = convert (b , String.class  );
        } catch (Exception e){

            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }

        double ret = Math.min(Double.parseDouble(a_string), Double.parseDouble(b_string));
        return ret;
    }

    public static < Coord extends Comparable<? super Coord>> double MAX(Coord a, Coord b)
    {

        String a_string, b_string;

        try{
            a_string = convert (a , String.class  );
            b_string = convert (b , String.class  );
        } catch (Exception e){

            System.out.println("Something Wrong in subtract conversion ");
            return 1.00;
        }
		
        double ret = Math.max(Double.parseDouble(a_string), Double.parseDouble(b_string));
        return ret;
    }
}
