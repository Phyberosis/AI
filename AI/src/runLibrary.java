import java.util.ArrayList;
import java.util.Scanner;

class thought implements Runnable {
	
	public static ArrayList<String> nrnsA = new ArrayList<String>();//    list of active nrns syns
	public static ArrayList<String> nrnsALst = new ArrayList<String>(); //list of active nrns names
	public static ArrayList<String> nrnsCtxt = new ArrayList<String>(); //list of previously active nrns names
	public static boolean pause = false, isPaused = false, reset = false, concluded = false;
	public static short certainty = 100, highestCert = 0, iterations = 0;
	
	private Thread thdThought;
	
	thought() {}
	
	public void run() {
		
		String id = "", to;
		Scanner scn, sTo;
		
		while (true){ // THIS THREAD SHOULD NEVER DIE!!
			
			//for reset
			reset = false;
			pause = false;
			int i = 0, idx = 0;
			String nrn = "", nextNrn;
			Float synIns, synLeft;
			boolean changed = false;
			nrnsA.clear();
			nrnsALst.clear();
			iterations = 0;
			certainty = 100;
			
			/** should begin from neuron one each time*/
			while (!reset) {
				
				if (nrnsA.isEmpty() || nrnsALst.isEmpty() || pause) {
					log.println("thought is idle");
					pause = true;
					changed = false;
					isPaused = true;
					while(pause) { //wait
					}
					isPaused = false;
					
					if (reset) {
						break;
					}
					
					log.println("thought loop has resumed");
					i = 0;
					iterations = 0;
					certainty = 100;
					highestCert = 0;
				}

				/**synchronize brought problems from lock ownership**/
				/*synchronized (thtAnchor) {
					while(nrnsA.isEmpty() || nrnsALst.isEmpty() || pause) {System.out.println(nrnsA);
						log.println("thought is idle");
						try {
							isPaused = true;
							changed = false;
							thtAnchor.notifyAll();
							thtAnchor.wait();
							isPaused = false;
						} catch (InterruptedException e) {
							isPaused = false;
						}
						log.println("thought loop has started again");
					}				System.out.println(nrnsA.toString() + nrnsALst.toString());
				}*/

				try {
					//main process
					nrn = nrnsA.get(i); //         -> gets nrn to be tested
					scn = new Scanner(nrn);
					id = nrnsALst.get(i);
					//boolean blnFire = false;
					
					log.println("\n> processing neuron: " + id + " with: " + nrn + "\n>>Certainty: " + certainty + "%");
					nextNrn = scn.next(); //nextNrn used as temp!!
					synIns = Float.valueOf(nextNrn.substring(2,nextNrn.length()-1 ));
					nextNrn = scn.next();//nextNrn used as temp!!
					synLeft = Float.valueOf(nextNrn.substring(1,nextNrn.length()-1 ));
					
					if ((((synLeft / synIns) * 100 <= (100 - certainty)) || synIns == 0) && nrn.contains("(false)")) {
						// if (left/total is greater than current certainty but not 100%, or zero total), and nrn not fired yet
						changed = true;
						sTo = new Scanner(cerebrum.getTo(cerebrum.toNum(id)));

						while (sTo.hasNext()) { //  -> for each following syn
							
							nextNrn = sTo.next();
							log.println("to synapse: " + nextNrn);
							if (cerebrum.fire(nextNrn, id) == 1) {//nrn or word distinguishment done by .fire()
								nrnsALst.set(i, nrn.concat("x")); /**MARKS AS WORD*/
							} 
						}
						sTo.close();
						
						nrn = nrn.replace("(false)", "(true)"); //mark nrn as fired
						nrnsA.set(i, nrn);
					}
					if (highestCert <= 100 -(synLeft / synIns) * 100 && nrn.contains("false") && changed == false) {
						highestCert = (short) (99.5 - (synLeft / synIns) * 100);
					}

					scn.close();
					i++;

					if(i == nrnsA.size()) { //end of each cycle this is true, is really > size-1
						//log.println("\n-next cycle");
						i = 0;
						/*if (concluded) {
							iterations ++;
							concluded = false;
						}
						if (iterations >= 2) { //sets max synapse generations
							pause = true;
						}*/
						if (changed) {
							changed = false;
							certainty = 100;
							highestCert = 0;
						}else{
							if (certainty > 0) {
								certainty = (short) (highestCert);
							}else{
								certainty = 100;
								log.println("\nno change in active neurons, pausing...");
								// say compiled response
								taskHandler.taskLst.add(tasks.respond);
								taskHandler.taskData.add(new Object());
								taskHandler.taskLst.add(tasks.addToLog);
								taskHandler.taskData.add("\ncompiling response at " + certainty);
								taskHandler.pause = false; //notify
								pause = true;
							}
						}
					}
					
					if (reset) {
						break;
					}
					
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();

						taskHandler.taskLst.add(tasks.addToLog);
						taskHandler.taskData.add((Object) "\n**Active neuron lists changed while thought process was going on error occured reseting thought");
						taskHandler.pause = false;
					
					break;
				}
			}
		}
	}
	
	public void start() {
		
		if (thdThought == null) {
			thdThought = new Thread(this);
			thdThought.start();
			
		}else{
			taskHandler.taskLst.add(tasks.addToLog);
			taskHandler.taskData.add((Object) "The thought thread is already running");
			taskHandler.pause = false;
		}
	}
}