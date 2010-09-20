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

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ReportParameter
{
  public enum Type {LISTBOX, TEXTBOX, CHECKBOX, RADIO_BUTTON}
  public enum DataType {STRING, NUMBER, DATETIME, BOOLEAN, ANY}

  private String name;
  private String helptext;
  private String promptText;

  private Type type;
  private DataType dataType = DataType.STRING;

  public ReportParameter(String name, Type type)
  {
    this.name = name;
    this.type = type;
  }

  public String getName()
  {
    return name;
  }

  public Type getType()
  {
    return type;
  }

  public String getHelptext()
  {
    return helptext;
  }

  public void setHelptext(String helptext)
  {
    this.helptext = helptext;
  }

  public String getPromptText()
  {
    return promptText;
  }

  public void setPromptText(String promptText)
  {
    this.promptText = promptText;
  }

  public DataType getDataType()
  {
    return dataType;
  }

  public void setDataType(DataType dataType)
  {
    this.dataType = dataType;
  }


  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReportParameter that = (ReportParameter) o;

    if (dataType != that.dataType) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (type != that.type) return false;

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (name != null ? name.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
    return result;
  }
}
