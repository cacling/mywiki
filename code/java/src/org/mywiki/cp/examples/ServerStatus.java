package org.mywiki.cp.examples;

import java.util.HashSet;
import java.util.Set;

public class ServerStatus {

	public final Set<String> users = new HashSet<String>();

	public final Set<String> queries = new HashSet<String>();

	public  void addUser(String u) {
		synchronized(users){
			users.add(u);
		}
	}

	public void addQuery(String q) {
		synchronized(queries){
			queries.add(q);
		}
	}

	public void removeUser(String u) {
		synchronized(users){
			users.remove(u);
		}
	}

	public void removeQuery(String q) {
		synchronized(queries){
			queries.remove(q);
		}
	}

}
