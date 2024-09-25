package edu.hebbible;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.DataInputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Function {


  private List<Pasuk> store = null;

    @FunctionName("myPsukimViaLambda")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Pass a name on the query string or in the request body").build();
        } else {
			    init();
          context.getLogger().info(store.size() + " psukim");

          boolean containsName = false;
          if (request.getQueryParameters().containsKey("containsName")) {
            containsName = Boolean.parseBoolean(request.getQueryParameters().get("containsName"));
          }

          List<Pasuk> result = psukim(name, containsName);
          if (! result.isEmpty()) context.getLogger().info("1st: " + result.iterator().next());

          return request.createResponseBuilder(HttpStatus.OK).body("Total Psukim: " + result.size()).build();
        }
    }

  // Repo

  private void init() {
    if (store == null) {
      store = new ArrayList<>();
      int EndFile = 0; // amount of psukim
      int currBookIdx = 0;
      long ts = System.currentTimeMillis();
      try (DataInputStream inputStream = new DataInputStream(new URL("https://raw.githubusercontent.com/shahart/heb-bible/master/BIBLE.TXT").openStream())) {
        int[] findStr2 = new int[47];
        int PPsk = 999;
        int PPrk = 1;
        StringBuilder line = new StringBuilder();
        while (true) {
          for (int i = 0; i < 47; ++i) {
            findStr2[i] = inputStream.readUnsignedByte();
          }
          if ((findStr2[1] - 31 != PPsk)
                  && (!line.isEmpty())) {
            if (findStr2[0] - 31 == 1 && findStr2[1] - 31 == 1 && findStr2[1] - 31 != PPsk) {
              ++ currBookIdx;
            }
            Pasuk pasuk = new Pasuk(currBookIdx, PPrk, PPsk, line.toString().trim());
            store.add(pasuk);
            line = new StringBuilder();
            ++EndFile;
          }
          PPrk = findStr2[0] - 31;
          PPsk = findStr2[1] - 31;
          line.append(" ").append(decryprt(findStr2));
        }
      } catch (Exception ignored) {
      }
      System.out.println((System.currentTimeMillis() - ts) + " msec");
    }
  }

  private void getHebChar(StringBuilder s, int i) { // DOS Hebrew/ code page 862 - Aleph is 128. Now it"s unicode
    char res = i == 31 ? ' ' : (char)('א' + i);
    s.insert(0, res);
  }

  private String decryprt(int[] findstr2) {
    int m = 2; // 0-Prk, 1-Psk
    StringBuilder s = new StringBuilder();
    // Java has no unsigned, so for InputStream look at https://mkyong.com/java/java-convert-bytes-to-unsigned-bytes/
    int i = 1;
    while (i <= 72) {
      getHebChar(s, ((findstr2[m]) >> 3));                                           // {11111000}
      getHebChar(s, (((findstr2[m]) & 7) << 2) | ((findstr2[m + 1]) >> 6));       // {00000111+11000000}
      getHebChar(s, ((findstr2[m + 1]) & 62) >> 1);                               // {00111110}
      getHebChar(s, (((findstr2[m + 1]) & 1) << 4) | ((findstr2[m + 2]) >> 4));   // {00000001+11110000}
      getHebChar(s, (((findstr2[m + 2]) & 15) << 1) | ((findstr2[m + 3]) >> 7));  // {00001111+10000000}
      getHebChar(s, ((findstr2[m + 3]) & 124) >> 2);                              // {01111100}
      getHebChar(s, (((findstr2[m + 3]) & 3) << 3) | ((findstr2[m + 4]) >> 5));   // {00000011+11100000}
      getHebChar(s, (findstr2[m + 4]) & 31);                                      // {00011111}
      m += 5;
      i += 8;
    }
    return s.toString().trim();
  }

  // SvcImpl

  public List<Pasuk> psukim(String name, boolean containsName) {
    List<Pasuk> result = new ArrayList<>();
    int findings = 0;
    for (Pasuk pasuk: store) {
      String line = pasuk.text();
      if ((line.charAt(0) == name.charAt(0) && line.charAt(line.length()-1) == name.charAt(name.length()-1)) || (containsName && line.contains(name))) {
        ++findings;
        result.add(pasuk);
      }
    }
    // log.info(findings + " verses total");
    return result;
  }

  public record Pasuk(int book, int perek, int pasuk, String text) { // todo String book
  }

}
