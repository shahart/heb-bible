package edu.hebbible;

import java.io.BufferedWriter;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.DataInputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Function implements HttpFunction {

  private static final Logger logger = Logger.getLogger(Function.class.getName());

  private List<Pasuk> store = null;

  @Override
  public void service(final HttpRequest request, final HttpResponse response) throws Exception {

    init();

    Map<String, List<String>> queryParams = request.getQueryParameters();
    String name = queryParams.get("name").getFirst();
    boolean containsName = false;
    if (queryParams.containsKey("containsName")) {
      containsName = Boolean.parseBoolean(queryParams.get("containsName").getFirst());
    }

    List<Pasuk> result = psukim(name, containsName);
    if (! result.isEmpty()) logger.info("1st: " + result.iterator().next());

    final BufferedWriter writer = response.getWriter();
    writer.write("Total Psukim: " + result.size());

    // todoFirebase(name);
  }

  private void todoFirebase(String name) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"name\":\"" + name + "\"}"))
            .uri(URI.create("https://nd4w9kk8f3.execute-api.eu-north-1.amazonaws.com/v1/pasuk"))
            .header("Content-Type", "application/json")
            .build();
    java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
    System.out.println("aws lambda's body: " + response.body());
  }

  // todo upload the function, for now POC via inline editor https://console.cloud.google.com/functions/details/us-central1/myPsukimViaLambda?env=gen2&hl=en&project=pivotal-racer-435706-c6&tab=source

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
        // ignored.printStackTrace();
      }

      String prefix = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + ":INFO ";
      logger.info(prefix + (System.currentTimeMillis() - ts) + " msec");
      logger.info(prefix + EndFile + " psukim"); // having the same as 2024-09-15 10:57:33.671:INFO:
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
