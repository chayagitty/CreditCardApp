import java.io.Serializable;

public class Address  implements Serializable{
	
	//need this field for the serialization
	private static final long serialVersionUID = 1L;
	
	private String street;
	private String city;
	private USState state;
	private String zipcode;
	
	public Address(String street, String city, USState state, String zip) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipcode = zip;
	}
	public USState getState() {
		return this.state;
	}
	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}
	public String getZipcode() {
		return zipcode;
	}

}
