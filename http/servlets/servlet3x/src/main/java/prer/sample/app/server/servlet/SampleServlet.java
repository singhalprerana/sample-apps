package prer.sample.app.server.servlet;

import prer.sample.app.client.grizzly.SampleGrizzlyClient;
import prer.sample.app.client.okhttp.SampleOkHttpClient;

import javax.servlet.AsyncContext;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author prerana.singhal on 27/09/2020
 */
public class SampleServlet extends HttpServlet {

  public enum ClientType {
    OkHttp,
    Grizzly;
  }

  private final SampleOkHttpClient okHttpClient;
  private final SampleGrizzlyClient grizzlyClient;

  public SampleServlet() {
    this.okHttpClient = null;
    this.grizzlyClient = null;
  }

  public SampleServlet(int port, Set<ClientType> clientTypes) {
    if (clientTypes == null) {
      this.okHttpClient = null;
      this.grizzlyClient = null;
    } else {
      if (clientTypes.contains(ClientType.OkHttp)) {
        this.okHttpClient = new SampleOkHttpClient(port);
      } else {
        this.okHttpClient = null;
      }
      if (clientTypes.contains(ClientType.Grizzly)) {
        this.grizzlyClient = new SampleGrizzlyClient(port);
      } else {
        this.grizzlyClient = null;
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("***** Get Request received at " + request.getRequestURL());
    request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
    AsyncContext asyncCtx = request.startAsync();
    asyncCtx.addListener(new SampleAsyncListener(okHttpClient, grizzlyClient));
    asyncCtx.addListener(new SampleAsyncListener());
    makeClientCall((HttpServletRequest) asyncCtx.getRequest(), SampleOkHttpClient.CallType.GET, SampleGrizzlyClient.CallType.GET);
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Hola</title>");
    out.println("</head>");
    out.println("<body bgcolor=\"yellow\">");
    out.println("Whether you come back by page or by the big screen, Hogwarts will always be there to welcome you home.");
    out.println("</body>");
    out.println("</html>");
    asyncCtx.complete();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    System.out.println("***** Post Request received at " + request.getRequestURL());
    request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
    switch (request.getContentType().split(";")[0]) {
      case "text/plain":
        makeClientCall(request, SampleOkHttpClient.CallType.POST_TEXT, SampleGrizzlyClient.CallType.POST_TEXT);
        doPostText(request, response);
        break;
      case "application/json":
        makeClientCall(request, SampleOkHttpClient.CallType.POST_JSON, SampleGrizzlyClient.CallType.POST_JSON);
        doPostJson(request, response);
        break;
      case "application/x-www-form-urlencoded":
        makeClientCall(request, SampleOkHttpClient.CallType.POST_FORM, SampleGrizzlyClient.CallType.POST_FORM);
        doPostForm(request, response);
        break;
      default:
        doGet(request, response);
    }
  }

  private void doPostText(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String str;
    if (System.currentTimeMillis() % 2 == 0) {
      str = "Partial reader: " + request.getReader().readLine();
    } else {
      str = "Full reader: \n" + request.getReader().lines().collect(Collectors.joining("\n"));
    }

    response.setContentType("text/plain");
    response.getWriter().println(str);
  }

  private void doPostJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
    AsyncContext asyncCtx;
    if (request instanceof HttpServletRequestWrapper && response instanceof HttpServletResponseWrapper) {
      asyncCtx = request.startAsync(new HttpServletRequestWrapper((HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest()),
          new HttpServletResponseWrapper((HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse()));
    } else {
      asyncCtx = request.startAsync();
    }
    asyncCtx.addListener(new SampleAsyncListener());

    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(1, 1, 1000, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(2));
    executor.execute(new AsyncRequestProcessor(asyncCtx));
  }

  private void doPostForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (System.currentTimeMillis() % 2 == 0) {
      StringBuilder sb = new StringBuilder();
      request.getParameterMap().entrySet().stream()
          .map(entry -> String.join(":", (String) ((Map.Entry) entry).getKey(), String.join(";", (String[]) ((Map.Entry) entry).getValue())))
          .forEach(s -> sb.append(s + " "));
      response.setContentType("text/plain");
      response.getWriter().println("Parameters read as: " + sb.toString());
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  private void makeClientCall(HttpServletRequest request,
                              SampleOkHttpClient.CallType okhttpCallType,
                              SampleGrizzlyClient.CallType grizzlyCallType) throws IOException {
    if (okHttpClient != null) {
      okHttpClient.makeCall(request.getRequestURI(), okhttpCallType, true, true, "");
    }
    if (grizzlyClient != null) {
      grizzlyClient.makeCall(request.getRequestURI(), grizzlyCallType, true, true, "");
    }
  }
}
