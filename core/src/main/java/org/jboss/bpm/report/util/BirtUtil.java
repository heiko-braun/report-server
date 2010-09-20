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

import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.CascadingParameterGroupHandle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.bpm.report.BirtService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class BirtUtil
{
  private static final Log log = LogFactory.getLog(BirtService.class);
  
  //Function to load parameter details in a map.
  public static HashMap<String, Serializable> loadParameterDetails(
      IGetParameterDefinitionTask task,
      IScalarParameterDefn scalar,
      IReportRunnable report,
      IParameterGroupDefn group
  )
  {
    HashMap<String, Serializable> parameter = new HashMap<String, Serializable>();

    if( group == null){
      parameter.put("Parameter Group", "Default");
    }else{
      parameter.put("Parameter Group", group.getName());
    }
    parameter.put("Name", scalar.getName());
    parameter.put("Help Text", scalar.getHelpText());
    parameter.put("Display Name", scalar.getDisplayName());
    //this is a format code such as  > for UPPERCASE
    parameter.put("Display Format", scalar.getDisplayFormat());

    if( scalar.isHidden() ){
      parameter.put("Hidden", "Yes");
    }else{
      parameter.put("Hidden", "No");
    }
    if( scalar.allowBlank() ){
      parameter.put("Allow Blank", "Yes");
    }else{
      parameter.put("Allow Blank", "No");
    }
    if( scalar.allowNull() ){
      parameter.put("Allow Null", "Yes");
    }else{
      parameter.put("Allow Null", "No");
    }
    if( scalar.isValueConcealed() ){
      parameter.put("Conceal Entry", "Yes");  //ie passwords etc
    }else{
      parameter.put("Conceal Entry", "No");
    }


    switch (scalar.getControlType()) {
      case IScalarParameterDefn.TEXT_BOX:  parameter.put("Type", "TEXTBOX"); break;
      case IScalarParameterDefn.LIST_BOX:  parameter.put("Type", "LISTBOX"); break;
      case IScalarParameterDefn.RADIO_BUTTON:  parameter.put("Type", "RADIO_BUTTON"); break;
      case IScalarParameterDefn.CHECK_BOX:  parameter.put("Type", "CHECKBOX"); break;
      default: parameter.put("Type", "TEXTBOX");break;
    }


    switch (scalar.getDataType()) {
      case IScalarParameterDefn.TYPE_STRING:  parameter.put("DataType", "STRING"); break;
      case IScalarParameterDefn.TYPE_FLOAT:  parameter.put("DataType", "NUMBER"); break;
      case IScalarParameterDefn.TYPE_DECIMAL:  parameter.put("DataType", "NUMBER"); break;
      case IScalarParameterDefn.TYPE_DATE_TIME:  parameter.put("DataType", "DATETIME"); break;
      case IScalarParameterDefn.TYPE_BOOLEAN:  parameter.put("DataType", "BOOLEAN"); break;
      case IScalarParameterDefn.TYPE_DATE:  parameter.put("DataType", "DATETIME"); break;
      default:  parameter.put("DataType", "ANY"); break;
    }


    //Get report design and find default value, prompt text and data set expression using the DE API
    ReportDesignHandle reportHandle = ( ReportDesignHandle ) report.getDesignHandle( );
    ScalarParameterHandle parameterHandle = (ScalarParameterHandle) reportHandle.findParameter( scalar.getName() );
    parameter.put("Default Value", parameterHandle.getDefaultValue());
    parameter.put("Prompt Text", parameterHandle.getPromptText());
    parameter.put("Data Set Expression", parameterHandle.getValueExpr());

    if(scalar.getControlType() !=  IScalarParameterDefn.TEXT_BOX)
    {
      //retrieve selection list for cascaded parameter
      if ( parameterHandle.getContainer( ) instanceof CascadingParameterGroupHandle){
        Collection sList = Collections.EMPTY_LIST;
        if ( parameterHandle.getContainer( ) instanceof CascadingParameterGroupHandle )
        {
          int index = parameterHandle.getContainerSlotHandle( )
              .findPosn( parameterHandle );
          Object[] keyValue = new Object[index];
          for ( int i = 0; i < index; i++ )
          {
            ScalarParameterHandle handle = (ScalarParameterHandle) ( (CascadingParameterGroupHandle) parameterHandle.getContainer( ) ).getParameters( )
                .get( i );
            //Use parameter default values
            keyValue[i] = handle.getDefaultValue();
          }
          String groupName = parameterHandle.getContainer( ).getName( );
          task.evaluateQuery( groupName );

          sList = task.getSelectionListForCascadingGroup( groupName, keyValue );
          HashMap<Object, String> dynamicList = new HashMap<Object, String>();


          for ( Iterator sl = sList.iterator( ); sl.hasNext( ); )
          {
            IParameterSelectionChoice sI = (IParameterSelectionChoice) sl.next( );


            Object value = sI.getValue( );
            Object label = sI.getLabel( );
            log.debug( label + "--" + value);
            dynamicList.put(value,(String) label);

          }
          parameter.put("Selection List", dynamicList);


        }
      }else{
        //retrieve selection list
        Collection selectionList = task.getSelectionList( scalar.getName() );

        if ( selectionList != null )
        {
          HashMap<Object, String> dynamicList = new HashMap<Object, String>();

          for ( Iterator sliter = selectionList.iterator( ); sliter.hasNext( ); )
          {
            IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next( );

            Object value = selectionItem.getValue( );
            String label = selectionItem.getLabel( );
            //log.debug( label + "--" + value);
            dynamicList.put(value,label);

          }
          parameter.put("Selection List", dynamicList);
        }
      }

    }


    //Print out results
    Iterator iter = parameter.keySet().iterator();
    log.debug("======================Parameter =" + scalar.getName());
    while (iter.hasNext()) {
      String name = (String) iter.next();
      if( name.equals("Selection List")){
        HashMap selList = (HashMap)parameter.get(name);
        Iterator selIter = selList.keySet().iterator();
        while (selIter.hasNext()) {
          Object lbl = selIter.next();
          log.debug( "Selection List Entry ===== Key = " + lbl + " Value = " + selList.get(lbl));
        }

      }else{
        log.debug( name + " = " + parameter.get(name));
      }
    }
    return parameter;
  }
}
