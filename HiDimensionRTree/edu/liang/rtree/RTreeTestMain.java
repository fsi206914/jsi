/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */
package edu.liang.rtree;

import edu.liang.Rectangle;
import edu.liang.GenericPoint;

public class RTreeTestMain{

    public static void main(String[] args){

    GenericPoint<Double> a1 = new GenericPoint(Double.class, 3);
    a1.setCoord(0, 0.0);a1.setCoord(1, 0.0);
        a1.setCoord(2, 1.0);

    GenericPoint<Double> a2 = new GenericPoint(Double.class, 3);
    a2.setCoord(0, 2.0);a2.setCoord(1, 1.0);
        a2.setCoord(2, 4.0);

    GenericPoint<Double> b1 = new GenericPoint(Double.class, 3);
    b1.setCoord(0, 3.0);b1.setCoord(1, 1.0);
        b1.setCoord(2, 1.0);

    GenericPoint<Double> b2 = new GenericPoint(Double.class, 3);
    b2.setCoord(0, 4.0);b2.setCoord(1, 2.0);
        b2.setCoord(2, 3.0);

    GenericPoint<Double> c1 = new GenericPoint(Double.class, 3);
    c1.setCoord(0, 0.0);c1.setCoord(1, 3.0);
        c1.setCoord(2, 1.0);

    GenericPoint<Double> c2 = new GenericPoint(Double.class, 3);
    c2.setCoord(0, 2.0);c2.setCoord(1, 4.0);
        c2.setCoord(2, 3.0);

    GenericPoint<Double> d1 = new GenericPoint(Double.class, 3);
    d1.setCoord(0, 3.0);d1.setCoord(1, 3.0);
        d1.setCoord(2, 1.0);

    GenericPoint<Double> d2 = new GenericPoint(Double.class, 3);
    d2.setCoord(0, 4.0);d2.setCoord(1, 4.0);
        d2.setCoord(2, 3.0);

    GenericPoint<Double> e1 = new GenericPoint(Double.class, 3);
    e1.setCoord(0, 0.0);e1.setCoord(1, 4.0);
        e1.setCoord(2, 1.0);

    GenericPoint<Double> e2 = new GenericPoint(Double.class, 3);
    e2.setCoord(0, 1.0);e2.setCoord(1, 5.0);
        e2.setCoord(2, 3.0);

    Rectangle A = new Rectangle(Double.class, 3);
    A.set(a1,a2);

    Rectangle B = new Rectangle(Double.class, 3);
    B.set(b1,b2);

    Rectangle C = new Rectangle(Double.class, 3);
    C.set(c1,c2);

    Rectangle D = new Rectangle(Double.class, 3);
    D.set(d1,d2);

    Rectangle E = new Rectangle(Double.class, 3);
    E.set(e1,e2);

    RTree<Double> rtree = new RTree<Double>(3);

    rtree.insert(A);
    rtree.insert(B);
    rtree.insert(C);
    rtree.insert(D);
    rtree.insert(E);

    System.out.println(rtree.toString());
    }
}
