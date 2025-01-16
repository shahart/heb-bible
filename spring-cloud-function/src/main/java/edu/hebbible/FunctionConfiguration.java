package edu.hebbible;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
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
    SpringApplication.run(FunctionConfiguration.class, args);
  }

  @Bean
  public Function</*Map<String, Object>*/String, String> psukim() throws Exception {
    return queryParams -> {
      log.info("/get " + queryParams);
        List<Pasuk> result;
        try {
            result = svc.psukim(
                    (((Map<String, String>)(new ObjectMapper().readValue(queryParams, Map.class))).getOrDefault("name", "")),
                    false, // Boolean.parseBoolean(queryParams.getOrDefault("containsName", Boolean.FALSE).toString()),
                    false);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!result.isEmpty()) log.info("1st: " + result.getFirst());
      log.info("Total Psukim: " + result.size());
      return "Total psukim: " + result.size();
    };
  }

}
