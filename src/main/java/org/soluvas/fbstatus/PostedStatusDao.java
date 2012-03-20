package org.soluvas.fbstatus;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named @Stateless
public class PostedStatusDao {

	@PersistenceContext(unitName="primary") EntityManager em;
	
	public List<PostedStatus> findAll() {
		List<PostedStatus> result = em.createQuery(
				"SELECT ps FROM PostedStatus ps ORDER BY ps.created DESC", PostedStatus.class).getResultList();
		return result;
	}
	
	public PostedStatus createStatus(Long profileId, Long postId, String message) {
		PostedStatus status = new PostedStatus();
		status.setPostId(postId);
		status.setProfileId(profileId);
		status.setMessage(message);
		status.setCreated(new Date());
		em.merge(status);
		return status;
	}
}
