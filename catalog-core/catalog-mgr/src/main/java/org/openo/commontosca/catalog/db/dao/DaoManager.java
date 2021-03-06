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
package org.openo.commontosca.catalog.db.dao;

import org.hibernate.SessionFactory;

import org.openo.commontosca.catalog.db.common.CatalogResuorceType;
import org.openo.commontosca.catalog.db.exception.CatalogResourceException;

/**
 * DAO manager class.
 * a class to store DAO instances and provide methods to get these instances.
 * 
 */
public class DaoManager {
  private static DaoManager instance = new DaoManager();

  private ServiceTemplateDao serviceTemplateDao;
  private PackageDao packageDao;
  private NodeTemplateDao nodeTemplateDao;
  private TemplateDao templateDao;
  private ServiceTemplateMappingDao templateMappingDao;
  private SessionFactory sessionFactory;

  private DaoManager() {}

  public static synchronized DaoManager getInstance() {
    return instance;
  }

  /**
   * get dao.
   * @param type data type
   * @return BaseDao<?>
   */
  public BaseDao<?> getDao(String type) throws CatalogResourceException{
    if (sessionFactory == null) {
      throw new CatalogResourceException("errorMsg:database connect init faild!");
    }
    switch (CatalogResuorceType.getType(type)) {
      case SERVICETEMPLATE:
        return getServiceTemplateDao();
      case PACKAGE:
        return getPackageDao();
      case NODETEMPLATE:
        return getNodeTemplateDao();
      case TEMPLATE:
        return getTemplateDao();
      case SERVICETEMPLATEMAPPING:
        return getTemplateMappingDao();
      default:
        return null;
    }
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * get service template dao.
   * @return ServiceTemplateDao
   */
  public ServiceTemplateDao getServiceTemplateDao() {
    //if (serviceTemplateDao == null) {
      serviceTemplateDao = new ServiceTemplateDao(sessionFactory);
   // }
    return serviceTemplateDao;
  }

  public void setServiceTemplateDao(ServiceTemplateDao serviceTemplateDao) {
    this.serviceTemplateDao = serviceTemplateDao;
  }

  /**
   * get package dao.
   * @return PackageDao
   */
  public PackageDao getPackageDao() {
   // if (packageDao == null) {
      packageDao = new PackageDao(sessionFactory);
   // }
    return packageDao;
  }

  public void setPackageDao(PackageDao packageDao) {
    this.packageDao = packageDao;
  }

  /**
   * get node templage dao.
   * @return NodeTemplateDao
   */
  public NodeTemplateDao getNodeTemplateDao() {
   // if (nodeTemplateDao == null) {
      nodeTemplateDao = new NodeTemplateDao(sessionFactory);
   // }
    return nodeTemplateDao;
  }

  public void setTemplateDao(TemplateDao templateDao) {
    this.templateDao = templateDao;
  }

  /**
   * get template dao.
   * @return TemplateDao
   */
  public TemplateDao getTemplateDao() {
   // if (templateDao == null) {
      templateDao = new TemplateDao(sessionFactory);
   // }
    return templateDao;
  }

  public void setNodeTemplateDao(NodeTemplateDao nodeTemplateDao) {
    this.nodeTemplateDao = nodeTemplateDao;
  }

  /**
   * get template mapping dao.
   * @return ServiceTemplateMappingDao
   */
  public ServiceTemplateMappingDao getTemplateMappingDao() {
    //if (templateMappingDao == null) {
      templateMappingDao = new ServiceTemplateMappingDao(sessionFactory);
   // }
    return templateMappingDao;
  }

  public void setTemplateMappingDao(ServiceTemplateMappingDao templateMappingDao) {
    this.templateMappingDao = templateMappingDao;
  }

  /**
   * set dao null.
   */
  public void setDaoNull() {
    this.nodeTemplateDao = null;
    this.templateDao = null;
    this.serviceTemplateDao = null;
    this.packageDao = null;
    this.templateMappingDao = null;
  }
}
