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
package org.jboss.bpm.report;

import javax.management.*;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Hooks into JMX to get the JBoss AS configuration.
 * (temp directories, etc)
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class JMXServerConfig
{
   // The MBeanServer
   private MBeanServer mbeanServer;

   public JMXServerConfig()
   {
      this.mbeanServer = getDefaultMBeanServer();
   }

   public MBeanServer getMbeanServer()
   {
      return mbeanServer;
   }

   public void setMbeanServer(MBeanServer mbeanServer)
   {
      this.mbeanServer = mbeanServer;
   }

   public File getServerTempDir()
   {
      try
      {
         ObjectName oname = ObjectNameFactory.create("jboss.system:type=ServerConfig");
         File dir = (File)getMbeanServer().getAttribute(oname, "ServerTempDir");
         return dir;
      }
      catch (JMException e)
      {
         return null;
      }
   }

   public File getHomeDir()
   {
      try
      {
         ObjectName oname = ObjectNameFactory.create("jboss.system:type=ServerConfig");
         File dir = (File)getMbeanServer().getAttribute(oname, "HomeDir");
         return dir;
      }
      catch (JMException e)
      {
         return null;
      }
   }

   public File getServerDataDir()
   {
      try
      {
         ObjectName oname = ObjectNameFactory.create("jboss.system:type=ServerConfig");
         File dir = (File)getMbeanServer().getAttribute(oname, "ServerDataDir");
         return dir;
      }
      catch (JMException e)
      {
         return null;
      }
   }

   public static MBeanServer getDefaultMBeanServer() {
      return findMBeanServer("jboss");
   }

   private static MBeanServer findMBeanServer(String agentId) {
      List servers = MBeanServerFactory.findMBeanServer(null);
      if (servers != null && servers.size() > 0) {
         for (Object object : servers) {
            MBeanServer server = (MBeanServer) object;
            if (server.getDefaultDomain().equals(agentId)) {
               return server;
            }
         }
      }
      return null;
   }

   public static class ObjectNameFactory
   {
      public static ObjectName create(String name)
      {
         try
         {
            return new ObjectName(name);
         }
         catch (MalformedObjectNameException e)
         {
            throw new Error("Invalid ObjectName: " + name + "; " + e);
         }
      }

      public static ObjectName create(String domain, String key, String value)
      {
         try
         {
            return new ObjectName(domain, key, value);
         }
         catch (MalformedObjectNameException e)
         {
            throw new Error("Invalid ObjectName: " + domain + "," + key + "," + value + "; " + e);
         }
      }

      public static ObjectName create(String domain, Hashtable table)
      {
         try
         {
            return new ObjectName(domain, table);
         }
         catch (MalformedObjectNameException e)
         {
            throw new Error("Invalid ObjectName: " + domain + "," + table + "; " + e);
         }
      }
   }
}
