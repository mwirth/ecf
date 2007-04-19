/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn;

import java.io.IOException;
import java.net.ConnectException;

import org.eclipse.ecf.protocol.msn.internal.encode.ResponseCommand;

/**
 * <p>
 * The DispatchSession class connects to the dispatch server and retrieves the
 * address of the notification server for the {@link NotificationSession} class
 * to connect to. It currently does not serve any other purpose.
 * </p>
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
class DispatchSession extends Session {

	DispatchSession(MsnClient client) {
		super(client);
	}

	/**
	 * Creates a new DispatchSocket to connect to the given hostname and port.
	 * 
	 * @param hostname
	 *            the host to be connected to
	 * @param port
	 *            the corresponding port number
	 * @param client
	 *            the client that that invoked this dispatch session
	 * @throws IOException
	 *             If an I/O error occurs while attempting to open the
	 *             SocketChannel
	 */
	DispatchSession(String hostname, int port) throws IOException {
		super(hostname, port, null);
	}

	/**
	 * Connects to the server specified during this DispatchSession's
	 * construction and attempts to retrieve a viable notification server
	 * address.
	 * 
	 * @param username
	 *            the name to use for authentication
	 * @return a ResponseCommand which holds the information received from the
	 *         dispatch server
	 * @throws ConnectException
	 *             If the MSN servers did not respond as expected.
	 * @throws IOException
	 *             If an I/O error occurs during the read or write operations
	 */
	ResponseCommand connect(String username) throws ConnectException,
			IOException {
		write("VER", "MSNP11 CVR0"); //$NON-NLS-1$ //$NON-NLS-2$
		String input = super.read();
		if (!input.startsWith("VER")) { //$NON-NLS-1$
			// TODO: throw a more descriptive exception
			throw new ConnectException("The server did not respond properly.");
		}

		write("CVR", "0x040c winnt 5.1 i386 MSNMSGR 7.0.0813 msmsgs " //$NON-NLS-1$ //$NON-NLS-2$
				+ username);
		input = super.read();
		if (!input.startsWith("CVR")) { //$NON-NLS-1$
			// TODO: throw a more descriptive exception
			throw new ConnectException("The server did not respond properly.");
		}

		write("USR", "TWN I " + username); //$NON-NLS-1$ //$NON-NLS-2$
		return new ResponseCommand(super.read());
	}

	/**
	 * Attempts to authenticate the given username with the MSN dispatch server.
	 * 
	 * @param username
	 *            the username to be authenticated with
	 * @return the hostname of the notification server
	 * @throws ConnectException
	 *             If the MSN servers did not respond as expected.
	 * @throws IOException
	 *             If an I/O error occurs during the read or write operations
	 */
	String authenticate(String username) throws ConnectException, IOException {
		ResponseCommand received = connect(username);
		if (!received.getCommand().equals("XFR")) { //$NON-NLS-1$
			throw new ConnectException("The server did not respond properly.");
		}
		return received.getParam(2);
	}

}
