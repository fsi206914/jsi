// Hyper-Rectangle class supporting KDTree class

package com.liang.jsi;

public class Rectangle< Coord extends Comparable<? super Coord>> {

    public GenericPoint min;
    public GenericPoint max;

    public int dim;

    private Class own;
    private final Class nameDouble = Double.class;
    private final Class nameInt = Integer.class;
    private final Class nameFloat = Float.class;

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
    }

    public Rectangle copy() {

        Rectangle newRect = new Rectangle(this.own, this.dim);
        newRect.set(this);
        return newRect;
    }


    public Rectangle(Class T, int ndims) {

        dim = ndims;
        if(  (T!= nameDouble) &&  (T!=nameInt) && (T!=nameFloat) )
            System.out.println("The type can not be supported");
        else
            {own = T; init();}

    }

    public void set(GenericPoint a_min, GenericPoint a_max) {

        min = a_min;
        max = a_max;

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
    aa.setCoord(0, 2);aa.setCoord(1, 2);aa.setCoord(2, 2);

    GenericPoint<Integer> bb = new GenericPoint(Integer.class, 3);
    bb.setCoord(0, 3);bb.setCoord(1, 3);bb.setCoord(2, 3);


    Rectangle Rect = new Rectangle(Integer.class, 3);
    Rect.set(a,b);

    Rectangle RRect = new Rectangle(Integer.class, 3);
    RRect.set(aa,bb);

    System.out.println(RRect.containedBy(Rect));

    }
}

