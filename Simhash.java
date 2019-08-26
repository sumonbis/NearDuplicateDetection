import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Simhash {
	private static final int HASHLENGTH = 64;
	private final String FOLDER;
	private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	public String[] filenames;
	HashMap<String, List<Word>> vectors = new HashMap<String, List<Word>>();

	public Simhash(String folder) {
		this.FOLDER = folder;
		createDictionary();
	}

	private void createDictionary() {
		filenames = allDocs();
		int count = 0;
		System.out.print("Loading data..");
		for (int i = 0; i < filenames.length; i++) {
			if (i % 100 == 0)
				System.out.println(".");
			vectors.put(filenames[i], new ArrayList<Word>());
			HashMap<String, Integer> words = getAllWords(filenames[i]); 
			for (String w : words.keySet()) {
				if (!dictionary.containsKey(w)) {
					dictionary.put(w, count);
					count++;
				}
				vectors.get(filenames[i]).add(new Word(dictionary.get(w), words.get(w)));
			}
		}
		// System.out.println(dictionary);
	}

	public String[] allDocs() {
		File[] files = getAllFiles(FOLDER);
		List<String> results = new ArrayList<String>();
		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
			}
		}
		String[] fileNameArray = new String[results.size()];
		fileNameArray = results.toArray(fileNameArray);
		if (fileNameArray.length < 1) {
			System.out.println("Error: No file in the specified directory.");
			System.exit(0);
		}
		return fileNameArray;
	}

	// Get all the files from the given directory path
	private File[] getAllFiles(String path) {
		File[] allFiles = new File(path).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		return allFiles;
	}

	// Returns array of processed words in lowercase from the given file
	private HashMap<String, Integer> getAllWords(String file) {
		Scanner s;
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		
		try {
			s = new Scanner(new File(FOLDER + "/" + file), "ISO-8859-1");
			while (s.hasNext()) {
				
				String w = s.next().replaceAll("[.,:;’']", "").toLowerCase();
				if (w.length() < 3 || w.equals("the"))
					continue;
				if (!words.containsKey(w)) {
					words.put(w, 1);
				} else {
					words.put(w, words.get(w) + 1);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return words;
	}

	public double exactJaccard(String file1, String file2) {
		double exactJaccard;
		//System.out.println(file1 + " " + file2);
		List<Integer> wordsFile1 = new ArrayList<Integer>();
		List<Integer> wordsFile2 = new ArrayList<Integer>();
		List<Word> words1 = vectors.get(file1);
		List<Word> words2 = vectors.get(file2);
		//System.out.println("he " + words1);
		for (Word w : words1) {
			wordsFile1.add(w.getWord());
			//System.out.print("hi " + w.getWord() + " ");
		}
		for (Word w : words2) {
			wordsFile2.add(w.getWord());
		}
		Set<Integer> unionSet = new HashSet<Integer>(); // remove duplicates
		unionSet.addAll(wordsFile1);
		unionSet.addAll(wordsFile2);
		exactJaccard = (double) (wordsFile1.size() + wordsFile2.size() - unionSet.size()) / unionSet.size();
		return exactJaccard;
	}

	public double exactCosine(String file1, String file2) {
		double exactCosine;
		List<Integer> wordsFile1 = new ArrayList<Integer>();
		List<Integer> wordsFile2 = new ArrayList<Integer>();
		List<Word> words1 = vectors.get(file1);
		List<Word> words2 = vectors.get(file2);
		for (Word w : words1) {
			wordsFile1.add(w.getWord());
		}
		for (Word w : words2) {
			wordsFile2.add(w.getWord());
		}
		Set<Integer> unionSet = new HashSet<Integer>(); // remove duplicates
		unionSet.addAll(wordsFile1);
		unionSet.addAll(wordsFile2);
		exactCosine = (double) (wordsFile1.size() + wordsFile2.size() - unionSet.size())
				/ Math.sqrt(wordsFile1.size() * wordsFile2.size());
		return exactCosine;
	}

	public char[] simhashSignature(String fileName) {
		char[] sig = new char[HASHLENGTH];
		int[] temp = new int[HASHLENGTH];

		for (Word w : vectors.get(fileName)) {
			int wrd = w.getWord();
			int freq = w.getfrequency();
			long wordHash = MurmurHash.hash64(Integer.toString(wrd));
			char[] wh = String.format("%064d", new BigInteger(Long.toBinaryString(wordHash))).toCharArray();

			for (int i = 0; i < wh.length; i++) {
				if (wh[i] == '1')
					temp[i] += freq;
				else
					temp[i] -= freq;
			}
		}
		for (int i = 0; i < HASHLENGTH; i++) {
			if (temp[i] < 0)
				sig[i] = '0';
			else
				sig[i] = '1';
		}
		// System.out.println(sig.size);
		return sig;
	}

	// approximateCosine
	public double approximateCosine(String file1, String file2) {
		char[] sig1 = simhashSignature(file1);
		char[] sig2 = simhashSignature(file2);
		int distance = hammingDistance(sig1, sig2);
		double similarity = 1 - ((double) distance / HASHLENGTH);
		return similarity;
	}

	public int hammingDistance(char[] sig1, char[] sig2) {
		int count = 0;
		for (int i = 0; i < sig1.length; i++) {
			if (sig1[i] != sig2[i])
				count++;
		}
		return count;
	}
	
	public List<char[]> fingerprint() {
		//HashMap<String, char[]> table = new HashMap<String, char[]>();
		List<char[]> table = new ArrayList<char[]>();
		for (int i =0; i<filenames.length;i++) {
			char[] sig = simhashSignature(filenames[i]);
			table.add(sig); 
			//System.out.println(sig);
		}
		return table;
	}

	public int numTerms() {
		return dictionary.size();
	}

	public static void main(String[] args) {
		Simhash simhash = new Simhash("/Users/sumon/IOWA STATE UNIVERSITY/Fall 17/CPR E 528/Project/Dataset/Space");
		System.out.println(simhash.exactJaccard("space-771.txt", "space-753.txt"));
		System.out.println(simhash.exactCosine("space-771.txt", "space-753.txt"));
		System.out.println(simhash.approximateCosine("space-771.txt", "space-753.txt"));
		//simhash.fingerprint();       
	}
}

class Word {
	private int word, frequency;

	public Word(int word, int frequency) {
		this.word = word;
		this.frequency = frequency;
	}

	public int getWord() {
		return word;
	}

	public int getfrequency() {
		return frequency;
	}
}
