package Marble;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Player extends JPanel implements Comparable<Player>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final int DEFAULT_MONEY = 200; //플레이어의 머니 초기화
	
	int order; //플레이어의 진행 순서
	String name; //플레이어 이름
	Color color; // 플레이어의 고유 색상
	int rank; // 플레이어의 순위등급
	int money = DEFAULT_MONEY; //보유현금
	boolean isLive = true; // 게임진행/종료 여부
	boolean isMyTurn = false;
	int previousLocationIndex = 0; // 이전에 위치했던 Area (세계여행 로직 처리)
	int currentLocationIndex = 0; // 현재 위치하고 있는 Area
	int doubleCount = 0;
	int currentTurn = 1; //현재 턴수
	int restTurnCount = 3; // 이동제한턴수 (무인도)
	private int countOwningCity = 0; // 소유중인 도시
	private int countOwningResort = 0; // 소유중인 휴양지
	private int countOwningVilla = 0; // 소유중인 빌라
	private int countOwningBuilding = 0; // 소유중인 빌딩
	private int countOwningHotel = 0; // 소유중인 호텔
	private int countOwningLandmark = 0; // 소유중인 랜드마크
	
	GameBoard gameboard = null;
	PlayerPanel panelPlayer = new PlayerPanel();
	
	class ThreadUpdatePlayerInfomation implements Runnable
	{
		@Override
		public void run() {
			while (true) {
				updatePlayerInfomation();
				
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {}
			}
		}
	}
	
	Player(int order, String name, Color color)
	{
		this.order = order;
		this.name = name;
		this.color = color;
		
		gameboard = GameBoard.getInstance();
		gameboard.area[0].setExistPlayer(order, true); // isExistPlayer[order-1] = true;
		gameboard.repaint();
		
		currentTurn = 1;
		previousLocationIndex = 0;
		currentLocationIndex = 0;
		
		Thread daemonThreadUpdatePlayerInfomation = new Thread(new ThreadUpdatePlayerInfomation());
		daemonThreadUpdatePlayerInfomation.setDaemon(true);
		daemonThreadUpdatePlayerInfomation.start();
	}
	
	// Player의 행위가 이루어지는 메인 메소드 (Sequence Chart를 참조하여 로직을 구성)
	public int play()
	{
		int movingPoint = 0;
		
		Area.Type areaType = gameboard.area[currentLocationIndex].type;
		switch (areaType) {
		case DESERT:
			doubleCount = 0;
			
			if (restTurnCount <= 0) {
				normal_state(); // goto Normal state
				break;
			}
			
			if (gameboard.areaDesert.getEscapeCost() <= money) {
				if (gameboard.areaDesert.waitPayEscapeCost()) {
					money -= gameboard.areaDesert.getEscapeCost();
					
					JOptionPane.showMessageDialog(gameboard, "무인도를 탈출했습니다. 주사위를 던지세요.", "무인도", JOptionPane.INFORMATION_MESSAGE);
					
					normal_state(); // goto Normal state
					break;
				}
			}
			
			movingPoint = gameboard.dice.waitThrowingDice();
			
			// 무인도 탈출
			if (gameboard.dice.isDouble()) {
				JOptionPane.showMessageDialog(gameboard, "더블이 나왔으므로 무인도를 탈출합니다.", "무인도", JOptionPane.INFORMATION_MESSAGE);
				moving_state(movingPoint); // goto Moving state
				break;
			}
			
			restTurnCount--;
			break; // goto Root state
			
		case WORLD_TRAVEL:
			if (previousLocationIndex == currentLocationIndex) {
				normal_state(); // goto Normal state
				break;
			}
			
			// 여행비 지급가능?
			if (gameboard.areaWorldTravel.getTravelCost() <= money) {
				int destenationLocationIndex = gameboard.areaWorldTravel.waitSelectDestinationArea();
				
				// 여행비 지급 (money 감소)
				money -= gameboard.areaWorldTravel.getTravelCost();
				
				// 이동할 영역 지정 (위치를 지정)
				doubleCount = 0;
				movingPoint = gameboard.getMovingPoint(currentLocationIndex, destenationLocationIndex);
				moving_state(movingPoint); // goto Moving state
				break;
			}
			else {
				JOptionPane.showMessageDialog(gameboard, "현금이 부족하여 세계여행을 할 수 없습니다.", "세계여행", JOptionPane.INFORMATION_MESSAGE);
			}
			
			previousLocationIndex = currentLocationIndex;
			check_state(); // goto Check state
			break;
			
		default:
			normal_state();
		}
		
		return currentTurn;
	}
	
	void normal_state()
	{
		int diceNumber = gameboard.dice.waitThrowingDice();
		
		if (gameboard.dice.isDouble()) {
			doubleCount++;
			System.out.println(order + "::doubleCount=" + doubleCount);
			if (doubleCount >= 3) {
				// go 무인도
				JOptionPane.showMessageDialog(gameboard, "더블이 3번 이상 나왔습니다. 더블 벌칙으로 무인도로 이동합니다.", "무인도", JOptionPane.INFORMATION_MESSAGE);
				gameboard.soundPlay("sound/move.wav");
				gameboard.area[currentLocationIndex].setExistPlayer(order, false);
				gameboard.area[gameboard.areaDesert.index].setExistPlayer(order, true);
				gameboard.repaint();
				currentLocationIndex = gameboard.areaDesert.index;
				doubleCount = 0;
				restTurnCount = 3;
				
				try {
					Thread.sleep(300);
				} catch(Exception e) {}
				
				return; // goto Root state
			}
		}
		
		moving_state(diceNumber);
	}
	void moving_state(int movingPoint)
	{
		previousLocationIndex = currentLocationIndex;
		
		// 말 이동
		currentLocationIndex = movePlayer(currentLocationIndex, movingPoint);
		if (currentTurn > gameboard.getTotalTurnCount()) {
			currentLocationIndex = 0;
			return; // goto Root state
		}
		
//		System.out.println("<" + order + "> move " + movingPoint + " - current=area[" + currentLocationIndex + "], turn=" + currentTurn);
		
		// Area procedure
		Area.Type areaType = gameboard.area[currentLocationIndex].type;
		switch (areaType) {
		case CITY:
			// 주인없음
			if (!((City)gameboard.area[currentLocationIndex]).isOwner()) {
				// 땅구입
				if (((City)gameboard.area[currentLocationIndex]).waitBuyLand(money, currentTurn)) {
					money -= ((City)gameboard.area[currentLocationIndex]).getLandCost();
					((City)gameboard.area[currentLocationIndex]).buyLand(order);
					increaseOwningCityCount();
					construction_state(); // goto Construction state
				}
				
				// goto Check state
			}
			else {
				// 다른사람
				if (gameboard.player[order-1].name != ((City)gameboard.area[currentLocationIndex]).owner) {
					// 통행세 납부
					gameboard.area[currentLocationIndex].waitNoticeChargeTravelingTax();
					int travelingTax = gameboard.area[currentLocationIndex].getTravelingTax();
					if (money < travelingTax) {
						JOptionPane.showMessageDialog(gameboard, "현금이 부족하여  보유한 부동산을 처분합니다.", "통행세", JOptionPane.INFORMATION_MESSAGE);
						// 보유 부동산 매각
						int sellMoney = gameboard.sellCity(order, travelingTax);
						money += sellMoney;
					}
					money -= travelingTax;
					((City)gameboard.area[currentLocationIndex]).giveTravelingTax(order);
					
					// 땅/건물 매입 (랜드마크 유무 및 보유현금으로 구매가능 여부 체크)
					if (money > 0) {
						if (((City)gameboard.area[currentLocationIndex]).waitBuyCity(money)) {
							money -= ((City)gameboard.area[currentLocationIndex]).getDisposalCost();
							((City)gameboard.area[currentLocationIndex]).transferCity(order);
							construction_state();
						}
					}
				}
				// 자신
				else {
					construction_state();
				}
			}
			
			check_state(); // goto Check state
			break;
			
		case RESORT:
			// 주인없음
			if (!((Resort)gameboard.area[currentLocationIndex]).isOwner()) {
				// 땅구입
				if (((Resort)gameboard.area[currentLocationIndex]).waitBuyLand(money)) {
					money -= ((Resort)gameboard.area[currentLocationIndex]).getLandCost();
					((Resort)gameboard.area[currentLocationIndex]).buyLand(order);
					increaseOwningResortCount();
				}
				
				// goto Check state
			}
			else {
				// 다른사람
				if (gameboard.player[order-1].name != ((Resort)gameboard.area[currentLocationIndex]).owner) {
					// 통행세 납부
					gameboard.area[currentLocationIndex].waitNoticeChargeTravelingTax();
					int travelingTax = gameboard.area[currentLocationIndex].getTravelingTax();
					if (money < travelingTax) {
						JOptionPane.showMessageDialog(gameboard, "현금이 부족하여  보유한 부동산을 처분합니다.", "통행세", JOptionPane.INFORMATION_MESSAGE);
						// 보유 부동산 매각
						int sellMoney = gameboard.sellCity(order, travelingTax);
						money += sellMoney;
					}
					money -= travelingTax;
					((Resort)gameboard.area[currentLocationIndex]).giveTravelingTax(order);
					// goto Check state
				}
				else {
					// goto Check state
				}
			}
			
			check_state(); // goto Check state
			break;
			
		case START_POINT:
			check_state(); // goto Check state
			break;
			
		case DESERT:
			restTurnCount = 3;
			JOptionPane.showMessageDialog(gameboard, "무인도에 갇혔습니다. 탈출할려면 다음의 조건을 만족해야합니다.\n"
					+ "    - 무인도 탈출비용(" + gameboard.areaDesert.getEscapeCost() + "만) 지불\n"
					+ "    - 주사위가 더블이 나옴\n"
					+ "    - 제한턴수(" + restTurnCount + "턴) 종료\n", 
					"무인도", JOptionPane.INFORMATION_MESSAGE);
			break; // goto Root state
			
		case OLYMPIC:
			int index = gameboard.areaOlympic.waitSelectCandidateCity(order);
			if (index != -1) {
				if (gameboard.areaOlympic.getHostingOlympicCost() <= money) {
					// 개최비용 지급
					money -= gameboard.areaOlympic.getHostingOlympicCost();
					
					// 개최횟수 & 통행세 증가
					((City)gameboard.area[index]).addHostedOlympicCount();
				}
			}
			
			check_state(); // goto Check state
			break;
			
		case WORLD_TRAVEL:
			JOptionPane.showMessageDialog(gameboard, "다음 턴에서 세계여행을 합니다.", "세계여행", JOptionPane.INFORMATION_MESSAGE);
			check_state(); // goto Check state
			break;
			
		case TAX_SERVICE:
			// 재산세 납부
			money -= gameboard.areaTaxService.getTax(order);
			check_state(); // goto Check state
			break;
		}
	}
	
	void construction_state()
	{
		// 최소턴수 여부?
		if (currentTurn < 2) {
//x			JOptionPane.showMessageDialog(gameboard, "최소 2턴 이상 돌아야 건물을 건설할 수 있습니다.", "도시", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// 랜드마크 존재여부?
		if (((City)gameboard.area[currentLocationIndex]).isExistLandmark()) {
			return;
		}
		
		int constructionCost = ((City)gameboard.area[currentLocationIndex]).waitBuildStructure(currentTurn);
		money -= constructionCost;
		if (((City)gameboard.area[currentLocationIndex]).isCurrentConstructVilla()) {
			increaseOwningVillaCount();
		}
		if (((City)gameboard.area[currentLocationIndex]).isCurrentConstructBuilding()) {
			increaseOwningBuildingCount();
		}
		if (((City)gameboard.area[currentLocationIndex]).isCurrentConstructHotel()) {
			increaseOwningHotelCount();
		}
		if (((City)gameboard.area[currentLocationIndex]).isCurrentConstructLandmark()) {
			increaseOwningLandmarkCount();
		}
		
		// goto Check state
	}
	
	int movePlayer(int currentLocationIndex, int movingPoint)
	{
		int prevIndex = 0;
		int nextIndex = 0;
		
		// ex) [5] + 1 -> [6]
		for (int j=0; j<movingPoint; j++) {
			prevIndex = currentLocationIndex++;	// 5, 6, 7 ~ 30, 31, 0
			if (currentLocationIndex % gameboard.getTotalAreaCount() == 0) {
				currentTurn++;
				currentLocationIndex %= gameboard.getTotalAreaCount();
			}
			nextIndex = currentLocationIndex;	// 6, 7, 8 ~ 31,  0, 1
			
			gameboard.soundPlay("sound/move.wav");
			
			gameboard.area[prevIndex].setExistPlayer(order, false); // isExistPlayer[order-1] = false;
			gameboard.area[nextIndex].setExistPlayer(order, true); // isExistPlayer[order-1] = true;
			gameboard.repaint();
			
			if (currentTurn > gameboard.getTotalTurnCount()) {
				break;
			}
			
			if (currentLocationIndex == 0) {
				JOptionPane.showMessageDialog(gameboard, "시작점을 경유하면 축하금(" + gameboard.areaStartPoint.getPaycheck() + "만)을 지급합니다.", "시작점", JOptionPane.INFORMATION_MESSAGE);
				money += gameboard.areaStartPoint.getPaycheck(); // 축하금 지급
			}
			
			// 육안으로 확인하기 위한 시간지연
			try {
				Thread.sleep(300);
			} catch(Exception e) {}
		}
		
		return currentLocationIndex;
	}
	
	void check_state()
	{
		gameboard.dice.viewDiceButton();
		
		if (gameboard.getTotalMoney(order, money) < 0) {
			isLive = false;
			return; // goto Root state
		}
		
		if (gameboard.dice.isDouble()) {
			JOptionPane.showMessageDialog(gameboard, "이전에 더블이 나왔으므로 한번더 주사위를 던지세요.", "더블찬스", JOptionPane.INFORMATION_MESSAGE);
			normal_state(); // goto Normal state
			return;
		}
		
		// goto Root state
	}
	
	public void increaseOwningCityCount()
	{
		countOwningCity++;
	}
	
	public void decreaseOwningCityCount()
	{
		countOwningCity--;
		if (countOwningCity < 0) {
			countOwningCity = 0;
		}
	}
	
	public void increaseOwningResortCount()
	{
		countOwningResort++;
	}
	
	public void decreaseOwningResortCount()
	{
		countOwningResort--;
		if (countOwningResort < 0) {
			countOwningResort = 0;
		}
	}
	
	public void increaseOwningVillaCount()
	{
		countOwningVilla++;
	}
	
	public void decreaseOwningVillaCount()
	{
		countOwningVilla--;
		if (countOwningVilla < 0) {
			countOwningVilla = 0;
		}
	}
	
	public void increaseOwningBuildingCount()
	{
		countOwningBuilding++;
	}
	
	public void decreaseOwningBuildingCount()
	{
		countOwningBuilding--;
		if (countOwningBuilding < 0) {
			countOwningBuilding = 0;
		}
	}
	
	public void increaseOwningHotelCount()
	{
		countOwningHotel++;
	}
	
	public void decreaseOwningHotelCount()
	{
		countOwningHotel--;
		if (countOwningHotel < 0) {
			countOwningHotel = 0;
		}
	}
	
	public void increaseOwningLandmarkCount()
	{
		countOwningLandmark++;
	}
	
	public void decreaseOwningLandmarkCount()
	{
		countOwningLandmark--;
		if (countOwningLandmark < 0) {
			countOwningLandmark = 0;
		}
	}
	
	// Player의 정보 화면 갱신
	public void updatePlayerInfomation()
	{
		panelPlayer.setTurnCount(currentTurn);
		panelPlayer.setMoney(money);
		panelPlayer.setCityCount(countOwningCity);
		panelPlayer.setResortCount(countOwningResort);
		panelPlayer.setVillaCount(countOwningVilla);
		panelPlayer.setBuildingCount(countOwningBuilding);
		panelPlayer.setHotelCount(countOwningHotel);
		panelPlayer.setLandmarkCount(countOwningLandmark);
	}
	
	@Override
	public int compareTo(Player p) {
		// Step 1. 자산가치 비교 (내림차순)
		int result = gameboard.getTotalMoney(p.order, p.money) - gameboard.getTotalMoney(this.order, this.money);
		
		// Step 2. Current Turn Count 비교  (내림차순) 
		if (result == 0) {
			result = p.currentTurn - this.currentTurn;
		}
		
		// Step 3. Current Area Index 비교  (내림차순)
		if (result == 0) {
			result = p.currentLocationIndex - this.currentLocationIndex;
		}
		
		// Step 4. Initial Order 비교 (오름차순)
		if (result == 0) {
			result = this.order - p.order;
		}
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}

//차후 Player클래스와 통합?
class PlayerPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int order; //플레이어의 순서
	String name; //플레이어 이름
	Color color; // 플레이어의 고유 색상
	boolean isMyTurn = false;
	boolean isBlinkTemp;
	
	GridBagConstraints gbc;
	JLabel labelOrder;
	JLabel labelName;
	JLabel labelTurnTitle;
	JLabel labelTurn;
	JLabel labelPlayer;
	JLabel labelMoney;
	JLabel labelCity;
	JLabel labelResort;
	JLabel labelVilla;
	JLabel labelBuilding;
	JLabel labelHotel;
	JLabel labelLandmark;
	
	private Timer swingTimer;
	
	PlayerPanel()
	{
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.fill = GridBagConstraints.BOTH;
		
		ImageIcon iconPlayer = getResizeIcon("image/player_empty.gif", 0.5);
		ImageIcon iconCoin = getResizeIcon("image/coin.gif", 0.8);
		ImageIcon iconCity = getResizeIcon("image/city.gif", 0.6);
		ImageIcon iconResort = getResizeIcon("image/resort.gif", 0.6);
		ImageIcon iconVilla = getResizeIcon("image/villa.gif", 0.6);
		ImageIcon iconBuilding = getResizeIcon("image/building.gif", 0.6);
		ImageIcon iconHotel = getResizeIcon("image/hotel.gif", 0.6);
		ImageIcon iconLandmark = getResizeIcon("image/landmark.gif", 0.6);
		
		labelOrder = new JLabel("1st", JLabel.CENTER);
		labelName = new JLabel("홍길동", JLabel.CENTER);
		labelTurnTitle = new JLabel("턴", JLabel.CENTER);
		labelTurn = new JLabel("1", new ImageIcon("image/dummy.gif"), JLabel.CENTER);
		labelPlayer = new JLabel(iconPlayer, JLabel.CENTER);
		labelMoney = new JLabel("20만 ", iconCoin, JLabel.LEFT);
		labelCity = new JLabel("3", iconCity, JLabel.CENTER);
		labelResort = new JLabel("4", iconResort, JLabel.CENTER);
		labelVilla = new JLabel("99", iconVilla, JLabel.CENTER);
		labelBuilding = new JLabel("99", iconBuilding, JLabel.CENTER);
		labelHotel = new JLabel("99", iconHotel, JLabel.CENTER);
		labelLandmark = new JLabel("99", iconLandmark, JLabel.CENTER);
		
		labelOrder.setFont(new Font("Times New Roman", Font.BOLD, 24));
		
		labelOrder.setOpaque(true);
		labelName.setOpaque(true);
		labelTurnTitle.setOpaque(true);
		labelTurn.setOpaque(true);
		labelPlayer.setOpaque(true);
		labelMoney.setOpaque(true);
		labelCity.setOpaque(true);
		labelResort.setOpaque(true);
		labelVilla.setOpaque(true);
		labelBuilding.setOpaque(true);
		labelHotel.setOpaque(true);
		labelLandmark.setOpaque(true);
		
		setLayoutGridBagConstraints(labelOrder, 0, 0, 2, 1);
		setLayoutGridBagConstraints(labelName, 2, 0, 2, 1);
		setLayoutGridBagConstraints(labelTurnTitle, 4, 0, 1, 1);
		setLayoutGridBagConstraints(labelTurn, 5, 0, 1, 1);
		setLayoutGridBagConstraints(labelPlayer, 0, 1, 2, 2);
		setLayoutGridBagConstraints(labelMoney, 2, 1, 2, 1);
		setLayoutGridBagConstraints(labelCity, 4, 1, 1, 1);
		setLayoutGridBagConstraints(labelResort, 5, 1, 1, 1);
		setLayoutGridBagConstraints(labelVilla, 2, 2, 1, 1);
		setLayoutGridBagConstraints(labelBuilding, 3, 2, 1, 1);
		setLayoutGridBagConstraints(labelHotel, 4, 2, 1, 1);
		setLayoutGridBagConstraints(labelLandmark, 5, 2, 1, 1);
		
		ActionListener timerTaskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) 
			{
				isBlinkTemp = !isBlinkTemp;
				Color color = (isBlinkTemp) ? new Color(243, 248, 90) : new Color(238, 238, 238);
				
				labelName.setBackground(color);
				labelTurn.setBackground(color);
				labelMoney.setBackground(color);
				labelCity.setBackground(color);
				labelResort.setBackground(color);
				labelVilla.setBackground(color);
				labelBuilding.setBackground(color);
				labelHotel.setBackground(color);
				labelLandmark.setBackground(color);
			}
		};
		swingTimer = new Timer(500, timerTaskPerformer); // milliseconds
	}
	
	private void setLayoutGridBagConstraints(Component obj, int x, int y, int width, int height)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		((JComponent) obj).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		add(obj, gbc);
	}
	
	void setCurrentPlayer(boolean isMyTurn)
	{
		this.isMyTurn = isMyTurn;
		
		if (isMyTurn) {
			swingTimer.start();
		}
		else {
			swingTimer.stop();
			
			Color color = new Color(238, 238, 238);
			labelName.setBackground(color);
			labelTurn.setBackground(color);
			labelMoney.setBackground(color);
			labelCity.setBackground(color);
			labelResort.setBackground(color);
			labelVilla.setBackground(color);
			labelBuilding.setBackground(color);
			labelHotel.setBackground(color);
			labelLandmark.setBackground(color);
		}
	}
	
	void setPlayerInfo(String name, Color color, String iconName)
	{
		this.name = name;
		this.color = color;
		
		labelName.setText(name);
		labelOrder.setBackground(color);
		labelTurnTitle.setBackground(color);
		labelPlayer.setIcon(getResizeIcon(iconName, 0.5));
	}
	
	void setRank(int rank)
	{
		String rankStr = "";
		
		switch (rank) {
		case 1:
			rankStr = rank + "st";
			break;
		case 2:
			rankStr = rank + "nd";
			break;
		case 3:
			rankStr = rank + "rd";
			break;
		case 4:
		default:
			rankStr = rank + "th";
			break;
		}
		
		labelOrder.setText(rankStr);
	}
	
	void setTurnCount(int count)
	{
		labelTurn.setText(String.valueOf(count));
	}
	
	void setMoney(int money)
	{
		labelMoney.setText(money + "만");
	}
	
	void setCityCount(int count)
	{
		labelCity.setText(String.valueOf(count));
	}
	
	void setResortCount(int count)
	{
		labelResort.setText(String.valueOf(count));
	}
	
	void setVillaCount(int count)
	{
		labelVilla.setText(String.valueOf(count));
	}
	
	void setBuildingCount(int count)
	{
		labelBuilding.setText(String.valueOf(count));
	}
	
	void setHotelCount(int count)
	{
		labelHotel.setText(String.valueOf(count));
	}
	
	void setLandmarkCount(int count)
	{
		labelLandmark.setText(String.valueOf(count));
	}
	
	ImageIcon getResizeIcon(String imgName, double scale)
	{
		ImageIcon icon = new ImageIcon(imgName);
		return new ImageIcon(icon.getImage().getScaledInstance((int)(icon.getIconWidth()*scale), (int)(icon.getIconHeight()*scale), Image.SCALE_SMOOTH));		
	}
}
