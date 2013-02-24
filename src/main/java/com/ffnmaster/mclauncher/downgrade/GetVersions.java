/*
 * FFNLauncher
 * Copyright (C) 2013 Abel Hoogeveen <http://www.sigmacoders.nl>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.ffnmaster.mclauncher.downgrade;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Gets version.xml from SigmaCoders
 * 
 * @author FFNMaster
 */
public class GetVersions{
	
	/**
	* Download version info from SigmaCoders
	*/
	public GetVersions {
		String xmlFile = "versions.xml";	
		URL xmlLoc;
    	URLConnection connection;
    	DataInputStream dis;
    	FileOutputStream fos;
    	byte[] fileData;
    	String Location = "http://download.sigmacoders.nl/FFNLauncher/";
    	String xmlLocation = Location + xmlFile;
    	
    	try {
    		xmlLoc = new URL(xmlLocation);
    		connection = xmlLoc.openConnection();
    		dis = new DataInputStream(connection.getInputStream());
    		fileData = new byte[connection.getContentLength()];
    		for (int x = 0; x<fileData.length; x++) {
    			fileData[x] = dis.readByte();
    		}
    		dis.close();
    		fos = new FileOutputStream(new File(xmlFile));
    		fos.write(fileData);
    		fos.close();
    	}
    	catch(MalformedURLException mue){
    		System.out.println("An error occured in the version path");
    	}
    	catch(IOException io) {
    		System.out.println(io);
    	}
	}
}
