package com.ffnmaster.mclauncher;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.ffnmaster.mclauncher.modpack.Pack;

public class ModPacksCellRenderer implements ListCellRenderer {
	
	private static final int PAD = 5;
	private static BufferedImage defaultIcon;
	private JLabel logo;
	
    static {
        try {
        	InputStream in = Launcher.class
                    .getResourceAsStream("/resources/config_icon.png");
            if (in != null) {
                defaultIcon = ImageIO.read(in);
            }
        } catch (IOException e) {
        }
    }
	
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value,
	        
    	int index, final boolean isSelected, boolean cellHasFocus) {
        final Pack pack = (Pack) value;
		
        JIconPanel panel = new JIconPanel(pack.getIcon());
		panel.setLayout(new GridLayout(2, 1, 0, 1));
		panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		panel.setBorder(BorderFactory.createEmptyBorder(PAD, PAD * 2 + 32, PAD, PAD));
		
		JLabel titleLabel = new JLabel();
		titleLabel.setText(pack.getTitle());
        titleLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		Font font = titleLabel.getFont();
		font = font.deriveFont((float) (font.getSize() * 1.3)).deriveFont(Font.BOLD);
		titleLabel.setFont(font);
		
		panel.add(titleLabel);
		
		
		JLabel infoLabel = new JLabel();
				
		infoLabel.setText(pack.getAuthor());
		Color color = isSelected ? list.getSelectionForeground() : list.getForeground();
		infoLabel.setForeground(color);
		panel.add(infoLabel);
		
		return panel;
	}
	
    private static class JIconPanel extends JPanel {
        
        private static final long serialVersionUID = 6455230127195332368L;
        private BufferedImage icon;
        
        public JIconPanel(BufferedImage icon) {
            this.icon = icon;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension dim = getPreferredSize();
            if (icon != null) {
                g.drawImage(icon, PAD, (int) ((dim.getHeight() - 32) / 2), null);    
            } else if (defaultIcon != null) {
                g.drawImage(defaultIcon, PAD, (int) ((dim.getHeight() - 32) / 2), null);    
            }
        }
        
    }
}
