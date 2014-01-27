/* ----------------------------------------------------------------------------
    Author: Liang Tang
    Email:  liang@auburn.edu
    Date:   Sept, 2013
    Decrption: High dimensional R-Tree library for research use only
	---------------------------------------------------------------------------- */
package edu.liang.rtree;

import java.io.Serializable;
import java.util.Properties;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

import java.util.LinkedList;
import java.util.ArrayList;

import edu.liang.GenericPoint;
import edu.liang.Rectangle;
import edu.liang.rtree.Node;

/*
 *R-trees are tree data structures used for spatial access methods, i.e., for indexing multi-dimensional information such as geographical coordinates, 
 *rectangles or polygons. The R-tree was proposed by Antonin Guttman in 1984[1] and has found significant use in both theoretical and applied contexts.[2] 
 *A common real-world usage for an R-tree might be to store spatial objects such as restaurant locations or the polygons that typical maps are made of: streets, buildings, 
 *outlines of lakes, coastlines, etc. and then find answers quickly to queries such as "Find all museums within 2 km of my current location", 
 *"retrieve all road segments within 2 km of my location" (to display them in a navigation system) or "find the nearest gas station" 
 *(although not taking roads into account).
 *
 *
 *
 * [1]: Guttman, A. (1984). "R-Trees: A Dynamic Index Structure for Spatial Searching". Proceedings of the 1984 ACM SIGMOD international conference on Management of data - SIGMOD '84. p. 47.
 * [2]: Y. Manolopoulos; A. Nanopoulos; Y. Theodoridis (2006). R-Trees: Theory and Applications. Springer. ISBN 978-1-85233-977-7. Retrieved 8 October 2011.
 */
public class RTree< Coord extends Comparable<? super Coord>> implements  Serializable {

	private static final long serialVersionUID = 59462327816090309L;

	// parameters of the tree
	private final static int DEFAULT_MAX_NODE_ENTRIES = 2;
	private final static int DEFAULT_MIN_NODE_ENTRIES = 1;
	int maxNodeEntries;
	int minNodeEntries;
	int numDims;
	public Class own = Double.class;
	public Node root;

	public enum SeedPicker { LINEAR, QUADRATIC }
	private final SeedPicker seedPicker;

	/**
	 * Constructs a RTree with the specified parameters(maxEntries, minEntries, seedpicker, the generic type).
	 *
	 */
	public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker, Class a_own)
	{
		assert (minEntries <= (maxEntries / 2));
		this.numDims = numDims;
		this.maxNodeEntries = maxEntries;
		this.minNodeEntries = minEntries;
		this.seedPicker = seedPicker;
		this.own = a_own;

		root = buildRoot(true);
	}
	
	/**
	 * Constructs a RTree with the specified dimensions.
	 *
	 */
	public RTree(int numDims)
	{

		this(DEFAULT_MAX_NODE_ENTRIES, DEFAULT_MIN_NODE_ENTRIES, numDims, RTree.SeedPicker.QUADRATIC, Double.class);
	}
	
	/**
	 * Constructs a RTree with the specified dimensions and generic type.
	 *
	 */
	public RTree(int numDims, Class a_own)
	{
		this(DEFAULT_MAX_NODE_ENTRIES, DEFAULT_MIN_NODE_ENTRIES, numDims, RTree.SeedPicker.QUADRATIC, a_own);
	}

	/**
	 * Start building Rtree from the root.
	 *
	 * @return the root of the Rtree
	 */
	private Node buildRoot(boolean asLeaf)
	{
		return new Node(own, numDims, maxNodeEntries);
	}
	
	/**
	 * Search Rtree by an input, a rectangle.
	 *
	 * @return the Rectangle list.
	 */
	public List<Rectangle <Coord>  > search(Rectangle a_Rect)
	{
		assert (a_Rect.dim == numDims);
		List<Rectangle <Coord>  > results = new ArrayList<Rectangle <Coord> >();
		search(root, results, a_Rect);
		return results;
	}
	
	/**
	 * Searching is quite similar to searching in a B+ tree. 
	 * The search starts from the root node of the tree. Every internal node contains a set of rectangles and pointers 
	 * to the corresponding child node and every leaf node contains the rectangles of spatial objects. 
	 * For every rectangle in a node, it has to be decided if it overlaps the search rectangle or not. 
	 * If yes, the corresponding child node has to be searched also. 
	 * Searching is done like this in a recursive manner until all overlapping nodes have been traversed. 
	 * When a leaf node is reached, the contained bounding boxes (rectangles) are tested against the search rectangle and 
	 * their objects (if there are any) are put into the result set if they lie within the search rectangle.
	 *
	 */
	private void search(Node root, List<Rectangle <Coord>  > results, Rectangle a_Rect )
	{
		if (root.entry)
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
	
	/**
	 *To insert an object, the tree is traversed recursively from the root node. 
	 * At each step, all rectangles in the current directory node are examined, and a candidate is chosen using a heuristic 
	 * such as choosing the rectangle which requires least enlargement. The search then descends into this page, until reaching a leaf node. 
	 * If the leaf node is full, it must be split before the insertion is made. 
	 * Again, since an exhaustive search is too expensive, a heuristic is employed to split the node into two. 
	 * Adding the newly created node to the previous level, this level can again overflow, 
	 * and these overflows can propagate up to the root node; when this node also overflows, a new root node is created and the tree has increased in height.
	 */
	public void insert(Rectangle a_Rect)
	{
		assert (a_Rect.dim == numDims): "The number of dim in Rtree is different from the one in new inserted rectangle";

		Node oneLeaf = chooseLeaf(root, a_Rect);
		Node InsertedNode = new Node(a_Rect, this.maxNodeEntries);
		InsertedNode.entry = true;
		oneLeaf.children.add( InsertedNode );

		((Node) oneLeaf.children.getLast() ).parent = oneLeaf;

		if (oneLeaf.children.size() > maxNodeEntries)
		{
			Node[] splits = splitNode(oneLeaf);
			adjustTree(splits[0], splits[1]);
		}
		else
			adjustTree(oneLeaf, null);
	}

	/**
	 * At each level, the algorithm needs to decide in which subtree to insert the new data object. 
	 * When a data object is fully contained in a single rectangle, the choice is clear. 
	 * When there are multiple options or rectangles in need of enlargement, the choice can have a significant impact on the performance of the tree.
	 * In the classic R-tree, objects are inserted into the subtree that needs the least enlargement. 
	 * In the more advanced R*-tree, a mixed heuristic is employed. At leaf level, it tries to minimize 
	 * the overlap (in case of ties, prefer least enlargement and then least area); at the higher levels, 
	 * it behaves similar to the R-tree, but on ties again preferring the subtree with smaller area. 
	 * The decreased overlap of rectangles in the R*-tree is one of the key benefits over the traditional 
	 * R-tree (this is also a consequence of the other heuristics used, not only the subtree choosing).
	 */
	private Node chooseLeaf(Node a_node, Rectangle a_Rect)
	{
		if (a_node.leaf)  return a_node;

		double minIncArea = Double.MAX_VALUE;
		Node next = ((Node) a_node.children.getFirst());
		Iterator<Node> iterator = a_node.children.iterator();
		while (iterator.hasNext()) {
			Node c = iterator.next();
			double inc =  ((Rectangle)c ). getRequiredExpansion(a_Rect);

			if (inc < minIncArea)
			{
				minIncArea = inc;
				next = c;
			}
			else continue;
		}
		return chooseLeaf(next, a_Rect);

	}


	/**
	 * Detailed Splitting process.
	 *
	 * Since redistributing all objects of a node into two nodes has an exponential number of options, 
	 * a heuristic needs to be employed to find the best split. In the classic R-tree, Guttman proposed two such heuristics, 
	 * called QuadraticSplit and LinearSplit.
	 */
	private Node[] splitNode(Node a_leaf)
	{
		@SuppressWarnings("unchecked")
			Node[] newNodes = new Node[]
			{ new Node( a_leaf ), new Node( a_leaf ) };
		if (a_leaf.parent != null)
		{
			newNodes[0].parent = newNodes[1].parent = a_leaf.parent;
		}
		else{
			newNodes[0].parent = newNodes[1].parent = this.root;
		}
		if(newNodes[0].parent.leaf == true) newNodes[0].parent.leaf=false;

		LinkedList<Node> cc = new LinkedList<Node>(a_leaf.children);

		if( a_leaf.children.size()>0  && !((Node)a_leaf.children.getFirst()).entry )
		{
			newNodes[0].leaf = false;newNodes[1].leaf = false;
		}

		a_leaf.children.clear(); newNodes[0].children.clear(); newNodes[1].children.clear();
		newNodes[0].parent.children.remove(a_leaf);
		newNodes[0].parent.children.add(newNodes[1]);
		newNodes[0].parent.children.add(newNodes[0]);

		Node[] firstTwo =  qPickSeeds(cc);
		newNodes[0].children.add(firstTwo[0]);
		newNodes[1].children.add(firstTwo[1]);


		printAllNodes(firstTwo);

		tighten(newNodes);
		Random random = new Random();
		while (!cc.isEmpty())
		{
			if ((newNodes[0].children.size() >= minNodeEntries)
					&& (newNodes[1].children.size() + cc.size() == minNodeEntries))
			{
				newNodes[1].children.addAll(cc);
				cc.clear();
				tighten(newNodes); // Not sure this is required.
				return newNodes;
			}
			else if ((newNodes[1].children.size() >= minNodeEntries)
					&& (newNodes[0].children.size() + cc.size() == minNodeEntries))
			{
				newNodes[0].children.addAll(cc);
				cc.clear();
				tighten(newNodes); // Not sure this is required.
				return newNodes;
			}
			Node nextNode = qPickNext(cc, newNodes);
			Node preferred;
			double e0 = nextNode.getRequiredExpansion(newNodes[0]);
			double e1 = nextNode.getRequiredExpansion(newNodes[1]);
			if (e0 < e1)
			{
				preferred = newNodes[0];
			}
			else if (e0 > e1)
			{
				preferred = newNodes[1];
			}
			else
			{
				double a0 = newNodes[0].getArea();
				double a1 = newNodes[1].getArea();
				if (a0 < a1)
				{
					preferred = newNodes[0];
				}
				else if (a0 > a1)
				{
					preferred = newNodes[1];
				}
				else
				{
					if (newNodes[0].children.size() < newNodes[1].children.size())
					{
						preferred = newNodes[0];
					}
					else if (newNodes[0].children.size() > newNodes[1].children.size())
					{
						preferred = newNodes[1];
					}
					else
					{
						preferred = newNodes[ (new Random()).nextInt(2) ];
					}
				}
			}
			preferred.children.add(nextNode);
			tighten(preferred);
		}
		return newNodes;
	}


	/**
	 * In quadratic split, the algorithm searches the pair of rectangles that is the worst combination 
	 * to have in the same node, and puts them as initial objects into the two new groups. 
	 * It then searches the entry which has the strongest preference for one of the groups (in terms of area increase) 
	 * and assigns the object to this group until all objects are assigned (satisfying the minimum fill).
	 *
	 */
	private Node[] qPickSeeds(LinkedList<Node> Nodes )
	{
		@SuppressWarnings("unchecked")
			Node[] bestPair = new Node[2];
		double maxWaste = -1.0f * Double.MAX_VALUE;
		for (Node n1: Nodes)
		{
			for (Node n2: Nodes)
			{
				if (n1 == n2) continue;
				double n1a = n1.getArea();
				double n2a = n2.getArea();
				double whole = n1.getRequiredExpansion(n2);

				double waste = whole - n1a - n2a;
				if ( waste > maxWaste )
				{
					maxWaste = waste;
					bestPair[0] = n1;
					bestPair[1] = n2;
				}
			}
		}
		Nodes.remove(bestPair[0]);
		Nodes.remove(bestPair[1]);
		return bestPair;
	}

	private Node qPickNext(LinkedList<Node> cc, Node[] nn)
	{
		double maxDiff = -1.0f * Double.MAX_VALUE;
		Node nextC = null;
		for ( Node c: cc )
		{
			double n0Exp = nn[0].getRequiredExpansion(c);
			double n1Exp = nn[1].getRequiredExpansion(c);
			double diff = Math.abs(n1Exp - n0Exp);
			if (diff > maxDiff)
			{
				maxDiff = diff;
				nextC = c;
			}
		}
		assert (nextC != null) : "No node selected from qPickNext";
			cc.remove(nextC);
			return nextC;
	}

	private void tighten(Node... nodes)
	{
		assert(nodes.length >= 1): "Pass some nodes to tighten!";
			for (Node n: nodes) {
				assert(n.children.size() > 0) : "tighten() called on empty node!";
					n.recalculateMBR();
			}
	}

	private void printAllNodes(Node... nodes)
	{
		assert(nodes.length >= 1): "Pass some nodes to tighten!";
			for (Node n: nodes) {
				n.printInfo();
			}
	}


	private void adjustTree(Node n, Node newNode)
	{
		if (n == root)
		{
			if (newNode != null)
			{
				// build new root and add children.
				root = buildRoot(false);
				root.children.add(n);
				n.parent = root;
				root.children.add(newNode);
				newNode.parent = root;
			}
			tighten(root);
			return;
		}
		tighten(n);
		if (newNode != null)
		{
			tighten(newNode);
			if (n.parent.children.size() > this.maxNodeEntries)
			{
				Node[] splits = splitNode(n.parent);
				adjustTree(splits[0], splits[1]);
			}
		}
		if (n.parent != null)
		{
			adjustTree(n.parent, null);
		}
	}

	/**
	 * Implement a Tree Printer to check if my implementation is good or not.
	 *
	 */
	protected static class TreePrinter {

		public static < T extends Comparable<? super T>> String getString(RTree<T> tree) {
			if (tree.root == null) return "Tree has no nodes.";
			return getString(tree.root, " ", true);
		}

		private static < T extends Comparable<? super T>> String getString(Node node, String prefix, boolean isTail) {
			StringBuilder builder = new StringBuilder();

			if (node.parent!=null) {

				if(node.entry)
					builder.append(prefix + "Entry" + "$--- " + "[  ] " + "Rectangle=" + node.printInfo() );
				else
					builder.append(prefix + "     " + "$--- " + "[  ] " + "Rectangle=" + node.printInfo() );
				builder.append("\n");
			} else {
				builder.append("Root:" +   "$--- " + "  Rectangle =" + node.printInfo() +  "\n");
			}
			List<Node> children = null;
			children = new ArrayList<Node>( node.maxEntryCount );

			Iterator<Node> iterator = node.children.iterator();
			while (iterator.hasNext()) {
				Node c = iterator.next();
				children.add(c);
			}

			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					if( children.get(i).entry == true )
						builder.append(getString(children.get(i), prefix + "  ", true));
					else
						builder.append(getString(children.get(i), prefix+ "  ",  false));
				}
			}
			return builder.toString();
		}
	}

	/**
	 * toString() method
	 *
	 */
	@Override
		public String toString() {
			return TreePrinter.getString(this);
		}

}
