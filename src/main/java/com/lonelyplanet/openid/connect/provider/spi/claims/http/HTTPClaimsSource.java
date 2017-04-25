package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import java.io.InputStream;
import java.util.*;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.provider.spi.InitContext;
import com.nimbusds.openid.connect.provider.spi.claims.ClaimUtils;
import com.nimbusds.openid.connect.provider.spi.claims.ClaimsSource;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;


/**
 * Connector for retrieving OpenID Connect claims from an HTTP endpoint.
 */
@ThreadSafe
public class HTTPClaimsSource implements ClaimsSource {


	/**
	 * The configuration file path.
	 */
	public static final String CONFIG_FILE_PATH = "/WEB-INF/httpClaimsSource.properties";

	
	/**
	 * The HTTP connector configuration.
	 */
	private Configuration config;
	
	
	/**
	 * The Infinispan cache manager. TODO
	 */
	private EmbeddedCacheManager cacheManager;
	
	
	/**
	 * The Infinispan claims cache, claims keyed by subject identifiers. TODO
	 */
	private Cache<String,Map<String,?>> claimsCache;


	/**
	 * Creates a new HTTP claims source. It must be {@link #init
	 * initialised} before it can be used.
	 */
	public HTTPClaimsSource() { }


	/**
	 * Logs the overriding system properties.
	 */
	public static void logOverridingSystemProperties() {

		Properties sysProps = System.getProperties();

		StringBuilder sb = new StringBuilder();

		for (String key: sysProps.stringPropertyNames()) {

			if (! key.startsWith(Configuration.DEFAULT_PREFIX))
				continue;

			if (sb.length() > 0)
				sb.append(" ");

			sb.append(key);
		}

		Loggers.MAIN_LOG.info("[CSHTTP0010] Overriding system properties: {}", sb);
	}


	/**
	 * Loads the configuration.
	 *
	 * @param initContext The initialisation context. Must not be
	 *                    {@code null}.
	 *
	 * @return The configuration.
	 *
	 * @throws Exception If loading failed.
	 */
	private static Configuration loadConfiguration(final InitContext initContext)
		throws Exception {

		InputStream inputStream = initContext.getResourceAsStream(CONFIG_FILE_PATH);

		if (inputStream == null) {
			throw new Exception("Couldn't find HTTP claims source configuration file: " + CONFIG_FILE_PATH);
		}

		Properties props = new Properties();
		props.load(inputStream);

		// Override with any system properties
		logOverridingSystemProperties();
		props.putAll(System.getProperties());

		return new Configuration(props);
	}


	@Override
	public void init(final InitContext initContext)
		throws Exception {

		Loggers.MAIN_LOG.info("[CSHTTP0011] Initializing HTTP claims source...");

		config = loadConfiguration(initContext);

		config.log();

		if (! config.enable) {
			// stop initialisation
			return;
		}
		
		cacheManager = initContext.getInfinispanCacheManager();
	}
	
	
	/**
	 * Returns the HTTP claims source configuration.
	 *
	 * @return The HTTP claims source configuration, {@code null} if not
	 *         configured yet.
	 */
	public Configuration getConfiguration() {
		
		return config;
	}


	@Override
	public boolean isEnabled() {

		return config.enable;
	}


	@Override
	public Set<String> supportedClaims() {

		if (! config.enable) {
			// Empty set
			return Collections.unmodifiableSet(new HashSet<String>());
		}

		return Collections.unmodifiableSet(new HashSet<>(config.supportedClaims));
	}


	/**
	 * Resolves the individual requested claims from the specified
	 * requested claims and preferred locales.
	 *
	 * @param claims        The requested claims. May contain optional
	 *                      language tags. Must not be {@code null}.
	 * @param claimsLocales The preferred locales, {@code null} if not
	 *                      specified.
	 *
	 * @return The resolved individual requested claims.
	 */
	protected List<String> resolveRequestedClaims(final Set<String> claims,
						      final List<LangTag> claimsLocales) {

		// Use set to ensure no duplicates get into the collection
		Set<String> individualClaims = new HashSet<>();

		for (String claim: claims) {
			individualClaims.add(claim);
		}

		// Apply the preferred language tags if any
		individualClaims = ClaimUtils.applyLangTags(individualClaims, claimsLocales);

		return new ArrayList<>(individualClaims);
	}


	@Override
	public UserInfo getClaims(final Subject subject,
				  final Set<String> claims,
				  final List<LangTag> claimsLocales)
		throws Exception {

		if (! config.enable)
			return null;

		// Resolve the individual requested claims
		List<String> claimsToRequest = resolveRequestedClaims(claims, claimsLocales);
		
		// Construct the request JSON object
		JSONObject requestJSONObject = new JSONObject();
		requestJSONObject.put("sub", subject.getValue());
		requestJSONObject.put("claims", claimsToRequest);
		
		JSONObject claimsJSONObject;

		try {
			HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, config.url);
			httpRequest.setContentType(CommonContentTypes.APPLICATION_JSON);
			httpRequest.setAuthorization(config.apiAccessToken.toAuthorizationHeader());
			httpRequest.setConnectTimeout(config.connectTimeout);
			httpRequest.setReadTimeout(config.readTimeout);
			httpRequest.setQuery(requestJSONObject.toJSONString());
			
			HTTPResponse httpResponse = httpRequest.send();
			httpResponse.ensureStatusCode(200);
			claimsJSONObject = httpResponse.getContentAsJSONObject();

		} catch (Exception e) {
			Loggers.USERINFO_LOG.error("[CSHTTP0014] UserInfo retrieval error: {} {}", e.getMessage(), e);
			throw new Exception("Couldn't get UserInfo for subject \"" + subject + "\": " + e.getMessage(), e);
		}
		
		
		if (! claimsJSONObject.containsKey("sub")) {
			// Make sure we have subject in the JSON object before we
			// create a UserInfo object from it
			claimsJSONObject.put("sub", subject.getValue());
		}

		
		try {
			return new UserInfo(claimsJSONObject);

		} catch (Exception e) {
			Loggers.USERINFO_LOG.error("[CSHTTP0013] UserInfo construction error: {} {}", e.getMessage(), e);
			throw new Exception("Couldn't create UserInfo object: " + e.getMessage(), e);
		}
	}


	@Override
	public void shutdown()
		throws Exception {

		Loggers.MAIN_LOG.info("[CSHTTP0012] Shutting down HTTP claims source...");
	}
}
