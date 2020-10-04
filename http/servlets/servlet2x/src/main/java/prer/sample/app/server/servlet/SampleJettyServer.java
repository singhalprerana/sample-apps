package prer.sample.app.server.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author prerana.singhal on 27/09/2020
 */
public class SampleJettyServer {
  private final int serverPort;
  private final int clientPort;
  private final Set<SampleServlet.ClientType> clientTypes;
  private final String path;
  private final String filter = "/filter";
  private final String asterix = "/*";

  public SampleJettyServer(int serverPort, int clientPort, String path, Set<SampleServlet.ClientType> clientTypes) {
    this.serverPort = serverPort;
    this.clientPort = clientPort;
    this.path = path;
    this.clientTypes = clientTypes;
  }

  public SampleJettyServer(int serverPort, int clientPort, Set<SampleServlet.ClientType> clientTypes) {
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
    SampleJettyServer server = new SampleJettyServer(serverPort, clientPort, clientTypes);
    server.start();
  }

  /**
   * Start serving requests.
   */
  public void start() throws Exception {
    Server server = new Server(serverPort);
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    server.setHandler(handler);

    if (clientPort > 0) {
      SampleServlet servletWithClients = new SampleServlet(clientPort,clientTypes);
      handler.addServlet(new ServletHolder(servletWithClients), path + asterix);
      handler.addServlet(new ServletHolder(servletWithClients), filter + path + asterix);
    } else {
      handler.addServlet(SampleServlet.class, path + asterix);
      handler.addServlet(SampleServlet.class, filter + path + asterix);
    }
    handler.addFilter(SampleFilter.class, filter + asterix, FilterMapping.REQUEST);

    server.start();
    System.out.println("Server is started at http://localhost:" + serverPort + path);
  }
}
