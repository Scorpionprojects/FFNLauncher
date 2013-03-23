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

package com.ffnmaster.mclauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.ffnmaster.mclauncher.config.Configuration;
import com.ffnmaster.mclauncher.config.ConfigurationsManager;
import com.ffnmaster.mclauncher.modpack.ModPackParser;
import com.ffnmaster.mclauncher.modpack.Pack;
import com.ffnmaster.mclauncher.util.SettingsList;
import com.ffnmaster.mclauncher.util.UIUtil;
import com.ffnmaster.mclauncher.modpack.ModPackInstaller;

/**
 * Dialog for adding or modifying a {@link Configuration}.
 * 
 * @author sk89q
 */
public class ConfigurationDialog extends JDialog {

    private static final int PAD = 12;
    private static final long serialVersionUID = -7347791965966294361L;
    private OptionsDialog optionsDialog;
    private LauncherFrame launcherFrame;
    private ConfigurationsManager configsManager;
    private JButton browseBtn;
    private JButton tempBtn;
    private JTextField nameText;
    private JTextField pathText;
    private JTextField subText;
    private boolean withPack = false;
    private Pack selectedPack;
    private JCheckBox customPathCheck;
    private Configuration configuration;
    private Pack pack;
    private SettingsList settings;
    private List<OptionsPanel> optionsPanels = new ArrayList<OptionsPanel>();
    public static boolean test = false;
    
    
    private JList modPackList;
    private ModPackParser parser;
    
    /**
     * Start editing a given configuration.
     * 
     * @param owner owning dialog
     * @param configsManager configurations manager
     * @param configuration configuration to edit
     */
    public ConfigurationDialog(OptionsDialog owner, ConfigurationsManager configsManager,
            Configuration configuration) {
        super(owner, "Edit Configuration", true);
        this.configuration = configuration;
        this.settings = configuration.getSettings();
        setup(owner, configsManager);
    }
    
    /**
     * Open ConfigsManager with selected Modpack as template
     * @param owner
     * @param configsManager
     * @param pack
     */
    public ConfigurationDialog(LauncherFrame owner, ConfigurationsManager configsManager, Pack pack) {
    	super(owner, "New Version from Template", true);
    	this.settings = new SettingsList();
    	this.pack = pack;
    	this.withPack = true;
    	
    	setup(owner, configsManager, pack);
    }

    /**
     * Start a new configuration.
     * 
     * @param owner owning dialog
     * @param configsManager configurations manager
     */
    public ConfigurationDialog(OptionsDialog owner, ConfigurationsManager configsManager) {
        super(owner, "New Configuration", true);
        this.settings = new SettingsList();
        setup(owner, configsManager);
    }
    
    
    /**
     * Setup.
     * 
     * @param owner owning dialog
     * @param configsManager configurations manager
     */
    private void setup(OptionsDialog owner, ConfigurationsManager configsManager) {
        this.optionsDialog = owner;
        this.configsManager = configsManager;
        
        setResizable(false);
        buildUI();
        pack();
        setSize(400, 500);
        setLocationRelativeTo(owner);

        for (OptionsPanel panel : optionsPanels) {
            panel.copySettingsToFields();
        }

        if (configuration != null) {
            nameText.setText(configuration.getName());
            if (configuration.isBuiltIn()) {
                customPathCheck.setSelected(true);
                pathText.setText(configuration.getBaseDir().getPath());
            } else {
                boolean usingDefault = configuration.isUsingDefaultPath();
                customPathCheck.setSelected(!usingDefault);
                if (!usingDefault) {
                    File f = configuration.getBaseDir();
                    pathText.setText(f != null ? f.getPath() : "");
                }
            }
            
            if (configuration.isBuiltIn()) {
                nameText.setEnabled(false);
                customPathCheck.setEnabled(false);
                pathText.setEnabled(false);
                browseBtn.setEnabled(false);
            }
        }
    }
    
    private void setup(LauncherFrame owner, ConfigurationsManager configsManager, Pack pack) {
        this.launcherFrame = owner;
        this.configsManager = configsManager;
        
        setResizable(false);
        buildUI();
        pack();
        setSize(400, 500);
        setLocationRelativeTo(owner);
        
        for (OptionsPanel panel : optionsPanels) {
            panel.copySettingsToFields();
        }

        if (configuration != null) {
            nameText.setText(configuration.getName());
            subText.setText(configuration.getSubtitle());
            selectedPack = pack;
            if (configuration.isBuiltIn()) {
                customPathCheck.setSelected(true);
                pathText.setText(configuration.getBaseDir().getPath());
            } else {
                boolean usingDefault = configuration.isUsingDefaultPath();
                customPathCheck.setSelected(!usingDefault);
                if (!usingDefault) {
                    File f = configuration.getBaseDir();
                    pathText.setText(f != null ? f.getPath() : "");
                }
            }
            
            if (configuration.isBuiltIn()) {
                nameText.setEnabled(false);
                customPathCheck.setEnabled(false);
                pathText.setEnabled(false);
                browseBtn.setEnabled(false);
            }
        }
    }
    
    /**
     * Adds an option panel to the index.
     * 
     * @param panel panel
     * @return panel
     */
    private <T extends OptionsPanel> T wrap(T panel) {
        optionsPanels.add(panel);
        return panel;
    }
    
    /**
     * Build the UI.
     */
    private void buildUI() {
        final ConfigurationDialog self = this;
        
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(8, 8, 5, 8));
        container.setLayout(new BorderLayout(3, 3));
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pack", buildConfigurationPanel());
        tabs.addTab("Advanced", wrap(new EnvironmentOptionsPanel(settings, true)));
        container.add(tabs, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(70, (int) cancelBtn.getPreferredSize().getHeight()));
        okBtn.setPreferredSize(cancelBtn.getPreferredSize());
        buttonsPanel.add(okBtn);
        buttonsPanel.add(cancelBtn);
        container.add(buttonsPanel, BorderLayout.SOUTH);

        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (complete()) {
                    self.dispose();
                }
            }
        });
        
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.dispose();
            }
        });
        
        add(container, BorderLayout.CENTER);
    }
    
    /**
     * Build the main configuration tab.
     * 
     * @return panel
     */
    private JPanel buildConfigurationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fieldConstraints.insets = new Insets(2, 1, 2, 1);
        
        GridBagConstraints labelConstraints = (GridBagConstraints) fieldConstraints.clone();
        labelConstraints.weightx = 0.0;
        labelConstraints.gridwidth = 1;
        labelConstraints.insets = new Insets(1, 1, 1, 10);
        
        GridBagConstraints fullFieldConstraints = (GridBagConstraints) fieldConstraints.clone();
        fullFieldConstraints.insets = new Insets(5, 2, 1, 2);
        
        if (configuration != null && configuration.isBuiltIn()) {
            panel.add(new JLabel("This is the built-in configuration. You cannot edit."), fullFieldConstraints);
            panel.add(Box.createVerticalStrut(10), fullFieldConstraints);
        }
        
        JLabel nameLabel = new JLabel("Name:");
        panel.add(nameLabel, labelConstraints);
        nameText = new JTextField(30);
        nameLabel.setLabelFor(nameText);
        panel.add(nameText, fieldConstraints);

        JLabel pathLabel = new JLabel("Path:");
        JLabel subtitleLabel = new JLabel("Description:");
        JLabel selectedMP = new JLabel("Selected Template:");
        panel.add(pathLabel, labelConstraints);

        customPathCheck = new JCheckBox("Use a custom path");
        customPathCheck.setBorder(null);
        //panel.add(customPathCheck, fieldConstraints);
        panel.add(Box.createGlue(), labelConstraints);
        JPanel pathPanel = new JPanel();
        JPanel subPanel = new JPanel();
        JPanel selectedMPPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
        selectedMPPanel.setLayout(new BoxLayout(selectedMPPanel, BoxLayout.X_AXIS));
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
        pathText = new JTextField(30);
        subText = new JTextField(30);
        
        JLabel selMP;
        
        if (withPack == true) {
        	selMP = new JLabel(pack.getTitle());
        } else {
        	selMP = new JLabel("No Template Selected");
        }
        
        pathText.setMaximumSize(pathText.getPreferredSize());
        nameLabel.setLabelFor(pathText);
        pathLabel.setLabelFor(subText);
        tempBtn = new JButton("Test Install");
        browseBtn = new JButton("Browse...");
        browseBtn.setPreferredSize(new Dimension(
                browseBtn.getPreferredSize().width,
                pathText.getPreferredSize().height));
        tempBtn.setPreferredSize(new Dimension(
        		tempBtn.getPreferredSize().width,
        		pathText.getPreferredSize().height));
        pathPanel.add(pathText);
        pathPanel.add(Box.createHorizontalStrut(3));
        pathPanel.add(browseBtn);
        pathPanel.add(tempBtn);
        subPanel.add(subText);
        subPanel.add(Box.createHorizontalStrut(3));  
       	selectedMPPanel.add(selMP);
        panel.add(pathPanel, fieldConstraints);
        panel.add(subtitleLabel, labelConstraints);
        panel.add(subPanel, fieldConstraints);
        panel.add(selectedMP, labelConstraints);
        panel.add(selectedMPPanel, fieldConstraints);
        
        panel.add(Box.createVerticalStrut(10), fullFieldConstraints);

 
        browseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPathBrowser();
            }
        });
        
        tempBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
                String pathStr2 = pathText.getText();
                File file = new File(pathStr2);
				try {
					System.out.println("GETLINK" + pack.getLinkDir());
					ModPackInstaller.installFTBTemplate(pack.getUrl(), file, pack.getLinkDir());
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
        	}
        });

        subText.setEnabled(true);
        pathText.setEnabled(true);
        browseBtn.setEnabled(true);
        

		
		
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(panel);
        container.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 10000), new Dimension(0, 10000)));
        UIUtil.removeOpaqueness(container);
        return container;
    }
    
    /**
     * Validate and save.
     * 
     * @return true if successful
     */
    private boolean complete() {
        String name = nameText.getText().trim();
        String subtitle = subText.getText().trim();
        String pathStr = pathText.getText();
        File f = null;
        

            if (name.length() == 0) {
                UIUtil.showError(this, "No name", "A name must be entered.");
                return false;
            }
            
            if (pathStr != null && pathStr.length() == 0) {
                UIUtil.showError(this, "No path", "A path must be entered.");
                return false;
            }
            
            if (subtitle.length() == 0) {
            	subtitle = "User Configuration";
            }
            
            
            if (pathStr != null) {
                f = new File(pathStr);
                if (!f.isDirectory()) {
                    UIUtil.showError(this, "Invalid path", "The path that you entered does not exist or is not a directory.");
                    return false;
                }
            }

        
        for (OptionsPanel panel : optionsPanels) {
            panel.copyFieldsToSettings();
        }
        
        if (configuration == null) { // New configuration
            String id = UUID.randomUUID().toString();
            Configuration config = new Configuration(id, name, subtitle, 0, "", "", "", "", f);
            config.setSettings(settings);
            configsManager.register(config);
            this.configuration = config;
        } else {
            configuration.setName(name);
            configuration.setCustomBasePath(f);
        }
        
        optionsDialog.save(false);
        
        return true;
    }
    
    /**
     * Open the path browser.
     */
    private void openPathBrowser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                return false;
            }

            @Override
            public String getDescription() {
                return "Directories";
            }
        });
        
        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            pathText.setText(chooser.getSelectedFile().getPath());
        }
    }
}
