package edu.hebbible;

import java.util.List;
import java.util.function.Function;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FunctionConfiguration {

  private static final Logger log = LoggerFactory.getLogger(FunctionConfiguration.class);

  @Autowired
  private Svc svc;

  /*
   * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
   * in the POM to ensure boot plug-in makes the correct entry
   */
  public static void main(String[] args) {
    // empty unless using Custom runtime at which point it should include
    // SpringApplication.run(FunctionConfiguration.class, args);
  }

  @Bean
  public Function<String, String> psukim() {
    return value -> {
      log.info("/get " + value);
      List<Pasuk> result = svc.psukim(ServiceImpl.engTx(value), false, false);
      if (!result.isEmpty()) log.info("1st: " + result.getFirst());
      log.info("Total Psukim: " + result.size());
      return "Total psukim: " + result.size();
    };
  }

}
