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
package org.openo.commontosca.catalog.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.commontosca.catalog.CatalogAppConfiguration;
import org.openo.commontosca.catalog.common.Config;
import org.openo.commontosca.catalog.common.HttpServerAddrConfig;
import org.openo.commontosca.catalog.common.HttpServerPathConfig;
import org.openo.commontosca.catalog.common.MsbAddrConfig;
import org.openo.commontosca.catalog.db.dao.DaoManager;
import org.openo.commontosca.catalog.db.entity.PackageData;
import org.openo.commontosca.catalog.db.exception.CatalogResourceException;
import org.openo.commontosca.catalog.db.resource.PackageManager;
import org.openo.commontosca.catalog.db.util.H2DbServer;
import org.openo.commontosca.catalog.db.util.HibernateSession;
import org.openo.commontosca.catalog.entity.EnumOnboardState;
import org.openo.commontosca.catalog.entity.EnumOperationalState;
import org.openo.commontosca.catalog.entity.EnumProcessState;
import org.openo.commontosca.catalog.entity.EnumUsageState;
import org.openo.commontosca.catalog.entity.response.CsarFileUriResponse;
import org.openo.commontosca.catalog.entity.response.PackageMeta;
import org.openo.commontosca.catalog.model.parser.EnumPackageFormat;
import org.openo.commontosca.catalog.model.parser.ModelParserFactory;
import org.openo.commontosca.catalog.model.parser.yaml.zte.ToscaYamlModelParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

public class PackageWrapperTest {
  private static String resourcePath;
  
  static {
    MsbAddrConfig.setMsbAddress("http://10.74.44.28:80");
    HttpServerAddrConfig.setHttpServerAddress("http://127.0.0.1:8080");
    HttpServerPathConfig.setHttpServerPath("../tomcat/webapps/ROOT/");
    
    CatalogAppConfiguration configuration = new CatalogAppConfiguration();
    Config.setConfigration(configuration);
    Config.getConfigration().setParserType("zte");
    ModelParserFactory.getInstance().put(EnumPackageFormat.TOSCA_YAML, new TestYamlModelParser());
  }
  
  private static PackageManager manager;


  /**
   * startup db session before class.
   * @throws Exception e
   */
  @BeforeClass
  public static void setUpBeforeClass() throws CatalogResourceException {
    H2DbServer.startUp();
    DaoManager.getInstance().setSessionFactory(HibernateSession.init());
    manager = PackageManager.getInstance();
    CatalogAppConfiguration configuration = new CatalogAppConfiguration();
    Config.setConfigration(configuration);
    System.out.println("Set up before class");
  }

  /**
   * create data before test.
   */
  @Before
  public void setUp() throws Exception {
    ArrayList<PackageData> packageList = manager.queryPackage(null, null, null, null, null);
    if (packageList != null && packageList.size() > 0) {
      for (int i = 0; i < packageList.size(); i++) {
        String packageOid = packageList.get(i).getCsarId();
        manager.deletePackage(packageOid);
      }
    }
    ArrayList<PackageData> packageList2 = manager.queryPackage(null, null, null, null, null);
    if (packageList2 != null && packageList2.size() > 0) {
      for (int j = 0; j < packageList2.size(); j++) {
        if (packageList2.get(j).getCsarId().equals("123456")) {
          manager.updatePackage(getPackageData(), "123456");
        } else {
          PackageData packageData = new PackageData();
          packageData = getPackageData();
          manager.addPackage(packageData);
        }
      }
    } else {
      PackageData packageData = new PackageData();
      packageData = getPackageData();
      manager.addPackage(packageData);
    }
  }

  // @Ignore
  @Test
  public void testUploadPackage() throws Exception {
    InputStream ins = null;
    Response result = null;
    Response result1 = null;
    Response result2 = null;
    // PackageData packageData = new PackageData();
    // packageData = getPackageData();

    FormDataContentDisposition fileDetail =
        FormDataContentDisposition.name("fileName").fileName("NanocellGW.csar").build();

    try {
      resourcePath = HibernateSession.class.getResource("/").toURI().getPath();
    } catch (URISyntaxException e1) {
      e1.printStackTrace();
    }
    final String filename = "NanocellGW.csar";
    File packageFile = new File(resourcePath + filename);
    try {
      ins = new FileInputStream(packageFile);
    } catch (FileNotFoundException e2) {
      e2.printStackTrace();
    }
    if (ins != null) {
      try {
        result = PackageWrapper.getInstance().uploadPackage(ins, fileDetail, null);
      } catch (Exception e3) {
        e3.printStackTrace();
      }
    }
    assertNotNull(result);
    assertEquals(200, result.getStatus());
    assertNotNull(result.getEntity());

    try {
      result1 = PackageWrapper.getInstance().uploadPackage(null, fileDetail, null);
    } catch (Exception e4) {
      e4.printStackTrace();
    }
    assertEquals(500, result1.getStatus());

    try {
      result2 = PackageWrapper.getInstance().uploadPackage(ins, null, null);
    } catch (Exception e5) {
      e5.printStackTrace();
    }
    assertEquals(500, result2.getStatus());
  }

  @Test
  public void testQueryPackageById() throws Exception {
    PackageMeta metas = new PackageMeta();
    metas = getPackageMeta();

    Response result = PackageWrapper.getInstance().queryPackageById("123456");
    assertEquals(200, result.getStatus());
    //assertEquals(metas, result.getEntity());
  }

  @Test
  public void testQueryPackageByCond() {
    ArrayList<PackageMeta> metas = new ArrayList<PackageMeta>();
    metas = getPackageMetaList();
    System.out.println("Test query package by Id");
    Response result =
        PackageWrapper.getInstance().queryPackageListByCond("NanocellGW", "ZTE", "V1.0", "false",
            "NSAR");
    assertEquals(200, result.getStatus());
    //assertEquals(metas, result.getEntity());
  }

  @Test
  public void testUpdatePackageStatus() {
    System.out.println("Test update package status");
    Response result =
        PackageWrapper.getInstance().updatePackageStatus("123456", "Enabled", "NotInUse", "true",
            "onBoarding", "true");
    assertEquals(200, result.getStatus());
  }

  @Test
  public void testGetCsarFileUri() {
    System.out.println("Test get csar file uri ");
    CsarFileUriResponse expectResult = new CsarFileUriResponse();
    String csarFileUri =
        getDownloadUriHead() + "/NSAR/ZTE/NanocellGW/v1.0/NanocellGW/images/segw.img";
    String localUri = HttpServerPathConfig.getHttpServerPath()
        + "NSAR/ZTE/NanocellGW/v1.0/NanocellGW/images/segw.img";
    File srcDir = new File(localUri);
    String localPath = srcDir.getAbsolutePath().replace("\\", "/");

    expectResult.setDownloadUri(csarFileUri);
    expectResult.setLocalPath(localPath);
    Response result = PackageWrapper.getInstance().getCsarFileUri("123456", "/images/segw.img");
    assertEquals(200, result.getStatus());
    //assertEquals(expectResult, result.getEntity());
  }

  // @Test
  // public void testDelPackage() {
  // System.out.println("Test delete package ");
  // Response result = PackageWrapper.getInstance().delPackage("123456");
  // assertEquals(204, result.getStatus());
  // try {
  // Thread.sleep(5000);
  // } catch (InterruptedException e1) {
  // e1.printStackTrace();
  // }
  // }

  /**
   * delete data after test.
   */
  @After
  public void tearDown() throws Exception {
    ArrayList<PackageData> packageList = manager.queryPackageByCsarId("123456");
    if (packageList != null && packageList.size() != 0) {
      manager.deletePackage("123456");
    } else {
      return;
    }
    System.out.println("Tear down");
  }

  /**
   * destory db session after class.
   * @throws Exception e
   */
  @AfterClass
  public static void tearDownAfterClass() {
    try {
      HibernateSession.destory();
      DaoManager.getInstance().setDaoNull();
      H2DbServer.shutDown();
    } catch (Exception e1) {
      Assert.fail("Exception" + e1.getMessage());
    }
  }

  private PackageData getPackageData() {
    PackageData packageData = new PackageData();
    packageData.setCsarId("123456");
    packageData.setCreateTime("2016-06-29 03:33:15");
    packageData.setDeletionPending("false");
    packageData.setDownloadUri("/NSAR/ZTE/NanocellGW/v1.0/");
    packageData.setFormat("yml");
    packageData.setModifyTime("2016-06-29 03:33:15");
    packageData.setName("NanocellGW");
    packageData.setOnBoardState("non-onBoarded");
    packageData.setOperationalState("Disabled");
    packageData.setProvider("ZTE");
    packageData.setSize("0.93M");
    packageData.setType("NSAR");
    packageData.setUsageState("InUse");
    packageData.setVersion("V1.0");
    packageData.setProcessState("normal");
    return packageData;
  }

  private ArrayList<PackageMeta> getPackageMetaList() {
    PackageMeta meta = new PackageMeta();
    meta = getPackageMeta();
    ArrayList<PackageMeta> metas = new ArrayList<PackageMeta>();
    metas.add(meta);
    return metas;
  }
  
  private PackageMeta getPackageMeta() {
    PackageMeta meta = new PackageMeta();
    meta.setCreateTime("2016-06-29 03:33:15");
    meta.setCsarId("123456");
    meta.setDeletionPending(false);
    meta.setDownloadUri(getDownloadUriHead() 
        + "/NSAR/ZTE/NanocellGW/v1.0/NanocellGW.csar");
    meta.setFormat("yml");
    meta.setModifyTime("2016-06-29 03:33:15");
    meta.setName("NanocellGW");
    meta.setOperationalState(EnumOperationalState.valueOf("Disabled"));
    meta.setProvider("ZTE");
    meta.setSize("0.93M");
    meta.setType("NSAR");
    meta.setUsageState(EnumUsageState.valueOf("InUse"));
    meta.setVersion("V1.0");
    meta.setOnBoardState(EnumOnboardState.nonOnBoarded.getValue());
    meta.setProcessState(EnumProcessState.valueOf("normal"));
    return meta;
  }
  
  private String getDownloadUriHead() {
    return MsbAddrConfig.getMsbAddress() + "/files/catalog-http";
  }
}
