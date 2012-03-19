/**
 * 
 */
package org.soluvas.fbstatus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ceefour
 * Handles Facebook OAuth authentication.
 */
@Named @ApplicationScoped
public class FacebookAuthenticator {

	private transient Logger log = LoggerFactory.getLogger(FacebookAuthenticator.class);
	private String appId = "";
	private String appSecret = "";
	private URI redirectUri;
	
	@PostConstruct public void init() throws IOException {
		// Load Facebook App credentials from ${OPENSHIFT_DATA_DIR}/fbstatus.properties if exists,
		// otherwise load from WEB-INF/fbstatus.properties
		String dataDir = System.getenv("OPENSHIFT_DATA_DIR");
		Properties props = new Properties();
		if (dataDir != null) {
			String propsFile = dataDir + "/fbstatus.properties";
			log.info("Loading credentials from file: {}", propsFile);
			FileReader fileReader = new FileReader(propsFile);
			props.load(fileReader);
		} else {
			log.info("Loading credentials from resource: {}", "/WEB-INF/fbstatus.properties");
			InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/fbstatus.properties");
			if (stream == null)
				throw new FileNotFoundException("Either ${OPENSHIFT_DATA_DIR}/fbstatus.properties or /WEB-INF/fbstatus.properties must exist.");
			props.load(stream);
		}
		appId = props.getProperty("facebook.app.id");
		appSecret = props.getProperty("facebook.app.secret");
		log.info("App ID: {}", appId);
		
		// Generate redirect URI
		final ExternalContext external = FacesContext.getCurrentInstance()
				.getExternalContext();
		redirectUri = UriBuilder.fromPath(external.encodeActionURL("/faces/fb_auth.xhtml"))
				.scheme(external.getRequestScheme())
				.host(external.getRequestServerName())
				.port(external.getRequestServerPort()).build();
	}
	
	public URI getRedirectUri() {
		return redirectUri;
	}

	public URI getAuthorizeUri() {
		return UriBuilder.fromUri("https://graph.facebook.com/oauth/authorize").queryParam("client_id", appId)
			.queryParam("redirect_uri", getRedirectUri())
			.queryParam("scope", "publish_stream").build();
	}
	
	public URI getAccessTokenUri(String authCode) {
		return UriBuilder
				.fromUri("https://graph.facebook.com/oauth/access_token")
				.queryParam("client_id", appId)
				.queryParam("redirect_uri", getRedirectUri())
				.queryParam("client_secret", appSecret)
				.queryParam("code", authCode).build();
	}

	/**
	 * Sets the authCode received from Facebook and exchanges it with the access token.
	 * @param authCode
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String fetchAccessToken(String authCode) throws ClientProtocolException, IOException {
		log.info("Retrieving access token using authCode {}", authCode);
		URI accessTokenUri = getAccessTokenUri(authCode);
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
		String accessToken = data.get(0).getValue();
		//log.debug("Access token = {}", fbAccessToken); // security risk?
		return accessToken;
	}
	
}
