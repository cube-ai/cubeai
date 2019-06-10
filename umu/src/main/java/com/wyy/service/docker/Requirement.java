package com.wyy.service.docker;

public class Requirement {
	public String name;
	public String version;
	public String operator;

	@Override
	public String toString() {
		if (version != null) {
			return "[name:" + name + ", version:" + version + ", operator:" + operator + "]";
		} else {
			return "[name:" + name + "]";
		}
	}

}
