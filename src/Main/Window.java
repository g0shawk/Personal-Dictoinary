package Main;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import Assistant.BackgroundColorChanger;
import Assistant.InternetConnectionChecker;
import Assistant.ReplacementApostrophe;
import Assistant.Restarter;
import Assistant.TableAutoSizer;
import Data.BackupRestoreSave;
import Data.DataManagment;
import Voice.GoogleSpeechRecognitionSimple;
import Voice.Sound;
import Insertion.InputWindow;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Dimension;

public class Window {
	private final String version = "Personal Dictionary v1.2 ";
	private String selectedLanguage = null;
	private JFrame frame;
	private JTextField searchText;
	private JTable table;
	private int row = -1;
	private int column = -1;
	private JTextField message;
	private JScrollPane scrollPane;
	private JComboBox<String> comboBox;
	private JButton clr;
	private JButton restore;
	private JButton save;
	private int fontSize = 12;
	private final Font defaultFont = new Font("Arial", Font.PLAIN, 12);
	private JButton increaseFont;
	private JButton decreaseFont;
	private JButton playButton;
	private JButton microphone;
	private String languageCode;
	private JButton languageCodeBtn;
	private VoiceInputWindow viw = new VoiceInputWindow();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private Window() {
		initialize();
	}

	private void mouseListener() {
		table.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				row = table.getSelectedRow();
				column = table.getSelectedColumn();
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}
		});
	}

	private class SmartSearch extends KeyAdapter {
		public void keyReleased(KeyEvent arg0) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else if (DataManagment.readEntireDictionary(selectedLanguage).getRowCount() == 0) {
				message.setText(selectedLanguage + " is empty.");
			} else {
				row = -1;
				column = -1;
				table = new JTable();
				table = DataManagment.readWantedExpressions(
						ReplacementApostrophe.replaceApostrophe(searchText.getText()), selectedLanguage);
				TableAutoSizer.resizeCellWidthAndHieght(table, JLabel.LEFT, JLabel.TOP, frame, fontSize);
				scrollPane.setViewportView(table);
				message.setText(selectedLanguage);
				mouseListener();
			}
		}
	}

	private class ComboBoxManipulation implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (DataManagment.languageList().size() > 0) {
				selectedLanguage = ((JComboBox<?>) e.getSource()).getSelectedItem().toString();
				message.setText(selectedLanguage);

				languageCode = DataManagment.readCODES(selectedLanguage)[0];

				if (languageCode != null && languageCode.length() == 2)
					languageCodeBtn.setText(languageCode);

				else
					languageCodeBtn.setText("CODE");

			} else {
				selectedLanguage = null;
			}
		}
	}

	private class ReadingDictionary implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else if (DataManagment.readEntireDictionary(selectedLanguage).getRowCount() == 0) {
				message.setText(selectedLanguage + " is empty.");
				scrollPane.setViewportView(new JTable());
			} else {
				row = -1;
				column = -1;
				table = new JTable();
				table.setFont(defaultFont);
				table = DataManagment.readEntireDictionary(selectedLanguage);
				TableAutoSizer.resizeCellWidthAndHieght(table, JLabel.LEFT, JLabel.TOP, frame, fontSize);
				scrollPane.setViewportView(table);
				message.setText(selectedLanguage);
				mouseListener();
			}
		}
	}

	private class InsertionExpression implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			InputWindow.enter(frame, selectedLanguage, fontSize, message, "", "");
		}
	}

	private class DeletionExpression implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else if (selectedLanguage != null && table == null) {
				JOptionPane.showMessageDialog(frame, "Select the row !", "Warning !", JOptionPane.WARNING_MESSAGE);
			} else {
				mouseListener();
				if (row != -1 && column != -1) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					String delExp = model.getValueAt(row, 1).toString();
					Object tran = model.getValueAt(row, 2);
					String delTran = null;

					if (tran != null)
						delTran = tran.toString();

					boolean del = DataManagment.delete_entry(delExp, delTran, selectedLanguage);

					if (del == false) {
						JOptionPane.showMessageDialog(frame, "Deletion Error !", "Error !", JOptionPane.ERROR_MESSAGE);
					} else {
						model.removeRow(row);
						scrollPane.setViewportView(table);
						row = -1;
						column = -1;
						message.setText("The expression has been successfully deleted !");
						BackgroundColorChanger.setBackgroundColor(message, Color.getHSBColor(.33f, .5f, 1.0f), 1);
					}

				} else {
					JOptionPane.showMessageDialog(frame, "Select the row or press Reading Dictiorary button!",
							"Warning !", JOptionPane.WARNING_MESSAGE);
				}

			}
		}
	}

	private class CreatingNewDictionary implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String dictionaryName = JOptionPane.showInputDialog(frame, "Insert the Dictionary Name ! ");
			if (DataManagment.languageList().contains(dictionaryName))
				JOptionPane.showMessageDialog(frame, "This Dictionary has already existed !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			else if (dictionaryName != null && dictionaryName.length() == 0) {
				JOptionPane.showMessageDialog(frame, "You must write new's dictionary name !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else {
				if (dictionaryName != null) {
					if (DataManagment.createTable(dictionaryName)) {
						JOptionPane.showMessageDialog(frame, dictionaryName + " " + "has been created !", null,
								JOptionPane.INFORMATION_MESSAGE);
						comboBox.addItem(dictionaryName);
						BackgroundColorChanger.setBackgroundColor(message, Color.getHSBColor(.33f, .5f, 1.0f), 1);
					} else
						JOptionPane.showMessageDialog(frame, "Error creating dictionary !", null,
								JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class DeletionDictionary implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else {
				String input = "";
				input = JOptionPane.showInputDialog(frame, selectedLanguage + " will be deleted ! \n" + "Type 'yes'...",
						"Warning", JOptionPane.WARNING_MESSAGE);
				if (input != null && input.equals("yes")) {
					String language = selectedLanguage;
					if (DataManagment.dropTable(selectedLanguage) == false) {
						JOptionPane.showMessageDialog(frame, "Deletion Error !", null, JOptionPane.ERROR_MESSAGE);
					} else {
						comboBox.removeItem(selectedLanguage);
						JOptionPane.showMessageDialog(frame, language + " " + "has been deleted !", null,
								JOptionPane.INFORMATION_MESSAGE);
						scrollPane.setViewportView(null);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Deletion canceled !", null, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	private class DeleteAll implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String input = "";
			input = JOptionPane.showInputDialog(frame, "ALL Dictionaries will be deleted ! \n" + "Type 'yes'...",
					"Warning", JOptionPane.WARNING_MESSAGE);
			if (DataManagment.languageList().isEmpty() == true) {
				JOptionPane.showMessageDialog(frame, "Dictionaries List is empty !", null,
						JOptionPane.INFORMATION_MESSAGE);
			} else if (input != null && input.equals("yes")) {
				if (DataManagment.dropAllTables() == false) {
					JOptionPane.showMessageDialog(frame, "Deletion Error !", null, JOptionPane.ERROR_MESSAGE);
				} else {
					comboBox.removeAllItems();
					JOptionPane.showMessageDialog(frame, "All Dictionaries has been deleted !", null,
							JOptionPane.INFORMATION_MESSAGE);
					scrollPane.setViewportView(null);
				}
			} else {
				JOptionPane.showMessageDialog(frame, "Deletion canceled !", null, JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class EditingExpression implements ActionListener {//////
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else if (selectedLanguage != null && table == null) {
				JOptionPane.showMessageDialog(frame, "Select the field !", "Warning !", JOptionPane.WARNING_MESSAGE);
			} else {
				mouseListener();
				if (row != -1 && column > 0) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					String oldColumnn1 = model.getValueAt(row, 1).toString();
					String oldColumnn2 = null;
					if (model.getValueAt(row, 2) != null)
						oldColumnn2 = model.getValueAt(row, 2).toString();
					String new1 = "";
					String new2 = "";
					boolean ins = false;
					boolean del = false;
					JTextField textField = new JTextField();
					textField.setFont(defaultFont);
					if ((oldColumnn2 == null && column == 1) || oldColumnn2 != null)
						textField.setText(model.getValueAt(row, column).toString());
					Object[] inputFields = { "Enter new Expression", textField };
					int option = JOptionPane.showConfirmDialog(frame, inputFields, "Edit Expression",
							JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
						if (column == 1) {
							new1 = ReplacementApostrophe.replaceApostrophe(textField.getText().trim());
							new2 = ReplacementApostrophe.replaceApostrophe(oldColumnn2);
							model.setValueAt(new1, row, column);

						}
						if (column == 2) {
							new1 = ReplacementApostrophe.replaceApostrophe(oldColumnn1);
							new2 = ReplacementApostrophe.replaceApostrophe(textField.getText().trim());
							model.setValueAt(new2, row, column);
						}

						if (textField.getText().trim().length() == 0) {
							JOptionPane.showMessageDialog(frame, "You must write some text !", "Warning !",
									JOptionPane.WARNING_MESSAGE);
						} else {
							del = DataManagment.delete_entry(oldColumnn1, oldColumnn2, selectedLanguage);
							ins = DataManagment.insert(new1, new2, selectedLanguage);
							scrollPane.setViewportView(table);
							if (ins == true && del == true) {
								message.setText("The Expression has been successfully changed !");
								BackgroundColorChanger.setBackgroundColor(message, Color.getHSBColor(.33f, .5f, 1.0f),
										1);
							} else {
								JOptionPane.showMessageDialog(frame, "Editing Error !", "Error !",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					row = -1;
					column = -1;
				} else if (row != -1 && column == 0)
					JOptionPane.showMessageDialog(frame, "You cannot edit the first column !", "Warning !",
							JOptionPane.WARNING_MESSAGE);
				else {
					JOptionPane.showMessageDialog(frame, "Select the field !", "Warning !",
							JOptionPane.WARNING_MESSAGE);
				}

			}
		}
	}

	private class ClearingMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			message.setText("");
			if (row != -1) {
				table.clearSelection();
				row = -1;
				column = -1;
			}
		}
	}

	private class RestoringDatabase implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String input = "";
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("SQLite File", "db"));
			int result = chooser.showOpenDialog(frame);
			if (result == JFileChooser.OPEN_DIALOG) {
				if (chooser.getSelectedFile().isFile() == false)
					JOptionPane.showMessageDialog(frame, "No file chosen !", "Error !", JOptionPane.ERROR_MESSAGE);
				else
					input = JOptionPane.showInputDialog(
							frame, "All the existing Dictionaries will be overwritten ! \n"
									+ "Do you want to overwrite ? \n" + "Type 'yes'...",
							"Warning", JOptionPane.WARNING_MESSAGE);

				if (input != null && input.equals("yes")) {
					if (BackupRestoreSave.restoreBackup(false, chooser, message) == false) {
						JOptionPane.showMessageDialog(frame, "Resrarting Error !", "Error !",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(frame,
								"Restoring has been done ! \n" + "All the existing Dictionaries has been deleted ! \n"
										+ "The Application will be restarted !",
								null, JOptionPane.INFORMATION_MESSAGE);
						Restarter.restartApplication(frame, message);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Restoring canceled !", null, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	private class Backup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("SQLite File", "db"));
			int result = chooser.showSaveDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File checkFile = new File(chooser.getSelectedFile().toString() + ".db");
				if (checkFile.exists()) {
					int option = JOptionPane.showConfirmDialog(frame, "File already exists. Do you want to overwrite ?",
							"Warning", JOptionPane.OK_OPTION, JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						BackupRestoreSave.saveBackup(false, chooser, message);
					}
				} else {
					BackupRestoreSave.saveBackup(false, chooser, message);
				}
			}
		}
	}

	private class Saving implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage != null) {
				table = new JTable();
				table = DataManagment.readEntireDictionary(selectedLanguage);
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Text Document", "txt"));
				int result = chooser.showSaveDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					File checkFile = new File(chooser.getSelectedFile().toString() + ".txt");
					if (checkFile.exists()) {
						int option = JOptionPane.showConfirmDialog(frame,
								"File already exists. Do you want to overwrite ?", "Warning", JOptionPane.OK_OPTION,
								JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							BackupRestoreSave.saveToTXT(chooser, message, table);
						}
					} else {
						BackupRestoreSave.saveToTXT(chooser, message, table);
					}
				}
			} else {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private class IncreasingFont implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (fontSize < 40) {
				++fontSize;
				message.setText("Font Size = " + fontSize);
			} else
				message.setText("Maximum Font Size = " + fontSize);
		}
	}

	private class DecreasingFont implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (fontSize > 10) {
				--fontSize;
				message.setText("Font Size = " + fontSize);
			} else
				message.setText("Minimum Font Size = " + fontSize);
		}
	}

	private class Listening implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectedLanguage == null) {
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			} else if (selectedLanguage != null && table == null) {
				JOptionPane.showMessageDialog(frame, "Select the row !", "Warning !", JOptionPane.WARNING_MESSAGE);
			} else {
				mouseListener();
				if (row != -1) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					String expression = model.getValueAt(row, 1).toString();
					Sound sound = new Sound(frame);
					if (InternetConnectionChecker.netIsAvailable()) {
						if (sound.speak(expression, languageCode) == false)
							message.setText("An Error has occured !");
					} else
						JOptionPane.showMessageDialog(frame, "No Internet Connection !", "Warning !",
								JOptionPane.WARNING_MESSAGE);

					column = -1;
				} else
					JOptionPane.showMessageDialog(frame, "Select the row !", "Warning !", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private class VoiceInput implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// System.out.println("Thread " + Thread.activeCount());

			if (viw.getSpeechRecognition() != null) {
				if (viw.getSpeechRecognition().getFrame().isShowing()) {

					// Must be in front when is clicked
					viw.getSpeechRecognition().getFrame().toFront();
				} else {
					viw.voiceInputRunning(frame, selectedLanguage, languageCode, fontSize, message);
				}
			}

			else if (selectedLanguage == null)
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);

			else if (languageCode == null || languageCode.equals("null")) {
				languageCode = JOptionPane.showInputDialog(frame,
						"Insert the language code according\n" + "to the ISO 639-1 standard");
				if (languageCode != null) {
					if (languageCode.length() == 2 && languageCode.contains(" ") == false)
						languageCodeBtn.setText(languageCode);
					else
						languageCodeBtn.setText("CODE");
				}
			}

			else {
				viw.setSpeechRecognition(
						new GoogleSpeechRecognitionSimple(frame, selectedLanguage, languageCode, fontSize, message));
				viw.setVoiceWindowThread(new Thread(viw.getSpeechRecognition()));

				if (InternetConnectionChecker.netIsAvailable()) {
					viw.getVoiceWindowThread().start();
				} else
					JOptionPane.showMessageDialog(frame, "No Internet Connection !", "Warning !",
							JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private class LanguageCode implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (selectedLanguage == null)
				JOptionPane.showMessageDialog(frame, "Choose the Dictionary !", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			else {
				languageCode = JOptionPane.showInputDialog(frame, "Insert the ISO 639-1 language code ! ");
				if (DataManagment.insertLangCodeIntoCODE_TABLE(selectedLanguage, languageCode, 1) == false)
					JOptionPane.showMessageDialog(frame, "Insertion Error !", null, JOptionPane.ERROR_MESSAGE);
			}

			if (languageCode == null)
				languageCodeBtn.setText("CODE");

			else if (languageCode.length() == 2 && languageCode.contains(" ") == false)
				languageCodeBtn.setText(languageCode);
			else
				languageCodeBtn.setText("CODE");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// JFrame initializing
		frame = new JFrame(version);
		frame.setBounds(100, 100, 818, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// JTextField initializing
		message = new JTextField();
		message.setFont(new Font("Tahoma", Font.PLAIN, 11));
		message.setMinimumSize(new Dimension(300, 15));
		message.setToolTipText("Messages");
		message.setEditable(false);
		message.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// JPanel initializing
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// JScrollPane initializing
		scrollPane = new JScrollPane();

		// Searching expression
		searchText = new JTextField();
		searchText.setToolTipText("Type for search");
		searchText.addKeyListener(new SmartSearch());
		searchText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		searchText.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// ComboBox
		comboBox = new JComboBox<String>();
		comboBox.setAlignmentY(Component.TOP_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setToolTipText("Select Language");
		for (String str : DataManagment.languageList())
			comboBox.addItem(str);
		comboBox.addActionListener(new ComboBoxManipulation());

		// Reading Dictionary
		JButton readDictionary = new JButton("Read Entire Dictionary");
		readDictionary.addActionListener(new ReadingDictionary());

		// Inserting expression
		JButton insertExpression = new JButton("Insert Expression");
		insertExpression.addActionListener(new InsertionExpression());

		// Deletion expression
		JButton deleteExpression = new JButton("Delete Expression");
		deleteExpression.addActionListener(new DeletionExpression());

		// Creating Expression
		JButton createDictionary = new JButton("Create Dictionary");
		createDictionary.addActionListener(new CreatingNewDictionary());

		// Deleting Expression
		JButton delete = new JButton("Delete Dictionary");
		delete.addActionListener(new DeletionDictionary());

		// Deleting all dictionaries
		JButton deleteAllDictionaries = new JButton("Delete All Dictionaries");
		deleteAllDictionaries.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		deleteAllDictionaries.addActionListener(new DeleteAll());

		// Editing Expression
		JButton editExpression = new JButton("Edit Expression");
		editExpression.addActionListener(new EditingExpression());

		// Clearing Message
		clr = new JButton("Clear");
		clr.setAlignmentX(Component.CENTER_ALIGNMENT);
		clr.setToolTipText("Clears the Message window and deselects the Table.");
		clr.addActionListener(new ClearingMessage());

		// Restoring Database
		restore = new JButton("Restore");
		restore.setToolTipText(
				"Be carefull ! Restores from your Backup Data. The existing database will be overwritten.");
		restore.setAlignmentX(Component.RIGHT_ALIGNMENT);
		restore.addActionListener(new RestoringDatabase());

		// Backup Database
		JButton backup = new JButton("Backup");
		backup.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		backup.setAlignmentX(Component.RIGHT_ALIGNMENT);
		backup.setToolTipText(
				"Makes a database file copy.  (All of the Dictionaries data is stored in this database.) ");
		backup.addActionListener(new Backup());

		// Saving to .txt
		save = new JButton("Save to .txt");
		save.setToolTipText("Saves choosen language to .txt file");
		save.addActionListener(new Saving());

		// Changing font size
		increaseFont = new JButton("+");
		increaseFont.setToolTipText("Increase font");
		increaseFont.addActionListener(new IncreasingFont());
		decreaseFont = new JButton("-");
		decreaseFont.setToolTipText("Decrease font");
		decreaseFont.addActionListener(new DecreasingFont());

		// Play
		playButton = new JButton("");
		playButton.setToolTipText("Play Button");
		playButton.setIcon(new ImageIcon(".\\Icons\\iconfinder_icon-ios7-play_211801.png"));
		playButton.addActionListener(new Listening());

		// Microphone
		microphone = new JButton("");
		microphone.setToolTipText("Voice Input");
		microphone.setIcon(new ImageIcon(".\\Icons\\radio.png"));
		microphone.addActionListener(new VoiceInput());

		// Language Indicator Button
		languageCodeBtn = new JButton();
		languageCodeBtn.setText("CODE");
		languageCodeBtn.setMinimumSize(new Dimension(40, 15));
		languageCodeBtn.setToolTipText("The Language Code Indicator Button");
		languageCodeBtn.addActionListener(new LanguageCode());

		// Layout Settings - automatically
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(scrollPane,
				GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(scrollPane,
				GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE));

		panel.setLayout(gl_panel);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(10)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(deleteAllDictionaries, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(delete, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(createDictionary, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(comboBox, Alignment.TRAILING, 0, 139, Short.MAX_VALUE)
						.addComponent(searchText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
						.addComponent(readDictionary, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(insertExpression, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 139,
								Short.MAX_VALUE)
						.addComponent(deleteExpression, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 139,
								Short.MAX_VALUE)
						.addComponent(editExpression, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 139,
								Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup().addComponent(increaseFont)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(decreaseFont))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(playButton, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(microphone, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
						.createSequentialGroup().addGap(6)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
								.createSequentialGroup().addComponent(clr).addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(message, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(languageCodeBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(save, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
								.addComponent(panel, GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(restore, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(backup, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 101,
												Short.MAX_VALUE))))
				.addContainerGap()));
		groupLayout
				.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(11).addGroup(groupLayout
								.createParallelGroup(
										Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup().addGap(2)
										.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addGap(73)
										.addComponent(searchText, GroupLayout.PREFERRED_SIZE, 23,
												GroupLayout.PREFERRED_SIZE)
										.addGap(59).addComponent(readDictionary).addGap(14)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(playButton, GroupLayout.PREFERRED_SIZE, 36,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(microphone, GroupLayout.PREFERRED_SIZE, 36,
														GroupLayout.PREFERRED_SIZE))
										.addGap(37).addComponent(insertExpression).addGap(6)
										.addComponent(deleteExpression).addGap(6).addComponent(editExpression)
										.addGap(27)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(increaseFont).addComponent(decreaseFont)))
								.addComponent(panel, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)).addGap(6)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(createDictionary, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(backup, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(delete, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(restore, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE))
								.addGap(7)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(deleteAllDictionaries, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(clr, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
										.addComponent(message, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(save, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
										.addComponent(languageCodeBtn, GroupLayout.PREFERRED_SIZE, 15,
												GroupLayout.PREFERRED_SIZE))
								.addGap(10)));
		frame.getContentPane().setLayout(groupLayout);
	}
}