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

import java.util.Map;
import java.util.HashMap;

/**
 * Drives a {@link org.jboss.bpm.report.BirtService#render(RenderMetaData)} call.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class RenderMetaData
{
  public enum Format {HTML, PDF};

  private ClassLoader classloader;
  private String reportName;
  private Format format;
  private Map<String,String> parameters = new HashMap<String,String>();
  private String imageBaseUrl;

  public RenderMetaData()
  {
  }

  public RenderMetaData(ClassLoader classloader, String reportName, Format format, String imageBaseUrl)
  {
    this.classloader = classloader;
    this.reportName = reportName;
    this.format = format;
    this.imageBaseUrl = imageBaseUrl;
  }

  public String getImageBaseUrl()
  {
    return imageBaseUrl;
  }

  public void setImageBaseUrl(String imageBaseUrl)
  {
    this.imageBaseUrl = imageBaseUrl;
  }

  public ClassLoader getClassloader()
  {
    return classloader;
  }

  public void setClassloader(ClassLoader classloader)
  {
    this.classloader = classloader;
  }

  public String getReportName()
  {
    return reportName;
  }

  public void setReportName(String reportName)
  {
    this.reportName = reportName;
  }

  public Format getFormat()
  {
    return format;
  }

  public void setFormat(Format format)
  {
    this.format = format;
  }


  public String toString()
  {
    return "RenderMetaData {reportName="+reportName+", format="+format+"}";
  }

  public Map<String,String> getParameters()
  {
    return parameters;
  }
}
