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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ffnmaster.mclauncher.modpack.ModPackListener;
import com.ffnmaster.mclauncher.modpack.Pack;

class ModPackListModelAdapter extends AbstractListModel implements ModPackListener {
	private Map<Integer, Integer> filteredPacks;

	public ModPackListModelAdapter() {
		super();
		filteredPacks = new HashMap<Integer, Integer>();
	}

	public void filter(String origin, String mcVersion, String availability, String query) {
		filteredPacks.clear();
		int counter = 0;
		for(int i = 0; i < Pack.size(); ++i) {
			Pack pack = Pack.getPack(i);
			if(originCheck(pack, origin) && mcVersionCheck(pack, mcVersion) && availabilityCheck(pack, availability) && textSearch(pack, query)) {
				filteredPacks.put(counter, i);
				counter++;
			}
		}
		if(counter + 1 == Pack.size()) {
			fireIntervalRemoved(this, 0, Pack.size());
			fireIntervalAdded(this, 0, Pack.size());
			filteredPacks.clear();
		}
		else {
			fireIntervalRemoved(this, 0, Pack.size());
			fireIntervalAdded(this, 0, filteredPacks.size());
		}
	}

	public int getSize() {
		return (!filteredPacks.isEmpty()) ? filteredPacks.size() : Pack.size();
	}

	public Object getElementAt(int index) {
		return (!filteredPacks.isEmpty()) ? Pack.getPack(filteredPacks.get(index)) : Pack.getPack(index);
	}

	@Override
	public void onModPackAdded(Pack pack) {
		System.out.println("Adding pack " + Pack.size());
		filteredPacks.clear();
		fireIntervalAdded(this, Pack.size() - 1, Pack.size());
	}

	private static boolean availabilityCheck(Pack pack, String availability) {
		return (availability.equalsIgnoreCase("MAIN_ALL")) || (availability.equalsIgnoreCase("FILTER_PUBLIC") && !pack.isPrivatePack()) || (availability.equalsIgnoreCase("FILTER_PRIVATE") && pack.isPrivatePack());
	}

	private static boolean mcVersionCheck(Pack pack, String mcVersion) {
		return (mcVersion.equalsIgnoreCase("MAIN_ALL")) || (mcVersion.equalsIgnoreCase(pack.getMcVersion()));
	}

	private static boolean originCheck(Pack pack, String origin) {
		return (origin.equalsIgnoreCase("MAIN_ALL")) || (origin.equalsIgnoreCase("ftb") && pack.getAuthor().equalsIgnoreCase("the ftb team")) || (origin.equalsIgnoreCase("FILTER_3THPARTY") && !pack.getAuthor().equalsIgnoreCase("the ftb team"));
	}

	private static boolean textSearch(Pack pack, String query) {
		return ((query.isEmpty()) || pack.getName().toLowerCase().contains(query) || pack.getAuthor().toLowerCase().contains(query));
	}
}




class ModPackCellRenderer extends JPanel implements ListCellRenderer {
	private JLabel logo;
	private JTextArea description;

	public ModPackCellRenderer() {
		super();

		logo = new JLabel();
		description = new JTextArea();

		setLayout(null);
		logo.setBounds(6, 6, 42, 42);

		description.setBorder(null);
		description.setEditable(false);
		description.setForeground(Color.white);
		description.setBounds(58, 6, 378, 42);
		description.setBackground(new Color(255, 255, 255, 0));

		add(description);
		add(logo);

		setMinimumSize(new Dimension(420, 55));
		setPreferredSize(new Dimension(420, 55));
	}

	public Component getListCellRendererComponent(
		JList list, Object value, int index, boolean isSelected, boolean cellHasFocus
	) {
		Pack pack = (Pack)value;

		if(cellHasFocus || isSelected) {
			setBackground(UIManager.getColor("control").darker().darker());
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setBackground(UIManager.getColor("control"));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		logo.setIcon(new ImageIcon(pack.getLogo()));
		description.setText(pack.getName() + " (v" + pack.getVersion() + ") Minecraft Version " + pack.getMcVersion() + "\n" + "By " + pack.getAuthor());

		return this;
	}
}

public class NewsLayoutManager {
	// Pack info
	private ModPackListModelAdapter model;
	private static JList packs;
	private static JEditorPane packInfo;
	private static JScrollPane packsScroll;
	public static String origin = "ALL", mcVersion = "ALL", avaliability = "ALL";
	private static JButton server;
	private static JComboBox version;
	
	public NewsLayoutManager() {
		super();
		model = new ModPackListModelAdapter();
		
		packs = new JList(model);
		packs.setCellRenderer(new ModPackCellRenderer());
		
		packs.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					System.out.println("DOUBLE CLICK DEBUG FTW");
				}
			}
		});
		
		packs.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				Pack pack = (Pack)packs.getSelectedValue();
				if(pack != null) {
					String mods = "";
					if(pack.getMods() != null) {
						mods += "<p>This pack contains the following mods by default:</p><ul>";
						for (String name : pack.getMods()) {
							mods += "<li>" + name + "</li>";
						}
						mods += "</ul>";
					}
					File tempDir = new File(Launcher.getLauncherDir(), "ModPacks" + File.separator + pack.getDir());
					packInfo.setText("<html><img src='file:///" + tempDir.getPath() + File.separator + pack.getImageName() +"' width=400 height=200></img> <br>" + pack.getInfo() + mods);
					packInfo.setCaretPosition(0);

					if(Pack.getSelectedPack().getServerUrl().equals("") || Pack.getSelectedPack().getServerUrl() == null) {
						server.setEnabled(false);
					} else {
						server.setEnabled(true);
					}
					String tempVer = Settings.getSettings().getPackVer();
					version.removeAllItems();
					version.addItem("Recommended");
					if(pack.getOldVersions() != null) {
						for(String s : pack.getOldVersions()) {
							version.addItem(s);
						}
						version.setSelectedItem(tempVer);
					}
				}
			}
		});
		
	}
	
	public static int getSelectedModIndex() {
		return packs.getSelectedIndex();
	}
	*/
	
	
    private static final int PROGRESS_WIDTH = 100;
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        throw new UnsupportedOperationException("Can't remove things!");
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
        
        int numComps = parent.getComponentCount();
        for (int i = 0 ; i < numComps ; i++) {
            Component comp = parent.getComponent(i);
            
            if (comp instanceof JProgressBar) {
                Dimension size = comp.getPreferredSize();
                comp.setLocation((parent.getWidth() - PROGRESS_WIDTH) / 2,
                        (int) (parent.getHeight() / 2.0 - size.height / 2.0));
                comp.setSize(PROGRESS_WIDTH,
                        (int) comp.getPreferredSize().height);
            } else {
                comp.setLocation(insets.left, insets.top);
                comp.setSize(maxWidth, maxHeight);
            }
        }
    }
    

}

