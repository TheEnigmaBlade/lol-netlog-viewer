package net.enigmablade.netlog.data;

public class NetlogEvent implements Comparable<NetlogEvent>
{
	public enum Type
	{
		INFO, WARNING, PROBLEM
	}
	
	private Type type;
	private long time;
	private String message;
	
	public NetlogEvent(Type type, long time, String message)
	{
		this.type = type;
		this.time = time;
		this.message = message;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public String getMessage()
	{
		return message;
	}

	@Override
	public int compareTo(NetlogEvent o)
	{
		if(time > o.time)
			return 1;
		if(time < o.time)
			return -1;
		return 0;
	}
}
