
package com.liang.jsi;

/**
 * The Point interface represents a point in a k-dimensional space.
 * It is used to specify point keys that index into spatial data
 * structures.
 */
public interface Point<Coord> {
  /**
   * Returns the value of the coordinate of the given dimension.
   *
   * @return The value of the coordinate of the given dimension.
   * @exception IllegalArgumentException if the Point does not
   *            support the dimension.
   */
  public Coord getCoord(int dimension);

  /**
   * Returns the number of dimensions in the point.
   *
   * @return The number of dimensions in the point.
   */
  public int getDimensions();
}
