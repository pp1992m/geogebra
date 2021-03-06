package org.geogebra.common.gui.dialog.options;

import java.util.Arrays;

public abstract class OptionsCAS {

	/** available CAS timeout options (will be reused in OptionsCAS) */
	final private static Integer[] cbTimeoutOptions = { 5, 10, 20, 30, 60 };

	/**
	 * @param integer
	 *            option index
	 * @return timeout in seconds
	 */
	public static Integer getTimeoutOption(long integer) {
		for (int i = 0; i < cbTimeoutOptions.length; i++) {
			if (cbTimeoutOptions[i].intValue() == integer) {
				return cbTimeoutOptions[i];
			}
		}
		return cbTimeoutOptions[0];
	}

	public static Integer[] getTimeoutOptions() {
		return Arrays.copyOf(cbTimeoutOptions, cbTimeoutOptions.length);
	}

}
