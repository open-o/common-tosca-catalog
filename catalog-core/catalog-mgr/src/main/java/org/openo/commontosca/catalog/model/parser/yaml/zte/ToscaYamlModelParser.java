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
package org.openo.commontosca.catalog.model.parser.yaml.zte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openo.commontosca.catalog.common.ToolUtil;
import org.openo.commontosca.catalog.db.exception.CatalogResourceException;
import org.openo.commontosca.catalog.db.resource.TemplateManager;
import org.openo.commontosca.catalog.model.common.TemplateDataHelper;
import org.openo.commontosca.catalog.model.entity.CapReqMapping;
import org.openo.commontosca.catalog.model.entity.InputParameter;
import org.openo.commontosca.catalog.model.entity.NodeTemplate;
import org.openo.commontosca.catalog.model.entity.OutputParameter;
import org.openo.commontosca.catalog.model.entity.RelationShip;
import org.openo.commontosca.catalog.model.entity.ServiceTemplate;
import org.openo.commontosca.catalog.model.entity.ServiceTemplateOperation;
import org.openo.commontosca.catalog.model.entity.SubstitutionMapping;
import org.openo.commontosca.catalog.model.parser.AbstractModelParser;
import org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlRequestParemeter;
import org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlResult;
import org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlResult.TopologyTemplate.Input;
import org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlResult.TopologyTemplate.NodeTemplate.Relationship;
import org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlResult.TopologyTemplate.Output;
import org.openo.commontosca.catalog.model.parser.yaml.zte.service.YamlParseServiceConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToscaYamlModelParser extends AbstractModelParser {
  private static final Logger logger = LoggerFactory.getLogger(ToscaYamlModelParser.class);

  @Override
  public String parse(String packageId, String fileLocation) throws CatalogResourceException {
    logger.info("tosca-yaml-parser parse begin.");
    ParseYamlResult result = getParseYamlResult(fileLocation);
    
    // service template
    ServiceTemplate st = parseServiceTemplate(
        result, packageId, parseServiceTemplateFileName(packageId, fileLocation));
    // workflow
    ServiceTemplateOperation[] operations = parseOperations(fileLocation);
    st.setOperations(operations);
    // node templates
    List<NodeTemplate> ntList = parseNodeTemplates(packageId, st.getServiceTemplateId(), result);
    st.setType(getTemplateType(getSubstitutionType(result), ntList).toString());
    // save to db
    TemplateManager.getInstance().addServiceTemplate(
        TemplateDataHelper.convert2TemplateData(st, result.getRawData(), ntList));

    // substitution
    SubstitutionMapping stm = parseSubstitutionMapping(st.getServiceTemplateId(), result);
    if (stm != null) {
      TemplateManager.getInstance()
          .addServiceTemplateMapping(TemplateDataHelper.convert2TemplateMappingData(stm));
    }

    return st.getServiceTemplateId();
  }

  private ParseYamlResult getParseYamlResult(String fileLocation) throws CatalogResourceException {
    String destPath = copyTemporaryFile2HttpServer(fileLocation);
    try {
      String url = getUrlOnHttpServer(toTempFilePath(fileLocation));
      return YamlParseServiceConsumer.getServiceTemplates(comboRequest(url));
    } finally {
      if (destPath != null && !destPath.isEmpty() && (new File(destPath)).exists()) {
        (new File(destPath)).delete();
      }
    }
  }

  private ParseYamlRequestParemeter comboRequest(String fileLocation) {
    ParseYamlRequestParemeter request = new ParseYamlRequestParemeter();
    request.setPath(fileLocation);
    return request;
  }

  private SubstitutionMapping parseSubstitutionMapping(String serviceTemplateId,
      ParseYamlResult result) {
    String type = getSubstitutionType(result);
    if (ToolUtil.isTrimedEmptyString(type)) {
      return null;
    }

    org.openo.commontosca.catalog.model.parser.yaml.zte.entity.ParseYamlResult
        .TopologyTemplate.SubstitutionMapping stm =
        result.getTopologyTemplate().getSubstitutionMappings();
    return new SubstitutionMapping(
        serviceTemplateId, type,
        parseSubstitutionRequirements(stm.getRequirementList()),
        parseSubstitutionCapabilities(stm.getCapabilityList()));
  }
  
  /**
   * @param requirementList
   * @return
   */
  private CapReqMapping[] parseSubstitutionRequirements(Map<String, String[]> requirementList) {
    return parseMappings(requirementList);
  }

  /**
   * @param capabilityList
   * @return
   */
  private CapReqMapping[] parseSubstitutionCapabilities(Map<String, String[]> capabilityList) {
    return parseMappings(capabilityList);
  }

  private CapReqMapping[] parseMappings(Map<String, String[]> mappings) {
    List<CapReqMapping> ret = new ArrayList<>();
    if (mappings != null) {
      for (Entry<String, String[]> mapping : mappings.entrySet()) {
        if (mapping.getValue().length >= 2) {
          ret.add(new CapReqMapping(
              mapping.getKey(), mapping.getValue()[0], mapping.getValue()[1]));
        }
      }
    }
    return ret.toArray(new CapReqMapping[0]);
  }

  private ServiceTemplate parseServiceTemplate(ParseYamlResult result, String packageId,
      String stDownloadUri) {
    ServiceTemplate st = new ServiceTemplate();

    st.setServiceTemplateId(ToolUtil.generateId());
    st.setId(parserId(result.getMetadata()));
    st.setTemplateName(parserServiceTemplateName(result.getMetadata()));
    st.setVendor(parserServiceTemplateVendor(result.getMetadata()));
    st.setVersion(parserServiceTemplateVersion(result.getMetadata()));
    st.setCsarId(packageId);
    st.setDownloadUri(stDownloadUri);
    st.setInputs(parseInputs(result));
    st.setOutputs(parseOutputs(result));
    return st;
  }

  private InputParameter[] parseInputs(ParseYamlResult result) {
    List<Input> inputList = result.getTopologyTemplate().getInputs();
    if (inputList == null) {
      return new InputParameter[0];
    }
    List<InputParameter> retList = new ArrayList<InputParameter>();
    for (Input input : inputList) {
      retList.add(new InputParameter(input.getName(), input.getType(),
          input.getDescription(), input.getDefault(), input.isRequired()));
    }
    return retList.toArray(new InputParameter[0]);
  }

  private OutputParameter[] parseOutputs(ParseYamlResult result) {
    List<Output> outputList = result.getTopologyTemplate().getOutputs();
    if (outputList == null || outputList.isEmpty()) {
      return new OutputParameter[0];
    }
    List<OutputParameter> retList = new ArrayList<OutputParameter>();
    for (Output output : outputList) {
      retList
          .add(new OutputParameter(output.getName(), output.getDescription(), output.getValue()));
    }
    return retList.toArray(new OutputParameter[0]);
  }

  private List<NodeTemplate> parseNodeTemplates(String csarId, String templateId,
      ParseYamlResult result) {
    List<ParseYamlResult.TopologyTemplate.NodeTemplate> nodetemplateList =
        result.getTopologyTemplate().getNodeTemplates();
    if (nodetemplateList == null) {
      return null;
    }

    List<NodeTemplate> retList = new ArrayList<>();
    for (ParseYamlResult.TopologyTemplate.NodeTemplate nodeTemplate : nodetemplateList) {
      NodeTemplate ret = new NodeTemplate();
      ret.setId(nodeTemplate.getName());
      ret.setName(nodeTemplate.getName());
      ret.setType(nodeTemplate.getNodeType());
      ret.setProperties(nodeTemplate.getPropertyList());
      List<RelationShip> relationShipList =
          parseNodeTemplateRelationShip(nodeTemplate.getRelationships());
      ret.setRelationShips(relationShipList);

      retList.add(ret);
    }

    return retList;
  }


  private List<RelationShip> parseNodeTemplateRelationShip(List<Relationship> relationshipList) {
    List<RelationShip> retList = new ArrayList<>();

    if (relationshipList == null) {
      return retList;
    }

    for (Relationship relationship : relationshipList) {
      RelationShip ret = new RelationShip();
      ret.setSourceNodeId(relationship.getSourceNodeName());
      ret.setSourceNodeName(relationship.getSourceNodeName());
      ret.setTargetNodeId(relationship.getTargetNodeName());
      ret.setTargetNodeName(relationship.getTargetNodeName());
      ret.setType(relationship.getType());
      retList.add(ret);
    }

    return retList;
  }

  private String getSubstitutionType(ParseYamlResult result) {
    if (result.getTopologyTemplate().getSubstitutionMappings() == null) {
      return null;
    }
    return result.getTopologyTemplate().getSubstitutionMappings().getNode_type();
  }

}
