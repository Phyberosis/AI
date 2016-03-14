import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

class mimicData {
	public String strOne;
	public String strTwo;
	public ArrayList<actNeuron> actNeurons;
	public ArrayList<String> actNeuronIDs;
	
	public mimicData() {
		strOne = "";
		strTwo = "";
		actNeurons = new ArrayList<actNeuron>();
		actNeuronIDs = new ArrayList<String>();
	}
}

class inquireData {
	public String strWord;
	public String strQuote;
	
	public inquireData(String strW, String strQ) {
		strWord = strW;
		strQuote = strQ;
	}
}

class memory {
	public String strNeuronRepresentingMe;
	public ArrayList<String> strRelatedNeurons;
	
	public memory() {
		strNeuronRepresentingMe = "";
		strRelatedNeurons = new  ArrayList<String>();
	}
}

class neuron {
	public ArrayList<String> strRequirements; //nrns that proc me
	public ArrayList<String> strNeuronsIPointTo;
	public boolean blnIsFunction;
	
	public neuron() {
		strRequirements = new ArrayList<String>();
		strRequirements.trimToSize();
		strNeuronsIPointTo = new ArrayList<String>();
		strNeuronsIPointTo .trimToSize();
		blnIsFunction = false;
	}
}

class actNeuron {
	public ArrayList<String> strRequirements; //nrns that proc me
	public ArrayList<String> strNeuronsIPointTo;
	public int intPreReqs;
	public int intAmntPoked;
	public boolean blnFired;
	
	public boolean blnIsFunction;
	
	public actNeuron(neuron nrn) {
		strRequirements = new ArrayList<String>(nrn.strRequirements);
		strNeuronsIPointTo = new ArrayList<String>(nrn.strNeuronsIPointTo);
		intPreReqs = strRequirements.size();
		intAmntPoked = 0;
		blnFired = false;
		
		blnIsFunction = new Boolean(nrn.blnIsFunction);
	}
}

class checkDupData {
	public neuron nrn;
	public int intIdx;
	
	public checkDupData(neuron nrn, int intIdx) {
		this.nrn = nrn;
		this.intIdx = intIdx;
	}
	
	public checkDupData() {}
}

class resData {
	public String[] strQuote;
	public  ArrayList<String> strResponse;
	
	public resData() {
		strResponse = new ArrayList<String>();
	}
	
}

class neuralNet implements Runnable {

	private static ArrayList<actNeuron> actNeurons;
	private static ArrayList<String> actNeuronIDs;
	private static boolean blnPause, blnIsPaused, blnDone;
	private static short srtCertainty = 1000, srtCycle = 0;
	private static Thread thdMe;
	private static Object anchor = new Object();
	
	public static synchronized void pause() {
		blnPause = true;
	}
	
	public static synchronized void unpause() {
		blnPause = false;
		synchronized (anchor) {
			anchor.notify();
		}
	}
	
	public static synchronized boolean blnIsPaused() {
		return blnIsPaused;
	}
	
	public static synchronized boolean blnDone() {
		return blnDone;
	}
	
	public static synchronized void setDone(boolean blnToSet) {
		blnDone = blnToSet;
	}
	
	public static synchronized ArrayList<actNeuron> actNeurons() {
		return actNeurons;
	}
	
	public static synchronized ArrayList<String>  actNeuronIDs() {
		return actNeuronIDs;
	}
	
	public static synchronized void setCertainty(short srtToSet) {
		srtCertainty = srtToSet;
	}
	
	public neuralNet() {
		actNeurons = new ArrayList<actNeuron>();
		actNeuronIDs = new ArrayList<String>();
		blnPause = false;
		blnIsPaused = false;
		blnDone = false;
	}
	
	public void run() {
		try {
		
		int intItrtr = 0;
		int intIdxOfActNrn = 0;
		actNeuron actTemp = new actNeuron(new neuron());
		boolean blnChanged;
		float fltPercentCert, fltHighestCert = 0;
		
		while (true) {
			
			blnIsPaused = true;
			synchronized (anchor) {
				while (blnPause) {
					anchor.wait();
				}
			}
			blnIsPaused = false;
			
			log.println(" * * * neural net cycle runs with neurons: " + actNeuronIDs.toString() + ", certainty: " + srtCertainty);
			
			/*@@@@@*/
			/** BEGINNING OF LOOP**/
			/*@@@@@*/
			intItrtr = 0;
			blnChanged = false;
			while (intItrtr < actNeurons.size() && !blnPause) { //if pause, reset
				actTemp = actNeurons.get(intItrtr); //get nrn to work with
				
				//if requirements met, fire nrn
				fltPercentCert = ((float)1 - ((float)(actTemp.strRequirements.size())/(float)actTemp.intPreReqs))*(float)1000;
				if ((actTemp.intPreReqs == 0 ||fltPercentCert >= srtCertainty) && !actTemp.blnFired) {
					
					blnChanged = true;
					
					for (int i = 0; i < actTemp.strNeuronsIPointTo.size(); i++) { //nrns I point to
						
						intIdxOfActNrn = actNeuronIDs.indexOf(actTemp.strNeuronsIPointTo.get(i));
						if(intIdxOfActNrn != -1) {
							actNeurons.get(intIdxOfActNrn).intAmntPoked ++; //if nrn I pointed to already fired, increments fired amnt
							
						} else {
							for (int ii = 0; ii <= actTemp.intAmntPoked; ii ++ ) {
								neuronHandler.fire(actTemp.strNeuronsIPointTo.get(i), actNeuronIDs.get(intItrtr));
							}
						}
					}
					
					actTemp.blnFired = true;
					actNeurons.set(intItrtr, actTemp); //set changes
					
				} else if (fltPercentCert > fltHighestCert && !actTemp.blnFired){
					fltHighestCert = fltPercentCert;
				}
				
				intItrtr ++;
			}
			/*@@@@@*/
			/**@@ END OF LOOP @@**/
			/*@@@@@*/
			srtCycle ++;
			
			if (!blnPause && !blnChanged) {// if break not from pause event, ie no more fires possible from current params
				
				if (srtCycle <= 2) {
					srtCertainty = (short) Math.floor(fltHighestCert - 1);//System.out.println(fltHighestCert);
					
				} else {
					log.println(" * * * neural net cycle ended with neurons: " + actNeuronIDs.toString());
					
					srtCycle = 0;
					srtCertainty = 1000;
					fltHighestCert = 0;
					
					blnDone = true;
					blnPause = true;
				}
			}
		}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			log.println(sw.toString());
		}
	}
	
	public void start() {
		if (thdMe == null) {
			thdMe = new Thread(this);
			thdMe.start();
			pause();
			log.println( "The neuralNet thread has started.");
			
		} else {
			log.println("The neuralNet thread is already running.");
		}	
	}
}

class checkDuplicate implements Runnable {

	private static neuron nrn;
	private static int intIdx;
	private static boolean blnRun= false;
	private Thread thdMe;
	private static Object anchor = new Object();
	
	public static synchronized void setNrn(neuron nrnToSet) {
		nrn = nrnToSet;
	}
	
	public static synchronized void setIdx(int intToSet) {
		intIdx = intToSet;
	}
	
	public static synchronized boolean check() {
		
		if (!blnRun) {
			
			blnRun = true;
			synchronized (anchor) {
				anchor.notify();
			}
			return false;
			
		} else {
			return true;
		}

	}
	
	public checkDuplicate() {
	}
	
	public void run() {
		try {
			
		String strTemp = "";
		while (true) {
			
			synchronized (anchor) {
				while (!blnRun) {
					anchor.wait();
				}
			}
			
			/*@@@@@*/
			/** BEGINNING OF "LOOP"**/
			/*@@@@@*/
			if (!neuronHandler.intFreeIdxs().contains(intIdx)) {
				
				for (int i = 0; i < neuronHandler.neurons().size(); i++) {
					
					if (nrn.strRequirements.equals(neuronHandler.neurons().get(i).strRequirements) && i != intIdx) {
						
						if (nrn.strNeuronsIPointTo.equals(neuronHandler.neurons().get(i).strNeuronsIPointTo)) {
							
							if (!neuronHandler.intFreeIdxs().contains(intIdx)) {
								neuronHandler.rmvNrn(intIdx);

								log.println("---Neuron (" + neuronHandler.toHex(intIdx) + ") was duplicate - removed.");
								
								for (int ii = 0; ii < nrn.strNeuronsIPointTo.size(); ii++) {
									strTemp = nrn.strRequirements.get(ii);

									if (Character.valueOf(strTemp.charAt(0)) > 47 && Character.valueOf(strTemp.charAt(0)) < 58) { //is word
										memoryHandler.memories.get(Integer.valueOf(strTemp)).strRelatedNeurons.remove(neuronHandler.toHex(intIdx));

									} else {
										neuronHandler.neurons().get(neuronHandler.toNum(strTemp)).strRequirements.remove(neuronHandler.toHex(intIdx));
									}
								}
							}
							
							blnRun = false;
							break;
						}
					}
				}
			}
			/*@@@@@*/
			/**@@ END OF "LOOP" @@**/
			/*@@@@@*/
			
			blnRun = false;
		}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			log.println(sw.toString());
		}
	}
	
	public void start() {
		if (thdMe == null) {
			thdMe = new Thread(this);
			thdMe.start();
		}
	}
}