package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Tests the configuration class.
 */
public class ConfigurationTest {


	@Test
	public void testParse()
		throws Exception {

		Properties props = new Properties();

		props.setProperty("op.httpClaimsSource.enable", "true");
		props.setProperty("op.httpClaimsSource.supportedClaims", "email, email_verified, name, given_name, family_name");
		props.setProperty("op.httpClaimsSource.url", "https://example.com/claims-source");
		props.setProperty("op.httpClaimsSource.connectTimeout", "250");
		props.setProperty("op.httpClaimsSource.readTimeout", "500");
		props.setProperty("op.httpClaimsSource.trustSelfSignedCerts", "false");
		props.setProperty("op.httpClaimsSource.apiAccessToken", "ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6");

		Configuration config = new Configuration(props);

		assertTrue(config.enable);
		assertTrue(config.supportedClaims.contains("email"));
		assertTrue(config.supportedClaims.contains("email_verified"));
		assertTrue(config.supportedClaims.contains("name"));
		assertTrue(config.supportedClaims.contains("given_name"));
		assertTrue(config.supportedClaims.contains("family_name"));
		assertEquals(5, config.supportedClaims.size());
		assertEquals("https://example.com/claims-source", config.url.toString());
		assertEquals(250, config.connectTimeout);
		assertEquals(500, config.readTimeout);
		assertFalse(config.trustSelfSignedCerts);
		assertEquals("ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6", config.apiAccessToken.getValue());
		assertNull(config.apiAccessToken.getScope());
	}
}
