/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.kb.burnigram.beans;



import java.nio.ByteBuffer;
import java.util.Date;

import com.datastax.driver.core.utils.Bytes;

/**
 *
 * @author Administrator
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID=null;
    private Date updated = null;
    private User user;
    private String filename;
    
    public Pic() {

    }
    public void setUUID(java.util.UUID UUID){
        this.UUID =UUID;
    }
    public String getSUUID(){
        return this.UUID.toString();
    }
    public java.util.UUID getUUID(){
    	return this.UUID;
    }
    
    public void setPic(ByteBuffer bImage, int length,String type, Date updated, User user, String name){
    	this.updated=updated;
    	this.user=user;
    	this.filename=name;
    	this.setPic(bImage, length, type);
    }
      
    public void setPic(ByteBuffer bImage, int length,String type) {
        this.bImage = bImage;
        this.length = length;
        this.type=type;
    }

    public ByteBuffer getBuffer() {
        return bImage;
    }

    public int getLength() {
        return length;
    }
    
    public String getType(){
        return type;
    }

    public byte[] getBytes() {
         
        byte image[] = Bytes.getArray(bImage);
        return image;
    }
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getDate() {
		return updated;
	}
	
	public String getName(){
		return filename;
	}

}
