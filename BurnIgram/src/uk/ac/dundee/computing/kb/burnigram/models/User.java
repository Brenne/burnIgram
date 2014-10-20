/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 *
 * @author Administrator
 */
public class User {

	private Cluster cluster;
	private String username;
	private String firstname;
	private String lastname;
	private Set<String> email;
	private UUID profilepic;

	public User() {
		this.cluster = CassandraHosts.getCluster();

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

	public boolean registerUser(String password) throws Throwable {

		if (this.username == null || !validUserName(this.username)){
			throw new Throwable("Username must not be empty and between 3 and  15 Characters long");
		}else if(userNameExists(this.username)) {
			throw new Throwable("Username already exists");
		}
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			throw new Throwable("Please don't use special characters in your password");
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("insert into userprofiles "
				+ "(login,password,first_name,last_name,email) "
				+ "Values(?,?,?,?,?)");

		session.execute( // this is where the query is executed

		ps.bind(
		// here you are binding the 'boundStatement'
		this.username, encodedPassword, this.firstname, this.lastname, this.email));
		session.close();
		return true;
	}
	
	public boolean updateUser(){
		if (!userNameExists()) {
			//ensures that where query will match a entry
			return false;
		} else {
			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement ps = session.prepare("UPDATE userprofiles "
					+ " SET first_name=? ,last_name=?,email=?,profilepic=? WHERE login=?");
			session.execute(ps.bind(this.firstname,this.lastname,this.email,this.profilepic,
					this.username));
			session.close();
			return true;
		}
	}

	public boolean isValidUser(String password, String salt) {
		if (this.username==null)
			return false;
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("SELECT password FROM userprofiles where login =?");
		
		ResultSet rs = session.execute( // this is where the query is executed
				ps.bind( // here you are binding the
										// 'boundStatement'
						this.username));
		session.close();
		if (rs.isExhausted()) {
			System.out.println("No user  match");
			return false;
		} else {
			Row userEntry = rs.one();
			String dbPassword = userEntry.getString("password");
			try {
				String dbWithSalt = AeSimpleSHA1.SHA1(salt+dbPassword);
				if (dbWithSalt.equals(password)) {
					return true;
				} else {
					return false;
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				
				e.printStackTrace();
				return false;
			}

		}
	}

	private boolean validUserName(String userName) {
		String pattern = "^[a-zA-Z0-9_-]{3,15}$";
		if (userName.matches(pattern))
			return true;
		else
			return false;

	}

	public static boolean userNameExists(String userName){
		User user = new User(userName);
		return user.userNameExists();
	}
	
	public boolean userNameExists() {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("Select login FROM userprofiles where login=?");
		ResultSet rs = session.execute(ps.bind(this.username));
		session.close();
		if (rs.isExhausted()) {
			return false;
		} else {
			return true;
		}
	}
	
	public void deleteProfilePic(){
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps1 = session
				.prepare("UPDATE userprofiles SET profilepic=NULL WHERE login=?");
		session.execute(ps1.bind(this.username));
		this.profilepic=null;
		
	}
	
	public static User initUserFromDB(String userName) {
		User user = new User();
		user.username = userName;
		user.initUserFromDB();
		return user;
	}

	public void initUserFromDB() {

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("SELECT * FROM userprofiles WHERE login=?");
		ResultSet rsUser = session.execute(ps.bind(this.getUsername()));
		if (!rsUser.isExhausted()) {
			Row rowUser = rsUser.one();
			this.firstname = rowUser.getString("first_name");
			this.lastname = rowUser.getString("last_name");
			this.email = rowUser.getSet("email", String.class);
			this.profilepic = rowUser.getUUID("profilepic");

		}

	}



	// @Override
	// protected void finalize() throws Throwable {
	//
	// if(this.cluster != null && !this.cluster.isClosed()){
	// this.cluster.close();
	// }
	// super.finalize();
	// }

}
