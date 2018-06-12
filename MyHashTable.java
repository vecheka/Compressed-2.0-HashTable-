import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * TCSS 342- HW 4
 */

/**
 * A class that implements Hash Table using Linear Probing.
 * @author Vecheka Chhourn
 * @version 1.0
 * @param <K> generic Key of the map
 * @param <V> generic Value of the map
 */
public class MyHashTable<K, V> {
	
	/** Default Map's Capacity.*/
	private static final int DEFAULT_CAPACITY = 32768;
	
	/** Map's capacity.*/
	private final int myCapacity;
	/** Map's size.*/
	private int size;
	/** List to store HashNode.*/
	private final List<HashNode<K, V>> myMap;
	/** List to store each linear probing count.*/
	private final List<Integer> myHistogram;
	/** Keys set of the map.*/
	private final Set<K> myKeySet;
	/** Value set of the map.*/
	private final Set<V> myValueSet;
	
	
	
	/**
	 * Constructor to initialize Hash Table with a size, 
	 * and list with empty elements.
	 * @param theCapacity size of the Hash Table
	 */
	public MyHashTable(final int theCapacity) {
		myCapacity = theCapacity;
		size = 0;
		myMap = new ArrayList<>(myCapacity);
		myKeySet = new HashSet<>();
		myValueSet = new HashSet<>();
		myHistogram = new ArrayList<>();
		myHistogram.add(0);
		for (int i = 0; i < theCapacity; i++) {
			myMap.add(new HashNode<K, V>());
		}
		
	}
	
	/**
	 * Copy constructor to initialize Hash Table to the default size.
	 */
	public MyHashTable() {
		this(DEFAULT_CAPACITY);
	}
	
	
	/**
	 * Put new key and value to the map.
	 * Pre-conditions: 
	 *  1. If the key already exists, update new value.
	 *  2. If the key is new, find a new index using Linear Probing to put in the map.
	 * @param searchKey key to put in the map
	 * @param newValue value to put in the map
	 */
	public void put(final K searchKey, final V newValue) {
		if (searchKey == null || newValue == null) throw new IllegalArgumentException();
		
		if (searchKey != null) myKeySet.add(searchKey);
		if (newValue != null) myValueSet.add(newValue);
		
		int index = hash(searchKey);
		HashNode<K, V> temp = myMap.get(index);
		
		// Linear prob till found a free index
		boolean isNewKey = false;
		int probCount = 0;
		while (temp.key != null && !temp.key.equals(searchKey)) {
			index = (index + 1) % myCapacity;
			temp = myMap.get(index);
			probCount++;
		}
		
		if (temp.key != null) {
			temp.value = newValue;
		} else {
			isNewKey = true;
			temp.key = searchKey;
			temp.value = newValue;
			size++;
		} 
		
		// update Histogram List
		if (isNewKey) {
			if (probCount >= myHistogram.size()) {
				final int size = myHistogram.size();
				for (int i = 0; i <= probCount - size; i++) {
					myHistogram.add(0);
				}
				myHistogram.set(probCount, 1);
			} else {
				myHistogram.set(probCount, myHistogram.get(probCount) + 1);
			}
		}
		
	}
	
	/**
	 * Get value corresponding the key
	 * @param searchKey key to search for the value
	 * @return value if found; else return null.
	 */
	public V get(final K searchKey) {
		if (searchKey == null) return null;
		int index = hash(searchKey);
		V value = null;
		
		HashNode<K, V> temp = myMap.get(index);
		
		while (temp.key != null && !searchKey.equals(temp.key)) {
			index = (index + 1) % myCapacity;
			temp = myMap.get(index);
		}
		if (searchKey.equals(temp.key)) {
			value = temp.value;
		}
		return value;
	}
	
	/**
	 * Determine if the key exists in the map.
	 * @param searchKey key to search for
	 * @return true if found
	 */
	public boolean containsKey(final K searchKey) {
		return myKeySet.contains(searchKey);
	}
	
	
	/**
	 * Displays statistics for the data in the Hash Table.
	 */
	public void stats() {
		final StringBuilder result = new StringBuilder();
		System.out.println("Hash Table Stats");
		System.out.println("================");
		System.out.println("Number of Entries: " + size());
		System.out.println("Number of Buckets: " + DEFAULT_CAPACITY);
		System.out.print("Histogram of Probes: ");
		result.append("[" + myHistogram.get(0));
		for (int i = 1; i < myHistogram.size(); i++) {
			result.append(", " + myHistogram.get(i));
		}
		result.append("]");
		System.out.print(result.toString() + "\n");
		System.out.println("Fill Percentage: " + String.format("%.2f", (size() * 100.0) / DEFAULT_CAPACITY) + "%");
		System.out.println("Max Linear Prob: " + myHistogram.size());
		
		double average = 0;
		for (int i = 0; i < myHistogram.size(); i++) {
			average += myHistogram.get(i) * i;
		}
		
		System.out.println("Average Linear Prob: " + String.format("%.2f", average / size()) + "\n");
	}
	
	/**
	 * Display map as a string representation.
	 * @return string representation of the map
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		HashNode<K, V> temp = myMap.get(0);
		result.append("[");
		if (temp.key != null) {
			result.append(temp.key + "=" + temp.value);
		}
		for (int i = 1; i < myMap.size(); i++) {
			temp = myMap.get(i);
			if (temp.key != null) {
				result.append(", " + temp.key + "=" + temp.value);
			}
		}
		result.append("]");
		return result.toString();
	}
	
	//helper method
	/**
	 * Get index of the key to put in the map.
	 * @param key to calculate for the index
	 * @return index of the key
	 */
	private int hash(final K key) {
		return Math.abs(key.hashCode() % myCapacity);
	}

	
	// added methods
	/**
	 * Get a set of keys in the map
	 * @return list of map's keys
	 */
	public Set<K> keySet() {
		return myKeySet;
	}
	
	// use for decoding the encoded message
	/**
	 * Determine if the value exists in the map.
	 * @param newValue value to search for
	 * @return true if found
	 */
	public boolean containsValue(final V newValue) {
		return myValueSet.contains(newValue);
	}
	
	/** 
	 * Get Map's size.
	 * @return size of the map
	 */
	public int size() {
		return size;
	}
	
	
}


/**
 * A class that creates Hash Table node containing Key, and Value.
 * @author Vecheka Chhourn
 * @version 1.0
 * @param <K> generic Key of the map
 * @param <V> generic Value of the map
 */
class HashNode<K, V> {
	
	/** Key of the map.*/
	protected K key;
	/** Value of the map. */
	protected V value;
	
	/**
	 * Constructor to initialize Key and Value of the map.
	 * @param theKey key to store in map
	 * @param theValue value to store in map corresponding to the Key
	 */
	public HashNode(final K theKey, final V theValue) {
		key = theKey;
		value = theValue;
	}
	
	/**
	 * Copy constructor to initialize key and value nodes to null in the map.
	 */
	public HashNode() {
		this(null, null);
	}
}