package net.enigmablade.netlog;

import java.io.*;
import java.util.*;

public class HandyDandyDirectoryFinder
{
	private static final String[] COMMON_GAME_DIRS = new String[]{"League of Legends", "Riot Games/League of Legends", "LoL", "Riot Games/LoL"};
	
	public static File findLogDirectory()
	{
		//Create list of common root locations
		List<File> roots = new LinkedList<>();
		File[] drives = File.listRoots();
		for(int i = 0; i < drives.length; i++)
			roots.add(drives[i]);
		roots.add(new File(System.getenv("ProgramFiles")));
		roots.add(new File(System.getenv("ProgramFiles(x86)")));
		
		//Search root locations
		for(File root : roots)
		{
			for(String commonGameDir : COMMON_GAME_DIRS)
			{
				File game = getChildFile(root, commonGameDir);
				if(game.isDirectory())
				{
					File logs = getChildFile(game, "Logs/Network Logs");
					if(logs.isDirectory())
					{
						return logs;
					}
				}
			}
		}
		
		return null;
	}
	
	private static File getChildFile(File parentDir, String childRelativePath)
	{
		String parentPath = parentDir.getAbsolutePath();
		StringBuilder path = new StringBuilder(parentPath);
		if(!parentPath.endsWith(File.separator) && !childRelativePath.startsWith(File.separator))
			path.append(File.separatorChar);
		path.append(childRelativePath);
		
		return new File(path.toString());
	}
}
