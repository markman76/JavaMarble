package Marble;

import java.awt.Color;

import javax.swing.JOptionPane;

public class Desert extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int escapeCost = 20; //���ε� Ż����
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
		int result = JOptionPane.showConfirmDialog(gameboard, "���ε� Ż����(" + escapeCost + "��)�� �����Ͻð� Ż���Ͻðڽ��ϱ�?", "���ε� Ż��", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		isPayEscapeCost = (result == JOptionPane.YES_OPTION) ? true : false;
		
		return isPayEscapeCost;
	}
	
	public int getEscapeCost()
	{
		return escapeCost;
	}
}
