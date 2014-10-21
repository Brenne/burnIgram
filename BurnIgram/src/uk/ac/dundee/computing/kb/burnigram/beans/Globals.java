package uk.ac.dundee.computing.kb.burnigram.beans;

public final class Globals {
	
	public final static String APP_NAME = "Burnigram";
	public final static String ROOT_PATH ="/Burnigram";
	public final static boolean DEBUG = true;
	private String app_name = APP_NAME;
	private String root_path = ROOT_PATH;
	
	public String getApp_name() {
		return app_name;
	}
	public String getRoot_path() {
		return root_path;
	}
	
	public Globals(){
		
	}
	

}
