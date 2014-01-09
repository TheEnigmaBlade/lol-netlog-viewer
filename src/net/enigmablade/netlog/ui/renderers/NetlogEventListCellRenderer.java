package net.enigmablade.netlog.ui.renderers;

import java.awt.*;
import javax.swing.*;
import com.alee.laf.list.*;
import com.alee.laf.optionpane.*;
import net.enigmablade.netlog.data.*;

public class NetlogEventListCellRenderer extends WebListCellRenderer
{
	private static final ImageIcon INFO_ICON = new ImageIcon(WebOptionPaneUI.INFORMATION_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_FAST));
	private static final ImageIcon WARNING_ICON = new ImageIcon(WebOptionPaneUI.WARNING_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_FAST));
	private static final ImageIcon ERROR_ICON = new ImageIcon(WebOptionPaneUI.ERROR_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_FAST));
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		NetlogEvent event = (NetlogEvent)value;
		
		if(event != null)
		{
			switch(event.getType())
			{
				case INFO: setIcon(INFO_ICON);
					break;
				case WARNING: setIcon(WARNING_ICON);
					break;
				case PROBLEM: setIcon(ERROR_ICON);
					break;
			}
		}
		
		setText("@"+event.getTime()+": "+event.getMessage());
		
		return this;
	}
}
