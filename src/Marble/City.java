package Marble;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class City extends Area 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String owner; //소유주
	boolean isOwner; //소유주 존재 여부
	
	private int landCost; //토지구입비용
	private int villaCost;//빌라건설비용
	private int buildingCost; //빌딩건설비용
	private int hotelCost; //호텔건설비용
	private int landmarkCost; //랜드마크건설비용
	private int totalConstructionCost = 0;
	
	private boolean isExistVilla; //빌라여부
	private boolean isExistBuilding; //빌딩여부
	private boolean isExistHotel; //호텔여부
	private boolean isExistLandmark; //랜드마크여부
	private boolean isCurrentConstructVilla; //빌라여부
	private boolean isCurrentConstructBuilding; //빌딩여부
	private boolean isCurrentConstructHotel; //호텔여부
	private boolean isCurrentConstructLandmark; //랜드마크여부
	int hostedOlympicCount = 0; // 올림픽개최횟수
	
	JDialog dialogBuildStructure = null;
	JButton buttonOk = null;
	JCheckBox checkConstruction1 = null;
	JCheckBox checkConstruction2 = null;
	JCheckBox checkConstruction3 = null;
	JCheckBox checkConstruction4 = null;
	JLabel labelConstructionCost1 = null;
	JLabel labelConstructionCost2 = null;
	JLabel labelConstructionCost3 = null;
	JLabel labelConstructionCost4 = null;
	JLabel labelConstructionTotalCost = null;
	JLabel labelTravelingTax = null;
	GameBoard gameboard = null;
	
	Image imgVilla = null;
	Image imgBuilding = null;
	Image imgHotel = null;
	Image imgLandmark = null;
	
	class ConstructionEventHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent e) {
			totalConstructionCost = 0;
			if (checkConstruction1.isEnabled() && checkConstruction1.isSelected()) {
				totalConstructionCost += villaCost;
				isCurrentConstructVilla = true;
			}
			else {
				isCurrentConstructVilla = false;
			}
			
			if (checkConstruction2.isEnabled() && checkConstruction2.isSelected()) {
				totalConstructionCost += buildingCost;
				isCurrentConstructBuilding = true;
			}
			else {
				isCurrentConstructBuilding = false;
			}
			
			if (checkConstruction3.isEnabled() && checkConstruction3.isSelected()) {
				totalConstructionCost += hotelCost;
				isCurrentConstructHotel = true;
			}
			else {
				isCurrentConstructHotel = false;
			}
			
			if (checkConstruction4.isEnabled() && checkConstruction4.isSelected()) {
				totalConstructionCost += landmarkCost;
				isCurrentConstructLandmark = true;
			}
			else {
				isCurrentConstructLandmark = false;
			}
			
			labelConstructionTotalCost.setText("건설 비용: " + totalConstructionCost + "만");
			labelTravelingTax.setText("통행료: " + (getTravelingTax() + totalConstructionCost*2) + "만");
		}
	}
	
	City(int index, String name)
	{
		super(index, name, Area.Type.CITY);
		gameboard = GameBoard.getInstance();
		
		imgVilla = getResizeIcon("image/villa.gif", 0.75).getImage();
		imgBuilding = getResizeIcon("image/building.gif", 0.75).getImage();
		imgHotel = getResizeIcon("image/hotel.gif", 0.75).getImage();
		imgLandmark = getResizeIcon("image/landmark.gif", 2.3, 0.75).getImage();
		
		// 1. Top
		JPanel panelTop = new JPanel();
		panelTop.setOpaque(true);
		panelTop.setBackground(Color.YELLOW);
		panelTop.add(new JLabel("건설할 건물을 선택하세요.", JLabel.CENTER));
		panelTop.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		// 빌라
		checkConstruction1 = new JCheckBox("빌라");
		checkConstruction1.addItemListener(new ConstructionEventHandler());
		JPanel panelConstructionTitle1 = new JPanel();
		panelConstructionTitle1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panelConstructionTitle1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelConstructionTitle1.add(checkConstruction1);
		JLabel labelConstruction1 = new JLabel(getResizeIcon("image/villa.gif", 2.0), JLabel.CENTER);
		labelConstruction1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		labelConstruction1.setOpaque(true);
		labelConstruction1.setBackground(Color.WHITE);
		labelConstructionCost1 = new JLabel(villaCost+"만", JLabel.RIGHT);
		labelConstructionCost1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// 2.1.1
		JPanel panelStructure1 = new JPanel();
		panelStructure1.setLayout(new BorderLayout());
		panelStructure1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panelStructure1.add(panelConstructionTitle1, BorderLayout.NORTH);
		panelStructure1.add(labelConstruction1, BorderLayout.CENTER);
		panelStructure1.add(labelConstructionCost1, BorderLayout.SOUTH);
		
		// 빌딩
		checkConstruction2 = new JCheckBox("빌딩");
		checkConstruction2.addItemListener(new ConstructionEventHandler());
		JPanel panelConstructionTitle2 = new JPanel();
		panelConstructionTitle2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panelConstructionTitle2.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelConstructionTitle2.add(checkConstruction2);
		JLabel labelConstruction2 = new JLabel(getResizeIcon("image/building.gif", 2.0), JLabel.CENTER);
		labelConstruction2.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		labelConstruction2.setOpaque(true);
		labelConstruction2.setBackground(Color.WHITE);
		labelConstructionCost2 = new JLabel(buildingCost+"만", JLabel.RIGHT);
		labelConstructionCost2.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// 2.1.2
		JPanel panelStructure2 = new JPanel();
		panelStructure2.setLayout(new BorderLayout());
		panelStructure2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panelStructure2.add(panelConstructionTitle2, BorderLayout.NORTH);
		panelStructure2.add(labelConstruction2, BorderLayout.CENTER);
		panelStructure2.add(labelConstructionCost2, BorderLayout.SOUTH);
		
		// 호텔
		checkConstruction3 = new JCheckBox("호텔");
		checkConstruction3.addItemListener(new ConstructionEventHandler());
		JPanel panelConstructionTitle3 = new JPanel();
		panelConstructionTitle3.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panelConstructionTitle3.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelConstructionTitle3.add(checkConstruction3);
		JLabel labelConstruction3 = new JLabel(getResizeIcon("image/hotel.gif", 2.0), JLabel.CENTER);
		labelConstruction3.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		labelConstruction3.setOpaque(true);
		labelConstruction3.setBackground(Color.WHITE);
		labelConstructionCost3 = new JLabel(hotelCost+"만", JLabel.RIGHT);
		labelConstructionCost3.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// 2.1.3
		JPanel panelStructure3 = new JPanel();
		panelStructure3.setLayout(new BorderLayout());
		panelStructure3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panelStructure3.add(panelConstructionTitle3, BorderLayout.NORTH);
		panelStructure3.add(labelConstruction3, BorderLayout.CENTER);
		panelStructure3.add(labelConstructionCost3, BorderLayout.SOUTH);
		
		// 랜드마크
		checkConstruction4 = new JCheckBox("랜드마크");
		checkConstruction4.addItemListener(new ConstructionEventHandler());
		JPanel panelConstructionTitle4 = new JPanel();
		panelConstructionTitle4.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panelConstructionTitle4.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelConstructionTitle4.add(checkConstruction4);
		JLabel labelConstruction4 = new JLabel(getResizeIcon("image/landmark.gif", 2.0), JLabel.CENTER);
		labelConstruction4.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		labelConstruction4.setOpaque(true);
		labelConstruction4.setBackground(Color.WHITE);
		labelConstructionCost4 = new JLabel(landmarkCost+"만", JLabel.RIGHT);
		labelConstructionCost4.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// 2.1.4
		JPanel panelStructure4 = new JPanel();
		panelStructure4.setLayout(new BorderLayout());
		panelStructure4.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panelStructure4.add(panelConstructionTitle4, BorderLayout.NORTH);
		panelStructure4.add(labelConstruction4, BorderLayout.CENTER);
		panelStructure4.add(labelConstructionCost4, BorderLayout.SOUTH);
		
		// 2.1 Center
		JPanel panelCenterSub1 = new JPanel();
		panelCenterSub1.setLayout(new GridLayout(1,4));
		panelCenterSub1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelCenterSub1.add(panelStructure1);
		panelCenterSub1.add(panelStructure2);
		panelCenterSub1.add(panelStructure3);
		panelCenterSub1.add(panelStructure4);
		
		// 2.2.1. Center
		labelConstructionTotalCost = new JLabel("건설 비용: 0만", JLabel.CENTER);
		labelConstructionTotalCost.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		labelConstructionTotalCost.setOpaque(true);
		labelConstructionTotalCost.setBackground(Color.WHITE);
		
		buttonOk = new JButton("건설");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkConstruction1.isSelected()) {
					isExistVilla = true;
					checkConstruction1.setEnabled(false);
				}
				if (checkConstruction2.isSelected()) {
					isExistBuilding = true;
					checkConstruction2.setEnabled(false);
				}
				if (checkConstruction3.isSelected()) {
					isExistHotel = true;
					checkConstruction3.setEnabled(false);
				}
				if (checkConstruction4.isSelected()) {
					isExistLandmark = true;
					checkConstruction4.setEnabled(false);
				}
				
				dialogBuildStructure.setVisible(false);
				repaint();
				
				increaseTravelingTax((int)(totalConstructionCost*0.5));
			}
		});
		
		JLabel labelDummy = new JLabel("    ");
		
		JButton buttonClose = new JButton("닫기");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				totalConstructionCost = 0;
				labelConstructionTotalCost.setText("건설 비용: " + totalConstructionCost + "만");
				dialogBuildStructure.setVisible(false);
			}
		});
		
		// 2.2.2 Center
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout());
		panelButtons.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panelButtons.add(buttonOk);
		panelButtons.add(labelDummy);
		panelButtons.add(buttonClose);
		
		// 2.2.3 Center
		labelTravelingTax = new JLabel("통행료: ", JLabel.CENTER);
		labelTravelingTax.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		labelTravelingTax.setOpaque(true);
		labelTravelingTax.setBackground(Color.WHITE);
		
		// 2.2. Center
		JPanel panelCenterSub2 = new JPanel();
		panelCenterSub2.setLayout(new GridLayout(3,1));
		panelCenterSub2.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelCenterSub2.add(labelConstructionTotalCost);
		panelCenterSub2.add(panelButtons);
		panelCenterSub2.add(labelTravelingTax);
		
		// 2. Center
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(2,1));
		panelCenter.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		panelCenter.add(panelCenterSub1);
		panelCenter.add(panelCenterSub2);
		
		dialogBuildStructure = new JDialog(gameboard, name+": 건물 건설", true);
		dialogBuildStructure.setSize(380, 360);
		dialogBuildStructure.setLocation(gameboard.getWidth()/2-getWidth()/2, gameboard.getHeight()/2-getHeight()/2);
		dialogBuildStructure.setLayout(new BorderLayout());
		dialogBuildStructure.setResizable(false);
		dialogBuildStructure.add(panelTop, BorderLayout.NORTH);
		dialogBuildStructure.add(panelCenter, BorderLayout.CENTER);
		dialogBuildStructure.setVisible(false);
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if (isExistLandmark) {
			g.drawImage(imgLandmark, 3, 69, this);
		}
		else {
			if (isExistVilla) {
				g.drawImage(imgVilla, 3, 69, this);
			}
			if (isExistBuilding) {
				g.drawImage(imgBuilding, 28, 69, this);
			}
			if (isExistHotel) {
				g.drawImage(imgHotel, 53, 69, this);
			}
		}
	}
	
	ImageIcon getResizeIcon(String imgName, double scale)
	{
		return getResizeIcon(imgName, scale, scale);
	}
	ImageIcon getResizeIcon(String imgName, double scaleX, double scaleY)
	{
		ImageIcon icon = new ImageIcon(imgName);
		return new ImageIcon(icon.getImage().getScaledInstance((int)(icon.getIconWidth()*scaleX), (int)(icon.getIconHeight()*scaleY), Image.SCALE_SMOOTH));		
	}
	
	public void setInitialCost(int landCost, int villaCost, int buildingCost, int hotelCost, int landmarkCost)
	{
		this.landCost = landCost;
		this.villaCost = villaCost;
		this.buildingCost = buildingCost;
		this.hotelCost = hotelCost;
		this.landmarkCost = landmarkCost;
		
		labelConstructionCost1.setText(villaCost + "만");
		labelConstructionCost2.setText(buildingCost + "만");
		labelConstructionCost3.setText(hotelCost + "만");
		labelConstructionCost4.setText(landmarkCost + "만");
		
		labelTravelingTax.setText("통행료: " + getTravelingTax() + "만");
	}
	
	public void setOwner(String owner, Color color)
	{
		this.isOwner = true;
		this.owner = owner;
		this.color = color;
		labelCenter.setBackground(this.color);
	//x	labelBottom.setBackground(this.color);
		repaint();
	}
	
	public void resetOwner()
	{
		this.isOwner = false;
		this.owner = "";
		this.isExistVilla = false;
		this.isExistBuilding = false;
		this.isExistHotel = false;
		this.isExistLandmark = false;
		
		this.color = new Color(255, 255, 255); // 238, 238, 238
		labelCenter.setBackground(this.color);
	//x	labelBottom.setBackground(this.color);
		repaint();
	}
	
	public boolean isOwner()
	{
		return isOwner;
	}
	
	// 건물 존재여부
	public boolean isExistVilla()
	{
		return isExistVilla;
	}
	
	public boolean isExistBuilding()
	{
		return isExistBuilding;
	}
	
	public boolean isExistHotel()
	{
		return isExistHotel;
	}
	
	public boolean isExistLandmark()
	{
		return isExistLandmark;
	}
	
	// 현재 진행순서에서 건물건설여부
	public boolean isCurrentConstructVilla()
	{
		return isCurrentConstructVilla;
	}
	
	public boolean isCurrentConstructBuilding()
	{
		return isCurrentConstructBuilding;
	}
	
	public boolean isCurrentConstructHotel()
	{
		return isCurrentConstructHotel;
	}
	
	public boolean isCurrentConstructLandmark()
	{
		return isCurrentConstructLandmark;
	}
	
	// 건물 구입비용알아오기
	public int getLandCost()
	{
		return landCost;
	}
	
	public int getVillaCost()
	{
		return villaCost;
	}
	
	public int getBuildingCost()
	{
		return buildingCost;
	}
	
	public int getHotelCost()
	{
		return hotelCost;
	}
	
	public int getLandmarkCost()
	{
		return landmarkCost;
	}
	
	// 올림픽 개최여부
	public int getHostedOlympicCount()
	{
		return hostedOlympicCount;
	}
	
	public boolean addHostedOlympicCount()
	{
		boolean result;
		
		// 통행세 증가: {1st:x2, 2nd:x3, 3rd:x4, 4th:x5}
		if (hostedOlympicCount < 4) {
			hostedOlympicCount++;
			travelingTax *= (hostedOlympicCount+1);
			result = true;
		}
		else {
			result = false;
		}
		
		return result;
	}
	
	public void buyLand(int playerOrder)
	{
		// 소유권 설정(해당 Player의 소유권 색상표시)
		setOwner(gameboard.player[playerOrder-1].name, gameboard.player[playerOrder-1].color);
	}
	
	public void buildVilla()
	{
		
	}
	
	public void buildBuilding()
	{
		
	}
	public void buildHotel()
	{
		
	}
	public void buildLandmark()
	{
		
	}
	
	// 매각금액
	public int getDisposalCost()
	{
		int disposalCost = landCost * 2;
		
		disposalCost = (isExistVilla) ?		disposalCost + villaCost * 2	: disposalCost;
		disposalCost = (isExistBuilding) ?	disposalCost + buildingCost * 2	: disposalCost;
		disposalCost = (isExistHotel) ?		disposalCost + hotelCost * 2	: disposalCost;
		
		return disposalCost;
	}
	
	public boolean waitBuyLand(int money, int currentTurn)
	{
		boolean result = false;
		
		if (!isOwner) {
			String message;
			if (currentTurn < 2) {
				message = "땅 구입비용(" + getLandCost() + "만)을 지불하시고 구입하시겠습니까?\n" + "  - 현재 턴에서는 땅만 구입 가능\n" + "  - 최소 2턴 이상 돌아야 건물  건설 가능";
			}
			else {
				message = "땅 구입비용(" + getLandCost() + "만)을 지불하시고 구입하시겠습니까?";
			}
			
			int choice = JOptionPane.showConfirmDialog(gameboard, message, name+": 땅 구입", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				if (money >= getLandCost()) {
					result = true;
				}
				else {
					JOptionPane.showMessageDialog(gameboard, "보유한 현금이 부족합니다.", name+": 땅 구입", JOptionPane.INFORMATION_MESSAGE);
					result = false;
				}
			}
		}
		
		return result;
	}
	
	public boolean waitBuyCity(int money)
	{
		boolean result = false;
		
		if (!isExistLandmark()) {
			int choice = JOptionPane.showConfirmDialog(gameboard, "다른 Player에게 땅/건물 매입비용(" + getDisposalCost() + "만)을 지불하시고 매입하시겠습니까?", name+": 땅/건물 매입", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				if (money >= getDisposalCost()) {
					result = true;
				}
				else {
					JOptionPane.showMessageDialog(gameboard, "보유한 현금이 부족합니다.", name+":땅 구입", JOptionPane.INFORMATION_MESSAGE);
					result = false;
				}
			}
		}
		
		return result;
	}
	
	public void transferCity(int playerOrder)
	{
		// 기존 소유주에게 구입비용 지급
		for (int i=0; i<gameboard.player.length; i++) {
			// Player 자신은 제외
			if (i == playerOrder-1) {
				continue;
			}
			
			if (gameboard.player[i].name == owner) {
			//	System.out.println("transferCity(" + playerOrder + "):owner=" + owner + ", 매입가=" + getDisposalCost());
				gameboard.player[i].decreaseOwningCityCount();
				gameboard.player[playerOrder-1].increaseOwningCityCount();
				
				if (isExistVilla) {
					gameboard.player[i].decreaseOwningVillaCount();
					gameboard.player[playerOrder-1].increaseOwningVillaCount();
				}
				if (isExistBuilding) {
					gameboard.player[i].decreaseOwningBuildingCount();
					gameboard.player[playerOrder-1].increaseOwningBuildingCount();
				}
				if (isExistHotel) {
					gameboard.player[i].decreaseOwningHotelCount();
					gameboard.player[playerOrder-1].increaseOwningHotelCount();
				}
				
				// 소유주 변경 (GUI에 해당 Player의 소유권 색상표시)
				gameboard.player[i].money += getDisposalCost();
				buyLand(playerOrder);
				break;
			}
		}
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
	
	public int waitBuildStructure(int currentTurn)
	{
		totalConstructionCost = 0;
		isCurrentConstructVilla = false;
		isCurrentConstructBuilding = false;
		isCurrentConstructHotel = false;
		isCurrentConstructLandmark = false;
		
		// 빌라
		if (currentTurn < 2) {
			checkConstruction1.setEnabled(false);
		}
		else {
			checkConstruction1.setEnabled(!isExistVilla);
			checkConstruction1.setSelected(isExistVilla);
		}
		
		// 빌딩
		if (currentTurn < 3) {
			checkConstruction2.setEnabled(false);
		}
		else {
			checkConstruction2.setEnabled(!isExistBuilding);
			checkConstruction2.setSelected(isExistBuilding);
		}
		
		// 호텔
		if (currentTurn < 4) {
			checkConstruction3.setEnabled(false);
		}
		else {
			checkConstruction3.setEnabled(!isExistHotel);
			checkConstruction3.setSelected(isExistHotel);
		}
		
		// 랜드마크
		if (currentTurn < 5 || !isExistVilla || !isExistBuilding || !isExistHotel) {
			System.out.println("isExistVilla=" + isExistVilla + ",isExistBuilding=" + isExistBuilding + ",isExistHotel=" + isExistHotel);
			checkConstruction4.setEnabled(false);
		}
		else {
			checkConstruction4.setEnabled(!isExistLandmark);
			checkConstruction4.setSelected(isExistLandmark);
		}
		
		if (checkConstruction1.isEnabled() || checkConstruction2.isEnabled() || checkConstruction3.isEnabled() || checkConstruction4.isEnabled()) {
			buttonOk.setEnabled(true);
		}
		else {
			buttonOk.setEnabled(false);
		}
		
		dialogBuildStructure.setVisible(true);
		
		return totalConstructionCost;
	}
}
