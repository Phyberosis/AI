import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class memory {
	
	public static String srtLoc;
	public static ArrayList<String> memory = new ArrayList<String>();
	public static ArrayList<String> memoryLst = new ArrayList<String>();
	public static ArrayList<Integer> freeIdx = new ArrayList<Integer>();
	public static ArrayList<String> log = new ArrayList<String>();
	
	//loads ityral net and related variables
	public static void ini() {

		try {
			srtLoc = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().toString();
			srtLoc = srtLoc.substring(0, srtLoc.lastIndexOf("/") - 3) + "cranium/";
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File a = new File(srtLoc + "shortTerm.txt");
		Scanner scn = null;
		String name, syn, str = "";

		int i = 0;
		while (true) {
			try {
				scn = new Scanner(a);
				while (scn.hasNextLine()) {
					scn.nextLine();
					i++;
				}
				scn.close();
				break;
				
			} catch (FileNotFoundException e1) {
				savSrt();
			}
		}
		
		try {
			scn = new Scanner(a);
			while (i>0) {
				str = scn.nextLine();
				name = str.substring(0, str.indexOf(32)); // 32 is space
				syn = str.substring(str.indexOf(32)+1);
				memory.add(syn);
				memoryLst.add(name);
				if (str.contains("_ _")) {
					freeIdx.add(i-1);
				}
				i--;
			}
			scn.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		scn.close();
		
		//ini log
		File b = new File(srtLoc + "Qlog.txt");
		try {
			scn = new Scanner(b);
			while (scn.hasNextLine()){
				scn.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean addSyn(int idx, String syn) {
		
		boolean ret = false;
		int added = 0;
		String ity, s = "";
		Scanner scn;

		scn = new Scanner(syn);
		while (scn.hasNext()) {
			ity = memory.get(idx);
			s = scn.next();
			if (!ity.contains(s)) {
				if (ity.contains("_")) {
					ity = "";
				}
				memory.set(idx, ity.concat(s+" "));
				added++;
			}
		}
		
		if (added > 0){
			ret = true;
		}else{
			ret = false;
		}
		scn.close();
		return ret;
	}
	
	//removes synapse syn
	public static boolean rmvSyn(int idx, String syn) {
		
		boolean ret = false;
		int start;
		String first, last;
		String syns, s;
		
		syn = syn.trim() + " ";
		Scanner scn = new Scanner(syn);
		
		while(scn.hasNext()){
			s = scn.next();
			syns = memory.get(idx);
			if(syns.contains(s)) {
				start = syns.indexOf(s);
				last = syns.substring(start);
				first = syns.substring(0, start);
				last = last.substring(s.length() + 1);
				memory.set(idx, first.concat(last));
				ret = true;
			}
		}
		
		scn.close();
		
		//if last syn;
		if (memory.get(idx).equals("")) {
			memory.set(idx, "_");
			freeIdx.add(idx);
		}
		
		return ret;
	}
	
	//adds entity with synapses, if synapses = "", adds "_" 
	public static int addIty(String Name, String synapses) {
		int ret = -1;
		int l = freeIdx.size();
		int idx = 0;
		
		if (memoryLst.contains(Name)) {
			return ret;
		}
		
		if (l != 0) { //free idx avail
			ret = freeIdx.get(l-1);
			memoryLst.set(ret, Name);
			idx = cerebrum.addNeu(Integer.toString(ret));
			memory.set(ret, "-" + cerebrum.toHex(idx) + " ");
		}else{
			ret = memory.size();
			memoryLst.add(Name);
			idx = cerebrum.addNeu(Integer.toString(ret));
			memory.add("-" + cerebrum.toHex(idx) + " ");
		}
		return ret;
	}
	
	//true = done, false = entity is not used
	public static boolean rmvIty(int idx) {
		
		boolean bol;
		
		if (!memoryLst.get(idx).equals("_")) {
			freeIdx.add(idx);
			memory.set(idx, "_");
			memoryLst.set(idx, "_");
			bol = true;
		}else{
			bol = false;
		}
		
		return bol;
	}

	public static void savSrt() {
		
		File f = new File(srtLoc + "shortTerm.txt");
		String str, name;
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			int i = 0, l = memory.size();
			while (i<l) {
				str = memory.get(i);
				name = memoryLst.get(i);
				writer.println(name+" "+str);
				i++;
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//sav log
		File b = new File(srtLoc + "Qlog.txt");
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(b)));
			int l = log.size()-1;
			for (int i = 0; i <= l; i++) {
				writer.println(log.get(l-i));
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}