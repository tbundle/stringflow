package abs.ixi.server.sys.admin;

import java.io.Serializable;
import java.util.Map;

/**
 * The class captures an server administrator command. Any admin command
 * consists of two things 1) String literal indicating command name 2) a map of
 * command arguments.
 * <p>
 * It may be a good idea to introduce to DSL to capture command argument list
 * and related attributes so that a command can be validated against its
 * definition.</>
 * 
 * @author Yogi
 *
 */
public class AdminCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private CommandName name;
    private Map<String, String> args;

    public AdminCommand(CommandName name) {
	this.name = name;
    }

    public AdminCommand(CommandName name, Map<String, String> args) {
	this.name = name;
	this.args = args;
    }

    /**
     * @return name of the command; it's an enumeration {@link CommandName}
     *         instance.
     */
    public CommandName name() {
	return this.name;
    }

    /**
     * @return args of the command.
     */
    public Map<String, String> args() {
	return args;
    }

    @Override
    public String toString() {
	return this.name.name() + "[" + this.name.val() + "]" + this.args;
    }

    /**
     * String literals representing a command name for server administrator
     */
    public enum CommandName {
	STOP_SERVER("stop"),

	START_SERVER("start"),

	SHOW("show");

	private String val;

	private CommandName(String val) {
	    this.val = val;
	}

	public String val() {
	    return this.val;
	}

	public static CommandName from(String val) {
	    for (CommandName cn : values()) {
		if (cn.val.equals(val)) {
		    return cn;
		}
	    }

	    throw new IllegalArgumentException(val + " is not a valid command name");
	}
    }
}
