package Marble;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Olympic extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int hostingCost = 5; //올림픽 개최 비용
	private int hostedIndex;
	JDialog dialogSelectArea = null;
	JList<String> listArea = null;
	DefaultListModel<String> listModel = null;
	GameBoard gameboard = null;
	
	Olympic(int index, String name)
	{
		super(index, name, Area.Type.OLYMPIC);
		labelTop.setBackground(new Color(200, 254, 173));
		labelCenter.setBackground(new Color(200, 254, 173));
		labelBottom.setBackground(new Color(200, 254, 173));
		
		gameboard = GameBoard.getInstance();
		
		dialogSelectArea = new JDialog(gameboard, "올림픽 개최 도시 선택", true);
		dialogSelectArea.setSize(340, 400);
		dialogSelectArea.setLocation(gameboard.getWidth()/2-getWidth()/2, gameboard.getHeight()/2-getHeight()/2);
		dialogSelectArea.setLayout(new BorderLayout());
		dialogSelectArea.setResizable(false);
		
		dialogSelectArea.add(new JLabel("올림픽 개최하고 싶은 도시를 선택하세요.", JLabel.CENTER), BorderLayout.NORTH);
		listModel = new DefaultListModel<String>();
		listArea = new JList<String>(listModel);
		JScrollPane scrollPaneListArea = new JScrollPane(listArea);
		scrollPaneListArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dialogSelectArea.add(scrollPaneListArea, BorderLayout.CENTER);
		
		JPanel panelButtons = new JPanel();
		JButton buttonOk = new JButton("OK");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hostedIndex = listArea.getSelectedIndex();
				if (hostedIndex == -1) {
					JOptionPane.showMessageDialog(gameboard, "개최할 도시를 선택하세요.", "올림픽", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				dialogSelectArea.setVisible(false);
			}
		});
		panelButtons.add(buttonOk);
		
		JLabel labelDummy = new JLabel("    ");
		panelButtons.add(labelDummy);
		
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hostedIndex = -1;
				dialogSelectArea.setVisible(false);
			}
		});
		panelButtons.add(buttonCancel);
		dialogSelectArea.add(panelButtons, BorderLayout.SOUTH);
		
		dialogSelectArea.setVisible(false);
	}
	
	public int waitSelectCandidateCity(int playerOrder)
	{
		hostedIndex = -1;
		int count = setCandidateCityList(playerOrder);
		if (count <= 0) {
			JOptionPane.showMessageDialog(gameboard, "소유하고 있는 도시가 없으므로 올림픽을 개최할 수 없습니다.", "올림픽", JOptionPane.INFORMATION_MESSAGE);
			return -1;
		}
		
		dialogSelectArea.setVisible(true);
		
		if (hostedIndex != -1) {
			String cityName = listModel.getElementAt(hostedIndex);
			int lastIndex = cityName.indexOf(']');
			hostedIndex = Integer.parseInt(cityName.substring(1, lastIndex));
		}
		
		return hostedIndex;
	}
	
	public int setCandidateCityList(int playerOrder)
	{
		listModel.removeAllElements();
		for (int i=0; i<gameboard.area.length; i++) {
			if (gameboard.area[i].type != Type.CITY) {
				continue;
			}
			
			if (!((City)gameboard.area[i]).isOwner()) {
				continue;
			}
			
			if (gameboard.player[playerOrder-1].name != ((City)gameboard.area[i]).owner) {
				continue;
			}
			
			if (((City)gameboard.area[i]).getHostedOlympicCount() > 4) {
				continue;
			}
			
			listModel.addElement("[" + (i) + "] " + gameboard.area[i].getName() + " (개최횟수: " + ((City)gameboard.area[i]).getHostedOlympicCount() + ")");
		}
		
		
		return listModel.getSize();
	}
	
	public int getHostingOlympicCost()
	{
		return hostingCost;
	}
}
