/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */
package edu.liang;

import java.lang.reflect.Array;

/**
 * A Point implementation supporting k dimensions.
 */
public class GenericPoint< Coord extends Comparable<? super Coord>>
  implements Point<Coord>, Comparable< GenericPoint<Coord>>
{
    public  Coord[] __coordinates;
    public int currDimComp;

  /**
   * Constructs a GenericPoint with the specified dimensions.
   *
   * param dimensions The number of dimensions in the point.  Must be
   * greater than 0.
   */
    public GenericPoint(Class<Coord> T, int dimensions) {
		assert(dimensions > 0);
		__coordinates  = (Coord[]) Array.newInstance(T, dimensions);
		currDimComp=0;
    }

    public GenericPoint copy( ) {

		GenericPoint gp = new GenericPoint(this.__coordinates[0].getClass(), this.getDimensions());
		gp.setCoord(this);
		return gp;
    }

  /**
   * Two-dimensional convenience constructor.
   *
   */
    public GenericPoint(Class<Coord> T, Coord x, Coord y) {
		this(T, 2);
		setCoord(0, x);
		setCoord(1, y);
    }


    /**
    * Sets the value of the coordinate for the specified dimension.
    *
    * @exception ArrayIndexOutOfBoundsException If the dimension is
    * outside of the range [0,getDimensions()-1].
    */
    public void setCoord(int dimension, Coord value) throws ArrayIndexOutOfBoundsException
    {
		__coordinates[dimension] = value;
    }

    public void setCoord(GenericPoint<Coord> a_point) throws ArrayIndexOutOfBoundsException
    {
        for(int i=0; i<a_point.getDimensions(); i++)
        {
            __coordinates[i] = a_point.__coordinates[i];
        }
    }


    /**
    * Returns the value of the coordinate for the specified dimension.
	*
    */
    public Coord getCoord(int dimension) {
		return (Coord)__coordinates[dimension];
    }

    /**
    * Returns the number of dimensions of the point.
    *
    * @return The number of dimensions of the point.
    */
    public int getDimensions() { return __coordinates.length; }

    /**
    * Returns if two points are equal.
    */
    public boolean equals(Object obj) {

        if(!(obj instanceof GenericPoint))
            return false;
        GenericPoint point = (GenericPoint)obj;
        for(int i = 0; i < __coordinates.length; ++i)
            if(!__coordinates[i].equals(point.getCoord(i)))
            return false;
        return true;
    }


    /**
    * Returns a string representation of the point, listing its
    * coordinate values in order.
    *
    * @return A string representation of the point.
    */
    public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[ ");
		buffer.append(__coordinates[0].toString());

		for(int i = 1; i < __coordinates.length; ++i) {
			buffer.append(", ");
			buffer.append(__coordinates[i].toString());
		}

		buffer.append(" ]");
		return buffer.toString();
	}


	public void setCurrDimComp(int a_dim){
		currDimComp = a_dim;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
		public int compareTo(final GenericPoint<Coord> o) {
			int cmp = (this.__coordinates[currDimComp]).compareTo(o.__coordinates[currDimComp]);
			return cmp;
		}

	/**
	 * cast conversion from any type to any type (String actually) using String method.
	 * all boxed primitives have a String construtor, so this method does the trick:
	 */
	public static <I, O> O convert(I input, Class<O> outputClass) throws Exception {
		return input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString());
	}

	public int coordRoundInt(int a_dim){
		String RoundInt;
		try{
			RoundInt = convert (__coordinates[a_dim] , String.class  );
		} catch (Exception e){
			System.out.println("Something Wrong in type conversion ");
			return 0;
		}

		double value = Double.parseDouble(RoundInt);
		return (int)value;
	}



	/**
	 * Main function to test the basic function of Generic Point.
	 */
	public static void main(String args[]){
		GenericPoint<Integer> a = new GenericPoint(Integer.class, 5);
		a.setCoord(0, 1);a.setCoord(1, 2);a.setCoord(2, 3);
		Integer ret = a.coordRoundInt(1);
		System.out.println("Integer returned is " + ret);
		System.out.println("class name  " + a.__coordinates[0].getClass().toString());
		GenericPoint new_a = a.copy();
		ret = a.coordRoundInt(1);
		System.out.println("Integer returned is " + ret);
	}
}
