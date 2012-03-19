package org.soluvas.fbstatus;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
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
		log.info("Retrieving access token using authCode {}", authCode);
		URI accessTokenUri = facebookAuth.getAccessTokenUri(authCode);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet accessTokenReq = new HttpGet(accessTokenUri);
		HttpResponse response = client.execute(accessTokenReq);
		if (response.getStatusLine().getStatusCode() != 200)
			throw new IOException(String.format("GET %s throws HTTP Error %d: %s",
					accessTokenUri, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
		Scanner scanner = new Scanner(response.getEntity().getContent());
		ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
		URLEncodedUtils.parse(data, scanner, "UTF-8");
		// Probably due to non-existing Content-Encoding, this one is not working:
//		List<NameValuePair> data = URLEncodedUtils.parse(response.getEntity());
		// see: https://issues.apache.org/jira/browse/HTTPCLIENT-1175
		setFbAccessToken(data.get(0).getValue());
		//log.debug("Access token = {}", fbAccessToken); // security risk?
		
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
