
package com.liang.jsi.rtree;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;

import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liang.jsi.GenericPoint;
import com.liang.jsi.Rectangle;
import com.liang.jsi.SpatialIndex;


public class RTree implements  Serializable {

    private static final long serialVersionUID = 5946232781609920309L;
    private static final Logger log = LoggerFactory.getLogger(RTree.class);
    private static final Logger deleteLog = LoggerFactory.getLogger(RTree.class.getName() + "-delete");

    // parameters of the tree
    private final static int DEFAULT_MAX_NODE_ENTRIES = 2;
    private final static int DEFAULT_MIN_NODE_ENTRIES = 1;
    int maxNodeEntries;
    int minNodeEntries;
    int numDims;
    public Node root;


    public enum SeedPicker { LINEAR, QUADRATIC }
    private final SeedPicker seedPicker;

    public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
    {
    assert (minEntries <= (maxEntries / 2));
    this.numDims = numDims;
    this.maxEntries = maxEntries;
    this.minEntries = minEntries;
    this.seedPicker = seedPicker;
    pointDims = new float[numDims];
    root = buildRoot(true);
    }

    public RTree(int maxEntries, int minEntries, int numDims)
    {
    this(maxEntries, minEntries, numDims, SeedPicker.LINEAR);
    }

    private Node buildRoot(boolean asLeaf)
    {
    float[] initCoords = new float[numDims];
    float[] initDimensions = new float[numDims];
    for (int i = 0; i < this.numDims; i++)
    {
      initCoords[i] = (float) Math.sqrt(Float.MAX_VALUE);
      initDimensions[i] = -2.0f * (float) Math.sqrt(Float.MAX_VALUE);
    }
    return new Node(initCoords, initDimensions, asLeaf);
    }
}
