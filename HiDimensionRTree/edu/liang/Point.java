/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */
package edu.liang;

/**
 * The Point interface represents a point in a k-dimensional space.
 * It also ccan be used to specify point keys that index into spatial data
 * structures.
 */
public interface Point<Coord> {

  /**
   * Returns the value of the coordinate of the given dimension.
   *
   * @return The value of the coordinate of the given dimension.
   */
  public Coord getCoord(int dimension);

  /**
   * Returns the number of dimensions in the point.
   *
   * @return The number of dimensions in the point.
   */
  public int getDimensions();
}
