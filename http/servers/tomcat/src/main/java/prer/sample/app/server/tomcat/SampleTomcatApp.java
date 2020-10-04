package prer.sample.app.server.tomcat;

import prer.sample.app.client.grizzly.SampleGrizzlyClient;
import prer.sample.app.client.okhttp.SampleOkHttpClient;
import prer.sample.app.server.servlet.SampleServlet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author prerana.singhal on 04/10/2020
 */
public class SampleTomcatApp {

  private final int upstreamPort;
  private final int downstreamPort;

  public SampleTomcatApp(int upstreamPort, int downstreamPort) {
    this.upstreamPort = upstreamPort;
    this.downstreamPort = downstreamPort;
  }

  public static void main(String[] args) throws Exception {
    SampleTomcatApp sampleTomcatApp = new SampleTomcatApp(50071, 50072);
    if (args.length > 1) {
      sampleTomcatApp = new SampleTomcatApp(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
    Set<SampleServlet.ClientType> clientTypes = new HashSet<>();
    if (args.length > 2) {
      for (int i=2; i< args.length; i++) {
        clientTypes.add(Enum.valueOf(SampleServlet.ClientType.class, args[i]));
      }
    } else {
      clientTypes.add(SampleServlet.ClientType.OkHttp);
    }
    sampleTomcatApp.runFullApp(clientTypes);
  }

  public void runFullApp(Set<SampleServlet.ClientType> clientTypes) throws Exception {
    if (clientTypes == null || clientTypes.isEmpty()) {
      clientTypes = new HashSet<>();
      clientTypes.add(SampleServlet.ClientType.OkHttp);
    }
    SampleTomcatServer downstreamServer = new SampleTomcatServer(downstreamPort, -1, null);
    downstreamServer.start();

    SampleTomcatServer upstreamServer = new SampleTomcatServer(upstreamPort, downstreamPort, clientTypes);
    upstreamServer.start();

    if (clientTypes.contains(SampleServlet.ClientType.OkHttp)) {
      SampleOkHttpClient okHttpClient = new SampleOkHttpClient(upstreamPort);
      Thread thread1 = new Thread(){
        public void run(){
          try {
            okHttpClient.runClientApp("/get", "/form", "/json", "/text");
          } catch (Exception e) {}
        }
      };
      Thread thread2 = new Thread(){
        public void run(){
          try {
            okHttpClient.runClientApp("/filter/get", "/filter/form", "/filter/json", "/filter/text");
          } catch (Exception e) {}
        }
      };
      thread1.start();
      thread2.start();
    }
    if (clientTypes.contains(SampleServlet.ClientType.Grizzly)) {
      SampleGrizzlyClient grizzlyClient = new SampleGrizzlyClient(upstreamPort);
      Thread thread1 = new Thread(){
        public void run(){
          try {
            grizzlyClient.runClientApp("/get", "/form", "/json", "/text");
          } catch (Exception e) {}
        }
      };
      Thread thread2 = new Thread(){
        public void run(){
          try {
            grizzlyClient.runClientApp("/filter/get", "/filter/form", "/filter/json", "/filter/text");
          } catch (Exception e) {}
        }
      };
      thread1.start();
      thread2.start();
    }
  }

}
