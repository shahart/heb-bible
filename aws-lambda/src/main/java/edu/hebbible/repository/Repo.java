package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import org.apache.commons.io.IOUtils;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

//@Repository
public class Repo {

//    Logger log = LoggerFactory.getLogger(Repo.class);

    private final List<Pasuk> store = new ArrayList<>();

//    @PostConstruct
//    public void postConstruct() {
//        init();
//    }

    public Repo() {
        init();
    }

    public List<Pasuk> getStore() {
        return Collections.unmodifiableList(store);
    }

    void init() {
        if (store.isEmpty()) {
            int EndFile = 0; // amount of psukim
            int currBookIdx = 0;
            int PPsk = 999;
            int PPrk = 1;
            StringBuilder line = new StringBuilder();
//          long ts = System.currentTimeMillis();
            try (InputStream is2 = new URL("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz").openStream()) {
                InputStream is = IOUtils.toBufferedInputStream(is2);
                BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.UTF_8));
                String content;
                while ((content = br.readLine()) != null) {
                    String []splits = content.split(",");
                    Pasuk pasuk = new Pasuk(Integer.parseInt(splits[0].split(":")[0]) - 1, Integer.parseInt(splits[0].split(":")[1]), Integer.parseInt(splits[0].split(":")[2]),
                            splits[1].trim());
                    store.add(pasuk);
                }
                EndFile = store.size();
                System.out.println("Used gzip");
            }
            catch (Exception e) {
                System.err.println("Unable to gUnzip >> " + e);
                // oldRead();
                try (DataInputStream inputStream = new DataInputStream(new URL("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.5bit").openStream())) {
                    int[] findStr2 = new int[47];

                    while (true) {
                        for (int i = 0; i < 47; ++i) {
                            findStr2[i] = inputStream.readUnsignedByte();
                        }
                        if ((findStr2[1] - 31 != PPsk)
                                && (!line.isEmpty())) {
                            Pasuk pasuk = new Pasuk(currBookIdx, PPrk, PPsk, line.toString().trim());
                            store.add(pasuk);
                            if (findStr2[0] - 31 == 1 && findStr2[1] - 31 == 1 && findStr2[1] - 31 != PPsk) {
                                ++currBookIdx;
                            }
                            line = new StringBuilder();
                            ++EndFile;
                        }
                        PPrk = findStr2[0] - 31;
                        PPsk = findStr2[1] - 31;
                        line.append(" ").append(decryprt(findStr2));
                    }
                } catch (Exception eof) {
                    Pasuk pasuk = new Pasuk(currBookIdx, PPrk, PPsk, line.toString().trim());
                    store.add(pasuk);
                    ++EndFile;
                }
            }
            System.out.println(EndFile + " psukim");
//          log.info(System.currentTimeMillis() - ts + " msec");
//          log.info(EndFile + " psukim");
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


}
