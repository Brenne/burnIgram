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
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class CommentDbHelper {
	
	private Cluster cluster;
	
	public CommentDbHelper() {
		this.cluster=CassandraHosts.getCluster();
	}
	
	public List<Comment> getCommentListByPicid(UUID picid){
		List<Comment> commentList = new LinkedList<Comment>();	
		Clause where = QueryBuilder.eq("picid", picid);
		ResultSet comments = selectFromComments(where);
		if(!comments.isExhausted()){
			for(Row commentRow : comments){
				Comment comment = rowToComment(commentRow);
				commentList.add(comment);
			}
		}
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
			PreparedStatement ps = session.prepare("INSERT INTO comments (id,picid, user, comment, comment_added )"
					+ " VALUES(?,?,?,?,?);");
			
			session.execute(ps.bind(	
					comment.getId(),
					comment.getPicid(),comment.getUser().getUsername(),
					comment.getContent(),comment.getCreated()));
			session.close();
			
			return true;
		}catch(Exception ex){
			System.err.println("Error in insertComment "+ex);
			return false;
		}
		
	}
	
	public Comment getCommentById(UUID commentId){
		Comment comment = null;
		Clause where = QueryBuilder.eq("id", commentId);
		ResultSet comments = selectFromComments(where);
		if(!comments.isExhausted()){
			comment = rowToComment(comments.one());
		}else{
			System.err.println("No comment fetched with Id "+ 
					commentId.toString());
		}
		return comment;
	}
	
	public void deleteCommentById(UUID commentId){
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		PreparedStatement ps = session.prepare("DELETE FROM comments WHERE id=?");
		session.execute(ps.bind(commentId));
		session.close();
	}
	
	private ResultSet selectFromComments(Clause where){
		Session session = cluster.connect(Keyspaces.KEYSPACE_NAME);
		Statement statement = QueryBuilder.select().all().from("comments").where(where);
		ResultSet comments = session.execute(statement);
		return comments;
		
	}
	
	private Comment rowToComment(Row row){
		Date added = row.getDate("comment_added");
		String username = row.getString("user");
		String contents = row.getString("comment");
		UUID commentId = row.getUUID("id");
		UUID picid = row.getUUID("picid");
		
		UserDbHelper dbHelper = new UserDbHelper();
		User user=  dbHelper.getUserFromDb(username);
		Comment comment = new Comment(commentId, picid, added, user, contents);
		return comment;
	}

}
