package edu.hebbible.repository;

import java.io.DataInputStream;
//import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import edu.hebbible.model.Pasuk;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

//import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@Repository
public class Repo {

    private static Logger log = LoggerFactory.getLogger(Repo.class);

    private final List<Pasuk> store = new ArrayList<>();

    final static String []bookHeb = new String[] {"תישארב","תומש","ארקיו","רבדמב","םירבד","עשוהי","םיטפוש","א לאומש","ב לאומש", "א םיכלמ","ב םיכלמ","היעשי","הימרי","לאקזחי","עשוה","לאוי","סומע","הידבוע","הנוי","הכימ","םוחנ","קוקבח","הינפצ", "יגח","הירכז","יכאלמ","םילהת","ילשמ","בויא","םירישה ריש","תור","הכיא","תלהק","רתסא","לאינד","ארזע","הימחנ", "א םימיה ירבד","ב םימיה ירבד"};

    @PostConstruct
    public void postConstruct() {
        logGitProps();
        init();
    }

    // reads the result of the git prop in the build.gradle
    private void logGitProps() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties");
            if (is != null) {
                props.load(is);
            }
            log.info("git.commit.id.abbrev: " + props.get("git.commit.id.abbrev"));
        } catch (IOException e) {
            log.warn("cannot get the git props " + e.getMessage());
        }
    }

    public Collection<Pasuk> getStore() {
        return Collections.unmodifiableList(store);
    }

    @SuppressWarnings(value = "REC_CATCH_EXCEPTION") // , justification = "eof exception, ignored")
    void init() {
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
                    Pasuk pasuk = new Pasuk(new StringBuilder(bookHeb[currBookIdx]).reverse().toString(), PPrk, PPsk, line.toString().trim());
                    store.add(pasuk);
                    line = new StringBuilder();
                    ++EndFile;
                }
                PPrk = findStr2[0] - 31;
                PPsk = findStr2[1] - 31;
                line.append(" ").append(decryprt(findStr2));
            }
        } catch (Exception ignored) {
            // ignored.printStackTrace(); // EndOfFile
        }
        log.info(System.currentTimeMillis() - ts + " msec");
        log.info(EndFile + " psukim");
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