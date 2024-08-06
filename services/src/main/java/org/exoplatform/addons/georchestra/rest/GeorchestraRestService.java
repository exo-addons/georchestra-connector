package org.exoplatform.addons.georchestra.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.addons.georchestra.services.GeorchestraService;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/georchestra")
public class GeorchestraRestService implements ResourceContainer {
  private GeorchestraService georchestraService;
  private SpaceService spaceService;

  public GeorchestraRestService(GeorchestraService georchestraService, SpaceService spaceService) {
    this.georchestraService = georchestraService;
    this.spaceService = spaceService;
  }

  @POST
  @RolesAllowed("administrators")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Save georchestra role", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response addUserInSpacesByRole(@RequestBody(description = "Parameters to apply", required = true) GeorchestraRestParameter parameter) {
    if (georchestraService.addUserInSpacesByRole(parameter.getUsername(),parameter.getRole())) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

  }

  @DELETE
  @RolesAllowed("administrators")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Remove georchestra role", method = "DELETE")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response removeUserInSpacesByRole(@RequestBody(description = "Parameters to apply", required = true) GeorchestraRestParameter parameter) {
    if (georchestraService.removeUserInSpacesByRole(parameter.getUsername(),parameter.getRole())) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

  }

  @Path("/validate")
  @GET
  @RolesAllowed("users")
  @Produces(MediaType.TEXT_PLAIN)
  @Operation(summary = "Validate georchestra role", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response validateRole(@Parameter(description = "role to check", required = true)
                                 @QueryParam("role") String role,
                               @Parameter(description = "spaceId", required = true) @QueryParam("spaceId") String spaceId) {
    String authenticatedUser = ConversationState.getCurrent().getIdentity().getUserId();
    Space space = spaceService.getSpaceById(spaceId);

    if(space == null || (!spaceService.isSuperManager(authenticatedUser) && !spaceService.isManager(space, authenticatedUser))) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    return Response.ok().entity(String.valueOf(georchestraService.validateRole(role))).type(MediaType.TEXT_PLAIN).build();

  }

  @Path("/save")
  @POST
  @RolesAllowed("users")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Save georchestra role", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response saveRole(@Parameter(description = "role to save", required = true) @QueryParam("role") String role,
                           @Parameter(description = "spaceId", required = true) @QueryParam("spaceId") String spaceId) {
    String authenticatedUser = ConversationState.getCurrent().getIdentity().getUserId();
    Space space = spaceService.getSpaceById(spaceId);

    if(space == null || (!spaceService.isSuperManager(authenticatedUser) && !spaceService.isManager(space, authenticatedUser))) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    if (georchestraService.saveRole(role,space)) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @Path("/delete")
  @DELETE
  @RolesAllowed("users")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Delete georchestra role", method = "POST")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response deleteRole(@Parameter(description = "spaceId", required = true) @QueryParam("spaceId") String spaceId) {
    String authenticatedUser = ConversationState.getCurrent().getIdentity().getUserId();
    Space space = spaceService.getSpaceById(spaceId);

    if(space == null || (!spaceService.isSuperManager(authenticatedUser) && !spaceService.isManager(space, authenticatedUser))) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    georchestraService.deleteRole(space);
    return Response.ok().build();
  }

  @Path("/getRole/{spaceId}")
  @GET
  @RolesAllowed("users")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get georchestra role for space", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response getRole(@Parameter(description = "spaceId", required = true) @PathParam("spaceId") String spaceId) {
    String authenticatedUser = ConversationState.getCurrent().getIdentity().getUserId();
    Space space = spaceService.getSpaceById(spaceId);

    if(space == null || (!spaceService.isSuperManager(authenticatedUser) && !spaceService.isManager(space, authenticatedUser))) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    String role = georchestraService.getRole(space);
    JSONObject result = new JSONObject();
    result.put("role",role);
    return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();

  }

  @Path("/queue/{spaceId}")
  @GET
  @RolesAllowed("users")
  @Produces(MediaType.TEXT_PLAIN)
  @Operation(summary = "Get queue length for space", method = "GET")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response getQueueLength(@Parameter(description = "spaceId", required = true) @PathParam("spaceId") String spaceId) {
    String authenticatedUser = ConversationState.getCurrent().getIdentity().getUserId();
    Space space = spaceService.getSpaceById(spaceId);

    if(space == null || (!spaceService.isSuperManager(authenticatedUser) && !spaceService.isManager(space, authenticatedUser))) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    return Response.ok(String.valueOf(georchestraService.getQueueLengthForSpace(space))).build();

  }
}
