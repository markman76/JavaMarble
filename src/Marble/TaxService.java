package Marble;

import java.awt.Color;

import javax.swing.JOptionPane;

public class TaxService extends Area
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	GameBoard gameboard = null;
	
	TaxService(int index, String name)
	{
		super(index, name, Area.Type.TAX_SERVICE);
		labelTop.setBackground(new Color(200, 254, 173));
		labelCenter.setBackground(new Color(200, 254, 173));
		labelBottom.setBackground(new Color(200, 254, 173));
		
		gameboard = GameBoard.getInstance();
	}

	public int getTax(int playerOrder)
	{
		int totalPropertyValue = 0;
		
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
			
			if (((City)gameboard.area[i]).isExistLandmark()) {
				totalPropertyValue += ((City)gameboard.area[i]).getLandmarkCost();
			}
			if (((City)gameboard.area[i]).isExistHotel()) {
				totalPropertyValue += ((City)gameboard.area[i]).getHotelCost();
			}
			if (((City)gameboard.area[i]).isExistBuilding()) {
				totalPropertyValue += ((City)gameboard.area[i]).getBuildingCost();
			}
			if (((City)gameboard.area[i]).isExistVilla()) {
				totalPropertyValue += ((City)gameboard.area[i]).getVillaCost();
			}
			totalPropertyValue += ((City)gameboard.area[i]).getLandCost();
		}
		
		
		JOptionPane.showMessageDialog(gameboard, "������ �ǹ��� ���� ��꼼�� �����ϼ���.\n"
				+ "    - ��ü �ǹ� �Ѿ�: " + totalPropertyValue + "��\n"
				+ "    - ��꼼(5%): " + (int)(totalPropertyValue * 0.05) + "��",
				"����û", JOptionPane.INFORMATION_MESSAGE);
		
		return (int)(totalPropertyValue * 0.05);
	}
}
