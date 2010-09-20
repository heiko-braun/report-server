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

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.exception.BirtException;

/**
 * Constructs an {@link IReportEngine} from {@link org.jboss.bpm.report.IntegrationConfig}
 * 
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class BirtEngineFactory
{
   public static IReportEngine newInstance(IntegrationConfig iConfig)
   {
      try
      {
         EngineConfig config = new EngineConfig( );
         config.setBIRTHome(iConfig.getBirtHome());

         Platform.startup( config );
         IReportEngineFactory factory = (IReportEngineFactory) Platform
               .createFactoryObject( IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );

         IReportEngine engine = factory.createReportEngine( config );
         return engine;
      }
      catch (BirtException e)
      {
         throw new RuntimeException("Failed to create birt engine", e);
      }
   }
}
