package Assistant;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Main.Window;

public class Restarter {
	public static void restartApplication(JFrame fr, JTextField mes) {
	    final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
	    File currentJar = null;
	    try {
	    	currentJar = new File(Window.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	    	} catch (URISyntaxException e) {
	    		JOptionPane.showInternalMessageDialog(fr, "Resrarting Error !", "Error !",
						JOptionPane.ERROR_MESSAGE);
	    	}

	    /* is it a jar file? */
	    if(!currentJar.getName().endsWith(".jar"))
	    	return;

	    /* Build command: java -jar application.jar */
	    final ArrayList<String> command = new ArrayList<String>();
	    command.add(javaBin);
	    command.add("-jar");
	    command.add(currentJar.getPath());
	    final ProcessBuilder builder = new ProcessBuilder(command);
	    try {
	    	builder.start();
	    } catch (IOException e) {
	    	mes.setText("WARNING ! Error restarting file !");
	    	BackgroundColorChanger.setBackgroundColor(mes, Color.getHSBColor(0f, .5f, 1.0f), 1);
	    	e.printStackTrace();
	    }
	    System.exit(0);
	}
}
