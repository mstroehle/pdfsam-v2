/*
 * Created on 29-Sep-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.thumbnails.creators;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.i18n.GettextResource;

/**
 * Abstract thumbnail creator
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractThumbnailCreator implements ThumbnailsCreator {
	
	private static final Logger log = Logger.getLogger(AbstractThumbnailCreator.class.getPackage().getName());
	
	public BufferedImage getPageImage(String fileName, String password, int page) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		if(fileName != null && fileName.length()>0){
    		File inputFile = new File(fileName);
    		retVal = getPageImage(inputFile, password, page);
		}else{
			throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create image for a null input document"));
		}
		return retVal;
	}
	
	public void initThumbnailsPanel(String fileName, String password, JVisualPdfPageSelectionPanel panel) {
		try{
			if(fileName != null && fileName.length()>0){
	    		File inputFile = new File(fileName);
	    		initThumbnailsPanel(inputFile, password, panel);			
			}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create thumbnails for a null input document"));
			}
        }catch(Exception e){
        	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), e);
        }
	}
	
	

	public BufferedImage getThumbnail(String fileName, String password, int page, float resizePercentage, String quality) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		if(fileName != null && fileName.length()>0){
			File inputFile = new File(fileName);
    		retVal = getThumbnail(inputFile, password, page, resizePercentage, quality);
		}else{
			throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create image for a null input document"));
		}
		return retVal;		
	}

}
