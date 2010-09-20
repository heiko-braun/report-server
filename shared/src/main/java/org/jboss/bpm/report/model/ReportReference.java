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
package org.jboss.bpm.report.model;

import java.util.*;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public final class ReportReference
{
  private String title;
  private String description;
  private String reportFileName;
  private List<ReportParameter> parameterMetaData = new LinkedList<ReportParameter>();

  public ReportReference(String reportFileName)
  {
    this.reportFileName = reportFileName;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getTitle()
  {
    return title;
  }

  public String getDescription()
  {
    return description;
  }

  public String getReportFileName()
  {
    return reportFileName;
  }

  public List<ReportParameter> getParameterMetaData()
  {
    return parameterMetaData;
  }

  public ReportParameter getParameter(String name)
  {
    ReportParameter match = null;
    for(ReportParameter p : parameterMetaData)
    {
      if(p.getName().equals(name))
      {
        match = p;
        break;
      }
    }

    return match;    
  }

  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReportReference that = (ReportReference) o;

    if (parameterMetaData != null ? !parameterMetaData.equals(that.parameterMetaData) : that.parameterMetaData != null)
      return false;
    if (reportFileName != null ? !reportFileName.equals(that.reportFileName) : that.reportFileName != null)
      return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (title != null ? title.hashCode() : 0);
    result = 31 * result + (reportFileName != null ? reportFileName.hashCode() : 0);
    result = 31 * result + (parameterMetaData != null ? parameterMetaData.hashCode() : 0);
    return result;
  }
}
