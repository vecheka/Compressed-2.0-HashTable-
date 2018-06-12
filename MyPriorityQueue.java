import java.util.ArrayList;
import java.util.List;

/*
 * TCSS 342 - HW 3 
 */

/**
 * @author Vecheka Chhourn
 * @version 1.0
 * @param T generic type
 * A class that implements a Priority Queue using List Data Structure with the smallest 
 * item as a priority.
 */
public class MyPriorityQueue<T extends Comparable<T>> {
	
	/** Queue list.*/
	private final List<T> myQueue;
	
	
	/**
	 * Constructor that initializes an empty queue (list).
	 */
	public MyPriorityQueue() {
		myQueue = new ArrayList<>();
	}

	
	/**
	 * To add the items to the queue.
	 * @param theData item to add.
	 * @return true 
	 */
	public boolean offer(final T theData) {
		if (isEmpty()) {
			myQueue.add(theData);
		} else {
			// compare items before add
			// smallest item always comes first
			// add to the end of the list if it's the biggest item
			boolean isBiggest = true;
			for (int i = 0; i < size(); i++) {
				if (theData.compareTo(myQueue.get(i)) == -1 
						|| theData.compareTo(myQueue.get(i)) == 0) {
					myQueue.add(i, theData);
					isBiggest = false;
					break;
				}
			}
			if (isBiggest) {
				myQueue.add(theData);
			}
		}
		
		return true;
	}
	
	/**
	 * To retrieve the top item in the queue, but not removing.
	 * @return the top item in the queue
	 */
	public T peek() {
	    T  item = null;
		if (!isEmpty()) {
			item = myQueue.get(0);
		}
		return item;
	}
	
	/**
	 * To retrieve and remove the element on the top of the queue.
	 * @return the top item in the queue
	 */
	public T poll() {
		T  item = null;
		if (!isEmpty()) {
			item = myQueue.remove(0);
		}
		return item;
	}
	
	
	/** 
	 * Check if the queue is empty or not.
	 * @return true if it is empty
	 */
	public boolean isEmpty() {
		return myQueue.isEmpty();
	}
	
	/**
	 * Check if the queue has a specified element or not.
	 * @param theData element to check for in the queue
	 * @return true if the queue has the specified element
	 */
	public boolean contains(final T theData) {
		return myQueue.contains(theData);
	}
	
	
	/**
	 * Get the size of the queue.
	 * @return size of the queue
	 */
	public int size() {
		return myQueue.size();
	}
	
	
	@Override
	public String toString() {
		return myQueue.toString();
	}
}
