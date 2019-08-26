import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlockPermutedSimhash {
	String folder;
	private List<char[]> table = new ArrayList<char[]>();
	Simhash simhash;
	HashMap<Integer, Integer> similar = new HashMap<Integer, Integer>();

	public BlockPermutedSimhash(String folder) {
		this.folder = folder;
		simhash = new Simhash(folder);
	}

	private void blockPermutation() {
		table = simhash.fingerprint();
		compareAdjacent(table);
		for (int i = 0; i < 31; i += 2) {
			for (int j = 0; j < table.size(); j++) {
				table.set(j, rotateLeft(table.get(j), 2));
			}
			compareAdjacent(table);
		}
	}

	private char[] rotateLeft(char[] t, int rotation) {
		if (rotation == 0)
			return t;
		char temp1, temp2;
		temp1 = t[0];
		temp2 = t[1];
		for (int i = 2; i < t.length; i++) {
			t[i - 2] = t[i];
		}
		t[t.length - 2] = temp1;
		t[t.length - 1] = temp2;
		return t;
	}

	private void compareAdjacent(List<char[]> table) {

		System.out.println("--------------------");

		List<Fingerprint> t = new ArrayList<Fingerprint>();
		for (int i = 0; i < table.size(); i++) {
			BigInteger b = new BigInteger(new String(table.get(i)), 2);
			t.add(new Fingerprint(i, b));
			// System.out.println(new String(table.get(i)) + " " + b);
		}
		Collections.sort(t, new Comparator<Fingerprint>() {
			public int compare(Fingerprint c1, Fingerprint c2) {
				return c1.hashValue.compareTo(c2.hashValue);
			}
		});
		for (int i = 0; i < t.size() - 1; i++) {
			int index1 = t.get(i).getIndex();
			int index2 = t.get(i + 1).getIndex();
			int distance = simhash.hammingDistance(table.get(index1), table.get(index2));
			if (distance < 7) {
				if (!((similar.containsKey(index1) && similar.get(index1).equals(index2)
						|| (similar.containsKey(index2) && similar.get(index2).equals(index1))))) {
					similar.put(index1, index2);
					System.out.println(simhash.filenames[index1] + " " + simhash.filenames[index2] + " " + distance);
				}
			}
		}
		System.out.println(similar.size());
	}

	private List<String> createPermutation(int G) {
		List<String> blocks = new ArrayList<String>();
		for (int i = 1; i <= G; i++) {
			for (int j = i + 1; j <= G; j++) {
				for (int k = j + 1; k <= G; k++) {
					blocks.add(i + "" + j + "" + k);
				}
			}
		}
		return blocks;
	}

	public static void main(String[] args) {
		long startTime = 0, stopTime = 0;
		startTime = System.nanoTime();
		double time = 0;
		BlockPermutedSimhash blockPermutedSimhash = new BlockPermutedSimhash(
				"/Users/sumon/IOWA STATE UNIVERSITY/Fall 17/CPR E 528/Project/Dataset/space");
		blockPermutedSimhash.blockPermutation();
		stopTime = System.nanoTime();
		time = (double) (stopTime - startTime) / 1000000000.0;
		System.out.println("Time: " + time+ " sec.");
	}

}

class Fingerprint {
	int index;
	BigInteger hashValue;

	public Fingerprint(int index, BigInteger hashValue) {
		this.index = index;
		this.hashValue = hashValue;
	}

	public int getIndex() {
		return index;
	}

	public BigInteger getHashValue() {
		return hashValue;
	}
}
