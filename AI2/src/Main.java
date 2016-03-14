import java.io.IOException;

public class Main {
	
	public static void main(String arge[]) throws IOException{
			new log();
			new com(); //main interface
			
			taskHandler TH = new taskHandler();
			TH.start();
	}
}