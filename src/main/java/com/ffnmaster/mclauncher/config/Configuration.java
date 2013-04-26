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

package com.ffnmaster.mclauncher.config;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.MinecraftJar;
import com.ffnmaster.mclauncher.addons.AddonsProfile;
import com.ffnmaster.mclauncher.util.SettingsList;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;

/**
 * Represents a configuration for the game.
 * 
 * @author sk89q
 */
public class Configuration {
    
    private static final Logger logger = Logger.getLogger(Configuration.class.getCanonicalName());
    
    private String id;
    private File customBasePath;
    private String appDir;
    private String name;
    private URL updateUrl;
    private String lastActiveJar;
    private SettingsList settings = new SettingsList();
    private boolean builtIn = false;
    private BufferedImage cachedIcon;
    private Image logo, image;
    private String iconAddress;
    
    // FTB
    private String subtitle;
    private int version;
    private String ftb;
    private String ftbDir;
    private String serverURL;
    
    /**
     * Construct a configuration.
     * @param id id
     * @param name name
     * @param subtitle of the modpack
     * @param int version of the modpack
     * @param serverURL of the server software
     * @param Bool isFTB
     * @param String iconaddress
     * @param f data directory name
     */
    public Configuration(String id, String name, String subtitle, int version, String serverURL, String ftb, String iconaddress, String f, URL updateUrl) {
        if (!id.matches("^[A-Za-z0-9\\-]+{1,64}$")) {
            throw new IllegalArgumentException("Invalid configuration name");
        }
        this.id = id;
        setName(name);
        setAppDir(f);
        setUpdateUrl(updateUrl);
        
        // FTB Compat
        setSubtitle(subtitle);
        setVersion(version);
        setServerURL(serverURL);
        setFTB(ftb);
        setIcon(iconaddress);
    }
    
    public void setSubtitle(String subtitle) {
    	this.subtitle = subtitle;
    }
    
    public void setVersion(int version) {
    	this.version = version;
    }
    
    public void setServerURL(String serverURL) {
    	this.serverURL = serverURL;
    }
    
    public void setFTB(String ftb) {
    	this.ftb = ftb;
    }
    
    public void setFtbDir(String ftbDir) {
    	this.ftbDir = ftbDir;
    }
        
    public String getSubtitle() {
    	/*if (subtitle == "") {
    		return null;
    	}*/
    	return subtitle;
    }
    
    
    
    
    /**
     * Construct a configuration.
     * 
     * @param id id
     * @param name name
     * @param subtitle of the modpack
     * @param int version of the modpack
     * @param serverURL of the server software
     * @param String isFTB
     * @param String iconaddress
     * @param String ftbDir
     * @param customBasePath base path to use
     */
    public Configuration(String id, String name, String subtitle, int version, String serverURL, String ftb, String iconaddress, String ftbDir, File customBasePath, URL updateUrl) {
        if (!id.matches("^[A-Za-z0-9\\-]+{1,64}$")) {
            throw new IllegalArgumentException("Invalid configuration name");
        }
        this.id = id;
        setName(name);
        setCustomBasePath(customBasePath);
        setUpdateUrl(updateUrl);
        
        // Set the logo
        File dir = new File(customBasePath, "Logo");
        this.logo = Toolkit.getDefaultToolkit().createImage(dir.getPath() + File.separator + logo );
        
        // FTB Compat
        setSubtitle(subtitle);
        setVersion(version);
        setServerURL(serverURL);
        setFTB(ftb);
        setFtbDir(ftbDir);
        setIcon(iconaddress);
    }

    /**
     * Get the ID.
     * 
     * @return ID
     */
    public String getId() {
        return id;
    }

    
    
    /**
     * Get the name.
     * 
     * @return name
     */
    public String getTitle() {
        String title;
    	if (version == 0) {
    		title = name;
    	}
        else {
        	title = name + " | " + version;
        }
        
        return title;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getFtbDir() {
    	return ftbDir;
    }

    /**
     * Set the name.
     * 
     * @param name name
     */
    public void setName(String name) {
        if (!name.matches("^.{1,32}$")) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;
    }
    
    /**
     * Returns whether the default base path is used.
     * 
     * @return true if the default path is used
     */
    public boolean isUsingDefaultPath() {
        return customBasePath == null && appDir == null;
    }

    /**
     * Get the custom base path. A custom base path, if set, is the base
     * directory that is used, otherwise the application directory field
     * is used instead.
     * 
     * @return path or null
     */
    
    public File getCustomBasePath() {
        return customBasePath;
    }

    /**
     * Set the custom base path. A custom base path, if set, is the base
     * directory that is used, otherwise the application directory field
     * is used instead.
     * 
     * @param customBasePath path or null
     */
    public void setCustomBasePath(File customBasePath) {
        this.customBasePath = customBasePath;
    }

    /**
     * Get the data directory name.
     * 
     * @see #setCustomBasePath(File) can override
     * @return name or null for default
     */
    public String getAppDir() {
        return appDir;
    }

    /**
     * Set the data directory name.
     * 
     * @see #setCustomBasePath(File) can override
     * @param appDir name
     */
    public void setAppDir(String appDir) {
        if (appDir != null && !appDir.matches("^[A-Za-z0-9\\-_]+{1,32}$")) {
            throw new IllegalArgumentException("Invalid data directory name");
        }
        this.appDir = appDir;
    }

    /**
     * Get the update URL.
     * 
     * @return url or null for default
     */
    public URL getUpdateUrl() {
        return updateUrl;
    }

    /**
     * Set the update URL.
     * 
     * @param updateUrl url or null for default
     */
    public void setUpdateUrl(URL updateUrl) {
        this.updateUrl = updateUrl;
    }
    
    /**
     * Get whether this configuration is built-in.
     * 
     * @return built-in status
     */
    public boolean isBuiltIn() {
        return builtIn;
    }

    /**
     * Set whether this configuration is built-in.
     * 
     * @param builtIn built-in status
     */
    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    /**
     * Get the last active JAR.
     * 
     * @return JAR name or null
     */
    public String getLastActiveJar() {
        return lastActiveJar;
    }

    /**
     * Set the last active jar.
     * 
     * @param lastActiveJar JAR name or null
     */
    public void setLastActiveJar(String lastActiveJar) {
        this.lastActiveJar = lastActiveJar;
    }

    /**
     * Get the settings.
     * 
     * @return settings
     */
    public SettingsList getSettings() {
        return settings;
    }

    /**
     * Set settings.
     * 
     * @param settings settings
     */
    public void setSettings(SettingsList settings) {
        this.settings = settings;
    }

    /**
     * Shortcut method to get the base working directory.
     * 
     * @return directory
     */
    public File getBaseDir() {
        File path;
        if (getCustomBasePath() != null) {
            return getCustomBasePath();
        } else if (getAppDir() == null) {
            path = Launcher.getAppDataDir();
        } else {
            path = Launcher.getAppDataDir(getAppDir());
        }
        
        path.mkdirs();
        return path;
    }

    /**
     * Shortcut method to get the directory that Minecraft will actually use.
     * 
     * @return directory
     */
    public File getMinecraftDir() {
        File path;
        if (getCustomBasePath() != null) {
            return Launcher.toMinecraftDir(getCustomBasePath());
        } else if (getAppDir() == null) {
            path = Launcher.getOfficialDataDir();
        } else {
            path = Launcher.toMinecraftDir(getBaseDir());
        }
        
        path.mkdirs();
        return path;
    }

    /**
     * Get a list of JARs.
     * 
     * @return list of jars
     */
    public List<MinecraftJar> getJars() {
        File base = new File(getMinecraftDir(), "bin");
        List<MinecraftJar> jars = new ArrayList<MinecraftJar>();
        jars.add(new MinecraftJar(new File(base, "minecraft.jar")));
        File[] files = base.listFiles();
        if (files == null) {
            return jars;
        }
        for (File f : files) {
            String name = f.getName();
            
            if (name.matches("^[^\\/:;]+\\.jar$") && !name.equalsIgnoreCase("jinput.jar")
                    && !name.equalsIgnoreCase("lwjgl.jar")
                    && !name.equalsIgnoreCase("lwjgl_util.jar")
                    && !name.equalsIgnoreCase("minecraft.jar")) {
                jars.add(new MinecraftJar(f));
            }
        }
        return jars;
    }

    /**
     * Get the addons profile.
     * 
     * @param activeJar jar
     * @return addons profile
     */
    public AddonsProfile getAddonsProfile(String activeJar) {
        return new AddonsProfile(new File(getMinecraftDir(), "addons/" + activeJar));
    }

    /**
     * Get the icon for the profile. May return null.
     * 
     * @return icon or null
     */
    public String getIcon() {
    	return iconAddress;
    }
    
    /**
     * Set the icon location
     * @param iconAddress
     */
    public void setIcon(String iconAddress) {
    	this.iconAddress = iconAddress;
    }
    
    public Image getLogo() {
    	return logo;
    }
    
    /**
     * Try to load an icon from the JAR.
     * 
     * @param path path
     * @return this object
     */
    public Configuration loadIcon(String path) {
        InputStream in = Launcher.class.getResourceAsStream(path);
        
        if (in != null) {
            try {
                cachedIcon = ImageIO.read(in);
            } catch (IOException e) {
                logger.warning("Failed to load icon at " + path);
            }
        }
        
        return this;
    }

    public Map<String, String> getMPServers() throws FileNotFoundException, IOException {
        File file = new File(getMinecraftDir(), "servers.dat");
        Map<String, String> retn = new HashMap<String, String>();

        if (file.exists()) {
            NBTInputStream nbt = new NBTInputStream(new FileInputStream(file), false);
            Tag tag = nbt.readTag();
            ListTag<?> servers = (ListTag<?>) ((CompoundMap) tag.getValue()).get("servers");


            for (Object val : servers.getValue()) {
                CompoundMap server = ((CompoundTag) val).getValue();
                String name = (String) server.get("name").getValue();
                String ip = (String) server.get("ip").getValue();

                retn.put(name, ip);
            }
            nbt.close();
        }
        return retn;
    }
    
}
