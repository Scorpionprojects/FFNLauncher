/* TO DO:
Downgrader inbouwen. Groot deel al gemaakt

*/
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import com.ffnmaster.mclauncher.config.Configuration;
import com.ffnmaster.mclauncher.config.Def;
import com.ffnmaster.mclauncher.config.LauncherOptions;
import com.ffnmaster.mclauncher.config.ServerHotListManager;
import com.ffnmaster.mclauncher.util.UIUtil;
import com.ffnmaster.mclauncher.modpack.ModPackParser;
import com.ffnmaster.mclauncher.modpack.ModPacksCellRenderer;

/**
 * Main launcher GUI frame.
 * 
 * @author sk89q
 */
public class LauncherFrame extends JFrame {

    private static final long serialVersionUID = 4122023031876609883L;
    private static final int PAD = 12;
    private boolean allowOfflineName = true;
    private JList configurationList;
    private JList modPackList;
    private JComboBox jarCombo;
    private JComboBox userText;
    private JTextField passText;
    private JTextField serverText;
    private JCheckBox rememberPass;
    private JCheckBox forceUpdateCheck;
    private JCheckBox playOfflineCheck;
    private JCheckBox showConsoleCheck;
    private JCheckBox autoConnectCheck;
    private String autoConnect;
    private LinkButton expandBtn;
    private JButton playBtn;
    private LauncherOptions options;
    private ModPackParser parser;
    private TaskWorker worker = new TaskWorker();
    

    /**
     * Construct the launcher.
     */
    public LauncherFrame() {
        setTitle("FFNLauncher v" + Launcher.VERSION);
        setSize(575, 500);
        setResizable(false);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        try {
            InputStream in = Launcher.class
                    .getResourceAsStream("/resources/newicon.png");
            if (in != null) {
                setIconImage(ImageIO.read(in));
            }
        } catch (IOException e) {
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        options = Launcher.getInstance().getOptions();
        parser = Launcher.getInstance().getModPacks();

        buildUI();
        
        setLocationRelativeTo(null);

        // Setup
        setConfiguration(options.getStartupConfiguration());
        populateIdentities();
        setLastUsername();

        if (options.getSettings().getBool(Def.LAUNCHER_ALWAYS_MORE_OPTIONS,
                false)) {
            expandBtn.doClick();
        }

        // Focus initial item
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (getInputUsername().length() > 0
                        && getInputPassword().length() > 0) {
                    playBtn.requestFocusInWindow();
                } else if (getInputUsername().length() > 0) {
                    passText.requestFocusInWindow();
                } else {
                    userText.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Sets the configuration to use.
     * 
     * @param configuration
     *            configuration
     */
    public void setConfiguration(Configuration configuration) {
        if (!configuration.getBaseDir().isDirectory()) {
            UIUtil.showError(
                    this,
                    "Misconfigured configuration",
                    "The last selected configuration is broken. Switching to the default...");
            configuration = options.getConfigurations().getDefault();
        }
        
        ListModel model = configurationList.getModel();
        if (configurationList.getSelectedValue() != configuration) {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i) == configuration) {
                    configurationList.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        populateJarEntries();
        setLastJar();

        try {
            for (Map.Entry<String, String> entry : configuration.getMPServers().entrySet()) {
                options.getServers().register(entry.getKey(), entry.getValue(), false);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the currently selected workspace.
     * 
     * @return workspace
     */
    public Configuration getWorkspace() {
        Configuration configuration = (Configuration) configurationList.getSelectedValue();
        
        // Switch to default if the current one is broken
        if (configuration == null || !configuration.getBaseDir().isDirectory()) {
            configuration = options.getConfigurations().getDefault();
            setConfiguration(configuration);
        }
        
        return configuration;
    }

    /**
     * Get the entered username.
     * 
     * @return username
     */
    public String getInputUsername() {
        Object selectedName = userText.getSelectedItem();
        return selectedName != null ? selectedName.toString() : "";
    }

    /**
     * Get the entered password.
     * 
     * @return password
     */
    public String getInputPassword() {
        return passText.getText();
    }

    /**
     * Get the currently active JAR.
     * 
     * @return active JAR name
     */
    public String getActiveJar() {
        Object o = jarCombo.getSelectedItem();
        if (o == null) {
            return "minecraft.jar";
        }
        if (o instanceof DefaultVersion) {
            return "minecraft.jar";
        }
        return ((MinecraftJar) o).getName();
    }

    /**
     * Set a username and password.
     * 
     * @param username
     *            username
     * @param password
     *            password, or null to not change the password
     */
    public void setLogin(String username, String password) {
        ((JTextComponent) userText.getEditor().getEditorComponent())
                .setText(username);
        if (passText != null) {
            passText.setText(password);
        }
    }

    /**
     * Set an address to autoconnect to.
     * 
     * @param address
     *            address of server, in host:port or host format
     */
    public void setAutoConnect(String address) {
        this.autoConnect = address;
         
        if (address == null) {
            autoConnectCheck.setSelected(false);
            autoConnectCheck.setVisible(true);
        } else {
            autoConnectCheck.setText("Auto-connect to '" + address + "'");
            autoConnectCheck.setSelected(true);
            autoConnectCheck.setVisible(true);
        }
    }

    /**
     * Set whether the console should be shown.
     * 
     * @param show true to show the console
     */
    public void setShowConsole(boolean show) {
        if (show) {
            expandBtn.doClick();
        }
        showConsoleCheck.setSelected(show);
    }

    /**
     * Open the options window.
     * 
     * @return dialog
     */
    private OptionsDialog openOptions() {
        return openOptions(0);
    }

    /**
     * Open the options window.
     * 
     * @param index
     *            index of the tab to start at
     * @return dialog
     */
    private OptionsDialog openOptions(int index) {
        OptionsDialog dialog = new OptionsDialog(this, getWorkspace(), options, index);
        dialog.setVisible(true);
        return dialog;
    }

    /**
     * Open the addons management window.
     * 
     * @return dialog
     */
    private AddonManagerDialog openAddons() {
        AddonManagerDialog dialog = new AddonManagerDialog(this, getWorkspace(), getActiveJar());
        dialog.setVisible(true);
        return dialog;
    }
	
    
    private void showModPacks(JLayeredPane modPacksPanel) {
        final LauncherFrame self = this;
        //modPacksPanel.setLayout(new NewsLayoutManager());
        modPacksPanel.setBorder(BorderFactory.createEmptyBorder(PAD, 0, PAD, PAD));
        JEditorPane newsView = new JEditorPane();
        newsView.setEditable(true);
        
        /*newsView.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    UIUtil.openURL(e.getURL(), self);
                }
            }
        }); */
        JScrollPane newsScroll = new JScrollPane(newsView);
        modPacksPanel.add(newsScroll, new Integer(1));
        JProgressBar newsProgress = new JProgressBar();
        newsProgress.setIndeterminate(true);
        modPacksPanel.add(newsProgress, new Integer(2));
        NewsFetcher.update(newsView, newsProgress);
    }

    /**
     * Build the UI.
     */
    private void buildUI() {
        final LauncherFrame self = this;
        setLayout(new BorderLayout(0, 0));
        boolean hidenews = options.getSettings().getBool(Def.LAUNCHER_HIDE_NEWS, false);
        allowOfflineName = options.getSettings().getBool(
                Def.LAUNCHER_ALLOW_OFFLINE_NAME, true);
        
        if (!hidenews) {
            if (options.getSettings().getBool(Def.LAUNCHER_NO_NEWS, false)) {
                final JLayeredPane newsPanel = new JLayeredPane();
                
                newsPanel.setBorder(new CompoundBorder(BorderFactory
                        .createEmptyBorder(PAD, 0, PAD, PAD), new CompoundBorder(
                        BorderFactory.createEtchedBorder(), BorderFactory
                                .createEmptyBorder(4, 4, 4, 4))));
                newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
                
                final JButton showNews = new JButton("Show news");
                showNews.setAlignmentX(Component.CENTER_ALIGNMENT);
                showNews.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showNews.setVisible(false);
                        showModPacks(newsPanel);
                    }
                });
                
                // Center the button vertically.
                newsPanel.add(new Box.Filler(new Dimension(0,0), 
                        new Dimension(0,0), new Dimension(1000,1000)));
                newsPanel.add(showNews);
                newsPanel.add(new Box.Filler(new Dimension(0,0), 
                        new Dimension(0,0), new Dimension(1000,1000)));
                
                add(newsPanel, BorderLayout.CENTER);
            } else {
                JLayeredPane newsPanel = new JLayeredPane();
                showModPacks(newsPanel);
                add(newsPanel, BorderLayout.CENTER);
            }
        }
        
        JPanel rightPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel middlePanel = new JPanel(new GridLayout(4,4,4,4));
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        //middlePanel.setLayout(new BorderLayout());
        
        //add(leftPanel, BorderLayout.LINE_START);
        add(leftPanel, BorderLayout.WEST);
        add(middlePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
   

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 3, 0));
        playBtn = new JButton("Play");
        JButton modpacksBtn = new JButton("Modpacks");
        final JButton optionsBtn = new JButton("Options...");
        JButton addonsBtn = new JButton("Addons...");
        buttonsPanel.add(playBtn);
        buttonsPanel.add(addonsBtn);
        buttonsPanel.add(optionsBtn);
        //buttonsPanel.add(modpacksBtn);
        
        JButton installBtn = new JButton(">");
        JButton removeBtn = new JButton("<");
	    installBtn.setPreferredSize(new Dimension(5,5));
	    removeBtn.setPreferredSize(new Dimension(5,5));
        
        middlePanel.add(installBtn, BorderLayout.WEST);
        middlePanel.add(removeBtn, BorderLayout.WEST);
        
        JPanel root = new JPanel();
        root.setBorder(BorderFactory.createEmptyBorder(0, PAD, PAD, PAD));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(createLoginPanel());
        root.add(buttonsPanel);
        leftPanel.add(root, BorderLayout.SOUTH);
        leftPanel.add(middlePanel, BorderLayout.EAST);

        
        JPanel modPacksPanel = new JPanel();
        modPacksPanel.setLayout(new BorderLayout(0,0));
        modPacksPanel.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
        modPackList = new JList(parser.getModpacks());
        modPackList.setCellRenderer(new ModPacksCellRenderer());
        modPackList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane modPacksScroll = new JScrollPane(modPackList);
        modPacksPanel.add(modPacksScroll, BorderLayout.WEST);
        leftPanel.add(modPacksPanel, BorderLayout.WEST);

        JPanel configurationsPanel = new JPanel();
        configurationsPanel.setLayout(new BorderLayout(0, 0));
        configurationsPanel.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
        configurationList = new JList(options.getConfigurations());
        configurationList.setCellRenderer(new ConfigurationCellRenderer());
        configurationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane configScroll = new JScrollPane(configurationList);
        configurationsPanel.add(configScroll, BorderLayout.CENTER);
        rightPanel.add(configurationsPanel, BorderLayout.EAST);
        
        
        // Add listener
        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseServer();
            }
        });

        playBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupServerHotListMenu(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        
        configurationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setConfiguration((Configuration) ((JList) e.getSource()).getSelectedValue());
            }
        });

        optionsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOptions();
            }
        });
		
        addonsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddons();
            }
        });
    }

    /**
     * Create the login panel.
     * 
     * @return panel
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        GridBagConstraints fieldD = new GridBagConstraints();
        fieldD.fill = GridBagConstraints.HORIZONTAL;
        fieldD.weightx = 0.8;
        fieldD.gridwidth = GridBagConstraints.RELATIVE;
        fieldD.insets = new Insets(2, 1, 2, 1);
       
        GridBagConstraints fieldC = new GridBagConstraints();
        fieldC.fill = GridBagConstraints.HORIZONTAL;
        fieldC.weightx = 1.0;
        fieldC.gridwidth = GridBagConstraints.REMAINDER;
        fieldC.insets = new Insets(2, 1, 2, 1);

        GridBagConstraints labelC = (GridBagConstraints) fieldC.clone();
        labelC.weightx = 0.0;
        labelC.gridwidth = 1;
        labelC.insets = new Insets(1, 1, 1, 10);

        GridBagConstraints checkboxC = (GridBagConstraints) fieldC.clone();
        checkboxC.insets = new Insets(5, 2, 1, 2);

        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        final JLabel jarLabel = new JLabel("Active JAR:", SwingConstants.LEFT);
        JLabel userLabel = new JLabel("Username:", SwingConstants.LEFT);
        JLabel passLabel = new JLabel("Password:", SwingConstants.LEFT);
        JLabel serverLabel = new JLabel("Server: ", SwingConstants.LEFT);
        
        autoConnectCheck = new JCheckBox("");
        autoConnectCheck.setBorder(null);

        jarCombo = new JComboBox();
        userText = new JComboBox();
        serverText = new JTextField(30);
        userText.setEditable(true);
        passText = new JPasswordField(20);
        jarLabel.setLabelFor(jarCombo);
        userLabel.setLabelFor(userText);
        passLabel.setLabelFor(passText);
        serverLabel.setLabelFor(serverText);
        layout.setConstraints(jarCombo, fieldC);
        layout.setConstraints(userText, fieldC);
        layout.setConstraints(passText, fieldC);
        layout.setConstraints(serverText, fieldC);

        rememberPass = new JCheckBox("Remember my password");
        rememberPass.setBorder(null);

        playOfflineCheck = new JCheckBox("Play in offline mode");
        playOfflineCheck.setBorder(null);		
		
        forceUpdateCheck = new JCheckBox("Force a game update");
        forceUpdateCheck.setBorder(null);

        showConsoleCheck = new JCheckBox("Launch with console");
        showConsoleCheck.setBorder(null);

        expandBtn = new LinkButton("More options...");
        final JPanel expandContainer = new JPanel();
        expandContainer.setLayout(new BoxLayout(expandContainer,
                BoxLayout.X_AXIS));
        expandContainer.setBorder(null);
        expandContainer.add(expandBtn);
        expandContainer.add(Box.createHorizontalGlue());

        panel.add(jarLabel, labelC);
        panel.add(jarCombo, fieldC);
        panel.add(userLabel, labelC);
        panel.add(userText, fieldC);
        panel.add(passLabel, labelC);
        panel.add(passText, fieldC);
        panel.add(serverLabel, labelC);
        panel.add(serverText, fieldD);
        panel.add(autoConnectCheck, checkboxC);
        panel.add(rememberPass, checkboxC);
        panel.add(forceUpdateCheck, checkboxC);
        panel.add(playOfflineCheck, checkboxC);
        panel.add(showConsoleCheck, checkboxC);
        panel.add(expandContainer, checkboxC);

        autoConnectCheck.setVisible(true);
        jarLabel.setVisible(false);
        jarCombo.setVisible(false);
        forceUpdateCheck.setVisible(false);
        playOfflineCheck.setVisible(false);
        showConsoleCheck.setVisible(false);

        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavedPassword();
            }
        });

        userText.getEditor().getEditorComponent()
                .addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        Object text = userText.getSelectedItem();
                        if (text == null)
                            return;
                        if (options.getSavedPassword((String) text) != null) {
                            passText.setText("");
                            rememberPass.setSelected(false);
                        }

                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        	parseServer();
                        }
                    }
                });

        userText.getEditor().getEditorComponent()
                .addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    private void maybeShowPopup(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupIdentityMenu(e.getComponent(), e.getX(),
                                    e.getY());
                        }
                    }
                });
        

        
        passText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	parseServer();
                }
            }
        });
        
        serverText.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        			String serverAddress = serverText.getText();
        			launch(serverAddress);
        		}
        	}
        });
        
        expandBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandContainer.setVisible(false);
                jarLabel.setVisible(true);
                jarCombo.setVisible(true);
                forceUpdateCheck.setVisible(true);
                playOfflineCheck.setVisible(allowOfflineName);
                showConsoleCheck.setVisible(true);
                // registerAccount.setVisible(true);
            }
        });

        return panel;
    }

    /**
     * Pop the menu that appears when right clicking the username box.
     * 
     * @param component
     *            component to display from
     * @param x
     *            top left x
     * @param y
     *            top left y
     */
    private void popupIdentityMenu(Component component, int x, int y) {
        final LauncherFrame self = this;

        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;

        final String username = getInputUsername();

        if (options.getSavedPassword(username) != null) {
            menuItem = new JMenuItem("Forget '" + username
                    + "' and its password");
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    options.forgetIdentity(username);
                    if (username.equals(options.getLastUsername())) {
                        options.setLastUsername(null);
                    }
                    populateIdentities();
                    options.save();
                }
            });
            popup.add(menuItem);
        }

        menuItem = new JMenuItem("Forget all saved passwords...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane
                        .showConfirmDialog(
                                self,
                                "Are you sure that you want to forget all saved passwords?",
                                "Forget passwords", JOptionPane.YES_NO_OPTION) == 0) {
                    options.forgetAllIdentities();
                    options.setLastUsername(null);
                    populateIdentities();
                    options.save();
                }
            }
        });
        popup.add(menuItem);

        popup.show(component, x, y);
    }

    /**
     * Open the server hot list menu.
     * 
     * @param component
     *            component to open from
     */
    private void popupServerHotListMenu(Component component, int x, int y) {
        final ServerHotListManager servers = options.getServers();
        Set<String> names = servers.getServerNames();

        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;

        for (final String name : names) {
            menuItem = new JMenuItem("Connect to " + name);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    launch(servers.get(name));
                }
            });
            popup.add(menuItem);
        }

        if (names.size() == 0) {
            menuItem = new JMenuItem("No servers in the hot list.");
            menuItem.setEnabled(false);
            popup.add(menuItem);
        }

        popup.show(component, x, y);
    }

    /**
     * Populate the list of JAR versions to use.
     */
    private void populateJarEntries() {
        jarCombo.removeAllItems();

        jarCombo.addItem(new DefaultVersion());

        for (MinecraftJar jar : getWorkspace().getJars()) {
            jarCombo.addItem(jar);
        }
    }

    /**
     * Set the JAR field to the last JAR used.
     */
    private void setLastJar() {
        String lastJar = getWorkspace().getLastActiveJar();
        if (lastJar != null) {
            // TODO: This is a horrible hack
            ComboBoxModel model = jarCombo.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                Object item = model.getElementAt(i);
                if (item instanceof MinecraftJar && ((MinecraftJar) item).getName().equals(lastJar)) {
                    model.setSelectedItem(item);
                }
            }
        }
    }

    /**
     * Update the drop down list of saved user/pass combinations.
     */
    private void populateIdentities() {
        Object selectedName = userText.getSelectedItem();
        String password = passText.getText();
        boolean remember = rememberPass.isSelected();

        userText.removeAllItems();

        for (String name : options.getSavedUsernames()) {
            userText.addItem(name);
        }

        userText.setSelectedItem(selectedName);
        passText.setText(password);
        rememberPass.setSelected(remember);
    }

    /**
     * Set the username field to the last saved username.
     */
    private void setLastUsername() {
        if (options.getLastUsername() != null) {
            userText.setSelectedItem(options.getLastUsername());
        }

        loadSavedPassword();
    }
    

    /**
     * Load the saved password for the current entered username.
     */
    private void loadSavedPassword() {
        LauncherOptions options = Launcher.getInstance().getOptions();

        Object selected = userText.getSelectedItem();
        if (selected != null) {
            String password = options.getSavedPassword(selected.toString());
            if (password != null) {
                passText.setText(password);
                rememberPass.setSelected(true);
            }
        }
    }
    
    /**
     * Send server address to launcher, independant of mouse/keyboard position
     */
    public void parseServer() {
    	// Server Address String
    	String serverAddress = serverText.getText();
    	boolean yes = autoConnectCheck.isSelected();
    	
        if (yes == false) {
            launch();
        } else {
            launch(serverAddress);
        }
    }
    
    /**
     * Launch the game.
     */
    public void launch() {
        launch(null, false);
    }

    public void launch(String autoConnect) {
        launch(autoConnect, false);
    }
    /**
     * Launch the game.
     * 
     * @param autoConnect address to try auto-connecting to
     * @param test set test mode
     */
    public void launch(String autoConnect, boolean test) {
        if (worker.isAlive())
            return;

        Object selectedName = userText.getSelectedItem();

        if (selectedName == null) {
            JOptionPane.showMessageDialog(this, "A username must be entered.",
                    "No username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!playOfflineCheck.isSelected() && passText.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "A password must be entered.",
                    "No password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = selectedName.toString();
        String password = passText.getText();
        boolean remember = rememberPass.isSelected();
        String jar = jarCombo.getSelectedItem() instanceof MinecraftJar ? ((MinecraftJar) jarCombo
                .getSelectedItem()).getName() : null;

        // Save the identity
        if (!playOfflineCheck.isSelected()) {
            if (remember) {
                options.saveIdentity(username, password);
                options.setLastUsername(username);
            } else {
                options.saveIdentity(username, password);
                options.setLastUsername(username);
            }
        }
        options.setLastConfigName(getWorkspace().getId());
        options.save();

        // Save options
        getWorkspace().setLastActiveJar(jar);
        options.save();

        // Want to update the GUI
        populateIdentities();

        // JFrame frame, Configuration configuration, String username, String password, String jar, String autoConnect
        LaunchTask task = new LaunchTask(this, getWorkspace(), username, password, jar, autoConnect);
        task.setForceUpdate(forceUpdateCheck.isSelected());
        task.setPlayOffline(playOfflineCheck.isSelected() || (test && options.getSettings().getBool(Def.FAST_TEST, false)));
        task.setShowConsole(showConsoleCheck.isSelected());
        if (autoConnect != null) {
            task.setAutoConnect(autoConnect);
        } else if (autoConnectCheck.isSelected() && this.autoConnect != null) {
            task.setAutoConnect(this.autoConnect);
        }

        worker = Task.startWorker(this, task);
    }
}
