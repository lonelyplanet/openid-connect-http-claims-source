package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.internet.InternetAddress;

import static net.jadler.Jadler.*;
import static org.junit.Assert.*;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HTTPClaimsSourceTest {
	
	
	static class JSONObjectMatcher extends BaseMatcher<String> {
		
		
		private Subject expectedSubject;
		
		
		private Set<String> expectedClaimNames;
		
		
		private List<LangTag> expectedLangTags;
		
		
		public JSONObjectMatcher(Subject expectedSubject, Set<String> expectedClaimNames, List<LangTag> expectedLangTags) {
			assertNotNull(expectedSubject);
			this.expectedSubject = expectedSubject;
			assertNotNull(expectedClaimNames);
			this.expectedClaimNames = expectedClaimNames;
			this.expectedLangTags = expectedLangTags;
		}
		
		
		@Override
		public boolean matches(Object o) {
			
			try {
				JSONObject requestJSONObject = JSONObjectUtils.parse(o.toString());
				
				if (! expectedSubject.getValue().equals(requestJSONObject.get("sub"))) {
					return false;
				}
				
				List<String> claims = JSONObjectUtils.getStringList(requestJSONObject, "claims");
				
				if (! expectedClaimNames.containsAll(claims)) {
					return false;
				}
				
				if (expectedClaimNames.size() != claims.size()) {
					return false;
				}
				
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage());
			}
			
			return true;
		}
		
		
		@Override
		public void describeTo(Description description) {
			
		}
	}
	
	
	@Before
	public void setUp() {
		initJadler();
	}


	@After
	public void tearDown() {
		closeJadler();
	}
	
	
	private HTTPClaimsSource getConfiguredConnector()
		throws Exception {
		
		System.setProperty("op.httpClaimsSource.url", "http://localhost:" + port() + "/claims-source/");
		
		HTTPClaimsSource claimsSource = new HTTPClaimsSource();
		claimsSource.init(new MockServletInitContext());
		
		System.out.println("Test claims source URL: " + claimsSource.getConfiguration().url);
		
		return claimsSource;
	}
	
	
	@Test
	public void testSimpleClaimsRetrieval()
		throws Exception {
		
		Set<String> requestedClaims = new HashSet<>(Arrays.asList(
			"email",
			"email_verified",
			"name",
			"given_name",
			"family_name"
			));
		
		UserInfo resultToReturn = new UserInfo(new Subject("alice"));
		resultToReturn.setEmail(new InternetAddress("alice@wonderland.net"));
		resultToReturn.setEmailVerified(true);
		resultToReturn.setName("Alice Adams");
		resultToReturn.setGivenName("Alice");
		resultToReturn.setFamilyName("Adams");
		
		onRequest()
			.havingMethodEqualTo("POST")
			.havingPathEqualTo("/claims-source/")
			.havingBody(new JSONObjectMatcher(new Subject("alice"), requestedClaims, null))
			.respond()
			.withStatus(200)
			.withBody(resultToReturn.toJSONObject().toJSONString())
			.withEncoding(Charset.forName("UTF-8"))
			.withContentType("application/json; charset=UTF-8");
		
		HTTPClaimsSource claimsSource = getConfiguredConnector();
		
		assertTrue(claimsSource.isEnabled());
		
		UserInfo result = claimsSource.getClaims(new Subject("alice"), requestedClaims, null);
		
		assertEquals(resultToReturn.toJSONObject().toJSONString(), result.toJSONObject().toJSONString());
	}
}
