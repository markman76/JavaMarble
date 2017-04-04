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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class WorldTravel extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final int travelCost = 5;
	private int travelIndex;
	JDialog dialogSelectDestinationArea = null;
	JList<String> listArea = null;
	GameBoard gameboard = null;
	
	WorldTravel(int index, String name)
	{
		super(index, name, Area.Type.WORLD_TRAVEL);
		labelTop.setBackground(new Color(200, 254, 173));
		labelCenter.setBackground(new Color(200, 254, 173));
		labelBottom.setBackground(new Color(200, 254, 173));
		
		gameboard = GameBoard.getInstance();
		
		dialogSelectDestinationArea = new JDialog(gameboard, "여행지 선택", true);
		dialogSelectDestinationArea.setSize(340, 400);
		dialogSelectDestinationArea.setLocation(gameboard.getWidth()/2-getWidth()/2, gameboard.getHeight()/2-getHeight()/2);
		dialogSelectDestinationArea.setLayout(new BorderLayout());
		dialogSelectDestinationArea.setResizable(false);
		
		dialogSelectDestinationArea.add(new JLabel("가고 싶은 지역을 선택하세요.", JLabel.CENTER), BorderLayout.NORTH);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (int i=0; i<gameboard.areaNameList.length; i++) {
			listModel.addElement("[" + (i) + "] " + gameboard.areaNameList[i]);
		}
		
		listArea = new JList<String>(listModel);
		JScrollPane scrollPaneListArea = new JScrollPane(listArea);
		scrollPaneListArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	//	listArea.addListSelectionListener(new ListSelectionListener() {
	//		public void valueChanged(ListSelectionEvent e) {
	//			travelIndex = listArea.getSelectedIndex();
	//		}
	//	});
		dialogSelectDestinationArea.add(scrollPaneListArea, BorderLayout.CENTER);
		
		JPanel panelButtons = new JPanel();
		JButton buttonOk = new JButton("OK");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				travelIndex = listArea.getSelectedIndex();
				dialogSelectDestinationArea.setVisible(false);
			}
		});
		panelButtons.add(buttonOk);
		dialogSelectDestinationArea.add(panelButtons, BorderLayout.SOUTH);
		
		dialogSelectDestinationArea.setVisible(false);
	}
	
	public int waitSelectDestinationArea()
	{
		travelIndex = this.index;
		listArea.setSelectedIndex(this.index);
		dialogSelectDestinationArea.setVisible(true);
		return travelIndex;
	}
	
	public int getTravelCost()
	{
		return travelCost;
	}
}
