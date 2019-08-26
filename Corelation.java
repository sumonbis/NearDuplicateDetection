import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Corelation {
	private String folder;
	private double error;
	private List<Double> exactCosine = new ArrayList<Double>();
	private List<Double> approximateCosine = new ArrayList<Double>();
	private int count = 0;
	double time = 0;
	

	public Corelation(String folder, double error) {
		this.folder = folder;
		this.error = error;
	}
	
	private void Accuracy() {
		long startTime = 0, stopTime = 0;
		startTime = System.nanoTime();
		
		Simhash simhash = new Simhash(folder);
		for (int i = 0; i < simhash.filenames.length; i++) {
			for (int j = i + 1; j < simhash.filenames.length; j++) {
				double exact = simhash.exactCosine(simhash.filenames[i], simhash.filenames[j]);
//				if (exact < 0.85)
//					continue;
				double approx = simhash.approximateCosine(simhash.filenames[i], simhash.filenames[j]);
				exactCosine.add(exact);
				approximateCosine.add(approx);
				System.out.println(simhash.filenames[i] + " " + simhash.filenames[j] + " " + exact + " " + approx);
				if (approx < 0.8 && exact > 0.90)
					count++;
//				if (Math.abs(exact - approx) > error)
//					count++;
			}
		}
		stopTime = System.nanoTime();
		time = (double) (stopTime - startTime) / 1000000000.0;
		System.out.println("Time: " + time+ " sec.");
	}

	public static void main(String[] args) {
		Corelation corelation = new Corelation("/Users/sumon/IOWA STATE UNIVERSITY/Fall 17/CPR E 528/Project/Dataset/space", 0.1);
		corelation.Accuracy();
		//System.out.println(corelation.exactCosine);
		//System.out.println(corelation.approximateCosine);
		System.out.println(corelation.count);
		
		PrintWriter printer;
		try {
			printer = new PrintWriter("corelation-F17PA2-85.csv", "UTF-8");
			for (int i = 0; i < corelation.exactCosine.size(); i++) {
				printer.println(corelation.exactCosine.get(i) + ", " + corelation.approximateCosine.get(i));
			}
			printer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
