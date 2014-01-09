package net.enigmablade.netlog.data;

import java.io.*;
import java.util.*;

public class NetlogLoader
{
	public static List<Netlog> getNetlogs(File netlogDir)
	{
		if(netlogDir == null)
			return new ArrayList<>(0);
		if(!netlogDir.exists() || !netlogDir.isDirectory())
			throw new IllegalArgumentException("The netlog dir must exist and be a directory");
		
		File[] netlogFiles = netlogDir.listFiles(Netlog.NETLOG_FILENAME_FILTER);
		
		List<Netlog> netlogs = new ArrayList<>(netlogFiles.length);
		for(File netlogFile : netlogFiles)
			netlogs.add(loadNetlog(netlogFile));
		
		Collections.sort(netlogs);
		return netlogs;
	}
	
	public static Netlog loadNetlog(File file)
	{
		String fname = file.getName();
		int i = fname.indexOf('T');
		if(i < 0)
			i = fname.indexOf('_');
		String[] date = fname.substring(0, i).split("-");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1])-1;
		int day = Integer.parseInt(date[2]);
		String[] time = fname.substring(i+1, fname.lastIndexOf('_')).split("-");
		int hour = Integer.parseInt(time[0]);
		int min = Integer.parseInt(time[1]);
		int sec = Integer.parseInt(time[2]);
		Calendar c = new GregorianCalendar(year, month, day, hour, min, sec);
		Date startTime = c.getTime();
		return new Netlog(startTime, file);
	}
	
	protected static void loadNetlog(Netlog netlog)
	{
		List<Netlog.Entry> entries = new ArrayList<Netlog.Entry>();
		try(Scanner scanner = new Scanner(netlog.getFile()))
		{
			while(scanner.hasNext())
			{
				String line = scanner.nextLine();
				try
				{
					if(Character.isDigit(line.charAt(0)))		//Ignore lines starting with a log date
					{
						String[] parts = line.split(",");
						long time = Long.parseLong(parts[0]);
						String ip = parts[1];
						long incoming = Long.parseLong(parts[2]);
						long outgoing = Long.parseLong(parts[3]);
						long app_ctos = Long.parseLong(parts[4]);
						long app_stoc = Long.parseLong(parts[5]);
						long packetsLost = Long.parseLong(parts[6]);
						long packetsSent = Long.parseLong(parts[7]);
						int ping = Integer.parseInt(parts[8]);
						int variance = Integer.parseInt(parts[9]);
						int reliableDelayed = Integer.parseInt(parts[10]);
						int unreliableDelayed = Integer.parseInt(parts[11]);
						int appUpdateDelayed = Integer.parseInt(parts[12]);
						long timeSpentInCS = Long.parseLong(parts[13]);
						Netlog.Entry entry = new Netlog.Entry(time, ip, incoming, outgoing, app_ctos, app_stoc, packetsLost, packetsSent, ping, variance, reliableDelayed, unreliableDelayed, appUpdateDelayed, timeSpentInCS);
						entries.add(entry);
					}
				}
				catch(Exception e)
				{
					System.err.println("Failed to load netlog entry!");
				}
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		netlog.loadData(entries);
	}
}
