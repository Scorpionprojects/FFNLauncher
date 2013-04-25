package com.ffnmaster.mclauncher.dialog;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.autoupdate.UpdateChecker;

public class UpdateDialog extends JDialog {
	private JLabel messageLbl;
	private JLabel updateLbl;
	private JButton showChangeLog;
	private JButton update;
	private JButton abort;
	
	public UpdateDialog(final UpdateChecker updateChecker) {
		
		setupGui();
		
		showChangeLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Launcher.browse("http://download.sigmacoders.nl/FFNLauncher/changelog.html");
			}
		});
		
		update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				updateChecker.update();
			}
		});
		
		abort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
	}
	
	private void setupGui() {
		//setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/logo_ftb.png")));
		setTitle("Update Available");
		setResizable(false);

		Container panel = getContentPane();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		messageLbl = new JLabel("Version " + UpdateChecker.verString + " " + "is now available");
		JLabel updateLbl = new JLabel("Want to update?");
		showChangeLog = new JButton("Changelog");
		update = new JButton("Yes");
		abort = new JButton("No");

		messageLbl.setHorizontalAlignment(SwingConstants.CENTER);
		updateLbl.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(messageLbl);
		panel.add(showChangeLog);
		panel.add(updateLbl);
		panel.add(update);
		panel.add(abort);

		Spring hSpring;
		Spring columnWidth;

		hSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.WEST, messageLbl, hSpring, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.WEST, updateLbl,  hSpring, SpringLayout.WEST, panel);

		columnWidth = Spring.width(messageLbl);
		columnWidth = Spring.max(columnWidth, Spring.width(showChangeLog));
		columnWidth = Spring.max(columnWidth, Spring.width(updateLbl));

		hSpring = Spring.sum(hSpring, columnWidth);

		layout.putConstraint(SpringLayout.EAST, messageLbl, hSpring, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, updateLbl,  hSpring, SpringLayout.WEST, panel);

		hSpring = Spring.sum(hSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, showChangeLog,  0, SpringLayout.HORIZONTAL_CENTER, panel);
		layout.putConstraint(SpringLayout.EAST,              update,        -5, SpringLayout.HORIZONTAL_CENTER, panel);
		layout.putConstraint(SpringLayout.WEST,              abort,          5, SpringLayout.HORIZONTAL_CENTER, panel);

		Spring vSpring;
		Spring rowHeight;

		vSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.NORTH, messageLbl, vSpring, SpringLayout.NORTH, panel);

		vSpring = Spring.sum(vSpring, Spring.height(messageLbl));
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, showChangeLog, vSpring, SpringLayout.NORTH, panel);

		vSpring = Spring.sum(vSpring, Spring.height(showChangeLog));
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, updateLbl, vSpring, SpringLayout.NORTH, panel);

		vSpring = Spring.sum(vSpring, Spring.height(updateLbl));
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, update, vSpring, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, abort,  vSpring, SpringLayout.NORTH, panel);

		rowHeight = Spring.height(update);
		rowHeight = Spring.max(rowHeight, Spring.height(abort));

		vSpring = Spring.sum(vSpring, rowHeight);
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.SOUTH, panel, vSpring, SpringLayout.NORTH, panel);

		pack();
		setLocationRelativeTo(getOwner());
	}
}
