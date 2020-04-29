package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList init= new PartialTreeList();
		
		for(Vertex v : graph.vertices)
		{
			PartialTree tree = new PartialTree(v);
			Vertex.Neighbor ptr = v.neighbors;
			while(ptr!=null)
			{
				
				MinHeap<Arc> arcs = tree.getArcs();
				arcs.insert(new Arc(v,ptr.vertex,ptr.weight));
				//System.out.println(arcs);
				ptr=ptr.next;
			}
			System.out.println(tree);
			init.append(tree);
		}
		return init;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		
		ArrayList<Arc> mst = new ArrayList<Arc>();
		
		while(ptlist.size>1)
		{
			
			PartialTree temp = ptlist.remove();
			//System.out.println(temp);


			while(!temp.getArcs().isEmpty()) {
				Arc min = temp.getArcs().deleteMin();

				try{PartialTree tree2 = ptlist.removeTreeContaining(min.getv2());
	
					//System.out.println(tree2);
					temp.merge(tree2);
					mst.add(min);
					ptlist.append(temp);
					break;
				
				}
				catch(Exception e)
				{
					//element not found
				}
			}

		}
		
		return mst;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	if(rear==null)
			throw new NoSuchElementException("Empty list");
    	
    	PartialTree tree = null;
    	Node ptr = rear;
    	
    	do {
    		Vertex v = vertex;
    		while(v.parent!=v)
    		{
    			v=v.parent;
    		}
    		if(v==ptr.tree.getRoot())
    		{
    			tree = ptr.tree;
    			Node prev = ptr;
    			Node next = ptr.next;
    			while(prev.next!=ptr)
    			{
    				prev = prev.next;
    			}
    			
    			if(prev==ptr && next == ptr)
    			{
    				rear = null;
    			}
    			else if(prev == next) {
    				if(ptr==rear)
    					rear=rear.next;
    				ptr.next.next = ptr.next;
    			}
    			else {
    				if(ptr==rear)
    				{
    					rear = prev;
    				}
    				prev.next = next;
    			}
    			size--;
    			ptr.next = null;
    			break;
    		}
    		ptr=ptr.next;
    		
    	}
    	while(ptr!=rear);
    	
    	if(tree==null)
    		throw new NoSuchElementException("no matching tree");
    	return tree;
    		
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}

