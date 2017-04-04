package Marble;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Area extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum Type {CITY, RESORT, START_POINT, DESERT, OLYMPIC, WORLD_TRAVEL, TAX_SERVICE}
	
	protected int index;
	protected String name; //영역명
	final Type type; //영역유형 {1:도시 , 2:휴양지, 3:출발점, 4:무인도, 5:올림픽, 6:세계여행, 7:국세청}
	protected Color color = null; // 영역색상
	protected int travelingTax = 0; //통행세
	
	private ArrayList<Color> playerColorList;
	private boolean[] isExistPlayer = new boolean[4];
	
	JLabel labelTop = null;
	JLabel labelCenter = null;
	JLabel labelBottom = null;
	GameBoard gameboard = null;
	
	Area(int index, String name, Type type)
	{
		//super(name, JLabel.CENTER);
		this.index = index;
		this.name = name;
		this.type = type;
		gameboard = GameBoard.getInstance();
		
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		setPreferredSize(new Dimension(54, 20));
		setOpaque(true);
		setLayout(new GridLayout(3, 1));
		
		labelTop = new JLabel();
		labelTop.setOpaque(true);
		labelTop.setBackground(new Color(255, 255, 255));
		add(labelTop);
		labelCenter = new JLabel(name, JLabel.CENTER);
		labelCenter.setOpaque(true);
		labelCenter.setBackground(new Color(255, 255, 255));
		add(labelCenter);
		labelBottom = new JLabel();
		labelBottom.setOpaque(true);
		add(labelBottom);
		labelBottom.setBackground(new Color(255, 255, 255));
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		for (int i=0; i<playerColorList.size(); i++) {
			if (isExistPlayer[i]) {
				g.setColor(playerColorList.get(i));
				g.fillOval(3+20*i,8, 14,14);
				g.setColor(Color.DARK_GRAY);
				g.drawOval(3+20*i,8, 14,14);
			}
		}
	}
	
	public void setPlayerColorList(ArrayList<Color> colorList)
	{
		playerColorList = colorList;
	}
	
	public void setExistPlayer(int order, boolean isExist)
	{
		isExistPlayer[order-1] = isExist;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	//도시이름 
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	//통행세
	public int getTravelingTax()
	{
		return travelingTax;
	}
	
	public void setTravelingTax(int travelingTax)
	{
		this.travelingTax = travelingTax;
		setToolTipText("통행료:" + getTravelingTax() + "만");
	}
	
	public void increaseTravelingTax(int travelingTax)
	{
		this.travelingTax += travelingTax;
		setToolTipText("통행료:" + getTravelingTax() + "만");
	}
	
	public void waitNoticeChargeTravelingTax()
	{
		JOptionPane.showMessageDialog(gameboard, "통행세(" + travelingTax + "만)를 납부해야 합니다.", name+": 통행세", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/*
	//현재 방문자수
	public int getCurrentVisitor()
	{
		return currentVisitorCount;
	}
	*/
}
