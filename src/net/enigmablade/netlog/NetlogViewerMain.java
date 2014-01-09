package net.enigmablade.netlog;

import com.alee.laf.*;

public class NetlogViewerMain
{
	public static void main(String[] args)
	{
		WebLookAndFeel.install();
		//WebLookAndFeel.setDecorateFrames(true);
		new NetlogViewer();
		
		/*List<Netlog> netlogs = NetlogLoader.getNetlogs("C:\\Riot Games\\League of Legends\\Logs\\Network Logs");
		Netlog last = netlogs.get(netlogs.size()-1);
		
		long lastTime = 0, lastReceived = 0;
		for(Netlog.Entry entry : last.getEntries())
		{
			long timeSince = entry.getTime()-lastTime;
			long receivedSince = entry.getIncoming()-lastReceived;
			double receivedPerMS = 1.0*receivedSince/timeSince;
			System.out.println(receivedPerMS+" bytes/ms");
		}*/
	}
}
