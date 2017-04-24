package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Log4j loggers.
 */
public final class Loggers {


	/**
	 * Main logger. Records general configuration, startup, shutdown and
	 * system messages.
	 */
	public static final Logger MAIN_LOG = LogManager.getLogger("MAIN");


	/**
	 * UserInfo endpoint logger.
	 */
	public static final Logger USERINFO_LOG = LogManager.getLogger("USERINFO");
}
