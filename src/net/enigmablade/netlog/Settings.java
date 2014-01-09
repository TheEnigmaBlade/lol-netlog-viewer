package net.enigmablade.netlog;

import java.io.*;
import java.util.*;

public class Settings extends Properties
{
	private static final String CONFIG_NAME = "netlog_config.txt";
	
	public Settings()
	{
		super();
	}
	
	public void loadSettings()
	{
		//Load existing
		try(InputStream in = new FileInputStream(getSettingsFile(false)))
		{
			load(in);
		}
		catch(FileNotFoundException e)
		{
			//Ignore because the config just doesn't exist
		}
		catch(IOException e)
		{
			System.err.println("Failed to load settings file");
			e.printStackTrace();
		}
	}
	
	public void saveSettings()
	{
		//Write to file
		try(OutputStream out = new FileOutputStream(getSettingsFile(true)))
		{
			store(out, "Config for Enigma's Netlog Viewer");
		}
		catch(IOException e)
		{
			System.err.println("Failed to write to settings file");
			e.printStackTrace();
		}
	}
	
	private static File getSettingsFile(boolean createDir)
	{
		//If a local config file already exists, use it
		File file = getChildFile(new File(System.getProperty("user.dir")), CONFIG_NAME);
		if(file.exists())
			return file;
		
		//Otherwise find the system's application data directory
		String dir, os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win"))
			dir = System.getenv("APPDATA")+"\\Enigma";
		else if(os.contains("mac"))
			dir = System.getProperty("user.home")+"/Library/Application Support/Enigma";
		else if(os.contains("nux"))
			dir = System.getProperty("user.home")+"/Enigma";
		else
			dir = System.getProperty("user.dir");
		
		//Check to make sure the directory exists
		File dirFile = new File(dir);
		if(!dirFile.exists())
			//Return the local config path if it couldn't be created (on save)
			if(createDir && !dirFile.mkdir())
				return file;
		
		//Return the config path
		return getChildFile(dirFile, CONFIG_NAME);
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
