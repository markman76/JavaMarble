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
	
	String owner; //소유주
	boolean isOwner = false; //소유주 존재 여부
	int landCost; //토지구입비용
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
			int choice = JOptionPane.showConfirmDialog(gameboard, "땅 구입비용(" + getLandCost() + "만)을 지불하시고 구입하시겠습니까?", name+": 땅 구입", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				if (money >= getLandCost()) {
					result = true;
				}
				else {
					JOptionPane.showMessageDialog(gameboard, "보유한 현금이 부족합니다.", "무인도", JOptionPane.INFORMATION_MESSAGE);
					result = false;
				}
			}
		}
		
		return result;
	}
	
	public void buyLand(int playerOrder)
	{
		// 소유권 설정(해당 Player의 소유권 색상표시)
		setOwner(gameboard.player[playerOrder-1].name, gameboard.player[playerOrder-1].color);
	}
	
	public void giveTravelingTax(int playerOrder)
	{
		// 기존 소유주에게 통행세 지급
		for (int i=0; i<gameboard.player.length; i++) {
			// Player 자신은 제외
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
