package uk.ac.dundee.computing.kb.burnigram.dbHelpers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.beans.Globals;
import uk.ac.dundee.computing.kb.burnigram.beans.User;
import uk.ac.dundee.computing.kb.burnigram.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class UserDbHelper {

	private Cluster cluster;

	public UserDbHelper() {
		this.cluster = CassandraHosts.getCluster();
	}
	
	public boolean registerUser(User user, String encodedPassword) {

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("insert into userprofiles "
				+ "(login,password,first_name,last_name,email) "
				+ "Values(?,?,?,?,?)");

		session.execute( // this is where the query is executed

		ps.bind(
		// here you are binding the 'boundStatement'
		user.getUsername(), encodedPassword, user.getFirstname(),
				user.getLastname(), user.getEmail()));
		session.close();
		return true;
	}

	public boolean updateUser(User user) {
		if (!userNameExists(user.getUsername())) {
			// ensures that where query will match an entry
			return false;
		} else {
			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement ps = session
					.prepare("UPDATE userprofiles "
							+ " SET first_name=? ,last_name=?,email=?,profilepic=? WHERE login=?");
			session.execute(ps.bind(user.getFirstname(), user.getLastname(),
					user.getEmail(), user.getProfilepicId(), user.getUsername()));
			session.close();
			return true;
		}
	}

	public boolean isValidUser(String username, String encodedPassword,
			String salt) {
		if (username == null)
			return false;
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("SELECT password FROM userprofiles where login =?");

		ResultSet rs = session.execute( // this is where the query is executed
				ps.bind( // here you are binding the
							// 'boundStatement'
				username));
		session.close();
		if (rs.isExhausted()) {
			System.out.println("No user  match");
			return false;
		} else {
			Row userEntry = rs.one();
			String dbPassword = userEntry.getString("password");
			try {
				String dbWithSalt = AeSimpleSHA1.SHA1(salt + dbPassword);
				if (dbWithSalt.equals(encodedPassword)) {
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

	public boolean userNameExists(String username) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("Select login FROM userprofiles where login=?");
		ResultSet rs = session.execute(ps.bind(username));
		session.close();
		if (rs.isExhausted()) {
			return false;
		} else {
			return true;
		}
	}

	public void deleteProfilePic(String username) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps1 = session
				.prepare("UPDATE userprofiles SET profilepic=NULL WHERE login=?");
		session.execute(ps1.bind(username));
		session.close();

	}

	public User getUserFromDb(String username) {

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session
				.prepare("SELECT * FROM userprofiles WHERE login=?");

		ResultSet rsUser = session.execute(ps.bind(username));
		session.close();
		User user = null;
		if (!rsUser.isExhausted()) {
			Row rowUser = rsUser.one();
			String firstname = rowUser.getString("first_name");
			String lastname = rowUser.getString("last_name");
			Set<String> email = rowUser.getSet("email", String.class);
			UUID profilepic = rowUser.getUUID("profilepic");
			user = new User(username, firstname, lastname, email);
			user.setProfilepic(profilepic);

		} else {
			if (Globals.DEBUG)
				System.out.println("No user found for " + username);
		}
		return user;

	}

}
