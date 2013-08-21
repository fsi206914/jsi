
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


  public RTree() {
  }

}
