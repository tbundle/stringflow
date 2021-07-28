package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.xmpp.UserProfileData.Address;
import abs.ixi.server.packet.xmpp.UserProfileData.UserAvtar;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;

public class IQVCardContent extends AbstractIQContent {
	private static final long serialVersionUID = -3175427158044254234L;

	public static final String XML_ELM_NAME = "vCard";
	public static final String VCARD_XMLNS = "vcard-temp";

	private static final String NAME = "N";
	private static final String GIVEN = "GIVEN";
	private static final String MIDDLE = "MIDDLE";
	private static final String FAMILY = "FAMILY";
	
	private static final String NICKNAME = "NICKNAME";

	private static final String EMAIL = "EMAIL";
	private static final String EMAIL_USER_ID = "USERID";

	private static final String GENDER = "GENDER";
	private static final String BDAY = "BDAY";

	private static final String TEL = "TEL";
	private static final String NUMBER = "NUMBER";

	private static final String ADR = "ADR";
	private static final String HOME = "HOME";
	private static final String STREET = "STREET";
	private static final String LOCALITY = "LOCALITY";
	private static final String CITY = "CITY";
	private static final String STATE = "STATE";
	private static final String CTRY = "CTRY";
	private static final String PCODE = "pcode";

	private static final String DESC = "DESC";

	private static final String PHOTO = "PHOTO";
	private static final String BIN_VAL = "BINVAL";
	private static final String TYPE = "TYPE";

	private static final String JABBERID = "JABBERID";

	private static final String VCARD_ELEMENT = "<vCard xmlns='vcard-temp'/>";
	private static final String VCARD_OPEN_TAG = "<vCard xmlns='vcard-temp'>";
	private static final String VCARD_CLOSE_TAG = "</vCard>";

	private static final String N_OPEN_TAG = "<N>";
	private static final String N_CLOSE_TAG = "</N>";
	private static final String GIVEN_OPEN_TAG = "<GIVEN>";
	private static final String GIVEN_CLOSE_TAG = "</GIVEN>";
	private static final String MIDDLE_OPEN_TAG = "<MIDDLE>";
	private static final String MIDDLE_CLOSE_TAG = "</MIDDLE>";
	private static final String FAMILY_OPEN_TAG = "<FAMILY>";
	private static final String FAMILY_CLOSE_TAG = "</FAMILY>";
	
	private static final String NICK_NAME_OPEN_TAG = "<NICKNAME>";
	private static final String NICK_NAME_CLOSE_TAG = "</NICKNAME>";

	private static final String EMAIL_OPEN_TAG = "<EMAIL>";
	private static final String EMAIL_CLOSE_TAG = "</EMAIL>";
	private static final String USER_ID_OPEN_TAG = "<USERID>";
	private static final String USER_ID_CLOSE_TAG = "</USERID>";

	private static final String GENDER_OPEN_TAG = "<GENDER>";
	private static final String GENDER_CLOSE_TAG = "</GENDER>";

	private static final String BDAY_OPEN_TAG = "<BDAY>";
	private static final String BDAY_CLOSE_TAG = "</BDAY>";

	private static final String TEL_OPEN_TAG = "<TEL>";
	private static final String TEL_CLOSE_TAG = "</TEL>";
	private static final String NUMBER_OPEN_TAG = "<NUMBER>";
	private static final String NUMBER_CLOSE_TAG = "</NUMBER>";

	private static final String ADR_OPEN_TAG = "<ADR>";
	private static final String ADR_CLOSE_TAG = "</ADR>";
	private static final String HOME_OPEN_TAG = "<HOME>";
	private static final String HOME_CLOSE_TAG = "</HOME>";
	private static final String STREET_OPEN_TAG = "<STREET>";
	private static final String STREET_CLOSE_TAG = "</STREET>";
	private static final String LOCALITY_OPEN_TAG = "<LOCALITY>";
	private static final String LOCALITY_CLOSE_TAG = "</LOCALITY>";
	private static final String CITY_OPEN_TAG = "<CITY>";
	private static final String CITY_CLOSE_TAG = "</CITY>";
	private static final String STATE_OPEN_TAG = "<STATE>";
	private static final String STATE_CLOSE_TAG = "</STATE>";
	private static final String CTRY_OPEN_TAG = "<CTRY>";
	private static final String CTRY_CLOSE_TAG = "</CTRY>";
	private static final String PCODE_OPEN_TAG = "<pcode>";
	private static final String PCODE_CLOSE_TAG = "</pcode>";

	private static final String DESC_OPEN_TAG = "<DESC>";
	private static final String DESC_CLOSE_TAG = "</DESC>";

	private static final String PHOTO_OPEN_TAG = "<PHOTO>";
	private static final String PHOTO_CLOSE_TAG = "</PHOTO>";
	private static final String BINVAL_OPEN_TAG = "<BINVAL>";
	private static final String BINVAL_CLOSE_TAG = "</BINVAL>";
	private static final String TYPE_OPEN_TAG = "<TYPE>";
	private static final String TYPE_CLOSE_TAG = "</TYPE>";

	private static final String JABBERID_OPEN_TAG = "<JABBERID>";
	private static final String JABBERID_CLOSE_TAG = "</JABBERID>";

	private UserProfileData userData;

	public IQVCardContent() {
		super(VCARD_XMLNS, IQContentType.VCARD);
	}

	public IQVCardContent(UserProfileData userData) {
		super(VCARD_XMLNS, IQContentType.VCARD);
		this.userData = userData;
	}

	public IQVCardContent(Element vCardElment) throws InvalidJabberId {
		super(VCARD_XMLNS, IQContentType.VCARD);
		generateUserData(vCardElment);
	}

	private void generateUserData(Element vCardElment) throws InvalidJabberId {
		if (!CollectionUtils.isNullOrEmpty(vCardElment.getChildren())) {
			this.userData = new UserProfileData();

			for (Element elm : vCardElment.getChildren()) {
				if (StringUtils.safeEquals(elm.getName(), NAME)) {

					Element firstName = elm.getChild(GIVEN);

					if (firstName != null) {
						userData.setFirstName(firstName.val());
					}

					Element middleName = elm.getChild(MIDDLE);

					if (middleName != null) {
						userData.setMiddleName(middleName.val());
					}

					Element lastName = elm.getChild(FAMILY);

					if (lastName != null) {
						userData.setLastName(lastName.val());
					}

				}  else if (StringUtils.safeEquals(elm.getName(), NICKNAME)) {
					userData.setNickName(elm.val());

				} else if (StringUtils.safeEquals(elm.getName(), EMAIL)) {
					Element userIdElm = elm.getChild(EMAIL_USER_ID);

					if (userIdElm != null) {
						userData.setEmail(userIdElm.val());
					}

				} else if (StringUtils.safeEquals(elm.getName(), GENDER)) {
					userData.setGender(elm.val());

				} else if (StringUtils.safeEquals(elm.getName(), BDAY)) {
					userData.setBday(elm.val());

				} else if (StringUtils.safeEquals(elm.getName(), TEL)) {
					Element numberElm = elm.getChild(NUMBER);

					if (numberElm != null) {
						userData.setPhone(numberElm.val());
					}

				} else if (StringUtils.safeEquals(elm.getName(), ADR)) {
					Address address = userData.new Address();
					userData.setAddress(address);

					Element home = elm.getChild(HOME);

					if (home != null) {
						address.setHome(home.val());
					}

					Element street = elm.getChild(STREET);

					if (street != null) {
						address.setStreet(street.val());
					}

					Element locality = elm.getChild(LOCALITY);

					if (locality != null) {
						address.setLocality(locality.val());
					}

					Element city = elm.getChild(CITY);

					if (city != null) {
						address.setCity(city.val());
					}

					Element state = elm.getChild(STATE);

					if (state != null) {
						address.setState(state.val());
					}

					Element country = elm.getChild(CTRY);

					if (country != null) {
						address.setCountry(country.val());
					}

					Element pcode = elm.getChild(PCODE);

					if (pcode != null) {
						address.setPcode(pcode.val());
					}

				} else if (StringUtils.safeEquals(elm.getName(), DESC)) {
					userData.setDescription(elm.val());

				} else if (StringUtils.safeEquals(elm.getName(), PHOTO)) {
					Element binvalElm = elm.getChild(BIN_VAL);
					Element typeElm = elm.getChild(TYPE);

					if (binvalElm != null && typeElm != null) {
						UserAvtar avtar = userData.new UserAvtar(binvalElm.val(), typeElm.val());
						userData.setAvtar(avtar);
					}

				} else if (StringUtils.safeEquals(elm.getName(), JABBERID)) {
					userData.setJabberId(new BareJID(elm.val()));

				}

			}
		}
	}

	public UserProfileData getUserData() {
		return userData;
	}

	public void setUserData(UserProfileData userData) {
		this.userData = userData;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();
		this.appendXml(sb);
		return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		if (userData == null) {
			sb.append(VCARD_ELEMENT);

		} else {
			sb.append(VCARD_OPEN_TAG);

			sb.append(N_OPEN_TAG);

			if (userData.getFirstName() != null) {
				sb.append(GIVEN_OPEN_TAG).append(userData.getFirstName()).append(GIVEN_CLOSE_TAG);
			}

			if (userData.getMiddleName() != null) {
				sb.append(MIDDLE_OPEN_TAG).append(userData.getMiddleName()).append(MIDDLE_CLOSE_TAG);
			}

			if (userData.getLastName() != null) {
				sb.append(FAMILY_OPEN_TAG).append(userData.getLastName()).append(FAMILY_CLOSE_TAG);
			}

			sb.append(N_CLOSE_TAG);
			
			if (userData.getNickName() != null) {
				sb.append(NICK_NAME_OPEN_TAG).append(userData.getNickName()).append(NICK_NAME_CLOSE_TAG);
			}

			if (userData.getEmail() != null) {
				sb.append(EMAIL_OPEN_TAG).append(USER_ID_OPEN_TAG).append(userData.getEmail()).append(USER_ID_CLOSE_TAG)
						.append(EMAIL_CLOSE_TAG);
			}

			if (userData.getGender() != null) {
				sb.append(GENDER_OPEN_TAG).append(userData.getGender()).append(GENDER_CLOSE_TAG);
			}

			if (userData.getBday() != null) {
				sb.append(BDAY_OPEN_TAG).append(userData.getBday()).append(BDAY_CLOSE_TAG);
			}

			if (userData.getPhone() != null) {
				sb.append(TEL_OPEN_TAG).append(NUMBER_OPEN_TAG).append(userData.getPhone()).append(NUMBER_CLOSE_TAG)
						.append(TEL_CLOSE_TAG);
			}

			if (userData.getAddress() != null) {
				Address address = userData.getAddress();

				sb.append(ADR_OPEN_TAG);

				if (address.getHome() != null) {
					sb.append(HOME_OPEN_TAG).append(address.getHome()).append(HOME_CLOSE_TAG);
				}

				if (address.getStreet() != null) {
					sb.append(STREET_OPEN_TAG).append(address.getStreet()).append(STREET_CLOSE_TAG);
				}

				if (address.getLocality() != null) {
					sb.append(LOCALITY_OPEN_TAG).append(address.getLocality()).append(LOCALITY_CLOSE_TAG);
				}

				if (address.getCity() != null) {
					sb.append(CITY_OPEN_TAG).append(address.getCity()).append(CITY_CLOSE_TAG);
				}

				if (address.getState() != null) {
					sb.append(STATE_OPEN_TAG).append(address.getState()).append(STATE_CLOSE_TAG);
				}

				if (address.getCountry() != null) {
					sb.append(CTRY_OPEN_TAG).append(address.getCountry()).append(CTRY_CLOSE_TAG);
				}

				if (address.getPcode() != null) {
					sb.append(PCODE_OPEN_TAG).append(address.getPcode()).append(PCODE_CLOSE_TAG);
				}

				sb.append(ADR_CLOSE_TAG);

			}

			if (userData.getDescription() != null) {
				sb.append(DESC_OPEN_TAG).append(userData.getDescription()).append(DESC_CLOSE_TAG);
			}

			if (userData.getAvtar() != null) {
				UserAvtar avtar = userData.getAvtar();

				sb.append(PHOTO_OPEN_TAG).append(BINVAL_OPEN_TAG).append(avtar.getBase64EncodedImage())
						.append(BINVAL_CLOSE_TAG).append(TYPE_OPEN_TAG).append(avtar.getImageType())
						.append(TYPE_CLOSE_TAG).append(PHOTO_CLOSE_TAG);

			}

			if (userData.getJabberId() != null) {
				sb.append(JABBERID_OPEN_TAG).append(userData.getJabberId().toString()).append(JABBERID_CLOSE_TAG);
			}

			sb.append(VCARD_CLOSE_TAG);
		}

		return sb;
	}

}
