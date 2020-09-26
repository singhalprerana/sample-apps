package prer.sample.app.grpc;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author prerana.singhal on 21/09/2020
 */
public class SampleGrpcApp {

  private final int upstreamPort;
  private final int downstreamPort;

  public SampleGrpcApp(int upstreamPort, int downstreamPort) {
    this.upstreamPort = upstreamPort;
    this.downstreamPort = downstreamPort;
  }

  public static void main(String[] args) throws Exception {
    SampleGrpcApp sampleGrpcApp = new SampleGrpcApp(50051, 50052);
    if (args.length > 1) {
      sampleGrpcApp = new SampleGrpcApp(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    sampleGrpcApp.runFullApp();
  }

  public void runFullApp() throws IOException, InterruptedException {
    SampleGrpcServer downstreamServer = new SampleGrpcServer(downstreamPort, -1);
    downstreamServer.start();

    SampleGrpcServer upstreamServer = new SampleGrpcServer(upstreamPort, downstreamPort);
    upstreamServer.start();

    SampleGrpcClient grpcClient = new SampleGrpcClient(upstreamPort);

    while (true) {
      Thread.sleep(1000);
      grpcClient.sayHello("Expecto", "Patronum");
      Thread.sleep(1000);
      grpcClient.getWords("To the well-organized mind, death is but the next great adventure.");
      Thread.sleep(1000);
      grpcClient.getLine(Arrays.asList("I", "solemnly", "swear,", "I'm", "upto", "no", "good"));
      Thread.sleep(1000);
      grpcClient.streamLines(Arrays.asList("Do not pity", "the dead, Harry. Pity", "the living. And,", "above all,", "those who live", "without love."));
      Thread.sleep(1000);
    }
  }

}
