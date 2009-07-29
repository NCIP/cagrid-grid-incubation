package org.cagrid.monitor.command;

import java.util.Collection;

public interface Command {
	
	public CommandResult run();
	
	public void setParameters(Collection<Parameter> parameters);

}