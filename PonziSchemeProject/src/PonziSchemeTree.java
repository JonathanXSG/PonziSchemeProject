import java.util.ArrayList;
import java.util.Iterator;

public class PonziSchemeTree implements Iterable<Member>, Cloneable{

	private Node root; 
	private int size; 
	
	public PonziSchemeTree() { 
		root = null; 
		size = 0; 
	}
	
	public Position addChildAfter(Member member){
		Position newPos = null;
		if(size==0) {
			newPos=addRoot(member);
		}
		for(Position p: positions()) {
			if(p.getElement().getName().equals(member.getSponsor())) {
				if(numChildren(p)>0) {
					newPos = addChild(p, member);
					((Node) newPos).mentor = ((Node) p).children.get(((Node) p).children.size()-2);
				}
				else {
					newPos = addChild(p, member);
					((Node) newPos).mentor = ((Node) p);
				}
			}
		}
		return newPos;
	}
	
	private Node validate(Position p) throws IllegalArgumentException { 
		if (!(p instanceof Node)) 
			throw new IllegalArgumentException("Invalid position type for this implementation."); 
		Node np = (Node) p; 
		if (np.getSponsor() == np)
			throw new IllegalArgumentException("Target position is not part of a tree.");
		
		// the following validates that p is a position in this tree
		if (np.getOwnerTree() != this)
			throw new IllegalArgumentException("Target position is not part of the tree.");	
		return np; 
	}
	
	public Position root() {
		return root;
	}

	public Position sponsor(Position p) throws IllegalArgumentException {
		Node np = this.validate(p); 
		return np.getSponsor(); 
	}
	
	public Position mentor(Position p) throws IllegalArgumentException {
		Node np = this.validate(p); 
		return np.getMentor(); 
	}

	public Iterable<Position> children(Position p)
			throws IllegalArgumentException {
		Node np = this.validate(p);
		ArrayList<Position> result = 
				new ArrayList<Position>(); 
		if (np.getChildren() != null) 
			for(Position cp : np.getChildren())
				result.add(cp); 
		return result; 
	}

	public int numChildren(Position p) throws IllegalArgumentException {
		Node np = validate(p);  
		if (np.getChildren() != null) 
			return np.getChildren().size();
		else 
			return 0; 
	}

	public int size() {
		return size;
	}

	// some tree update operations.....
	public Position addRoot(Member element) throws IllegalStateException { 
		if (this.root != null) 
			throw new IllegalStateException("Tree must be empty to add a root."); 
		root = new Node(element, null, null, this); 
		size++; 
		return root; 
	}

	/**
	 * Add a new element as a child to a given position in the tree
	 * @param p the position to be the parent of the new element position
	 * @param element the new element to add to the tree
	 * @return the Position<E> of where the new element is stored
	 * @throws IllegalArgumentException if the position is not valid.....
	 */
	public Position addChild(Position p, Member element) 
			throws IllegalArgumentException { 
		Node np = validate(p);  
		Node nuevo = new Node(element, np, null, this); 
		if (np.getChildren() == null)
			np.setChildren(new ArrayList<Node>());
		np.getChildren().add(nuevo); 
		size++; 
		return nuevo; 
	}
	
	public Member remove(Position p) throws IllegalArgumentException { 
		Node ntd = validate(p); 
		Member etr = ntd.getElement(); 
		Node parent = ntd.getSponsor(); 
		if (parent == null)    // then ntd is the root
			if (numChildren(ntd) > 1) 
				throw new IllegalArgumentException
				("Cannot remove a root having more than one child."); 
			else if (numChildren(ntd) == 0)
				root = null; 
			else { 
				root = ntd.getChildren().get(0);    // the only child
				root.setSponsor(null);
			}
		else { 
			for (Node childNTD : ntd.children) { 
				parent.getChildren().add(childNTD);   
				childNTD.setSponsor(parent); 
			}	
		}
		
		parent.children.remove(ntd);

		size--; 
		return etr; 
	}
	
	// Creating a CLONE
	public PonziSchemeTree clone() throws CloneNotSupportedException { 
		PonziSchemeTree other = new PonziSchemeTree(); 
		if (!isEmpty())
			other.addRoot(root().getElement()); 
		cloneSubtree(root(), other, other.root()); 
		
		return other; 
	}
	
	private void cloneSubtree(Position rThis, PonziSchemeTree other,
			Position rOther) {
		for (Position pThis : children(rThis)) { 
			Position pOther = other.addChild(rOther, pThis.getElement());
			cloneSubtree(pThis, other, pOther); 
		}
	}
	public boolean isInternal(Position p) throws IllegalArgumentException {
		return this.numChildren(p) > 0;
	}

	public boolean isExternal(Position p) throws IllegalArgumentException {
		return this.numChildren(p) == 0;
	}

	public boolean isRoot(Position p) throws IllegalArgumentException {
		return this.sponsor(p) == null; 
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	
	@Override
	/**
	 * Returns Iterator object of elements in the tree,
	 * and based on inorder traversal. Notice that this is
	 * based on the Iterable<Position<E>> object produced by
	 * method positions()
	 */
	public Iterator iterator() {		
		ArrayList<Position> pList = new ArrayList<>(); 
		fillIterable(root(), pList); 
		
		ArrayList eList = new ArrayList<>(); 
		for (Position p : pList)
			eList.add(p.getElement()); 
		return eList.iterator();
	}

	/**
	 * Produces an iterable of positions in the tree based on
	 * postorder traversal. 
	 */
	public Iterable<Position> positions() {
		ArrayList<Position> pList = new ArrayList<Position>(); 
		fillIterable(root(), pList); 
		return pList;
	}
		
    /**
     * Method to fill the Iterable<Position<E>> object by properly traversing
     * the positions in the tree. Final version is decided at the particular 
     * type of tree - general, binary, etc.
     * 
     * The default method, as implemented here, generates an Iterable<Position<E>>
     * object based on PREORDER. 
     * @param r
     * @param pList
     */
	protected void fillIterable(Position r, ArrayList<Position> pList) {
		if (r != null) { 
			pList.add(r); 
			for (Position p : children(r))
				fillIterable(p, pList); 
		}	
	}
	
	private static class Node implements Position {

		private Member element; 
		private Node sponsor; 
		private Node mentor; 
		private ArrayList<Node> children; 
		private PonziSchemeTree ownerTree;  // the tree the node belongs to
		
		public Node(Member element, Node sponsor, Node mentor, PonziSchemeTree ownerTree) { 
			this.element = element; 
			this.sponsor = sponsor; 
			this.mentor = mentor; 
			this.children = null; 
			this.ownerTree = ownerTree;   
		}
		
		public Node(Member element) { 
			this.element = element; 
			this.sponsor = this; 
			this.children = null; 
		}
		
		public Node getSponsor() {
			return sponsor;
		}

		public void setSponsor(Node parent) {
			this.sponsor = parent;
		}
		
		/**
		 * @return the mentor
		 */
		public Node getMentor() {
			return mentor;
		}

		/**
		 * @param mentor the mentor to set
		 */
		public void setMentor(Node mentor) {
			this.mentor = mentor;
		}

		public ArrayList<Node> getChildren() {
			return children;
		}

		public void setChildren(ArrayList<Node> children) {
			this.children = children;
		}

		@Override
		public Member getElement() {
			return element;
		} 
		
		public void setElement(Member e) { 
			element = e; 
		}		
		
		public PonziSchemeTree getOwnerTree() { 
			return ownerTree; 
		}
		
		public void setOwnerTree(PonziSchemeTree t) { 
			ownerTree = t; 
		}


	}
	
	///////////////////////////////////////////////////////////////////////
	// The following are miscellaneous methods to display the content of //
	// the tree ....                                                     //
	///////////////////////////////////////////////////////////////////////
	public void display() {                                              //
		final int MAXHEIGHT = 100;                                       //
		int[] control = new int[MAXHEIGHT];                              //
		control[0]=0;                                                    //
		if (!this.isEmpty())                                             //
			recDisplay(this.root(), control, 0);                         //
		else                                                             //
			System.out.println("Tree is empty.");                        //
	}                                                                    //
                                                                         //
	// Auxiliary Method to support display                               //
	protected void recDisplay(Position root,                          //
			int[] control, int level)                                    //
	{                                                                    //
		printPrefix(level, control);                                     //
		System.out.println();                                            //
		printPrefix(level, control);                                     //
		System.out.println("__("+root.getElement()+")");                 //
		control[level]--;                                                //
		int nc = this.numChildren(root);                                 //
		control[level+1] = nc;                                           //
		for (Position  p : this.children(root)) {                     //
			recDisplay(p, control, level+1);                             //
		}                                                                //
	}                                                                    //
                                                                         //
	// Auxiliary method to support display                               //
	protected static void printPrefix(int level, int[] control) {        //
		for (int i=0; i<=level; i++)                                     //
			if (control[i] <= 0)                                         //
				System.out.print("    ");                                //
			else                                                         //
				System.out.print("   |");                                //
	}                                                                    //
    ///////////////////////////////////////////////////////////////////////
}
