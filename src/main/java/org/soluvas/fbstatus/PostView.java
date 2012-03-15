package org.soluvas.fbstatus;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

@SessionScoped @Named
public class PostView implements Serializable {

	static final long serialVersionUID = 1L;
	private transient Logger log = LoggerFactory.getLogger(PostView.class);
//	@Inject Messages messages;
	String accessToken;
	String message;
	String lastStatusId;
	String lastStatusUrl;
	
	public void manualPost() {
		FacesContext faces = FacesContext.getCurrentInstance();
		DefaultFacebookClient client = new DefaultFacebookClient(accessToken);
		FacebookType response = client.publish("me/feed", FacebookType.class, Parameter.with("message", message));
		lastStatusId = response.getId();
		log.info("Got Post ID: {}", lastStatusId);
		Matcher matcher = Pattern.compile("(\\d+)_(\\d+)").matcher(lastStatusId);
		if (matcher.matches()) {
			String userId = matcher.group(1);
			String postId = matcher.group(2);
			lastStatusUrl = "http://www.facebook.com/" + userId + "/posts/" + postId;
			log.info("Post URL is {}", lastStatusUrl);
			faces.addMessage(null, new FacesMessage(
					"Status posted to Facebook with ID: " + lastStatusId, ""));
//			messages.info("Status posted to Facebook with ID: {0}", lastStatusId);
			message = "";
		} else {
			log.error("Cannot parse Post ID: {}", lastStatusId);
			faces.addMessage(null, new FacesMessage(faces.getMaximumSeverity(),
					"Cannot parse Post ID: " + lastStatusId, ""));
		}
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getLastStatusUrl() {
		return lastStatusUrl;
	}

	public void setLastStatusUrl(String lastStatusUrl) {
		this.lastStatusUrl = lastStatusUrl;
	}

	public String getLastStatusId() {
		return lastStatusId;
	}

	public void setLastStatusId(String lastStatusId) {
		this.lastStatusId = lastStatusId;
	}
	
}
