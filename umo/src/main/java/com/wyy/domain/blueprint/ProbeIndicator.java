package com.wyy.domain.blueprint;

import java.io.Serializable;

public class ProbeIndicator implements Serializable {

	private static final long serialVersionUID = 2726354988535931970L;

	private String value;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
