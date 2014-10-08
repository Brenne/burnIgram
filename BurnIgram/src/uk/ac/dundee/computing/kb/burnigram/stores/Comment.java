package uk.ac.dundee.computing.kb.burnigram.stores;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;
import uk.ac.dundee.computing.kb.burnigram.models.User;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Comment {
	
	private UUID picid;
	private Date created;
	private User user;
	private String content;
	
	public Comment(){
		
	}
	
	public Comment(UUID picid, Date created, User user, String content){
		this.picid = picid;
		this.created = created;
		this.user = user;
		this.content = content;
	}

	public UUID getPicid() {
		return picid;
	}

	public Date getCreated() {
		return created;
	}

	public User getUser() {
		return user;
	}

	public String getContent() {
		return content;
	}
	
	public static List<Comment> getCommentListFromDbByPicid(UUID picid){
		List<Comment> commentList = new LinkedList<Comment>();
		
		Session session = CassandraHosts.getCluster().connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("SELECT * FROM comments WHERE picid = ? ");
		ResultSet comments = session.execute(ps.bind(picid));
		if(!comments.isExhausted()){
			for(Row comment : comments){
				Date added = comment.getDate("comment_added");
				String username = comment.getString("user");
				String contents = comment.getString("comment");
				User user = User.initUserFromDB(username);
				Comment commentFromDB = new Comment(picid, added, user, contents);
				commentList.add(commentFromDB);
			}
		}
		session.close();
		return commentList;
	}
	
	public boolean insertCommentIntoDB(){
		if(this.picid == null || this.user == null || 
				this.user.getUsername() == null || this.user.getUsername().isEmpty()){
			//invalid values
			return false;
		}
		try{
			Session session = CassandraHosts.getCluster().connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement ps = session.prepare("INSERT INTO comments (picid, user, comment, comment_added )"
					+ " VALUES(?,?,?,?);");
			session.execute(ps.bind(this.getPicid(),this.getUser().getUsername(),
					this.getContent(),this.getCreated()));
			session.close();
			return true;
		}catch(Exception ex){
			System.err.println("Error in insertCommentIntoDB "+ex);
			return false;
		}
		
	}
	
}
