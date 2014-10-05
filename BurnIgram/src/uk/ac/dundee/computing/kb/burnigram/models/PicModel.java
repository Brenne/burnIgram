package uk.ac.dundee.computing.kb.burnigram.models;

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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr.Method;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

//import uk.ac.dundee.computing.aec.stores.TweetStore;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

public class PicModel {

	private Cluster cluster;

	public PicModel() {

	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public void setCluster() {
		this.cluster = CassandraHosts.getCluster();
	}

	/**
	 * 
	 * @param b
	 * @param contentType
	 * @param name
	 *            Filename of Picture
	 * @param username
	 *            Name of the User
	 */
	public void insertPic(byte[] b, String contentType, String name,
			String username) {
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

			Date currentTimestamp = new Date();

			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

			PreparedStatement psInsertPic = session
					.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement psInsertPicToUser = session
					.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");

			session.execute(psInsertPic.bind(picid, buffer, thumbbuf,
					processedbuf, username, currentTimestamp, length,
					thumblength, processedlength, contentType, name));
			session.execute(psInsertPicToUser.bind(picid, username,
					currentTimestamp));
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

	public LinkedList<Pic> getPicsForUser(String username) {
		LinkedList<Pic> picList = new LinkedList<>();
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

		PreparedStatement ps = session
				.prepare("SELECT picid FROM userpiclist WHERE user =?");
		ResultSet rs = null;

		rs = session.execute( // this is where the query is executed
				ps.bind( // here you are binding the
							// 'boundStatement'
				username));
		if (rs.isExhausted()) {
			System.out.println("No Images returned");
			return null;
		} else {
			for (Row row : rs) {
				UUID picid = row.getUUID("picid");
				Pic pic = new Pic();
				System.out.println("UUID" + picid.toString());
				pic.setUUID(picid);
				picList.add(pic);
			}
		}
		return picList;
	}

	public void deletePic(Pic pic) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps1 = session
				.prepare("DELETE FROM pics WHERE picid=?");
		ResultSet rs1 = session.execute(ps1.bind(pic.getUUID()));
		PreparedStatement ps2 = session
				.prepare("DELETE FROM userpiclist WHERE pic_added=?"
						+ " AND user=?");
		ResultSet rs2 = session.execute(ps2.bind(pic.getDate(), pic.getUser()
				.getUsername()));

		session.close();
	}

	public Pic getPicFromDB(int image_type, java.util.UUID picid) {
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

		try {
			ResultSet rs = null;
			PreparedStatement ps = null;
			final String PREFIX_QUERY = "SELECT type, interaction_time, user, name, ";
			final String POSTFIX_QUERY = " FROM pics WHERE picid=?";
			switch (image_type) {
			case Convertors.DISPLAY_IMAGE:
				ps = session.prepare(PREFIX_QUERY + "image,imagelength"
						+ POSTFIX_QUERY);
				break;
			case Convertors.DISPLAY_THUMB:
				ps = session.prepare(PREFIX_QUERY + "thumb,thumblength"
						+ POSTFIX_QUERY);
				break;
			case Convertors.DISPLAY_PROCESSED:
				ps = session.prepare(PREFIX_QUERY + "processed,processedlength"
						+ POSTFIX_QUERY);
				break;
			default:
				System.err.println("getPicFromDB invalid image_type 1"
						+ Integer.toString(image_type));

			}
			rs = session.execute( // this is where the query is executed
					ps.bind(picid));

			if (rs.isExhausted()) {
				System.out.println("No Images returned");
				return null;
			} else {
				ByteBuffer bImage = null;
				int length = 0;
				Row row = rs.one();
				switch (image_type) {
				case Convertors.DISPLAY_IMAGE:
					bImage = row.getBytes("image");
					length = row.getInt("imagelength");
					break;
				case Convertors.DISPLAY_THUMB:
					bImage = row.getBytes("thumb");
					length = row.getInt("thumblength");
					break;
				case Convertors.DISPLAY_PROCESSED:
					bImage = row.getBytes("processed");
					length = row.getInt("processedlength");
					break;
				default:
					System.err.println("getPicFromDB invalid image_type 2"
							+ Integer.toString(image_type));
					return null;

				}

				String type = row.getString("type");
				Date date = row.getDate("interaction_time");
				User user = User.initUserFromDB(row.getString("user"));
				String picname = row.getString("name");
				session.close();
				Pic p = new Pic();
				p.setPic(bImage, length, type, date, user, picname);
				p.setUUID(picid);
				if (!rs.isExhausted()) {
					// should never happen.
					System.out
							.println("More than one row in with same picid in pics?");
				}
				return p;

			}
		} catch (Exception et) {
			System.err.println("Can't get Pic" + et);
			return null;
		}

	}

}
