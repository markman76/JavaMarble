package Marble;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MarbleMain extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int totalTurnCount = 0; // 총 턴수
	int numberOfPlayer = 0; // 참여  플레이어 수
	OrderCard[] orderCards;
	
	JDialog dialogStart = null;
	JDialog dialogChoicePlayer = null;
	JCheckBox[] checkPlayer;
	JLabel[] labelPlayer;
	JTextField[] textFieldPlayer;
	JPanel[] panelPlayer;
	JPanel panelCenter = null;
	
	JLabel[] labelPlayerOrder;
	JPanel panelCenterOrder = null;
	JComboBox<Integer> comboBoxTotalTurn = null;
	
	class PlayerCheckEventHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent e) {
			textFieldPlayer[2].setEnabled(checkPlayer[2].isSelected());
			textFieldPlayer[3].setEnabled(checkPlayer[3].isSelected());
			
			numberOfPlayer = 2;
			if (checkPlayer[2].isSelected()) {
				numberOfPlayer++;
			}
			if (checkPlayer[3].isSelected()) {
				numberOfPlayer++;
			}
		}
	}
	
	MarbleMain()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initDialog1();
		initDialog2();
	}
	
	void initDialog1()
	{
		dialogStart = new JDialog(this, "자바마블", true);
		dialogStart.setSize(800, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialogStart.setLocation(screenSize.width/2-dialogStart.getWidth()/2, screenSize.height/2-dialogStart.getHeight()/2);
		dialogStart.setLayout(null);
		dialogStart.setResizable(false);
		dialogStart.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				dialogStart.setVisible(false);
				dialogStart.dispose();
			}
		});
		
		JLabel labelTop = new JLabel("『자바 프로그래밍 실습 프로젝트』", JLabel.CENTER);
		labelTop.setBounds(10, 10, 320, 40);
		labelTop.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		labelTop.setForeground(Color.BLUE);
		
		JLabel labelCenter1 = new JLabel("Java Marble", JLabel.CENTER);
		labelCenter1.setBounds(50, 160, 700, 200);
		labelCenter1.setFont(new Font("Times New Roman", Font.PLAIN, 140));
		
		JLabel labelCenter2 = new JLabel("자바프로그래밍 전문가과정 10기 (2013/7/1 ~ 12/27)", JLabel.CENTER);
		labelCenter2.setBounds(14, 518, 390, 40);
		labelCenter2.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		labelCenter2.setForeground(Color.BLUE);
		
		JLabel labelBottom1 = new JLabel("마우스로 화면을 클릭하세요.", JLabel.CENTER);
		labelBottom1.setBounds(250, 330, 300, 40);
		labelBottom1.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		labelBottom1.setForeground(Color.RED);
		
		JLabel labelBottom2 = new JLabel("프로젝트 참여자 : 노명규, 박지은", JLabel.CENTER);
		labelBottom2.setBounds(473, 513, 320, 54);
		labelBottom2.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		labelBottom2.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		labelBottom2.setOpaque(true);
		labelBottom2.setBackground(Color.LIGHT_GRAY);
		
		dialogStart.add(labelTop);
		dialogStart.add(labelCenter1);
		dialogStart.add(labelCenter2);
		dialogStart.add(labelBottom1);
		dialogStart.add(labelBottom2);
		dialogStart.setVisible(true);
	}
	
	void initDialog2()
	{
		// 1. Top
		JPanel panelTop = new JPanel();
		panelTop.setOpaque(true);
		panelTop.setBackground(Color.YELLOW);
		panelTop.add(new JLabel("참여할 Player 선택 / 이름 입력", JLabel.CENTER));
		panelTop.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		checkPlayer = new JCheckBox[4];
		labelPlayer = new JLabel[4];
		textFieldPlayer = new JTextField[4];
		panelPlayer = new JPanel[4];
		labelPlayerOrder = new JLabel[4];
		
		// 2. Center
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(1,4));
		panelCenter.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		panelCenterOrder = new JPanel();
		panelCenterOrder.setLayout(new GridLayout(1,4));
		panelCenterOrder.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelCenterOrder.setOpaque(true);
		
		String[] defaultPlayerName = {"싸이", "귀염이", "야옹이", "예쁜이"};
		
		for (int i=0; i<4; i++) {
			checkPlayer[i] = new JCheckBox();
			checkPlayer[i].addItemListener(new PlayerCheckEventHandler());
			JPanel panelTitle = new JPanel();
			panelTitle.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			panelTitle.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			panelTitle.add(checkPlayer[i]);
			
			labelPlayer[i] = new JLabel(new ImageIcon("image/player" + (i+1) + ".gif"), JLabel.CENTER);
			labelPlayer[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			labelPlayer[i].setOpaque(true);
			labelPlayer[i].setBackground(Color.WHITE);
			
			textFieldPlayer[i] = new JTextField(defaultPlayerName[i]);
			textFieldPlayer[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			
			panelPlayer[i] = new JPanel();
			panelPlayer[i].setLayout(new BorderLayout());
			panelPlayer[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			panelPlayer[i].add(panelTitle, BorderLayout.NORTH);
			panelPlayer[i].add(labelPlayer[i], BorderLayout.CENTER);
			panelPlayer[i].add(textFieldPlayer[i], BorderLayout.SOUTH);
			
			panelCenter.add(panelPlayer[i]);
			
			// 순번
			labelPlayerOrder[i] = new JLabel("·", JLabel.CENTER);
			labelPlayerOrder[i].setSize(240, 120);
			labelPlayerOrder[i].setBorder(BorderFactory.createLineBorder(new Color(102, 51, 0)));
			labelPlayerOrder[i].setOpaque(true);
			labelPlayerOrder[i].setForeground(new Color(102, 51, 0));
			labelPlayerOrder[i].setBackground(new Color(255, 255, 255));
			labelPlayerOrder[i].setFont(new Font("Times New Roman", Font.PLAIN, 160));
			panelCenterOrder.add(labelPlayerOrder[i]);
		}
		
		JButton buttonOk = new JButton("순서 결정");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String buttonTitle = ((JButton)e.getSource()).getText();
				if (buttonTitle == "순서 결정") {
					// Player 생성  및 초기화 (순서 결정)
					//   (1) Dialog창을 띄워서  각 Player들이 카드 선택
					//   (2) {1 ~ 참여인원 수} 범위로 임의로 번호 생성
					//   (3) 각 Player에 순서 지정
					
					HashSet<String> hashSet = new HashSet<String>();
					while (hashSet.size() < numberOfPlayer) {
						int num = (int)(Math.random()*numberOfPlayer) + 1;
						hashSet.add(new String(""+num));
					}
					
					ArrayList<String> list = new ArrayList<String>(hashSet);
					Collections.shuffle(list);
					
					Color[] defaultPlayerColor = {Color.ORANGE, new Color(153, 0, 255), Color.RED, new Color(0, 112, 192)};
					
					int j = 0;
					for (int i=0; i<4; i++) {
						if (j < list.size() && checkPlayer[i].isSelected()) {
							labelPlayerOrder[i].setBackground(defaultPlayerColor[i]);
							labelPlayerOrder[i].setText(list.get(j));
							j++;
						}
					}
					
					dialogChoicePlayer.add(panelCenterOrder, BorderLayout.CENTER);
					panelCenter.setVisible(false);
					((JButton)e.getSource()).setText("게임 시작");
				}
				else {
					totalTurnCount = ((Integer)comboBoxTotalTurn.getSelectedItem()).intValue(); 
					
					orderCards = new OrderCard[numberOfPlayer];
					
					int j = 0;
					for (int i=0; i<4; i++) {
						if (j < numberOfPlayer && checkPlayer[i].isSelected()) {
							orderCards[j] = new OrderCard();
							orderCards[j].order = Integer.parseInt(labelPlayerOrder[i].getText());
							orderCards[j].name = textFieldPlayer[i].getText();
							orderCards[j].color = labelPlayerOrder[i].getBackground();
							orderCards[j].iconName = ((ImageIcon)labelPlayer[i].getIcon()).getDescription();
							j++;
						}
					}
					
					dialogChoicePlayer.setVisible(false);
					dialogChoicePlayer.dispose();
				}
			}
		});
		
		Vector<Integer> vector = new Vector<Integer>(31);
		for (int i=2; i<=32; i++) {
			vector.add(new Integer(i));
		}
		comboBoxTotalTurn = new JComboBox<Integer>(vector);
		comboBoxTotalTurn.setSelectedIndex(0);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		panelBottom.add(new JLabel("                                                                     "));
		panelBottom.add(buttonOk);
		panelBottom.add(new JLabel("                              "));
		panelBottom.add(new JLabel("Total Turn : ", JLabel.RIGHT));
		panelBottom.add(comboBoxTotalTurn);
		
		checkPlayer[0].setSelected(true);
		checkPlayer[0].setEnabled(false);
		checkPlayer[1].setSelected(true);
		checkPlayer[1].setEnabled(false);
		
		dialogChoicePlayer = new JDialog(this, "참여할 Player 선택 / 이름 입력", true);
		dialogChoicePlayer.setSize(540, 302);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialogChoicePlayer.setLocation(screenSize.width/2-dialogChoicePlayer.getWidth()/2, screenSize.height/2-dialogChoicePlayer.getHeight()/2);
		dialogChoicePlayer.setLayout(new BorderLayout());
		dialogChoicePlayer.setResizable(false);
		dialogChoicePlayer.add(panelTop, BorderLayout.NORTH);
		dialogChoicePlayer.add(panelCenter, BorderLayout.CENTER);
		dialogChoicePlayer.add(panelBottom, BorderLayout.SOUTH);
		dialogChoicePlayer.setVisible(true);
	}
	
	int getTotalTurnCount()
	{
		return totalTurnCount;
	}
	
	int getPlayerNumber()
	{
		return numberOfPlayer;
	}
	
	OrderCard[] getOrderCards()
	{
		return orderCards;
	}
	
	public static void main(String[] args)
	{
		MarbleMain marbleMain = new MarbleMain();
		
		int totalTurnCount = marbleMain.getTotalTurnCount();
		int number = marbleMain.getPlayerNumber();
		OrderCard[] cards = marbleMain.getOrderCards();
		if (cards != null) {
			switch (number) {
			case 4:
				cards[0].order = 4;
				cards[1].order = 1;
				cards[2].order = 3;
				cards[3].order = 2;
				break;
				
			case 3:
				cards[0].order = 3;
				cards[1].order = 1;
				cards[2].order = 2;
				break;
			case 2:
				cards[1].order = 1;
				cards[0].order = 2;
				break;
			}
			
			new GameBoard(totalTurnCount, number, cards); //매개변수: 플레이어 수, 순번카드
		}
	}
}

