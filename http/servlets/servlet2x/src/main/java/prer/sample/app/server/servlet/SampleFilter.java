package prer.sample.app.server.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @author prerana.singhal on 27/09/2020
 */
public class SampleFilter implements Filter {

  public void init(FilterConfig arg0) {
  }

  public void doFilter(ServletRequest req, ServletResponse resp,
                       FilterChain chain) throws IOException, ServletException {
    System.out.println("Filter called for " + ((HttpServletRequest) req).getRequestURL().toString());
    if (req instanceof HttpServletRequestWrapper && resp instanceof HttpServletResponseWrapper) {
      chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) ((HttpServletRequestWrapper) req).getRequest()),
          new HttpServletResponseWrapper((HttpServletResponse) ((HttpServletResponseWrapper) resp).getResponse()));
    } else {
      chain.doFilter(req, resp);
    }
  }

  public void destroy() {
  }
}


