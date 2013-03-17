package com.ffnmaster.mclauncher.modpack;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.ffnmaster.mclauncher.modpack.Pack;

public class ModPackInstaller {
	
	public static String getModPackDir() {
		String currentDir = new File(".").getAbsolutePath();
		
		String dir = currentDir + File.separator + "ModPacks" + File.separator + "Tempdir";
		return dir;
	}
	
	protected boolean downloadModPack(String modPackName, String dir) throws IOException, NoSuchAlgorithmException {
		System.out.println("Downloading ModPack");
		String storage = "SomeDirectory"; // GBH
		String LauncherPath = getModPackDir();
		Pack pack = Pack.getSelectedPack();
		
		
		return false;
	}
	
	
	
}
