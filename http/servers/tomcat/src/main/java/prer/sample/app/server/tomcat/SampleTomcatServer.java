package prer.sample.app.server.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import prer.sample.app.server.servlet.SampleFilter;
import prer.sample.app.server.servlet.SampleServlet;

import java.io.File;
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
    SampleTomcatServer server = new SampleTomcatServer(serverPort, clientPort, clientTypes);
    server.start();
    server.await();
  }

  /**
   * Start serving requests.
   */
  public void start() throws Exception {
    server.setPort(serverPort);
    Context ctx = server.addContext("/", new File(".").getAbsolutePath());

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

    FilterDef filterDef = new FilterDef();
    filterDef.setFilterClass(SampleFilter.class.getName());
    filterDef.setFilterName("SampleFilter");
    ctx.addFilterDef(filterDef);

    FilterMap filterMap = new FilterMap();
    filterMap.setFilterName("SampleFilter");
    filterMap.addURLPattern(filter + asterix);
    filterMap.setDispatcher("REQUEST");
    ctx.addFilterMap(filterMap);

    server.start();
    System.out.println("Server is started at http://localhost:" + serverPort + path);
  }

  public void await() {
    server.getServer().await();
  }
}
