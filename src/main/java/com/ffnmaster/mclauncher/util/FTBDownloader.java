package com.ffnmaster.mclauncher.util;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class FTBDownloader extends Thread {
	public static boolean serversLoaded = false;
	public static HashMap<String, String> downloadServers = new HashMap<String, String>();
	private static String currentmd5 = "";
	
	public static String getFTBDownloadLink(String file) throws NoSuchAlgorithmException{
		if(currentmd5.isEmpty()) {
			currentmd5 = md5("mcepoch1" + getTime());
		}
		
		return resolved;
	}
	
	public static String getTime() {
		String content = null;
		Scanner scanner = null;
		String resolved = (downloadServers.containsKey())
		
		return content;
	}
}
