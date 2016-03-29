import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class neuronHandler {
	
	private static ArrayList<neuron> neurons;
	private static ArrayList<String> strWordsFired;
	private static ArrayList<Integer> intFreeIdxs;

	public static synchronized ArrayList<neuron> neurons() {
		return neurons;
	}
	
	public static synchronized ArrayList<String> strWordsFired() {
		return strWordsFired;
	}
	
	public static synchronized ArrayList<Integer> intFreeIdxs() {
		return intFreeIdxs;
	}
	
	//loads neural net and related variables
	public static void ini() {
		
		String strLoc = "";
		neurons = new ArrayList<neuron>();
		intFreeIdxs = new ArrayList<Integer>();
		strWordsFired = new ArrayList<String>();
		
		strLoc = ClassLoader.getSystemClassLoader().getResource(".").getPath().toString();
		strLoc = strLoc.substring(0, strLoc.lastIndexOf("/") - 3) + "Cranium/";
		
		File fleNrns = new File(strLoc + "neuralNet.txt");
		File fleFreeIdxs = new File(strLoc + "nrnFreeIdxs.txt");
		
		Scanner scn;
		String strToken = "";
		
		while (true) { // in case file not exists, call save and try again
			try {
				scn = new Scanner(fleNrns);
				while (scn.hasNext()) { //each neuron
					
					neuron nrn = new neuron();
					scn.next(); //id
					
					scn.next(); // for _
					do {
						strToken = scn.next();
						
						if (!strToken.equals("|||")) { //marks end of requirements
							nrn.strRequirements.add(strToken);
						} else {
							break;
						}
					} while (true);
					
					scn.next(); // for _
					do {
						strToken = scn.next();
						
						if (!strToken.equals("|||")) { //marks end of NeuronsIPointTo
							nrn.strNeuronsIPointTo.add(strToken);
						} else {
							break;
						}
					} while (true);

					nrn.blnIsFunction = "true".equals(scn.next());
					
					neurons.add(nrn);
				}
				
				scn.close();
				
				scn = new Scanner(fleFreeIdxs);
				while (scn.hasNext()) {
					intFreeIdxs.add(scn.nextInt());
				}
				
				scn.close();
				break;
				
			} catch (FileNotFoundException e1) {
				sav(); //creates file if not exists
			}
		}
	}
	
	public static int addNrn(neuron nrn) {
		int intIdx = -1;
		
		neuron newNrn = new neuron();
		newNrn = nrn;
		if (intFreeIdxs.isEmpty()) {
			neurons.add(newNrn);
			intIdx =  neurons.size() -1;
			
		} else {
			neurons.set(intFreeIdxs.get(0), newNrn);
			intIdx = intFreeIdxs.get(0);
			intFreeIdxs.remove(0);
		}
		return intIdx;
	}
	
	public static void rmvNrn(int intIdx) {
		intFreeIdxs.add(intIdx);
		neurons.set(intIdx, new neuron());
	}
	
	public static neuron getNrn(String strId) {
		
		if (toNum(strId) < neurons.size()) {
			return neurons.get(toNum(strId));
		} else {
			return null;
		}
	}
	
	public static ArrayList<String> getCommons(String strWordOne, String strWordTwo) {
		
		ArrayList<String> strNrnsOfOne = new ArrayList<String>();
		strNrnsOfOne = getNrn(strWordOne).strNeuronsIPointTo;
		
		ArrayList<String> strNrnsOfTwo = new ArrayList<String>();
		strNrnsOfTwo = getNrn(strWordTwo).strNeuronsIPointTo;
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for (int i = 0; i < strNrnsOfOne.size(); i ++){
			
			if (strNrnsOfTwo.contains(strNrnsOfOne.get(i))) {
				ret.add(strNrnsOfOne.get(i));
			}
		}
		
		return ret;
	}
	
	public static short fire(String strNrn, String strFrom) {
		
		log.println("fire neuron (" + strNrn + ") called by (" + strFrom + ")");
		
		actNeuron nrnTemp;
		
		if (Character.valueOf(strNrn.charAt(0)) > 47 && Character.valueOf(strNrn.charAt(0)) < 58) { //is word
			strWordsFired.add(memoryHandler.memoryNames.get(Integer.valueOf(strNrn)));
			
		} else { //is nrn
			
			if (toNum(strNrn) > neurons.size()) {
				return -1;
				
			} else if (intFreeIdxs.contains(toNum(strNrn))) {
				return -2;
			}
			
			if (!neuralNet.actNeuronIDs().contains(strNrn)) { //if fired before, += fire tally (amnt poked)

				nrnTemp = new actNeuron(neurons.get(toNum(strNrn)));
				nrnTemp.strRequirements.remove(Integer.toString(memoryHandler.memoryNames.indexOf(strFrom)));
				
				log.println("  new neuron (" + strNrn + ") - Requirments: " + nrnTemp.strRequirements.toString());
				
				neuralNet.actNeuronIDs().add(strNrn);
				neuralNet.actNeurons().add(nrnTemp);
				
			} else { //not fired before
				int i = neuralNet.actNeuronIDs().indexOf(strNrn);
				neuralNet.actNeurons().get(i).strRequirements.remove(Integer.toString(memoryHandler.memoryNames.indexOf(strFrom)));
				neuralNet.actNeurons().get(i).intAmntPoked ++;
				
				log.println("  neuron (" + strNrn + ") - Requirments: " + neuralNet.actNeurons().get(i).strRequirements.toString() + 
						" - removing: " + Integer.toString(memoryHandler.memoryNames.indexOf(strFrom)));
			}
		}
		return 0;
	}
	
	//aa = 17, a` = 16, o = 15
	public static int toNum(String str) {
		
		int ret = 0;
		char ch;
		str = str.replace("-", "");
		str = str.trim();
		int l = str.length(), i = 0;
		
		while (i < l) {
			ch = str.charAt(i);
			ret = (int) (ret + (ch-96)*Math.pow(16, l - i - 1));
			i++;
		}

		return ret;
	}
	
	//not actually hex, its just to letters ex a = 1, ` = 0
		public static String toHex(int intIn) {
			String ret = "";
			char ch;
			
			if (intIn == 0) {
				return "`";
			}
			
			int i = 4;
			do {
				ch = (char) (intIn/Math.pow(16, i) + 96);
				ret = ret.concat(Character.toString(ch));
				intIn = (int) (intIn % Math.pow(16, i));
				
				i--;
			} while (i >= 0);
			
			//removes preceding `s
			while (ret.startsWith("`")) {
				ret = ret.substring(1);
			}
			return ret;
		}
	
	public static void sav() {
		String strLoc = "";
		
		strLoc = ClassLoader.getSystemClassLoader().getResource(".").getPath().toString();
		strLoc = strLoc.substring(0, strLoc.lastIndexOf("/") - 3) + "Cranium/";
		
		File fleNrns = new File(strLoc + "neuralNet.txt");
		File fleFreeIdxs = new File(strLoc + "nrnFreeIdxs.txt");
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fleNrns)));
			
			neuron nrnTemp = new neuron();
			String strReqs = "", strNrnsIPT;
			
			for (int i = 0; i < neurons.size(); i++) {
				nrnTemp = neurons.get(i);
				
				strReqs = nrnTemp.strRequirements.toString();
				strReqs = strReqs.replace(",", ""); //removes commas from .toString of arraylist
				strReqs = strReqs.substring(1, strReqs.length() -1); //removes the brackets
				
				strNrnsIPT = nrnTemp.strNeuronsIPointTo.toString();
				strNrnsIPT = strNrnsIPT.replace(",", ""); //removes commas from .toString of arraylist
				strNrnsIPT = strNrnsIPT.substring(1, strNrnsIPT.length() -1); //removes the brackets
				
				String str = Boolean.toString(nrnTemp.blnIsFunction);
				toNum(str);

				writer.println(toHex(i) + " _ " + strReqs + " ||| _ " + strNrnsIPT + " ||| "
				+ Boolean.toString(nrnTemp.blnIsFunction));
				writer.flush();
			}
			
			writer.close();
			
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fleFreeIdxs)));
			
			for (int i = 0; i < intFreeIdxs.size(); i++) {
				writer.print(intFreeIdxs.get(i) + " ");
			}
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}