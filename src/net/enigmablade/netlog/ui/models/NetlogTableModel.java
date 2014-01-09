package net.enigmablade.netlog.ui.models;

import java.util.*;
import javax.swing.table.*;

public class NetlogTableModel extends AbstractTableModel
{
	private final Class<?>[] columnClasses = {Boolean.class, Date.class, String.class};
	private final String[] columnNames = {"Include", "Date Played", "IP"};
	
	private List<List<Object>> data;
	
	public NetlogTableModel()
	{
		data = new ArrayList<>();
	}
	
	@Override
	public int getRowCount()
	{
		return data.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return columnClasses.length;
	}
	
	@Override
	public Class<?> getColumnClass(int col)
	{
		return columnClasses[col];
	}
	
	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return col == 0;
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		return data.get(row).get(col);
	}
	
	@Override
	public void setValueAt(Object value, int row, int col)
	{
		data.get(row).set(col, value);
		fireTableCellUpdated(row, col);
	}
	
	public void addRow(Date date, String ip)
	{
		List<Object> row = new ArrayList<>(columnClasses.length);
		row.add(true);
		row.add(date);
		row.add(ip);
		
		data.add(row);
	}
}
