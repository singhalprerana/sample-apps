package prer.sample.app.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author prerana.singhal on 21/09/2020
 */
public class SampleGrpcServer {

  private final int port;
  private final Server server;

  public SampleGrpcServer(int serverPort, int clientPort) throws IOException {
    this.port = serverPort;
    SampleGrpcClient grpcClient = clientPort < 0 ? null : new SampleGrpcClient(clientPort);
    server = ServerBuilder.forPort(serverPort).addService(new SampleGrpcService(grpcClient, serverPort)).build();
  }

  public static void main(String[] args) throws Exception {
    int serverPort = Integer.parseInt(args[0]);
    int clientPort = Integer.parseInt(args[1]);

    SampleGrpcServer server = new SampleGrpcServer(serverPort, clientPort);
    server.start();
    server.blockUntilShutdown();
  }

  /**
   * Start serving requests.
   */
  public void start() throws IOException {
    server.start();
    System.out.println("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          SampleGrpcServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  /**
   * Stop serving requests and shutdown resources.
   */
  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

}
