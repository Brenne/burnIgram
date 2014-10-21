/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.beans;


/**
 *
 * @author Administrator
 */
public class LoggedIn {
    boolean logedin=false;
    private User user=null;
    public void LogedIn(){
        
    }
    
    public void setUser(User user){
        this.user=user;
    }
    public User getUser(){
        return this.user;
    }
    /**
     * sets member variable logedin to true
     */
    public void setLogedin(){
        logedin=true;
    }
    public void setLogedout(){
        logedin=false;
    }
    
    public void setLoginState(boolean logedin){
        this.logedin=logedin;
    }
    public boolean getLogedin(){
        return logedin;
    }
}
