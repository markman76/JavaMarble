package Marble;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GameBoard extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int TOTAL_AREA_COUNT = 32; //�� ������(���)
	final int FRAME_WIDTH = 1024;
	final int FRAME_HEIGHT = 768;
	
	JLabel labelGameTimer = null;
	JLabel labelGameTurn = null;
	
	int totalTurnCount; // �� �ϼ�
	int numberOfPlayer; // ����  �÷��̾� ��
	
	private Timer swingTimer;
	int timerSecond = 0;
	int timerMinute = 0;
	int timerHour = 0;
	
	/*
	String[] boardArea = {
		"�����",										// [ 0   ] south/east
		"����", "Ÿ�̿�", "����¡", "����", "Ÿ������",	// [ 1~ 5] south
		"�ι���", "ī�̷�", "���ε�", "�߸�", "����",		// [ 6~10] south
		"�õ��",										// [11   ] south/west
		"��Ƽ�ư�", "��Ʈ����", "�Ͽ���", "���Ŀ��",		// [12~15] west
		"�ø���",										// [16   ] north/west
		"������", "Ǫ��", "������", "ĵ����", "��ũ��",	// [17~21] north
		"���׹�", "�θ�", "���迩��", "Ÿ��Ƽ", "����",	// [22~26] north
		"�ĸ�",											// [27   ] north/east
		"����ī", "����", "����û", "����"				// [28~31] east
	};
	*/
	
	String[] areaNameList;
	
	Dice dice = null; //�ֻ���
	Player[] player; // �÷��̾� ����Ʈ
	Area[] area = new Area[TOTAL_AREA_COUNT]; // ��������Ʈ
	StartPoint areaStartPoint = null;
	Desert areaDesert = null;
	Olympic areaOlympic = null;
	WorldTravel areaWorldTravel = null;
	TaxService areaTaxService = null;
	Resort areaResort = null;
	City areaCity = null;
	
	private static GameBoard gameBoard = null;
	
	class AreaSAXParser extends DefaultHandler
	{
		private SAXParserFactory parserFact;
		private SAXParser parser;
		private Attributes tagAttributes;
		private String fileName; // xml file name to be parsed
	//	private String startTagName;
	//	private String endTagName;
		private String tagData;
		private String[] attrQNames; // attribute's names
		private String[] attrValues; // attribute's values
		private StringBuffer buffer = new StringBuffer();
		private ArrayList<AreaStruct> areaList = new ArrayList<AreaStruct>();
		
		class AreaStruct {
			int type;
			int travelingTax;
			int landCost;
			int villaCost;
			int buildingCost;
			int hotelCost;
			int landmarkCost;
			String name;
		}
		
		AreaSAXParser(String fileName)
		{
			super();
			
			try {
				parserFact = SAXParserFactory.newInstance();
				parser = parserFact.newSAXParser();
			} catch(Exception e) {}
			
			this.fileName = fileName;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
		//	startTagName = qName;
			tagAttributes = attributes;
			
			attrQNames = new String[tagAttributes.getLength()];
			attrValues = new String[tagAttributes.getLength()];
			
			for (int i=0; i<tagAttributes.getLength(); i++) {
				attrQNames[i] = tagAttributes.getQName(i).trim();
				attrValues[i] = tagAttributes.getValue(i).trim();
			}
			
			// ���⼭ ���۸� ������, ������ ���ۿ��� �о��� �͵��� �����ϰ� �� �� �ִ�.
			buffer.setLength(0); // ���� ����
		}
		
		@Override
		public void characters(char[] ch, int start, int len) throws SAXException
		{
			buffer.append(ch, start, len);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
		{
		//	endTagName = qName;
			tagData = buffer.toString().trim(); // end tag �� ���� ���ۿ� �ִ� ������ ���
			
			AreaStruct areaStruct = new AreaStruct();
			areaStruct.type = (attrValues[0].isEmpty()) ? -1 : Integer.parseInt(attrValues[0]);
			areaStruct.travelingTax = (attrValues[1].isEmpty()) ? -1 : Integer.parseInt(attrValues[1]);
			areaStruct.landCost = (attrValues[2].isEmpty()) ? -1 : Integer.parseInt(attrValues[2]);
			areaStruct.villaCost = (attrValues[3].isEmpty()) ? -1 : Integer.parseInt(attrValues[3]);
			areaStruct.buildingCost = (attrValues[4].isEmpty()) ? -1 : Integer.parseInt(attrValues[4]);
			areaStruct.hotelCost = (attrValues[5].isEmpty()) ? -1 : Integer.parseInt(attrValues[5]);
			areaStruct.landmarkCost = (attrValues[6].isEmpty()) ? -1 : Integer.parseInt(attrValues[6]);
			areaStruct.name = tagData;
			
			areaList.add(areaStruct);
		}
		
		public void parse()
		{
			try {
				parser.parse(fileName, this);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public int getAreaListSize()
		{
			return areaList.size()-1; // Root element�� ����
		}
		
		public int getType(int index)
		{
			return areaList.get(index).type;
		}
		
		public int getTravelingTax(int index)
		{
			return areaList.get(index).travelingTax;
		}
		
		public int getLandCost(int index)
		{
			return areaList.get(index).landCost;
		}
		
		public int getVillaCost(int index)
		{
			return areaList.get(index).villaCost;
		}
		
		public int getBuildingCost(int index)
		{
			return areaList.get(index).buildingCost;
		}
		
		public int getHotelCost(int index)
		{
			return areaList.get(index).hotelCost;
		}
		
		public int getLandmarkCost(int index)
		{
			return areaList.get(index).landmarkCost;
		}
		
		public String getName(int index)
		{
			return areaList.get(index).name;
		}
	}
	
	public static GameBoard getInstance()
	{
		return gameBoard;
	}
	
	GameBoard(int totalTurnCount, int numberOfPlayer, OrderCard[] card)
	{
		super("�ڹٸ���");
		gameBoard = this;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2-getWidth()/2, screenSize.height/2-getHeight()/2);
		setResizable(false);
		
		// 1. Player �ο����� �� �� ��ü ����/�ʱ�ȭ
		this.totalTurnCount = totalTurnCount;
		this.numberOfPlayer = numberOfPlayer;
		
		AreaSAXParser parser = new AreaSAXParser("area.xml");
		try{
			parser.parse();
		//	System.out.println("parsing success.");
		} catch(Exception e){
			System.out.println("parsing error.");
			e.printStackTrace();
			System.exit(0);
		}
		
		areaNameList = new String[parser.getAreaListSize()];
		for (int i=0; i<areaNameList.length; i++) {
			areaNameList[i] = parser.getName(i);
		}
		
		// ����, �ǹ��Ǽ���� �� �����  (������ �Ű��ݾ��� ���Ժ���� 2��)
		//   - ���� type {1:���� , 2:�޾���, 3:�����, 4:���ε�, 5:�ø���, 6:���迩��, 7:����û}
		for (int i=0; i<parser.getAreaListSize(); i++) {
			switch (parser.getType(i)) { 
			case 1: // CITY
				area[i] = new City(i, parser.getName(i));
				if (areaCity == null) { // ���� 1���� �ʱ�ȭ
					areaCity = (City)area[i];
				}
				area[i].setTravelingTax(parser.getTravelingTax(i));
				((City)area[i]).setInitialCost(parser.getLandCost(i), parser.getVillaCost(i), 
						parser.getBuildingCost(i), parser.getHotelCost(i), parser.getLandmarkCost(i));
				break;
				
			case 2: // RESORT
				area[i] = new Resort(i, parser.getName(i));
				if (areaResort == null) { // ���� 1���� �ʱ�ȭ
					areaResort = (Resort)area[i];
				}
				area[i].setTravelingTax(parser.getTravelingTax(i));
				((Resort)area[i]).setLandCost(parser.getLandCost(i));
				break;
				
			case 3: // START_POINT
				areaStartPoint = new StartPoint(i, parser.getName(i));
				area[i] = areaStartPoint;
				break;
				
			case 4: // DESERT
				areaDesert = new Desert(i, parser.getName(i));
				area[i] = areaDesert;
				break;
				
			case 5: // OLYMPIC
				areaOlympic = new Olympic(i, parser.getName(i));
				area[i] = areaOlympic;
				break;
				
			case 6: // WORLD_TRAVEL
				areaWorldTravel = new WorldTravel(i, parser.getName(i));
				area[i] = areaWorldTravel;
				break;
				
			case 7: // TAX_SERVICE
				areaTaxService = new TaxService(i, parser.getName(i));
				area[i] = areaTaxService;
				break;
			}
		}
		
		// GameBoardCenter
		dice = new Dice();
		dice.setBounds(110, 140, 240, 120);
		
		labelGameTimer = new JLabel("����ð�  00:00:00", JLabel.CENTER);
		labelGameTimer.setBorder(BorderFactory.createLineBorder(new Color(102, 51, 0)));
		labelGameTimer.setOpaque(true);
		labelGameTimer.setForeground(new Color(255, 255, 255));
		labelGameTimer.setBackground(new Color(102, 51, 0));
		labelGameTimer.setFont(new Font("���� ���", Font.BOLD, 25));
		
		ActionListener timerTaskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) 
			{
				timerSecond++;
				
				if (timerSecond >= 60) {
					timerMinute++;
					timerSecond = 0;
				}
				
				if (timerMinute >= 60) {
					timerHour++;
					timerMinute = 0;
				}
				
				DecimalFormat df = new DecimalFormat("00");
				labelGameTimer.setText("����ð�  " + df.format(timerHour) + ":" + 
						df.format(timerMinute) + ":" + df.format(timerSecond));
			}
		};
		swingTimer = new Timer(1000, timerTaskPerformer); // 1000 milliseconds
		
		labelGameTurn = new JLabel("��  00 / 00", JLabel.CENTER);
		labelGameTurn.setBorder(BorderFactory.createLineBorder(new Color(102, 51, 0)));
		labelGameTurn.setOpaque(true);
		labelGameTurn.setForeground(new Color(102, 51, 0));
		labelGameTurn.setBackground(new Color(255, 255, 255));
		labelGameTurn.setFont(new Font("���� ���", Font.BOLD, 25));
		setDisplayGameTurn(1);
		
		JPanel panelGameInfo = new JPanel();
		panelGameInfo.setBounds(480, 140, 240, 120);
		panelGameInfo.setLayout(new GridLayout(2, 1));
		panelGameInfo.add(labelGameTimer);
		panelGameInfo.add(labelGameTurn);
		
		JPanel panelGameBoardCenter = new JPanel();
		panelGameBoardCenter.setLayout(null);
		panelGameBoardCenter.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		panelGameBoardCenter.add(dice);
		panelGameBoardCenter.add(panelGameInfo);
		
		// Area
		JPanel panelGameBoard = new JPanel();
		panelGameBoard.setBounds(10, 12, getWidth()-28, getHeight()-178);
		panelGameBoard.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		
		panelGameBoard.add(panelGameBoardCenter, setLayoutGridBagConstraints(gbc, 1, 1, 10, 4));
		
		// 16~27
		for (int i=0; i<12; i++) {
			panelGameBoard.add(area[16+i], setLayoutGridBagConstraints(gbc, i, 0, 1, 1));
		}
		
		// 12~15, 28~31
		for (int i=0; i<4; i++) {
			panelGameBoard.add(area[15-i], setLayoutGridBagConstraints(gbc, 0, i+1, 1, 1));
			panelGameBoard.add(area[28+i], setLayoutGridBagConstraints(gbc, 11, i+1, 1, 1));
		}
		
		// 0~11
		for (int i=0; i<12; i++) {
			panelGameBoard.add(area[11-i], setLayoutGridBagConstraints(gbc, i, 5, 1, 1));
		}
		
		// Player ���� ǥ��
		JPanel panelPlayers = new JPanel();
		panelPlayers.setBounds(0, getHeight()-155, getWidth()-6, 120);
		panelPlayers.setLayout(new GridLayout(1, 4));
		
		// ���� ������� Player ����
		player = new Player[numberOfPlayer];
		for (int i=0; i<card.length; i++) {
			int j = card[i].order-1;
			player[j] = new Player(card[i].order, card[i].name, card[i].color);
			player[j].panelPlayer.setPlayerInfo(card[i].name, card[i].color, card[i].iconName);
			player[j].panelPlayer.setRank(player[j].order);
			player[j].panelPlayer.setMoney(player[j].money);
		}
		
		ArrayList<Color> playerColorList = new ArrayList<Color>(numberOfPlayer);
		for (int i=0; i<player.length; i++) {
			playerColorList.add(player[i].color);
		}
		
		for (int i=0; i<4; i++) {
			if (i < player.length) {
				panelPlayers.add(player[i].panelPlayer);
			}
			else {
				JLabel labelTemp = new JLabel("EMPTY", JLabel.CENTER);
				labelTemp.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
				labelTemp.setFont(new Font("Times New Roman", Font.PLAIN, 50));
				panelPlayers.add(labelTemp);
			}
		}
		
		// Area�� Player���� ǥ���ϱ� ���� �ʱ⼳��
		for (int i=0; i<area.length; i++) {
			area[i].setPlayerColorList(playerColorList);
		}
		
		Container contentPane = getRootPane().getContentPane();
		contentPane.setLayout(null);
		contentPane.add(panelGameBoard); // BorderLayout.CENTER
		contentPane.add(panelPlayers); // BorderLayout.SOUTH
		setVisible(true);
		
		// goto State:Root
		root_state();
	}
	
	private GridBagConstraints setLayoutGridBagConstraints(GridBagConstraints gbc, int x, int y, int width, int height)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		
		return gbc;
	}
	
	// Dialog���� ������� ������ �ʿ��� �޼ҵ�
	
	void root_state()
	{
		int currentOrder = 0; // ���� ����
		int currentTurn = 1;
		int maxCurrentTurn = 1;
		int winnerIndex = 0;
		
		setGameTimer(true);
		
		// Player�� 2�� �̻��϶����� �ݺ� (1�� ���ϰ� �Ǹ� ���� ����)
		while (isContinueGame()) {
			dice.viewDiceButton();
			
			// ������� Player ���� {0~3}
			currentOrder %= player.length;
			
			// Player �������� ���ɿ��θ� üũ - true: �ش� ���� Player ��������, false: ���� Player���� �����ѱ�
			if (player[currentOrder].isLive) {
				player[currentOrder].panelPlayer.setCurrentPlayer(true);
				currentTurn = player[currentOrder].play();
				maxCurrentTurn = (maxCurrentTurn < currentTurn) ? currentTurn : maxCurrentTurn;
				player[currentOrder].panelPlayer.setCurrentPlayer(false);
				currentOrder++;
			}
			else {
				currentOrder++;
				continue;
			}
			
			// ���üũ
			winnerIndex = checkRank();
			
			// Player�� �����ϼ���ŭ ���� �̵��Ͽ��ٸ� �ݺ��� ���� �� ����� ����
			if (currentTurn > totalTurnCount) {
				player[currentOrder-1].panelPlayer.setTurnCount(currentTurn-1);
				break;
			}
			
			setDisplayGameTurn(maxCurrentTurn);
			
			// while�� ����� �ʹ� ���� ���ҽ��� �������� �ʱ� ���� sleep�� �ɾ���
			try {
				Thread.sleep(50);
			} catch(Exception e) {}
		}
		
		// Ÿ�̸� ����
		setGameTimer(false);
		
		// 3. ����� ����
		//   (1) �������� ���ݰ� �ε��갡ġ�� totalMoney�� �ջ� 
		//   (2) money�� ���� ū Player�� ����ڷ� �����Ѵ�.
		
		// ���߿� Dialog�� ���ȭ�鿡 ��������/����� �ȳ� ǥ��
	//x	String notice = getTitle() + " - ��������: ����ڴ� " + player[winnerIndex].name + " �Դϴ�.";
	//x	setTitle(notice);
	//x	System.out.println("�����ϼ� �ʰ��� ������ �����մϴ�.");
	//x	System.out.println("�����մϴ�." + player[winnerIndex].name + "���� ����ϼ̽��ϴ�.");
		
		
		
		JOptionPane.showMessageDialog(this, 
				"������ �ϼ��� ��� ���Ƽ� ������ �����մϴ�.\n" + "����ڴ� " + player[winnerIndex].name + " �Դϴ�.", 
				"��������", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public boolean isContinueGame()
	{
		int livePlayerCount = 0;
		
		for (int i=0; i<player.length; i++) {
			if (player[i].isLive) {
				livePlayerCount++;
			}
		}
		
		return (livePlayerCount > 1) ? true : false;
	}
	
	public int getMovingPoint(int currentLocationIndex, int destinationLocationIndex)
	{
		int movingPoint = destinationLocationIndex - currentLocationIndex;
		return (movingPoint < 0) ? area.length - Math.abs(movingPoint) : movingPoint;
	}
	
	int checkRank()
	{
		TreeSet<Player> playerSet = new TreeSet<Player>();
		for (int i=0; i<player.length; i++) {
			playerSet.add(player[i]);
		}
		
		Iterator<Player> it = playerSet.iterator();
		
		int i = 0;
		int winnerIndex = 0;
		while (it.hasNext()) {
			Player p = it.next();
			winnerIndex = (i == 0) ? p.order-1 : winnerIndex; 
			p.rank = ++i;
			player[p.order-1].panelPlayer.setRank(p.rank);
		}
		
		return winnerIndex;
	}
	
	void soundPlay(String fileName)
	{
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
			Clip clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();
		} catch (Exception e) {}
	}
	
	void setGameTimer(boolean isStart)
	{
		if (isStart) {
			timerSecond = 0;
			timerMinute = 0;
			timerHour = 0;
			swingTimer.start();
		}
		else {
			swingTimer.stop();
		}
	}
	
	void setDisplayGameTurn(int turnCount)
	{
		labelGameTurn.setText("��  " + turnCount + " / " + totalTurnCount);
	}
	
	public int getTotalTurnCount()
	{
		return totalTurnCount;
	}
	
	public int getTotalAreaCount()
	{
		return area.length;
	}
	
	public int sellCity(int playerOrder, int minAmount)
	{
		int money = 0;
		
		for (int i=0; i<area.length; i++) {
			if (area[i].type != Area.Type.CITY) {
				continue;
			}
			
			if (!((City)area[i]).isOwner()) {
				continue;
			}
			
			if (player[playerOrder-1].name != ((City)area[i]).owner) {
				continue;
			}
			
			if (((City)area[i]).isExistVilla()) {
				player[playerOrder-1].decreaseOwningVillaCount();
			}
			if (((City)area[i]).isExistBuilding()) {
				player[playerOrder-1].decreaseOwningBuildingCount();
			}
			if (((City)area[i]).isExistHotel()) {
				player[playerOrder-1].decreaseOwningHotelCount();
			}
			if (((City)area[i]).isExistLandmark()) {
				player[playerOrder-1].decreaseOwningLandmarkCount();
			}
			player[playerOrder-1].decreaseOwningCityCount();
			
			money += ((City)area[i]).getDisposalCost();
			((City)area[i]).resetOwner();
			
			if (money >= minAmount) {
				break;
			}
		}
		
		return money;
	}
	
	
	public int getTotalMoney(int playerOrder, int money)
	{
		int totalMoney = money;
		
		for (int i=0; i<area.length; i++) {
			if (area[i].type != Area.Type.CITY) {
				continue;
			}
			
			if (!((City)area[i]).isOwner()) {
				continue;
			}
			
			if (player[playerOrder-1].name != ((City)area[i]).owner) {
				continue;
			}
			
			totalMoney += ((City)area[i]).getDisposalCost();
		}
		
		return totalMoney;
	}
}

class OrderCard {
	int order;
	String name;
	Color color;
	String iconName;
}

