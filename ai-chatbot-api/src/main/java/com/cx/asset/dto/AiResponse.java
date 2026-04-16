package com.cx.asset.dto;

public class AiResponse {
	private String type;   // ORDER | INVENTORY | REPORT | INFO | ERROR
    private String status; // SUCCESS | FAILED
    private Object data;
    private String message;

    public AiResponse() {}

    public AiResponse(String type, String status, Object data, String message) {
        this.type = type;
        this.status = status;
        this.data = data;
        this.message = message;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}
