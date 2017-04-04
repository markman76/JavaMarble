package Marble;

import java.awt.Color;

import javax.swing.JOptionPane;

public class Desert extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int escapeCost = 20; //무인도 탈출비용
	private boolean isPayEscapeCost = false;
	GameBoard gameboard = null;
	
	Desert(int index, String name)
	{
		super(index, name, Area.Type.DESERT);
		labelTop.setBackground(new Color(200, 254, 173));
		labelCenter.setBackground(new Color(200, 254, 173));
		labelBottom.setBackground(new Color(200, 254, 173));
		
		gameboard = GameBoard.getInstance();
	}
	
	public boolean waitPayEscapeCost()
	{
		int result = JOptionPane.showConfirmDialog(gameboard, "무인도 탈출비용(" + escapeCost + "만)을 지불하시고 탈출하시겠습니까?", "무인도 탈출", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		isPayEscapeCost = (result == JOptionPane.YES_OPTION) ? true : false;
		
		return isPayEscapeCost;
	}
	
	public int getEscapeCost()
	{
		return escapeCost;
	}
}
