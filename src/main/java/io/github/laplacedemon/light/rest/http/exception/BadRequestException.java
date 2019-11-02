package io.github.laplacedemon.light.rest.http.exception;

public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private int status = 500;

	public BadRequestException() {
		super();
	}

	public BadRequestException(int status) {
		super();
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}