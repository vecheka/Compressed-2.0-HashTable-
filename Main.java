import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/*
 * TCSS 342 - HW4
 */

/**
 * Main class to execute the program.
 * @author Vecheka Chhourn
 * @version 1.0
 */
public class Main {

	/** Format milliseconds to seconds.*/
	private static final double SECOND = 1000.0;
	/** Format bytes to kilobytes.*/
	private static final double KILOBYTES = 1024.0;
	/** Delimiter .*/
	private static final String DELIMITER = "=";
	/** File to compress.*/
	private static final String TEXT_FILE = "WarAndPeace.txt";
	/** Test file to compress.*/
	private static final String TEST_FILE = "SenseAndSensibility.txt";
	/** Character codes file.*/
	private static final String CODE_FILE = "codes.txt";
	/** Compressed file.*/
	private static final String COMPRESSED = "compressed.txt";
	
	
	
	public static void main(final String[] theArgs) {
		//		new CodingTree("HICKORY DICKORY DOCK KNOCK KNOCK TICK TOCK CLOCK");

		long startTime = System.currentTimeMillis();

		// compressing the message
		encodeMessage();

		long endTime = System.currentTimeMillis();
		// in seconds
		System.out.println("Running Time for Compressing: " + (String.format("%.2f", 
				(endTime - startTime) / SECOND)) + 
				" seconds.");

		// display statistics after compression
		displayStatistics();


		startTime = System.currentTimeMillis();

		// uncompressing the message
//		decodeMessage();

		endTime = System.currentTimeMillis();
		// in seconds
		System.out.println("\nRunning Time for Uncompressing: " + (String.format("%.2f", 
				(endTime - startTime) / SECOND)) + 
				" seconds.");



		// testing coding tree class, and hash table
		//testCodingTree();
//		testMyHashTable();
	}
	
	/**
	 * Test MyHashTable class.
	 */
	private static void testMyHashTable() {
		MyHashTable<String, Integer> testMap = new MyHashTable<>(10);
		testMap.put("Vecheka", 10);
		testMap.put("Hello", 98);
		testMap.put("You", 1);
		testMap.put("Me", 5);
		testMap.put("Vecheka", 20);
		testMap.put("Yes", 3);
		System.out.println(testMap);
		System.out.println(testMap.get("Null"));
		System.out.println(testMap.containsKey("Vecheka"));
		
	}

	/**
	 * Testing CodingTree class.
	 */
	private static void testCodingTree() {
		final CodingTree test = new CodingTree("HICKORY DICKORY DOCK KNOCK KNOCK TICK TOCK");
		System.out.println("Encoded Message: " + test.toString());
		
	}
	
	
	/**
	 * Display statistics of file compression by comparing its original file size
	 * and the compressed file size.
	 */
	private static void displayStatistics() {
		final File original_file = new File(TEXT_FILE);
		final File compressed_file = new File(COMPRESSED);
		
		final double original_size = original_file.length() / KILOBYTES;
		final double compressed_size = compressed_file.length() / KILOBYTES;
		
		System.out.println("Original size of the file: " + String.format("%.2f", original_size) + "kbs");
		System.out.println("Compressed size of the file: " + String.format("%.2f", compressed_size) + "kbs");
		
		final double compressed_ratio = (compressed_size * 100.00) / original_size;
		
		System.out.println(String.format("Compression Ratio: %.2f", compressed_ratio) + "%");
		
		
	}
	
	
	/**
	 * Encode message read from a file, and compress it.
	 */
	private static void encodeMessage() {
		final Scanner reader;
		final StringBuilder text = new StringBuilder();

		try {
			// reading in text's contexts
			reader = new Scanner(new File(TEXT_FILE));
			while (reader.hasNextLine()) {
				text.append(reader.nextLine() + "\n");
				
			}
			reader.close();
		} catch (final FileNotFoundException theE) {
			theE.printStackTrace();
		}
		
		// encode message to a compressed file
		new CodingTree(text.toString());
		
	}
	
	/**
	 * Decode message back to its original context.
	 */
	private static void decodeMessage() {
		
		
		final FileInputStream fileReader;
		final int num_bits = 8; // number of bits representation
		final CodingTree coder = new CodingTree();
		String extraBits = "";
		final StringBuilder bits = new StringBuilder();
		
		
		// reading in compressed file, and convert bytes to binary codes.
		try {
			final File file = new File(COMPRESSED);
			fileReader = new FileInputStream(file);
			final byte temp[] = new byte[(int)file.length()];
			
			try {
				fileReader.read(temp);
				
				// check if there were extra bits
				int size = temp.length;
				if (temp[temp.length - 2] == 0) {
					extraBits = Integer.toBinaryString(temp[temp.length - 1]);
					size = temp.length - 2;
				}
				for (int i = 0; i < size; i++) {
					if (temp[i] < 0) {
						final String binary = Integer.toBinaryString(temp[i]);
						// obtain the last 8 bits from a 32 bits binary
						bits.append(binary.substring(binary.length() - num_bits));
					} else {
						String binary = Integer.toBinaryString(temp[i]);
						while (binary.length() < num_bits) {
							binary = "0" + binary;
						}
						bits.append(binary);
					}
					
				}
				fileReader.close();
			} catch (final IOException theE) {
				theE.printStackTrace();
			}
			
		} catch (final FileNotFoundException theE1) {
				theE1.printStackTrace();
		}
		
		// read in character codes
		final Scanner reader;
		final MyHashTable<String, String> codes = new MyHashTable<>();
		try {

			// read characters' codes from a file
			reader = new Scanner(new File(CODE_FILE));
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				if (line.isEmpty()) {
					line = reader.nextLine();
					codes.put("\n", line.substring(line.indexOf(DELIMITER) + 1));
				}else if (line.charAt(0) == DELIMITER.charAt(0)) {
					codes.put(line, line.substring(line.lastIndexOf(DELIMITER) + 1));
				} else {
					final String[] temp = line.split(DELIMITER);
					codes.put(temp[0], temp[1]);
					
				}
			}
			reader.close();
			
		} catch (final FileNotFoundException theE) {
			theE.printStackTrace();
		}
		
		
		// finding the extra bit codes in the character codes' map
		int size = 0;
		String tempBit = extraBits;
		while (size < codes.size()) {
			if (!codes.containsValue(tempBit)) {
				tempBit = "0" + tempBit;
				size++;
			} else {
				extraBits = tempBit;
				size = codes.size();
			}
			
		}
	
		bits.append(extraBits);
		coder.decode(bits.toString(), codes);

	}

}
