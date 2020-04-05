package Data;
// BRS means Backup, Restore and Save

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextField;

import Assistant.BackgroundColorChanger;

public class BackupRestoreSave {
	public static void saveBackup(boolean b, JFileChooser chooser, JTextField mes) {
		try {
    		FileChannel in = null, out = null;
    		File f = new File("");
			FileInputStream fileInputStream = new FileInputStream(f.getAbsolutePath() + "\\Dictionaries.db");  // Relative path to \Dictionaries.db
			in = fileInputStream.getChannel();				
			FileOutputStream fileOutputStream = new FileOutputStream(chooser.getSelectedFile().toString() + ".db");
			out = fileOutputStream.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
				while (in.read(buffer) != -1) {
					buffer.flip(); // Prepare for writing
					out.write(buffer);
					buffer.clear(); // Prepare for reading
				}
				fileInputStream.close();	
				fileOutputStream.close();
				in.close();
				out.close();
		} catch (IOException e0) {
			b = true;
			mes.setText("WARNING ! Error saving file !");
			BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(0f, .5f, 1.0f), 1);
			e0.printStackTrace();
			}
    	if (b == false) {
    		mes.setText("Backup has been saved !");
    		BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(.33f, .5f, 1.0f), 1);
    	}
	}
	public static boolean restoreBackup(boolean b, JFileChooser chooser, JTextField mes) {
		try {
    		FileChannel in = null, out = null;
    		File f = new File("");
    		FileInputStream fileInputStream = null ;
    		if(chooser.getSelectedFile().isFile() == false) {
    			return b;
    		}
    		else {
    			fileInputStream = new FileInputStream(chooser.getSelectedFile());
    			in = fileInputStream.getChannel();
    		}
    							
			FileOutputStream fileOutputStream = new FileOutputStream(f.getAbsolutePath() + "\\Dictionaries.db");  // Relative path to \Dictionaries.db
			out = fileOutputStream.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
				while (in.read(buffer) != -1) {
					buffer.flip(); // Prepare for writing
					out.write(buffer);
					buffer.clear(); // Prepare for reading
				}
				fileInputStream.close();	
				fileOutputStream.close();
				in.close();
				out.close();
				b = true;
		} catch (IOException e0) {
			b = false;
			mes.setText("WARNING ! Restoring Error !");
			BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(0f, .5f, 1.0f), 1);
			e0.printStackTrace();
			}
    	mes.setText("Backup has been restored !");
    	BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(.33f, .5f, 1.0f), 1);
    	return b;
	}
	
	
	public static void saveToTXT(JFileChooser chooser, JTextField mes, JTable tbl) {
		{
        	File file = new File(chooser.getSelectedFile().toString() + ".txt");
        	try {
        		 file.createNewFile();
        		 Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), "UTF8"));
		         for(int i = 0; i < tbl.getRowCount(); i++) {
		        	 if(i != 0)
		        	 bw.write("\n");
		        	for(int j = 0; j < tbl.getColumnCount(); j++) {
		        		bw.write(tbl.getModel().getValueAt(i, j) + "  ");
		        	}
		         }
		         bw.close();
		         mes.setText(".txt file has been created !");
		         BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(.33f, .5f, 1.0f), 1);
			} catch (IOException e1) {
				mes.setText("WARNING ! Error creating file !");
				BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(0f, .5f, 1.0f), 1);
				e1.printStackTrace();
			}	
        }
	}
}
