package Marble;

import java.awt.Color;

public class StartPoint extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int paycheck = 30; //√‡«œ±›
	
	StartPoint(int index, String name)
	{
		super(index, name, Area.Type.START_POINT);
		labelTop.setBackground(new Color(200, 254, 173));
		labelCenter.setBackground(new Color(200, 254, 173));
		labelBottom.setBackground(new Color(200, 254, 173));
	}
	
	public int getPaycheck()
	{
		return paycheck;
	}
}
