package com.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigUtil {
	
	public static List<Connection> raftNodes = new ArrayList<Connection>();
	public static List<Connection> proxyNodes = new ArrayList<Connection>();
	public static List<Connection> databaseNodes = new ArrayList<Connection>();
	public static List<Connection> globalNodes = new ArrayList<Connection>();
	
	public ConfigUtil(){
		Scanner scan = null;
		try {
			scan = new Scanner(new File("src/main/resources/config.json"));
		} catch (FileNotFoundException e){
			System.out.println("Cannot find config.json!");
			System.exit(1);
		}

		String cfgStr = "";
		while(scan.hasNext()){
			cfgStr += scan.nextLine();
		}
		JSONObject config = new JSONObject(cfgStr);
		
        JSONArray proxyArr = (JSONArray)config.get("proxyNodes");
        JSONArray raftArr = (JSONArray)config.get("raftNodes");
        JSONArray dbarr = (JSONArray)config.get("databaseNodes");
        JSONArray globalArr = (JSONArray)config.get("globalNodes");

		raftNodes = new ArrayList<Connection>();
		proxyNodes = new ArrayList<Connection>();
		databaseNodes = new ArrayList<Connection>();
		globalNodes = new ArrayList<Connection>();

    	for(int i = 0; i < proxyArr.length(); i++){
			JSONObject obj = proxyArr.getJSONObject(i);
			proxyNodes.add(new Connection(obj.getString("host"),obj.getInt("port")));
		}
    	
    	for(int i = 0; i < raftArr.length(); i++){
			JSONObject obj = raftArr.getJSONObject(i);
			raftNodes.add(new Connection(obj.getString("host"),obj.getInt("port")));
		}
		for(Connection c : raftNodes)
			System.out.println("Node "+c.getIP()+":"+c.getPort());

		for(int i = 0; i < dbarr.length(); i++){
			JSONObject obj = dbarr.getJSONObject(i);
			databaseNodes.add(new Connection(obj.getString("host"),obj.getInt("port")));
		}
		
		for(int i = 0; i < globalArr.length(); i++){
			JSONObject obj = globalArr.getJSONObject(i);
			globalNodes.add(new Connection(obj.getString("host"),obj.getInt("port")));
		}
    }
   
	
	
}
