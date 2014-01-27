/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */

package edu.liang;
import edu.liang.GenericPoint;
import java.io.Serializable;
import java.math.BigDecimal;
import edu.liang.Arithmetic;
import java.lang.reflect.Array;


/**
 * A super Rectangle implementation supporting k dimensions.
 */
public class Rectangle< Coord extends Comparable<? super Coord>> implements Serializable{

	public GenericPoint min;
	public GenericPoint max;

	public int dim;
	private Class own;
	public final Class nameDouble = Double.class;
	public final Class nameInt = Integer.class;
	public final Class nameFloat = Float.class;
	public static final int iMaxValue = 1000;
	public static final int iminValue = 1000;
	public static final double dMaxValue = 1000.0;
	public static final double dMinValue = 1000.0;
    /*
    * Rectangle initialization with Generic min and Generic max defined.
    *
    */
	public void init() {
		min = new GenericPoint(own, this.dim);
		max = new GenericPoint(own, this.dim);
		MinMaxInit();
	}
	
    /*
    * Based on the Generic class, MinMaxInit will set the infinite vlue for Min and Max
    * Point.
    *
    */	
    public void MinMaxInit(){

		if(own == nameDouble) infiniteHRectDouble();
		else infiniteHRectInteger();
	}

	public void infiniteHRectDouble() {

		for (int i=0; i<dim; ++i) {
			min.setCoord(i, dMaxValue);
			max.setCoord(i, dMinValue);
		}
	}

	public void infiniteHRectInteger() {

		for (int i=0; i<dim; ++i) {
			min.setCoord(i, iMaxValue);
			max.setCoord(i, iminValue);
		}
	}
	
    /*
    * Copy Constructor for Rectangle object.
    *
    * @return a copied Rectangle object.
    */
	public Rectangle copy() {

		Rectangle newRect = new Rectangle(this.own, this.dim);
		newRect.set(this);
		return newRect;
	}

    /*
    * default Constructor for Rectangle object.
    */
	public Rectangle() {
	}

    /*
    * isOverlap function is in charge of justify if two high-dimensional rectangle overlaps.
    *
    * @return if two rectangle overlaps.
    */
	public static boolean isOverlap( Rectangle p_Rect, Rectangle q_Rect){

		if( p_Rect.intersects(q_Rect) ) return true;
		else if ( p_Rect.contains(q_Rect) )  return true;
		else if ( p_Rect.containedBy(q_Rect) )  return true;
		else return false;

	}

    /*
    * Constructor for Rectangle object.
    * Currently, we only support int, double, float.
    *
    */
	public Rectangle(Class T, int ndims) {

		dim = ndims;
		if(  (T!= nameDouble) &&  (T!=nameInt) && (T!=nameFloat) )
			System.out.println("The type can not be supported");
		else
		{own = T; init();}

	}
    /*
    * set Rectangle's generic type
    *
    */
	public void setRectangle(Class T, int ndims) {

		dim = ndims;
		if(  (T!= nameDouble) &&  (T!=nameInt) && (T!=nameFloat) )
			System.out.println("The type can not be supported");
		else
		{own = T; init();}
	}

    /*
    * set Rectangle's Min and Max point
    *
    */
	public void set(GenericPoint a_min, GenericPoint a_max) {
		min.setCoord( a_min);
		max.setCoord( a_max);
	}
	
    /*
    * set Min and Max point by a Rectange object.
    *
    */
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


    /*
    * Justify if one high-dimensional rectangle contained by another Rectangle.
    *
    * @return true if this is contained by r.
    */
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

    /*
    * compute the required expansion distance
    *
    * @return the value.
    */
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

    /*
    * compute high dimensional subtraction between two rectangle's area
    *
    * @return the value.
    */
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
	
    /*
    * get The generic type's name
    *
    * @return the name.
    */
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

    /*
    * Basic unit test for Rectangle class.
    *
    * @return the value.
    */
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

