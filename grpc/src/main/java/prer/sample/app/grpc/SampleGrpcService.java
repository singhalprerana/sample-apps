package prer.sample.app.grpc;

import ai.traceable.sample.app.grpc.sample.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prerana.singhal on 21/09/2020
 */
public class SampleGrpcService extends SampleServiceGrpc.SampleServiceImplBase {

  private final SampleGrpcClient grpcClient;
  private final String service;

  public SampleGrpcService(SampleGrpcClient grpcClient, int port) {
    this.grpcClient = grpcClient;
    this.service = "Service-at-" + port;
  }

  @Override
  // A simple RPC.
  public void sayHello(Name request, StreamObserver<Greeting> responseObserver) {
    responseObserver.onNext(Greeting.newBuilder().setName(request).setMessage("Have a nice day.!").build());
    System.out.println(service + " replied at 'sayHello' for " + request.getFirstName() + " " + request.getLastName());
    if (grpcClient != null) {
      grpcClient.sayHello(request.getFirstName(), request.getLastName());
    }
    responseObserver.onCompleted();
  }

  @Override
  // A server-to-client streaming RPC.
  public void getWords(Line request, StreamObserver<ChunkedWord> responseObserver) {
    String[] words = request.getLine().split("\\s+");
    for (int i = 0; i < words.length - 1; i++) {
      ChunkedWord.Builder wordBuilder = ChunkedWord.newBuilder();
      if (words[i].endsWith(",")) {
        wordBuilder.setWord(words[i].substring(0, words[i].length() - 1));
        wordBuilder.setAddComma(true);
      } else if (words[i].endsWith(".")) {
        wordBuilder.setWord(words[i].substring(0, words[i].length() - 1));
      } else {
        wordBuilder.setWord(words[i]);
      }
      responseObserver.onNext(wordBuilder.build());
    }
    responseObserver.onNext(ChunkedWord.newBuilder().setWord(words[words.length - 1]).build());
    System.out.println(service + " replied at 'getWords' for: " + request.getLine());
    if (grpcClient != null) {
      grpcClient.getWords(request.getLine());
    }
    responseObserver.onCompleted();
  }

  // A client-to-server streaming RPC.
  public StreamObserver<ChunkedWord> getLine(StreamObserver<Line> responseObserver) {
    return new StreamObserver<ChunkedWord>() {
      final List<String> words = new ArrayList<>();
      final StringBuilder sb = new StringBuilder();

      @Override
      public void onNext(ChunkedWord value) {
        if (sb.length() != 0) {
          sb.append(" ");
        }
        sb.append(value.getWord());
        if (value.getAddComma()) {
          sb.append(",");
          words.add(value.getWord() + ",");
        } else {
          words.add(value.getWord());
        }
      }

      @Override
      public void onError(Throwable t) {
        System.out.println(service + " Error at 'getLine': " + t);
      }

      @Override
      public void onCompleted() {
        sb.append(".");
        Line line = Line.newBuilder().setLine(sb.toString()).build();
        responseObserver.onNext(line);
        if (grpcClient != null) {
          try {
            grpcClient.getLine(words);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        System.out.println(service + " replied at 'getLine' with: " + line.getLine());
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  // A Bidirectional streaming RPC.
  public StreamObserver<ChunkedPhrase> streamLines(StreamObserver<Line> responseObserver) {
    return new StreamObserver<ChunkedPhrase>() {
      final List<String> phrases = new ArrayList<>();
      StringBuilder sb = new StringBuilder();
      boolean addStop;

      @Override
      public void onNext(ChunkedPhrase value) {
        phrases.add(value.getPhrase());
        addStop = value.getAddStop();
        String[] phrases = value.getPhrase().split("\\.");
        int i;
        for (i = 0; i < phrases.length - 1; i++) {
          append(phrases[i].split("\\s+"));
          if (addStop) {
            sb.append(".");
          }
          Line line = Line.newBuilder().setLine(sb.toString()).build();
          responseObserver.onNext(line);
          System.out.println(service + " streamed response at 'streamLines' with: " + line.getLine());
          sb = new StringBuilder();
        }
        append(phrases[i].split("\\s+"));
      }

      @Override
      public void onError(Throwable t) {
        System.out.println(service + " Error at 'streamLines': " + t);
      }

      @Override
      public void onCompleted() {
        if (sb.length() != 0) {
          if (addStop) {
            sb.append(".");
          }
          Line line = Line.newBuilder().setLine(sb.toString()).build();
          responseObserver.onNext(line);
          System.out.println(service + " streamed response at 'streamLines' with: " + line.getLine());
        }
        System.out.println(service + " finished streaming response at 'streamLines'");
        if (grpcClient != null) {
          try {
            grpcClient.streamLines(phrases);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        responseObserver.onCompleted();
      }

      private void append(String[] words) {
        for (String word : words) {
          if (sb.length() != 0) {
            sb.append(" ");
          }
          sb.append(word);
        }
      }
    };
  }
}
