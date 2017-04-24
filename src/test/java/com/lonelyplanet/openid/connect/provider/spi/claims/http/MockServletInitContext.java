package com.lonelyplanet.openid.connect.provider.spi.claims.http;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.provider.spi.ServiceContext;
import com.nimbusds.openid.connect.provider.spi.ServletInitContext;


public class MockServletInitContext extends ServletInitContext {
	
	
	public MockServletInitContext() {
		
		super(new ServletContext() {
			@Override
			public String getContextPath() {
				return null;
			}
			
			
			@Override
			public ServletContext getContext(String s) {
				return null;
			}
			
			
			@Override
			public int getMajorVersion() {
				return 0;
			}
			
			
			@Override
			public int getMinorVersion() {
				return 0;
			}
			
			
			@Override
			public int getEffectiveMajorVersion() {
				return 0;
			}
			
			
			@Override
			public int getEffectiveMinorVersion() {
				return 0;
			}
			
			
			@Override
			public String getMimeType(String s) {
				return null;
			}
			
			
			@Override
			public Set<String> getResourcePaths(String s) {
				return null;
			}
			
			
			@Override
			public URL getResource(String s) throws MalformedURLException {
				return null;
			}
			
			
			@Override
			public InputStream getResourceAsStream(String path) {
				
				if (path.equals(HTTPClaimsSource.CONFIG_FILE_PATH)) {
					
					try {
						return new FileInputStream("test.properties");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				
				return null;
			}
			
			
			@Override
			public RequestDispatcher getRequestDispatcher(String s) {
				return null;
			}
			
			
			@Override
			public RequestDispatcher getNamedDispatcher(String s) {
				return null;
			}
			
			
			@Override
			public Servlet getServlet(String s) throws ServletException {
				return null;
			}
			
			
			@Override
			public Enumeration<Servlet> getServlets() {
				return null;
			}
			
			
			@Override
			public Enumeration<String> getServletNames() {
				return null;
			}
			
			
			@Override
			public void log(String s) {
				
			}
			
			
			@Override
			public void log(Exception e, String s) {
				
			}
			
			
			@Override
			public void log(String s, Throwable throwable) {
				
			}
			
			
			@Override
			public String getRealPath(String s) {
				return null;
			}
			
			
			@Override
			public String getServerInfo() {
				return null;
			}
			
			
			@Override
			public String getInitParameter(String s) {
				return null;
			}
			
			
			@Override
			public Enumeration<String> getInitParameterNames() {
				return null;
			}
			
			
			@Override
			public boolean setInitParameter(String s, String s1) {
				return false;
			}
			
			
			@Override
			public Object getAttribute(String s) {
				return null;
			}
			
			
			@Override
			public Enumeration<String> getAttributeNames() {
				return null;
			}
			
			
			@Override
			public void setAttribute(String s, Object o) {
				
			}
			
			
			@Override
			public void removeAttribute(String s) {
				
			}
			
			
			@Override
			public String getServletContextName() {
				return null;
			}
			
			
			@Override
			public ServletRegistration.Dynamic addServlet(String s, String s1) {
				return null;
			}
			
			
			@Override
			public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
				return null;
			}
			
			
			@Override
			public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
				return null;
			}
			
			
			@Override
			public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
				return null;
			}
			
			
			@Override
			public ServletRegistration getServletRegistration(String s) {
				return null;
			}
			
			
			@Override
			public Map<String, ? extends ServletRegistration> getServletRegistrations() {
				return null;
			}
			
			
			@Override
			public FilterRegistration.Dynamic addFilter(String s, String s1) {
				return null;
			}
			
			
			@Override
			public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
				return null;
			}
			
			
			@Override
			public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
				return null;
			}
			
			
			@Override
			public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
				return null;
			}
			
			
			@Override
			public FilterRegistration getFilterRegistration(String s) {
				return null;
			}
			
			
			@Override
			public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
				return null;
			}
			
			
			@Override
			public SessionCookieConfig getSessionCookieConfig() {
				return null;
			}
			
			
			@Override
			public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
				
			}
			
			
			@Override
			public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
				return null;
			}
			
			
			@Override
			public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
				return null;
			}
			
			
			@Override
			public void addListener(String s) {
				
			}
			
			
			@Override
			public <T extends EventListener> void addListener(T t) {
				
			}
			
			
			@Override
			public void addListener(Class<? extends EventListener> aClass) {
				
			}
			
			
			@Override
			public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
				return null;
			}
			
			
			@Override
			public JspConfigDescriptor getJspConfigDescriptor() {
				return null;
			}
			
			
			@Override
			public ClassLoader getClassLoader() {
				return null;
			}
			
			
			@Override
			public void declareRoles(String... strings) {
				
			}
		});
	}
	
	
	@Override
	public Issuer getOPIssuer() {
		return new Issuer("https://c2id.com");
	}
	
	
	@Override
	public URI getTokenEndpointURI() {
		return URI.create("https://c2id.com/token");
	}
	
	
	@Override
	public ServiceContext getServiceContext() {
		return null;
	}
}
