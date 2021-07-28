package abs.ixi.server.packet.xmpp;

import java.io.Serializable;

public class UserProfileData implements Serializable {
	private static final long serialVersionUID = 2014615611258436482L;

	private BareJID jabberId;
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

	public BareJID getJabberId() {
		return jabberId;
	}

	public void setJabberId(BareJID jabberId) {
		this.jabberId = jabberId;
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

	public class UserAvtar implements Serializable {
		private static final long serialVersionUID = 1L;

		private String base64EncodedImage;
		private String imageType;

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

	public class Address implements Serializable {
		private static final long serialVersionUID = -6538270771514437645L;

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

		public void setCountry(String countery) {
			this.country = countery;
		}

	}

}
