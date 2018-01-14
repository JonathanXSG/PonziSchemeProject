import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Jonathan
 */
public class PonziSchemeTree implements Iterable<Member>, Cloneable{
	private Node root; 
	private int size; 
	
	/**
	 * Default Constructor
	 */
	public PonziSchemeTree() { 
		root = null; 
		size = 0; 
	}
	
	/**
	 * Method to add a new node as child, the parent being a String variable inside the Member object
	 * @param member element to add
	 * @return Position of added element
	 */
	public Position addChildToParent(Member member){
		Position newPos = null;
		if(size==0) {
			newPos=addRoot(member);
			return newPos;
		}
		for(Position p: positions()) {
			if(p.getElement().getName().equals(member.getSponsor())) {
				if(numChildren(p)>0) {
					newPos = addChild(p, member);
					((Node) newPos).mentor = ((Node) p).children.get(numChildren(p)-2);
				}
				else {
					newPos = addChild(p, member);
					((Node) newPos).mentor = ((Node) p);
				}
				break;
			}
		}
		return newPos;
	}
	
	/**
	 * Verifies that the position is part of the tree
	 * @param pos Position to check
	 * @return Node object of the element in the tree
	 * @throws IllegalArgumentException if the position is invalid
	 */
	private Node validate(Position pos) throws IllegalArgumentException { 
		if (!(pos instanceof Node)) throw new IllegalArgumentException("Invalid position type for this implementation."); 
		Node node = (Node) pos; 
		if (node.sponsor == node)throw new IllegalArgumentException("Target position is not part of a tree.");
		if (node.ownerTree != this)
			throw new IllegalArgumentException("Target position is not part of the tree.");	
		return node; 
	}
	
	/**
	 * @return to Position of the tree that is the root
	 */
	public Position root() {
		return root;
	}

	/**
	 * @param p Position to get the Sponsor from
	 * @return Sponsor of Position specified
	 * @throws IllegalArgumentException if the Position is invalid
	 */
	public Position sponsor(Position pos) throws IllegalArgumentException {
		Node node = this.validate(pos); 
		return node.sponsor; 
	}
	/**
	 * @param p Position to get the Mentor from
	 * @return Mentor of Position specified
	 * @throws IllegalArgumentException if the Position is invalid
	 */
	public Position mentor(Position pos) throws IllegalArgumentException {
		Node node = this.validate(pos); 
		return node.mentor; 
	}

	/**
	 * @param p  Position from which to get the children from
	 * @return Iterable object that contains all the positions of children from the specified ZPosition
	 * @throws IllegalArgumentException
	 */
	public Iterable<Position> children(Position pos) throws IllegalArgumentException {
		Node node = this.validate(pos);
		ArrayList<Position> result = new ArrayList<Position>(); 
		if (node.children != null) 
			for(Position p : node.children)
				result.add(p); 
		return result; 
	}

	/**
	 * @param pos Position from which to get the number of children
	 * @return Integer number of children of the specified Position
	 * @throws IllegalArgumentException
	 */
	public int numChildren(Position pos) throws IllegalArgumentException {
		Node np = validate(pos);  
		if (np.children != null) return np.children.size();
		else return 0; 
	}

	/**
	 * @return amount of objects in the tree
	 */
	public int size() {
		return size;
	}

	/**
	 * Add the first element to the tree
	 * @param element the new element to be the root
	 * @return position of the new element
	 * @throws IllegalStateException if the position is not valid
	 */
	public Position addRoot(Member element) throws IllegalStateException { 
		if (this.root != null) throw new IllegalStateException("Tree must be empty to add a root."); 
		size++; 
		root = new Node(element, null, null, this); 
		return root; 
	}

	/**
	 * Add a new element as a child of a given position 
	 * @param pos the position to be the parent 
	 * @param element the new element to add as child
	 * @return the Position of the new element 
	 * @throws IllegalArgumentException if the position is not valid
	 */
	public Position addChild(Position pos, Member element) throws IllegalArgumentException { 
		Node parent = validate(pos);  
		Node newNode = new Node(element, parent, null, this); 
		if (parent.children == null) parent.children =(new ArrayList<Node>());
		size++; 
		parent.children.add(newNode); 
		return newNode; 
	}
	
	/**
	 * @param pos Position in the tree to be removed
	 * @return the element that was removed
	 * @throws IllegalArgumentException
	 */
	public Member remove(Position pos) throws IllegalArgumentException { 
		Node node = validate(pos); 
		Member member = node.getElement(); 
		Node parent = node.sponsor; 
		if (parent == null)    
			if (numChildren(node) > 1) throw new IllegalArgumentException ("Cannot remove a root having more than one child."); 
			else if (numChildren(node) == 0) root = null; 
			else { 
				root = node.children.get(0);
				root.sponsor =null;
			}
		else { 
			for (Node child : node.children) { 
				parent.children.add(child);   
				child.sponsor = parent; 
			}	
		}
		parent.children.remove(node);
		size--; 
		return member; 
	}
	
	
	/**
	 * @param pos Position to check if it's an internal node of the tree
	 * @return true if it is internal
	 * @throws IllegalArgumentException
	 */
	public boolean isInternal(Position pos) throws IllegalArgumentException {
		return this.numChildren(pos) > 0;
	}
	/**
	 * @param pos Position to check if it's an external node of the tree
	 * @return true if it is external
	 * @throws IllegalArgumentException
	 */
	public boolean isExternal(Position pos) throws IllegalArgumentException {
		return this.numChildren(pos) == 0;
	}
	/**
	 * @param pos Position to check if it's the root node of the tree
	 * @return true if it is the root
	 * @throws IllegalArgumentException
	 */
	public boolean isRoot(Position pos) throws IllegalArgumentException {
		return this.sponsor(pos) == null; 
	}

	/**
	 * @return true if the tree is empty
	 */
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	
	@Override
	/**
	 * Returns Iterator object of elements in the tree
	 */
	public Iterator iterator() {		
		ArrayList<Position> posList = new ArrayList<>(); 
		fillIterable(root(), posList); 
		
		ArrayList<Member> elemList = new ArrayList<>(); 
		for (Position p : posList)
			elemList.add(p.getElement()); 
		return elemList.iterator();
	}

	/**
	 * Produces an iterable of positions in the tree
	 */
	public Iterable<Position> positions() {
		ArrayList<Position> posList = new ArrayList<Position>(); 
		fillIterable(root(), posList); 
		return posList;
	}
		
    /**
     * Method to fill the Iterable<Position> object 
     * @param pos Position root of the tree
     * @param posList
     */
	protected void fillIterable(Position pos, ArrayList<Position> posList) {
		if (pos != null) { 
			posList.add(pos); 
			for (Position p : children(pos))
				fillIterable(p, posList); 
		}	
	}
	
	/**
	 * Node Class to store the element and it's connections in the tree
	 */
	private static class Node implements Position {
		private Member element; 
		private Node sponsor; 
		private Node mentor; 
		private ArrayList<Node> children; 
		private PonziSchemeTree ownerTree;  // the tree the node belongs to
		
		/**
		 * @param element Member to store in the node
		 * @param sponsor Sponsor reference
		 * @param mentor Mentor reference
		 * @param ownerTree Reference to the tree it is in
		 */
		public Node(Member element, Node sponsor, Node mentor, PonziSchemeTree ownerTree) { 
			this.element = element; 
			this.sponsor = sponsor; 
			this.mentor = mentor; 
			this.children = null; 
			this.ownerTree = ownerTree;   
		}
		
		/**
		 * @param element Member to store in the Node
		 */
		public Node(Member element) { 
			this.element = element; 
			this.sponsor = this; 
			this.children = null; 
		}

		@Override
		public Member getElement() {
			return element;
		} 
	}
}
