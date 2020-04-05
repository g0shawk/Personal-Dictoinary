package Voice;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.darkprograms.speech.translator.GoogleTranslate;

public class Translation {
		
	public static String translateExpression(String motherLanguageCode, String expression, JFrame frameParent) {
		try {
			String translation = GoogleTranslate.translate(motherLanguageCode, expression);
			return translation;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frameParent, "An Error has occured !", "Error !",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
		
}
