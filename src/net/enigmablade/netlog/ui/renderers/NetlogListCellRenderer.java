package net.enigmablade.netlog.ui.renderers;

import java.awt.*;
import javax.swing.*;
import com.alee.laf.list.*;
import net.enigmablade.netlog.data.*;

public class NetlogListCellRenderer extends WebListCellRenderer
{
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		Netlog netlog = (Netlog)value;
		setText(netlog.getStartTime().toString());
		setBoldFont(netlog.isMarked());
		
		return this;
	}
}
