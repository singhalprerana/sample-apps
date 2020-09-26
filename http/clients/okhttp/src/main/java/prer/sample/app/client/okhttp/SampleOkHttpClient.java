package prer.sample.app.client.okhttp;


import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author prerana.singhal on 23/09/2020
 */

public class SampleOkHttpClient {

  private final String url;
  private final OkHttpClient client = new OkHttpClient();
  private final Callback callback;

  public SampleOkHttpClient(int serverPort) {
    this.url = "http://localhost:" + serverPort;
    callback = new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        System.out.println("Client Response error: " + e);
      }
      @Override
      public void onResponse(Call call, final Response response) {
        if (!response.isSuccessful()) {
          System.out.println("Client Response error: " + response);
        } else {
          System.out.println("Client Response completed: " + response.toString());
        }
        response.close();
      }
    };
  }

  public static void main(String[] args) throws Exception {
    int serverPort = Integer.parseInt(args[0]);

    SampleOkHttpClient okHttpClient = new SampleOkHttpClient(serverPort);
    if (args.length >= 5) {
      okHttpClient.runClientApp(args[1], args[2], args[3], args[4]);
    } else {
      okHttpClient.runClientApp("/get", "/form", "/json", "/text");
    }
  }

  public void makeCall(String path, CallType callType, boolean addQueryParams, boolean addIpHeaders, String paramPrefix) throws IOException {
    Request.Builder builder = new Request.Builder();
    addIpHeaders(builder);

    String key = paramPrefix + "key";
    String value = paramPrefix + "val";

    String urlString = url + path;
    if (addQueryParams) {
      urlString += "?" + key + "-q0=" + value + "-q0&" + key + "-q1=" + value + "-q1";
    }
    builder = builder.url(urlString);
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
    client.newCall(request).enqueue(callback);
  }

  private Request getGetRequest(Request.Builder builder) {
    return builder.get().build();
  }

  private Request getPostTextRequest(Request.Builder builder) {
    String body = "I'm not great at advice.\nBut can I interest you in a sarcastic comment?";
    return builder.post(RequestBody.create(MediaType.parse("text/plain"), body)).build();
  }

  private Request getPostJsonRequest(Request.Builder builder) {
    Map<String, String[]> map = new HashMap<>();
    map.put("Friends", new String[]{"I make jokes when I'm uncomfortable.", "I rarely practice my meals before I eat."});
    map.put("Harry Potter", new String[]{"Mischief Managed"});
    String body = new Gson().toJson(map);
    return builder.post(RequestBody.create(MediaType.parse("application/json"), body)).build();
  }

  private Request getPostFormRequest(Request.Builder builder) {
    RequestBody formBody = new FormBody.Builder()
        .add("Accio", "Summon stuff")
        .add("Expelliarmus", "Disarm your opponent")
        .build();
    return builder.post(formBody).build();
  }

  private void addIpHeaders(Request.Builder builder) {
    builder.addHeader("x-real-ip", "127.0.0.1")
        .addHeader("x-forwarded-for", "127.0.0.2,9.2.3")
        .addHeader("x-proxyuser-ip", "127.0.0.3")
        .addHeader("forwarded", "for=127.0.0.31;for=127.0.0.32;for=127.0.0.41;for=127.0.2.42;f=127.0.0.43;127.0.0.44;");
  }

  public void runClientApp(String getPath, String formPath, String jsonPath, String textPath) throws InterruptedException, IOException {
    while (true) {
      Thread.sleep(100);
      makeCall(getPath, CallType.GET, false, true, "");
      Thread.sleep(100);
      makeCall(formPath, CallType.POST_FORM, true, true, "");
      Thread.sleep(100);
      makeCall(jsonPath, CallType.POST_JSON, true, true, "");
      Thread.sleep(100);
      makeCall(textPath, CallType.POST_TEXT, true, true, "");
      Thread.sleep(100);
    }
  }

  public enum CallType {
    POST_JSON,
    POST_TEXT,
    POST_FORM,
    GET
  }
}
