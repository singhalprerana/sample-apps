package prer.sample.app.server.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import prer.sample.app.server.servlet.SampleFilter;
import prer.sample.app.server.servlet.SampleServlet;

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
    int serverPort = 50071;
    int clientPort = -1;

    if (args.length > 1) {
      serverPort = Integer.parseInt(args[0]);
      clientPort = Integer.parseInt(args[1]);
    }

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
    ServletHandler handler = new ServletHandler();
    server.setHandler(handler);

    if (clientPort > 0) {
      SampleServlet servletWithClients = new SampleServlet(clientPort,clientTypes);
      handler.addServletWithMapping(new ServletHolder(servletWithClients), path + asterix);
      handler.addServletWithMapping(new ServletHolder(servletWithClients), filter + path + asterix);
    } else {
      handler.addServletWithMapping(SampleServlet.class, path + asterix);
      handler.addServletWithMapping(SampleServlet.class, filter + path + asterix);
    }
    handler.addFilterWithMapping(SampleFilter.class, filter + asterix, FilterMapping.REQUEST);

    server.start();
    System.out.println("Server is started at http://localhost:" + serverPort + path);
  }
}
