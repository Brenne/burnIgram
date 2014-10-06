package uk.ac.dundee.computing.kb.burnigram.models;

import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_GRAYSCALE;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Rotation;

import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class PicModel {

	private Cluster cluster;

	private static final String ROTATE = "rotate";
	private static final String BRIGHTNESS = "brightness";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";

	private static final List<String> manipulationKeyList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(ROTATE);
		myList.add(BRIGHTNESS);
		manipulationKeyList = Collections.unmodifiableList(myList);
	}

	private static final List<String> rotationOperationsList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(LEFT);
		myList.add(RIGHT);
		rotationOperationsList = Collections.unmodifiableList(myList);
	}

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

			java.util.UUID picid = Convertors.getTimeUUID();

			ByteArrayInputStream is = new ByteArrayInputStream(b);
			BufferedImage bufferedImg = ImageIO
					.read(new ByteArrayInputStream(b));
			ByteBuffer imageInByte = ByteBuffer.wrap(b);
			is.close();
			// Creating thumbnail image
			BufferedImage thumbnail = createThumbnail(bufferedImg);
			byte[] thumbInByte = bufferedImageToByteArray(thumbnail, types[1]);
			ByteBuffer thumbbuf = ByteBuffer.wrap(thumbInByte);

			// Creating processed image
			BufferedImage processed = createProcessed(bufferedImg);
			byte[] processedInByte = bufferedImageToByteArray(processed,
					types[1]);
			ByteBuffer processedbuf = ByteBuffer.wrap(processedInByte);

			Date currentTimestamp = new Date();

			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement psInsertPic = session
					.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement psInsertPicToUser = session
					.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");

			session.execute(psInsertPic.bind(picid, imageInByte, thumbbuf,
					processedbuf, username, currentTimestamp, b.length,
					thumbInByte.length, processedInByte.length, contentType,
					name));
			session.execute(psInsertPicToUser.bind(picid, username,
					currentTimestamp));
			session.close();

		} catch (IOException ex) {
			System.out.println("Error --> " + ex);
		}
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

	/**
	 * @param pic
	 * @param direction
	 *            a string containing either "right" or "left"
	 * @return returns rotated BufferedImage. In case of an error this method
	 *         returns null
	 */
	public static BufferedImage rotate(Pic pic, final String direction) {
		if (!stringInStringList(direction, rotationOperationsList)) {
			System.err.println("rotate pic invalid rotation direction "
					+ direction);
			return null;

		}
		byte[] bytes = pic.getBytes();
		BufferedImage img = null;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			img = ImageIO.read(is);
			is.close();
		} catch (IOException e) {
			System.err.println("picRotate pic byte stream is empty");
			e.printStackTrace();
			return img;
		}

		switch (direction) {
		case LEFT:
			img = org.imgscalr.Scalr.rotate(img, Rotation.CW_270);
			break;
		case RIGHT:
			img = org.imgscalr.Scalr.rotate(img, Rotation.CW_90);
			break;

		}
		if (img == null) {
			return img;
		}
		return img;

	}

	public void updatePic(Pic pic, Entry<String, String> typeOfManipulation) {
		final String manipulationKey = typeOfManipulation.getKey();

		if (!stringInStringList(manipulationKey, manipulationKeyList)) {
			System.err.println("update Picture invalid manipulation type "
					+ manipulationKey);
			return;
		}
		String types[] = Convertors.SplitFiletype(pic.getType());
		BufferedImage buffManipulatedImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_BYTE_BINARY);
		switch (manipulationKey) {
		case ROTATE:
			buffManipulatedImage = PicModel.rotate(pic,
					typeOfManipulation.getValue());
			break;

		}

		BufferedImage thumbnail = createThumbnail(buffManipulatedImage);
		BufferedImage processed = createProcessed(buffManipulatedImage);
		byte[] thumbnailInBytes = null;
		byte[] processedInBytes = null;

		try {
			thumbnailInBytes = bufferedImageToByteArray(thumbnail, types[1]);
			processedInBytes = bufferedImageToByteArray(processed, types[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteBuffer proccesedByteBuff = ByteBuffer.wrap(processedInBytes);
		ByteBuffer thumbnailByteBuff = ByteBuffer.wrap(thumbnailInBytes);

		Date currentTimestamp = new Date();

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);

		PreparedStatement psUpdatePic = session
				.prepare("UPDATE pics SET thumb=?, thumblength=?, processed=?, processedlength=?, interaction_time=? WHERE picid=?");

		session.execute(psUpdatePic.bind(thumbnailByteBuff,
				thumbnailInBytes.length, proccesedByteBuff,
				processedInBytes.length, currentTimestamp, pic.getUUID()));

		session.close();

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

	private byte[] bufferedImageToByteArray(BufferedImage bufferedImage,
			String formatString) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (!ImageIO.write(bufferedImage, formatString, baos)) {
			System.err
					.println("Error in bufferdImageToByteArray no ImageIO reader found "
							+ "for type " + formatString);
		}
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;

	}

	private static boolean stringInStringList(String needle,
			final List<String> stringList) {
		boolean keyInList = false;
		for (String key : stringList) {
			if (key.equalsIgnoreCase(needle)) {
				keyInList = true;
				break;
			}
		}
		return keyInList;
	}

}
