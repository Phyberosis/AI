import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class com extends JFrame implements KeyListener, ActionListener{

	private static final long serialVersionUID = 2007645652554426099L;
	private static String[] quote = new String[2];
	private final Button btnClose;
	
	JPanel p=new JPanel();
	static JTextArea dialog=new JTextArea(24,60);
	static JTextArea input=new JTextArea(1,60);
	static JLabel label = new JLabel();
	JScrollPane scroll=new JScrollPane(
		dialog,
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
	);
	
	//makes window
	public com(){
		super("Brain");
		setSize(700,500);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		btnClose = new Button("close");
	
		p.add(scroll);
		label.setText("       Speak to me       ");
		p.add(label);
		p.add(input);
		p.add(btnClose);
		DefaultCaret caret = (DefaultCaret) dialog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		p.setBackground(new Color(220,220,220));
		getContentPane().add(p);
		
		dialog.setEditable(false);
		input.addKeyListener(this);
		btnClose.addActionListener(this);
		
		setVisible(true);
		
		input.requestFocus();
		
		//addTextM("memBnk location:\n" + memBnk.getLoc());
		//addTextM("\nchange by adding to input then press \"Home\"\n");
	}
	// adds to text area
	public static void addText(String str) {
		dialog.setText(dialog.getText()+str);
	}
	
	public static void respond(String str){
		dialog.setText(dialog.getText()+"\n--->Me: " + str);
	}
	
	public static String getquote(){
		return quote[0];
	}
	
	//adds input on ENTER
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			if (!input.getText().equals("")) {
				input.setEditable(false);
				quote[1] = Long.toString(System.currentTimeMillis());
				quote[0]=input.getText();
				input.setText("");
				quote[0] = quote[0].trim();
				addText("\n-->You: "+quote[0]);
				
				taskHandler.taskLst.add(tasks.newInput);
				taskHandler.taskData.add(quote);
				taskHandler.pause = false;

			}else{
				input.setEditable(false);
				input.setText("");
			}

		}else if(e.getKeyCode()==KeyEvent.VK_DOWN){
			log.println((Object) ">> >> manual nrn check " + thought.nrnsA.toString() + " " + thought.nrnsALst.toString());
		}
	}
	
	public void keyTyped(KeyEvent e){}

	public void keyReleased(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			input.setEditable(true);
		}
	}
	public void actionPerformed(ActionEvent e) {
		cerebrum.sav();
		memory.savSrt();
		System.exit(0);
	}
}
