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
package org.openo.commontosca.catalog.model.parser.yaml.zte.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openo.commontosca.catalog.common.ToolUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParseYamlResult {
  private String toscaDefinitionsVersion;
  private String description;
  private JsonObject nodeTypes;
  private JsonObject capabilityTypes;
  private JsonObject relationshipTypes;
  private JsonObject policyTypes;
  private TopologyTemplate topologyTemplate;
  private Map<String, String> metadata;
  private JsonObject plans;
  
  private String rawData;

  public String getToscaDefinitionsVersion() {
    return toscaDefinitionsVersion;
  }

  public void setToscaDefinitionsVersion(String toscaDefinitionsVersion) {
    this.toscaDefinitionsVersion = toscaDefinitionsVersion;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public JsonObject getNodeTypes() {
    return nodeTypes;
  }

  public void setNodeTypes(JsonObject nodeTypes) {
    this.nodeTypes = nodeTypes;
  }

  public List<NodeType> getNodeTypeList() {
    return jsonObject2NodeTypes(nodeTypes);
  }

  private ArrayList<NodeType> jsonObject2NodeTypes(JsonObject nodeTypes) {
    ArrayList<NodeType> nodeTypeList = new ArrayList<NodeType>();
    Iterator<Entry<String, JsonElement>> iterator = nodeTypes.entrySet().iterator();
    while (iterator.hasNext()) {
      NodeType type = new NodeType();
      Entry<String, JsonElement> next = iterator.next();
      type.setType(next.getKey());
      type.setValue(new Gson().fromJson(next.getValue(), NodeType.NodeTypeValue.class));
      nodeTypeList.add(type);
    }
    return nodeTypeList;
  }

  public JsonObject getCapabilityTypes() {
    return capabilityTypes;
  }

  public void setCapabilityTypes(JsonObject capabilityTypes) {
    this.capabilityTypes = capabilityTypes;
  }

  public JsonObject getRelationshipTypes() {
    return relationshipTypes;
  }

  public void setRelationshipTypes(JsonObject relationshipTypes) {
    this.relationshipTypes = relationshipTypes;
  }

  public List<RelationshipType> getRelationshipTypeList() {
    return jsonObject2RelationshipTypes(relationshipTypes);
  }

  private ArrayList<RelationshipType> jsonObject2RelationshipTypes(JsonObject relationshipTypes) {
    ArrayList<RelationshipType> relationshipTypeList = new ArrayList<RelationshipType>();
    Iterator<Entry<String, JsonElement>> iterator = relationshipTypes.entrySet().iterator();
    while (iterator.hasNext()) {
      RelationshipType type = new RelationshipType();
      Entry<String, JsonElement> next = iterator.next();
      type.setType(next.getKey());
      type.setValue(new Gson().fromJson(next.getValue(), RelationshipType.RelationshipValue.class));
      relationshipTypeList.add(type);
    }
    return relationshipTypeList;
  }

  public JsonObject getPolicyTypes() {
    return policyTypes;
  }

  public void setPolicyTypes(JsonObject policyTypes) {
    this.policyTypes = policyTypes;
  }

  public TopologyTemplate getTopologyTemplate() {
    return topologyTemplate;
  }

  public void setTopologyTemplate(TopologyTemplate topologyTemplate) {
    this.topologyTemplate = topologyTemplate;
  }

  public Map<String, String> getMetadata() {
    if (this.metadata == null) {
      return new HashMap<>();
    }
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  public JsonObject getPlans() {
    return plans;
  }

  public void setPlans(JsonObject plans) {
    this.plans = plans;
  }

  public List<Plan> getPlanList() {
    return jsonObject2PlanList(this.plans);
  }

  private List<Plan> jsonObject2PlanList(JsonObject plans) {
    if (plans == null) {
      return new ArrayList<>();
    }
    List<Plan> retList = new ArrayList<>();
    Iterator<Entry<String, JsonElement>> iterator = plans.entrySet().iterator();
    while (iterator.hasNext()) {
      Plan ret = new Plan();
      Entry<String, JsonElement> next = iterator.next();
      ret.setName(next.getKey());
      ret.setValue(new Gson().fromJson(next.getValue(), Plan.PlanValue.class));
      retList.add(ret);
    }
    return retList;
  }

  public class TopologyTemplate {
    private String description;
    private List<Input> inputs;
    private List<Output> outputs;
    private List<NodeTemplate> nodeTemplates;
    private SubstitutionMapping substitutionMappings;

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public List<Input> getInputs() {
      return inputs;
    }

    public void setInputs(List<Input> inputs) {
      this.inputs = inputs;
    }

    public List<Output> getOutputs() {
      return outputs;
    }

    public void setOutputs(List<Output> outputs) {
      this.outputs = outputs;
    }

    public List<NodeTemplate> getNodeTemplates() {
      return nodeTemplates;
    }

    public void setNodeTemplates(List<NodeTemplate> nodeTemplates) {
      this.nodeTemplates = nodeTemplates;
    }

    public SubstitutionMapping getSubstitutionMappings() {
      return substitutionMappings;
    }

    public void setSubstitutionMappings(SubstitutionMapping substitutionMappings) {
      this.substitutionMappings = substitutionMappings;
    }

    public class Input {
      private String name;
      private String type;
      private String description;
      private String defaultValue;
      private boolean required;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

      public String getDefault() {
        return defaultValue;
      }

      public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
      }

      public boolean isRequired() {
        return required;
      }

      public void setRequired(boolean required) {
        this.required = required;
      }
    }

    public class Output {
      private String name;
      private String description;
      private Object value;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

      public Object getValue() {
        return value;
      }

      public void setValue(Object value) {
        this.value = value;
      }
    }

    public class NodeTemplate {
      private String name;
      private String nodeType;
      private JsonObject properties;
      private JsonObject[] requirements;
      private JsonObject capabilities;
      private List<Relationship> relationships;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getNodeType() {
        return nodeType;
      }

      public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
      }

      public JsonObject getProperties() {
        return properties;
      }

      public void setProperties(JsonObject properties) {
        this.properties = properties;
      }

      public Map<String, Object> getPropertyList() {
        return jsonObject2Properties(properties);
      }

      private Map<String, Object> jsonObject2Properties(JsonObject properties) {
        Map<String, Object> ret = new HashMap<>();
        Iterator<Entry<String, JsonElement>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<String, JsonElement> next = iterator.next();
          ret.put(next.getKey(), ToolUtil.getAsString(next.getValue()));
        }
        return ret;
      }

      public JsonObject[] getRequirements() {
        return requirements;
      }

      public void setRequirements(JsonObject[] requirements) {
        this.requirements = requirements;
      }

      public JsonObject getCapabilities() {
        return capabilities;
      }

      public void setCapabilities(JsonObject capabilities) {
        this.capabilities = capabilities;
      }

      public List<Relationship> getRelationships() {
        return relationships;
      }

      public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
      }

      /**
       * get scalable.
       * @return NodeTemplateScalable
       */
      public NodeTemplateScalable getScalable() {
        if (capabilities == null) {
          return null;
        }
        JsonElement scaleableJson = capabilities.get("scalable");
        if (scaleableJson == null || !scaleableJson.isJsonObject()) {
          return null;
        }
        JsonElement propertyJson = scaleableJson.getAsJsonObject().get("properties");
        if (propertyJson == null || !propertyJson.isJsonObject()) {
          return null;
        }

        NodeTemplateScalable scalable = new NodeTemplateScalable();
        scalable.setMin_instances(
            ToolUtil.getAsString(propertyJson.getAsJsonObject().get("min_instances")));
        scalable.setMax_instances(
            ToolUtil.getAsString(propertyJson.getAsJsonObject().get("max_instances")));
        scalable.setDefault_instances(
            ToolUtil.getAsString(propertyJson.getAsJsonObject().get("default_instances")));
        return scalable;
      }

      public class Relationship {
        private String targetNodeName;
        private String type;
        private String sourceNodeName;

        public String getTargetNodeName() {
          return targetNodeName;
        }

        public void setTargetNodeName(String targetNodeName) {
          this.targetNodeName = targetNodeName;
        }

        public String getType() {
          return type;
        }

        public void setType(String type) {
          this.type = type;
        }

        public String getSourceNodeName() {
          return sourceNodeName;
        }

        public void setSourceNodeName(String sourceNodeName) {
          this.sourceNodeName = sourceNodeName;
        }
      }

      public class NodeTemplateScalable {
        private String minInstances;
        private String maxInstances;
        private String defaultInstances;

        public String getMin_instances() {
          return minInstances;
        }

        public void setMin_instances(String minInstances) {
          this.minInstances = minInstances;
        }

        public String getMax_instances() {
          return maxInstances;
        }

        public void setMax_instances(String maxInstances) {
          this.maxInstances = maxInstances;
        }

        public String getDefault_instances() {
          return defaultInstances;
        }

        public void setDefault_instances(String defaultInstances) {
          this.defaultInstances = defaultInstances;
        }
      }
    }

    public class SubstitutionMapping {
      private String node_type;
      private JsonObject requirements;
      private JsonObject capabilities;
      
      public String getNode_type() {
        return node_type;
      }

      public void setNode_type(String node_type) {
        this.node_type = node_type;
      }

      public JsonObject getRequirements() {
        return requirements;
      }

      public void setRequirements(JsonObject requirements) {
        this.requirements = requirements;
      }

      public Map<String, String[]> getRequirementList() {
        return jsonObjects2Requirements(this.requirements);
      }

      private Map<String, String[]> jsonObjects2Requirements(JsonObject requirements) {
        if (requirements == null) {
          return new HashMap<String, String[]>();
        }
        
        Map<String, String[]> ret = new HashMap<String, String[]>();
        Iterator<Entry<String, JsonElement>> iterator = requirements.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<String, JsonElement> next = iterator.next();
          if (next.getValue().isJsonPrimitive() || next.getValue().isJsonObject()) {
            ret.put(next.getKey(), new String[] {ToolUtil.getAsString(next.getValue())});
            continue;
          }

          if (next.getValue().isJsonArray()) {
            String[] value = parseListValue((JsonArray) next.getValue());
            ret.put(next.getKey(), value);
          }
        }

        return ret;
      }

      private String[] parseListValue(JsonArray jsonArray) {
        String[] value = new String[jsonArray.size()];
        for (int i = 0, size = jsonArray.size(); i < size; i++) {
          value[i] = ToolUtil.getAsString(jsonArray.get(i));
        }
        return value;
      }

      public JsonObject getCapabilities() {
        return capabilities;
      }

      public void setCapabilities(JsonObject capabilities) {
        this.capabilities = capabilities;
      }

      public Map<String, String[]> getCapabilityList() {
        return jsonObject2Capabilities(this.capabilities);
      }

      private Map<String, String[]> jsonObject2Capabilities(JsonObject capabilities) {
        if (capabilities == null) {
          return new HashMap<String, String[]>();
        }
        
        Map<String, String[]> ret = new HashMap<String, String[]>();
        Iterator<Entry<String, JsonElement>> iterator = capabilities.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<String, JsonElement> next = iterator.next();
          if (next.getValue().isJsonPrimitive() || next.getValue().isJsonObject()) {
            ret.put(next.getKey(), new String[] {ToolUtil.getAsString(next.getValue())});
            continue;
          }

          if (next.getValue().isJsonArray()) {
            String[] value = parseListValue((JsonArray) next.getValue());
            ret.put(next.getKey(), value);
          }
        }

        return ret;
      }
    }
  }


  public class RelationshipType {
    private String type;
    private RelationshipValue value;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public RelationshipValue getValue() {
      return value;
    }

    public void setValue(RelationshipValue value) {
      this.value = value;
    }

    public class RelationshipValue {
      private String derivedFrom;
      private String[] validTargetTypes;

      public String getDerivedFrom() {
        return derivedFrom;
      }

      public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
      }

      public String[] getValid_target_types() {
        return validTargetTypes;
      }

      public void setValid_target_types(String[] validTargetTypes) {
        this.validTargetTypes = validTargetTypes;
      }
    }
  }


  public class NodeType {
    private String type;
    private NodeTypeValue value;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public NodeTypeValue getValue() {
      return value;
    }

    public void setValue(NodeTypeValue value) {
      this.value = value;
    }

    public class NodeTypeValue {
      private String derivedFrom;
      private JsonObject properties;
      private JsonObject[] requirements;
      private JsonObject capabilities;

      public String getDerivedFrom() {
        return derivedFrom;
      }

      public void setDerived_from(String derivedFrom) {
        this.derivedFrom = derivedFrom;
      }

      public JsonObject getProperties() {
        return properties;
      }

      public void setProperties(JsonObject properties) {
        this.properties = properties;
      }

      public List<NodeTypeProperty> getPropertyList() {
        return jsonObject2Properties(properties);
      }

      private List<NodeTypeProperty> jsonObject2Properties(JsonObject properties) {
        List<NodeTypeProperty> propertieList = new ArrayList<NodeTypeProperty>();
        Iterator<Entry<String, JsonElement>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
          NodeTypeProperty type = new NodeTypeProperty();
          Entry<String, JsonElement> next = iterator.next();
          type.setKey(next.getKey());
          type.setValue(new Gson().fromJson(next.getValue(), JsonObject.class));
          propertieList.add(type);
        }
        return propertieList;
      }

      public class NodeTypeProperty {
        private String key;
        private JsonObject value;

        public String getKey() {
          return key;
        }

        public void setKey(String key) {
          this.key = key;
        }

        /**
         * get default value.
         * @return default value
         */
        public String getDefaultValue() {
          JsonElement defaultValue = value.get("default");
          if (defaultValue == null || defaultValue.isJsonObject()) {
            return "";
          }

          return ToolUtil.getAsString(defaultValue);
        }

        public JsonObject getValue() {
          return value;
        }

        public void setValue(JsonObject value) {
          this.value = value;
        }
      }

      public JsonObject[] getRequirements() {
        return requirements;
      }

      public void setRequirements(JsonObject[] requirements) {
        this.requirements = requirements;
      }

      public JsonObject getCapabilities() {
        return capabilities;
      }

      public void setCapabilities(JsonObject capabilities) {
        this.capabilities = capabilities;
      }
    }
  }

  public class Plan {
    private String name;
    private PlanValue value;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return value.getDescription();
    }

    public String getReference() {
      return value.getReference();
    }

    public String getPlanLanguage() {
      return value.getPlanLanguage();
    }

    public List<PlanValue.PlanInput> getInputList() {
      return value.getInputList();
    }

    public PlanValue getValue() {
      return value;
    }

    public void setValue(PlanValue value) {
      this.value = value;
    }

    public class PlanValue {
      private String description;
      private String reference;
      private String planLanguage;
      private JsonObject inputs;

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

      public String getReference() {
        return reference;
      }

      public void setReference(String reference) {
        this.reference = reference;
      }

      public String getPlanLanguage() {
        return planLanguage;
      }

      public void setPlanLanguage(String planLanguage) {
        this.planLanguage = planLanguage;
      }

      public JsonObject getInputs() {
        return inputs;
      }

      public void setInputs(JsonObject inputs) {
        this.inputs = inputs;
      }

      public List<PlanInput> getInputList() {
        return jsonObject2PlanInputList(inputs);

      }

      private List<PlanInput> jsonObject2PlanInputList(JsonObject inputs) {
        List<PlanInput> retList = new ArrayList<PlanInput>();
        Iterator<Entry<String, JsonElement>> iterator = inputs.entrySet().iterator();
        while (iterator.hasNext()) {
          PlanInput ret = new PlanInput();
          Entry<String, JsonElement> next = iterator.next();
          ret.setName(next.getKey());
          ret.setValue(new Gson().fromJson(next.getValue(), PlanInput.PlanInputValue.class));
          retList.add(ret);
        }
        return retList;
      }

      public class PlanInput {
        private String name;
        private PlanInputValue value;

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }

        public String getType() {
          return value.getType();
        }

        public String getDescription() {
          return value.getDescription();
        }

        public String getDefault() {
          return value.getDefaultValue();
        }

        public boolean isRequired() {
          return value.isRequired();
        }

        public PlanInputValue getValue() {
          return value;
        }

        public void setValue(PlanInputValue value) {
          this.value = value;
        }

        public class PlanInputValue {
          private String type;
          private String description;
          @SerializedName("default")
          private String defaultValue;
          private boolean required;

          public String getType() {
            return type;
          }

          public void setType(String type) {
            this.type = type;
          }

          public String getDescription() {
            return description;
          }

          public void setDescription(String description) {
            this.description = description;
          }

          public String getDefaultValue() {
            return defaultValue;
          }

          public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
          }

          public boolean isRequired() {
            return required;
          }

          public void setRequired(boolean required) {
            this.required = required;
          }
        }
      }
    }
  }
}
