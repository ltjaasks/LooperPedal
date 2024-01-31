package guitarLooper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;

public class GuitarLooper {
	private static AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
	private static TargetDataLine targetLine;
	private static Scanner scanner;

	public static void main(String[] args) {
		System.out.println("Looper ready");
			
		scanner = new Scanner(System.in);
		
		while (true) {
			String input = scanner.nextLine();
			if (input.equals("")) {
				System.out.println("Recording...");
				recordLoop(scanner);
				break;
			}
		}
		scanner.close();
	}
	
	
	public static void playback() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				try {
					
					targetLine = (TargetDataLine) AudioSystem.getLine(info);
					targetLine.open();
					targetLine.start();
					
					
					DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
					SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
					sourceDataLine.open();
					sourceDataLine.start();
					byte[] buffer = new byte[1024];
					while (true) {
						int bytesRead = targetLine.read(buffer, 0, buffer.length);
						sourceDataLine.write(buffer, 0, bytesRead);
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public static void recordLoop(Scanner scanner) {
		// Audio format
		format = new AudioFormat(44100, 16, 2, true, true);
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			targetLine = (TargetDataLine) AudioSystem.getLine(info);
			targetLine.open();
			targetLine.start();
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		// Write audio data to file
		File loopFile = new File("C:\\Users\\Lauri\\Desktop\\guitarLooper\\loop.wav");
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					AudioSystem.write(new AudioInputStream(targetLine), AudioFileFormat.Type.WAVE, loopFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		
		while (true) {
			String input = scanner.nextLine();
			if (input.equals("")) {
				System.out.println("Stopped recording");
				// Stop capturing and close resources
				targetLine.stop();
				targetLine.close();
				playLoop();
				break;
			}
		}
	}
	
	
	public static void playLoop() {
		try {
			playback();
			// Load audio file
			File audioFile = new File("C:\\Users\\Lauri\\Desktop\\guitarLooper\\loop.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			
			// Create a clip and open it
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			
			
			while(true)
			{
			  clip.start();
			  clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
