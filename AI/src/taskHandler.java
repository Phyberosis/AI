import java.util.ArrayList;
import java.util.Scanner;

public class taskHandler implements Runnable{
	
	public static ArrayList<tasks> taskLst = new ArrayList<tasks>();
	public static ArrayList<Object> taskData = new ArrayList<Object>();
	public static boolean busy = false, pause = false;
	public static Thread thdTH;
	
	public taskHandler(){}
	
	@SuppressWarnings("unchecked")
	public void run() {
		
		int id, wordCount, freq, idx;
		String nrnId, nrns, word = "", str, syns;
		String[] quote = new String[2];
		ArrayList<String> wordsToLink, idxToLink;
		ArrayList<Integer> wIdx;
		boolean fired  = false;
		//float synsIn, synsLeft;
		
		while (true) {
			busy = true;
			while(taskLst.size() != 0) { //does tasks till done
				switch (taskLst.get(0)){
				
				case newInput:
					quote = (String[]) taskData.get(0);
					quote[0] = lang.rmvPunc(quote[0]);
					word = "";
					wordCount = 0;
					wordsToLink = new ArrayList<String>();
					
					//moves active nrns to context list, clears active nrns
					thought.nrnsCtxt.add(thought.nrnsALst.toString());
					if (thought.nrnsCtxt.size() > 50) {
						thought.nrnsCtxt.remove(0);
					}
					thought.nrnsA.clear();
					thought.nrnsALst.clear();
					
					log.println("\n >>>> new input");
					Scanner scn = new Scanner(quote[0]);
					//adds new words
					while (scn.hasNext()){
						word = scn.next();
						word = lang.rmvPunc(word);
						if (!(memory.addIty(word, "") == -1)) {
							log.println("learned " + word);
							
						}
						if (!wordsToLink.contains(word)) {
							wordsToLink.add(word);
							wordCount ++;
						}
					}
					scn.close();
					
					if (wordCount > 1) {
						taskLst.add(tasks.link);
						taskData.add(wordsToLink);
					}
					
					scn = new Scanner(quote[0]);
					Scanner scnNrns = null;
					wIdx = new ArrayList<Integer>();
					
					fired = false;
					while (scn.hasNext()){//fires each word
						word = scn.next();
						word = lang.rmvPunc(word);
						id = memory.memoryLst.indexOf(word);
						nrns = memory.memory.get(id);
						scnNrns = new Scanner(nrns);
						
						while(scnNrns.hasNext()) { //fires all nrns of word
							nrnId = scnNrns.next();
							if (!nrnId.equals("_") && !nrnId.contains("-")) { //checks to see if nrn is empty, skips the dedicated nrn linked to the word
									while (!thought.isPaused) {//wait
									}
									log.println("thought paused, manual fire commencing");
									cerebrum.fire(nrnId, Integer.toString(id));
									fired = true;
									wIdx.add(Integer.valueOf(id));
							}
						}
					}
					if (fired) {
						thought.pause = false;	
					}
					
					for (int i = 0; i < thought.nrnsA.size(); i++) {
						str = thought.nrnsA.get(i);
						if (Float.valueOf(str.substring(str.indexOf(")") + 3, str.indexOf(")", str.indexOf(") (") + 1))) == 0) {
							thought.nrnsA.remove(i);
							i--; //since one idx is removed
						}
					}
					
					scn.close();
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case ini: //initialize
					new log();
					new com();
					memory.ini();
					cerebrum.ini();
					thought thdTht = new thought();
					thdTht.start();
					
					com.addText("Initialization sucessful");
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case addToLog:
					//System.out.print((String) taskData.get(0));
					log.addText((String) taskData.get(0));
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case respond:
					freq = 0;
					for (int i = 0; i < cerebrum.resFreq.size(); i++) {
						if (freq < cerebrum.resFreq.get(i)) {
							freq = cerebrum.resFreq.get(i);
						}
					}
					
					str = "";
					while (true) {
						idx = cerebrum.resFreq.indexOf(freq);
						if (idx == -1 || cerebrum.responses.isEmpty()) {
							break;
						} else {
							str = str +  cerebrum.responses.get(idx) + " ";
							cerebrum.resFreq.remove(idx);
							cerebrum.responses.remove(idx);
						}
					}
					cerebrum.responses.clear();
					cerebrum.resFreq.clear();
					com.respond(str);
					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				case link: //redundancies removed at newInput
					wordsToLink = (ArrayList<String>) taskData.get(0);
					wIdx = new ArrayList<Integer>();
					wIdx.clear();
					idxToLink = new ArrayList<String>();
					idxToLink.clear();
					
					//populates idx array with idxs of associated nrns
					for(int i = 0; i < wordsToLink.size(); i ++) {
						wIdx.add(memory.memoryLst.indexOf(wordsToLink.get(i)));
						str = memory.memory.get(wIdx.get(i)); //gets nrns of word
						str = str.substring(str.indexOf("-") + 1, str.indexOf(" ", str.indexOf("-"))); //gets the associated nrn
						idxToLink.add(str);                                    /*****@@@@CHECK THIS **/
					}
					
					//sets up neuron for group
					str = idxToLink.toString();
					str = str.substring(1, str.length()-1);
					str = str.replace(",", ""); //replaces commas
					if (!cerebrum.nNetTo.contains(str + " ")) { //if exact link doesn't already exist
						id = cerebrum.addNeu("");
						syns = "";
						for (int i = 0; i < wIdx.size(); i ++) { //constructs string with pres
							syns = syns.concat("-" + wIdx.get(i).toString() + " ");
						}
						cerebrum.addSyn(id, str.concat(" " + syns));
						
						//adds group nrn as pre to each word's nrn
						for(int i = 0; i < wordsToLink.size(); i ++) {
							cerebrum.addSyn(cerebrum.toNum(idxToLink.get(i)), "-" + cerebrum.toHex(id));
							memory.addSyn(wIdx.get(i), cerebrum.toHex(id));
						}
						
						log.addText("\nlinked " + wordsToLink.toString());
					}

					taskLst.remove(0);
					taskData.remove(0);
					break;
					
				default:
					break;
				}
			}
			
			if (taskLst.size() == 0) {
				pause = true;
				log.addText("\ntaskHandler paused");
			}
			while (pause) { //wait
			}
			log.addText("\ntaskHandler resumed");
			
			/*try {
				busy = false;
				Main.hubAnchor.wait(); could cause problems from lock ownership, see solution in runLibrary.thought
			} catch (InterruptedException e) {
			}*/
		}
	}
	
	public void ini() {
		if (thdTH == null) {
			thdTH = new Thread(this);
			thdTH.start();
		}else{
			log.println("The task handler is already running");
		}
	}
}
