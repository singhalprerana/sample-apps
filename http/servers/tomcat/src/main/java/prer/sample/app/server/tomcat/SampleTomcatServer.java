package prer.sample.app.server.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.CoyotePrincipal;
import org.apache.catalina.connector.Request;
import org.apache.catalina.filters.SetCharacterEncodingFilter;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import prer.sample.app.server.servlet.SampleFilter;
import prer.sample.app.server.servlet.SampleServlet;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author prerana.singhal on 27/09/2020
 */
public class SampleTomcatServer {
  private final Tomcat server;
  private final int serverPort;
  private final int clientPort;
  private final Set<SampleServlet.ClientType> clientTypes;
  private final String path;
  private final String filter = "/filter";
  private final String asterix = "/*";

  public SampleTomcatServer(int serverPort, int clientPort, String path, Set<SampleServlet.ClientType> clientTypes) {
    server = new Tomcat();
    this.serverPort = serverPort;
    this.clientPort = clientPort;
    this.path = path;
    this.clientTypes = clientTypes;
  }

  public SampleTomcatServer(int serverPort, int clientPort, Set<SampleServlet.ClientType> clientTypes) {
    this(serverPort, clientPort, "", clientTypes);
  }

  public static void main(String[] args) throws Exception {
    int serverPort = Integer.parseInt(args[0]);
    int clientPort = Integer.parseInt(args[1]);

    Set<SampleServlet.ClientType> clientTypes = new HashSet<>();
    if (args.length > 2) {
      for (int i=2; i< args.length; i++) {
        clientTypes.add(Enum.valueOf(SampleServlet.ClientType.class, args[i]));
      }
    } else {
      clientTypes.add(SampleServlet.ClientType.OkHttp);
    }
    SampleTomcatServer server = new SampleTomcatServer(serverPort, clientPort, "/servlet", clientTypes);
    server.start();
    server.await();
  }

  /**
   * Start serving requests.
   */
  public void start() throws Exception {
    server.setPort(serverPort);
    Context ctx = server.addWebapp("/", new File("/Users/prerana.singhal/traceable/sample-apps/http/servers/tomcat/src/main/webapp").getAbsolutePath());

//    LoginConfig config = new LoginConfig();
//    config.setAuthMethod("BASIC");
//    ctx.setLoginConfig(config);
//    ctx.getPipeline().addValve(new BasicAuthenticator());

    ctx.getPipeline().addValve(new AuthenticatorBase() {
      @Override
      protected String getAuthMethod() {
        return null;
      }

      @Override
      public boolean authenticate(Request request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        String auth = null;
        MessageBytes authorization =
            request.getCoyoteRequest().getMimeHeaders()
                .getValue("authorization");

        if (authorization != null) {
          authorization.toBytes();
          ByteChunk authorizationBC = authorization.getByteChunk();
          auth = authorizationBC.toString();
        }
        if (auth == null || !auth.equals("Bearer qwerty123")) {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
          return false;
        }
        return true;
      }
    });

    {
      SampleServlet sampleServlet;
      if (clientPort > 0) {
        sampleServlet = new SampleServlet(clientPort, clientTypes);
      } else {
        sampleServlet = new SampleServlet();
      }
      server.addServlet(ctx, "SampleServlet", sampleServlet);
      ctx.addServletMapping(path + asterix, "SampleServlet");
      ctx.addServletMapping(filter + path + asterix, "SampleServlet");
    }

    {
      FilterDef filterDef = new FilterDef();
      filterDef.setFilterClass(SampleFilter.class.getName());
      filterDef.setFilterName("SampleFilter");
      ctx.addFilterDef(filterDef);
    }

    {
      SetCharacterEncodingFilter filter = new SetCharacterEncodingFilter();
      filter.setEncoding("UTF-8");
      FilterDef filterDef = new FilterDef();
      filterDef.setFilter(filter);
      filterDef.setFilterName("SetCharacterEncodingFilter");
      ctx.addFilterDef(filterDef);
    }

    {
      FilterMap filterMap = new FilterMap();
      filterMap.setFilterName("SampleFilter");
      filterMap.addURLPattern(filter + asterix);
      filterMap.setDispatcher("REQUEST");
      ctx.addFilterMap(filterMap);
    }
    {
      FilterMap filterMap = new FilterMap();
      filterMap.setFilterName("SetCharacterEncodingFilter");
      filterMap.addURLPattern(filter + asterix);
      filterMap.setDispatcher("REQUEST");
      ctx.addFilterMap(filterMap);
    }

//    ctx.setRealm(new BasicRealm());

    server.start();
    System.out.println("Server is started at http://localhost:" + serverPort + path);
  }

  public void await() {
    server.getServer().await();
  }

  public static class BasicRealm extends RealmBase {

    @Override
    protected String getName() {
      return "SampleRealm";
    }

    @Override
    protected String getPassword(String username) {
      return "password";
    }

    @Override
    protected Principal getPrincipal(String username) {
      return new CoyotePrincipal("SampleCoyote");
    }
  }
}
