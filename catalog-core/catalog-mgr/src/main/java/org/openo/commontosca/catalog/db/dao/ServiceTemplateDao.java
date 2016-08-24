/**
 * Copyright 2016 [ZTE] and others.
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
import org.openo.commontosca.catalog.db.entity.ServiceTemplateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class
 * 
 * *@author 10159474
 * 
 */
public class ServiceTemplateDao extends BaseDao<ServiceTemplateData> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateDao.class);

    public ServiceTemplateDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


}