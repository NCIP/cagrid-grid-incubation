package org.cagrid.monitor.command;

import java.lang.reflect.Constructor;

public class CommandUtils {
	public static Command loadCommand(String className) {
		Command commandToLoad = null;
		try {
			Class cls = Class.forName(className);
			Constructor ct = cls.getConstructor();
			commandToLoad = (Command) ct.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commandToLoad;
	}
}
