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

import org.openo.commontosca.catalog.model.externalservice.entity.container.ContainerServiceNodeTemplateList;
import org.openo.commontosca.catalog.model.externalservice.entity.container.ContainerServiceTemplateList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * The opentosca container interface for service template.
 * 
 * @author 10189609
 * 
 */
@Path("/servicetemplates")
public interface IContainerTemplateRest {
  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  ContainerServiceTemplateList getToscaServiceTemplate(@QueryParam("templateid") String templateId);

  @Path("/{templateid}/nodetemplates")
  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  ContainerServiceNodeTemplateList getToscaServiceNodeTemplates(
      @PathParam("templateid") String templateId);
}
