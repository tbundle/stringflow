package abs.ixi.server.io.multipart;


public enum MediaType {
	IMAGE_PNG("image/png"),
	IMAGE_JPEG("image/jpeg"),
	AUDIO_MPEG("audio/mpeg"),
	AUDIO_OGG("audio/ogg"),
	VIDEO_MP4("image/mp4");
	
	String type;
	private MediaType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	
	public static MediaType valueFrom(String type) throws IllegalArgumentException {
		for (MediaType mediaType : values()) {
			if (mediaType.getType().equalsIgnoreCase(type)) {
				return mediaType;
			}
		}

		throw new IllegalArgumentException("No MediaType for value [" + type + "]");
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
