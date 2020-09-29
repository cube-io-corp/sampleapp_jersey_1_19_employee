package com.journaldev.router;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

import java.util.HashMap;
import java.util.Map;

@Path("/office")
public class OfficeRouter {

	private  static ObjectMapper mapper = new ObjectMapper();

	@GET
	@Path("/health")
	@Produces(MediaType.APPLICATION_JSON)
	public Response health() throws JsonProcessingException {

		return Response.ok(mapper.writeValueAsString(new HashMap<String , Object>(){{put("message" , "All is Well From Office !!!!");}})).type(MediaType.APPLICATION_JSON).build();
	}


	@GET
	@Path("/getDepth2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepth2(@Context HttpHeaders headers, @QueryParam("count") int studentCount) throws JsonProcessingException {
		final Span span = GlobalTracer.get().activeSpan();

		System.out.println("TraceId: " + span.context().toTraceId());
		System.out.println("ParentSpanId: " + headers.getRequestHeader("x-datadog-parent-id"));
		System.out.println("SpanId: " + span.context().toSpanId());

		Map resp = new HashMap<String , Object>(){{
			put("message","HI from GET office depth2");
			put("SquareValue" , studentCount*studentCount);
		}};
		String jsonStr = mapper.writeValueAsString(resp);
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(jsonStr).build();
	}


	@POST
	@Path("/postDepth2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postDepth2(@Context HttpHeaders headers, @QueryParam("count") int studentCount) throws JsonProcessingException {
		final Span span = GlobalTracer.get().activeSpan();

		System.out.println("TraceId: " + span.context().toTraceId());
		System.out.println("ParentSpanId: " + headers.getRequestHeader("x-datadog-parent-id"));
		System.out.println("SpanId: " + span.context().toSpanId());

		Map resp = new HashMap<String , Object>(){{
			put("message","HI from POST office depth2");
			put("SquareValue++" , studentCount*studentCount + 1 );
		}};

		String jsonStr = mapper.writeValueAsString(resp);
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(jsonStr).build();
	}

}
