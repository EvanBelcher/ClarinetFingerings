package com.evanbelcher.ClarinetFingerings;

import java.util.ArrayList;

/**
 * @author Evan Belcher
 */
public class Pitch {
	
	private NoteName note; //The value of the note (ex. Ab)
	private int octave; //The octave value
	private ArrayList<Fingering> fingerings; //List of all Fingerings for this pitch
	private int staffX, staffY; //The x and y values of the pitch on the image allnotes.png
			
	public Pitch(NoteName note, int octave) {
		this.note = note;
		this.octave = octave;
		fingerings = new ArrayList<Fingering>();
	}
	
	public NoteName getNoteName() {
		return note;
	}
	
	public int getOctave() {
		return octave;
	}
	
	public String toString() { //prints an musically interpreted version of the pitch with all enharmonics
		ArrayList<NoteName> enharmonics = new ArrayList<NoteName>();
		for (NoteName name : NoteName.values())
			if (note.pitchVal == name.pitchVal)
				enharmonics.add(name);
		String ret = "";
		for (NoteName name : enharmonics) {
			String noteName = name.toString();
			if (noteName.contains("$")) {
				noteName = noteName.substring(0, noteName.length() - 1);
				noteName += "\u266F";
			} else if (noteName.contains("b")) {
				noteName = noteName.substring(0, noteName.length() - 1);
				noteName += "\u266D";
			} else {
				noteName += "\u266E";
			}
			ret += noteName;
			
			//Special cases for B, B#, Cb, and C
			if (name.equals(NoteName.B$))
				ret += octave - 1;
			else if (name.equals(NoteName.Cb))
				ret += octave + 1;
			else
				ret += octave;
			ret += "/";
		}
		
		return ret.substring(0, ret.length() - 1);
	}
	
	public void addFingering(String keys) {
		fingerings.add(new Fingering(keys));
	}
	
	public ArrayList<Fingering> getFingerings() {
		return fingerings;
	}
	
	public void setStaffCoords(int staffX, int staffY) {
		this.staffX = staffX;
		this.staffY = staffY;
	}
	
	public int getStaffX() {
		return staffX;
	}
	
	public int getStaffY() {
		return staffY;
	}
	
}
