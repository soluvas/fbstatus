/**
 * 
 */
package org.soluvas.fbstatus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

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
	private String appKey = "";
	
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
		appKey = props.getProperty("facebook.app.key");
		log.info("App ID: {}", appId);
	}
	
	public URI getRedirectUri() {
		final ExternalContext external = FacesContext.getCurrentInstance()
				.getExternalContext();
		return UriBuilder.fromPath(external.encodeActionURL("/fb_auth"))
				.scheme(external.getRequestScheme())
				.host(external.getRequestServerName())
				.port(external.getRequestServerPort()).build();
	}

	public URI getAuthorizeUri() {
		return UriBuilder.fromUri("https://graph.facebook.com/oauth/authorize").queryParam("client_id", appId)
			.queryParam("redirect_uri", getRedirectUri())
			.queryParam("scope", "publish_stream").build();
	}
}
