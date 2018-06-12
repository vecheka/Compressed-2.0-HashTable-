import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/*
 * TCSS 342 - HW 3 
 */

/**
 * @author Vecheka Chhourn
 * @version 1.0
 * A class that handles the Huffman's Encoding and Decoding of a text file, and 
 * writing it to a compressed and uncompressed files.
 * ********************************Note******************************** :
 * I have included a decoding method for the program.
 */
public class CodingTree {
	
	/** Character codes file.*/
	private static final String CODE_FILE = "codes.txt";
	/** Compressed file.*/
	private static final String COMPRESSED = "compressed.txt";
	/** Uncompressed file.*/
	private static final String UNCOMPRESSED = "uncompressed.txt";
	
	/** Map to store characters and their codes.*/
	private MyHashTable<String, String> codes;
	/** Encoded message in binary.*/
	private String bits;
	/** Map to store characters and their frequency.*/
	private MyHashTable<String, Integer> myWordCount;
	/** List to store all words in the text.*/
	private List<String> myWordsList;
	
	
	/**
	 * Constructor that takes the message to be encoded.
	 * @param message to be encoded
	 */
	public CodingTree(final String message) {
		codes = new MyHashTable<>();
		myWordCount = new MyHashTable<>();
		myWordsList = new ArrayList<>();
		bits = "";
		countWordsFrequency(message);
		buildHuffmanTree();
		encodeMessage(message);
		writeCharacterCodesToFile();
	}
	
	/**
	 * Empty Constructor.
	 */
	public CodingTree(){}
	
	
	/**
	 * Write characters' codes to a file.
	 */
	private void writeCharacterCodesToFile() {
		final PrintStream output;
		try {
			output = new PrintStream(new File(CODE_FILE));
			final StringBuilder temp = new StringBuilder();
			for (final String word: codes.keySet()) {
				temp.append(word + "=" + codes.get(word) + "\n");
			}
			output.print(temp.toString());
			output.close();
			
		} catch (final FileNotFoundException theE) {
			theE.printStackTrace();
		}
	
	}

	/**
	 * Encode the message to binary codes, write as bytes to a file.
	 * @param theMessage to be encoded
	 */
	private void encodeMessage(final String theMessage) {
		
		final StringBuilder binaryCodes = new StringBuilder();
		final int num_bits = 8;	
		final int radix = 2;
		final FileOutputStream output;
		try {
			output = new FileOutputStream(new File(COMPRESSED));
			
			for (final String word: myWordsList) {
				binaryCodes.append(codes.get(word));
			}
			
			final byte[] b = new byte[(binaryCodes.length() / num_bits)];
			
			for (int i = 0; i < b.length; i++) {
				b[i] = (byte) Integer.parseUnsignedInt(binaryCodes.substring(i * num_bits, (i * num_bits) + num_bits), radix);	
				
			}
			
			// write the remaining bits to the file
			final byte[] extraByte = new byte[2];
			if (binaryCodes.length() % num_bits != 0) {
				// index 0 informs that there is a remaining bits
				extraByte[0] = (byte) Integer.parseUnsignedInt("0", radix);
				extraByte[1] = (byte) Integer.parseUnsignedInt(binaryCodes.substring(binaryCodes.length() - binaryCodes.length() % num_bits), radix);
			}
			
			
			// write encoded message to the file
			try {
				output.write(b);
				output.write(extraByte);
				output.flush();
				output.close();
			} catch (final IOException theE) {
				theE.printStackTrace();
			}
		} catch (final FileNotFoundException theE1) {
			
			theE1.printStackTrace();
		}
		
		// not necessary
		bits = binaryCodes.toString();
		
		
	}


	/**
	 * Count each words frequency.
	 * @param theMessage to be read.
	 */
	private void countWordsFrequency(final String theMessage) {
		
		StringBuilder word = new StringBuilder();
		for (int i = 0; i < theMessage.length(); i++) {
			final String temp = theMessage.charAt(i) + "";
			if (isCharacter(temp)) {
				word.append(temp);
			} else {
				if (!word.toString().isEmpty()) {
					addWordsCountToMap(word.toString());
					word = new StringBuilder();
				}
				// add separators to the map
				addWordsCountToMap(temp);
			}
			
		}
		// add the last word in the text
		if (!word.toString().isEmpty()) {
			addWordsCountToMap(word.toString());
		}
		

		myWordCount.stats();
	}
	
	
	/**
	 * Build Huffman's Tree from Words frequency, and encode
	 * each words to binary codes.
	 */
	private void buildHuffmanTree() {
		// build trees and add each characters and their frequency to the queue
		final PriorityQueue<HuffmanTree> queue = new PriorityQueue<>();

		for (final String word: myWordCount.keySet()) {
			final HuffmanTree tree = new HuffmanTree();
			tree.buildTree(myWordCount.get(word), word);
			queue.offer(tree);
		}
	
		
		// combine each minimum trees till one tree is remained
		while (queue.size() > 1) {
			queue.offer(queue.poll().combineTrees(queue.poll()));
		}
		
		// encode the characters to binary codes
		final HuffmanTree finalTree = queue.peek();
		finalTree.encode();
		codes = finalTree.getWordCodesMap();
	
	}
	
	
	// added method
	/**
	 * Decode binary message to the original message.
	 * @param bits binary message
	 * @param codes each characters' codes in binary form
	 * @return the original message
	 */
	public void decode(final String bits, final MyHashTable<String, String> codes) {
		final StringBuilder originalMessage = new StringBuilder();
		// put map in reverse order string->character
		// intention: better run time
		final MyHashTable<String, String> reverseCodes = new MyHashTable<>();
		for (final String word: codes.keySet()) {
			reverseCodes.put(codes.get(word), word);
		}
		
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < bits.length(); i++) {
			temp.append(bits.charAt(i));
			if (reverseCodes.containsKey(temp.toString())) {
				originalMessage.append(reverseCodes.get(temp.toString()));
				temp = new StringBuilder();
			}
			
		}
		
		final PrintStream output;
		try {
			output = new PrintStream(new File(UNCOMPRESSED));
			output.print(originalMessage.toString());
			output.close();
		} catch (final FileNotFoundException theE) {
			theE.printStackTrace();
		}
	}
	
	
	/** 
	 * Display the encoded message.
	 * @return encoded message in binary form.
	 */
	@Override
	public String toString() {
		return bits;
	}
	
	// helper methods
	/** 
	 * Add words and their counts to map.
	 * @param theWord word to add to map
	 */
	private void addWordsCountToMap(final String theWord) {
		if (myWordCount.containsKey(theWord)) {
			myWordCount.put(theWord, myWordCount.get(theWord) + 1);
		} else {
			myWordCount.put(theWord, 1);
		}
		myWordsList.add(theWord);
	
	}

	/**
	 * Determine if it is a character, and not a separator.
	 * @param theString string to determine
	 * @return true if it is not separator.
	 */
	private boolean isCharacter(final String theString) {
//		return theString.matches("[0-9a-zA-Z'-]");
		return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'-".contains(theString);
	}
}
