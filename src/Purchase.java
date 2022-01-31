import java.io.IOException;
import java.io.Serializable;

public class Purchase extends Transaction implements Serializable{
	
	//need this field for the serialization
	private static final long serialVersionUID = 1L;
	
	private PurchaseType purchaseType;
	private Vendor vendor;
	private Fee interestFee;
	public Purchase(double amount, PurchaseType type, Vendor v, Fee f) throws IOException {
		super(amount);
		this.purchaseType = type;
		this.vendor = v;
		this.interestFee=f;
	}
	
	public Fee getInterestFee() {
		return interestFee;
	}

	public PurchaseType getPurchaseType() {
		return purchaseType;
	}
	public String getVendorName() {
		return vendor.getName();
	}
	public String getVendorStreetAddress() {
		return vendor.getStreetAddress();
	}
	public String getVendorCity() {
		return vendor.city();
	}
	public USState getVendorState() {
		return vendor.getState();
	}
	public String getVendorZip() {
		return vendor.zip();
	}


}
