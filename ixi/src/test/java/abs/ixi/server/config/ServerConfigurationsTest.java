package abs.ixi.server.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import abs.ixi.server.etc.conf.ConfigurationException;
import abs.ixi.server.etc.conf.Configurations.Bundle;
import abs.ixi.server.etc.conf.ServerConfigurations;

/**
 * Unit tests for {@link ServerConfigurations}
 */
public class ServerConfigurationsTest {
	private ServerConfigurations configurations;

	@Before
	public void setUp() throws ConfigurationException {
		this.configurations = new ServerConfigurations();
		this.configurations.loadServerConfig();
	}

	@Test
	public void testSystemConfig() throws ConfigurationException {
		assertEquals("abstest.com", this.configurations.getSystemProperty("ixi.server.domain"));
		assertEquals("5222", this.configurations.getSystemProperty("ixi.server.port"));
		assertEquals("9001", this.configurations.getSystemProperty("ixi.server.control.port"));
	}

	@Test
	public void testProcessConfig() throws ConfigurationException {
		assertEquals("single", this.configurations.get("sf.mode", Bundle.PROCESS));
		assertEquals("internal", this.configurations.get("sf.session-manager.store.type", Bundle.PROCESS));
		assertEquals("false", this.configurations.get("sf.ioc.short-circuit", Bundle.PROCESS));
	}

//	@Test
//	public void testClusterConfig() throws ConfigurationException {
//		assertEquals("single", this.configurations.get("sf.mode", Bundle.CLUSTER));
//		assertEquals("internal", this.configurations.get("sf.session-manager.store.type", Bundle.CLUSTER));
//		assertEquals("false", this.configurations.get("sf.ioc.short-circuit", Bundle.CLUSTER));
//	}

}
