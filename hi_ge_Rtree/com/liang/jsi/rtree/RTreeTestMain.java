package com.liang.jsi.rtree;

import com.liang.jsi.GenericPoint;
import com.liang.jsi.Rectangle;
import com.liang.jsi.SpatialIndex;
import com.liang.jsi.rtree.Node;
import com.liang.jsi.rtree.RTree;

public class RTreeTestMain{

    public static void main(String[] args){

    GenericPoint<Integer> a1 = new GenericPoint(Integer.class, 2);
    a1.setCoord(0, 0 );a1.setCoord(1, 0 );
    //a1.setCoord(2, 1);

    GenericPoint<Integer> a2 = new GenericPoint(Integer.class, 2);
    a2.setCoord(0, 2 );a2.setCoord(1, 1 );
    //a2.setCoord(2, 4);

    GenericPoint<Integer> b1 = new GenericPoint(Integer.class, 2);
    b1.setCoord(0, 3 );b1.setCoord(1, 1 );
    //b1.setCoord(2, 1);

    GenericPoint<Integer> b2 = new GenericPoint(Integer.class, 2);
    b2.setCoord(0, 4 );b2.setCoord(1, 2 );
    //b2.setCoord(2, 3);

    GenericPoint<Integer> c1 = new GenericPoint(Integer.class, 2);
    c1.setCoord(0, 0 );c1.setCoord(1, 3 );
    //c1.setCoord(2, 1);

    GenericPoint<Integer> c2 = new GenericPoint(Integer.class, 2);
    c2.setCoord(0, 2 );c2.setCoord(1, 4 );
    //c2.setCoord(2, 3);

    GenericPoint<Integer> d1 = new GenericPoint(Integer.class, 2);
    d1.setCoord(0, 3 );d1.setCoord(1, 3 );
    //d1.setCoord(2, 1);

    GenericPoint<Integer> d2 = new GenericPoint(Integer.class, 2);
    d2.setCoord(0, 4 );d2.setCoord(1, 4 );
    //d2.setCoord(2, 3);

    GenericPoint<Integer> e1 = new GenericPoint(Integer.class, 2);
    e1.setCoord(0, 0 );e1.setCoord(1, 4 );
    //d1.setCoord(2, 1);

    GenericPoint<Integer> e2 = new GenericPoint(Integer.class, 2);
    e2.setCoord(0, 1 );e2.setCoord(1, 5 );
    //d2.setCoord(2, 3);

    Rectangle A = new Rectangle(Integer.class, 2);
    A.set(a1,a2);

    Rectangle B = new Rectangle(Integer.class, 2);
    B.set(b1,b2);

    Rectangle C = new Rectangle(Integer.class, 2);
    C.set(c1,c2);

    Rectangle D = new Rectangle(Integer.class, 2);
    D.set(d1,d2);

    Rectangle E = new Rectangle(Integer.class, 2);
    E.set(e1,e2);

    RTree<Integer> rtree = new RTree<Integer>(2, Integer.class);

    rtree.insert(A);
    rtree.insert(B);
    rtree.insert(C);
    rtree.insert(D);
    rtree.insert(E);

    System.out.println(rtree.toString());
    }
}
