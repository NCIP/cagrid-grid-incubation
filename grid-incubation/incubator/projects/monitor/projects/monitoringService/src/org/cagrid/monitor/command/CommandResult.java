package org.cagrid.monitor.command;

public class CommandResult {

	private int returnCode;

	private String output;
	
	public CommandResult() {
		super();
	}
	
	public CommandResult(int returnCode, String output) {
		super();
		this.returnCode = returnCode;
		this.output = output;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}