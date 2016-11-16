package com.evanbelcher.ClarinetFingerings;

import java.awt.Color;

/**
 * @author Evan Belcher
 */
public enum Key {
	/*
	 * Every key on the clarinet with starting point and size on the image passed in
	 * Coordinates and sizes were retrieved manually in Paint.NET
	 */
	THUMB(262, 406, 29, 29, Color.RED), REGISTER(264, 365, 24, 40, Color.RED), LHA(73, 384, 20, 36, Color.RED), LHAb(103, 391, 14, 43, Color.RED), LH1(69, 428, 22, 23, Color.RED), LH2(71, 484, 24, 26, Color.RED), LHSLIVER(82, 515, 19, 11, Color.RED), LH3(74, 531, 16, 16, Color.RED), LHC$(93, 557, 25, 14, Color.RED), LHS1(25, 595, 17, 11, Color.RED), LHS2(29, 580, 12, 12, Color.RED), LHS3(22, 567, 13, 13, Color.RED), LHS4(22, 554, 13, 13, Color.RED), RH1(72, 632, 24, 28, Color.BLUE), RH2(71, 681, 24, 26, Color.BLUE), RHSLIVER(77, 711, 15, 7, Color.BLUE), RH3(70, 720, 28, 28, Color.BLUE), RHS1(109, 571, 24, 16, Color.BLUE), RHS2(117, 589, 11, 28, Color.BLUE), RHTL(22, 754, 24, 17, Color.BLUE), RHTR(50, 756, 26, 20, Color.BLUE), RHBL(19, 780, 26, 17, Color.BLUE), RHBR(51, 784, 30, 16, Color.BLUE), OPEN(-10, -10, 0, 0, Color.BLUE);
	
	//automatically public, non-static variables
	int startX;
	int startY;
	int width;
	int height;
	Color dispColor;
	
	Key(int startX, int startY, int width, int height, Color dispColor) {
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.dispColor = dispColor;
	}
}
