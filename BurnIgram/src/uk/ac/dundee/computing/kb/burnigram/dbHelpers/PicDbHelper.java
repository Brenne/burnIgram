package uk.ac.dundee.computing.kb.burnigram.dbHelpers;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Map.Entry;

import uk.ac.dundee.computing.kb.burnigram.beans.Globals;
import uk.ac.dundee.computing.kb.burnigram.beans.Pic;
import uk.ac.dundee.computing.kb.burnigram.beans.User;
import uk.ac.dundee.computing.kb.burnigram.controller.PicController;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;

public class PicDbHelper {
	
	private Cluster cluster;
	
	public PicDbHelper() {
		this.cluster=CassandraHosts.getCluster();
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
		bufferedImg = PicController.blackAndWhite(bufferedImg);
		ByteBuffer imageInByte = ByteBuffer.wrap(b);

		// Creating thumbnail image
		BufferedImage thumbnail = PicController.createThumbnail(bufferedImg);
		byte[] thumbInByte = Convertors.bufferedImageToByteArray(thumbnail, types[1]);
		ByteBuffer thumbbuf = ByteBuffer.wrap(thumbInByte);

		// Creating processed image
		BufferedImage processed = PicController.createProcessed(bufferedImg);
		byte[] processedInByte = Convertors.bufferedImageToByteArray(processed, types[1]);
		ByteBuffer processedbuf = ByteBuffer.wrap(processedInByte);

		Date currentTimestamp = new Date();

		Session session = this.cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement psInsertPic = session
				.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
		PreparedStatement psInsertPicToUser = session
				.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");

		session.execute(psInsertPic.bind(picid, imageInByte, thumbbuf,
				processedbuf, username, currentTimestamp, b.length,
				thumbInByte.length, processedInByte.length, contentType, name));
		session.execute(psInsertPicToUser.bind(picid, username,
				currentTimestamp));
		session.close();

	}
	
	public void updatePic(UUID picID, Entry<String, String> typeOfManipulation) {

		Pic processedPic = this.getPicFromDB(Convertors.DISPLAY_PROCESSED,
				picID);
		Pic thubmnailPic = this.getPicFromDB(Convertors.DISPLAY_THUMB, picID);
		PicController picModel = new PicController();
		BufferedImage proccesedBuff = picModel.manipulatePic(processedPic,
				typeOfManipulation);
		BufferedImage thumbnailBuff = picModel.manipulatePic(thubmnailPic,
				typeOfManipulation);

		byte[] thumbnailInBytes = null;
		byte[] processedInBytes = null;

		String thumbnailTypes[] = Convertors.SplitFiletype(thubmnailPic.getType());
		String processedTypes[] = Convertors.SplitFiletype(processedPic.getType());

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
				if(Globals.DEBUG)
					System.out.println("UUID" + picid.toString());
				pic.setUUID(picid);
				picList.add(pic);
			}
		}
		session.close();
		return picList;
	}

	public void deletePic(Pic pic) {
		if (pic == null) {
			System.err.println("Cannot delete Picture with pic is null");
			return;
		}
		
		UUID profilePicOfPicOwner = pic.getUser().getProfilepicId();
		//if picowner has a profilepic and this is his profilepic then delte his profilepic
		if(profilePicOfPicOwner !=null &&
		pic.getUser().getProfilepicId().equals(pic.getUUID())){
			pic.getUser().deleteProfilePic();
		}
		//TODO what if another user (not picowner) has this pic as his profilepic
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps1 = session
				.prepare("DELETE FROM pics WHERE picid=?");
		session.execute(ps1.bind(pic.getUUID()));
		PreparedStatement ps2 = session
				.prepare("DELETE FROM userpiclist WHERE picid=?");
		session.execute(ps2.bind(pic.getUUID()));
		PreparedStatement ps3 = session
				.prepare("DELETE FROM comments WHERE picid=?");
		session.execute(ps3.bind(pic.getUUID()));

		session.close();
	}
			
	public User getPicOwnerFromDB(UUID picid){
		SimpleStatement st = new SimpleStatement("SELECT user FROM pics WHERE picid=?",picid);
		Session session = this.cluster.connect(Keyspaces.KEYSPACE_NAME);
		ResultSet rs = session.execute(st);
		session.close();
		if(!rs.isExhausted()){
			Row row = rs.one();
			String username = row.getString(0);
			if(username != null && !username.isEmpty()){
				UserDbHelper dbHelper = new UserDbHelper();
				return dbHelper.getUserFromDb(username);
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
			String username = row.getString("user");
			String picname = row.getString("name");
			session.close();
			UserDbHelper dbHelper = new UserDbHelper();
			User user =  dbHelper.getUserFromDb(username);
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
}
