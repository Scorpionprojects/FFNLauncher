package com.ffnmaster.mclauncher.modpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;

import com.ffnmaster.mclauncher.config.Configuration;

public class ModPacksManager implements Iterable<Configuration>, TableModel, ListModel {
	
	private Map<String, Configuration> modpacks = new HashMap<String, Configuration>();
	private List<Configuration> modList = new ArrayList<Configuration>();
	private EventListenerList listenerList = new EventListenerList();
	private Configuration defaultmodpack;
	
	
	
}
