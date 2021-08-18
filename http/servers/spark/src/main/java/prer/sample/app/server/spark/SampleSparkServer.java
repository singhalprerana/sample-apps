package prer.sample.app.server.spark;

import static spark.Spark.*;

/**
 * @author prerana.singhal on 05/10/2020
 *
 */
public class SampleSparkServer {
  public static void main(String[] args) {
    System.out.println("Starting server at http://localhost:4567/hello");
    get("/hello", (req, res) -> "Hello World");
  }
}
