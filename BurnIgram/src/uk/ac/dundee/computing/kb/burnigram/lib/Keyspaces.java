package uk.ac.dundee.computing.kb.burnigram.lib;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;

public final class Keyspaces {

	public static final String KEYSPACE_NAME = "burnigram";

	public Keyspaces() {

	}

	public static void SetUpKeySpaces(Cluster c) {

		// Add some keyspaces here
		final String createkeyspace = "create keyspace if not exists "
				+ KEYSPACE_NAME
				+ "  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
		
		final String CreatePicTable = "CREATE TABLE if not exists " + KEYSPACE_NAME
				+ ".Pics (" + " user varchar," + " picid uuid, "
				+ " interaction_time timestamp," + " title varchar,"
				+ " image blob," + " thumb blob," + " processed blob,"
				+ " imagelength int," + " thumblength int,"
				+ " processedlength int," 
				+ " type  varchar," + " name  varchar,"
				+ " PRIMARY KEY (picid)" + ")";
		
		final String Createuserpiclist = "CREATE TABLE if not exists "
				+ KEYSPACE_NAME + ".userpiclist (\n" + "picid uuid,\n"
				+ "user varchar,\n" + "pic_added timestamp,\n"
				+ "PRIMARY KEY (picid,pic_added)\n"
				+ ") WITH CLUSTERING ORDER BY (pic_added desc);";

		final String CreatePicsIndexOnUser = "CREATE INDEX IF NOT EXISTS userpiclist_user ON "
						+KEYSPACE_NAME+".userpiclist (user);";
		
		final String CreateAddressType = "CREATE TYPE if not exists " + KEYSPACE_NAME
				+ ".address (\n" + "      street text,\n"
				+ "      city text,\n" + "      zip int\n" + "  );";
		
		final String CreateUserProfile = "CREATE TABLE if not exists "
				+ KEYSPACE_NAME + ".userprofiles (\n"
				+ " login varchar PRIMARY KEY,\n"
				+ " password text,\n" + "		salt varchar,\n"
				+ " first_name text,\n" + "      last_name text,\n"
				+ " email set<text>,\n"
				+ " addresses  map<text, frozen <address>>,\n"
				+ " profilepic uuid \n" + "  );";
		
		final String CreatePictureComment = "CREATE TABLE IF NOT EXISTS "+KEYSPACE_NAME + ".comments(\n"
				+ " id uuid, \n"
				+ " user varchar,\n"
				+ " comment text,\n"
				+ " picid uuid,\n"
				+ " comment_added timestamp,\n"
				+ " PRIMARY KEY (id, comment_added))"
				+ " WITH CLUSTERING ORDER BY (comment_added DESC);";
		
		final String CreateCommentPicidIndex  = "CREATE INDEX IF NOT EXISTS comment_picid_index ON "+
				KEYSPACE_NAME+".comments (picid);";
		Session session = null;
		try{
			session = c.connect();
		}catch(Exception ex){
			System.err.println("Can't connect to cluster. Is cassandra running?");
			return;
		}
		try {
			PreparedStatement statement = session.prepare(createkeyspace);
			session.execute(statement.bind());
			System.out.println("created " + KEYSPACE_NAME);
		} catch (Exception et) {
			System.err.println("Can't create " + KEYSPACE_NAME + " " + et);
		}

		// now add some column families
		System.out.println("" + CreatePicTable);

		try {
			SimpleStatement cqlQuery = new SimpleStatement(CreatePicTable);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create pics table " + et);
		}
		System.out.println("" + Createuserpiclist);

		try {
			SimpleStatement cqlQuery = new SimpleStatement(Createuserpiclist);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create userpiclist table " + et);
		}
		
		System.out.println("" + CreatePicsIndexOnUser);
		try{
			SimpleStatement cqlQuery = new SimpleStatement(CreatePicsIndexOnUser);
			session.execute(cqlQuery);
		}catch(Exception et){
			System.err.println("Can't create index on user in userpiclist "+ et);
		}
		
		System.out.println("" + CreateAddressType);
		try {
			SimpleStatement cqlQuery = new SimpleStatement(CreateAddressType);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create Address type " + et);
		}
		
		System.out.println("" + CreateUserProfile);
		try {
			SimpleStatement cqlQuery = new SimpleStatement(CreateUserProfile);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create usersprofile " + et);
		}
		
		System.out.println("" + CreatePictureComment);
		try {
			SimpleStatement cqlQuery = new SimpleStatement(CreatePictureComment);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create comment " + et);
		}
		
		System.out.println("" + CreateCommentPicidIndex);
		try {
			SimpleStatement cqlQuery = new SimpleStatement(CreateCommentPicidIndex);
			session.execute(cqlQuery);
		} catch (Exception et) {
			System.err.println("Can't create index on comment(user)" + et);
		}
		
		session.close();

	}
}
