
package com.liang.jsi.rtree;

import gnu.trove.list.linked.TLinkedList;
//import gnu.trove.map.hash.TIntObjectHashMap;
//import gnu.trove.procedure.TIntProcedure;
//import gnu.trove.stack.TIntStack;
//import gnu.trove.stack.array.TIntArrayStack;

import java.io.Serializable;
import java.util.Properties;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.ArrayList;

import com.liang.jsi.GenericPoint;
import com.liang.jsi.Rectangle;
import com.liang.jsi.SpatialIndex;
import com.liang.jsi.rtree.Node;


public class RTree< Coord extends Comparable<? super Coord>> implements  Serializable {

    private static final long serialVersionUID = 59462327816090309L;
    private static final Logger log = LoggerFactory.getLogger(RTree.class);
    private static final Logger deleteLog = LoggerFactory.getLogger(RTree.class.getName() + "-delete");

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


    public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker, Class a_own)
    {
    assert (minEntries <= (maxEntries / 2));
    this.numDims = numDims;
    this.maxNodeEntries = maxEntries;
    this.minNodeEntries = minEntries;
    this.seedPicker = seedPicker;
    this.own = a_own;

    root = buildRoot(true);
    System.out.println(root.toString());

    }

    public RTree(int numDims)
    {

        this(DEFAULT_MAX_NODE_ENTRIES, DEFAULT_MIN_NODE_ENTRIES, numDims, RTree.SeedPicker.QUADRATIC, Double.class);
    }

    public RTree(int numDims, Class a_own)
    {
        this(DEFAULT_MAX_NODE_ENTRIES, DEFAULT_MIN_NODE_ENTRIES, numDims, RTree.SeedPicker.QUADRATIC, a_own);
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
        System.out.println("unchecked area");
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }

    public static void main(String[] args){

    GenericPoint<Double> a1 = new GenericPoint(Double.class, 2);
    a1.setCoord(0, 0.0);a1.setCoord(1, 0.0);
    //a1.setCoord(2, 1);

    GenericPoint<Double> a2 = new GenericPoint(Double.class, 2);
    a2.setCoord(0, 2.0);a2.setCoord(1, 1.0);
    //a2.setCoord(2, 4);

    GenericPoint<Double> b1 = new GenericPoint(Double.class, 2);
    b1.setCoord(0, 3.0);b1.setCoord(1, 1.0);
    //b1.setCoord(2, 1);

    GenericPoint<Double> b2 = new GenericPoint(Double.class, 2);
    b2.setCoord(0, 4.0);b2.setCoord(1, 2.0);
    //b2.setCoord(2, 3);

    GenericPoint<Double> c1 = new GenericPoint(Double.class, 2);
    c1.setCoord(0, 0.0);c1.setCoord(1, 3.0);
    //c1.setCoord(2, 1);

    GenericPoint<Double> c2 = new GenericPoint(Double.class, 2);
    c2.setCoord(0, 2.0);c2.setCoord(1, 4.0);
    //c2.setCoord(2, 3);

    GenericPoint<Double> d1 = new GenericPoint(Double.class, 2);
    d1.setCoord(0, 3.0);d1.setCoord(1, 3.0);
    //d1.setCoord(2, 1);

    GenericPoint<Double> d2 = new GenericPoint(Double.class, 2);
    d2.setCoord(0, 4.0);d2.setCoord(1, 4.0);
    //d2.setCoord(2, 3);

    GenericPoint<Double> e1 = new GenericPoint(Double.class, 2);
    e1.setCoord(0, 0.0);e1.setCoord(1, 4.0);
    //d1.setCoord(2, 1);

    GenericPoint<Double> e2 = new GenericPoint(Double.class, 2);
    e2.setCoord(0, 1.0);e2.setCoord(1, 5.0);
    //d2.setCoord(2, 3);

    Rectangle A = new Rectangle(Double.class, 2);
    A.set(a1,a2);

    Rectangle B = new Rectangle(Double.class, 2);
    B.set(b1,b2);

    Rectangle C = new Rectangle(Double.class, 2);
    C.set(c1,c2);

    Rectangle D = new Rectangle(Double.class, 2);
    D.set(d1,d2);

    Rectangle E = new Rectangle(Double.class, 2);
    E.set(e1,e2);

    RTree<Double> rtree = new RTree<Double>(2);

    rtree.insert(A);
    rtree.insert(B);
    rtree.insert(C);
    rtree.insert(D);
    rtree.insert(E);

    System.out.println(rtree.toString());
    }
}
