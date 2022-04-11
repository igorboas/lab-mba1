package org.acme.rest.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/extensions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExtensionResource {

    private Set<Extension> extensions = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public ExtensionResource() {
        extensions.add(new Extension("io.quarkus:quarkus-rest-client", "ignored", "2.7.5.Final", "REST Client",	"Call REST services", "", "Web",  "true", "true", "https://quarkus.io/guides/rest-client", "10", "true", "io.quarkus.platform:quarkus-bom:2.7.5.Final"));
        extensions.add(new Extension("io.quarkus:quarkus-rest-client-super", "ignored", "2.7.5.Final", "REST Client",	"Call REST services", "", "Web",  "true", "true", "https://quarkus.io/guides/rest-client", "10", "true", "io.quarkus.platform:quarkus-bom:2.7.5.Final"));
//        fruits.add(new Fruit("Pineapple", "Tropical fruit"));
    }

    @GET
    public Set<Extension> list() {
        return extensions;
    }

    @GET
    @Path("/id/{id}")
    public Extension get(){
	return new Extension("io.quarkus:quarkus-rest-client", "ignored", "2.7.5.Final", "REST Client", "Call REST services", "", "Web",  "true", "true", "https://quarkus.io/guides/rest-client", "10", "true", "io.quarkus.platform:quarkus-bom:2.7.5.Final");//extensions[0];
    }
}
