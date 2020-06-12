package Voice;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import Data.DataManagment;
import Insertion.InputWindow;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;

public class GoogleSpeechRecognitionSimple implements Runnable{
	
	private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
	private GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
	
	
	private JFrame frame = new JFrame("Jarvis Speech API");
	private JTextArea response = new JTextArea();
	private final JButton record = new JButton("Record");
	private final JButton insert = new JButton("Insert");
	private  JButton motherLanguage = new JButton("Mother Language");
	private  JScrollPane scrollPane = new JScrollPane();
	
	private JLabel infoText = new JLabel("Press Record button and speak into the connected microphone." , 0);
	
	private JFrame frameParent;
	private String selectedLanguage;
	private String languageCode;
	private int fontSize;
	private JTextField message;
	private String motherLanguageCode;
	private String finalOutput;
	
	public JFrame getFrame() {
		return frame;
	}
	public  GoogleSpeechRecognitionSimple(JFrame frameParent, String selectedLanguage,String languageCode, int fontSize, 
			JTextField message) {
		
		scrollPane.setViewportView(response);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(record)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(insert)
							.addPreferredGap(ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
							.addComponent(motherLanguage))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
						.addComponent(infoText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(infoText, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(insert)
						.addComponent(motherLanguage)
						.addComponent(record))
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		this.frameParent = frameParent;
		this.selectedLanguage = selectedLanguage;
		this.fontSize = fontSize;
		this.message = message;
		this.languageCode = languageCode;
	}
	
	private class Recording implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent evt) {
			new Thread(() -> {
				try {
					duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(frameParent, "An Error has occured !", "Error !",
							JOptionPane.ERROR_MESSAGE);
				}
			}).start();
			response.setText("");
			record.setEnabled(false);
			record.setBackground(Color.getHSBColor(.5f, .5f, 1.0f));
			insert.setEnabled(true);
			infoText.setText("Press Insert button when you want to insert the last text line.");
		}	
	}
	
	private class Insert implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent evt) {
			mic.close();
			duplex.stopSpeechRecognition();
			record.setEnabled(true);
			record.setBackground(null);
			insert.setEnabled(false);
			infoText.setText("Press Record button and speak into the connected microphone.");
			try {
				//if(Translation.translateExpression(motherLanguageCode, finalOutput, frameParent))
				String translated = Translation.translateExpression(motherLanguageCode, finalOutput, frameParent);
				InputWindow.enter(frameParent, selectedLanguage, fontSize, message, finalOutput, translated);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frameParent, "No Text !", "Error !",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();	
			}	
		}	
	}
	
	private class ResponsingListener implements GSpeechResponseListener{
		String old_text = "";
		@Override
		public void onResponse(GoogleResponse gr) {
			String output = "";
			output = gr.getResponse();
			if (gr.getResponse() == null) {
				this.old_text = response.getText();
				if (this.old_text.contains("(")) {
					this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
				}
				//System.out.println("Paragraph Line Added");
				this.old_text = ( response.getText() + "\n" );
				this.old_text = this.old_text.replace(")", "").replace("( ", "");
				response.setText(this.old_text);
				return;
			}
			if (output.contains("(")) {
				output = output.substring(0, output.indexOf('('));
			}
			if (!gr.getOtherPossibleResponses().isEmpty()) {
				output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
			}
			response.append(output + "\n");
			finalOutput = output.trim();
		}
	}
	
	private class DuplexStop implements WindowListener {	// Stops the Recording Thread

		@Override
		public void windowClosing(WindowEvent e) {
				duplex.stopSpeechRecognition();
		}

		@Override
		public void windowOpened(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowDeactivated(WindowEvent e) {}
	
	}
	
	private class MotherLanguageCode implements ActionListener{
		@Override
			public void actionPerformed(ActionEvent e) {
				motherLanguageCode = JOptionPane.showInputDialog(frame, "Insert the ISO 639-1 language code ! ");
		    	  if(DataManagment.insertLangCodeIntoCODE_TABLE(selectedLanguage, motherLanguageCode, 2) == false)
		      	     JOptionPane.showMessageDialog(frame, "Insertion Error !", null, JOptionPane.ERROR_MESSAGE);
			      if (motherLanguageCode == null)
			    	  motherLanguage.setText("Mother Language CODE");
			      
			      else if (motherLanguageCode.length() == 2 && motherLanguageCode.contains(" ") == false)
			    	  motherLanguage.setText(motherLanguageCode);
			      
				  else
					  motherLanguage.setText("Mother Language CODE");
			}
	}
	
	public void run(){
		motherLanguageCode = DataManagment.readCODES(selectedLanguage)[1];
		
		if(motherLanguageCode != null && motherLanguageCode.length() == 2)
		   motherLanguage.setText(motherLanguageCode);
			
		frame.setVisible(true);
		duplex.setLanguage(languageCode);
		frame.setDefaultCloseOperation(2);
		response.setEditable(false);
		response.setWrapStyleWord(true);
		response.setLineWrap(true);
		
		insert.setEnabled(false);
		
		frame.pack();
		frame.setSize(400, 340);
		frame.setLocationRelativeTo(null);
		
		record.addActionListener(new Recording());
		insert.addActionListener(new Insert());
		motherLanguage.addActionListener(new MotherLanguageCode());
		motherLanguage.setToolTipText("The language into which the foreign expression is translated.");
		duplex.addResponseListener(new ResponsingListener());
		
		frame.addWindowListener(new DuplexStop());
	}
}
