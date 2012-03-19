package org.soluvas.fbstatus;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named @Stateless
public class PostedStatusDao {

	@PersistenceContext EntityManager em;
	
	public List<PostedStatus> findAll() {
		List<PostedStatus> result = em.createQuery("SELECT ps FROM PostedStatus ps", PostedStatus.class).getResultList();
		return result;
	}
	
	public PostedStatus createStatus(Long profileId, Long postId, String message) {
		PostedStatus status = new PostedStatus();
		status.setPostId(postId);
		status.setProfileId(profileId);
		status.setMessage(message);
		em.merge(status);
		return status;
	}
}
