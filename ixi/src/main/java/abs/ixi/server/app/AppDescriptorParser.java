package abs.ixi.server.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import abs.ixi.server.ServerStartupException;
import abs.ixi.server.app.AppDeploymentDescriptor.AppfrontInfo;
import abs.ixi.util.StringUtils;

public class AppDescriptorParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppDescriptorParser.class);

	private static final String NAME_TAG = "name";
	private static final String PUBLISH_ADDRESS_TAG = "publishAddress";
	private static final String AFTER_INIT_TAG = "after-init";
	private static final String BEFORE_START_TAG = "before-start";
	private static final String BEFORE_SHUTDOWN_TAG = "before-shutdown";
	private static final String APP_FRONT_CLASS_TAG = "appfront-class";
	private static final String URI_SEGMENT_TAG = "uri-segment";
	private static final String CALLBACK_TAG = "callback";
	private static final String APPFRONT_TAG = "appfront";
	private static final String LIST_TAG = "list";
	private static final String REQUEST_RECEIVER_TAG = "request-receiver";

	private DocumentBuilder docBuilder;

	public AppDescriptorParser() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = dbFactory.newDocumentBuilder();
	}

	public AppDeploymentDescriptor parse(String path)
			throws ServerStartupException, ParserConfigurationException, SAXException, IOException {

		AppDeploymentDescriptor descriptor = new AppDeploymentDescriptor();

		Path deployXml = Paths.get(path);

		if (!Files.exists(deployXml)) {
			LOGGER.error("Deploy.xml not found, Shutting down server ");
			throw new ServerStartupException("Deploy.xml not found");
		}

		File fXmlFile = new File(path);

		Document doc = docBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			
			if (child.getNodeType() == 3) {
				continue;
			}
			
			if(StringUtils.safeEquals(child.getNodeName(), NAME_TAG)) {
				descriptor.setName(child.getTextContent().trim());
				
			} else if(StringUtils.safeEquals(child.getNodeName(), PUBLISH_ADDRESS_TAG)) {
				descriptor.setPublishAddress(child.getTextContent());
				
			} else if(StringUtils.safeEquals(child.getNodeName(), CALLBACK_TAG)) {
				NodeList nodeList1 = child.getChildNodes();
				
				for (int j = 0; j < nodeList1.getLength(); j++) {
					Node child1 = nodeList1.item(j);
					
					if (child1.getNodeType() == 3) {
						continue;
					}
					
					if(StringUtils.safeEquals(child1.getNodeName(), AFTER_INIT_TAG)) {
						descriptor.getCallbacks().put(AFTER_INIT_TAG, child1.getTextContent().trim());
						
					} else if(StringUtils.safeEquals(child1.getNodeName(), BEFORE_START_TAG)) {
						descriptor.getCallbacks().put(BEFORE_START_TAG, child1.getTextContent().trim());
						
					} else if(StringUtils.safeEquals(child1.getNodeName(), BEFORE_SHUTDOWN_TAG)) {
						descriptor.getCallbacks().put(BEFORE_SHUTDOWN_TAG, child1.getTextContent().trim());
						
					}
				}
				
			} else if(StringUtils.safeEquals(child.getNodeName(), APPFRONT_TAG)) {
				NodeList nodeList1 = child.getChildNodes();
				AppfrontInfo appfrontInfo = descriptor.new AppfrontInfo();
				 
				for (int j = 0; j < nodeList1.getLength(); j++) {
					Node child1 = nodeList1.item(j);
					
					if (child1.getNodeType() == 3) {
						continue;
					}
					
					if(StringUtils.safeEquals(child1.getNodeName(), APP_FRONT_CLASS_TAG)) {
						appfrontInfo.setAppfrontClassName(child1.getTextContent().trim());
						
					} else if(StringUtils.safeEquals(child1.getNodeName(), URI_SEGMENT_TAG)) {
						appfrontInfo.setUriSegement(child1.getTextContent().trim());
						
					} else if(StringUtils.safeEquals(child1.getNodeName(), LIST_TAG)) {
						NodeList nodeList2 = child1.getChildNodes();
						
						for(int k = 0; k < nodeList2.getLength(); k++) {
							Node child2 = nodeList2.item(k);
							
							if (child2.getNodeType() == 3) {
								continue;
							}
							
							if(StringUtils.safeEquals(child2.getNodeName(), REQUEST_RECEIVER_TAG)) {
								appfrontInfo.addRequestReceiver(child2.getTextContent().trim());
							}
						}
					}
				}
				
				descriptor.addAppfrontInfo(appfrontInfo);
			}
	
		}
		
		return descriptor;
	}
}