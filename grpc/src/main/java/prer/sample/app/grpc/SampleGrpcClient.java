package prer.sample.app.grpc;

import ai.traceable.sample.app.grpc.sample.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author prerana.singhal on 21/09/2020
 */
public class SampleGrpcClient {
  private final SampleServiceGrpc.SampleServiceBlockingStub blockingStub;
  private final SampleServiceGrpc.SampleServiceStub asyncStub;
  private final ManagedChannel channel;
  private final String client;
  private final Random random = new Random();

  /**
   * Construct client for accessing HelloWorld server using the existing channel.
   */
  public SampleGrpcClient(int port) {
    channel = ManagedChannelBuilder.forTarget("localhost:" + port).usePlaintext().build();
    blockingStub = SampleServiceGrpc.newBlockingStub(channel);
    asyncStub = SampleServiceGrpc.newStub(channel);
    client = "Client-for-" + port;
  }

  public static void main(String[] args) throws Exception {
    int serverPort = Integer.parseInt(args[0]);

    SampleGrpcClient grpcClient = new SampleGrpcClient(serverPort);
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

  // A simple RPC.
  public void sayHello(String firstName, String lastName) {
    System.out.println("\n" + client + " starting to send request for 'sayHello'");
    Name request = Name.newBuilder().build().newBuilder().setFirstName(firstName).setLastName(lastName).build();
    try {
      Name response = blockingStub.sayHello(request).getName();
      System.out.println(client + " received response at 'sayHello' for: " + response.getFirstName() + " " + response.getLastName());
    } catch (StatusRuntimeException e) {
      System.out.println(client + " error at 'sayHello': " + e.getStatus());
      return;
    }
  }

  // A server-to-client streaming RPC.
  public void getWords(String line) {
    System.out.println("\n" + client + " starting to send request for 'getWords'");
    Line request = Line.newBuilder().build().newBuilder().setLine(line).build();
    try {
      Iterator<ChunkedWord> responseIterator = blockingStub.getWords(request);
      List<String> sb = new ArrayList<>();
      responseIterator.forEachRemaining(s -> sb.add(s.getWord()));
      System.out.println(client + " received response at 'getWords' as: " + String.join(",", sb));
    } catch (StatusRuntimeException e) {
      System.out.println(client + " error at 'getWords': " + e.getStatus());
      return;
    }
  }

  // A client-to-server streaming RPC.
  public void getLine(List<String> words) throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<Line> responseObserver = new StreamObserver<Line>() {
      @Override
      public void onNext(Line line) {
        System.out.println(client + " received response at 'getLine': " + line.getLine());
      }

      @Override
      public void onError(Throwable t) {
        System.out.println(client + " Response error at 'getLine': " + t);
        finishLatch.countDown();
      }

      @Override
      public void onCompleted() {
        System.out.println(client + " Response completed at 'getLine'\n");
        finishLatch.countDown();
      }
    };

    StreamObserver<ChunkedWord> requestObserver = asyncStub.getLine(responseObserver);
    System.out.println("\n" + client + " starting to send request for 'getLine'");
    try {
      for (String word : words) {
        ChunkedWord.Builder wordBuilder = ChunkedWord.newBuilder();
        if (word.endsWith(",")) {
          wordBuilder.setWord(word.substring(0, word.length() - 1));
          wordBuilder.setAddComma(true);
        } else {
          wordBuilder.setWord(word);
        }
        requestObserver.onNext(wordBuilder.build());
        // Sleep for a bit before sending the next one.
        Thread.sleep(random.nextInt(100) + 50);
        if (finishLatch.getCount() == 0) {
          // RPC completed or errored before we finished sending.
          // Sending further requests won't error, but they will just be thrown away.
          return;
        }
      }
    } catch (RuntimeException e) {
      // Cancel RPC
      requestObserver.onError(e);
      throw e;
    }

    // Mark the end of requests
    requestObserver.onCompleted();
    System.out.println(client + " finished sending request for 'getLine'");

    // Receiving happens asynchronously
    finishLatch.await(1, TimeUnit.MINUTES);
  }

  // A Bidirectional streaming RPC.
  public CountDownLatch streamLines(List<String> phrases) throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<ChunkedPhrase> requestObserver =
        asyncStub.streamLines(new StreamObserver<Line>() {
          @Override
          public void onNext(Line line) {
            System.out.println(client + " received response at 'streamLines': " + line.getLine());
          }

          @Override
          public void onError(Throwable t) {
            System.out.println(client + " Response error at 'streamLines': " + t);
            finishLatch.countDown();
          }

          @Override
          public void onCompleted() {
            System.out.println(client + " Response completed at 'streamLines'");
            finishLatch.countDown();
          }
        });

    System.out.println("\n" + client + " starting to send request for 'streamLines'");
    try {
      for (String phrase : phrases) {
        requestObserver.onNext(ChunkedPhrase.newBuilder().setPhrase(phrase).setAddStop(true).build());
        // Sleep for a bit before sending the next one.
        Thread.sleep(random.nextInt(100) + 50);
        if (finishLatch.getCount() == 0) {
          // RPC completed or errored before we finished sending.
          // Sending further requests won't error, but they will just be thrown away.
          return finishLatch;
        }
      }
    } catch (RuntimeException e) {
      // Cancel RPC
      requestObserver.onError(e);
      throw e;
    }
    // Mark the end of requests
    requestObserver.onCompleted();

    // return the latch while receiving happens asynchronously
    return finishLatch;
  }

  public void close() throws InterruptedException {
    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
  }
}
