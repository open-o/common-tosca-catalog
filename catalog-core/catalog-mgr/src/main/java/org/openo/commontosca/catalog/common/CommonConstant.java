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
package org.openo.commontosca.catalog.common;

public class CommonConstant {
  // Package Status
  public static final String PACKAGE_STATUS_DELETING = "deleting";

  public static final String PACKAGE_STATUS_DELETE_FAIL = "deleteFailed";

  public static final String PACKAGE_XML_FORMAT = "xml";


  public static final String PACKAGE_YAML_FORMAT = "yaml";

  // host image progress

  public static final String TOSCA_METADATA = "TOSCA-Metadata";

  public static final String CSAR_VERSION_META = "version";

  public static final String CSAR_TYPE_META = "type";

  public static final String CSAR_PROVIDER_META = "provider";

  public static final String DEFINITIONS = "Definitions";

  public static final String CSAR_META = "csar.meta";

  public static final String CSAR_SUFFIX = ".csar";

  public static final String HTTP_HEADER_CONTENT_RANGE = "Content-Range";
  
  public static final  String CATALOG_CSAR_DIR_NAME = "/csar";
  
  public static final String COMETD_CHANNEL_PACKAGE_DELETE = "/package/delete";
}
