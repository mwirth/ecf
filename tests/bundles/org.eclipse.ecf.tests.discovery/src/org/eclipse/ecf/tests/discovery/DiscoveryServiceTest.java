/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/

package org.eclipse.ecf.tests.discovery;

import java.net.InetAddress;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.IServiceTypeListener;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.ServiceID;
import org.eclipse.ecf.discovery.service.IDiscoveryService;

public class DiscoveryServiceTest extends TestCase {
	
	static final String TEST_SERVICE_TYPE = "_ecftcp._tcp.local.";
	static final String TEST_PROTOCOL = "ecftcp";
	static final String TEST_HOST = "localhost";
	static final int TEST_PORT = 3282;
	static final String TEST_SERVICE_NAME = System.getProperty("user.name") + "." + TEST_PROTOCOL;
	
	protected IDiscoveryService discoveryInstance = null;
	
	protected IDiscoveryService getDiscoveryInstance() {
		return Activator.getDefault().getDiscoveryService();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		discoveryInstance = getDiscoveryInstance();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddServiceTypeListener() throws Exception {
		assertNotNull(discoveryInstance);
		discoveryInstance
		.addServiceTypeListener(new CollabServiceTypeListener());
	}
	
	public void testRegisterServiceType() throws Exception {
		discoveryInstance.registerServiceType(TEST_SERVICE_TYPE);
		System.out.println("registered service type "+TEST_SERVICE_TYPE+" waiting 5s");
		Thread.sleep(5000);
	}
	
	public void testRegisterService() throws Exception {
		Properties props = new Properties();
		String protocol = TEST_PROTOCOL;
		InetAddress host = InetAddress.getByName(TEST_HOST);
		int port = TEST_PORT;
		String svcName = System.getProperty("user.name") + "."
				+ protocol;
		ServiceInfo svcInfo = new ServiceInfo(host, new ServiceID(
				TEST_SERVICE_TYPE, svcName), port,
				0, 0, new ServiceProperties(props));
		discoveryInstance.registerService(svcInfo);
	}
	public final void testDiscovery() throws Exception {
		
		System.out.println("Discovery started.  Waiting 10s for discovered services");
		Thread.sleep(10000);
	}

	class CollabServiceTypeListener implements IServiceTypeListener {
		public void serviceTypeAdded(IServiceEvent event) {
			System.out.println("serviceTypeAdded(" + event + ")");
			IServiceID svcID = event.getServiceInfo().getServiceID();
			discoveryInstance.addServiceListener(svcID.getServiceType(),
					new CollabServiceListener());
			discoveryInstance.registerServiceType(svcID.getServiceType());
		}
	}
	class CollabServiceListener implements IServiceListener {
		public void serviceAdded(IServiceEvent event) {
			System.out.println("serviceAdded(" + event + ")");
			discoveryInstance.requestServiceInfo(event.getServiceInfo()
					.getServiceID(), 3000);
		}
		public void serviceRemoved(IServiceEvent event) {
			System.out.println("serviceRemoved(" + event + ")");
		}
		public void serviceResolved(IServiceEvent event) {
			System.out.println("serviceResolved(" + event + ")");
		}
	}

}
