package Main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Assistant.InternetConnectionChecker;
import Voice.GoogleSpeechRecognitionSimple;

public class VoiceInputWindow {
	
	private GoogleSpeechRecognitionSimple speechRecognition;
	private Thread voiceWindowThread;
	
	public GoogleSpeechRecognitionSimple getSpeechRecognition() {
		return speechRecognition;
	}

	public void setSpeechRecognition(GoogleSpeechRecognitionSimple speechRecognition) {
		this.speechRecognition = speechRecognition;
	}

	public Thread getVoiceWindowThread() {
		return voiceWindowThread;
	}

	public void setVoiceWindowThread(Thread voiceWindowThread) {
		this.voiceWindowThread = voiceWindowThread;
	}
	
	public void voiceInputRunning(JFrame frame, String selectedLanguage, String languageCode, int fontSize,
								  JTextField message) {
		try {
			speechRecognition = new GoogleSpeechRecognitionSimple(frame, selectedLanguage, languageCode,
					fontSize, message);
			voiceWindowThread = new Thread(speechRecognition);

		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(frame, "An error has occured !", "Error !",
					  JOptionPane.ERROR_MESSAGE);
		}

		if (InternetConnectionChecker.netIsAvailable()) {
			voiceWindowThread.start();
		} else
			JOptionPane.showMessageDialog(frame, "No Internet Connection !", "Warning !",
					JOptionPane.WARNING_MESSAGE);
	}

}
