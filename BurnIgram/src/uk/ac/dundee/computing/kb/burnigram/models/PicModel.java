package uk.ac.dundee.computing.kb.burnigram.models;

import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_BRIGHTER;
import static org.imgscalr.Scalr.OP_DARKER;
import static org.imgscalr.Scalr.OP_GRAYSCALE;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

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
import com.datastax.driver.core.SimpleStatement;

public class PicModel {

	private Cluster cluster;

	private static final String ROTATE = "rotate";
	private static final String BRIGHTNESS = "brightness";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String DARK = "dark";
	private static final String BRIGHT = "bright";

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

	private static final List<String> brightnessOperationsList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(DARK);
		myList.add(BRIGHT);
		brightnessOperationsList = Collections.unmodifiableList(myList);
	}

	public PicModel() {
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

		String types[] = Convertors.SplitFiletype(contentType);

		java.util.UUID picid = Convertors.getTimeUUID();

		BufferedImage bufferedImg = Convertors.byteArrayToBufferedImage(b);
		bufferedImg = blackAndWhite(bufferedImg);
		ByteBuffer imageInByte = ByteBuffer.wrap(b);

		// Creating thumbnail image
		BufferedImage thumbnail = createThumbnail(bufferedImg);
		byte[] thumbInByte = Convertors.bufferedImageToByteArray(thumbnail, types[1]);
		ByteBuffer thumbbuf = ByteBuffer.wrap(thumbInByte);

		// Creating processed image
		BufferedImage processed = createProcessed(bufferedImg);
		byte[] processedInByte = Convertors.bufferedImageToByteArray(processed, types[1]);
		ByteBuffer processedbuf = ByteBuffer.wrap(processedInByte);

		Date currentTimestamp = new Date();

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement psInsertPic = session
				.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name,rotation) values(?,?,?,?,?,?,?,?,?,?,?,?)");
		PreparedStatement psInsertPicToUser = session
				.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");

		session.execute(psInsertPic.bind(picid, imageInByte, thumbbuf,
				processedbuf, username, currentTimestamp, b.length,
				thumbInByte.length, processedInByte.length, contentType, name,
				0));
		session.execute(psInsertPicToUser.bind(picid, username,
				currentTimestamp));
		session.close();

	}

	public static BufferedImage blackAndWhite(BufferedImage img) {
		img = org.imgscalr.Scalr.apply(img, OP_GRAYSCALE);
		return img;
	}

	public static BufferedImage createThumbnail(BufferedImage img) {
		img = resize(img, Method.BALANCED, 250, OP_ANTIALIAS);
		// Let's add a little border before we return result.
		return pad(img, 2);
	}

	public static BufferedImage createProcessed(BufferedImage img) {
		int Width = img.getWidth() - 1;
		img = resize(img, Method.QUALITY, Width, OP_ANTIALIAS);
		return pad(img, 4);
	}

	public static BufferedImage rotate(BufferedImage buffImage, String direction) {
		if (!stringInStringList(direction, rotationOperationsList)) {
			System.err.println("rotate Picture  invalid manipulation value "
					+ direction);
			return buffImage;
		}

		switch (direction) {
		case LEFT:
			buffImage = org.imgscalr.Scalr.rotate(buffImage, Rotation.CW_270);
			break;
		case RIGHT:
			buffImage = org.imgscalr.Scalr.rotate(buffImage, Rotation.CW_90);
			break;
		}

		return buffImage;
	}

	public static BufferedImage changeBrighteness(BufferedImage buffImage,
			String brightness) {
		if (!stringInStringList(brightness, brightnessOperationsList)) {
			System.err
					.println("picture update brightness invalid manipulation value "
							+ brightness);
			return buffImage;
		}
		switch (brightness) {
		case DARK:
			buffImage = org.imgscalr.Scalr.apply(buffImage, OP_DARKER);
			break;
		case BRIGHT:
			buffImage = org.imgscalr.Scalr.apply(buffImage, OP_BRIGHTER);
			break;
		}
		return buffImage;
	}

	private BufferedImage manipulatePic(Pic pic,
			Entry<String, String> typeOfManipulation) {
		final String manipulationKey = typeOfManipulation.getKey();
		final String manipulationValue = typeOfManipulation.getValue();
		if (!stringInStringList(manipulationKey, manipulationKeyList)) {
			System.err.println("update Picture invalid manipulation type "
					+ manipulationKey);
			return null;
		}

		BufferedImage manipulatedBuffImg = Convertors.byteArrayToBufferedImage(pic
				.getBytes());
		switch (manipulationKey) {
		case ROTATE:
			manipulatedBuffImg = rotate(manipulatedBuffImg, manipulationValue);

			break;
		case BRIGHTNESS:
			manipulatedBuffImg = changeBrighteness(manipulatedBuffImg,
					manipulationValue);
			break;
		}
		return manipulatedBuffImg;

	}

	public void updatePic(UUID picID, Entry<String, String> typeOfManipulation) {

		Pic processedPic = this.getPicFromDB(Convertors.DISPLAY_PROCESSED,
				picID);
		Pic thubmnailPic = this.getPicFromDB(Convertors.DISPLAY_THUMB, picID);

		BufferedImage proccesedBuff = manipulatePic(processedPic,
				typeOfManipulation);
		BufferedImage thumbnailBuff = manipulatePic(thubmnailPic,
				typeOfManipulation);

		byte[] thumbnailInBytes = null;
		byte[] processedInBytes = null;

		String thumbnailTypes[] = Convertors.SplitFiletype(thubmnailPic.getType());
		String processedTypes[] = Convertors.SplitFiletype(thubmnailPic.getType());

		thumbnailInBytes = Convertors.bufferedImageToByteArray(thumbnailBuff, thumbnailTypes[1]);
		processedInBytes = Convertors.bufferedImageToByteArray(proccesedBuff, processedTypes[1]);

		ByteBuffer proccesedByteBuff = ByteBuffer.wrap(processedInBytes);
		ByteBuffer thumbnailByteBuff = ByteBuffer.wrap(thumbnailInBytes);

		Date currentTimestamp = new Date();

		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement psUpdatePic = session
				.prepare("UPDATE pics SET thumb=?, thumblength=?, processed=?, processedlength=?, interaction_time=? WHERE picid=?");

		session.execute(psUpdatePic.bind(thumbnailByteBuff,
				thumbnailInBytes.length, proccesedByteBuff,
				processedInBytes.length, currentTimestamp, picID));

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
		if (pic == null) {
			System.err.println("Cannot delete Picture with pic is null");
			return;
		}
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps1 = session
				.prepare("DELETE FROM pics WHERE picid=?");
		session.execute(ps1.bind(pic.getUUID()));
		PreparedStatement ps2 = session
				.prepare("DELETE FROM userpiclist WHERE picid=?");
		session.execute(ps2.bind(pic.getUUID()));

		session.close();
	}
	
	public User getPicOwnerFromDB(UUID picid){
		SimpleStatement st = new SimpleStatement("SELECT user FROM pics WHERE picid=?",picid);
		Session session = this.cluster.connect(Keyspaces.KEYSPACE_NAME);
		ResultSet rs = session.execute(st);
		if(!rs.isExhausted()){
			Row row = rs.one();
			String username = row.getString(0);
			if(username != null && !username.isEmpty()){
				User user = User.initUserFromDB(username);
				return user;
			}else{
				System.err.println("getPicOwnerFromDB picture found but now owner");
			}
		}else{
			System.err.println("getPicOwnerFromDB no picture with this id found "+ picid.toString());
		}
		return null;
	}

	public Pic getPicFromDB(int image_type, UUID picid) {

		Session session = this.cluster.connect(Keyspaces.KEYSPACE_NAME);

		final String PREFIX_QUERY = "SELECT type, interaction_time, user, name, ";
		final String POSTFIX_QUERY = " FROM pics WHERE picid=?";
		final String LENGTH ="length";
		String imageType ="";
		switch (image_type) {
		case Convertors.DISPLAY_ORIGINAL_IMAGE:
			imageType = "image";
			break;
		case Convertors.DISPLAY_THUMB:
			imageType ="thumb";
			break;
		case Convertors.DISPLAY_PROCESSED:
			imageType="processed";
			break;
		default:
			System.err.println("getPicFromDB invalid image_type 1"
					+ Integer.toString(image_type));

		}
		PreparedStatement ps = session.prepare(PREFIX_QUERY + imageType + ","+imageType+LENGTH
				+ POSTFIX_QUERY);
		ResultSet rs = session.execute( // this is where the query is executed
				ps.bind(picid));

		if (rs.isExhausted()) {
			System.out.println("No Images returned");
			return null;
		} else {
			ByteBuffer bImage = null;
			int length = 0;
			Row row = rs.one();
			bImage = row.getBytes(imageType);
			length = row.getInt(imageType+LENGTH);
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
