package net.enigmablade.netlog.ui.renderers;

import java.awt.*;
import javax.swing.*;
import com.alee.laf.list.*;
import com.alee.laf.panel.*;
import net.enigmablade.netlog.ui.*;

public abstract class AbstractStripedListCellRenderer extends WebListCellRenderer
{
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		Color bg = WebListStyle.background;//getBackground();
		if(!isSelected && !cellHasFocus && index%2 == 0)
		{
			WebPanel p = new WebPanel();
			p.setBackground(UIUtil.adjustColor(bg, -20));
			p.add(this);
			return p;
		}
		else
		{
			setOpaque(false);
		}
		
		return this;
	}
}
