import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class memoryHandler {
	
	public static ArrayList<memory> memories;
	public static ArrayList<String> memoryNames;
	private static ArrayList<Integer> intFreeIdxs;
	
	public static void ini() {
		String strLoc = "";
		memories = new ArrayList<memory>();
		memoryNames = new ArrayList<String>();
		intFreeIdxs = new ArrayList<Integer>();
		
		strLoc = ClassLoader.getSystemClassLoader().getResource(".").getPath().toString();
		strLoc = strLoc.substring(0, strLoc.lastIndexOf("/") - 3) + "Cranium/";
		
		File fleMems = new File(strLoc + "memories.txt");
		File fleFreeIdxs = new File(strLoc + "memFreeIdxs.txt");
		Scanner scn;
		String strToken = "";
		
		while (true) { // in case file does not exist, try again
			try {
				scn = new Scanner(fleMems);
				while (scn.hasNext()) { //each memory
					
					memory mem = new memory();
					memoryNames.add(scn.next());
					mem.strNeuronRepresentingMe = scn.next();
					
					scn.next(); // for _
					do {
						strToken = scn.next();
						
						if (!strToken.equals("|||")) { //marks end
							mem.strRelatedNeurons.add(strToken);
							
						} else {
							break;
						}
					} while (true);
					memories.add(mem);
				}
				
				scn.close();
				
				scn = new Scanner(fleFreeIdxs);
				while (scn.hasNext()) {
					intFreeIdxs.add(scn.nextInt());
				}
				
				break;
			} catch (FileNotFoundException e1) {
				sav(); //creates file if not exists
			}
		}
	}
	
	public static boolean addMem(String strName) {
		
		memory mem = new memory();
		neuron nrn = new neuron();
		
		int intIdxOfNrn = 0, intIdxOfMe = 0;
		
		if (!memoryNames.contains(strName)) { //is new memory
			
			if (intFreeIdxs.isEmpty()) { //use free index
				intIdxOfMe = memories.size();
				
			} else {
				intIdxOfMe = intFreeIdxs.get(0);
			}
			
			nrn.strNeuronsIPointTo.add(Integer.toString(intIdxOfMe));
			intIdxOfNrn = neuronHandler.addNrn(nrn);
			mem.strNeuronRepresentingMe = neuronHandler.toHex(intIdxOfNrn);
			
			if (intFreeIdxs.isEmpty()) {
				memories.add(mem);
				memoryNames.add(strName);
				
			} else {
				memories.set(intFreeIdxs.get(0), mem);
				memoryNames.set(intFreeIdxs.get(0), strName);
				intFreeIdxs.remove(0);
			}
			return true; //return val might not be used
			
		} else {
			return false; //return val might not be used
		}
	}

	
	public static String getRelNrn(String strWord) {
		int intIdx = memoryNames.indexOf(strWord);
		
		if (intIdx != -1) {
			return memories.get(intIdx).strNeuronRepresentingMe;
		
		} else {
			return "";
		}
	}
	
	public static memory getMem(String strWord) {
		int intIdx = memoryNames.indexOf(strWord);
		if (intIdx != -1) {
			return memories.get(intIdx);
		} else {
			return null;
		}
	}
	
	public static void sav() {
		
		String strLoc = "";
		
		strLoc = ClassLoader.getSystemClassLoader().getResource(".").getPath().toString();
		strLoc = strLoc.substring(0, strLoc.lastIndexOf("/") - 3) + "Cranium/";
		
		File fleMems = new File(strLoc + "memories.txt");
		File fleFreeIdxs = new File(strLoc + "memFreeIdxs.txt");
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fleMems)));
			
			memory memTemp = new memory();
			String strNeurons = "";
			
			for (int i = 0; i < memories.size(); i++) {
				memTemp = memories.get(i);
				strNeurons = memTemp.strRelatedNeurons.toString();
				strNeurons = strNeurons.replace(",", ""); //removes commas from .toString of arraylist
				strNeurons = strNeurons.substring(1, strNeurons.length() -1); //removes the brackets
				
				writer.println(memoryNames.get(i) + " " + memTemp.strNeuronRepresentingMe +  " _ " + strNeurons + " |||");
				writer.flush();
			}
			
			writer.close();
			
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fleFreeIdxs)));
			 
			for (int i = 0; i < intFreeIdxs.size(); i++) {
				writer.print(intFreeIdxs.get(i) + " ");
				writer.flush();
			}
			 
			 writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}