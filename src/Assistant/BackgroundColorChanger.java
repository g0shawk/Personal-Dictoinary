package Assistant;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.Timer;

//Changes a background color for given time

public class BackgroundColorChanger {
	public static void setBackgroundColor(JTextField field, Color color, int delay) {
		field.setBackground(color);
		new Timer(delay * 1000, new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			  field.setBackground(null);
		     // stop the timer
		     ((Timer) e.getSource()).stop();
		  }
		}).start();
	}
}
