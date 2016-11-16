package com.evanbelcher.ClarinetFingerings;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.miginfocom.swing.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.*;

/**
 * @author Evan Belcher
 */
public class Display extends JPanel {
	
	private static final long serialVersionUID = 1L; //for serializability
	
	private static JFrame frame; //frame or window holding the body of the application
	private static Container pane; //Main window's inner pane
	
	private static final String CLARINET_IMAGE_PATH = "src/clarinet.png"; //relative path to image of clarinet
	private static BufferedImage clarImage; //clarinet image object
	private static double horizontalScale = 5.0 / 8.0; //"preferred" horizontal scalar of clarinet image. not a constant.
	private static double verticalScale = 1; //"preferred" vertical scalar of clarinet image. not a constant.
	
	private static final String STAFF_IMAGE_PATH = "src/allnotes.png"; //relative path to image of all notes in clarinet range
	private static BufferedImage staffImage; //staff image object
	private static final int STAFF_SIZE_X = 170, STAFF_SIZE_Y = 129; //width and height of the staff to be displayed
			
	private static final String FINGERING_FILE_PATH = "src/AllFingerings"; //relative path to text file containing all fingerings in parsable format
	private static int currentFingering = 0; //the current fingering that the program is displaying 
	
	private static ArrayList<Pitch> pitches = new ArrayList<Pitch>(); //List containing all pitches possible on the clarinet
	private static int currentPitch = 0; //the current pitch that the program is displaying
	
	private static JButton up = new JButton("\u2191"); //up pitch button. ascii up arrow
	private static JButton down = new JButton("\u2193"); //down pitch button. ascii down arrow
	private static JButton left = new JButton("\u2190"); //previous alternate fingering button. ascii left arrow
	private static JButton right = new JButton("\u2192"); //next alternate fingering button. ascii right arrow
	private static JLabel noteLabel = new JLabel("a"); //Displays the current note
	private static JLabel fingeringLabel = new JLabel("b"); //Displays the current fingering number
	
	private static final String songPath = "src/rib.mp3"; //path to Rhapsody in Blue mp3 file
	private static MediaPlayer mediaPlayer;
	private static JButton mute = new JButton("Mute Rhapsody in Blue");
	
	private static JLabel name = new JLabel("<html><div style=\"text-align: center;\">Made by Evan Belcher<br/>2015</div></html>", SwingConstants.CENTER);
	
	public static void main(String[] args) throws InterruptedException {
		//sets up pitches with all fingerings from AllFingerings[.txt]
		getPitchesFromFile();
		
		//initialize the frame
		frame = new JFrame("Clarinet Fingerings - Evan Belcher");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Display main = new Display();
		main.setFocusable(true);
		
		//add graphics to frame
		pane = frame.getContentPane();
		pane.setLayout(new MigLayout());
		float[] hsb = Color.RGBtoHSB(217, 235, 190, new float[]{84,19,92});
		pane.setBackground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		main.setSize(375, 900);
		main.setPreferredSize(new Dimension(375, 900));
		main.setMinimumSize(new Dimension(375, 900));
		main.setMaximumSize(new Dimension(375, 900));
		
		//create buttons
		createButtons();
		
		//add all components to pane using MigLayout
		pane.setLayout(new MigLayout());
		pane.add(main, "span 2 9");
		pane.add(new JLabel("Up half step"));
		pane.add(up, "wrap");
		pane.add(new JLabel("Down half step"));
		pane.add(down, "wrap");
		pane.add(new JLabel("Last alternate fingering"));
		pane.add(left, "wrap");
		pane.add(new JLabel("Next alternate fingering"));
		pane.add(right, "wrap");
		pane.add(noteLabel, "wrap");
		pane.add(fingeringLabel, "wrap");
		mute.setVisible(false);
		pane.add(mute, "span 2, wrap, align center");
		name.setVisible(false);
		pane.add(name, "span 2 1, align center");
		
		//set frame parameters
		frame.pack();
		frame.setResizable(false);
		
		//thread-safety pause
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//get and set up clarinet image
		int imageX = 0;
		int imageY = 0;
		try {
			clarImage = ImageIO.read(new File(CLARINET_IMAGE_PATH));
			imageX = clarImage.getWidth();
			imageY = clarImage.getHeight();
			clarImage = getScaledInstance(clarImage, (int) (pane.getWidth() * horizontalScale), (int) (pane.getHeight() * verticalScale), RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		//reset horizontal and vertical scale factors to be exactly accurate
		horizontalScale = clarImage.getWidth() / ((double) imageX);
		verticalScale = clarImage.getHeight() / ((double) imageY);
		
		//get staff image
		try {
			staffImage = ImageIO.read(new File(STAFF_IMAGE_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		initializePitchStaff();
		
		//thread-safety pause
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		//plays Rhapsody in Blue
		playRhapsody();
		
		Thread.sleep(6500);
		
		//set frame to visible
		frame.setVisible(true);
		
		int pseudoTimer = 7600 - 6500; //1050
		final int stopper = (int)((1.0 - 0.25) * -1 * 2 * 100); //-160
		//game loop - infinite
		while (true) {
			try {
				main.repaint(); //call paint() method for graphics
				Thread.sleep(10); //Set framerate - 1/100 second
				pseudoTimer--;
				if(pseudoTimer == 750)
					name.setVisible(true);
				if (pseudoTimer <= 0 && pseudoTimer > stopper){ //manual fadeout implementation
					mediaPlayer.volumeProperty().set(mediaPlayer.getVolume() - 0.005);
					mute.setVisible(true);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private static void getPitchesFromFile() { //retrieve pitches and fingerings from file
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(FINGERING_FILE_PATH)));
			String s;
			Pitch p = null;
			
			while ((s = br.readLine()) != null) {
				if (s.charAt(0) == '@') { //pitch names begin with "@"
					s = s.substring(1);
					if (p != null)
						pitches.add(p);
					if (s.length() == 2)
						p = new Pitch(NoteName.valueOf(s.substring(0, 1)), Integer.parseInt(s.substring(1))); // ex. E3
					else
						p = new Pitch(NoteName.valueOf(s.substring(0, 2)), Integer.parseInt(s.substring(2))); // ex. Eb3
				} else {
					p.addFingering(s); //add fingering to pitch. Pitch constructor handles parsing
				}
			}
			pitches.add(p);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createButtons() { //set up buttons
		up.addActionListener(new ActionListener() { //move pitch up half step
		
			@Override
			public void actionPerformed(ActionEvent e) {
				currentFingering = 0;
				currentPitch++;
				left.setEnabled(false);
				if (pitches.get(currentPitch).getFingerings().size() > 1)
					right.setEnabled(true);
				else
					right.setEnabled(false);
				if (currentPitch == pitches.size() - 1)
					up.setEnabled(false);
				if (currentPitch == 1)
					down.setEnabled(true);
			}
			
		});
		down.addActionListener(new ActionListener() { //move pitch down half step
		
			@Override
			public void actionPerformed(ActionEvent e) {
				currentFingering = 0;
				currentPitch--;
				left.setEnabled(false);
				if (pitches.get(currentPitch).getFingerings().size() > 1)
					right.setEnabled(true);
				else
					right.setEnabled(false);
				if (currentPitch == 0)
					down.setEnabled(false);
				if (currentPitch == pitches.size() - 2)
					up.setEnabled(true);
			}
			
		});
		down.setEnabled(false);
		
		left.addActionListener(new ActionListener() { //change to previous fingering
		
			@Override
			public void actionPerformed(ActionEvent e) {
				currentFingering--;
				if (currentFingering == 0)
					left.setEnabled(false);
				if (currentFingering == pitches.get(currentPitch).getFingerings().size() - 2)
					right.setEnabled(true);
			}
			
		});
		left.setEnabled(false);
		
		right.addActionListener(new ActionListener() { //change to next fingering
		
			@Override
			public void actionPerformed(ActionEvent e) {
				currentFingering++;
				if (currentFingering == pitches.get(currentPitch).getFingerings().size() - 1)
					right.setEnabled(false);
				if (currentFingering == 1)
					left.setEnabled(true);
			}
			
		});
		
		mute.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(mediaPlayer.isMute())
					mediaPlayer.muteProperty().set(false);
				else
					mediaPlayer.muteProperty().set(true);
			}
			
		});
	}
	
	public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) { //scale image well
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}
		
		if (img.getWidth() < targetWidth)
			w = targetWidth;
		if (img.getHeight() < targetHeight)
			h = targetHeight;
		
		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}
			
			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}
			
			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();
			
			ret = tmp;
		} while (w > targetWidth || h > targetHeight);
		
		return ret;
	}
	
	public static void initializePitchStaff() { //Gives each picth in pitches an x and y value for its note in allnotes.png
		int x = 0, y = 0;
		int xCount = 0;
		for (Pitch p : pitches) {
			p.setStaffCoords(x, y);
			xCount++;
			if (xCount == 3 || xCount == 5 || xCount == 7 || xCount == 10 || xCount == 12) //enharmonics are wider
				x += 179;
			else
				x += 157;
			if (xCount == 12) {
				x = 0;
				y += 240;
				xCount = 0;
			}
		}
	}
	
	public static void playRhapsody() {
		new JFXPanel();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Media media = new Media(new File(songPath).toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
				mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
				mediaPlayer.play();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) { //graphics code
		g.clearRect(0, 0, frame.getWidth(), frame.getHeight()); //refreshes pane
		g.setColor(Color.RED);
		g.drawImage(clarImage, 0, 0, null); //draws image of clarinet
		
		Pitch p = pitches.get(currentPitch);
		
		//Prints all keys in fingering
		for (Key k : p.getFingerings().get(currentFingering).getKeys())
			try {
				g.setColor(k.dispColor);
				g.fillOval((int) (k.startX * horizontalScale), (int) (k.startY * verticalScale), (int) (k.width * horizontalScale), (int) (k.height * verticalScale));
			} catch (NullPointerException e) { //in case of unknown fingering letter
				e.printStackTrace();
			}
		
		//Updates labels
		noteLabel.setText(p.toString());
		fingeringLabel.setText("Fingering " + (currentFingering + 1) + "/" + p.getFingerings().size());
		
		/*
		 * Draws specific part of the image allnotes.png that correlates to the current pitch
		 * Cropping is done with BufferedImage.getSubimage(int x, int y, int w, int h);
		 */
		g.drawImage(staffImage.getSubimage(p.getStaffX(), p.getStaffY(), STAFF_SIZE_X, STAFF_SIZE_Y), (int) (130 * horizontalScale), (int) (140 * verticalScale), (int) (110 * horizontalScale), 100, null);
	}
	
}
