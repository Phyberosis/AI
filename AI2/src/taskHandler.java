import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class taskHandler implements Runnable{
	private static ArrayList<tasks> taskLst;
	private static ArrayList<Object> taskData;
	private static Thread thdMe;
	private static boolean blnPause;
	private static Object anchor = new Object();
	
	public taskHandler(){
		taskLst = new ArrayList<tasks>();
		taskData =  new ArrayList<Object>();
	}
	
	public static synchronized void pause() {
		blnPause = true;
	}
	
	public static synchronized void unpause() {
		blnPause = false;
		synchronized (anchor) {
			anchor.notify();
		}
	}
	
	public static synchronized ArrayList<tasks> taskLst() {
		return taskLst;
	}
	
	public static synchronized ArrayList<Object> taskData() {
		return taskData;
	}
	
	public void run() {
		try {
		
		while (true) {
			
			if (blnPause || taskLst.isEmpty()) {
				
				com.label2.setText("idle");
				blnPause = true;
				synchronized (anchor) {
					while (blnPause) {
						anchor.wait();
					}
				}
			}
			
			/*@@@@@*/
			/** BEGINNING OF LOOP**/
			/*@@@@@*/
			
			while(!taskLst.isEmpty() && !blnPause) { //does tasks till done
				com.label2.setText("current task: " + taskLst.get(0).toString());
				taskLst.trimToSize();
				taskData.trimToSize();
				switch (taskLst.get(0)){
				
				case newInput:
					cseNewInput((String[]) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case getFunction:
					cseGetFunction((String) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case compileResponse:
					cseCompileRes((resData) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case mimic:
					cseMimic((String) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case learnFromMimic1:
					cseLearnFromMimic1((mimicData) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case learnFromMimic2:
					cseLearnFromMimic2((mimicData) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case learnFromMimic3:
					cseLearnFromMimic3((mimicData) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case ini:
					cseIni();
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case associate:
					cseAssociate((String[]) taskData.get(0));
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case checkDup:
					checkDupData cdd = new checkDupData();
					cdd = (checkDupData) taskData.get(0);
					if (!checkDuplicate.check()) {
						checkDuplicate.setNrn( cdd.nrn);
						checkDuplicate.setIdx(cdd.intIdx);
						checkDuplicate.check();
						
					} else {
						taskLst.add(tasks.checkDup);
						taskData.add(cdd);
					}
					
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				default:
					taskLst.remove(0);
					taskData.remove(0);
					break;
				}
			}
			/*@@@@@*/
			/**@@ END OF LOOP @@**/
			/*@@@@@*/
		}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			log.println(sw.toString());
			
			log.println("@@@ PLease restart @@@");
		}
	}
	
	private void cseIni() {
		//loads words and nrns
		memoryHandler.ini();
		neuronHandler.ini();
		
		//starts separate threads
		neuralNet nNet = new neuralNet();
		nNet.start();
		checkDuplicate chkNrn = new checkDuplicate();
		chkNrn.start();
		
		com.addText("Initialized\n");
	}

	private void cseNewInput(String[] strQuote) {
		
		log.println("\n--- New Input: " + strQuote[0]);
		
		Scanner scn = new Scanner(strQuote[0]);
		String strWord = "";
		memory mem = new memory();
		ArrayList<String> wordsToAssociate = new ArrayList<String>();
		
		neuralNet.pause();
		while (!neuralNet.blnIsPaused()) {
			//wait for pause
		}
		
		neuralNet.actNeuronIDs().clear();
		neuralNet.actNeurons().clear();

		while (scn.hasNext()) { //for each word in input
			
			strWord = lang.rmvPunc(scn.next());
			
			if (memoryHandler.addMem(strWord)) {
				//was new word, no nrns to fire
				
			} else {
				mem = memoryHandler.getMem(strWord);
				
				for (int i = 0; i < mem.strRelatedNeurons.size(); i++) { //FIRST ONE IS AN UNDERSCORE
					neuronHandler.fire(mem.strRelatedNeurons.get(i), strWord);
				}
			}
			wordsToAssociate.add(strWord);
		}
		
		neuralNet.setDone(false);
		neuralNet.unpause();
		
		//adds next tasks
		String[] strWords = new String[0];
		taskLst.add(tasks.associate);
		taskData.add(wordsToAssociate.toArray(strWords));
		scn.close();
		
		taskLst.add(tasks.getFunction);
		taskData.add(strQuote[0]);
	}
	
	private void cseGetFunction(String strQuote) {
		if (neuralNet.blnDone()) {
			
			//ArrayList<actNeuron> actFunctions = new ArrayList<actNeuron>();
			actNeuron actTemp = new actNeuron(new neuron());
			
			ArrayList<String> strWordNrns= new ArrayList<String>();
			for (int i = 0; i < neuralNet.actNeurons().size(); i++) { //gets functions for blacklist
				actTemp = neuralNet.actNeurons().get(i);
				if (actTemp.blnIsFunction ) { // the following restricts guessing ->*/			&& actTemp.strRequirements.isEmpty()) {
					strWordNrns.add(neuralNet.actNeuronIDs().get(i));
				}
			}
			
			neuron nrnTemp = new neuron();
			//String strNrn;
			if (!strWordNrns.isEmpty()) {
				
				strQuote = " " + strQuote + " ";
				strQuote = strQuote.replace(" ", "   ");
				
				for (int i = 0; i < strWordNrns.size(); i ++) {

					nrnTemp = neuronHandler.getNrn(strWordNrns.get(i));
					for (int ii = 0; ii < nrnTemp.strRequirements.size(); ii ++) { //blacklists
						strQuote = strQuote.replace(" " +    memoryHandler.memoryNames.get(  Integer.valueOf(nrnTemp.strRequirements.get(ii))  ) + " ", "");
					}
				}
				
		/*	} else {
				return; //restricts guessing*/
			}
			
			Scanner scn = new Scanner(strQuote);
			String strWord = "";
			ArrayList<String> strQuoteToSend = new ArrayList<String>();
			
			while (scn.hasNext()) {
				strWord = scn.next();
				strQuoteToSend.add(strWord); // for next task
			}
			scn.close();
			
			neuralNet.setDone(false);
			neuralNet.unpause();
			
			resData data = new resData();
			data.strQuote = strQuoteToSend.toArray(new String[strQuoteToSend.size()]);
			taskLst.add(tasks.compileResponse);
			taskData.add(data);
			
		} else if (neuralNet.actNeuronIDs().isEmpty()) {
			//nothing, I am clueless
			
		} else {
			taskLst.add(tasks.getFunction);
			taskData.add(strQuote);
		}
	}
	
	private void cseCompileRes(resData data) {		
		if (neuralNet.blnDone()) {
			
			int i = 0;
			for (i = 1; i < data.strQuote.length; i++) {
				if (!data.strQuote[i].equals("")) { // quote is empty
					break;
				}
			}
			
			// adds words fired from neural net cycle
			if (!data.strResponse.isEmpty()) {
				
				for (int ii = 0; ii < data.strResponse.size(); ii ++) {
					
					if (!neuronHandler.strWordsFired().contains(data.strResponse.get(ii))) {
						data.strResponse.set(ii, "");
					}
				}
	
			} else {
				data.strResponse.addAll(neuronHandler.strWordsFired());
			}
			
			neuralNet.actNeuronIDs().clear();
			neuralNet.actNeurons().clear();
			neuronHandler.strWordsFired().clear();

			if (i == data.strQuote.length || data.strQuote.length == 0) {
				String strRes = "";
				
				//adds unique words to response
				for (int ii = 0; ii < data.strResponse.size(); ii ++) {
					if (!strRes.contains(data.strResponse.get(ii))) {
						strRes += data.strResponse.get(ii) + " ";
					}
				}
				com.respond(strRes);
				return;
			}
			
			memory memTemp = new memory(); // fires neurons of one word, fires another word when called again
			memTemp = memoryHandler.getMem(lang.rmvPunc(data.strQuote[i]));
			for (int ii = 0; ii < memTemp.strRelatedNeurons.size(); ii ++) {
				neuronHandler.fire(memTemp.strRelatedNeurons.get(ii), data.strQuote[i]);
			}
			data.strQuote[i] = "";
			neuralNet.setDone(false);
			neuralNet.unpause();
			
			//calls self for next word
			taskLst.add(tasks.compileResponse);
			taskData.add(data);
			
		} else {
			taskLst.add(tasks.compileResponse);
			taskData.add(data);
		}
	}
	
	private void cseAssociate(String[] strWords) {
		ArrayList<String> strIdxs = new ArrayList<String>();
		ArrayList<String> strWordNrns = new ArrayList<String>();
		
		String nrns = "";
		for (int i = 0; i < strWords.length; i++) { //gets nrns of words of quote
			nrns = nrns.concat(" " +  strWords[i]);
			strIdxs.add(Integer.toString(   memoryHandler.memoryNames.indexOf(strWords[i])   ));
			strWordNrns.add(memoryHandler.getRelNrn(strWords[i]));
		}
		nrns = nrns.replace(" ", ", ");
		nrns = nrns.substring(2);
		
		log.println("associating: " + nrns);
		
		//makes the association nrn
		neuron nrn = new neuron();
		nrn.strNeuronsIPointTo = new ArrayList<String>(strWordNrns);
		nrn.strRequirements = new ArrayList<String>(strIdxs);
		
		int intNewNrnIdx = neuronHandler.addNrn(nrn);
		
		if (checkDuplicate.check()) {
			taskLst.add(tasks.checkDup);
			taskData.add(new checkDupData(nrn, intNewNrnIdx));
			
		} else {
			checkDuplicate.setNrn(nrn);
			checkDuplicate.setIdx(intNewNrnIdx);
			checkDuplicate.check();
		}
		
		log.println("  association made: "+ neuronHandler.toHex(intNewNrnIdx) + " " + neuronHandler.neurons().get(intNewNrnIdx).strRequirements.toString());
		
		//adds new nrn to each word
		for (int i = 0; i < strIdxs.size(); i++) {
			if (!memoryHandler.memories.get(Integer.valueOf(strIdxs.get(i))).
					strRelatedNeurons.contains(neuronHandler.toHex(intNewNrnIdx))) {// if does not already exist
				
				memoryHandler.memories.get(Integer.valueOf(strIdxs.get(i))). //gets memory
				strRelatedNeurons.add(neuronHandler.toHex(intNewNrnIdx)); //adds new neuron to memory
			}

		}
	}
	
	private void cseMimic(String strToAskBack) {
		com.respond(strToAskBack); //copies user
		
		Scanner scn = new Scanner(strToAskBack);
		String strWord = "";
		
		while (scn.hasNext()) { // if user response contains new words
			strWord = scn.next();
			memoryHandler.addMem(strWord);
		}
		scn.close();
		
		log.println("\n---mimic---");
	}
	
	private void cseLearnFromMimic1(mimicData mimicData) {
		neuralNet.actNeurons().clear();
		neuralNet.actNeuronIDs().clear();
		neuralNet.setCertainty((short)0);
		
		Scanner scn = new Scanner(mimicData.strTwo);
		String strWord = "";
		
		while (scn.hasNext()) {
			strWord = scn.next();
			memoryHandler.addMem(strWord);
		}
		scn.close();
		
		log.println("--- fetching neurons of prompt");
		
		ArrayList<String> strForAssociation = new ArrayList<String>();
		
		strWord = "";
		scn = new Scanner(mimicData.strOne);
		memory mem = new memory();
		
		while (scn.hasNext()) {
			
			strWord = lang.rmvPunc(scn.next());
			mem = memoryHandler.getMem(strWord);
			for (int i = 0; i < mem.strRelatedNeurons.size(); i++) {
				neuronHandler.fire(mem.strRelatedNeurons.get(i), strWord);
			}
			
			strForAssociation.add(strWord);
		}
		scn.close();
		neuralNet.unpause();
		neuralNet.setDone(false);
		
		String[] strToSend = new String[0];
		taskData.add(strForAssociation.toArray(strToSend));
		taskLst.add(tasks.associate);
		
		taskData.add(mimicData);
		taskLst.add(tasks.learnFromMimic2);
	}
	
	private void cseLearnFromMimic2(mimicData mimicData) {
		
		if (neuralNet.blnDone()) { // first net cycle over for prompt
			String strWord = "";
			Scanner scn = new Scanner(mimicData.strTwo);
			memory mem = new memory();
			
			log.println("--- fetching neurons of response");
			
			mimicData.actNeurons = new ArrayList<actNeuron>(neuralNet.actNeurons());
			mimicData.actNeuronIDs = new ArrayList<String>(neuralNet.actNeuronIDs());
			
			neuralNet.actNeuronIDs().clear();
			neuralNet.actNeurons().clear();
			
			while (scn.hasNext()) {
				
				strWord = lang.rmvPunc(scn.next());
				
				if (!mimicData.strOne.contains(strWord)) {
					
					mem = memoryHandler.getMem(strWord);
					for (int i = 0; i < mem.strRelatedNeurons.size(); i++) {
						neuronHandler.fire(mem.strRelatedNeurons.get(i), strWord);
					}
				}
			}
			scn.close();
			neuralNet.setDone(false);
			neuralNet.unpause();
			
			taskLst.add(tasks.learnFromMimic3);
			taskData.add(mimicData);
			
		} else { //wait
			taskLst.add(tasks.learnFromMimic2);
			taskData.add(mimicData);
		}
	}
	
	private void cseLearnFromMimic3(mimicData mimicData) {
		if (neuralNet.blnDone()) { // first net cycle over for response
			
			ArrayList<String> strCommonNeuronIDs = new ArrayList<String>();
			ArrayList<actNeuron> strCommonNeurons = new ArrayList<actNeuron>();
			ArrayList<Integer> intPoked = new ArrayList<Integer>();
			
			//gets common nrns that were fired by the mimic and the user response
			for (int i = 0; i < mimicData.actNeuronIDs.size(); i ++) {
				if (neuralNet.actNeuronIDs().contains(mimicData.actNeuronIDs.get(i))) {
					strCommonNeuronIDs.add(mimicData.actNeuronIDs.get(i));
					strCommonNeurons.add(mimicData.actNeurons.get(i));
					intPoked.add(mimicData.actNeurons.get(i).intAmntPoked); //gets amnt that each nrn was fired
				}
			}
			
			log.println("--- common neurons are: " + strCommonNeuronIDs.toString() + "\n--- each was poked:          " + intPoked.toString()
			+ "\n---mimic end---");
			
			ArrayList<String> strKeyWords = new ArrayList<String>();
			ArrayList<String>  strNIPT = new ArrayList<String>();
			String strWord, strID;
			for (int i = 0; i < strCommonNeurons.size(); i++) { //for each nrn
				
				strNIPT = strCommonNeurons.get(i).strNeuronsIPointTo;
				for (int ii = 0; ii < strNIPT.size(); ii ++) { //each item the nrn points to (which should be words)
					
					if (!(Character.valueOf(strNIPT.get(ii).charAt(0)) > 47 && Character.valueOf(strNIPT.get(ii).charAt(0)) < 58)) { //is not word
						strID = neuronHandler.getNrn(strNIPT.get(ii)).strNeuronsIPointTo.get(0); //if is word nrn, then first req is word
						strWord = memoryHandler.memoryNames.get(Integer.valueOf(strID)); //get word
						
						if (!strKeyWords.contains(strWord)) {
							strKeyWords.add(strWord); //add word to keyword list
						}
					}
				}
			}
			
			mimicData.strOne = " " + mimicData.strOne + " ";
			mimicData.strOne = mimicData.strOne.replace(" ", "   ");
			for (int i = 0; i < strKeyWords.size(); i ++) {
				mimicData.strOne = mimicData.strOne.replace(" " + strKeyWords.get(i) + " ", ""); //replaces the keyWords, only prompts left
			}
			
			neuron newNrn = new neuron();
			
			Scanner scn = new Scanner(mimicData.strOne);
			
			while (scn.hasNext()) {
				strWord = scn.next();
				
				newNrn.strRequirements.add(Integer.toString(  memoryHandler.memoryNames.indexOf(strWord)  ));
			}
			newNrn.blnIsFunction = true;
			neuronHandler.addNrn(newNrn);
			
			scn.close();
			
			neuralNet.actNeurons().clear();
			neuralNet.actNeuronIDs().clear();
			neuralNet.setCertainty((short)1000);
			
			com.blnMimicing = false;
			
		} else { //wait
			taskLst.add(tasks.learnFromMimic3);
			taskData.add(mimicData);
		}
	}
	
	public void start() {
		if (thdMe == null) {
			thdMe = new Thread(this);
			thdMe.start();
			
			taskLst.add(tasks.ini);
			taskData.add(new Object());
			unpause();
			
			log.println("The task handler thread has started.");
			
		}else{
			log.println("The task handler is already running");
		}
	}
}
