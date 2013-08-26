// Hyper-Rectangle class supporting KDTree class

package com.liang.jsi;
import com.liang.jsi.GenericPoint;
import gnu.trove.list.TLinkable;
import java.io.Serializable;
import java.math.BigDecimal;
import com.liang.jsi.Arithmetic;
import java.lang.reflect.Array;

public class Rectangle< Coord extends Comparable<? super Coord>> implements
    Serializable, TLinkable<Rectangle <Coord>>{

    public GenericPoint min;
    public GenericPoint max;

    public int dim;

    private Class own;
    public final Class nameDouble = Double.class;
    public final Class nameInt = Integer.class;
    public final Class nameFloat = Float.class;

    private Rectangle previous;
    private Rectangle next;
    private long code;

    public Rectangle getNext(){

        return this.next;
    }

    public Rectangle getPrevious(){

        return this.previous;
    }

    public void setPrevious(Rectangle a_prev){

        previous = a_prev;
    }

    public void setNext(Rectangle a_next){

        this.next = a_next;
    }


    public final class hyperplane{

        int planeDim;
        Coord value;

        public hyperplane(int a_planeDim, Coord a_value)
        {
            planeDim = a_planeDim;
            value = a_value;
        }

    }

    public void init() {
        min = new GenericPoint(own, this.dim);
        max = new GenericPoint(own, this.dim);
        MinMaxInit();
    }

    public void MinMaxInit(){

//    Coord[] MinAndMax;
//    MinAndMax  = (Coord[]) Array.newInstance(own, 2);
//
//    Object temp_max = Class.forName(own).getConstructor(String.class).newInstance("1");

    if(own == nameDouble) infiniteHRectDouble();
    else infiniteHRectInteger();
    }

    public void infiniteHRectDouble() {

        for (int i=0; i<dim; ++i) {
            min.setCoord(i, 1000.0);
            max.setCoord(i, -1000.0);
        }
    }

    public void infiniteHRectInteger() {

        for (int i=0; i<dim; ++i) {
            min.setCoord(i, 1000);
            max.setCoord(i, -1000);
        }
    }

    public Rectangle copy() {

        Rectangle newRect = new Rectangle(this.own, this.dim);
        newRect.set(this);
        return newRect;
    }

    public Rectangle() {
    }

    public static boolean isOverlap( Rectangle p_Rect, Rectangle q_Rect){

    if( p_Rect.intersects(q_Rect) ) return true;
    else if ( p_Rect.contains(q_Rect) )  return true;
    else if ( p_Rect.containedBy(q_Rect) )  return true;
    else return false;

    }


    public Rectangle(Class T, int ndims) {

        dim = ndims;
        if(  (T!= nameDouble) &&  (T!=nameInt) && (T!=nameFloat) )
            System.out.println("The type can not be supported");
        else
            {own = T; init();}

    }

    public void setRectangle(Class T, int ndims) {

        dim = ndims;
        if(  (T!= nameDouble) &&  (T!=nameInt) && (T!=nameFloat) )
            System.out.println("The type can not be supported");
        else
            {own = T; init();}

    }

    public void set(GenericPoint a_min, GenericPoint a_max) {

        min.setCoord( a_min);
        max.setCoord( a_max);
    }

    public void set(Rectangle a_rect) {

        min.setCoord(a_rect.min);
        max.setCoord(a_rect.max);
    }

    public boolean intersects(Rectangle r) {
        boolean ret = true;

        for(int i=0; i<dim; i++)
        {
            min.setCurrDimComp(i);
            max.setCurrDimComp(i);
            ret = ret && (max.compareTo(r.min) >=0 ) && (min.compareTo(r.max) <=0 );
            if(ret==false) return ret;
        }
        return ret;
    }

    public boolean contains(Rectangle r) {
        boolean ret = true;

        for(int i=0; i<dim; i++)
        {
            min.setCurrDimComp(i);
            max.setCurrDimComp(i);
            ret = ret && (max.compareTo(r.max) >=0 ) && (min.compareTo(r.min) <=0 );
            if(ret==false) return ret;
        }
        return ret;
    }


    public boolean containedBy(Rectangle r) {
        boolean ret = true;

        for(int i=0; i<dim; i++)
        {
            min.setCurrDimComp(i);
            max.setCurrDimComp(i);
            ret = ret && (max.compareTo(r.max) <=0 ) && (min.compareTo(r.min) >=0 );
            if(ret==false) return ret;
        }
        return ret;
    }

    public double getRequiredExpansion(Rectangle a_Rect)
    {
    double area = getRectArea();
    double expanded = 1.0f;

    for (int i = 0; i < dim; i++)
    {
        double max_i = Arithmetic.MAX( this.max.getCoord(i), a_Rect.max.getCoord(i) );
        double min_i = Arithmetic.MIN( this.min.getCoord(i), a_Rect.min.getCoord(i) );

        expanded *= (max_i-min_i);
    }

    return (expanded - area - a_Rect.getRectArea());
    }


    public double getRectArea()
    {
    double area = 1.0f;
    for (int i = 0; i < dim; i++)
    {
        try {
        area *= Arithmetic.subtract( max.getCoord(i), min.getCoord(i) );
        }catch(StackOverflowError t) {
            System.out.println(max.toString() + "   "+ min.toString());
            t.printStackTrace();
            System.exit(0);

        }
    }
    return area;
    }

    public Class getClassName(){
        return this.own;
    }


    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Rectangle) {
           Rectangle r = (Rectangle) o;
           if(min.equals(r.min) && max.equals(r.max) )
              equals = true;
        }
        return equals;
    }


    public String toString() {
        return "A rectangle: " + min.toString() + "   " + max.toString() + "\n";
    }

    public int compareTo(Rectangle a_Rect) {
        return 1;
    }



    public static void main(String args[]){

    GenericPoint<Integer> a = new GenericPoint(Integer.class, 3);
    a.setCoord(0, 1);a.setCoord(1, 1);a.setCoord(2, 1);

    GenericPoint<Integer> b = new GenericPoint(Integer.class, 3);
    b.setCoord(0, 4);b.setCoord(1, 4);b.setCoord(2, 4);


    GenericPoint<Integer> aa = new GenericPoint(Integer.class, 3);
    aa.setCoord(0, 1);aa.setCoord(1, 1);aa.setCoord(2, 1);

    GenericPoint<Integer> bb = new GenericPoint(Integer.class, 3);
    bb.setCoord(0, 4);bb.setCoord(1, 4);bb.setCoord(2, 3);


    Rectangle Rect = new Rectangle(Integer.class, 3);
    Rect.set(a,b);

    Rectangle RRect = new Rectangle(Integer.class, 3);
    RRect.set(aa,bb);

    System.out.println(RRect.equals(Rect));

    }
}

