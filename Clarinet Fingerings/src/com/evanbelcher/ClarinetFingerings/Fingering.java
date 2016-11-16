package com.evanbelcher.ClarinetFingerings;

import java.util.HashMap;

/**
 * @author Evan Belcher
 */
public class Fingering {
	
	private Key[] keys; //array of all keys in the fingering
	
	private static HashMap<String, Key> translator;
	
	public Fingering(String keys) {
		this.keys = parseKeys(keys);
	}
	
	private Key[] parseKeys(String keys) { //parses letters into corresponding keys. fairly inefficient as it recreates the translator every time
		Key[] theKeys = new Key[keys.length()];
		if (translator == null) {
			translator = new HashMap<String, Key>() { //dictionary of strings (characters) => Keys
			
				private static final long serialVersionUID = 1L;
				
				{
					for (int i = 0; i < Key.values().length; i++) {
						char c = (char) (97 + i); //(char) 97 = a, 98 = b, and so on
						put(String.valueOf(c), Key.values()[i]);
					}
				}
			};
		}
		for (int i = 0; i < keys.length(); i++) {
			String s = keys.substring(i, i + 1);
			theKeys[i] = translator.get(s);
		}
		return theKeys;
	}
	
	public Key[] getKeys() {
		return keys;
	}
	
}
