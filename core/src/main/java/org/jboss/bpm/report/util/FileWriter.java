/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.report.util;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Provider
@Produces({"text/html", "image/*"})
public class FileWriter implements MessageBodyWriter
{

   public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      return aClass.equals(java.io.File.class);
   }

   public long getSize(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      return ((File)o).length();
   }

   public void writeTo(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException
   {
      FileInputStream fin = new FileInputStream((File)o);
      int c;
      while ((c = fin.read()) != -1)
      {
         outputStream.write(c);
      }

      fin.close();

   }
}
