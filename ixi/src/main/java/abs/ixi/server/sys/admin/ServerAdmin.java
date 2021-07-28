package abs.ixi.server.sys.admin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.sys.admin.AdminCommand.CommandName;

/**
 * A class which exposes utility methods to adminstrate server at runtime.
 * 
 * @author Yogi
 *
 */
public class ServerAdmin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAdmin.class);

    /**
     * Shutdown port of the server. Although it is possible to change it in
     * server configurations; however the {@link ServerAdmin#shutdownServer()}
     * will not be able to reach server causing script to kill the process.
     * <p>
     * At some point, we must start accepting shutdown port in server "stop"
     * command arguments so that user can specify shutdown port if it was
     * chnaged.
     * </p>
     */
    public static final int SERVER_CONTROL_PORT = 9001;

    /**
     * Shutdown running server instance.
     */
    public void stopServer(Map<String, String> args) {
	LOGGER.info("Received server shutdown command");
	AdminCommand ac = new AdminCommand(CommandName.STOP_SERVER, args);
	sendCommandToServer(ac);
    }

    private void sendCommandToServer(AdminCommand ac) {
	Socket socket = new Socket();

	try {

	    socket.connect(new InetSocketAddress("localhost", SERVER_CONTROL_PORT));

	    ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

	    os.writeObject(ac);
	    
	    os.flush();

	} catch (UnknownHostException e) {
	    System.out.println("Server Host is unreachable");

	} catch (IOException e) {
	    System.out.println("Could not write on server socket");

	} finally {
	    try {
		socket.close();
	    } catch (IOException e) {
		System.out.println("Failed to close socket gracefully");
	    }
	}
    }

    /**
     * Server startup hook
     */
    public void startServer() {
	System.out.println("Starting server");
    }

    /**
     * Take args and prepare {@link AdminCommand}. And execute it.
     * 
     * @param args
     */
    public static void executeCommand(String[] args) {
	ServerAdmin admin = new ServerAdmin();
	CommandName cmd = CommandName.from(args[0]);

	Map<String, String> map = new HashMap<>();

	for (int i = 1; i < args.length; i++) {
	    String key = args[i++];
	    String value = null;

	    if (i < args.length) {
		value = args[i];
	    }

	    map.put(key, value);
	}

	switch (cmd) {
	case START_SERVER:
	    break;
	case STOP_SERVER:
	    admin.stopServer(map);
	    break;
	case SHOW:
	    admin.show(map);
	    break;
	}

    }

    /**
     * Execute show command to show server state.
     */
    private void show(Map<String, String> args) {
	System.out.println("Show command received with args ...");
	
	for(Entry<String, String> arg : args.entrySet()) {
	    System.out.println("Arg key : " + arg.getKey() + " Value : " + arg.getValue());
	}
	
	AdminCommand ac = new AdminCommand(CommandName.SHOW, args);
	sendCommandToServer(ac);
    }

    public static void main(String[] args) {
	if (args == null || args.length < 1) {
	    System.out.println("No admin command found");

	} else {
	    executeCommand(args);
	}
    }

}
