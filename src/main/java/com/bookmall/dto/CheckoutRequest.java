package com.bookmall.dto;


public class CheckoutRequest {
    private String receiverName;
    private String receiverAddress;
    
	public CheckoutRequest() {}
	
	public CheckoutRequest(String receiverName, String receiverAddress) {
		super();
		this.receiverName = receiverName;
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
    
}