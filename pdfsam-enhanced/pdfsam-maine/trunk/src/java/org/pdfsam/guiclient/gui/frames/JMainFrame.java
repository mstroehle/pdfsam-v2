/*
 * Created on 09-Nov-2007
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.guiclient.gui.frames;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.utils.TimeUtility;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.business.Environment;
import org.pdfsam.guiclient.business.listeners.LogActionListener;
import org.pdfsam.guiclient.business.listeners.mediators.EnvironmentMediator;
import org.pdfsam.guiclient.business.listeners.mediators.TreeMediator;
import org.pdfsam.guiclient.business.listeners.mediators.UpdateCheckerMediator;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JMainMenuBar;
import org.pdfsam.guiclient.gui.panels.JButtonsPanel;
import org.pdfsam.guiclient.gui.panels.JInfoPanel;
import org.pdfsam.guiclient.gui.panels.JLogPanel;
import org.pdfsam.guiclient.gui.panels.JSettingsPanel;
import org.pdfsam.guiclient.gui.panels.JStatusPanel;
import org.pdfsam.guiclient.gui.panels.JTreePanel;
import org.pdfsam.guiclient.plugins.PlugInsLoader;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.plugins.models.PluginDataModel;
import org.pdfsam.i18n.GettextResource;

/**
 * Main frame
 * @author Andrea Vacondio
 *
 */
public class JMainFrame extends JFrame {

	private static final long serialVersionUID = -3858244069059677829L;

	private static final Logger log = Logger.getLogger(JMainFrame.class.getPackage().getName());
	private String DEFAULT_ICON = "/images/pdf.png";
	private String DEFAULT_ICON_16 = "/images/pdf16.png";
	
	private JSplashScreen screen;
	private Map<PluginDataModel, AbstractPlugablePanel> pluginsMap;
	private EnvironmentMediator envMediator;
	private UpdateCheckerMediator updateMediator;
	private JStatusPanel statusPanel;
	private JTreePanel treePanel;
	private JButtonsPanel buttonsPanel;
	private JPanel mainPanel = new JPanel(new CardLayout());
	private JScrollPane mainScrollPanel;
	private JSplitPane verticalSplitPane;
	private JSplitPane horizSplitPane;
	private JLogPanel logPanel;
	
	public JMainFrame(){
		long start = System.currentTimeMillis();
		log.info("Starting "+GuiClient.getApplicationName()+" Ver. "+GuiClient.getVersion());
		runSplash();
		ToolTipManager.sharedInstance().setDismissDelay (300000);
		initialize();
		closeSplash();
		log.info(GuiClient.getApplicationName()+" Ver. "+GuiClient.getVersion()+" "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"started in ")+TimeUtility.format(System.currentTimeMillis()-start));
	}
	/**
	 * initialization
	 */
	private void initialize() {
		try{			
			//center(this,800,600);
			URL iconUrl = this.getClass().getResource("/images/pdf_"+GuiClient.getVersionType()+".png");
			URL iconUrl16 = this.getClass().getResource("/images/pdf_"+GuiClient.getVersionType()+"16.png");
			if(iconUrl == null){
				iconUrl = this.getClass().getResource(DEFAULT_ICON);
			}
			if(iconUrl16 == null){
				iconUrl16 = this.getClass().getResource(DEFAULT_ICON_16);
			}
			setIconImage(new ImageIcon(iconUrl).getImage());
	        setTitle(GuiClient.getApplicationName()+" Ver. "+GuiClient.getVersion());
	        
	        //load plugins
	        setSplashStep(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Loading plugins.."));
	        PlugInsLoader pluginsLoader = new PlugInsLoader(Configuration.getInstance().getPluginAbsolutePath());
	        pluginsMap = pluginsLoader.loadPlugins();
			
			//Info panel
			JInfoPanel infoPanel = new JInfoPanel(pluginsMap);
			PluginDataModel infoDataModel = new PluginDataModel(infoPanel.getPluginName(), infoPanel.getVersion(), infoPanel.getPluginAuthor(), infoPanel.getClass().getName());
			mainPanel.add(infoPanel,infoPanel.getPluginName());
			
	        //Settings panel
	        JSettingsPanel settingsPanel = new JSettingsPanel();
			PluginDataModel settingsDataModel = new PluginDataModel(settingsPanel.getPluginName(), settingsPanel.getVersion(), settingsPanel.getPluginAuthor(), settingsPanel.getClass().getName());
			mainPanel.add(settingsPanel,settingsPanel.getPluginName());
			
	        //sets main panel
	        mainPanel.setPreferredSize(new Dimension(670,500));
	        for(AbstractPlugablePanel instance: pluginsMap.values()){
	        	mainPanel.add(instance,instance.getPluginName());
	        }
	        
	        //status panel
	        setSplashStep(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Building status bar.."));
	        statusPanel = new JStatusPanel(new ImageIcon(iconUrl16),GuiClient.getApplicationName(),WorkDoneDataModel.MAX_PERGENTAGE);
	        getContentPane().add(statusPanel,BorderLayout.PAGE_END); 
	        Configuration.getInstance().getConsoleServicesFacade().addExecutionObserver(statusPanel);
			
	        //tree panel
	        setSplashStep(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Building tree.."));
	        treePanel = new JTreePanel(new DefaultMutableTreeNode(GuiClient.UNIXNAME+" "+GuiClient.getVersion()));
	    	for (PluginDataModel item : pluginsMap.keySet()) {
	    		treePanel.addToPlugsNode(item);
	        }
	        treePanel.addToRootNode(settingsDataModel);
	        treePanel.addToRootNode(infoDataModel);
	        treePanel.getTree().addTreeSelectionListener(new TreeMediator(this));
	        treePanel.expand();
	        
	        //add info and settings to plugins map
	        pluginsMap.put(settingsDataModel, settingsPanel);
	        pluginsMap.put(infoDataModel, infoPanel);	        	      
	        
	        //menu
	        setSplashStep(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Building menus.."));        
	        //env mediator
	        envMediator = new EnvironmentMediator(new Environment(pluginsMap, treePanel), this);
	        getRootPane().setJMenuBar(new JMainMenuBar(envMediator));
	        
	        //buttons bar
	        setSplashStep(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Building buttons bar.."));
	        buttonsPanel = new JButtonsPanel(envMediator, new LogActionListener());
	        getContentPane().add(buttonsPanel,BorderLayout.PAGE_START);  
	        
	        //set up check for updates mediator
	        updateMediator = new UpdateCheckerMediator(statusPanel);
	        if(Configuration.getInstance().isCheckForUpdates()){
	        	updateMediator.checkForUpdates(5000, false);
	        }
	        settingsPanel.setCheckUpdateMediator(updateMediator);
	        
	        //final set up
	        mainScrollPanel = new JScrollPane(mainPanel);
	        mainScrollPanel.setMinimumSize(new Dimension(100, 400));
	        
	        logPanel = new JLogPanel();
	        
	        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, mainScrollPanel);
	        horizSplitPane.setOneTouchExpandable(true);
	        horizSplitPane.setDividerLocation(155);
	        
	        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizSplitPane,logPanel);
	        verticalSplitPane.setOneTouchExpandable(true);
	        verticalSplitPane.setResizeWeight(1.0);
	        verticalSplitPane.setDividerLocation(0.75);
	        
	        //load the default env if set
	        String defaultEnvString = Configuration.getInstance().getDefaultEnvironment();
	        if(defaultEnvString!=null && defaultEnvString.length()>0){
	        	File defaultEnv = new File(defaultEnvString);
		        if(defaultEnv != null && defaultEnv.exists() && defaultEnv.isFile()){
		        	log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Loading default environment."));
		        	envMediator.getEnvironment().loadJobs(defaultEnv);
		        }
	        }
	        getContentPane().add(verticalSplitPane,BorderLayout.CENTER);
	        
	        setSize(640, 480);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}catch(Exception e){
			log.fatal(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error starting pdfsam."),e);
		}
		
	}	
    
	 /**
     * Run a splash screen
     */
    private void runSplash(){
    		screen = new JSplashScreen("pdfsam loader", "Initialization..");
			screen.setMaximumBarValue(5);
    		Runnable runner = new Runnable() {
    			public void run() {    				
    				screen.setVisible(true);
    			}
    		};
    		SwingUtilities.invokeLater(runner);
    }
    
    /**
     * close the splash screen
     */
    private void closeSplash(){
        if(screen != null){
	    	screen.setVisible(false);	                   
	    	screen.dispose();
	    }
    }
    
    /**
     * Sets the splash text and increment the progress bar
     * @param message
     */
    private void setSplashStep(String message){
    	if(screen != null){
    		screen.setText(message);
    		screen.addBarValue();
    	}
    }
	/**
	 * @return the statusPanel
	 */
	public JStatusPanel getStatusPanel() {
		return statusPanel;
	}
	/**
	 * @return the treePanel
	 */
	public JTreePanel getTreePanel() {
		return treePanel;
	}
	/**
	 * @return the buttonsPanel
	 */
	public JButtonsPanel getButtonsPanel() {
		return buttonsPanel;
	}
	/**
	 * @return the pluginsMap
	 */
	public Map<PluginDataModel, AbstractPlugablePanel> getPluginsMap() {
		return pluginsMap;
	}
	/**
	 * @return the mainPanel
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}    
    
	/**
	 * sets the minimum size of the scroll pane
	 * @param d
	 */
	public void setMainPanelPreferredSize(Dimension d){
		mainPanel.setPreferredSize(d);
	}
}
