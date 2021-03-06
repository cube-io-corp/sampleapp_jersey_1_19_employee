package com.journaldev.router;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import com.journaldev.exception.EmpNotFoundException;
import com.journaldev.model.DepRequest;
import com.journaldev.model.DepResponse;
import com.journaldev.model.EmpRequest;
import com.journaldev.model.EmpResponse;
import com.journaldev.model.ErrorResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import io.cube.interceptor.jersey_1x.egress.ClientLoggingFilter;
import io.cube.interceptor.jersey_1x.egress.ClientMockingFilter;
import io.cube.interceptor.jersey_1x.egress.ClientTracingFilter;

@Path("/emp")
public class EmpRouter {

	@POST
	@Path("/getEmp")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmp(JAXBElement<EmpRequest> empRequest) throws EmpNotFoundException {
		EmpResponse empResponse = new EmpResponse();
		if (empRequest.getValue().getId() == 1) {
			empResponse.setId(empRequest.getValue().getId());
			empResponse.setName(empRequest.getValue().getName());
			try {
				//String hostPort = fromEnvOrSystemProperties("depURL").orElse("35.160.68.101:8082");
				String hostPort = fromEnvOrSystemProperties("depURL");
				String uri = "http://"+hostPort+"/dept/dept/getDept";

				DepRequest request = new DepRequest();
				// set id as 1 for OK response
				request.setId(1);
				request.setName("HR");
				Client client = Client.create();
				client.addFilter(new ClientMockingFilter());
				client.addFilter(new ClientTracingFilter());
				client.addFilter(new ClientLoggingFilter(client.getMessageBodyWorkers()));
				WebResource r = client.resource(uri);
				System.out.println("Dept service URI: "+uri);
				ClientResponse response = r.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, request);
				System.out.println(response.getStatus());
				if (response.getStatus() == 200) {
					DepResponse deptResponse = response.getEntity(DepResponse.class);
					//System.out.println(empResponse.getId() + "::" + empResponse.getName());
					System.out.println("Response name : " + deptResponse.getName());
					empResponse.setDeptName(deptResponse.getName());
				} else {
					ErrorResponse exc = response.getEntity(ErrorResponse.class);
					System.out.println(exc.getErrorCode());
					System.out.println(exc.getErrorId());
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			throw new EmpNotFoundException("Wrong ID", empRequest.getValue().getId());
		}
		return Response.ok(empResponse).build();
	}

	public static String fromEnvOrSystemProperties(String propertyName) {
//		Optional<String> or = Optional.ofNullable(System.getenv(propertyName)).or(() -> {
//			return Optional.ofNullable(System.getProperty(propertyName));
//		});
		if (System.getenv(propertyName) != null ) {
			return System.getenv(propertyName);
		} else if (System.getProperty(propertyName) != null ) {
			return System.getProperty(propertyName);
		}
		return "35.160.68.101:8082";
	}
}
