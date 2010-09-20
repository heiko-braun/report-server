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

import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.bpm.report.model.ReportReference;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * BIRT integration facade.<p>
 *
 * Uses the server data directory (i.e. <code>$JBOSS_HOME/server/default/data/birt</code>)
 * as it's work directory (referred to as $WORK_DIR in the subsequent sections):
 *
 * <ul>
 *    <li>report template location: $WORK_DIR/
 *    <li>output directory: $WORK_DIR/output
 * </ul>
 *
 * NOTE: It requires a BIRT report engine to be installed in the work directory:
 * (<code>$WORK_DIR/ReportEngine</code>.
 *
 * @see org.jboss.bpm.report.JMXServerConfig
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("report")
public class ReportFacade
{
  private static final Log log = LogFactory.getLog(ReportFacade.class);
  private BirtService birtService;
  private boolean isInitialized;
  private boolean initAttempt;

  public ReportFacade()
  {
    try
    {
      if(!initAttempt) initBirtService();
    }
    catch (BirtInitException e)
    {
      initAttempt = true; // RIFTSAW-111: gracefully exit when BIRT not installed
      log.warn("Failed to initialize BIRT service. Please check the DEBUG log for further details.");
      log.debug("Initialization failed", e);
    }
  }

  public void initBirtService()
      throws BirtInitException
  {
    if(!isInitialized)
    {
      File serverDataDir = resolveBirtDataDir();

      IntegrationConfig iConfig = new IntegrationConfig();      
      String absServerDataDir = serverDataDir.getAbsolutePath();

      String birtDataDir = absServerDataDir;
      String defaultBirtHome = birtDataDir + "/ReportEngine";
      String birtOutputDir = birtDataDir + "/output";

      File birtOutput = new File(birtOutputDir);
      birtOutput.mkdirs(); // will create parent directoy as well

      // check dependency on ReportEngine
      if(! new File(defaultBirtHome).exists())
        throw new BirtInitException("The BIRT report engine doesn't seem to be installed:" +defaultBirtHome);

      // --

      iConfig.setBirtHome(defaultBirtHome);
      iConfig.setOutputDir( birtOutputDir );
      iConfig.setReportDir( birtDataDir );

      log.info("BIRT home: " +iConfig.getBirtHome());
      log.info("Output dir: " +iConfig.getOutputDir());
      log.info("Report dir: " +iConfig.getReportDir());

      try
      {
        this.birtService = new BirtService(iConfig);
        this.birtService.createAsync();
      }
      catch (Throwable t)
      {
        throw new BirtInitException(t.getMessage(), t);
      }

      isInitialized = true;
    }
  }

  private File resolveBirtDataDir()
  {
    JMXServerConfig jmxConfig = null;
    File serverDataDir = null;
    
    // first verify if custom location of BIRT is given
    if (System.getProperty("org.jbpm.report.engine.dir") != null) {
      if (log.isDebugEnabled()) {
        log.debug("Checks custom location defined with JVM parameter '-Dorg.jbpm.report.engine.dir' = " + System.getProperty("org.jbpm.report.engine.dir"));
      }
      serverDataDir = new File(System.getProperty("org.jbpm.report.engine.dir"));
      
      // file must exist
      if (serverDataDir != null && serverDataDir.exists()){
        return serverDataDir;
      }
    }
    
    try
    {
      // next rely on JBoss specific settings
      jmxConfig = new JMXServerConfig();
      serverDataDir = new File(jmxConfig.getServerDataDir(), "birt");
    }
    catch (Exception e)
    {
      // last rely on Tomcat settings
      // fallback on on CATALINA_HOME or blow up
      log.warn("Resolving serverDataDir based on -Dcatalina.home");
      if(System.getProperty("catalina.home")!=null)
        serverDataDir = new File(System.getProperty("catalina.home") + "/birt");
      else
        throw new IllegalStateException("Neither JMX config nor '-Dcatalina.home' nor '-Dorg.jbpm.report.engine.dir' available to resolve serverDataDir");
    }

    return serverDataDir;
  }

  @GET
  @Path("render/{fileName}")
  @Produces("text/html")
  public Response viewReportHtml(
      @PathParam("fileName")
      String fileName,
      @Context HttpServletRequest request
  )
  {
    assertBirtAvailability();

    try
    {
      RenderMetaData renderMeta = defaultRenderMetaData(fileName, request);

      String outputFileName = birtService.view(renderMeta);
      String absoluteFile = birtService.getIntegrationConfig().getOutputDir() + outputFileName;
      log.debug("View " + absoluteFile);

      File reportFile = new File(absoluteFile);
      return Response.ok(reportFile).type("text/html").build();
    }
    catch(Throwable e1)
    {
      return gracefulException(e1);
    }
  }

  private void assertBirtAvailability()
  {
    if(!isInitialized)
      throw new IllegalStateException("Report server not initialized. " +
          "Please check the server logs for further details.");
  }

  @POST
  @Path("render/{fileName}")
  @Produces("text/html")
  public Response renderReportHtml(
      @PathParam("fileName")
      String fileName,
      @Context HttpServletRequest request
  )
  {

    assertBirtAvailability();

    try
    {
      RenderMetaData renderMeta = defaultRenderMetaData(fileName, request);
      Map<String,String> postParams = convertRequestParametersToMap(request);
      renderMeta.getParameters().putAll(postParams);

      String outputFileName = birtService.render(renderMeta);
      String absoluteFile = birtService.getIntegrationConfig().getOutputDir() + outputFileName;
      log.debug("Render " + absoluteFile);

      return Response.ok().type("text/html").build();
    }
    catch(Throwable e1)
    {
      return gracefulException(e1);
    }
  }

  @GET
  @Path("view/image/{fileName}")
  public Response getImage(
      @PathParam("fileName")
      String fileName,
      @Context HttpServletRequest
          request
  )
  {
    assertBirtAvailability();

    String imageDir = birtService.getIntegrationConfig().getImageDirectory();
    String absName = imageDir + fileName;
    File imageFile = new File(absName);
    if(!imageFile.exists())
      throw new IllegalArgumentException("Image " +absName+" doesn't exist");
    return Response.ok(imageFile).build();
  }

  private RenderMetaData defaultRenderMetaData(String fileName, HttpServletRequest request)
  {
    RenderMetaData renderMeta = new RenderMetaData();
    renderMeta.setReportName(fileName);
    renderMeta.setFormat(RenderMetaData.Format.HTML);
    renderMeta.setClassloader(Thread.currentThread().getContextClassLoader());
    renderMeta.setImageBaseUrl(buildImageUrl(request));
    return renderMeta;
  }

  private String buildImageUrl(HttpServletRequest request)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("http://");
    sb.append(request.getServerName()).append(":");
    sb.append(request.getServerPort());
    sb.append(request.getContextPath());
    sb.append(request.getServletPath());
    sb.append("/report/view/image");
    return sb.toString();
  }

  private Response gracefulException(Throwable e)
  {
    log.error("Error processing report", e);

    StringBuffer sb = new StringBuffer();
    sb.append("<div style='font-family:sans-serif; padding:10px;'>");
    sb.append("<h3>Unable to process report").append("</h3>");
    sb.append(e.getMessage());
    sb.append("</div>");
    return Response.ok(sb.toString()).status(400).build();
  }

  static public Map<String, String> convertRequestParametersToMap(HttpServletRequest request){
    HashMap<String, String> parameterMap = new HashMap<String, String>();
    BufferedReader br = null;
    try
    {

      br = request.getReader();
      String line;
      while ((line = br.readLine()) != null)
      {
        StringTokenizer st = new StringTokenizer(line, ";");
        while(st.hasMoreTokens())
        {
          String s = st.nextToken();
          if(s.indexOf("=")!=-1)
          {
            String[] tuple = s.split("=");
            parameterMap.put(tuple[0], tuple[1]);
          }
        }
      }
    }
    catch (IOException e)
    {
      log.error("Failed to parse report parameters", e);
    }
    finally{
      if(br!=null)
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          //
        }
    }

    return parameterMap;
  }

  @GET
  @Path("config")
  @Produces("application/json")
  public Response getReportConfig()
  {
    assertBirtAvailability();

    List<ReportReference> refs = birtService.getReportReferences();
    String json = new GsonBuilder().create().toJson(refs);
    return Response.ok(json).build();
  }

  public class BirtInitException extends Exception
  {
    public BirtInitException(String message)
    {
      super(message);
    }

    public BirtInitException(String message, Throwable cause)
    {
      super(message, cause);
    }
  }

}
