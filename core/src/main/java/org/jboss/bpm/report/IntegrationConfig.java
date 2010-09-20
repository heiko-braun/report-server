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

/**
 * Adopts an integration layout (i.e. working directories) to BIRT configuration.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class IntegrationConfig
{
   private String birtHome = null;
   private String reportDir = null;   
   private String outputDir = null;
   private String imageBaseUrl = null;

   public String getBirtHome()
   {
      return birtHome;
   }

   public void setBirtHome(String birtHome)
   {
      this.birtHome = birtHome;
   }

   public String getReportDir()
   {
      return reportDir;
   }

   public void setReportDir(String reportDir)
   {
      this.reportDir = ensureDirectoryName(reportDir);
   }

   public String getOutputDir()
   {
      return outputDir;
   }

   public void setOutputDir(String outputDir)
   {
      this.outputDir = ensureDirectoryName(outputDir);
   }

   public void setImageBaseUrl(String imageBaseUrl)
   {
      this.imageBaseUrl = ensureDirectoryName(imageBaseUrl);
   }


   public String getImageDirectory()
   {
      return getOutputDir()+"image/";
   }

   private String ensureDirectoryName(String name)
   {
      if(name.endsWith("/"))
         return name;
      else
         return name+"/";
   }
   
}
