package net.enigmablade.netlog.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.jfree.chart.*;
import org.jfree.chart.annotations.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import com.alee.extended.drag.*;
import com.alee.extended.filechooser.*;
import com.alee.extended.panel.*;
import com.alee.laf.button.*;
import com.alee.laf.checkbox.*;
import com.alee.laf.label.*;
import com.alee.laf.list.*;
import com.alee.laf.menu.*;
import com.alee.laf.panel.*;
import com.alee.laf.scroll.*;
import com.alee.laf.separator.*;
import com.alee.laf.splitpane.*;
import com.alee.laf.tabbedpane.*;
import com.alee.laf.table.*;
import com.alee.managers.notification.*;
import com.alee.utils.swing.*;
import net.enigmablade.netlog.*;
import net.enigmablade.netlog.data.*;
import net.enigmablade.netlog.ui.models.*;
import net.enigmablade.netlog.ui.renderers.*;
import com.alee.extended.list.WebCheckBoxList;

@SuppressWarnings("unchecked")
public class NetlogViewerUI extends JFrame
{
	private NetlogViewer main;
	
	//Models
	private DefaultListModel<Netlog> netlogListModel;
	private DefaultListModel<NetlogEvent> eventListModel;
	private NetlogTableModel netlogTableModel;
	
	//Listeners
	private PathFieldListener netlogDirListener;
	private ListSelectionListener netlogListListener;
	
	//Data
	private JFreeChart chart;
	private int[] savedEventSelection = new int[0];
	
	private WebPopupMenu netlogMenu;
	private WebCheckBoxMenuItem netlogMenuBoldItem;
	
	//Components
	private WebPathField netlogDirField;
	private WebOverlay chartOverlayPanel;
	private ChartPanel chartPanel;
	private WebPanel fileDropOverlay;
	private WebList netlogList;
	private WebScrollPane netlogListScrollPane;
	private WebCheckBox dataTypeCheckBox, dataRateTypeCheckBox;
	private WebCheckBox packetTypeCheckBox, packetRateTypeCheckBox;
	private WebCheckBox appDataTypeCheckBox, appDataRateTypeCheckBox;
	private WebCheckBox pingTypeCheckBox, delayTypeCheckBox;
	private WebCheckBox infoEventCheckBox, warningEventCheckBox, problemEventCheckBox;
	private WebList eventsList;
	private WebButton netlogDirButton;
	private WebTabbedPane webTabbedPane;
	private WebSplitPane webPanel;
	private WebPanel webPanel_1;
	private WebTable netlogDataTable;
	private WebScrollPane webScrollPane;
	private WebPanel webPanel_3;
	private WebPanel webPanel_2;
	private WebLabel wblblTableOptions;
	private WebSeparator webSeparator;
	private WebCheckBox webCheckBox;
	private WebScrollPane webScrollPane_1;
	private WebCheckBoxList webCheckBoxList;
	
	//Initialization
	public NetlogViewerUI(NetlogViewer main)
	{
		setTitle("Enigma's Netlog Viewer");
		this.main = main;
		
		initComponents();
		initListeners();
		initModels();
	}
	
	private void initComponents()
	{
		setBounds(0, 0, 1200, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("resources/icon.png")).getImage());
		
		WebPanel contentPane = new WebPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		WebSplitPane contentSplitPane = new WebSplitPane();
		contentSplitPane.setOneTouchExpandable(true);
		contentSplitPane.setContinuousLayout(true);
		contentPane.add(contentSplitPane, BorderLayout.CENTER);
		
		WebPanel netlogListPanel = new WebPanel();
		netlogListPanel.setShadeWidth(2);
		netlogListPanel.setUndecorated(false);
		netlogListPanel.setPreferredSize(new Dimension(215, 0));
		contentSplitPane.setLeftComponent(netlogListPanel);
		GridBagLayout gbl_netlogListPanel = new GridBagLayout();
		gbl_netlogListPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_netlogListPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_netlogListPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_netlogListPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		netlogListPanel.setLayout(gbl_netlogListPanel);
		
		WebLabel netlogListTitle = new WebLabel();
		netlogListTitle.setFontSize(13);
		netlogListTitle.setMargin(new Insets(2, 0, 1, 0));
		netlogListTitle.setLabelFor(netlogListPanel);
		netlogListTitle.setDrawShade(true);
		netlogListTitle.setText("Netlogs");
		GridBagConstraints gbc_netlogListTitle = new GridBagConstraints();
		gbc_netlogListTitle.gridwidth = 3;
		gbc_netlogListTitle.gridx = 0;
		gbc_netlogListTitle.gridy = 0;
		netlogListPanel.add(netlogListTitle, gbc_netlogListTitle);
		
		WebSeparator separator1 = new WebSeparator();
		separator1.setSeparatorLightUpperColor(Color.MAGENTA);
		separator1.setSeparatorLightColor(Color.MAGENTA);
		separator1.setDrawSideLines(false);
		GridBagConstraints gbc_separator1 = new GridBagConstraints();
		gbc_separator1.gridwidth = 3;
		gbc_separator1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator1.gridx = 0;
		gbc_separator1.gridy = 1;
		netlogListPanel.add(separator1, gbc_separator1);
		
		netlogDirField = new WebPathField();
		netlogDirField.setDrawBottom(false);
		GridBagConstraints gbc_netlogDirField = new GridBagConstraints();
		gbc_netlogDirField.gridwidth = 2;
		gbc_netlogDirField.fill = GridBagConstraints.HORIZONTAL;
		gbc_netlogDirField.gridx = 0;
		gbc_netlogDirField.gridy = 2;
		netlogListPanel.add(netlogDirField, gbc_netlogDirField);
		netlogDirField.setShadeWidth(0);
		netlogDirField.setDrawTop(false);
		netlogDirField.setDrawRight(false);
		netlogDirField.setDrawLeft(false);
		
		netlogDirButton = new WebButton();
		netlogDirButton.setRolloverDecoratedOnly(true);
		netlogDirButton.setDrawRight(false);
		netlogDirButton.setDrawTop(false);
		netlogDirButton.setDrawShade(false);
		netlogDirButton.setDrawLeft(false);
		netlogDirButton.setDrawFocus(false);
		netlogDirButton.setDrawBottom(false);
		netlogDirButton.setText("...");
		GridBagConstraints gbc_netlogDirButton = new GridBagConstraints();
		gbc_netlogDirButton.fill = GridBagConstraints.BOTH;
		gbc_netlogDirButton.gridx = 2;
		gbc_netlogDirButton.gridy = 2;
		netlogListPanel.add(netlogDirButton, gbc_netlogDirButton);
		
		WebSeparator separator2 = new WebSeparator();
		GridBagConstraints gbc_separator2 = new GridBagConstraints();
		gbc_separator2.gridwidth = 3;
		gbc_separator2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator2.gridx = 0;
		gbc_separator2.gridy = 3;
		netlogListPanel.add(separator2, gbc_separator2);
		
		netlogListScrollPane = new WebScrollPane((Component) null);
		GridBagConstraints gbc_netlogListScrollPane = new GridBagConstraints();
		gbc_netlogListScrollPane.gridwidth = 3;
		gbc_netlogListScrollPane.fill = GridBagConstraints.BOTH;
		gbc_netlogListScrollPane.gridx = 0;
		gbc_netlogListScrollPane.gridy = 4;
		netlogListPanel.add(netlogListScrollPane, gbc_netlogListScrollPane);
		netlogListScrollPane.setDrawBorder(false);
		netlogListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		netlogList = new WebList();
		netlogList.setCellRenderer(new NetlogListCellRenderer());
		netlogListScrollPane.setViewportView(netlogList);
		netlogList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		webTabbedPane = new WebTabbedPane();
		//TODO: temp removed
		//contentSplitPane.setRightComponent(webTabbedPane);
		
		WebSplitPane netlogTab = new WebSplitPane();
		//TODO: temp removed
		contentSplitPane.setRightComponent(netlogTab);
		//webTabbedPane.addTab("Single netlog graphs and events", null, netlogTab, null);
		netlogTab.setContinuousLayout(true);
		netlogTab.setOneTouchExpandable(true);
		netlogTab.setResizeWeight(1.0);
		
		WebPanel netlogPanel = new WebPanel();
		netlogPanel.setWebColored(false);
		netlogPanel.setDrawBottom(false);
		netlogPanel.setDrawTop(false);
		netlogPanel.setDrawLeft(false);
		netlogPanel.setUndecorated(false);
		netlogTab.setLeftComponent(netlogPanel);
		netlogPanel.setLayout(new BorderLayout(0, 2));
		
		chartOverlayPanel = new WebOverlay();
		netlogPanel.add(chartOverlayPanel, BorderLayout.CENTER);
		
		chartPanel = new ChartPanel(null);
		//chartPanel.setMouseWheelEnabled(true);
		chartPanel.setBackground(WebPanelStyle.backgroundColor);
		chartPanel.setPreferredSize(new Dimension(0, 0));
		chartPanel.setChart(chart);
		chartOverlayPanel.setComponent(chartPanel);
		
		WebPanel chartOptionsPanel = new WebPanel();
		chartOptionsPanel.setOpaque(false);
		chartOptionsPanel.setMargin(new Insets(0, 2, 2, 2));
		
		netlogPanel.add(chartOptionsPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_chartOptionsPanel = new GridBagLayout();
		gbl_chartOptionsPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_chartOptionsPanel.rowHeights = new int[]{0, 0, 0};
		gbl_chartOptionsPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_chartOptionsPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		chartOptionsPanel.setLayout(gbl_chartOptionsPanel);
		
		dataTypeCheckBox = new WebCheckBox();
		dataTypeCheckBox.setRolloverDarkBorderOnly(true);
		dataTypeCheckBox.setText("Data (bytes)");
		GridBagConstraints gbc_dataTypeCheckBox = new GridBagConstraints();
		gbc_dataTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_dataTypeCheckBox.gridx = 0;
		gbc_dataTypeCheckBox.gridy = 0;
		chartOptionsPanel.add(dataTypeCheckBox, gbc_dataTypeCheckBox);
		
		packetTypeCheckBox = new WebCheckBox();
		packetTypeCheckBox.setRolloverDarkBorderOnly(true);
		packetTypeCheckBox.setText("Packets (packets, duh)");
		GridBagConstraints gbc_packetsTypeCheckBox = new GridBagConstraints();
		gbc_packetsTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_packetsTypeCheckBox.gridx = 1;
		gbc_packetsTypeCheckBox.gridy = 0;
		chartOptionsPanel.add(packetTypeCheckBox, gbc_packetsTypeCheckBox);
		
		appDataTypeCheckBox = new WebCheckBox();
		appDataTypeCheckBox.setRolloverDarkBorderOnly(true);
		appDataTypeCheckBox.setText("App data (bytes)");
		GridBagConstraints gbc_appDataTypeCheckBox = new GridBagConstraints();
		gbc_appDataTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_appDataTypeCheckBox.gridx = 2;
		gbc_appDataTypeCheckBox.gridy = 0;
		chartOptionsPanel.add(appDataTypeCheckBox, gbc_appDataTypeCheckBox);
		
		pingTypeCheckBox = new WebCheckBox();
		pingTypeCheckBox.setRolloverDarkBorderOnly(true);
		pingTypeCheckBox.setText("Ping");
		GridBagConstraints gbc_pingTypeCheckBox = new GridBagConstraints();
		gbc_pingTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_pingTypeCheckBox.gridx = 3;
		gbc_pingTypeCheckBox.gridy = 0;
		chartOptionsPanel.add(pingTypeCheckBox, gbc_pingTypeCheckBox);
		
		dataRateTypeCheckBox = new WebCheckBox();
		dataRateTypeCheckBox.setRolloverDarkBorderOnly(true);
		dataRateTypeCheckBox.setText("Data rates (bytes/sec)");
		GridBagConstraints gbc_dataRateTypeCheckBox = new GridBagConstraints();
		gbc_dataRateTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_dataRateTypeCheckBox.gridx = 0;
		gbc_dataRateTypeCheckBox.gridy = 1;
		chartOptionsPanel.add(dataRateTypeCheckBox, gbc_dataRateTypeCheckBox);
		
		packetRateTypeCheckBox = new WebCheckBox();
		packetRateTypeCheckBox.setRolloverDarkBorderOnly(true);
		packetRateTypeCheckBox.setText("Packet rates (packets/sec)");
		GridBagConstraints gbc_packetRateTypeCheckBox = new GridBagConstraints();
		gbc_packetRateTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_packetRateTypeCheckBox.gridx = 1;
		gbc_packetRateTypeCheckBox.gridy = 1;
		chartOptionsPanel.add(packetRateTypeCheckBox, gbc_packetRateTypeCheckBox);
		
		appDataRateTypeCheckBox = new WebCheckBox();
		appDataRateTypeCheckBox.setRolloverDarkBorderOnly(true);
		appDataRateTypeCheckBox.setText("App data rates (bytes/sec)");
		GridBagConstraints gbc_appDataRateTypeCheckBox = new GridBagConstraints();
		gbc_appDataRateTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_appDataRateTypeCheckBox.gridx = 2;
		gbc_appDataRateTypeCheckBox.gridy = 1;
		chartOptionsPanel.add(appDataRateTypeCheckBox, gbc_appDataRateTypeCheckBox);
		
		delayTypeCheckBox = new WebCheckBox();
		delayTypeCheckBox.setRolloverDarkBorderOnly(true);
		delayTypeCheckBox.setText("Delays");
		GridBagConstraints gbc_delaysTypeCheckBox = new GridBagConstraints();
		gbc_delaysTypeCheckBox.anchor = GridBagConstraints.WEST;
		gbc_delaysTypeCheckBox.gridx = 3;
		gbc_delaysTypeCheckBox.gridy = 1;
		chartOptionsPanel.add(delayTypeCheckBox, gbc_delaysTypeCheckBox);
		
		WebPanel eventsPanel = new WebPanel();
		eventsPanel.setPreferredWidth(200);
		eventsPanel.setWebColored(false);
		eventsPanel.setDrawBottom(false);
		eventsPanel.setDrawTop(false);
		eventsPanel.setDrawRight(false);
		eventsPanel.setUndecorated(false);
		netlogTab.setRightComponent(eventsPanel);
		GridBagLayout gbl_eventsPanel = new GridBagLayout();
		gbl_eventsPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_eventsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_eventsPanel.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_eventsPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		eventsPanel.setLayout(gbl_eventsPanel);
		
		WebLabel wblblNetworkEvents = new WebLabel();
		wblblNetworkEvents.setFontSize(13);
		wblblNetworkEvents.setMargin(new Insets(2, 0, 1, 0));
		wblblNetworkEvents.setDrawShade(true);
		wblblNetworkEvents.setText("Network Events");
		GridBagConstraints gbc_wblblNetworkEvents = new GridBagConstraints();
		gbc_wblblNetworkEvents.gridwidth = 3;
		gbc_wblblNetworkEvents.gridx = 0;
		gbc_wblblNetworkEvents.gridy = 0;
		eventsPanel.add(wblblNetworkEvents, gbc_wblblNetworkEvents);
		
		WebSeparator webSeparator_2 = new WebSeparator();
		GridBagConstraints gbc_webSeparator_2 = new GridBagConstraints();
		gbc_webSeparator_2.gridwidth = 3;
		gbc_webSeparator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_webSeparator_2.gridx = 0;
		gbc_webSeparator_2.gridy = 1;
		eventsPanel.add(webSeparator_2, gbc_webSeparator_2);
		
		WebScrollPane eventsScrollPane = new WebScrollPane((Component) null);
		eventsScrollPane.setDrawBorder(false);
		eventsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_eventsScrollPane = new GridBagConstraints();
		gbc_eventsScrollPane.gridwidth = 3;
		gbc_eventsScrollPane.fill = GridBagConstraints.BOTH;
		gbc_eventsScrollPane.gridx = 0;
		gbc_eventsScrollPane.gridy = 2;
		eventsPanel.add(eventsScrollPane, gbc_eventsScrollPane);
		
		eventsList = new WebList();
		eventsList.setCellRenderer(new NetlogEventListCellRenderer());
		eventsScrollPane.setViewportView(eventsList);
		
		WebSeparator webSeparator_3 = new WebSeparator();
		GridBagConstraints gbc_webSeparator_3 = new GridBagConstraints();
		gbc_webSeparator_3.insets = new Insets(0, 0, 2, 0);
		gbc_webSeparator_3.gridwidth = 3;
		gbc_webSeparator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_webSeparator_3.gridx = 0;
		gbc_webSeparator_3.gridy = 3;
		eventsPanel.add(webSeparator_3, gbc_webSeparator_3);
		
		infoEventCheckBox = new WebCheckBox();
		infoEventCheckBox.setRolloverDarkBorderOnly(true);
		infoEventCheckBox.setText("Info");
		GridBagConstraints gbc_infoEventCheckBox = new GridBagConstraints();
		gbc_infoEventCheckBox.gridx = 0;
		gbc_infoEventCheckBox.gridy = 4;
		eventsPanel.add(infoEventCheckBox, gbc_infoEventCheckBox);
		
		warningEventCheckBox = new WebCheckBox();
		warningEventCheckBox.setRolloverDarkBorderOnly(true);
		warningEventCheckBox.setText("Warnings");
		GridBagConstraints gbc_warningEventCheckBox = new GridBagConstraints();
		gbc_warningEventCheckBox.insets = new Insets(0, 0, 2, 0);
		gbc_warningEventCheckBox.gridx = 1;
		gbc_warningEventCheckBox.gridy = 4;
		eventsPanel.add(warningEventCheckBox, gbc_warningEventCheckBox);
		
		problemEventCheckBox = new WebCheckBox();
		problemEventCheckBox.setRolloverDarkBorderOnly(true);
		problemEventCheckBox.setText("Problems");
		GridBagConstraints gbc_problemEventCheckBox = new GridBagConstraints();
		gbc_problemEventCheckBox.insets = new Insets(0, 0, 2, 0);
		gbc_problemEventCheckBox.gridx = 2;
		gbc_problemEventCheckBox.gridy = 4;
		eventsPanel.add(problemEventCheckBox, gbc_problemEventCheckBox);
		
		webPanel = new WebSplitPane();
		webPanel.setResizeWeight(1.0);
		webPanel.setContinuousLayout(true);
		webPanel.setOneTouchExpandable(true);
		webTabbedPane.addTab("Marked netlog statistics", null, webPanel, null);
		
		webPanel_3 = new WebPanel();
		webPanel_3.setDrawTop(false);
		webPanel_3.setDrawRight(false);
		webPanel_3.setDrawBottom(false);
		webPanel_3.setWebColored(false);
		webPanel_3.setUndecorated(false);
		webPanel_3.setMinimumWidth(100);
		webPanel_3.setPreferredWidth(200);
		webPanel.setRightComponent(webPanel_3);
		GridBagLayout gbl_webPanel_3 = new GridBagLayout();
		gbl_webPanel_3.columnWidths = new int[]{0, 0};
		gbl_webPanel_3.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_webPanel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_webPanel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		webPanel_3.setLayout(gbl_webPanel_3);
		
		wblblTableOptions = new WebLabel();
		wblblTableOptions.setMargin(new Insets(2, 0, 1, 0));
		wblblTableOptions.setFontSize(13);
		wblblTableOptions.setDrawShade(true);
		wblblTableOptions.setText("Table Options");
		GridBagConstraints gbc_wblblTableOptions = new GridBagConstraints();
		gbc_wblblTableOptions.gridx = 0;
		gbc_wblblTableOptions.gridy = 0;
		webPanel_3.add(wblblTableOptions, gbc_wblblTableOptions);
		
		webSeparator = new WebSeparator();
		GridBagConstraints gbc_webSeparator = new GridBagConstraints();
		gbc_webSeparator.fill = GridBagConstraints.HORIZONTAL;
		gbc_webSeparator.gridx = 0;
		gbc_webSeparator.gridy = 1;
		webPanel_3.add(webSeparator, gbc_webSeparator);
		
		webCheckBox = new WebCheckBox();
		GridBagConstraints gbc_webCheckBox = new GridBagConstraints();
		gbc_webCheckBox.gridx = 0;
		gbc_webCheckBox.gridy = 2;
		webPanel_3.add(webCheckBox, gbc_webCheckBox);
		
		webScrollPane_1 = new WebScrollPane((Component) null);
		GridBagConstraints gbc_webScrollPane_1 = new GridBagConstraints();
		gbc_webScrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_webScrollPane_1.gridx = 0;
		gbc_webScrollPane_1.gridy = 3;
		webPanel_3.add(webScrollPane_1, gbc_webScrollPane_1);
		
		webCheckBoxList = new WebCheckBoxList();
		webScrollPane_1.setViewportView(webCheckBoxList);
		
		webPanel_2 = new WebPanel();
		webPanel_2.setWebColored(false);
		webPanel_2.setUndecorated(false);
		webPanel_2.setDrawTop(false);
		webPanel_2.setDrawBottom(false);
		webPanel_2.setDrawLeft(false);
		webPanel.setLeftComponent(webPanel_2);
		
		webScrollPane = new WebScrollPane((Component) null);
		webScrollPane.setDrawBorder(false);
		webPanel_2.add(webScrollPane, BorderLayout.CENTER);
		
		netlogDataTable = new WebTable();
		webScrollPane.setViewportView(netlogDataTable);
		
		webPanel_1 = new WebPanel();
		webTabbedPane.addTab("About", null, webPanel_1, null);
		
		fileDropOverlay = new WebPanel();
		fileDropOverlay.setOpaque(false);
		fileDropOverlay.setFocusable(true);
	}
	
	private void initListeners()
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt)
			{
				main.close();
			}
		});
		
		netlogDirField.addPathFieldListener(netlogDirListener = new PathFieldListener() {
			@Override
			public void directoryChanged(File dir)
			{
				main.setSelectedDirectory(dir);
			}
		});
		
		netlogDirButton.addActionListener(new ActionListener() {
			private WebDirectoryChooser chooser = null;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(chooser == null)
					chooser = new WebDirectoryChooser(NetlogViewerUI.this, "Choose a netlog directory");
				chooser.setVisible(true);
				
				if(chooser.getResult () == DialogOptions.OK_OPTION)
				{
					File dir = chooser.getSelectedDirectory();
					main.setSelectedDirectory(dir);
				}
			}
		});
		
		netlogList.addListSelectionListener(netlogListListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					int i = netlogList.getSelectedIndex();
					main.setSelectedNetlog(i);
				}
			}
		});
		netlogList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
					openMenu(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
					openMenu(e);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
					openMenu(e);
			}
			
			private void openMenu(MouseEvent e)
			{
				int i = netlogList.locationToIndex(e.getPoint());
				final Netlog netlog = netlogListModel.get(i);
				
				if(netlogMenu == null)
				{
					netlogMenu = new WebPopupMenu("Netlog menu");
					netlogMenuBoldItem = new WebCheckBoxMenuItem("Marked");
					netlogMenu.add(netlogMenuBoldItem);
				}
				
				//Clear old listeners
				for(ItemListener listener : netlogMenuBoldItem.getItemListeners())
					netlogMenuBoldItem.removeItemListener(listener);
				
				//Set up menu components
				netlogMenuBoldItem.setSelected(netlog.isMarked());
				
				//Add new listener
				netlogMenuBoldItem.addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e)
					{
						netlog.setMarked(e.getStateChange() == ItemEvent.SELECTED);
						netlogList.repaint();
					}
				});
				
				//Open menu
				Point p = SwingUtilities.convertPoint(netlogList, e.getPoint(), NetlogViewerUI.this);
				netlogMenu.show(NetlogViewerUI.this, (int)p.getX()-28, (int)p.getY()-4);
			}
		});
		
		chartPanel.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	        	chartPanel.setMaximumDrawHeight(e.getComponent().getHeight());
	        	chartPanel.setMaximumDrawWidth(e.getComponent().getWidth());
	        	chartPanel.setMinimumDrawWidth(e.getComponent().getWidth());
	        	chartPanel.setMinimumDrawHeight(e.getComponent().getHeight());
	        }
	    });
		
		fileDropOverlay.setTransferHandler(new FileDropHandler() {
			@Override
			protected boolean isDropEnabled()
			{
				return true;
			}
			
			@Override
			protected boolean filesImported(final List<File> files)
			{
				for(File file : files)
				{
					if(Netlog.NETLOG_FILENAME_FILTER.accept(file.getParentFile(), file.getName()))
					{
						Netlog netlog = NetlogLoader.loadNetlog(file);
						main.setNetlog(netlog);
						
						//Show success popup
						WebNotificationPopup popup = new WebNotificationPopup();
						popup.setIcon(NotificationIcon.file);
						popup.setContent("Loaded \""+file.getName()+"\".");
						popup.setDisplayTime(2000);
						NotificationManager.showNotification(NetlogViewerUI.this, popup);
						
						return true;
					}
					else
					{
						//Show success popup
						WebNotificationPopup popup = new WebNotificationPopup();
						popup.setIcon(NotificationIcon.error);
						popup.setContent("\""+file.getName()+"\" is not a valid netlog.");
						popup.setDisplayTime(4000);
						NotificationManager.showNotification(NetlogViewerUI.this, popup);
					}
				}
				return false;
			}
		});
		
		eventsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					savedEventSelection = eventsList.getSelectedIndices();
					main.setSelectedEvents(savedEventSelection);
				}
			}
		});
		
		ItemListener chartTypeListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				main.chartTypeSelectionChanged();
			}
		};
		dataTypeCheckBox.addItemListener(chartTypeListener);
		dataRateTypeCheckBox.addItemListener(chartTypeListener);
		appDataTypeCheckBox.addItemListener(chartTypeListener);
		appDataRateTypeCheckBox.addItemListener(chartTypeListener);
		pingTypeCheckBox.addItemListener(chartTypeListener);
		delayTypeCheckBox.addItemListener(chartTypeListener);
		packetTypeCheckBox.addItemListener(chartTypeListener);
		packetRateTypeCheckBox.addItemListener(chartTypeListener);
		
		ItemListener eventListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				main.eventTypeSelectionChanged();
			}
		};
		infoEventCheckBox.addItemListener(eventListener);
		warningEventCheckBox.addItemListener(eventListener);
		problemEventCheckBox.addItemListener(eventListener);
	}
	
	private void initModels()
	{
		chart = ChartFactory.createXYBarChart(null, null, false, null, null, PlotOrientation.VERTICAL, true, false, false);
		chart.setAntiAlias(true);
		chart.setBackgroundPaint(WebPanelStyle.backgroundColor);
		chart.getPlot().setBackgroundPaint(WebPanelStyle.backgroundColor);
		chart.getPlot().setNoDataMessage("It's... it's not like there's any data here. Select a netlog, baka!");
		chart.getXYPlot().setRangeAxis(null);
		NumberAxis domainAxis = new NumberAxis();
		domainAxis.setAutoRange(true);
		domainAxis.setUpperMargin(0.001);
		domainAxis.setTickMarksVisible(true);
		domainAxis.setTickMarkOutsideLength(4);
		domainAxis.setMinorTickMarksVisible(true);
		domainAxis.setLabel("Time since start of log (ms)");
		domainAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domainAxis.setLabelPaint(Color.black);
		domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		domainAxis.setTickLabelPaint(Color.black);
		domainAxis.setTickUnit(new NumberTickUnit(250_000, UIUtil.COOL_FORMATTER, 5));
		chart.getXYPlot().setDomainAxis(domainAxis);
		chartPanel.setChart(chart);
		
		fileDropOverlay = new WebPanel();
		fileDropOverlay.setOpaque(false);
		fileDropOverlay.setFocusable(true);
		chartOverlayPanel.addOverlay(fileDropOverlay);
		
		netlogList.setModel(netlogListModel = new DefaultListModel<>());
		
		eventsList.setModel(eventListModel = new DefaultListModel<>());
		
		netlogDataTable.setModel(netlogTableModel = new NetlogTableModel());
	}
	
	//Utility methods
	
	private void disableListeners()
	{
		netlogDirField.removePathFieldListener(netlogDirListener);
		netlogList.removeListSelectionListener(netlogListListener);
	}
	
	private void enableListeners()
	{
		
		netlogDirField.addPathFieldListener(netlogDirListener);
		netlogList.addListSelectionListener(netlogListListener);
	}
	
	//Interface methods
	
	public void setNetlogDir(File netlogDir)
	{
		disableListeners();
		netlogDirField.setSelectedPath(netlogDir);
		enableListeners();
	}
	
	public void setNetlogs(List<Netlog> netlogs)
	{
		netlogListModel.clear();
		for(Netlog netlog : netlogs)
			netlogListModel.addElement(netlog);
	}
	
	public void setNetlog(Netlog netlog)
	{
		//Nothing here
	}
	
	public void setChart(List<XYSeriesCollection> data, List<ValueAxis> axes, List<XYItemRenderer> renderers)
	{
		if(data.size() != axes.size() || axes.size() != renderers.size())
			return;
		
		//Reset chart
		for(int n = 0; n < chart.getXYPlot().getDatasetCount(); n++)
			chart.getXYPlot().setDataset(n, null);
		for(int n = 0; n < chart.getXYPlot().getRangeAxisCount(); n++)
			chart.getXYPlot().setRangeAxis(n, null);
		
		//Set chart
		for(int n = 0; n < data.size(); n++)
		{
			chart.getXYPlot().setDataset(n, data.get(n));
			chart.getXYPlot().setRangeAxis(n, axes.get(n));
			chart.getXYPlot().mapDatasetToRangeAxis(n, n);
			chart.getXYPlot().setRenderer(n, renderers.get(n));
		}
		
		//Fix selections
		main.setSelectedEvents(savedEventSelection);
	}
	
	public void setEvents(List<NetlogEvent> events)
	{
		eventListModel.removeAllElements();
		for(NetlogEvent event : events)
			eventListModel.addElement(event);
	}
	
	public void setChartHighlights(List<Long> xValues, List<Color> colors)
	{
		//Remove old annotations
		List<XYAnnotation> annos = chart.getXYPlot().getAnnotations();
		for(XYAnnotation anno : annos)
			chart.getXYPlot().removeAnnotation(anno, false);
		
		//Add new annotations
		if(chart.getXYPlot().getRangeAxis() != null)
		{
			for(int n = 0; n < xValues.size(); n++)
			{
				long x = xValues.get(n);
				double y = chart.getXYPlot().getRangeAxis().getUpperBound();
				Color c = UIUtil.addAlpha(UIUtil.adjustColor(colors.get(n), -25), 150);
				Stroke stroke = new BasicStroke(3.0f);
				XYLineAnnotation anno = new XYLineAnnotation(x, 0, x, y, stroke, c);
				chart.getXYPlot().addAnnotation(anno);
			}
		}
	}
	
	public void setChartTypeSelected(NetlogChartType type)
	{
		switch(type)
		{
			case DATA: dataTypeCheckBox.setSelected(true);
				break;
			case DATA_RATES: dataRateTypeCheckBox.setSelected(true);
				break;
			case PACKETS: packetTypeCheckBox.setSelected(true);
				break;
			case PACKET_RATES: packetRateTypeCheckBox.setSelected(true);
				break;
			case APP_DATA: appDataTypeCheckBox.setSelected(true);
				break;
			case APP_DATA_RATES: appDataRateTypeCheckBox.setSelected(true);
				break;
			case PING: pingTypeCheckBox.setSelected(true);
				break;
			case DELAYS: delayTypeCheckBox.setSelected(true);
				break;
		}
	}
	
	public void setEventTypeSelected(NetlogEvent.Type type)
	{
		switch(type)
		{
			case INFO: infoEventCheckBox.setSelected(true);
				break;
			case WARNING: warningEventCheckBox.setSelected(true);
				break;
			case PROBLEM: problemEventCheckBox.setSelected(true);
				break;
		}
	}
	
	public void close()
	{
		dispose();
	}
	
	//Accessor methods
	
	public List<NetlogChartType> getSelectedChartTypes()
	{
		List<NetlogChartType> types = new ArrayList<>();
		if(pingTypeCheckBox.isSelected())
			types.add(NetlogChartType.PING);
		if(delayTypeCheckBox.isSelected())
			types.add(NetlogChartType.DELAYS);
		if(dataTypeCheckBox.isSelected())
			types.add(NetlogChartType.DATA);
		if(dataRateTypeCheckBox.isSelected())
			types.add(NetlogChartType.DATA_RATES);
		if(appDataTypeCheckBox.isSelected())
			types.add(NetlogChartType.APP_DATA);
		if(appDataRateTypeCheckBox.isSelected())
			types.add(NetlogChartType.APP_DATA_RATES);
		if(packetTypeCheckBox.isSelected())
			types.add(NetlogChartType.PACKETS);
		if(packetRateTypeCheckBox.isSelected())
			types.add(NetlogChartType.PACKET_RATES);
		return types;
	}
	
	public List<NetlogEvent.Type> getSelectedEventTypes()
	{
		List<NetlogEvent.Type> types = new ArrayList<>();
		if(infoEventCheckBox.isSelected())
			types.add(NetlogEvent.Type.INFO);
		if(warningEventCheckBox.isSelected())
			types.add(NetlogEvent.Type.WARNING);
		if(problemEventCheckBox.isSelected())
			types.add(NetlogEvent.Type.PROBLEM);
		return types;
	}
}
