// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SaveDC TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SaveDC.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SaveDC extends EMFTag {

	public SaveDC() {
		super(33, 1);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		return this;
	}
}
