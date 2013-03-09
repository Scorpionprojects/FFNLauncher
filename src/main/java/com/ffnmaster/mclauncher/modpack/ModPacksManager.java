package com.ffnmaster.mclauncher.modpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.ffnmaster.mclauncher.config.Configuration;

public class ModPacksManager implements Iterable<Configuration>, TableModel, ListModel {
	
	private Map<String, Pack> modpacks = new HashMap<String, Pack>();
	private List<Pack> modList = new ArrayList<Pack>();
	private EventListenerList listenerList = new EventListenerList();
	private Configuration defaultmodpack;
	
	/**
	 * Get a Modpack
	 * @param id
	 * @return Modpack or null
	 */
	public Pack get(String id) {
		return modpacks.get(id);
	}
	
	/**
	 * Register a modpack (GBH)
	 */
	public void register(Pack pack) {
		int index = modList.indexOf(pack);
		
		if(index == -1) {
			modList.add(pack);
			fireTableChanged(new TableModelEvent(this, modList.size() -1));
			fireListChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,
					modList.size() -1, modList.size() -1));
		} else {
			fireTableChanged(new TableModelEvent(this, index));
			fireListChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,
					0, modList.size() -1));
		}
	}
	
	/**
	 * Get the map of modpacks
	 * @return Modpacks map
	 */
	public Map<String, Pack> getModPack() {
		return modpacks;
	}
	
	/**
	 * Get a modpack at a specific index
	 * @param i index
	 * @return modpacks
	 */
	public Pack getModpackAt(int i) {
		return modList.get(i);
	}
	
	/**
	 * Get iterator
	 */
	/*
	public Iterator<Pack> iterator() {
		return modpacks.values().iterator();
	}*/
	
	@Override
	public int getRowCount() {
		return modpacks.size();
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Name";
		case 1:
			return "Path";
		default:
			return null;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			return null;
		}
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Pack pack = modList.get(rowIndex);
		if (pack == null) {
			return null;
		}
		switch(columnIndex) {
		case 0:
			return pack.getName();
		default:
			return null;
		}
	}

	private void fireTableChanged(final TableModelEvent event) {
		final Object[] listeners = listenerList.getListenerList();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i = listeners.length -2; i >= 0; i -=2) {
					if(listeners[i] == TableModelListener.class) {
						((TableModelListener) listeners[i + 1]).tableChanged(event);
					}
				}
			}
		});
	}
	
	private void fireListChanged(final ListDataEvent event) {
		final Object[] listeners = listenerList.getListenerList();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i = listeners.length -2; i >= 0; i -= 2) {
					if (listeners[i] == ListDataListener.class) {
						((ListDataListener) listeners[i+1]).contentsChanged(event);
					}
				}
			}
		});
	}
	
	@Override
	public int getSize() {
		return modpacks.size();
	}
	
	@Override
	public Object getElementAt(int index) {
		return modpacks.get(index);
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}
	
	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Configuration> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
