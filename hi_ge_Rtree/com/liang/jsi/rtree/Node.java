//   Node.java
//   Java Spatial Index Library
//   Copyright (C) 2002-2005 Infomatiq Limited
//   Copyright (C) 2008-2010 aled@sourceforge.net
//

package com.liang.jsi.rtree;

import java.io.Serializable;
import com.liang.jsi.Rectangle;
import com.liang.jsi.GenericPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

/**
 * <p>Used by RTree. There are no public methods in this class.</p>
 */
public class Node< Coord extends Comparable<? super Coord>> extends Rectangle {
    private static final long serialVersionUID = -2823316966528817396L;

    int nodeId = 0;
    int level;
    int entryCount;
    int maxEntryCount;

    Node parent;
    public LinkedList<Node> children;
    Rectangle[] NodeRect;
    public boolean leaf;

    public Node(Class T, int ndims, int a_maxNodeEntries) {
        super(T,ndims);
        parent = null;
        maxEntryCount = a_maxNodeEntries;
        children = new LinkedList<Node>();
        NodeRect = new Rectangle[maxEntryCount];
        entryCount=0;
        leaf = true;
    }

    public Node(Class T, int ndims, int a_maxNodeEntries, boolean a_leaf) {
        this(T, ndims, a_maxNodeEntries);
        leaf = leaf;
    }

    public Node(Node a_Node) {
        this( ((Rectangle)a_Node).getClassName(), a_Node.dim, a_Node.maxEntryCount, true);
    }


    void addEntry(Rectangle a_Rect) {
        NodeRect[entryCount] = a_Rect;
        entryCount++;
        updateMBR(a_Rect);
    }

  // Return the index of the found entry, or -1 if not found
    int findEntry(Rectangle a_Rect) {
    for (int i = 0; i < entryCount; i++) {
        if ( NodeRect[i].equals(a_Rect)) {
          return i;
        }
    }
    return -1;
    }

  // delete entry. This is done by setting it to null and copying the last entry into its space.
  void deleteEntry(int i) {
	  int lastIndex = entryCount - 1;

      Rectangle deleteRect = NodeRect[lastIndex];

    if (i != lastIndex) {
        NodeRect[i] = NodeRect[lastIndex];
	}
    entryCount--;

    // adjust the MBR
    recalculateMBRIfInfluencedBy(deleteRect);
    NodeRect[lastIndex] = null;

  }


  // deletedMin/MaxX/Y is a rectangle that has just been deleted or made smaller.
  // Thus, the MBR is only recalculated if the deleted rectangle influenced the old MBR
    void recalculateMBRIfInfluencedBy(Rectangle a_DeleteRect) {

        for(int i=0; i< this.dim; i++){
            this.min.setCurrDimComp(i);

        if (this.min.compareTo(a_DeleteRect.min ) == 0 || this.max.compareTo( a_DeleteRect.max ) == 0 ){
            recalculateMBR();
            return;
            }
        }
    }

    void recalculateMBR() {

        this.min.setCoord( NodeRect[0].min);
        this.max.setCoord( NodeRect[0].max);


        for (int i = 1; i < entryCount; i++)
          for(int j=0; j<this.dim; j++)
          {
              this.min.setCurrDimComp(j);
              if(this.min.compareTo(NodeRect[i].min) > 0 )
                this.min.setCoord( j, NodeRect[i].min.getCoord(j));

              if(this.max.compareTo(NodeRect[i].max) < 0 )
                this.max.setCoord( j, NodeRect[i].max.getCoord(j));
          }
    }


    void updateMBR(Rectangle a_NewRect ) {

          for(int j=0; j<this.dim; j++)
          {
              this.min.setCurrDimComp(j);
              if(this.min.compareTo(a_NewRect.min) > 0 )
                this.min.setCoord( j, a_NewRect.min.getCoord(j));

              if(this.max.compareTo(a_NewRect.max) < 0 )
                this.max.setCoord( j, a_NewRect.max.getCoord(j));
          }
    }


    public double getArea(){
        return ((Rectangle)this).getArea();
    }

    public double getRequiredExpansion(Node a_Node){
        return ((Rectangle)this).getRequiredExpansion((Rectangle)a_Node);
    }

//  public int getEntryCount() {
//    return entryCount;
//  }
//
//  public int getId(int index) {
//    if (index < entryCount) {
//      return ids[index];
//    }
//    return -1;
//  }
//
//  boolean isLeaf() {
//    return (level == 1);
//  }
//
//  public int getLevel() {
//    return level;
//  }
}
