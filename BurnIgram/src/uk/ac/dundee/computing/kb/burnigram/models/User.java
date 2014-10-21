/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.dbHelpers.UserDbHelper;
import uk.ac.dundee.computing.kb.burnigram.lib.AeSimpleSHA1;

/**
 *
 * @author Administrator
 */
public class User {


	private String username;
	private String firstname;
	private String lastname;
	private Set<String> email;
	private UUID profilepic;

	public User() {
		

	}

	public User(String username, String firstname, String lastname,
			Set<String> email) {
		this(username);
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;

	}

	public User(String username) {
		this();
		this.username = username;	
	}

	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public Set<String> getEmail() {
		return email;
	}

	public UUID getProfilepicId() {
		return profilepic;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setEmail(Set<String> email) {
		this.email = email;
	}

	public void setProfilepic(UUID profilepic) {
		this.profilepic = profilepic;
	}


	private boolean validUserName(String userName) {
		String pattern = "^[a-zA-Z0-9_-]{3,15}$";
		if (userName.matches(pattern))
			return true;
		else
			return false;

	}
	
	public boolean registerUser(String password) throws Throwable {
		UserDbHelper userDbHelper = new UserDbHelper();
		if (this.username == null || !validUserName(this.username)){
			throw new Throwable("Username must not be empty and between 3 and  15 Characters long");
		}else if(userDbHelper.userNameExists(this.username)) {
			throw new Throwable("Username already exists");
		}
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			throw new Throwable("Please don't use special characters in your password");
		}
		
		return userDbHelper.registerUser(this, encodedPassword);
	}
	
	public void deleteProfilePic(){
		UserDbHelper dbHelper = new UserDbHelper();
		dbHelper.deleteProfilePic(this.username);
	}
	
	public static User initUserFromDB(String username){
		UserDbHelper dbHelper = new UserDbHelper();
		return dbHelper.getUserFromDb(username);
	}
	
	public static boolean userNameExists(String username){
		UserDbHelper dbHelper = new UserDbHelper();
		return dbHelper.userNameExists(username);
	}

}
