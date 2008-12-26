/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.search;

import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.ecf.presence.search.ISearch;

/**
 * Implement ISearch for XMPP
 *
 */
public class XMPPSearch implements ISearch {

	protected IResultList resultList;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISearch#getResultList()
	 */
	public IResultList getResultList() {
		return resultList;
	}

	protected XMPPSearch(IResultList resultList) {
		this.resultList = resultList;
	}
	

}
