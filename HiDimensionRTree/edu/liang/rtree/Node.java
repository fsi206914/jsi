/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
---------------------------------------------------------------------------- */
package edu.liang.rtree;

import java.io.Serializable;
import edu.liang.Rectangle;
import edu.liang.GenericPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

/**
 * Node class is an index node in a Rtree. Similar to B plus Tree, a node have parent
 * node and children nodes. It also defines the maximum and minimum number of entryCount.
 * Node extends Rectangle class.
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


	/**
	 * Constructs a Node with the specified dimensions.
	 *
	 */
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

	/**
	 * Constructs a Node and dicriminate if the node is a leaf or not.
	 *
	 */
	public Node(Class T, int ndims, int a_maxNodeEntries, boolean a_leaf) {
		this(T, ndims, a_maxNodeEntries);
		leaf = leaf;
	}

	/**
	 * Copy Constructor
	 *
	 */
	public Node(Node a_Node) {
		this( ((Rectangle)a_Node).getClassName(), a_Node.dim, a_Node.maxEntryCount, true);
	}

	/**
	 * Constructor with only Rectangle instance and entryCount.
	 *
	 */
	public Node(Rectangle a_Rect, int entryCount ) {
		this( a_Rect.getClassName(), a_Rect.dim, entryCount, true);
		this.updateMBR(a_Rect);
		NodeID = nodeIdCount;
		nodeIdCount++;
	}

	/**
	 * add an entry in this node.
	 *
	 */
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

	/**
	 * delete entry. This is done by setting it to null and copying the last entry into its space.
	 *
	 */
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

	/**
	 * deletedMin/MaxX/Y is a rectangle that has just been deleted or made smaller.
	 * Thus, the MBR is only recalculated if the deleted rectangle influenced the old MBR
	 */
	void recalculateMBRIfInfluencedBy(Rectangle a_DeleteRect) {
		for(int i=0; i< this.dim; i++){
			this.min.setCurrDimComp(i);

			if (this.min.compareTo(a_DeleteRect.min ) == 0 || this.max.compareTo( a_DeleteRect.max ) == 0 ){
				recalculateMBR();
				return;
			}
		}
	}

	/**
	 * recalculate Min and Max for this node.
	 *
	 */
	void recalculateMBR() {
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

	/**
	 * Update the MBR information for this node.
	 *
	 */
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

	/**
	 * compute the the area of this MBR of this node.
	 *
	 */
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

	/**
	 * compute the requiredExpansion distance for this node
	 *
	 */
	public double getRequiredExpansion(Node a_Node){
		return ((Rectangle)this).getRequiredExpansion((Rectangle)a_Node);
	}

	public String printInfo(){
		return " NodeID = " + this.NodeID + " this.min =  " + this.min.toString()+   " this.max =  " + this.max.toString() + "  this.leaf =" + this.leaf;
	}


	public String toString(){
		return this.printInfo();
	}
}
