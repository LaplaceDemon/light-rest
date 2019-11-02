package io.github.laplacedemon.light.rest.http.rest;

import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.exception.BadRequestException;
import io.github.laplacedemon.light.rest.http.request.RestRequest;

public abstract class RestHandler {
	public void get(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void post(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void put(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void patch(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void delete(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void head(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

	public void options(final RestRequest request, final IOSession ioSession) throws Exception {
		throw new BadRequestException(400);
	}

}