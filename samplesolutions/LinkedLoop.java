import java.util.Iterator;

public class LinkedLoop<E> implements Loop<E> {
	private DblListnode<E> current;
	private int size;
	
	public LinkedLoop() {
		current = null;
		size = 0;
	}
	
	public E getCurrent() {
		if (current == null)
			throw new EmptyLoopException();		
		return current.getData();
	}

	public void insert(E item) {
		
		DblListnode<E> node = new DblListnode<E>(item);
		if (current == null) {
			node.setPrev(node);
			node.setNext(node);
		}
		else {
			DblListnode<E> prev = current.getPrev();
			node.setPrev(prev);		
			prev.setNext(node);
			node.setNext(current);		
			current.setPrev(node);
		}

		++size;
		current = node;
	}

	public boolean isEmpty() {
		return current == null;
	}

	public Iterator<E> iterator() {
		return new LinkedLoopIterator<E>(current);
	}

	public void forward() {
		if (current == null)
			throw new EmptyLoopException();
		current = current.getNext();
	}

	public void backward() {
		if (current == null)
			throw new EmptyLoopException();
		current = current.getPrev();
	}

	public E removeCurrent() {
		if (current == null)
			throw new EmptyLoopException();
		
		E item = current.getData();
		if (size == 1) {
			current = null;
		} 
		
		else {
			DblListnode<E> prev = current.getPrev();
			DblListnode<E> next = current.getNext();
			prev.setNext(next);
			next.setPrev(prev);
			current = next;
		}
		
		--size;
		return item;
	}

	public int size() {
		return size;
	}
	
}