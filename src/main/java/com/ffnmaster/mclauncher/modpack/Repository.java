package com.ffnmaster.mclauncher.modpack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Repository {
	private Map<String, String> repos = new HashMap<String, String>();
	
	/**
	 * Register a repo
	 * @param name of the repo
	 * @param URL of the repo
	 */
	public void register(String name, String address) {
		repos.put(name, address);
	}
	
	
	/**
	 * Get the address of a repo
	 * @param name of the repo
	 * @return address or null
	 */
	public String get(String name) {
		return repos.get(name);
	}
	
	/**
	 * Get a list of repo names
	 * @return list of the repos
	 */
	public Set<String> getRepoNames() {
		return repos.keySet();
	}
	
	
	
	
	
	
}
