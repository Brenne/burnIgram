/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
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
	private String password;
	private String firstname;
	private String lastname;
	private String email;
	private UUID profilepic;

	public User() {
		this.cluster = CassandraHosts.getCluster();

	}

	public User(String username, String firstname, String lastname,
			String email, String password) {
		this(username, password);
		this.firstname = firstname;
		this.lastname = lastname;

	}

	public User(String username, String password) {
		this();
		this.username = username;
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public UUID getProfilepic() {
		return profilepic;
	}

	public boolean registerUser() {

		if (isUserNameOrPasswordEmpty() | !validUserName(this.username)
				| userNameExists(this.username)) {
			return false;
		}
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(this.password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			return false;
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("insert into userprofiles "
				+ "(login,password,first_name,last_name,email) "
				+ "Values(?,?,?,?,?)");

		BoundStatement boundStatement = new BoundStatement(ps);
		session.execute( // this is where the query is executed
		boundStatement.bind(
				// here you are binding the 'boundStatement'
				this.username, encodedPassword, this.firstname, this.lastname,
				this.email));
		// We are assuming this always works. Also a transaction would be good
		// here !
		session.close();
		return true;
	}

	public boolean isValidUser() {
		if(isUserNameOrPasswordEmpty())
			return false;
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(this.password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			return false;
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("SELECT password FROM userprofiles where login =?");
		ResultSet rs = null;
		BoundStatement boundStatement = new BoundStatement(ps);
		rs = session.execute( // this is where the query is executed
				boundStatement.bind( // here you are binding the
										// 'boundStatement'
						this.username));
		session.close();
		if (rs.isExhausted()) {
			System.out.println("No user  match");
			return false;
		}else{
			Row userEntry = rs.one();
			String dbPassword = userEntry.getString("password");
			if(dbPassword.equals(encodedPassword)){
				return true;
			}else{
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

	public boolean userNameExists(String userName) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("Select login FROM userprofiles where login=?");
		ResultSet rs = session.execute(ps.bind(userName));
		session.close();
		if (rs.isExhausted()) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean changeProfilepic(Pic picture){
		if(!userNameExists(this.username)){
			return false;
		}else{
			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement ps = session
					.prepare("UPDATE userprofiles "
							+ " SET profilepic=? WHERE login=?");
			ResultSet rs = session.execute(ps.bind(picture.getUUID(),this.username));
			this.profilepic=picture.getUUID();
			session.close();
			return true;
		}
	}

	private boolean isUserNameOrPasswordEmpty() {
		if (this.username == null | this.username.isEmpty()
				| this.password == null | this.password.isEmpty())
			return true;
		else
			return false;
	}
	
//	@Override
//	protected void finalize() throws Throwable {
//		
//		if(this.cluster != null && !this.cluster.isClosed()){
//			this.cluster.close();
//		}
//		super.finalize();
//	}

}
