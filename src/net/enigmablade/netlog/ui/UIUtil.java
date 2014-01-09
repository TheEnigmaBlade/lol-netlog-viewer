package net.enigmablade.netlog.ui;

import java.awt.*;
import java.net.*;
import java.text.*;

public class UIUtil
{
	public static final DecimalFormat COOL_FORMATTER = new DecimalFormat() {
		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos)
		{
			return toAppendTo.append(coolFormat(number, 0));
		}
		
		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
		{
			return toAppendTo.append(coolFormat(number, 0));
		}
		
		/*
		 * Copied from StackOverflow.
		 */
		private char[] c = new char[]{'k', 'm', 'b', 't'};

		private String coolFormat(double n, int iteration) {
		    double d = ((long) n / 100) / 10.0;
		    boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
		    return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
		        ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
		         (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
		         ) + "" + c[iteration]) 
		        : coolFormat(d, iteration+1));

		}
	};
	
	public static void openURL(String url)
	{
		if(Desktop.isDesktopSupported()) 
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(new URI(url));
			}
			catch (Exception e){}
		}
	}
	
	public static Color addAlpha(Color c, int alpha)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static Color adjustColor(Color c, int offset)
	{
		return new Color(capColorValue(c.getRed()+offset), capColorValue(c.getGreen()+offset), capColorValue(c.getBlue()+offset), c.getAlpha());
	}
	
	private static int capColorValue(int c)
	{
		return c < 0 ? 0 : c > 255 ? 255 : c;
	}
}
