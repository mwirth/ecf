package org.eclipse.ecf.internal.osgi.services.distribution;

import java.util.Map;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.AbstractTopologyManager;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;

public class BasicTopologyManagerImpl extends AbstractTopologyManager implements
		EndpointListener {

	private static final boolean allowLoopbackReference = new Boolean(
			System.getProperty(
					"org.eclipse.ecf.osgi.services.discovery.allowLoopbackReference", //$NON-NLS-1$
					"false")).booleanValue(); //$NON-NLS-1$

	private static final String defaultScope = System
			.getProperty("org.eclipse.ecf.osgi.services.discovery.endpointListenerScope"); //$NON-NLS-1$

	private String endpointListenerScope;
	private static final String ALL_SCOPE = "(" //$NON-NLS-1$
			+ org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID
			+ "=*)"; //$NON-NLS-1$

	BasicTopologyManagerImpl(BundleContext context) {
		super(context);
		if (defaultScope != null)
			this.endpointListenerScope = defaultScope;
		else if (allowLoopbackReference)
			endpointListenerScope = ALL_SCOPE;
		else {
			StringBuffer elScope = new StringBuffer("("); //$NON-NLS-1$
			// filter so that local framework uuid is not the same as local
			// value
			elScope.append("!("); //$NON-NLS-1$
			elScope.append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID);
			elScope.append("="); //$NON-NLS-1$
			elScope.append(getFrameworkUUID());
			elScope.append(")"); //$NON-NLS-1$
			elScope.append(")"); //$NON-NLS-1$
			endpointListenerScope = elScope.toString();
		}
	}

	String[] getScope() {
		return (ALL_SCOPE.equals(endpointListenerScope)) ? new String[] { endpointListenerScope }
				: new String[] { endpointListenerScope, ALL_SCOPE };
	}

	protected String getFrameworkUUID() {
		return super.getFrameworkUUID();
	}

	void exportRegisteredServices(String exportRegisteredSvcsClassname,
			String exportRegisteredSvcsFilter) {
		try {
			final ServiceReference[] existingServiceRefs = getContext()
					.getAllServiceReferences(exportRegisteredSvcsClassname,
							exportRegisteredSvcsFilter);
			// Now export as if the service was registering right now...i.e.
			// perform
			// export
			if (existingServiceRefs != null && existingServiceRefs.length > 0) {
				// After having collected all pre-registered services (with
				// marker prop) we are going to asynchronously remote them.
				// Registering potentially is a long-running operation (due to
				// discovery I/O...) and thus should no be carried out in the
				// OSGi FW thread. (https://bugs.eclipse.org/405027)
				new Thread(new Runnable() {
					public void run() {
						for (int i = 0; i < existingServiceRefs.length; i++) {
							// This method will check the service properties for
							// remote service props. If previously registered as
							// a
							// remote service, it will export the remote
							// service if not it will simply return/skip
							handleServiceRegistering(existingServiceRefs[i]);
						}
					}
				}, "BasicTopologyManagerPreRegSrvExporter").start(); //$NON-NLS-1$
			}
		} catch (InvalidSyntaxException e) {
			logError(
					"exportRegisteredServices", //$NON-NLS-1$
					"Could not retrieve existing service references for exportRegisteredSvcsClassname=" //$NON-NLS-1$
							+ exportRegisteredSvcsClassname
							+ " and exportRegisteredSvcsFilter=" //$NON-NLS-1$
							+ exportRegisteredSvcsFilter, e);
		}
	}

	// EndpointListener impl
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.remoteserviceadmin.EndpointListener#endpointAdded(org
	 * .osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
	 */
	public void endpointAdded(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint,
			String matchedFilter) {
		if (matchedFilter.equals(endpointListenerScope))
			handleEndpointAddedViaELScope(endpoint);
		else if (matchedFilter.equals(ALL_SCOPE))
			handleEndpointAddedViaAllScope(endpoint);
	}

	protected void handleEndpointAddedViaELScope(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
		if (endpoint instanceof EndpointDescription)
			handleECFEndpointAdded((EndpointDescription) endpoint);
		else
			handleNonECFEndpointAdded(this, endpoint);
	}

	protected void handleEndpointAddedViaAllScope(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
		if (endpoint instanceof EndpointDescription) {
			logError(
					"handleEndpointAddedViaAllScope", "Attempt to add ECF endpoint description directly.  endpoint=" + endpoint); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		} else
			advertiseEndpointDescription(endpoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.remoteserviceadmin.EndpointListener#endpointRemoved(
	 * org.osgi.service.remoteserviceadmin.EndpointDescription,
	 * java.lang.String)
	 */
	public void endpointRemoved(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint,
			String matchedFilter) {
		if (matchedFilter.equals(endpointListenerScope))
			handleEndpointRemovedViaELScope(endpoint);
		else if (matchedFilter.equals(ALL_SCOPE))
			handleEndpointRemovedViaAllScope(endpoint);
	}

	protected void handleEndpointRemovedViaELScope(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
		if (endpoint instanceof EndpointDescription)
			handleECFEndpointRemoved((EndpointDescription) endpoint);
		else
			handleNonECFEndpointRemoved(this, endpoint);
	}

	protected void handleEndpointRemovedViaAllScope(
			org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
		if (endpoint instanceof EndpointDescription) {
			logError(
					"handleEndpointRemovedViaAllScope", "Attempt to remove ECF endpoint description directly.  endpoint=" + endpoint); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		} else
			unadvertiseEndpointDescription(endpoint);
	}

	// EventListenerHook impl
	void event(ServiceEvent event, Map listeners) {
		handleEvent(event, listeners);
	}

	// RemoteServiceAdminListener impl
	void handleRemoteAdminEvent(RemoteServiceAdminEvent event) {
		handleRemoteServiceAdminEvent(event);
	}
}
