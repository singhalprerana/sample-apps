package prer.sample.app.server.spark;

import static spark.Spark.*;

/**
 * @author prerana.singhal on 05/10/2020
 */
public class SampleSparkServer {
  public static void main(String[] args) {
    get("/hello", (req, res) -> "Hello World");
  }
}
