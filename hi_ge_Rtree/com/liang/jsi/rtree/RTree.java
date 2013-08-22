
package com.liang.jsi.rtree;

import gnu.trove.list.linked.TLinkedList;
//import gnu.trove.map.hash.TIntObjectHashMap;
//import gnu.trove.procedure.TIntProcedure;
//import gnu.trove.stack.TIntStack;
//import gnu.trove.stack.array.TIntArrayStack;

import java.io.Serializable;
import java.util.Properties;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;

import com.liang.jsi.GenericPoint;
import com.liang.jsi.Rectangle;
import com.liang.jsi.SpatialIndex;
import com.liang.jsi.rtree.Node;


public class RTree< Coord extends Comparable<? super Coord>> implements  Serializable {

    private static final long serialVersionUID = 5946232781609920309L;
    private static final Logger log = LoggerFactory.getLogger(RTree.class);
    private static final Logger deleteLog = LoggerFactory.getLogger(RTree.class.getName() + "-delete");

    // parameters of the tree
    private final static int DEFAULT_MAX_NODE_ENTRIES = 2;
    private final static int DEFAULT_MIN_NODE_ENTRIES = 1;
    int maxNodeEntries;
    int minNodeEntries;
    int numDims;
    private final Class own = Double.class;
    public Node root;

    public enum SeedPicker { LINEAR, QUADRATIC }
    private final SeedPicker seedPicker;


    public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
    {
    assert (minEntries <= (maxEntries / 2));
    this.numDims = numDims;
    this.maxNodeEntries = maxEntries;
    this.minNodeEntries = minEntries;
    this.seedPicker = seedPicker;

    root = buildRoot(true);
    }


    public RTree(int numDims)
    {
        this(DEFAULT_MAX_NODE_ENTRIES, DEFAULT_MIN_NODE_ENTRIES, numDims, RTree.SeedPicker.QUADRATIC);
    }


    private Node buildRoot(boolean asLeaf)
    {
        return new Node(own, numDims, maxNodeEntries);
    }

    public List<Rectangle <Coord>  > search(Rectangle a_Rect)
    {
    assert (a_Rect.dim == numDims);

    List<Rectangle <Coord>  > results = new TLinkedList<Rectangle <Coord> >();
    search(root, results, a_Rect);
    return results;
    }

    private void search(Node root, List<Rectangle <Coord>  > results, Rectangle a_Rect )
    {
    if (root.leaf)
    {
        if (Rectangle.isOverlap( (Rectangle)root, a_Rect ))
        {
          results.add( ((Rectangle)root) );
        }
    }
    else
    {
      for (Object c : root.children)
      {
        if (Rectangle.isOverlap( (Rectangle)c, a_Rect ))
        {
          search((Node)c, results, a_Rect);
        }
      }

    }

    }


    public static void main(String args[]){

    RTree a = new RTree(3);
    System.out.println( a.root.children.getClass().toString() );

    }



}
