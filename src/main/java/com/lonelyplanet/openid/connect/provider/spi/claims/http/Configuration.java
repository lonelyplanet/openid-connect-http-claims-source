package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.nimbusds.common.config.ConfigurationException;
import com.nimbusds.common.config.LoggableConfiguration;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.thetransactioncompany.util.PropertyParseException;
import com.thetransactioncompany.util.PropertyRetriever;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * HTTP claims source configuration. It is typically derived from a Java key /
 * value properties file. The configuration is stored as public fields which 
 * become immutable (final) after their initialisation.
 *
 * <p>Example configuration properties:
 *
 * <pre>
 * op.httpClaimsSource.enable = true
 *
 * op.httpClaimsSource.supportedClaims = email, email_verified, name, given_name, family_name
 *
 * op.httpClaimsSource.url = https://example.com/claims-source
 * op.httpClaimsSource.connectTimeout = 250
 * op.httpClaimsSource.readTimeout = 250
 * op.httpClaimsSource.trustSelfSignedCerts = false
 * op.httpClaimsSource.apiAccessToken = ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6
 * </pre>
 */
public final class Configuration implements LoggableConfiguration {


	/**
	 * The default properties prefix.
	 */
	public static final String DEFAULT_PREFIX = "op.httpClaimsSource.";


	/**
	 * Enables / disables the HTTP claims source.
	 *
	 * <p>Property key: [prefix]enable
	 */
	public final boolean enable;
	
	
	/**
	 * The names of the supported (standard and custom) OpenID Connect
	 * claims.
	 *
	 * <p>Property key: [prefix]supportedClaims
	 */
	public final List<String> supportedClaims;
	
	
	/**
	 * The URL of the HTTP endpoint for sourcing the OpenID Connect claims.
	 *
	 * <p>Property key: [prefix]url
	 */
	public final URL url;
	
	
	/**
	 * The timeout in milliseconds for establishing HTTP connections. If
	 * zero the underlying HTTP client library will handle this value.
	 *
	 * <p>Property key: [prefix]connectTimeout
	 */
	public final int connectTimeout;
	
	
	/**
	 * The timeout in milliseconds for obtaining HTTP responses after
	 * connection. If zero the underlying HTTP client library will handle
	 * this value.
	 *
	 * <p>Property key: [prefix]readTimeout
	 */
	public final int readTimeout;
	
	
	/**
	 * Determines whether to accept self-signed certificates presented by
	 * the HTTP server (for secure SSL or StartTLS connections).
	 * Self-signed certificates are not trusted by default.
	 *
	 * <p>Property key: [prefix]trustSelfSignedCerts
	 */
	public final boolean trustSelfSignedCerts;
	
	
	/**
	 * Access token of type bearer (non-expiring) for accessing the HTTP
	 * endpoint. Should contain at least 32 random alphanumeric characters
	 * to make brute force guessing impractical.
	 *
	 * <p>Property key: [prefix]apiAccessToken
	 */
	public final BearerAccessToken apiAccessToken;


	/**
	 * Creates a new HTTP claims source configuration from the specified
	 * properties.
	 *
	 * @param props The properties. Must not be {@code null}.
	 *
	 * @throws ConfigurationException On a missing or invalid property.
	 */
	public Configuration(final Properties props)
		throws ConfigurationException {

		PropertyRetriever pr = new PropertyRetriever(props);

		try {
			enable = pr.getBoolean(DEFAULT_PREFIX + "enable");
			
			if (! enable) {
				supportedClaims = Collections.emptyList();
				url = null;
				connectTimeout = 0;
				readTimeout = 0;
				trustSelfSignedCerts = false;
				apiAccessToken = null;
				return;
			}
			
			String claimsStrig = pr.getString(DEFAULT_PREFIX + "supportedClaims");
			
			List<String> claimsList = new LinkedList<>();
			
			for (String claimName: StringUtils.split(claimsStrig, ", ")) {
				claimsList.add(claimName);
			}
			
			if (claimsList.isEmpty()) {
				throw new PropertyParseException("Missing supported claims", DEFAULT_PREFIX + "supportedClaims");
			}
			
			supportedClaims = Collections.unmodifiableList(claimsList);

			url = pr.getURL(DEFAULT_PREFIX + "url");
			
			connectTimeout = pr.getInt(DEFAULT_PREFIX + "connectTimeout");
			
			readTimeout = pr.getInt(DEFAULT_PREFIX + "readTimeout");
			
			trustSelfSignedCerts = pr.getOptBoolean(DEFAULT_PREFIX + "trustSelfSignedCerts", false);

			apiAccessToken = new BearerAccessToken(pr.getString(DEFAULT_PREFIX + "apiAccessToken"));

		} catch (PropertyParseException e) {

			throw new ConfigurationException(e.getMessage() + ": Property: " + e.getPropertyKey());
		}
	}


	/**
	 * Logs the configuration details at INFO level. Properties that may
	 * adversely affect security are logged at WARN level.
	 */
	@Override
	public void log() {

		Logger log = LogManager.getLogger("MAIN");

		log.info("[CSHTTP0000] HTTP claims source configuration:");
		log.info("[CSHTTP0001] HTTP claims source enabled: {}", enable);
		
		if (! enable) {
			return;
		}
		
		log.info("[CSHTTP0002] HTTP claims source supported claims: {}", supportedClaims);
		
		if ("https".equalsIgnoreCase(url.getAuthority())) {
			log.info("[CSHTTP0003] HTTP claims source URL: {}", url);
		} else {
			log.warn("[CSHTTP0003] HTTP claims source URL (unsecured, consider using HTTPS): {}", url);
		}
		log.info("[CSHTTP0004] HTTP claims source connect timeout : {} ms", connectTimeout);
		log.info("[CSHTTP0005] HTTP claims source read timeout : {} ms", readTimeout);
		log.info("[CSHTTP0006] HTTP claims source self-signed SSL certificates trusted : {}", trustSelfSignedCerts);
	}
}

