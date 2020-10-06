package prer.sample.app.server.servlet;

import com.google.gson.Gson;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author prerana.singhal on 28/09/2020
 */
public class AsyncRequestProcessor implements Runnable {

  private AsyncContext asyncContext;

  public AsyncRequestProcessor(AsyncContext asyncCtx) {
    this.asyncContext = asyncCtx;
  }

  @Override
  public void run() {
    HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
    HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

    try {
      String str = "";
      if (System.currentTimeMillis() % 2 == 0) {
        str = "Partial stream: " + (char) request.getInputStream().read() + "";
      } else {
        int ch;
        while ((ch = request.getInputStream().read()) != -1) {
          str += (char) ch;
        }
      }
      Map<String, String> map = new HashMap<>();
      map.put("Request", str);
      map.put("Response", "Harry Potter meets Chanandler Bong.!");

      response.setContentType("application/json");
      response.getWriter().print(new Gson().toJson(map));
    } catch (IOException e) {
      e.printStackTrace();
    }
    asyncContext.complete();
  }

  private void longProcessing(int secs) {
    // wait for given time before finishing
    try {
      Thread.sleep(secs);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
