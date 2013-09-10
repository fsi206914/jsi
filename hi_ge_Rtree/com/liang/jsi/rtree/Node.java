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

    static int nodeIdCount = 0;
    int level;
    int entryCount;
    int maxEntryCount;
    public int NodeID;

    Node parent;
    public LinkedList<Node> children;
    Rectangle[] NodeRect;
    public boolean leaf;

    public boolean entry = false;

    public Node(Class T, int ndims, int a_maxNodeEntries) {
        super(T,ndims);
        //System.out.println(T);
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

    public Node(Rectangle a_Rect, int entryCount ) {
        this( a_Rect.getClassName(), a_Rect.dim, entryCount, true);
        this.updateMBR(a_Rect);
        NodeID = nodeIdCount;
        nodeIdCount++;
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

        //System.out.println(this.min.toString());
        //System.out.println(((Rectangle)children.get(0)).min.toString());

        this.min.setCoord( ((Rectangle)children.get(0)).min   );
        this.max.setCoord( ((Rectangle)children.get(0)).max   );
        for (int i = 1; i < children.size(); i++)
          for(int j=0; j<this.dim; j++)
          {
              this.min.setCurrDimComp(j);
              this.max.setCurrDimComp(j);

              GenericPoint Rect_min = ((Rectangle)children.get(i)).min;
              GenericPoint Rect_max = ((Rectangle)children.get(i)).max;

              if(this.min.compareTo(  Rect_min   ) > 0 )
                this.min.setCoord( j, Rect_min.getCoord(j));

              if(this.max.compareTo(  Rect_max    ) < 0 )
                this.max.setCoord( j, Rect_max.getCoord(j));
          }
    }


    void updateMBR(Rectangle a_NewRect ) {

          for(int j=0; j<this.dim; j++)
          {
              this.min.setCurrDimComp(j);
              this.max.setCurrDimComp(j);

              if(this.min.compareTo(a_NewRect.min) > 0 )
                this.min.setCoord( j, a_NewRect.min.getCoord(j));

              if(this.max.compareTo(a_NewRect.max) < 0 )
                this.max.setCoord( j, a_NewRect.max.getCoord(j));
          }
    }


    public double getArea(){

        double ret = 0.0;
        try {
        ret = ((Rectangle)this).getRectArea();
        }catch(StackOverflowError t) {
            //System.out.println("in Node.java  "+max.toString() + "   "+ min.toString());
            t.printStackTrace();
            System.exit(0);

        }

        return ret;
    }

    public double getRequiredExpansion(Node a_Node){
        return ((Rectangle)this).getRequiredExpansion((Rectangle)a_Node);
    }

    public String printInfo(){
        return " NodeID = " + this.NodeID + " this.min =  " + this.min.toString()+   " this.max =  " + this.max.toString() + "  this.leaf =" + this.leaf;
    }


    public String toString(){
        return this.printInfo();
    }

//    public void update (){
//
//    Coord[] minCoords = new Coord[this.dim];
//    Coord[] maxCoords = new Coord[this.dim];
//
//    for (int i = 0; i < numDims; i++)
//    {
//    this.min.setCoord(i = Float.MAX_VALUE;
//    maxCoords[i] = Float.MIN_VALUE;
//
//    for (Node c : n.children)
//    {
//      // we may have bulk-added a bunch of children to a node (eg. in
//      // splitNode)
//      // so here we just enforce the child->parent relationship.
//      c.parent = n;
//      if (c.coords[i] < minCoords[i])
//      {
//        minCoords[i] = c.coords[i];
//      }
//      if ((c.coords[i] + c.dimensions[i]) > maxCoords[i])
//      {
//        maxCoords[i] = (c.coords[i] + c.dimensions[i]);
//      }
//    }
//    }
//    }

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
