package sjq.light.rest.http.rest;

import sjq.light.rest.http.exception.BadRequestException;
import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;

public abstract class RestHandler {
	public Response get(Request request) throws Exception  {
		throw new BadRequestException(400);
	}

	public Response post(Request request) throws Exception {
		throw new BadRequestException(400);
	}

	public Response put(Request request) throws Exception  {
		throw new BadRequestException(400);
	}

	public Response delete(Request request) throws Exception  {
		throw new BadRequestException(400);
	}

	public Response head(Request request) throws Exception  {
		throw new BadRequestException(400);
	}

	public Response options(Request request) throws Exception  {
		throw new BadRequestException(400);
	}

}