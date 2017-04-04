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
	protected String name; //������
	final Type type; //�������� {1:���� , 2:�޾���, 3:�����, 4:���ε�, 5:�ø���, 6:���迩��, 7:����û}
	protected Color color = null; // ��������
	protected int travelingTax = 0; //���༼
	
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
	
	//�����̸� 
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	//���༼
	public int getTravelingTax()
	{
		return travelingTax;
	}
	
	public void setTravelingTax(int travelingTax)
	{
		this.travelingTax = travelingTax;
		setToolTipText("�����:" + getTravelingTax() + "��");
	}
	
	public void increaseTravelingTax(int travelingTax)
	{
		this.travelingTax += travelingTax;
		setToolTipText("�����:" + getTravelingTax() + "��");
	}
	
	public void waitNoticeChargeTravelingTax()
	{
		JOptionPane.showMessageDialog(gameboard, "���༼(" + travelingTax + "��)�� �����ؾ� �մϴ�.", name+": ���༼", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/*
	//���� �湮�ڼ�
	public int getCurrentVisitor()
	{
		return currentVisitorCount;
	}
	*/
}
