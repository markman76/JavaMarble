package Marble;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Dice extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int diceTotalNumber = 0;
	private boolean isDouble = false;
	JButton buttonRollDice = null;
	JPanel panelDiceSquares = null;
	DiceSquare panelDiceSquare1 = null;
	DiceSquare panelDiceSquare2 = null;
	GameBoard gameboard = null;
	
	class DiceSquare extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private int number;
		
		DiceSquare()
		{
			setSize(120, 120);
			setBorder(BorderFactory.createLineBorder(new Color(102, 51, 0)));
			setOpaque(true);
			setForeground(new Color(102, 51, 0));
			setBackground(new Color(255, 255, 255));
		}
		
		public void paint(Graphics g)
		{
			super.paint(g);
			
			switch (number) {
			case 1:
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.5)-7);
				break;
				
			case 2:
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.3)-7);
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.7)-7);
				break;
				
			case 3:
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.25)-7);
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.5)-7);
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.75)-7);
				break;
				
			case 4:
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.3)-7);
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.7)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.3)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.7)-7);
				break;
				
			case 5:
				paintDiceCircle(g, (int)(getWidth()*0.5)-7, (int)(getHeight()*0.5)-7);
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.3)-7);
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.7)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.3)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.7)-7);
				break;
				
			case 6:
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.25)-7);
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.5)-7);
				paintDiceCircle(g, (int)(getWidth()*0.3)-7, (int)(getHeight()*0.75)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.25)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.5)-7);
				paintDiceCircle(g, (int)(getWidth()*0.7)-7, (int)(getHeight()*0.75)-7);
				break;
			}
		}
		
		void paintDiceCircle(Graphics g, int x, int y)
		{
			g.setColor(Color.RED);
			g.fillOval(x,y, 14,14);
			g.setColor(Color.BLACK);
			g.drawOval(x,y, 14,14);
		}
		
		int getDiceNumber()
		{
			number = (int)(Math.random() * 6) + 1;
			return number;
		}
	}
	
	Dice()
	{
		gameboard = GameBoard.getInstance();
		
		setLayout(new BorderLayout());
		
		buttonRollDice = new JButton("주사위 던지기");
		buttonRollDice.setSize(240, 120);
		buttonRollDice.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		buttonRollDice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) 
			{
				gameboard.soundPlay("sound/sound2.wav");
				diceTotalNumber = getDiceNumber();
				// 주사위표현
				viewDiceSquare();
			}
		});
		add(buttonRollDice, BorderLayout.CENTER);
		
		panelDiceSquare1 = new DiceSquare();
		panelDiceSquare2 = new DiceSquare();
		
		panelDiceSquares = new JPanel();
		panelDiceSquares.setLayout(new GridLayout(1, 2));
		panelDiceSquares.add(panelDiceSquare1);
		panelDiceSquares.add(panelDiceSquare2);
		add(panelDiceSquares, BorderLayout.CENTER);
		
		viewDiceButton();
	}
	
	// Dialog에서 사용자의 선택이 필요한 메소드
	public int waitThrowingDice()
	{
		int diceTotalNumber = 0;
		while (diceTotalNumber == 0) {
			diceTotalNumber = this.diceTotalNumber;
			try {
				Thread.sleep(500);
			} catch(Exception e) {}
		}
		
		this.diceTotalNumber = 0;
		
		return diceTotalNumber;
	}
	
	public boolean isDouble()
	{
		return isDouble;
	}
	
	// [주사위 던지기] 버튼 표시
	public void viewDiceButton()
	{
		panelDiceSquares.setVisible(false);
		buttonRollDice.setVisible(true);
	}
	
	// [주사위 던지기] 버튼 클릭 후 주사위 표시
	public void viewDiceSquare()
	{
		buttonRollDice.setVisible(false);
		panelDiceSquares.setVisible(true);
	}
	
	private int getDiceNumber()
	{
		int number1 = panelDiceSquare1.getDiceNumber();
		int number2 = panelDiceSquare2.getDiceNumber();
		
		isDouble = (number1 == number2) ? true : false;
		
		return number1 + number2;
	}
}
