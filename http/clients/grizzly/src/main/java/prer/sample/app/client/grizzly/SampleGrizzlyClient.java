package prer.sample.app.client.grizzly;

import com.google.gson.Gson;
import com.ning.http.client.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author prerana.singhal on 23/09/2020
 */

public class SampleGrizzlyClient {

  private final String url;
  private final AsyncHttpClient client = new AsyncHttpClient();

  public SampleGrizzlyClient(int serverPort) {
    this.url = "http://localhost:" + serverPort;
  }

  public static void main(String[] args) throws Exception {
    int serverPort = Integer.parseInt(args[0]);

    SampleGrizzlyClient grizzlyClient = new SampleGrizzlyClient(serverPort);
    if (args.length >= 5) {
      grizzlyClient.runClientApp(args[1], args[2], args[3], args[4]);
    } else {
      grizzlyClient.runClientApp("/get", "/form", "/json", "/text");
    }
  }

  public CountDownLatch makeCall(String path, CallType callType, boolean addQueryParams, boolean addIpHeaders, String paramPrefix) throws IOException {
    RequestBuilder builder = new RequestBuilder();

    addIpHeaders(builder);

    String key = paramPrefix + "key";
    String value = paramPrefix + "val";

    String urlString = url + path;
    if (addQueryParams) {
      urlString += "?" + key + "-q0=" + value + "-q0&" + key + "-q1=" + value + "-q1";
    }
    builder = builder.setUrl(urlString);
    builder.addHeader(key + "-h0", value + "-h0").addHeader(key + "-h1", value + "-h1");

    Request request;
    switch (callType) {
      case POST_JSON:
        request = getPostJsonRequest(builder);
        break;
      case POST_TEXT:
        request = getPostTextRequest(builder);
        break;
      case POST_FORM:
        request = getPostFormRequest(builder);
        break;
      default:
        request = getGetRequest(builder);
    }

    CountDownLatch latch = new CountDownLatch(1);
    client.executeRequest(request, new AsyncCompletionHandler<Boolean>() {
      private StringBuilder builder = new StringBuilder();

      @Override
      public Boolean onCompleted(Response response) throws Exception {
        // Will be invoked once the response has been fully read or a ResponseComplete exception
        // has been thrown.
        System.out.println("Client Response completed: Response{code="
            + response.getStatusCode()
            + ", message="
            + response.getStatusText()
            + ", url="
            + response.getUri()
            + '}');
        latch.countDown();
        return true;
      }

      @Override
      public void onThrowable(Throwable t) {
        System.out.println("Client Response error: " + t);
        latch.countDown();
      }
    });

    return latch;
  }

  private Request getGetRequest(RequestBuilder builder) {
    return builder.setMethod("GET").build();
  }

  private Request getPostTextRequest(RequestBuilder builder) {
    String body = "I'm not great at advice.\nBut can I interest you in a sarcastic comment?";
    return builder.setMethod("POST")
        .setHeader("Content-Type", "text/plain")
        .setBody(body).build();
  }

  private Request getPostJsonRequest(RequestBuilder builder) {
    Map<String, String[]> map = new HashMap<>();
    map.put("Friends", new String[]{"I make jokes when I'm uncomfortable.", "I rarely practice my meals before I eat."});
    map.put("Harry Potter", new String[]{"Mischief Managed"});
    String body = new Gson().toJson(map);

    return builder.setMethod("POST")
        .setHeader("Content-Type", "application/json")
        .setBody(body).build();
  }

  private Request getPostFormRequest(RequestBuilder builder) {
    Map<String, List<String>> formParams = new HashMap<>();
    formParams.put("Accio", Collections.singletonList("Summon stuff"));
    formParams.put("Expelliarmus", Collections.singletonList("Disarm your opponent"));

    return builder.setMethod("POST")
        .setHeader("Content-Type", "application/x-www-form-urlencoded")
        .setFormParams(formParams).build();
  }

  private void addIpHeaders(RequestBuilder builder) {
    builder.addHeader("x-real-ip", "127.0.0.1");
    builder.addHeader("x-forwarded-for", "127.0.0.2,9.2.3");
    builder.addHeader("x-proxyuser-ip", "127.0.0.3");
    builder.addHeader("forwarded", "for=127.0.0.31;for=127.0.0.32;for=127.0.0.41;for=127.0.2.42;f=127.0.0.43;127.0.0.44;");
  }

  public void runClientApp(String getPath, String formPath, String jsonPath, String textPath) throws InterruptedException, IOException {
    while (true) {
      Thread.sleep(1000);
      makeCall(getPath, CallType.GET, false, true, "");
      Thread.sleep(1000);
      makeCall(formPath, CallType.POST_FORM, true, true, "");
      Thread.sleep(1000);
      makeCall(jsonPath, CallType.POST_JSON, true, true, "");
      Thread.sleep(1000);
      makeCall(textPath, CallType.POST_TEXT, true, true, "");
      Thread.sleep(1000);
    }
  }

  public enum CallType {
    POST_JSON,
    POST_TEXT,
    POST_FORM,
    GET
  }
}
