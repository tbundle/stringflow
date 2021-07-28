package abs.sf.ads.entity;

import org.springframework.data.annotation.Id;

public class User implements Entity {
	public static final String JID = "jid";
	public static final String FIRST_NAME = "firstName";
	public static final String MIDDLE_NAME = "middleName";
	public static final String LAST_NAME = "lastName";
	public static final String NICK_NAME = "nickName";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	public static final String GENDER = "gender";
	public static final String BDAY = "bday";
	public static final String AVTAR = "avtar";
	public static final String ADDRESS = "address";
	public static final String DESCRIPTION = "description";

	@Id
	private String jid;
	private String firstName;
	private String middleName;
	private String lastName;
	private String nickName;
	private String phone;
	private String email;
	private String gender;
	private String bday;
	private UserAvtar avtar;
	private Address address;
	private String description;

	public User() {

	}

	public User(String jid) {
		this.jid = jid;
	}

	public User(String jid, String firstName, String email) {
		this.jid = jid;
		this.firstName = firstName;
		this.email = email;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBday() {
		return bday;
	}

	public void setBday(String bday) {
		this.bday = bday;
	}

	public UserAvtar getAvtar() {
		return avtar;
	}

	public void setAvtar(UserAvtar avtar) {
		this.avtar = avtar;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public class UserAvtar {
		private String base64EncodedImage;
		private String imageType;

		public UserAvtar() {

		}

		public UserAvtar(String base64Image, String imageType) {
			this.base64EncodedImage = base64Image;
			this.imageType = imageType;
		}

		public String getBase64EncodedImage() {
			return base64EncodedImage;
		}

		public void setBase64EncodedImage(String base64Image) {
			this.base64EncodedImage = base64Image;
		}

		public String getImageType() {
			return imageType;
		}

		public void setImageType(String imageType) {
			this.imageType = imageType;
		}

	}

	public class Address {
		private String home;
		private String street;
		private String locality;
		private String pcode;
		private String city;
		private String state;
		private String country;

		public String getHome() {
			return home;
		}

		public void setHome(String home) {
			this.home = home;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}

		public String getPcode() {
			return pcode;
		}

		public void setPcode(String pcode) {
			this.pcode = pcode;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

	}
}
