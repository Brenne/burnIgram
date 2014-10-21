package uk.ac.dundee.computing.kb.burnigram.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;

public class Comment {
	
	private UUID id;
	private UUID picid;
	private Date created;
	private User user;
	private String content;
	
	public Comment(){
		this.id= Convertors.getTimeUUID();
	}
	
	public Comment(UUID picid, Date created, User user, String content){
		this();
		this.picid = picid;
		this.created = created;
		this.user = user;
		this.content = content;
	}
	
	public Comment(UUID id, UUID picid, Date created, User user, String content){
		this(picid, created, user, content);
		this.id= id;
	}

	public UUID getId() {
		return id;
	}

	public UUID getPicid() {
		return picid;
	}

	public Date getCreated() {
		return created;
	}
	
	public String getCreatedS(){
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(created);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    int hours = cal.get(Calendar.HOUR_OF_DAY);
	    int minutes = cal.get(Calendar.MINUTE);
	    String returnString = String.format("%02d:%02d on %d/%d/%d", 
	    		hours,minutes,day,month,year);
	    return returnString;
	}

	public User getUser() {
		return user;
	}

	public String getContent() {
		return content;
	}
	
	
}
