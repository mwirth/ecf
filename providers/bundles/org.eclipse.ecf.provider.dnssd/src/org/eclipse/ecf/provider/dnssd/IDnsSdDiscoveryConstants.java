/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

public interface IDnsSdDiscoveryConstants {

	/**
	 * Config admin key to define the default search path
	 */
	public final String CA_SEARCH_PATH = "searchPath"; //$NON-NLS-1$
	/**
	 * Config admin key to define the default resolver
	 */
	public final String CA_RESOLVER = "resolver"; //$NON-NLS-1$
	
	/**
	 * Config admin key to define the TSIG key to be used to sign requests 
	 */
	public final String CA_TSIG_KEY = "tsig-key"; //$NON-NLS-1$
	public final Object CA_TSIG_KEY_NAME = "tsig-key-name"; //$NON-NLS-1$

}
