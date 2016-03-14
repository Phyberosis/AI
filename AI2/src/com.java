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

	public static boolean blnMimicing = false;
	
	private static final long serialVersionUID = 2007645652554426099L;
	private static String[] quote = new String[2];
	private final Button btnClose, btnNegRe;
	
	JPanel p=new JPanel();
	static JTextArea dialog=new JTextArea(23,60);
	static JTextArea input=new JTextArea(1,60);
	static JLabel label = new JLabel(), label2 = new JLabel();
	JScrollPane scroll=new JScrollPane(
		dialog,
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
	);
	
	//makes window
	public com(){
		super("AI");
		setSize(700,500);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		btnClose = new Button("  save and close  ");
		btnNegRe = new Button("  negative reinforcement  ");
	
		p.add(scroll);
		label.setText("       Speak to me                                           ");
		label2.setText("*");
		p.add(label);
		p.add(label2);
		p.add(input);
		p.add(btnClose);
		p.add(btnNegRe);
		DefaultCaret caret = (DefaultCaret) dialog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		p.setBackground(new Color(220,220,220));
		getContentPane().add(p);
		
		dialog.setEditable(false);
		input.addKeyListener(this);
		btnClose.addActionListener(this);
		btnNegRe.addActionListener(this);
		
		setVisible(true);
		
		input.requestFocus();
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
				
				if (!blnMimicing) {
					input.setEditable(false);
					quote[1] = Long.toString(System.currentTimeMillis());
					quote[0]=input.getText();
					
					taskHandler.taskLst().add(tasks.newInput);
					taskHandler.taskData().add(quote);
					taskHandler.unpause();
					
				} else { //is mimic response
					
					mimicData mimicData = new mimicData();
					
					mimicData.strOne = quote[0]; // from before
					
					input.setEditable(false);
					quote[1] = Long.toString(System.currentTimeMillis());
					quote[0]=input.getText();
					
					mimicData.strTwo = quote[0];
					
					taskHandler.taskLst().add(tasks.learnFromMimic1);
					taskHandler.taskData().add(mimicData);
					taskHandler.unpause();
				}
				
				input.setText("");
				quote[0] = quote[0].trim();
				addText("\n-->You: "+quote[0]);

			}else{
				input.setEditable(false);
				input.setText("");
			}
		}
	}
	
	public void keyTyped(KeyEvent e){}

	public void keyReleased(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			input.setEditable(true);
		}
	}
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == btnNegRe && !blnMimicing) {
			blnMimicing = true;
			taskHandler.taskLst().add(tasks.mimic);
			taskHandler.taskData().add(quote[0]);
			taskHandler.unpause();
			
		} else if (e.getSource() == btnClose){
			neuronHandler.sav();
			memoryHandler.sav();
			System.exit(0);
		}
	}
}
