package org.soluvas.fbstatus;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named @SessionScoped
public class UserSession implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient Logger log = LoggerFactory.getLogger(UserSession.class);
	@Inject FacebookAuthenticator facebookAuth;
	private String fbAccessToken;
	
	public String getFacebookAuthCode() {
		return null;
	}
	
	/**
	 * Sets the authCode received from Facebook and exchanges it with the access token.
	 * @param authCode
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void setFacebookAuthCode(String authCode) throws ClientProtocolException, IOException {
		String accessToken = facebookAuth.fetchAccessToken(authCode);
		setFbAccessToken(accessToken);
		
		log.info("Access token received, redirecting to Post Status page");
		ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
		external.redirect(external.encodeActionURL("/faces/post.xhtml"));
	}

	public String getFbAccessToken() {
		return fbAccessToken;
	}

	public void setFbAccessToken(String fbAccessToken) {
		this.fbAccessToken = fbAccessToken;
	}
	
}
