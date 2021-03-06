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
package org.openo.commontosca.catalog.model.plan.wso2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.openo.commontosca.catalog.common.Config;
import org.openo.commontosca.catalog.db.exception.CatalogResourceException;
import org.openo.commontosca.catalog.model.common.TemplateUtils;
import org.openo.commontosca.catalog.model.plan.wso2.entity.DeletePackageResponse;
import org.openo.commontosca.catalog.model.plan.wso2.entity.DeployPackageResponse;
import org.openo.commontosca.catalog.model.plan.wso2.entity.StartProcessRequest;
import org.openo.commontosca.catalog.model.plan.wso2.entity.StartProcessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;
import com.google.gson.Gson;


public class Wso2ServiceConsumer {
  public static final String WSO2_APP_URL = "/openoapi/wso2bpel/v1/package";
  private static final Logger logger = LoggerFactory.getLogger(Wso2ServiceConsumer.class);
  
  /**
   * deploy package.
   * @param zipFileLocation zip file location
   * @param planFilePath plan file path
   * @return DeployPackageResponse
   * @throws CatalogResourceException e1
   */
  public static DeployPackageResponse deployPackage(String zipFileLocation, String planFilePath)
      throws CatalogResourceException {
    IpPort ipPort = getIpPort(Config.getConfigration().getMsbServerAddr());
    if (ipPort == null) {
      throw new CatalogResourceException("getMsbServerAddr failed.");
    }
    ZipFile zf = null;
    InputStream ins = null;
    try {
      zf = new ZipFile(zipFileLocation);
      ins = getInputStream(zf, planFilePath);
      RestResponse res = RestfulClient.post(
          ipPort.getIp(), ipPort.getPort(), WSO2_APP_URL,
          buildRequest(ins, planFilePath));

      if (res.getStatusCode() == null || res.getResult() == null) {
        throw new CatalogResourceException(
            "Deploy Package return null. Response = " + res);
      }
      
      if (200 == res.getStatusCode() || 201 == res.getStatusCode()) {
        DeployPackageResponse response =
            new Gson().fromJson(res.getResult(), DeployPackageResponse.class);
        if (response.isSuccess()) {
          return response;
        }
      }

      throw new CatalogResourceException(
          "Deploy Package return failed. Response = " + res.getResult());
    } catch (FileNotFoundException e) {
      throw new CatalogResourceException("buildRequest failed.", e);
    } catch (IOException e) {
      throw new CatalogResourceException("ZipFile failed.", e);
    } finally {
      TemplateUtils.closeInputStream(ins);
      TemplateUtils.closeZipFile(zf);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IpPort {
    private String ip;
    private int port;
  }
  
  private static IpPort getIpPort(String addr) {
    Pattern p = Pattern.compile("//(.*?):(.*)");
    Matcher m = p.matcher(addr);
    while(m.find()){
      return new IpPort(m.group(1), Integer.valueOf(m.group(2)));
    }
    return null;
  }

  private static HttpEntity buildRequest(InputStream inputStream, String filePath)
      throws FileNotFoundException {
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.seContentType(ContentType.MULTIPART_FORM_DATA);
    builder.addBinaryBody("file", inputStream, ContentType.APPLICATION_OCTET_STREAM,
        new File(filePath).getName());
    return builder.build();
  }

  private static InputStream getInputStream(ZipFile zf, String planFilePath)
      throws CatalogResourceException, IOException {
    ZipEntry ze = TemplateUtils.getZipEntryZipFile(zf, planFilePath);
    
    if (ze == null) {
      throw new CatalogResourceException("Can't file plan file in the csar. planFilePath = " + planFilePath);
    }
    return zf.getInputStream(ze);
  }

  /**
   * delet package.
   * @param packageName package to delete according packageName
   * @return DeletePackageResponse
   * @throws CatalogResourceException e1
   */
  public static DeletePackageResponse deletePackage(String packageName)
      throws CatalogResourceException {
    try {
      ClientConfig config = new ClientConfig();
      Iwso2RestService wso2Proxy = ConsumerFactory.createConsumer(
          Config.getConfigration().getMsbServerAddr(), config, Iwso2RestService.class);
      DeletePackageResponse response = wso2Proxy.deletePackage(packageName);
      if (response.isSuccess()) {
        return response;
      }
      throw new CatalogResourceException(response.toString());
    } catch (Exception e) {
      throw new CatalogResourceException(
          "Call Delete Package api failed. packageName = " + packageName, e);
    }
  }


  /**
   * start process.
   * @param processId process id
   * @param params params
   * @return StartProcessResponse
   * @throws CatalogResourceException e1
   */
  public static StartProcessResponse startProcess(String processId, Map<String, Object> params)
      throws CatalogResourceException {
    try {
      ClientConfig config = new ClientConfig();
      Iwso2RestService wso2Proxy = ConsumerFactory.createConsumer(
          Config.getConfigration().getMsbServerAddr(), config, Iwso2RestService.class);
      StartProcessResponse response =
          wso2Proxy.startProcess(new StartProcessRequest(processId, params));
      if (response.isSuccess()) {
        return response;
      }
      throw new CatalogResourceException(response.toString());
    } catch (Exception e) {
      throw new CatalogResourceException("Call Start Process api failed.", e);
    }
  }

}
