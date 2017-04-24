package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class LoggersTest {
	

	@Test
	public void testLoggerNames() {
		
		assertEquals("MAIN", Loggers.MAIN_LOG.getName());
		assertEquals("USERINFO", Loggers.USERINFO_LOG.getName());
	}
}
