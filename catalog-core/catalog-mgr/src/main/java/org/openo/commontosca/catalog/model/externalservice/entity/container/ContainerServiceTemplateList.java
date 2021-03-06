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
package org.openo.commontosca.catalog.model.externalservice.entity.container;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "servicetemplates")
public class ContainerServiceTemplateList {
  public static final String NAMESPACE_OF_SELFSERVICE =
      "http://www.eclipse.org/winery/model/selfservice";

  @XmlElement(name = "serviceTemplate")
  private List<ContainerServiceTemplate> serviceTemplateList;

  public List<ContainerServiceTemplate> getServiceTemplateList() {
    return serviceTemplateList;
  }

  public void setServiceTemplateList(List<ContainerServiceTemplate> optionList) {
    this.serviceTemplateList = optionList;
  }
}
