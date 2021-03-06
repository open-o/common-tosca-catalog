/**
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.catalog.model.externalservice.container;

import org.openo.commontosca.catalog.model.externalservice.entity.container.ContainerServicePackageList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * The opentosca container interface for csar.
 * 
 * @author 10189609
 * 
 */
@Path("/csars")
public interface IContainerExtPackageRest {
  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public ContainerServicePackageList getToscaServicePackage(
      @QueryParam("csarName") String csarName);

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  public String uploadPackageByToscaService(@QueryParam("fileLocation") String fileLocation);

  @Path("/{csarId}")
  @DELETE
  public String deletePackageById(@PathParam("csarId") String csarId);
}
