package uk.ac.dundee.computing.kb.burnigram.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_GRAYSCALE;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr.Method;

//import uk.ac.dundee.computing.aec.stores.TweetStore;





import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

public class PicModel {

	private Cluster cluster;

	public PicModel() {

	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public void setCluster(){
		this.cluster=CassandraHosts.getCluster();
	}

	/**
	 * 
	 * @param b
	 * @param contentType
	 * @param name
	 *            Filename of Picture
	 * @param user
	 *            Name of the User
	 */
	public void insertPic(byte[] b, String contentType, String name, String user) {
		try {

			String types[] = Convertors.SplitFiletype(contentType);
			ByteBuffer buffer = ByteBuffer.wrap(b);
			int length = b.length;
			java.util.UUID picid = Convertors.getTimeUUID();

			// The following is a quick and dirty way of doing this, will fill
			// the disk quickly !
			Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
			FileOutputStream output = new FileOutputStream(new File(
					"/var/tmp/instagrim/" + picid));

			output.write(b);
			output.close();
			byte[] thumbb = picresize(picid.toString(), types[1]);
			int thumblength = thumbb.length;
			ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
			byte[] processedb = picdecolour(picid.toString(), types[1]);
			ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
			int processedlength = processedb.length;
			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

			PreparedStatement psInsertPic = session
					.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement psInsertPicToUser = session
					.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
			BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
			BoundStatement bsInsertPicToUser = new BoundStatement(
					psInsertPicToUser);

			Date DateAdded = new Date();
			session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,
					processedbuf, user, DateAdded, length, thumblength,
					processedlength, contentType, name));
			session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
			session.close();

		} catch (IOException ex) {
			System.out.println("Error --> " + ex);
		}
	}

	public byte[] picresize(String picid, String type) {
		try {
			BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/"
					+ picid));
			BufferedImage thumbnail = createThumbnail(BI);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(thumbnail, type, baos);
			baos.flush();

			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException et) {

		}
		return null;
	}

	public byte[] picdecolour(String picid, String type) {
		try {
			BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/"
					+ picid));
			BufferedImage processed = createProcessed(BI);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(processed, type, baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException et) {

		}
		return null;
	}

	public static BufferedImage createThumbnail(BufferedImage img) {
		img = resize(img, Method.BALANCED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
		// Let's add a little border before we return result.
		return pad(img, 2);
	}

	public static BufferedImage createProcessed(BufferedImage img) {
		int Width = img.getWidth() - 1;
		img = resize(img, Method.BALANCED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
		return pad(img, 4);
	}

	public java.util.LinkedList<Pic> getPicsForUser(String User) {
		java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

		PreparedStatement ps = session
				.prepare("SELECT picid FROM userpiclist WHERE user =?");
		ResultSet rs = null;
		BoundStatement boundStatement = new BoundStatement(ps);
		rs = session.execute( // this is where the query is executed
				boundStatement.bind( // here you are binding the
										// 'boundStatement'
						User));
		if (rs.isExhausted()) {
			System.out.println("No Images returned");
			return null;
		} else {
			for (Row row : rs) {
				Pic pic = new Pic();
				java.util.UUID UUID = row.getUUID("picid");
				System.out.println("UUID" + UUID.toString());
				pic.setUUID(UUID);
				Pics.add(pic);

			}
		}
		return Pics;
	}
	
	public void deletePic(java.util.UUID picid){
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps =  session.prepare("DELETE FROM userpics WHERE picid=?");
		ResultSet rs = session.execute(ps.bind(picid));
		PreparedStatement ps1 =  session.prepare("DELETE FROM pics WHERE picid=?");
		ResultSet rs1 = session.execute(ps.bind(picid));
		session.close();
	}

	public Pic getPic(int image_type, java.util.UUID picid) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		ByteBuffer bImage = null;
		String type = null;
		int length = 0;
		try {
			ResultSet rs = null;
			PreparedStatement ps = null;

			if (image_type == Convertors.DISPLAY_IMAGE) {

				ps = session
						.prepare("select image,imagelength,type from pics where picid =?");
			} else if (image_type == Convertors.DISPLAY_THUMB) {
				ps = session
						.prepare("select thumb,thumblength,type from pics where picid =?");
			} else if (image_type == Convertors.DISPLAY_PROCESSED) {
				ps = session
						.prepare("select processed,processedlength,type from pics where picid =?");
			}
			BoundStatement boundStatement = new BoundStatement(ps);
			rs = session.execute( // this is where the query is executed
					boundStatement.bind( // here you are binding the
											// 'boundStatement'
							picid));

			if (rs.isExhausted()) {
				System.out.println("No Images returned");
				return null;
			} else {
				for (Row row : rs) {
					if (image_type == Convertors.DISPLAY_IMAGE) {
						bImage = row.getBytes("image");
						length = row.getInt("imagelength");
					} else if (image_type == Convertors.DISPLAY_THUMB) {
						bImage = row.getBytes("thumb");
						length = row.getInt("thumblength");

					} else if (image_type == Convertors.DISPLAY_PROCESSED) {
						bImage = row.getBytes("processed");
						length = row.getInt("processedlength");
					}

					type = row.getString("type");

				}
			}
		} catch (Exception et) {
			System.out.println("Can't get Pic" + et);
			return null;
		}
		session.close();
		Pic p = new Pic();
		p.setPic(bImage, length, type);
		p.setUUID(picid);

		return p;

	}

}
