package Insertion;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Assistant.BackgroundColorChanger;
import Assistant.ReplacementApostrophe;
import Data.DataManagment;

public class InputWindow {
	// The last two argument is used for the Voice Input
	public static void enter(JFrame frame, String selectedLanguage, int fontSize, JTextField message,
							String foreignExpressionOutput, String translationOutput) {
	boolean isInserted = false;
	if (selectedLanguage == null) {
		JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
				JOptionPane.WARNING_MESSAGE);
	} else {
		String pronounciationAndtranslation = null;
		JTextField textField1 = new JTextField(), textField2 = new JTextField(), textField3 = new JTextField(),
				textField4 = new JTextField();
		textField4.setEditable(false);
		textField1.setToolTipText("If the text in this box exists in your dictionary the "
				+ " fill is red, if it does not exist, the fill is green.");
		JRadioButton jr1 = new JRadioButton("Noun"), jr2 = new JRadioButton("Verb"),
				jr3 = new JRadioButton("Adjective"), jr4 = new JRadioButton("Adverb"),
				jr5 = new JRadioButton("Pronoun"), jr6 = new JRadioButton("Preposition"),
				jr7 = new JRadioButton("Conjunction"), jr8 = new JRadioButton("Irrelevant");

		jr1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[n]");
			}
		});
		jr2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[v]");
			}
		});
		jr3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[adj]");
			}
		});
		jr4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[adv]");
			}
		});
		jr5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[pron]");
			}
		});
		jr6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[prep]");
			}
		});
		jr7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField4.setText("[con]");
			}
		});
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(jr1); bg.add(jr2); bg.add(jr3); bg.add(jr4);
		bg.add(jr5); bg.add(jr6); bg.add(jr7); bg.add(jr8);
		
		Font font = new Font("Arial", Font.PLAIN, fontSize);
		textField1.setFont(font);
		textField2.setFont(font);
		textField3.setFont(font);
		Object[] inputFields = { "Enter foreign expression (mandatory)", textField1,
				"Enter pronounciation (optional)", textField2, "Enter translation (optional)", textField3, jr1,
				jr2, jr3, jr4, jr5, jr6, jr7, jr8 };

		// Searching for duplicate expressions in the textField1
		
		textField1.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				boolean doesDuplicateExist = false;
				
				doesDuplicateExist = DataManagment.exists(ReplacementApostrophe.replaceApostrophe(textField1.getText()), selectedLanguage);
				if (doesDuplicateExist)
					textField1.setBackground(Color.getHSBColor(0f, .5f, 1.0f));
				else
					textField1.setBackground(Color.getHSBColor(.33f, .5f, 1.0f));
			}
		});
		
		if(foreignExpressionOutput != null) {
			textField1.setText(foreignExpressionOutput);
			boolean doesDuplicateExist = false;
			
			doesDuplicateExist = DataManagment.exists(ReplacementApostrophe.replaceApostrophe(textField1.getText()), selectedLanguage);
			if (doesDuplicateExist)
				textField1.setBackground(Color.getHSBColor(0f, .5f, 1.0f));
			else
				textField1.setBackground(Color.getHSBColor(.33f, .5f, 1.0f));
		}
		textField3.setText(translationOutput);

		int option = JOptionPane.showConfirmDialog(frame, inputFields,
				"Experssion, Pronounciation, Translation", JOptionPane.OK_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			String expression = textField1.getText().trim();
			expression = ReplacementApostrophe.replaceApostrophe(expression);
			String kind = textField4.getText() + "  ";
			if (kind.equals("  "))
				kind = "";
			String pronounciation = "\\" + textField2.getText().trim() + "\\" + "  ";
			if (pronounciation.equals("\\\\" + "  "))
				pronounciation = "";
			String translation = textField3.getText().trim();
			pronounciationAndtranslation = ReplacementApostrophe.replaceApostrophe(kind + pronounciation + translation);

			if (textField1.getText().trim().length() == 0) {
				JOptionPane.showMessageDialog(frame, "You have to write some text !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else {
				isInserted = DataManagment.insert(expression, pronounciationAndtranslation, selectedLanguage);
				if (isInserted == true) {
					message.setText("The expression has been successfully inserted !");
					BackgroundColorChanger.setBackgroundColor(message, Color.getHSBColor(.33f, .5f, 1.0f), 1);
				}
					
				else {
					message.setText("Error at Insertion");
					BackgroundColorChanger.setBackgroundColor(message, Color.getHSBColor(0f, .5f, 1.0f), 1);
				}	
			}
		}
	 }
  }
}


