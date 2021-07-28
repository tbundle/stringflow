package abs.ixi.server.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppDeploymentDescriptor {
	private String name;
	private String publishAddress;
	
	// map of life-cycle event and callback listeners calss name
	private Map<String, String> callbacks; 
	
	private List<AppfrontInfo> appfronts; 
	

	public AppDeploymentDescriptor() {
		this.callbacks = new HashMap<>();
		this.appfronts = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublishAddress() {
		return publishAddress;
	}

	public void setPublishAddress(String publishAddress) {
		this.publishAddress = publishAddress;
	}

	public Map<String, String> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(Map<String, String> callbacks) {
		this.callbacks = callbacks;
	}

	public List<AppfrontInfo> getAppfronts() {
		return appfronts;
	}

	public void setAppfronts(List<AppfrontInfo> appfronts) {
		this.appfronts = appfronts;
	}
	
	public void addAppfrontInfo(AppfrontInfo appfrontInfo) {
		this.appfronts.add(appfrontInfo);
	}
	
	public class AppfrontInfo {
		private String uriSegement;
		private String appfrontClassName;
		private List<String> requestReceivers;
		
		protected AppfrontInfo() {
			this(null, null);
		}
		
		public AppfrontInfo(String uriSegement, String appfrontClassName) {
			this.uriSegement = uriSegement;
			this.appfrontClassName = appfrontClassName;
			this.requestReceivers = new ArrayList<>();
		}

		public String getUriSegement() {
			return uriSegement;
		}

		public void setUriSegement(String uriSegement) {
			this.uriSegement = uriSegement;
		}

		public String getAppfrontClassName() {
			return appfrontClassName;
		}

		public void setAppfrontClassName(String appfrontClassName) {
			this.appfrontClassName = appfrontClassName;
		}

		public List<String> getRequestReceivers() {
			return requestReceivers;
		}

		public void setRequestReceivers(List<String> requestReceivers) {
			this.requestReceivers = requestReceivers;
		}
		
		public void addRequestReceiver(String receiver) {
			this.requestReceivers.add(receiver);
		}
		
	}

}
