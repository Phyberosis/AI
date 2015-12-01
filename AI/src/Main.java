import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	//public static Object hubAnchor = new Object();
	//public static ArrayList<String> log = new ArrayList<String>();
	
	public static void main(String arge[]) throws IOException{

		// hi
		taskHandler TH = new taskHandler();
		TH.ini();
		taskHandler.taskLst.add(tasks.ini);
		taskHandler.taskData.add(new Object());
		taskHandler.pause = false;
		/*synchronized (hubAnchor){
			hubAnchor.notifyAll();
		}*/
	}
}