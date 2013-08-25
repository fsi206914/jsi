
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

    public void insert(Rectangle a_Rect)
    {
    assert (a_Rect.dim == numDims);

    Node oneLeaf = chooseLeaf(root);
    oneLeaf.children.add( a_Rect );
    ((Node) oneLeaf.children.getLast() ).parent = oneLeaf;

    if (oneLeaf.children.size() > maxNodeEntries)
    {
      Node[] splits = splitNode(oneLeaf);
      adjustTree(splits[0], splits[1]);
    }
    else
    {
      adjustTree(oneLeaf, null);
    }
    }


  private Node chooseLeaf(Node a_node)
  {
    if (a_node.leaf)  return a_node;

    double minIncArea = Double.MAX_VALUE;
    Node next = ((Node) a_node.children.getFirst());
    Iterator<Node> iterator = a_node.children.iterator();
    while (iterator.hasNext()) {
      Node c = iterator.next();
      double inc =  ((Rectangle)c ). getRequiredExpansion((Rectangle)a_node );
      if (inc < minIncArea)
      {
        minIncArea = inc;
        next = c;
      }
      else continue;
    }
    return chooseLeaf(next);

  }


  private Node[] splitNode(Node a_leaf)
  {
    // TODO: this class probably calls "tighten" a little too often.
    // For instance the call at the end of the "while (!cc.isEmpty())" loop
    // could be modified and inlined because it's only adjusting for the addition
    // of a single node.  Left as-is for now for readability.
    @SuppressWarnings("unchecked")
    Node[] newNodes = new Node[]
    { a_leaf, new Node( a_leaf ) };
    newNodes[1].parent = a_leaf.parent;
    if (a_leaf.parent != null)
    {
      a_leaf.parent.children.add(newNodes[1]);
    }
    LinkedList<Node> cc = new LinkedList<Node>(a_leaf.children);
    a_leaf.children.clear();
    Node[] firstTwo =  qPickSeeds(cc);
    newNodes[0].children.add(firstTwo[0]);
    newNodes[1].children.add(firstTwo[1]);
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



    private void tighten(Node... nodes)
    {
        assert(nodes.length >= 1): "Pass some nodes to tighten!";
        for (Node n: nodes) {
          assert(n.children.size() > 0) : "tighten() called on empty node!";
          n.recalculateMBR();
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

    public static void main(String args[]){

    RTree a = new RTree(3);
    System.out.println( a.root.children.getClass().toString() );

    }



}
