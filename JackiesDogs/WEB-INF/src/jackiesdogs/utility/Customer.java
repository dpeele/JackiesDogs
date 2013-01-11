package jackiesdogs.utility;

public class Customer {
	private String id, firstName, lastName, streetAddress, aptAddress, city, state, zip, phone, email, notes;
	boolean inactive;
	
	public Customer(String id) {
		this.id = id;
	}
	
	public Customer(String id, String firstName, String lastName,
			String streetAddress, String aptAddress, String city, String state,
			String zip, String phone, String email, String notes) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.streetAddress = streetAddress;
		this.aptAddress = aptAddress;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.email = email;
		this.notes = notes;
	}

	public Customer(String firstName, String lastName, String streetAddress,
			String aptAddress, String city, String state, String zip,
			String phone, String email, String notes) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.streetAddress = streetAddress;
		this.aptAddress = aptAddress;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.email = email;
		this.notes = notes;
	}

	public Customer(String firstName, String lastName, String phone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;		
		this.phone = phone;
		this.email = email;
	}

	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getAptAddress() {
		return aptAddress;
	}

	public void setAptAddress(String aptAddress) {
		this.aptAddress = aptAddress;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getFullName() {
		return lastName + ", " + firstName;
	}
}
