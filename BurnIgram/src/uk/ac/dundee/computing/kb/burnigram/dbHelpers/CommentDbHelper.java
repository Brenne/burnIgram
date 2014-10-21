package uk.ac.dundee.computing.kb.burnigram.dbHelpers;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import uk.ac.dundee.computing.kb.burnigram.beans.Comment;
import uk.ac.dundee.computing.kb.burnigram.beans.User;
import uk.ac.dundee.computing.kb.burnigram.lib.CassandraHosts;
import uk.ac.dundee.computing.kb.burnigram.lib.Keyspaces;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CommentDbHelper {
	
	private Cluster cluster;
	
	public CommentDbHelper() {
		this.cluster=CassandraHosts.getCluster();
	}
	
	public List<Comment> getCommentListFromDbByPicid(UUID picid){
		List<Comment> commentList = new LinkedList<Comment>();	
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("SELECT * FROM comments WHERE picid = ? ");
		ResultSet comments = session.execute(ps.bind(picid));
		if(!comments.isExhausted()){
			for(Row comment : comments){
				Date added = comment.getDate("comment_added");
				String username = comment.getString("user");
				String contents = comment.getString("comment");
				
				UserDbHelper dbHelper = new UserDbHelper();
				User user=  dbHelper.getUserFromDb(username);
				Comment commentFromDB = new Comment(picid, added, user, contents);
				commentList.add(commentFromDB);
			}
		}
		session.close();
		
		return commentList;
	}
	
	public boolean insertComment(Comment comment){
		if(comment.getPicid() == null || comment.getUser() == null || 
				comment.getUser().getUsername() == null || 
				comment.getUser().getUsername().isEmpty()){
			//invalid values
			return false;
		}
		try{
			
			Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
			PreparedStatement ps = session.prepare("INSERT INTO comments (picid, user, comment, comment_added )"
					+ " VALUES(?,?,?,?);");
			session.execute(ps.bind(		
					comment.getPicid(),comment.getUser().getUsername(),
					comment.getContent(),comment.getCreated()));
			session.close();
			
			return true;
		}catch(Exception ex){
			System.err.println("Error in insertComment "+ex);
			return false;
		}
		
	}

}
