package net.enigmablade.netlog;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.util.*;
import net.enigmablade.netlog.data.*;
import net.enigmablade.netlog.data.Netlog.Entry;
import net.enigmablade.netlog.ui.*;

public class NetlogViewer
{
	private Settings settings;
	
	//Data
	private File currentDir;
	private List<Netlog> currentNetlogs;
	private Netlog currentNetlog;
	private List<NetlogEvent> currentEvents;
	
	private SwingWorker<Void, Void> chartWorker = null, eventWorker = null;
	
	//UI
	private NetlogViewerUI ui;
	
	public NetlogViewer()
	{
		loadSettings();
		
		initData();
		initUI();
		initUIData();
	}
	
	private void initData()
	{
		String savedDir = settings.getProperty("lastdir");
		if(savedDir != null)
		{
			currentDir = new File(savedDir);
			if(!currentDir.isDirectory())
				currentDir = null;
		}
		
		if(currentDir == null)
			currentDir = HandyDandyDirectoryFinder.findLogDirectory();
	}
	
	private void initUI()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run()
				{
					//Create UI
					ui = new NetlogViewerUI(NetlogViewer.this);
					ui.setVisible(true);
					
					//Set data
					setSelectedDirectory(currentDir);
				}
			});
		}
		catch(InvocationTargetException | InterruptedException e)
		{
			System.err.println("Failed to initialize UI, closing");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void initUIData()
	{
		//Load selected chart types
		String chartTypesStr = settings.getProperty("selected_chart_types", "data_rates,packet_rates").toUpperCase();
		if(chartTypesStr != null && chartTypesStr.length() > 0)
		{
			String[] typeStrs = chartTypesStr.split(",");
			for(String typeStr : typeStrs)
			{
				try
				{
					NetlogChartType type = NetlogChartType.valueOf(typeStr);
					ui.setChartTypeSelected(type);
				}
				catch(Exception e){e.printStackTrace();}
			}
		}
		
		//Load selected event types
		String eventTypesStr = settings.getProperty("selected_event_types", "warning,problem").toUpperCase();
		if(eventTypesStr != null && eventTypesStr.length() > 0)
		{
			String[] typeStrs = eventTypesStr.split(",");
			for(String typeStr : typeStrs)
			{
				try
				{
					NetlogEvent.Type type = NetlogEvent.Type.valueOf(typeStr);
					ui.setEventTypeSelected(type);
				}
				catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	//Events
	
	public void setSelectedDirectory(File file)
	{
		if(file == null || (file.exists() && file.isDirectory()))
		{
			ui.setNetlogDir(currentDir = file);
			
			new SwingWorker<List<Netlog>, Void>() {
				@Override
				public List<Netlog> doInBackground()
				{
					return NetlogLoader.getNetlogs(currentDir);
				}
				
				@Override
				public void done()
				{
					try
					{
						currentNetlogs = get();
						ui.setNetlogs(currentNetlogs);
					}
					catch(InterruptedException | ExecutionException e)
					{
						e.printStackTrace();
					}
				}
			}.execute();
		}
	}
	
	public void setSelectedNetlog(int netlogIndex)
	{
		setNetlog(netlogIndex >= 0 ? currentNetlogs.get(netlogIndex) : null);
	}
	
	public void setNetlog(Netlog netlog)
	{
		stopWorkers();
		
		//Set current netlog
		currentNetlog = netlog;
		ui.setNetlog(currentNetlog);
		
		//Update 
		chartTypeSelectionChanged();
		eventTypeSelectionChanged();
	}
	
	public void setSelectedEvents(int[] indices)
	{
		List<Long> xValues = new ArrayList<>();
		List<Color> colors = new ArrayList<>();
		
		for(int i : indices)
		{
			NetlogEvent event = currentEvents.get(i);
			xValues.add(event.getTime());
			Color c;
			switch(event.getType())
			{
				case WARNING: c = Color.yellow;
					break;
				case PROBLEM: c = Color.red;
					break;
				default: c = Color.blue;
			}
			colors.add(c);
		}
		
		ui.setChartHighlights(xValues, colors);
	}
	
	public void chartTypeSelectionChanged()
	{
		(chartWorker = new SwingWorker<Void, Void>() {
			List<XYSeriesCollection> data = new ArrayList<>();
			List<ValueAxis> axes = new ArrayList<>();
			List<XYItemRenderer> renderers = new ArrayList<>();
			
			List<NetlogChartType> selectedTypes = ui.getSelectedChartTypes();
			
			@Override
			public Void doInBackground() throws Exception
			{
				if(currentNetlog != null)
				{
					for(NetlogChartType type : selectedTypes)
					{
						switch(type)
						{
							case DATA: addDataChartSeries(currentNetlog, data, axes, renderers);
								break;
							case DATA_RATES: addDataRateChartSeries(currentNetlog, data, axes, renderers);
								break;
							case PACKETS: addPacketChartSeries(currentNetlog, data, axes, renderers);
								break;
							case PACKET_RATES: addPacketRateChartSeries(currentNetlog, data, axes, renderers);
								break;
							case APP_DATA: addAppDataChartSeries(currentNetlog, data, axes, renderers);
								break;
							case APP_DATA_RATES: addAppDataRateChartSeries(currentNetlog, data, axes, renderers);
								break;
							case PING: addPingChartSeries(currentNetlog, data, axes, renderers);
								break;
							case DELAYS: addDelayChartSeries(currentNetlog, data, axes, renderers);
								break;
						}
					}
				}
				return null;
			}
			
			@Override
			public void done()
			{
				ui.setChart(data, axes, renderers);
			}
		}).execute();
	}
	
	public void eventTypeSelectionChanged()
	{
		(eventWorker = new SwingWorker<Void, Void>() {
			List<NetlogEvent> events = new ArrayList<>();
			
			@Override
			public Void doInBackground() throws Exception
			{
				if(currentNetlog != null)
				{
					//Find new events
					List<NetlogEvent.Type> selectedTypes = ui.getSelectedEventTypes();
					for(NetlogEvent.Type type : selectedTypes)
					{
						switch(type)
						{
							case INFO: addInfoEvents(currentNetlog, events);
								break;
							case WARNING: addWarningEvents(currentNetlog, events);
								break;
							case PROBLEM: addProblemEvents(currentNetlog, events);
								break;
						}
					}
					Collections.sort(events);
					
				}
				return null;
			}
			
			@Override
			public void done()
			{
				ui.setEvents(currentEvents = events);
			}
		}).execute();
	}
	
	public void close()
	{
		stopWorkers();
		
		saveSettings();
		ui.close();
	}
	
	//Helpers
	
	////Chart creation
	
	private void addDataChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Byte data
		XYSeries bytesReceivedSeries = new XYSeries("Bytes received", false);
		XYSeries bytesSentSeries = new XYSeries("Bytes sent", false);
		
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			
			//Byte data
			bytesReceivedSeries.add(entry.getTime(), entry.getIncoming());
			bytesSentSeries.add(entry.getTime(), entry.getOutgoing());
		}
		series.addSeries(bytesReceivedSeries);
		series.addSeries(bytesSentSeries);
		
		//Create axes
		NumberAxis byteAxis = new NumberAxis("Total bytes sent/received");
		NumberTickUnit u = new NumberTickUnit(250_000, UIUtil.COOL_FORMATTER);
		byteAxis.setTickUnit(u);
		axes.add(byteAxis);
		
		//Create renderers
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2, 2}, 0.0f);
		renderer.setSeriesStroke(0, stroke);
		renderer.setSeriesStroke(1, stroke);
		renderer.setSeriesPaint(0, UIUtil.adjustColor(Color.blue, -50));
		renderer.setSeriesPaint(1, UIUtil.adjustColor(Color.green, -100));
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		renderers.add(renderer);
	}
	
	private void addDataRateChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Byte data
		XYSeries bytesReceivedSeries = new XYSeries("Bytes received", false);
		XYSeries bytesSentSeries = new XYSeries("Bytes sent", false);
		
		Netlog.Entry lastEntry = new Netlog.Entry(0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			long timeSince = entry.getTime()-lastEntry.getTime();
			
			//Byte data
			long receivedSince = Math.max(entry.getIncoming()-lastEntry.getIncoming(), 0);
			double receivedRate = 1000.0*receivedSince/timeSince;
			long sentSince = Math.max(entry.getOutgoing()-lastEntry.getOutgoing(), 0);
			double sentRate = 1000.0*sentSince/timeSince;
			
			bytesReceivedSeries.add(entry.getTime(), receivedRate);
			bytesSentSeries.add(entry.getTime(), sentRate);
			
			lastEntry = entry;
		}
		series.addSeries(bytesReceivedSeries);
		series.addSeries(bytesSentSeries);
		
		//Create axes
		NumberAxis byteAxis = new NumberAxis("Bytes per second");
		byteAxis.setTickUnit(new NumberTickUnit(500, UIUtil.COOL_FORMATTER));
		axes.add(byteAxis);
		
		//Create renderers
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, UIUtil.adjustColor(Color.blue, -50));
		renderer.setSeriesPaint(1, UIUtil.adjustColor(Color.green, -100));
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		renderers.add(renderer);
	}
	
	private void addPacketChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Byte data
		XYSeries packetsLostSeries = new XYSeries("Packets lost", false);
		XYSeries packetsSentSeries = new XYSeries("Packets sent", false);
		
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			
			//Byte data
			packetsLostSeries.add(entry.getTime(), entry.getPacketsLost());
			packetsSentSeries.add(entry.getTime(), entry.getPacketsSent());
		}
		series.addSeries(packetsLostSeries);
		series.addSeries(packetsSentSeries);
		
		//Create axes
		NumberAxis packetAxis = new NumberAxis("Total packets sent/lost");
		NumberTickUnit u = new NumberTickUnit(500, UIUtil.COOL_FORMATTER);
		packetAxis.setTickUnit(u);
		axes.add(packetAxis);
		
		//Create renderers
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{2.0f, 2.0f}, 0.0f);
		renderer.setSeriesStroke(0, stroke);
		renderer.setSeriesStroke(1, stroke);
		renderer.setSeriesPaint(0, UIUtil.adjustColor(Color.red, -50));
		renderer.setSeriesPaint(1, UIUtil.adjustColor(new Color(200, 0, 255), -50));
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		renderers.add(renderer);
	}
	
	private void addPacketRateChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Packet data
		XYSeries packetsLost = new XYSeries ("Packets lost", false);
		XYSeries packetsSent = new XYSeries ("Packets sent", false);
		
		Netlog.Entry lastEntry = new Netlog.Entry(0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			//long timeSince = entry.getTime()-lastEntry.getTime();
			
			//Packet data
			long packetsLostSince = Math.max(entry.getPacketsLost()-lastEntry.getPacketsLost(), 0);
			//double packetsLostRate = 1000.0*packetsLostSince/timeSince;
			long packetsSentSince = Math.max(entry.getPacketsSent()-lastEntry.getPacketsSent(), 0);
			//double packetsSentRate = 1000.0*packetsSentSince/timeSince;
			
			packetsLost.add(entry.getTime(), packetsLostSince);
			packetsSent.add(entry.getTime(), packetsSentSince);
			
			lastEntry = entry;
		}
		series.addSeries(packetsLost);
		series.addSeries(packetsSent);
		
		//Create axes
		NumberAxis packetAxis = new NumberAxis("Packets lost/sent last period");
		axes.add(packetAxis);
		
		//Create renderers
		XYAreaRenderer packetRenderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		packetRenderer.setSeriesPaint(0, UIUtil.adjustColor(Color.red, 125));
		packetRenderer.setSeriesPaint(1, UIUtil.adjustColor(Color.blue, 200));
		renderers.add(packetRenderer);
	}
	
	private void addAppDataChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		
	}
	
	private void addAppDataRateChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		
	}
	
	private void addPingChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Packet data
		XYSeries ping = new XYSeries ("Ping", false);
		XYSeries variance = new XYSeries ("Variance", false);
		
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			
			ping.add(entry.getTime(), entry.getPing());
			variance.add(entry.getTime(), entry.getVariance());
		}
		series.addSeries(ping);
		series.addSeries(variance);
		
		//Create axes
		NumberAxis pingAxis = new NumberAxis("Ping/ping variance (ms)");
		axes.add(pingAxis);
		
		//Create renderers
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, UIUtil.adjustColor(Color.yellow, -75));
		renderer.setSeriesPaint(1, UIUtil.adjustColor(Color.orange, -75));
		renderer.setSeriesShape(0, XYLineAndShapeRenderer.DEFAULT_SHAPE);
		renderer.setSeriesShape(1, new Ellipse2D.Double(-3, -3, 6, 6));
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesFilled(0, false);
		renderer.setSeriesShapesFilled(1, false);
		renderers.add(renderer);
	}
	
	private void addDelayChartSeries(Netlog netlog, List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		XYSeriesCollection series = new XYSeriesCollection();
		data.add(series);
		
		//Packet data
		XYSeries reliableDelay = new XYSeries ("Reliable delayed", false);
		XYSeries unreliableDelay = new XYSeries ("Unreliable delayed", false);
		XYSeries appDelay = new XYSeries ("App update delayed", false);
		
		for(int n = 0; n < netlog.getEntries().size(); n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			
			reliableDelay.add(entry.getTime(), entry.getReliableDelayed());
			unreliableDelay.add(entry.getTime(), entry.getUnreliableDelayed());
			appDelay.add(entry.getTime(), entry.getAppUpdateDelayed());
			
		}
		series.addSeries(reliableDelay);
		series.addSeries(unreliableDelay);
		series.addSeries(appDelay);
		
		//Create axes
		NumberAxis pingAxis = new NumberAxis("Total delay (ms)");
		axes.add(pingAxis);
		
		//Create renderers
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, UIUtil.adjustColor(Color.cyan, -50));
		renderer.setSeriesPaint(1, UIUtil.adjustColor(Color.magenta, -50));
		renderer.setSeriesPaint(2, UIUtil.adjustColor(Color.lightGray, -50));
		renderer.setSeriesShape(0, ShapeUtilities.createUpTriangle(3));
		renderer.setSeriesShape(1, ShapeUtilities.createDownTriangle(3));
		renderer.setSeriesShape(2, ShapeUtilities.createDiamond(3));
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesLinesVisible(2, true);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesVisible(2, true);
		renderer.setSeriesShapesFilled(0, true);
		renderer.setSeriesShapesFilled(1, true);
		renderer.setSeriesShapesFilled(2, true);
		renderers.add(renderer);
	}
	
	////Event analysis
	
	private void addInfoEvents(Netlog netlog, List<NetlogEvent> events)
	{
		List<Netlog.Entry> entries = netlog.getEntries();
		if(entries.size() > 0)
		{
			Netlog.Entry firstEntry = entries.get(0);
			events.add(new NetlogEvent(NetlogEvent.Type.INFO, firstEntry.getTime(), "Log start"));
			
			Netlog.Entry lastEntry = entries.get(entries.size()-1);
			events.add(new NetlogEvent(NetlogEvent.Type.INFO, lastEntry.getTime(), "Log end"));
		}
	}
	
	private void addWarningEvents(Netlog netlog, List<NetlogEvent> events)
	{
		List<Netlog.Entry> entries = netlog.getEntries();
		//Create ping list
		List<Netlog.Entry> entriesSortedP = new ArrayList<>(entries);
		entriesSortedP.remove(0);
		Collections.sort(entriesSortedP, new Comparator<Netlog.Entry>(){
			@Override
			public int compare(Entry e1, Entry e2)
			{
				if(e1.getPing() < e2.getPing())
					return -1;
				if(e1.getPing() > e2.getPing())
					return 1;
				return 0;
			}
		});
		//Create packet loss list
		List<Netlog.Entry> entriesSortedPL = new ArrayList<>(entries.size());
		long prev = 0;
		for(Netlog.Entry entry : entries)
		{
			long packetsLost = Math.max(entry.getPacketsLost()-prev, 0);
			entriesSortedPL.add(new Netlog.Entry(entry.getTime(), null, 0, 0, 0, 0, packetsLost, 0, 0, 0, 0, 0, 0, 0));
			prev = entry.getPacketsLost();
		}
		Collections.sort(entriesSortedPL, new Comparator<Netlog.Entry>(){
			@Override
			public int compare(Entry e1, Entry e2)
			{
				if(e1.getPacketsLost() < e2.getPacketsLost())
					return -1;
				if(e1.getPacketsLost() > e2.getPacketsLost())
					return 1;
				return 0;
			}
		});
		
		if(entries.size() > 0)
		{
			int medianIndex = entries.size()/2;
			int medianP = entriesSortedP.get(medianIndex).getPing();
			
			double q1Index = medianIndex/2.0;
			int q1Index2 = (int)q1Index;
			int q1P = q1Index%1 > 0 ? (entriesSortedP.get(q1Index2).getPing()+entriesSortedP.get(q1Index2+1).getPing())/2 : entriesSortedP.get(q1Index2).getPing();
			long q1PL = q1Index%1 > 0 ? (entriesSortedPL.get(q1Index2).getPacketsLost()+entriesSortedPL.get(q1Index2+1).getPacketsLost())/2 : entriesSortedPL.get(q1Index2).getPacketsLost();
			
			double q3Index = (medianIndex+entries.size())/2.0;
			int q3Index2 = (int)q3Index;
			int q3P = q3Index%1 > 0 ? (entriesSortedP.get(q3Index2).getPing()+entriesSortedP.get(q3Index2+1).getPing())/2 : entriesSortedP.get(q3Index2).getPing();
			long q3PL = q3Index%1 > 0 ? (entriesSortedPL.get(q3Index2).getPacketsLost()+entriesSortedPL.get(q3Index2+1).getPacketsLost())/2 : entriesSortedPL.get(q3Index2).getPacketsLost();
			
			int iqrP = q3P-q1P;
			long iqrPL = q3PL-q1PL;
			
			double f3P = q3P+(1.5*iqrP);
			double f3PL = q3PL+(1.5*iqrPL);
			
			for(int n = entriesSortedP.size()-1; n >= 0; n--)
			{
				Entry entry = entriesSortedP.get(n);
				if(entry.getPing() > f3P && entry.getPing() > medianP+5)
					events.add(new NetlogEvent(NetlogEvent.Type.WARNING, entry.getTime(), "High ping"));
				else
					break;
			}
			for(int n = entriesSortedPL.size()-1; n >= 0; n--)
			{
				Entry entry = entriesSortedPL.get(n);
				if(entry.getPacketsLost() > f3PL)
					events.add(new NetlogEvent(NetlogEvent.Type.WARNING, entry.getTime(), "High packet loss"));
				else
					break;
			}
		}
	}
	
	private void addProblemEvents(Netlog netlog, List<NetlogEvent> events)
	{
		Netlog.Entry lastEntry = new Netlog.Entry(0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		for(int n = 0; n < netlog.getEntries().size()-1; n++)
		{
			Netlog.Entry entry = netlog.getEntries().get(n);
			
			long receivedSince = Math.max(entry.getIncoming()-lastEntry.getIncoming(), 0);
			long sentSince = Math.max(entry.getOutgoing()-lastEntry.getOutgoing(), 0);
			
			//Check for disconnections
			if(receivedSince == 0 || sentSince == 0)
			{
				events.add(new NetlogEvent(NetlogEvent.Type.PROBLEM, entry.getTime(), "Disconnection"));
			}
			
			lastEntry = entry;
		}
	}
	
	////Other
	
	private void stopWorkers()
	{
		if(chartWorker != null && !chartWorker.isDone())
			chartWorker.cancel(true);
		if(eventWorker != null && !eventWorker.isDone())
			eventWorker.cancel(true);
	}
	
	//Settings
	
	private void loadSettings()
	{
		settings = new Settings();
		settings.loadSettings();
	}
	
	private void saveSettings()
	{
		//Last dir used
		settings.setProperty("last_dir", currentDir.getAbsolutePath());
		//Selected chart types
		List<NetlogChartType> selectedChartTypes = ui.getSelectedChartTypes();
		StringBuilder selectedChartTypesStr = new StringBuilder();
		for(int n = 0; n < selectedChartTypes.size(); n++)
		{
			NetlogChartType type = selectedChartTypes.get(n);
			selectedChartTypesStr.append(type.toString());
			if(n < selectedChartTypes.size()-1)
				selectedChartTypesStr.append(',');
		}
		settings.setProperty("selected_chart_types", selectedChartTypesStr.toString().toLowerCase());
		//Selected event types
		List<NetlogEvent.Type> selectedEventTypes = ui.getSelectedEventTypes();
		StringBuilder selectedEventTypesStr = new StringBuilder();
		for(int n = 0; n < selectedEventTypes.size(); n++)
		{
			NetlogEvent.Type type = selectedEventTypes.get(n);
			selectedEventTypesStr.append(type.toString());
			if(n < selectedEventTypes.size()-1)
				selectedEventTypesStr.append(',');
		}
		settings.setProperty("selected_event_types", selectedEventTypesStr.toString().toLowerCase());
		
		//Save
		settings.saveSettings();
	}
}
