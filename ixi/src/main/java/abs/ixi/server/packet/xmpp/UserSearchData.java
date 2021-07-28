package abs.ixi.server.packet.xmpp;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class UserSearchData implements Serializable {
	private static final long serialVersionUID = -4020789004860662426L;

	private Map<String, String> searchRequestData;
	private Set<Item> searchedItems;
	private boolean sendSearchAttributes;

	public Set<Item> getSearchedItems() {
		return searchedItems;
	}

	public void setSearchedItems(Set<Item> searchedItems) {
		this.searchedItems = searchedItems;
	}

	public Map<String, String> getSearchRequestData() {
		return searchRequestData;
	}

	public void setSearchRequestData(Map<String, String> searchRequestData) {
		this.searchRequestData = searchRequestData;
	}

	public boolean isSendSearchAttributes() {
		return sendSearchAttributes;
	}

	public void setSendSearchAttributes(boolean sendSearchAttributes) {
		this.sendSearchAttributes = sendSearchAttributes;
	}

	public static class Item implements Serializable {
		private static final long serialVersionUID = 2063334208263658870L;

		private BareJID userJID;
		private String firstName;
		private String lastName;
		private String nickName;
		private String email;

		public Item() {
		}

		public Item(BareJID userJID) {
			this.userJID = userJID;
		}

		public BareJID getUserJID() {
			return userJID;
		}

		public void setUserJID(BareJID userJID) {
			this.userJID = userJID;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
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

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		@Override
		public boolean equals(Object obj) {
			Item item = (Item) obj;

			return this.userJID.equals(item.getUserJID());
		}

		@Override
		public int hashCode() {
			return this.userJID.hashCode();
		}

	}

}
