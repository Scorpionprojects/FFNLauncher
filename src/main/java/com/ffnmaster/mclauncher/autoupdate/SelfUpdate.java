package com.ffnmaster.mclauncher.autoupdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class SelfUpdate {
	
	public static enum OS {
		WINDOWS,
		LINUX,
		MACOSX,
		OTHER,
	}
	
	/**
	 * @param resource - the resource to delete
	 * @return whether deletion was successful
	 * @throws IOException
	 */
	public static boolean delete(File resource) throws IOException {
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}
		}
		return resource.delete();
	}
	
	/**
	 * @param sourceFile - the file to be moved
	 * @param destinationFile - where to move to
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destinationFile) throws IOException {
		if (sourceFile.exists()) {
			if(!destinationFile.exists()) {
				destinationFile.createNewFile();
			}
			FileChannel sourceStream = null, destinationStream = null;
			try {
				sourceStream = new FileInputStream(sourceFile).getChannel();
				destinationStream = new FileOutputStream(destinationFile).getChannel();
				destinationStream.transferFrom(sourceStream, 0, sourceStream.size());
			} finally {
				if(sourceStream != null) {
					sourceStream.close();
				}
				if(destinationStream != null) {
					destinationStream.close();
				}
			}
		}
	}
	
	/**
	 * Used to get the current operating system
	 * @return OS enum representing current operating system
	 */
	public static OS getCurrentOS() {
		String osString = System.getProperty("os.name").toLowerCase();
		if (osString.contains("win")) {
			return OS.WINDOWS;
		} else if (osString.contains("nix") || osString.contains("nux")) {
			return OS.LINUX;
		} else if (osString.contains("mac")) {
			return OS.MACOSX;
		} else {
			return OS.OTHER;
		}
	}
	
	
	public static void runUpdate(String currentPath, String temporaryUpdatePath) {
		List<String> arguments = new ArrayList<String>();
		
		String separator = System.getProperty("file.separator");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		arguments.add(path);
		arguments.add("-cp");
		arguments.add(temporaryUpdatePath);
		arguments.add(SelfUpdate.class.getCanonicalName());
		arguments.add(currentPath);
		arguments.add(temporaryUpdatePath);

		System.out.println("Would update with: " + arguments);
		System.out.println("c: " + currentPath);
		System.out.println("n: " + temporaryUpdatePath);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(arguments);
		try {
			processBuilder.start();
		} catch (IOException e) { System.out.println("Failed to start self-update process" + e); }
		System.exit(0);
	}
	
	public static void main(String[] args) {
		try {
			if (getCurrentOS() != OS.LINUX) {
				Thread.sleep(4000);
			}
		} catch (InterruptedException ignored) {
			System.out.println("Thread interrupt v005");
		}
		
		String launcherPath = args[0];
		String temporaryUpdatePath = args[1];
		File launcher = new File(launcherPath);
		File temporaryUpdate = new File(temporaryUpdatePath);
		try {
			delete(launcher);
			copyFile(temporaryUpdate, launcher);
		} catch (IOException e) {
			System.out.println("Auto updating failed");
		}
		
		List<String> arguments = new ArrayList<String>();
		String separator = System.getProperty("file.seperator");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		arguments.add(path);
		arguments.add("-jar");
		arguments.add(launcherPath);
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(arguments);
		
		try {
			processBuilder.start();
		} catch (IOException e) {
			System.out.println("Failed to start FFNLauncher after updating. Good luck! v006");
		}	
	}
}
