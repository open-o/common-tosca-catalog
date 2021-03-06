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
package org.openo.commontosca.catalog.model.parser.yaml.aria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openo.commontosca.catalog.common.ToolUtil;
import org.openo.commontosca.catalog.db.exception.CatalogResourceException;
import org.openo.commontosca.catalog.db.resource.TemplateManager;
import org.openo.commontosca.catalog.entity.response.CsarFileUriResponse;
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
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult;
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Input;
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Node;
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Node.Relationship;
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Output;
import org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Substitution.Mapping;
import org.openo.commontosca.catalog.model.parser.yaml.aria.service.AriaParserServiceConsumer;
import org.openo.commontosca.catalog.wrapper.PackageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 10090474
 *
 */
public class AriaModelParser extends AbstractModelParser {
  private static final Logger logger = LoggerFactory.getLogger(AriaModelParser.class);

  /* (non-Javadoc)
   * @see org.openo.commontosca.catalog.model.parser.AbstractModelParser#parse(java.lang.String, java.lang.String)
   */
  @Override
  public String parse(String packageId, String fileLocation) throws CatalogResourceException {
    logger.info("Parse begin.");
    
    String stFileLocation = parseServiceTemplateFileName(packageId, fileLocation);
    AriaParserResult result = getAriaParserResult(packageId, stFileLocation);
    
    // service template
    ServiceTemplate st = parseServiceTemplate(result, packageId, stFileLocation);
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
  

  /**
   * @param serviceTemplateId
   * @param result
   * @return
   */
  private SubstitutionMapping parseSubstitutionMapping(String serviceTemplateId,
      AriaParserResult result) {
    String type = getSubstitutionType(result);
    if (ToolUtil.isTrimedEmptyString(type)) {
      return null;
    }
    
    org.openo.commontosca.catalog.model.parser.yaml.aria.entity.AriaParserResult.Instance.Substitution stm =
        result.getInstance().getSubstitution();
    return new SubstitutionMapping(
        serviceTemplateId,
        type,
        parseSubstitutionRequirements(stm.getRequirement()),
        parseSubstitutionCapabilities(stm.getCapabilities()));
  }


  /**
   * @param capabilities
   * @return
   */
  private CapReqMapping[] parseSubstitutionCapabilities(Mapping[] capabilities) {
    return parseMappings(capabilities);
  }
  
  /**
   * @param requirement
   * @return
   */
  private CapReqMapping[] parseSubstitutionRequirements(Mapping[] requirement) {
    return parseMappings(requirement);
  }

  private CapReqMapping[] parseMappings(Mapping[] mappings) {
    List<CapReqMapping> ret = new ArrayList<>();
    if (mappings != null) {
      for (Mapping mapping : mappings) {
        ret.add(new CapReqMapping(
            mapping.getMapped_name(), mapping.getNode_id(), mapping.getName()));
      }
    }
    
    return ret.toArray(new CapReqMapping[0]);
  }

  /**
   * @param result
   * @return
   */
  private String getSubstitutionType(AriaParserResult result) {
    if (result.getInstance().getSubstitution() == null) {
      return null;
    }
    return result.getInstance().getSubstitution().getNode_type_name();
  }


  /**
   * @param packageId
   * @param serviceTemplateId
   * @param result
   * @return
   * @throws CatalogResourceException 
   */
  private List<NodeTemplate> parseNodeTemplates(String packageId, String serviceTemplateId,
      AriaParserResult result) throws CatalogResourceException {
    Node[] nodes = result.getInstance().getNodes();
    if (nodes == null || nodes.length == 0) {
      return null;
    }

    List<NodeTemplate> retList = new ArrayList<>();
    for (Node node : nodes) {
      NodeTemplate ret = new NodeTemplate();
      ret.setId(node.getTemplate_name());
      ret.setName(node.getTemplate_name());
      ret.setType(node.getType_name());
      ret.setProperties(node.getPropertyAssignments());
      List<RelationShip> relationShipList =
          parseNodeTemplateRelationShip(node.getRelationships(), node, nodes);
      ret.setRelationShips(relationShipList);

      retList.add(ret);
    }

    return retList;
  }


  /**
   * @param relationships
   * @param sourceNode 
   * @param nodes 
   * @return
   * @throws CatalogResourceException 
   */
  private List<RelationShip> parseNodeTemplateRelationShip(Relationship[] relationships,
      Node sourceNode, Node[] nodes) throws CatalogResourceException {
    List<RelationShip> retList = new ArrayList<>();

    if (relationships == null || relationships.length == 0) {
      return retList;
    }

    for (Relationship relationship : relationships) {
      if (relationship.getTarget_node_id().equals(sourceNode.getId())) {
        continue;  // target == source, ignore.
      }
      
      RelationShip ret = new RelationShip();
      ret.setSourceNodeId(sourceNode.getTemplate_name());
      ret.setSourceNodeName(sourceNode.getTemplate_name());
      Node targetNode = getNodeById(nodes, relationship.getTarget_node_id());
      ret.setTargetNodeId(targetNode.getTemplate_name());
      ret.setTargetNodeName(targetNode.getTemplate_name());
      ret.setType(relationship.getType_name());
      retList.add(ret);
    }

    return retList;
  }


  /**
   * @param nodes
   * @param nodeId
   * @return
   * @throws CatalogResourceException 
   */
  private Node getNodeById(Node[] nodes, String nodeId) throws CatalogResourceException {
    if (nodeId == null) {
      throw new CatalogResourceException("Target node id is null.");
    }
    if (nodes == null || nodes.length == 0) {
      throw new CatalogResourceException("Can't find target node. nodeId = " + nodeId);
    }
    
    for (Node node : nodes) {
      if (nodeId.equals(node.getId())) {
        return node;
      }
    }
    
    throw new CatalogResourceException("Can't find target node. nodeId = " + nodeId);
  }


  /**
   * @param result
   * @param packageId
   * @param downloadUri
   * @return
   */
  private ServiceTemplate parseServiceTemplate(AriaParserResult result, String packageId,
      String downloadUri) {
    ServiceTemplate st = new ServiceTemplate();

    st.setServiceTemplateId(ToolUtil.generateId());
    st.setId(parserId(result.getInstance().getMetadata()));
    st.setTemplateName(parserServiceTemplateName(result.getInstance().getMetadata()));
    st.setVendor(parserServiceTemplateVendor(result.getInstance().getMetadata()));
    st.setVersion(parserServiceTemplateVersion(result.getInstance().getMetadata()));
    st.setCsarId(packageId);
    st.setDownloadUri(downloadUri);
    st.setInputs(parseInputs(result));
    st.setOutputs(parseOutputs(result));
    return st;
  }


  /**
   * @param result
   * @return
   */
  private InputParameter[] parseInputs(AriaParserResult result) {
    Map<String, Input> inputs = result.getInstance().getInputs();
    if (inputs == null || inputs.isEmpty()) {
      return new InputParameter[0];
    }
    List<InputParameter> retList = new ArrayList<InputParameter>();
    for (Entry<String, Input> e : inputs.entrySet()) {
      retList.add(
          new InputParameter(
              e.getKey(),
              e.getValue().getType_name(),
              e.getValue().getDescription(),
              e.getValue().getValue(),
              false));
    }
    return retList.toArray(new InputParameter[0]);
  }

  /**
   * @param result
   * @return
   */
  private OutputParameter[] parseOutputs(AriaParserResult result) {
    Map<String, Output> outputs = result.getInstance().getOutpus();
    if (outputs == null || outputs.isEmpty()) {
      return new OutputParameter[0];
    }
    
    List<OutputParameter> retList = new ArrayList<OutputParameter>();
    for (Entry<String, Output> e: outputs.entrySet()) {
      retList.add(
          new OutputParameter(
              e.getKey(), e.getValue().getDescription(), e.getValue().getValue()));
    }

    return retList.toArray(new OutputParameter[0]);
  }

  private AriaParserResult getAriaParserResult(String packageId, String stFileLocation) throws CatalogResourceException {
    CsarFileUriResponse stDownloadUri =
        PackageWrapper.getInstance().getCsarFileDownloadUri(packageId, stFileLocation);
    return AriaParserServiceConsumer.parseCsarPackage(stDownloadUri.getDownloadUri());
  }

}
