import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class log extends JFrame{

	private static final long serialVersionUID = -7349031396240791631L;
	JPanel p = new JPanel();
	static JTextArea log = new JTextArea(30,50);
	JScrollPane scroll = new JScrollPane(
			log,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
	
	public log(){
		super("Brain - log");
		setSize(568,520);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		p.add(scroll);
		DefaultCaret caret = (DefaultCaret) log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		p.setBackground(new Color(220,220,220));
		getContentPane().add(p);
		
		log.setText("Log active");
		log.setEditable(false);
		setVisible(true);
	}
	
	public static void println(String str) {
		taskHandler.taskLst.add(tasks.addToLog);
		taskHandler.taskData.add((Object) "\n" + str);
		taskHandler.pause = false;
	}
	
	public static void addText(String str) {
		log.setText(log.getText() + str);
	}
}
