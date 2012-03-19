package org.soluvas.fbstatus;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * History of posted Facebook status.
 * @author ceefour
 */
@Entity
public class PostedStatus {

	@Id @GeneratedValue
	Long id;
	Long profileId;
	Long postId;
	String message;
	@Temporal(TemporalType.TIMESTAMP)
	Date created;
	
	public String getProfileUri() {
		return "http://www.facebook.com/profile.php?id=" + getProfileId();
	}
	
	public String getPostUri() {
		return "http://www.facebook.com/" + getProfileId() + "/" + getPostId();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProfileId() {
		return profileId;
	}
	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
}
