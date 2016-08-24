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
package org.openo.commontosca.catalog.model.externalservice.container;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.openo.commontosca.catalog.model.externalservice.entity.containerEntity.ContainerServicePackageList;

/**
 * The opentosca container service returns data to the package entity
 * @author 10189609
 *
 */
public class ContainerServicePackageProvider implements MessageBodyReader<ContainerServicePackageList> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return ContainerServicePackageList.class.isAssignableFrom(type);
	}

	@Override
	public ContainerServicePackageList readFrom(
			Class<ContainerServicePackageList> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		ContainerServicePackageList packageList = null;
		
		try 
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(ContainerServicePackageList.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			packageList = (ContainerServicePackageList) unmarshaller.unmarshal(entityStream);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return packageList;
	}

}