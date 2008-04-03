/*******************************************************************************
 * Copyright (c) 2006, 2008 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.protocol.bittorrent.TorrentConfiguration;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public final class BitTorrentProviderPlugin implements BundleActivator {

	/* package */static final String F_META_AREA = ".metadata"; //$NON-NLS-1$
	/* package */static final String F_PLUGIN_DATA = ".plugins"; //$NON-NLS-1$

	static final String CONTAINER_ID = "ecf.filetransfer.bittorrent"; //$NON-NLS-1$

	static final String NAMESPACE_ID = "ecf.bittorrent"; //$NON-NLS-1$

	private BundleContext context = null;

	private File getConfigurationPath() {
		Filter filter = null;
		try {
			filter = context.createFilter(Location.INSTANCE_FILTER);
		} catch (InvalidSyntaxException e) {
			// ignore this. It should never happen as we have tested the above
			// format.
		}
		ServiceTracker instanceLocationTracker = new ServiceTracker(context,
				filter, null);
		instanceLocationTracker.open();
		Location l = (Location) instanceLocationTracker.getService();
		instanceLocationTracker.close();
		if (l == null)
			return null;

		Path path = new Path(l.getURL().getPath());
		return path.append(F_META_AREA).append(F_PLUGIN_DATA)
				.append(context.getBundle().getSymbolicName()).toFile();
	}

	public void start(BundleContext context) throws Exception {
		this.context = context;
		TorrentConfiguration.setConfigurationPath(getConfigurationPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		this.context = null;
	}

}
