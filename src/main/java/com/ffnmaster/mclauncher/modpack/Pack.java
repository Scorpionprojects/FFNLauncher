package com.ffnmaster.mclauncher.modpack;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.config.Configuration;
import com.ffnmaster.mclauncher.modpack.Loader;

public class Pack {
	private String name, author, version, url, dir, mcVersion, serverUrl, logoName, imageName, info, animation, sep = File.separator, xml;
	private String[] mods, oldVersions;
	private Image logo, image;
	private int index;
	private boolean updated = false;
	private final static ArrayList<Pack> packs = new ArrayList<Pack>();
	private static List<ModPackListener> listeners = new ArrayList<ModPackListener>();
	private boolean privatePack;
	private BufferedImage cachedIcon;
	
	
	/**
	 * Loads modpack.xml and adds it to the modpack array in this class
	 * @param xmlFile
	 */
	public static void loadXml(ArrayList<String> xmlFile) {
		Loader loader= new Loader(xmlFile);
		loader.start();
	}
	
	public static void loadXml(String xmlFile) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(xmlFile);
		Loader loader = new Loader(temp);
		loader.start();
	}
	
	/**
	 * Adds a listener to the listeners array
	 */
	public static void addListener(ModPackListener listener) {
		listeners.add(listener);
	}
	
	public static void addPack(Pack pack) {
		synchronized (packs) {
			packs.add(pack);
		}
		for (ModPackListener listener : listeners) {
			packs.remove(pack);
		}
	}
	
	public static void removePacks(String xml) {
		ArrayList<Pack> remove = new ArrayList<Pack>();
		for(Pack pack : packs) {
			if(pack.getParentXml().equalsIgnoreCase(xml)) {
				remove.add(pack);
			}
		}
	}
	
	public static ArrayList<Pack> getPackArray() {
		return packs;
	}
	
	/**
	 * Gets the ModPack form the array and the given index
	 * @param i - the value in the array
	 * @return - the ModPack based on the i value
	 */
	public static Pack getPack(int i) {
		return packs.get(i);
	}
	
	
	public static int size() {
		return packs.size();
	}
	public static Pack getPack(String dir) {
		for(Pack pack : packs) {
			if(pack.getDir().equalsIgnoreCase(dir)) {
				return pack;
			}
		}
		return null;
	}
	

	/*public static Pack getSelectedPack() {
		return getPack(NewsLayoutManager.getSelectedModIndex());
	}*/
	
	/**
	 * The Modpack itself (DAM DAM DAM)
	 * @return
	 */
	public Pack(String name, String author, String version, String logo, String url, String image, String dir, String mcVersion, String serverUrl, String info, String mods, 
			String oldVersions, String animation, int idx, boolean privatePack, String xml) throws IOException, NoSuchAlgorithmException {
		index = idx;
		this.name = name;
		this.author = author;
		this.version = version;
		this.dir = dir;
		this.mcVersion = mcVersion;
		this.url = url;
		this.serverUrl = serverUrl;
		this.privatePack = privatePack;
		this.xml = xml;
		if(!animation.equalsIgnoreCase("")) {
			this.animation = animation;
		} else {
			this.animation = "empty";
		}
		logoName = logo;
		imageName = image;
		this.info = info;
		if(mods.isEmpty()) {
			this.mods = null;
		} else {
			this.mods = mods.split("; ");
		}
		if(oldVersions.isEmpty()) {
			this.oldVersions = null;
		} else {
			this.oldVersions = oldVersions.split(";");
		}
		String installPath = Launcher.getLauncherDir();
		File tempDir = new File(installPath, "ModPacks" + sep + dir);
		File verFile = new File(tempDir, "version");
		URL url_;
		if(!upToDate(verFile)) {
			url_ = new URL(Launcher.getUpdateLink(logo));
			this.logo = Toolkit.getDefaultToolkit().createImage(url_);
			BufferedImage tempImg = ImageIO.read(url_);
			ImageIO.write(tempImg, "png", new File(tempDir, logo));
			tempImg.flush();
		} else {
			if (new File(tempDir, logo).exists()) {
				this.logo = Toolkit.getDefaultToolkit().createImage(tempDir.getPath() + sep + logo);
			} else {
				url_ = new URL(Launcher.getUpdateLink(logo));
				this.logo = Toolkit.getDefaultToolkit().createImage(url_);
				BufferedImage tempImg = ImageIO.read(url_);
				ImageIO.write(tempImg, "png", new File(tempDir, logo));
				tempImg.flush();
			}
			if(new File(tempDir, image).exists()) {
				this.image = Toolkit.getDefaultToolkit().createImage(tempDir.getPath() + sep + image);
			} else {
				url_ = new URL(Launcher.getUpdateLink(image));
				this.image = Toolkit.getDefaultToolkit().createImage(url_);
				BufferedImage tempImg = ImageIO.read(url_);
				ImageIO.write(tempImg, "png", new File(tempDir, image));
				tempImg.flush();
			}
			
		}
	}
	
	
    /**
     * Try to load an icon from the JAR.
     * 
     * @param path path
     * @return this object
     */
    public Pack loadIcon(String path) {
        InputStream in = Launcher.class.getResourceAsStream(path);
        
        if (in != null) {
            try {
                cachedIcon = ImageIO.read(in);
            } catch (IOException e) {
                System.out.println("Failed to load icon at " + path);
            }
        }
        
        return this;
    }
	
	/**
	 * Used to check if the cached items are up to date
	 * @param verFile - the version file to check
	 * @return checks the version file against the current modpack version
	 */
	private boolean upToDate(File verFile) {
		boolean result = true;
		try {
			if(!verFile.exists()) {
				verFile.getParentFile().mkdirs();
				verFile.createNewFile();
				result = false;
			}
			BufferedReader in = new BufferedReader(new FileReader(verFile));
			String line;
			if((line = in.readLine()) == null || Integer.parseInt(version.replace(".", "")) > Integer.parseInt(line.replace(".", ""))) {
				BufferedWriter out = new BufferedWriter(new FileWriter(verFile));
				out.write(version);
				out.flush();
				out.close();
				result = false;
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return result;
	}
	
	
	/**
	 * Used to get index of modpack
	 * @return - the index of the modpack in the GUI
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Used to get name of modpack
	 * @return - the name of the modpack
	 */
	public String getName() {
		return name;
	}

	/**
	 * Used to get Author of modpack
	 * @return - the modpack's author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Used to get the version of the modpack
	 * @return - the modpacks version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Used to get an Image variable of the modpack's logo
	 * @return - the modpacks logo
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * Used to get the URL or File name of the modpack
	 * @return - the modpacks URL
	 */
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		String title;
		if (Integer.parseInt(version.replace(".", "")) == 0) {
			title = name;
		} else {
			title = name + " | " + version;
		}
		
		return title;
	}
	
	public BufferedImage getIcon(){
		return cachedIcon;
	}

	/**
	 * Used to get an Image variable of the modpack's splash image
	 * @return - the modpacks splash image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Used to get the directory of the modpack
	 * @return - the directory for the modpack
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * Used to get the minecraft version required for the modpack
	 * @return - the minecraft version
	 */
	public String getMcVersion() {
		return mcVersion;
	}

	/**
	 * Used to get the info or description of the modpack
	 * @return - the info for the modpack
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Used to get an array of mods inside the modpack
	 * @return - string array of all mods contained
	 */
	public String[] getMods() {
		return mods;
	}

	/**
	 * Used to get the name of the server file for the modpack
	 * @return - string representing server file name
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Used to get the logo file name
	 * @return - the logo name as saved on the repo
	 */
	public String getLogoName() {
		return logoName;
	}

	/**
	 * Used to get the splash file name
	 * @return - the splash image name as saved on the repo
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Used to set whether the modpack has been updated
	 * @param result - the status of whether the modpack has been updated or not
	 */
	public void setUpdated(boolean result) {
		updated = result;
	}

	/**
	 * Used to check if the modpack has been updated
	 * @return - the boolean representing whether the modpack has been updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Used to get all available old versions of the modpack
	 * @return - string array containing all available old version of the modpack
	 */
	public String[] getOldVersions() {
		return oldVersions;
	}

	/**
	 * Used to set the minecraft version required of the pack to a custom version
	 * @param version - the version of minecraft for the pack
	 */
	public void setMcVersion(String version) {
		mcVersion = version;
	}

	/**
	 * @return the filename of the gif animation to display before minecraft loads
	 */
	public String getAnimation() {
		return animation;
	}

	public boolean isPrivatePack() {
		return privatePack;
	}

	public String getParentXml() {
		return xml;
	}
	
	
	
	
	
	
}
