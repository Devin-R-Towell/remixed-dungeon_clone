package com.nyrds.util;

public class Scrambler {
	static private int k = 0xAAAAAAAA;

	
	static public int scramble(int in) {
		return in  ^ k ^ UserKey.someValue();
	}
	
	static public int descramble(int in) {
		return in ^ UserKey.someValue() ^ k;
	}
	
}
