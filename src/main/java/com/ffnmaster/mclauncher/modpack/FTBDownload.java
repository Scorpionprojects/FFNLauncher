package com.ffnmaster.mclauncher.modpack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Formatter;


public class FTBDownload extends Thread {
	// Vars
	public static HashMap<String, String> downloadServers = new HashMap<String, String>();
	public static boolean serversLoaded = false;
	private static String currentmd5 = "";
	
	
	/**
	 * @param file - the name of the file, as saved to the repo (including extension)
	 * @return - the direct link
	 * @throws NoSuchAlgorithmException - see md5
	 */
	public static String getCreeperhostLink(String file) throws NoSuchAlgorithmException {
		if (currentmd5.isEmpty()) {
			currentmd5 = md5("mcepoch1" + getTime());
		}
		String resolved = (downloadServers.containsKey("Automatic")) ? "http://" + downloadServers.get("Automatic") : "http://www.creeperrepo.net";
		resolved += "/direct/FTB2/" + currentmd5 + "/" + file;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(resolved).openConnection();
			for(String server : downloadServers.values()) {
				if(connection.getResponseCode() != 200 && !server.equalsIgnoreCase("www.creeperrepo.net")) {
					resolved = "http://" + server + "/direct/FTB2/" + currentmd5 + "/" + file;
					connection = (HttpURLConnection) new URL(resolved).openConnection();
				}
			}
		} catch (IOException e) { }
		connection.disconnect();
		System.out.println(resolved);
		return resolved;
	}
	
	
	/**
	 * @param file - the name of the file, as saved to the repo (including extension)
	 * @return - the direct link
	 */
	public static String getStaticCreeperhostLink(String file) {
		String resolved = (downloadServers.containsKey("Automatic")) ? "http://" + downloadServers.get("Automatic") : "http://www.creeperrepo.net";
		resolved += "/static/FTB2/" + file;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(resolved).openConnection();
			if(connection.getResponseCode() != 200) {
				for(String server : downloadServers.values()) {
					if(connection.getResponseCode() != 200 && !server.equalsIgnoreCase("www.creeperrepo.net")) {
						resolved = "http://" + server + "/static/FTB2/" + file;
						connection = (HttpURLConnection) new URL(resolved).openConnection();
					} else if(connection.getResponseCode() == 200) {
						break;
					}
				}
			}
		} catch (IOException e) { }
		connection.disconnect();
		return resolved; 
	}
	
	public static boolean staticFileExists(String file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(getStaticCreeperhostLink(file)).openStream()));
			return !reader.readLine().toLowerCase().contains("not found");
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @param file - file on the repo
	 * @return boolean representing if the file exists 
	 */
	public static boolean fileExists(String file) {
		try {
			if(currentmd5.isEmpty()) {
				currentmd5 = md5("mcepoch1" + getTime());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://www.creeperrepo.net/direct/FTB2/" + currentmd5 + "/" + file).openStream()));
			return !reader.readLine().toLowerCase().contains("not found");
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String md5(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while(result.length() < 32) {
				result = "0" + result;
			}
		}
		return result;
	}
	
	
	public static String fileMD5(File file) throws IOException {
		if(!file.exists()) {
			return "";
		}
		
		URL fileUrl = file.toURI().toURL();
		MessageDigest dgest = null;
		try {
			dgest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) { }
		InputStream str = fileUrl.openStream();
		byte[] buffer = new byte[65536];
		int readLen;
		while((readLen = str.read(buffer, 0, buffer.length)) != -1) {
			dgest.update(buffer, 0, readLen);
		}
		str.close();
		Formatter fmt = new Formatter();
		for(byte b : dgest.digest()) {
			fmt.format("%02X", b);
		}
		return fmt.toString();
	}
	
	/**
	 * gets the time from the creeperhost servers
	 * @return - the time in the DDMMYY format
	 */
	public static String getTime() {
		String content = null;
		Scanner scanner = null;
		String resolved = "http://www.creeperrepo.net";
		resolved += "/getdate";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(resolved).openConnection();
			if(connection.getResponseCode() != 200) {
				for(String server : downloadServers.values()) {
					if(connection.getResponseCode() != 200 && !server.equalsIgnoreCase("www.creeperrepo.net")) {
						resolved = "http://" + server + "/getdate";
						connection = (HttpURLConnection) new URL(resolved).openConnection();
					} else if(connection.getResponseCode() == 200) {
						break;
					}
				}
			}
			scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter( "\\Z" );
			content = scanner.next();
		} catch (IOException e) { 
		} finally {
			connection.disconnect();
			if (scanner != null) {
				scanner.close();
			}
		}
		return content;
	}
	
	/**
	 * Downloads data from the given URL and saves it to the given file
	 * @param url The url to download from
	 * @param file The file to save to.
	 */
	public static void downloadToFile(URL url, File file) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}
	
	public static void downloadToFile(String filename, String urlString) throws IOException {
		downloadToFile(new URL(urlString), new File(filename));
	}
	
	/**
	 * Checks the file for corruption.
	 * @param file - File to check
	 * @return boolean representing if it is valid
	 * @throws IOException 
	 */
	public static boolean isValid(File file, String url) throws IOException {
		String content = null;
		Scanner scanner = null;
		String resolved = "http://www.creeperrepo.net";
		resolved += "/md5/FTB2/" + url;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(resolved).openConnection();
			if(connection.getResponseCode() != 200) {
				for(String server : downloadServers.values()) {
					if(connection.getResponseCode() != 200 && !server.equalsIgnoreCase("www.creeperrepo.net")) {
						resolved = "http://" + server + "/md5/FTB2/" + url;
						connection = (HttpURLConnection) new URL(resolved).openConnection();
					} else if(connection.getResponseCode() == 200) {
						break;
					}
				}
			}
			scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter( "\\Z" );
			content = scanner.next();
		} catch (IOException e) { 
		} finally {
			connection.disconnect();
			if (scanner != null) {
				scanner.close();
			}
		}
		String result = fileMD5(file);
		System.out.println("Local: " + result.toUpperCase());
		System.out.println("Remote: " + content.toUpperCase());
		return content.equalsIgnoreCase(result);
	}
	
	/**
	 * Used to load all available download servers in a thread to prevent wait
	 */
	public void run() {
		downloadServers.put("Automatic", "www.creeperrepo.net");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new URL("http://www.creeperrepo.net/mirrors").openStream()));
			String line;
			while((line = in.readLine()) != null) {
				String[] splitString = line.split(",");
				if(splitString.length == 2) {
					downloadServers.put(splitString[0], splitString[1]);
				}
			} in.close();
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
		serversLoaded = true;
		// Insert here more???
	}
}
