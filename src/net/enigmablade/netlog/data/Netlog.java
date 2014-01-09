package net.enigmablade.netlog.data;

import java.io.*;
import java.util.*;

public class Netlog implements Comparable<Netlog>
{
	//Naming format switched to use 'T' instead of '_' to separate date and time on April 30, 2013
	public static final FilenameFilter NETLOG_FILENAME_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name)
		{
			return name.matches("\\d{4}-\\d{2}-\\d{2}[T_]\\d{2}-\\d{2}-\\d{2}_netlog.txt");
		}
	};
	
	//Netlog entry
	public static class Entry
	{
		private long time;
		private String ip;
		private long incoming, outgoing;
		private long app_ctos, app_stoc;
		private long packetsLost, packetsSent;
		private int ping, variance;
		private int reliableDelayed, unreliableDelayed, appUpdateDelayed;
		private long timeSpentInCS;
		
		public Entry(long time, String ip, long incoming, long outgoing, long app_ctos, long app_stoc, long packetsLost, long packetsSent, int ping, int variance, int reliableDelayed, int unreliableDelayed, int appUpdateDelayed, long timeSpentInCS)
		{
			this.time = time;
			this.ip = ip;
			this.incoming = incoming;
			this.outgoing = outgoing;
			this.app_ctos = app_ctos;
			this.app_stoc = app_stoc;
			this.packetsLost = packetsLost;
			this.packetsSent = packetsSent;
			this.ping = ping;
			this.variance = variance;
			this.reliableDelayed = reliableDelayed;
			this.unreliableDelayed = unreliableDelayed;
			this.appUpdateDelayed = appUpdateDelayed;
			this.timeSpentInCS = timeSpentInCS;
		}
		
		public long getTime()
		{
			return time;
		}
		
		public String getIp()
		{
			return ip;
		}
		
		public long getIncoming()
		{
			return incoming;
		}
		
		public long getOutgoing()
		{
			return outgoing;
		}
		
		public long getApp_ctos()
		{
			return app_ctos;
		}
		
		public long getApp_stoc()
		{
			return app_stoc;
		}
		
		public long getPacketsLost()
		{
			return packetsLost;
		}
		
		public long getPacketsSent()
		{
			return packetsSent;
		}
		
		public int getPing()
		{
			return ping;
		}
		
		public int getVariance()
		{
			return variance;
		}
		
		public int getReliableDelayed()
		{
			return reliableDelayed;
		}
		
		public int getUnreliableDelayed()
		{
			return unreliableDelayed;
		}
		
		public int getAppUpdateDelayed()
		{
			return appUpdateDelayed;
		}
		
		public long getTimeSpentInCriticalSection()
		{
			return timeSpentInCS;
		}
		
		@Override
		public String toString()
		{
			return packetsLost+"";
		}
	}
	
	//Netlog
	
	private Date startTime;
	private File file;
	
	private List<Entry> entries;
	private Map<NetlogEvent.Type, List<NetlogEvent>> events;
	
	private boolean marked = false;
	
	protected Netlog(Date startTime, File file)
	{
		this.startTime = startTime;
		this.file = file;
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	protected File getFile()
	{
		return file;
	}
	
	protected void loadData(List<Entry> entries)
	{
		this.entries = Collections.unmodifiableList(entries);
	}
	
	public List<Entry> getEntries()
	{
		if(entries == null)
			NetlogLoader.loadNetlog(this);
		return entries;
	}
	
	public List<NetlogEvent> getEvents(NetlogEvent.Type type)
	{
		return events.get(type);
	}
	
	public void setEvents(NetlogEvent.Type type, List<NetlogEvent> events)
	{
		this.events.put(type, Collections.unmodifiableList(events));
	}
	
	public boolean isMarked()
	{
		return marked;
	}
	
	public void setMarked(boolean marked)
	{
		this.marked = marked;
	}
	
	@Override
	public int compareTo(Netlog o)
	{
		return startTime.compareTo(o.startTime);
	}
}
