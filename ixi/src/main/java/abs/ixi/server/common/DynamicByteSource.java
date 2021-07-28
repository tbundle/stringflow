package abs.ixi.server.common;

import java.util.ArrayList;
import java.util.List;

public class DynamicByteSource {
	private List<ByteArray> byteSources;
	
	public DynamicByteSource() {
		this.byteSources = new ArrayList<>();
	}
	
	public DynamicByteSource(int initailCapacity) {
		this.byteSources = new ArrayList<>(initailCapacity);
	}
	
	public void addByteSource(ByteArray byteSource) {
		this.byteSources.add(byteSource);
	}
	
	public List<ByteArray> getByteSources() {
		return byteSources;
	}
}
