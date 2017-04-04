package Marble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Resort extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String owner; //������
	boolean isOwner = false; //������ ���� ����
	int landCost; //�������Ժ��
	GameBoard gameboard = null;
	Image imgResort = null;
	
	Resort(int index, String name)
	{
		super(index, name, Area.Type.RESORT);
		gameboard = GameBoard.getInstance();
	//x	labelCenter.setForeground(new Color(255, 0, 0));
		imgResort = new ImageIcon("image/resort.gif").getImage();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(imgResort, getWidth()/2-imgResort.getWidth(this)/2, 63, this);
	}
	
	void setLandCost(int landCost)
	{
		this.landCost = landCost;
	}
	
	public void setOwner(String owner, Color color)
	{
		this.isOwner = true;
		this.owner = owner;
		this.color = color;
		labelCenter.setBackground(this.color);
	}
	
	public boolean isOwner()
	{
		return isOwner;
	}
	
	public int getLandCost()
	{
		return landCost;
	}
	
	public boolean waitBuyLand(int money)
	{
		boolean result = false;
		
		if (!isOwner) {
			int choice = JOptionPane.showConfirmDialog(gameboard, "�� ���Ժ��(" + getLandCost() + "��)�� �����Ͻð� �����Ͻðڽ��ϱ�?", name+": �� ����", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				if (money >= getLandCost()) {
					result = true;
				}
				else {
					JOptionPane.showMessageDialog(gameboard, "������ ������ �����մϴ�.", "���ε�", JOptionPane.INFORMATION_MESSAGE);
					result = false;
				}
			}
		}
		
		return result;
	}
	
	public void buyLand(int playerOrder)
	{
		// ������ ����(�ش� Player�� ������ ����ǥ��)
		setOwner(gameboard.player[playerOrder-1].name, gameboard.player[playerOrder-1].color);
	}
	
	public void giveTravelingTax(int playerOrder)
	{
		// ���� �����ֿ��� ���༼ ����
		for (int i=0; i<gameboard.player.length; i++) {
			// Player �ڽ��� ����
			if (i == playerOrder-1) {
				continue;
			}
			
			if (gameboard.player[i].name == owner) {
				gameboard.player[i].money += getTravelingTax();
				break;
			}
		}
	}
}
