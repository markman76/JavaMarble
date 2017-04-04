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

	final int DEFAULT_MONEY = 200; //�÷��̾��� �Ӵ� �ʱ�ȭ
	
	int order; //�÷��̾��� ���� ����
	String name; //�÷��̾� �̸�
	Color color; // �÷��̾��� ���� ����
	int rank; // �÷��̾��� �������
	int money = DEFAULT_MONEY; //��������
	boolean isLive = true; // ��������/���� ����
	boolean isMyTurn = false;
	int previousLocationIndex = 0; // ������ ��ġ�ߴ� Area (���迩�� ���� ó��)
	int currentLocationIndex = 0; // ���� ��ġ�ϰ� �ִ� Area
	int doubleCount = 0;
	int currentTurn = 1; //���� �ϼ�
	int restTurnCount = 3; // �̵������ϼ� (���ε�)
	private int countOwningCity = 0; // �������� ����
	private int countOwningResort = 0; // �������� �޾���
	private int countOwningVilla = 0; // �������� ����
	private int countOwningBuilding = 0; // �������� ����
	private int countOwningHotel = 0; // �������� ȣ��
	private int countOwningLandmark = 0; // �������� ���帶ũ
	
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
	
	// Player�� ������ �̷������ ���� �޼ҵ� (Sequence Chart�� �����Ͽ� ������ ����)
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
					
					JOptionPane.showMessageDialog(gameboard, "���ε��� Ż���߽��ϴ�. �ֻ����� ��������.", "���ε�", JOptionPane.INFORMATION_MESSAGE);
					
					normal_state(); // goto Normal state
					break;
				}
			}
			
			movingPoint = gameboard.dice.waitThrowingDice();
			
			// ���ε� Ż��
			if (gameboard.dice.isDouble()) {
				JOptionPane.showMessageDialog(gameboard, "������ �������Ƿ� ���ε��� Ż���մϴ�.", "���ε�", JOptionPane.INFORMATION_MESSAGE);
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
			
			// ����� ���ް���?
			if (gameboard.areaWorldTravel.getTravelCost() <= money) {
				int destenationLocationIndex = gameboard.areaWorldTravel.waitSelectDestinationArea();
				
				// ����� ���� (money ����)
				money -= gameboard.areaWorldTravel.getTravelCost();
				
				// �̵��� ���� ���� (��ġ�� ����)
				doubleCount = 0;
				movingPoint = gameboard.getMovingPoint(currentLocationIndex, destenationLocationIndex);
				moving_state(movingPoint); // goto Moving state
				break;
			}
			else {
				JOptionPane.showMessageDialog(gameboard, "������ �����Ͽ� ���迩���� �� �� �����ϴ�.", "���迩��", JOptionPane.INFORMATION_MESSAGE);
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
				// go ���ε�
				JOptionPane.showMessageDialog(gameboard, "������ 3�� �̻� ���Խ��ϴ�. ���� ��Ģ���� ���ε��� �̵��մϴ�.", "���ε�", JOptionPane.INFORMATION_MESSAGE);
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
		
		// �� �̵�
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
			// ���ξ���
			if (!((City)gameboard.area[currentLocationIndex]).isOwner()) {
				// ������
				if (((City)gameboard.area[currentLocationIndex]).waitBuyLand(money, currentTurn)) {
					money -= ((City)gameboard.area[currentLocationIndex]).getLandCost();
					((City)gameboard.area[currentLocationIndex]).buyLand(order);
					increaseOwningCityCount();
					construction_state(); // goto Construction state
				}
				
				// goto Check state
			}
			else {
				// �ٸ����
				if (gameboard.player[order-1].name != ((City)gameboard.area[currentLocationIndex]).owner) {
					// ���༼ ����
					gameboard.area[currentLocationIndex].waitNoticeChargeTravelingTax();
					int travelingTax = gameboard.area[currentLocationIndex].getTravelingTax();
					if (money < travelingTax) {
						JOptionPane.showMessageDialog(gameboard, "������ �����Ͽ�  ������ �ε����� ó���մϴ�.", "���༼", JOptionPane.INFORMATION_MESSAGE);
						// ���� �ε��� �Ű�
						int sellMoney = gameboard.sellCity(order, travelingTax);
						money += sellMoney;
					}
					money -= travelingTax;
					((City)gameboard.area[currentLocationIndex]).giveTravelingTax(order);
					
					// ��/�ǹ� ���� (���帶ũ ���� �� ������������ ���Ű��� ���� üũ)
					if (money > 0) {
						if (((City)gameboard.area[currentLocationIndex]).waitBuyCity(money)) {
							money -= ((City)gameboard.area[currentLocationIndex]).getDisposalCost();
							((City)gameboard.area[currentLocationIndex]).transferCity(order);
							construction_state();
						}
					}
				}
				// �ڽ�
				else {
					construction_state();
				}
			}
			
			check_state(); // goto Check state
			break;
			
		case RESORT:
			// ���ξ���
			if (!((Resort)gameboard.area[currentLocationIndex]).isOwner()) {
				// ������
				if (((Resort)gameboard.area[currentLocationIndex]).waitBuyLand(money)) {
					money -= ((Resort)gameboard.area[currentLocationIndex]).getLandCost();
					((Resort)gameboard.area[currentLocationIndex]).buyLand(order);
					increaseOwningResortCount();
				}
				
				// goto Check state
			}
			else {
				// �ٸ����
				if (gameboard.player[order-1].name != ((Resort)gameboard.area[currentLocationIndex]).owner) {
					// ���༼ ����
					gameboard.area[currentLocationIndex].waitNoticeChargeTravelingTax();
					int travelingTax = gameboard.area[currentLocationIndex].getTravelingTax();
					if (money < travelingTax) {
						JOptionPane.showMessageDialog(gameboard, "������ �����Ͽ�  ������ �ε����� ó���մϴ�.", "���༼", JOptionPane.INFORMATION_MESSAGE);
						// ���� �ε��� �Ű�
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
			JOptionPane.showMessageDialog(gameboard, "���ε��� �������ϴ�. Ż���ҷ��� ������ ������ �����ؾ��մϴ�.\n"
					+ "    - ���ε� Ż����(" + gameboard.areaDesert.getEscapeCost() + "��) ����\n"
					+ "    - �ֻ����� ������ ����\n"
					+ "    - �����ϼ�(" + restTurnCount + "��) ����\n", 
					"���ε�", JOptionPane.INFORMATION_MESSAGE);
			break; // goto Root state
			
		case OLYMPIC:
			int index = gameboard.areaOlympic.waitSelectCandidateCity(order);
			if (index != -1) {
				if (gameboard.areaOlympic.getHostingOlympicCost() <= money) {
					// ���ֺ�� ����
					money -= gameboard.areaOlympic.getHostingOlympicCost();
					
					// ����Ƚ�� & ���༼ ����
					((City)gameboard.area[index]).addHostedOlympicCount();
				}
			}
			
			check_state(); // goto Check state
			break;
			
		case WORLD_TRAVEL:
			JOptionPane.showMessageDialog(gameboard, "���� �Ͽ��� ���迩���� �մϴ�.", "���迩��", JOptionPane.INFORMATION_MESSAGE);
			check_state(); // goto Check state
			break;
			
		case TAX_SERVICE:
			// ��꼼 ����
			money -= gameboard.areaTaxService.getTax(order);
			check_state(); // goto Check state
			break;
		}
	}
	
	void construction_state()
	{
		// �ּ��ϼ� ����?
		if (currentTurn < 2) {
//x			JOptionPane.showMessageDialog(gameboard, "�ּ� 2�� �̻� ���ƾ� �ǹ��� �Ǽ��� �� �ֽ��ϴ�.", "����", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// ���帶ũ ���翩��?
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
				JOptionPane.showMessageDialog(gameboard, "�������� �����ϸ� ���ϱ�(" + gameboard.areaStartPoint.getPaycheck() + "��)�� �����մϴ�.", "������", JOptionPane.INFORMATION_MESSAGE);
				money += gameboard.areaStartPoint.getPaycheck(); // ���ϱ� ����
			}
			
			// �������� Ȯ���ϱ� ���� �ð�����
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
			JOptionPane.showMessageDialog(gameboard, "������ ������ �������Ƿ� �ѹ��� �ֻ����� ��������.", "��������", JOptionPane.INFORMATION_MESSAGE);
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
	
	// Player�� ���� ȭ�� ����
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
		// Step 1. �ڻ갡ġ �� (��������)
		int result = gameboard.getTotalMoney(p.order, p.money) - gameboard.getTotalMoney(this.order, this.money);
		
		// Step 2. Current Turn Count ��  (��������) 
		if (result == 0) {
			result = p.currentTurn - this.currentTurn;
		}
		
		// Step 3. Current Area Index ��  (��������)
		if (result == 0) {
			result = p.currentLocationIndex - this.currentLocationIndex;
		}
		
		// Step 4. Initial Order �� (��������)
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

//���� PlayerŬ������ ����?
class PlayerPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int order; //�÷��̾��� ����
	String name; //�÷��̾� �̸�
	Color color; // �÷��̾��� ���� ����
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
		labelName = new JLabel("ȫ�浿", JLabel.CENTER);
		labelTurnTitle = new JLabel("��", JLabel.CENTER);
		labelTurn = new JLabel("1", new ImageIcon("image/dummy.gif"), JLabel.CENTER);
		labelPlayer = new JLabel(iconPlayer, JLabel.CENTER);
		labelMoney = new JLabel("20�� ", iconCoin, JLabel.LEFT);
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
		labelMoney.setText(money + "��");
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
