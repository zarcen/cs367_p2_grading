import java.util.*;

public class LinkedLoopIterator<E> implements Iterator<E> {
	private DblListnode<E> curr, first;
	private boolean seenFirst;
	
	LinkedLoopIterator(DblListnode<E> head) {
		curr = head;
		first = head;
		seenFirst = false;
	}
	
	public boolean hasNext() {
//		if (first == null) return false;
//		else if (seenFirst && curr == first) return false;
//		else if (!seenFirst) return true;
//		else return false;
//		
//		return !(first == null || (seenFirst && curr == first));
		
		return (first != null) && (!seenFirst || curr != first);
	}

	public E next() {
		if (!hasNext())
			throw new NoSuchElementException();
		if (!seenFirst) 
			seenFirst = true;
		E item = curr.getData();
		curr = curr.getNext();
		return item;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}