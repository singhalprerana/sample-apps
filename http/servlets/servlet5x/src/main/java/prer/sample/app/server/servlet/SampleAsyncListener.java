package prer.sample.app.server.servlet;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import prer.sample.app.client.grizzly.SampleGrizzlyClient;
import prer.sample.app.client.okhttp.SampleOkHttpClient;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author prerana.singhal on 28/09/2020
 */
public class SampleAsyncListener implements AsyncListener {

  private final SampleOkHttpClient okHttpClient;
  private final SampleGrizzlyClient grizzlyClient;

  public SampleAsyncListener() {
    this(null, null);
  }

  public SampleAsyncListener(SampleOkHttpClient okHttpClient, SampleGrizzlyClient grizzlyClient) {
    this.okHttpClient = okHttpClient;
    this.grizzlyClient = grizzlyClient;
  }

  @Override
  public void onComplete(AsyncEvent asyncEvent) throws IOException {

    System.out.println("AppAsyncListener onComplete");
  }

  @Override
  public void onError(AsyncEvent asyncEvent) throws IOException {
    System.out.println("AppAsyncListener onError");
    ((HttpServletResponse) asyncEvent.getAsyncContext().getResponse()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Override
  public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
    System.out.println("AppAsyncListener onStartAsync");
  }

  @Override
  public void onTimeout(AsyncEvent asyncEvent) throws IOException {
    System.out.println("AppAsyncListener onTimeout");
    //we can send appropriate response to client
    ServletResponse response = asyncEvent.getAsyncContext().getResponse();
    PrintWriter out = response.getWriter();
    out.write("TimeOut Error in Processing");
  }

}