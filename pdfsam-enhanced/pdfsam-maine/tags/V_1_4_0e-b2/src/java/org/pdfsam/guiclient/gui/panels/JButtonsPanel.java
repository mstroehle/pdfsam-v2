/*
 * Created on 08-Nov-2007
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
package org.pdfsam.guiclient.gui.panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.listeners.ExitActionListener;
import org.pdfsam.guiclient.business.listeners.LogActionListener;
import org.pdfsam.guiclient.business.listeners.mediators.EnvironmentMediator;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Buttons bar for the main GUI
 * 
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class JButtonsPanel extends JPanel {

	private static final long serialVersionUID = 203934401531647182L;

	private static final Logger log = Logger.getLogger(JButtonsPanel.class.getPackage().getName());
	
	private JToolBar toolBar;
	private Configuration config;
	private JButton buttonSaveEnv;
	private JButton buttonLoadEnv;
	private JButton buttonSaveLog;
	private JButton buttonClearLog;
	private JButton buttonExit;
	private EnvironmentMediator envMediator;
	private LogActionListener logActionListener;
	
	public JButtonsPanel(EnvironmentMediator envMediator, LogActionListener logActionListener){
		this.envMediator = envMediator;
		this.logActionListener = logActionListener;
		this.config = Configuration.getInstance();			
		init();
	}
	
	/**
	 * Initialize the panel
	 */
	private void init(){
		try{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setPreferredSize(new Dimension(600, 30));
	
			toolBar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);	
	        toolBar.setFloatable(true);
	        toolBar.setRollover(true);
	        toolBar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	
	        buttonSaveEnv = new JButton(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
	        buttonSaveEnv.setActionCommand(EnvironmentMediator.SAVE_ENV_ACTION);
	        buttonSaveEnv.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Save environment"));
	        buttonSaveEnv.addActionListener(envMediator);
	
	        buttonLoadEnv = new JButton(new ImageIcon(this.getClass().getResource("/images/fileopen.png")));
	        buttonLoadEnv.setActionCommand(EnvironmentMediator.LOAD_ENV_ACTION);
	        buttonLoadEnv.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Load environment"));
	        buttonLoadEnv.addActionListener(envMediator);
	        
	        buttonSaveLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-save.png")));
	        buttonSaveLog.setActionCommand(LogActionListener.SAVE_LOG_ACTION);
	        buttonSaveLog.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Save log"));
	        buttonSaveLog.addActionListener(logActionListener);
	        
	        buttonClearLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-clear.png")));
	        buttonClearLog.setActionCommand(LogActionListener.CLEAR_LOG_ACTION);
	        buttonClearLog.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear log"));
	        buttonClearLog.addActionListener(logActionListener);
	        
	        buttonExit = new JButton(new ImageIcon(this.getClass().getResource("/images/exit.png")));
	        buttonExit.setActionCommand(ExitActionListener.EXIT_COMMAND);
	        buttonExit.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Exit"));
	        buttonExit.addActionListener(new ExitActionListener());
	        
	        toolBar.add(buttonSaveEnv);
	        toolBar.add(buttonLoadEnv);
	        toolBar.addSeparator();
	        toolBar.add(buttonSaveLog);
	        toolBar.add(buttonClearLog);
	        toolBar.addSeparator();
	        toolBar.add(buttonExit);
	        add(toolBar);
		}catch(Exception e){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Unable to initialize button bar."), e);
		}
	}	
}
