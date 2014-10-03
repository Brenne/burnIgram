/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import uk.ac.dundee.computing.kb.burnigram.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;

import com.datastax.driver.core.BoundStatement;
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

	public User() {
		this.cluster = CassandraHosts.getCluster();

	}

	public boolean registerUser(String username, String Password) {
		if (userNameExists(username)) {
			return false;
		}
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(Password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			return false;
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("insert into userprofiles (login,password) Values(?,?)");

		BoundStatement boundStatement = new BoundStatement(ps);
		session.execute( // this is where the query is executed
		boundStatement.bind( // here you are binding the 'boundStatement'
				username, encodedPassword));
		// We are assuming this always works. Also a transaction would be good
		// here !

		return true;
	}

	public boolean isValidUser(String username, String Password) {
		String encodedPassword = null;
		try {
			encodedPassword = AeSimpleSHA1.SHA1(Password);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
			System.out.println("Can't check your password");
			return false;
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("select password from userprofiles where login =?");
		ResultSet rs = null;
		BoundStatement boundStatement = new BoundStatement(ps);
		rs = session.execute( // this is where the query is executed
				boundStatement.bind( // here you are binding the
										// 'boundStatement'
						username));
		if (rs.isExhausted()) {
			System.out.println("No Images returned");
			return false;
		} else {
			for (Row row : rs) {

				String StoredPass = row.getString("password");
				if (StoredPass.compareTo(encodedPassword) == 0)
					return true;
			}
		}

		return false;
	}

	private boolean checkUserName(String userName) {
		String Pattern = "^[a-zA-Z0-9_-]{3,15}$";
		return true;

	}

	public boolean userNameExists(String userName) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("Select login FROM userprofiles where login=?");
		ResultSet rs = session.execute(ps.bind(userName));
		if (rs.isExhausted()) {
			return false;
		} else {
			return true;
		}
	}

}
