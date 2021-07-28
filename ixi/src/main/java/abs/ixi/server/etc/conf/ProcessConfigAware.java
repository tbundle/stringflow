package abs.ixi.server.etc.conf;

/**
 * Process configurations Keys.
 */
public interface ProcessConfigAware {
	/**
	 * Core Component load filter property name.
	 */
	String _CORE_COMPONENT_LOAD_FILTER = "sf.core.component.load.filter";

	// ChannelEventDispatcher threadpool configuration keys
	String _NET_TRANSPORT_CORE_THREDAPOOL_SIZE = "sf.net.transport.threadpool.core.size";
	String _NET_TRANSPORT_MAX_THREADPOOL_SIZE = "sf.net.transport.threadpool.max.size";
	String _NET_TRANSPORT_THREADPOOL_KEEPALIVE_MINS = "sf.net.transport.threadpool.keep.alive.minutes";

	// Connection manager configuration properties
	String _XMPP_CONNECTION_MANAGER_ACTIVE = "sf.net.connection.manager.xmpp.active";
	String _XMPP_CONNECTION_MANAGER_PORT = "sf.net.connection.manager.xmpp.port";
	String _BOSH_CONNECTION_MANAGER_ACTIVE = "sf.net.connection.manager.bosh.active";
	String _BOSH_CONNECTION_MANAGER_PORT = "sf.net.connection.manager.bosh.port";
	String _MIME_CONNECTION_MANAGER_ACTIVE = "sf.net.connection.manager.mime.active";
	String _MIME_CONNECTION_MANAGER_PORT = "sf.net.connection.manager.mime.port";

	// IOController threadpool configuration keys
	String _IO_CONTROLLER_THREDAPOOL_CORE_SIZE = "sf.io.controller.threadpool.core.size";
	String _IO_CONTROLLER_THREADPOOL_MAX_SIZE = "sf.io.controller.threadpool.max.size";
	String _IO_CONTROLLER_THREADPOOL_KEEPALIVE = "sf.io.controller.threadpool.keep.alive.minutes";

	String _TASK_RUNNER_THREADPOOL_CORE_SIZE = "sf.task.runner.threadpool.core.size";
	String _TASK_RUNNER_THREADPOOL_MAX_SIZE = "sf.task.runner.threadpool.max.size";
	String _TASK_RUNNER_THREADPOOl_KEEPALIVE = "sf.task.runner.threadpool.keep.alive.minutes";

	String _TASK_RUNNER_SCHEDULE_THREADPOOL_CORE_SIZE = "sf.task.runner.schedule.threadpool.core.size";
	String _TASK_RUNNER_SCHEDULE_THREADPOOL_MAX_SIZE = "sf.task.runner.schedule.threadpool.max.size";
	String _TASK_RUNNER_SCHEDULE_THREADPOOL_KEEPALIVE = "sf.task.runner.schedule.threadpool.keep.alive.minutes";

	// Buffer Factory properties
	String _CACHED_BUF_FACTORY_CAPACITY = "sf.cached.bufferfactory.capacity";
	String _CACHED_BUF_FACTORY_BUFFER_SIZE = "sf.cached.bufferfactory.buffer.size";
	String _CACHED_BUF_FACTORY_BUFFER_TYPE = "sf.cached.bufferfactory.buffer.type";
	String _DEFAULT_BUF_FACTORY_BUFFER_SIZE = "sf.default.bufferfactory.buffer.size";

	// SASL props
	String SASL_ENGINE = "ixi.server.comp.sasl-engine";
	String SASL_ENGINE_NAME = "ixi.server.sasl-engine.name";
	String SASL_SERVER_FACTORY = "sf.sasl.serverfactory";

	String _FILE_SYSTEM_DIR_LENGTH = "";
	String _FILE_SYSTEM_DIR_LEVEL = "";

	// Swift Parser Configuration keys
	String MAX_ATTR_COUNT_LIMIT = "parser.swift.max.attr.count.limit";
	String PARSER_MAX_ATTRIBUTE_NUMBER = "parser.swift.max.attr.number.size";
	String PARSER_MAX_ELEMENT_NAME_SIZE = "parser.swift.max.element.name.size";
	String PARSER_MAX_ATTRIBUTE_NAME_SIZE = "parser.swift.max.attr.name.size";
	String PARSER_MAX_ATTRIBUTE_VALUE_SIZE = "parser.swift.max.attr.value.size";
	String PARSER_MAX_CDATA_SIZE = "parser.swift.max.cdata.size";

	/**
	 * Session watchdog frequency (in minutes)
	 */
	String _SESSION_WATCHDOG_FREQUENCY = "sf.session.watchdog.frequency";
	String _STREAM_MAX_RESUMPTION_TIME = "sf.stream.max.resumption.time";

	// server port on which server listens for control commands
	String _SERVER_CONTROL_PORT = "sf.server.control.port";

	String _TLS_SUPPORT = "sf.net.transport.tls";
}
