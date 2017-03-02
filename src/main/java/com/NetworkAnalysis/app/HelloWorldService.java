package com.NetworkAnalysis.app;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloWorldService
{
    @GET
    public Response getMsg()
    {
        String output = "I M Jersey";
        return Response.status(200).entity(output).build();
    }
}
