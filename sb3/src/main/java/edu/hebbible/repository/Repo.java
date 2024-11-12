package edu.hebbible.repository;

import java.io.*;
//import java.io.EOFException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

import edu.hebbible.model.Pasuk;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

//import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@Repository
public class Repo {

    private static final Logger log = LoggerFactory.getLogger(Repo.class);

    private final List<Pasuk> verses = new ArrayList<>();
    private final StringBuilder torTxt = new StringBuilder();

    private int totalVerses = 0;
    private int totalLetters = 1;

    final static String []bookHeb = new String[] {"תישארב","תומש","ארקיו","רבדמב","םירבד","עשוהי","םיטפוש","א לאומש","ב לאומש", "א םיכלמ","ב םיכלמ","היעשי","הימרי","לאקזחי","עשוה","לאוי","סומע","הידבוע","הנוי","הכימ","םוחנ","קוקבח","הינפצ", "יגח","הירכז","יכאלמ","םילהת","ילשמ","בויא","םירישה ריש","תור","הכיא","תלהק","רתסא","לאינד","ארזע","הימחנ", "א םימיה ירבד","ב םימיה ירבד"};

    @PostConstruct
    public void postConstruct() {
        logGitProps();
        init();
    }

    public int getTotalVerses() {
        return totalVerses;
    }

    public int getTotalLetters() {
        return totalLetters;
    }

    public StringBuilder getTorTxt() { // NOSONAR may expose internal representation by returning Repo.torTxt
        return torTxt;
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

    public List<Pasuk> getStore() {
        return Collections.unmodifiableList(verses);
    }

    @SuppressWarnings(value = "REC_CATCH_EXCEPTION") // , justification = "eof exception, ignored")
    public void init() {
        if (verses.isEmpty()) {
            long ts = System.currentTimeMillis();
            try (InputStream is2 = new URL("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz").openStream()) {
                InputStream is = IOUtils.toBufferedInputStream(is2);
                BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.UTF_8));
                String content;
                while ((content = br.readLine()) != null) {
                    String []splits = content.split(",");
                    addVerse(splits[1],
                            Integer.parseInt(splits[0].split(":")[0]) - 1, Integer.parseInt(splits[0].split(":")[1]), Integer.parseInt(splits[0].split(":")[2]));
                }
                log.info("Used gzip");
            }
            catch (Exception e) {
                log.warn("Unable to gUnzip >> " + e);
                oldRead();
            }
            log.info(torTxt.length() + " total Letters (no spaces)"); // 80% out of with spaces 1,479,010
            log.info(System.currentTimeMillis() - ts + " mSec");
            log.info(totalVerses + " psukim");
        }
    }

    private void oldRead() {
        int currBookIdx = 0;
        int PPsk = 999;
        int PPrk = 1;
        StringBuilder line = new StringBuilder();
        try (DataInputStream inputStream = new DataInputStream(new URL("https://raw.githubusercontent.com/shahart/heb-bible/master/BIBLE.TXT").openStream())) {
            int[] findStr2 = new int[47];
            while (true) {
                for (int i = 0; i < 47; ++i) {
                    findStr2[i] = inputStream.readUnsignedByte();
                }
                if ((findStr2[1] - 31 != PPsk)
                        && (!line.isEmpty())) {
                    addVerse(line.toString(), currBookIdx, PPrk, PPsk);
                    line = new StringBuilder();
                    if (findStr2[0] - 31 == 1 && findStr2[1] - 31 == 1 && findStr2[1] - 31 != PPsk) {
                        ++currBookIdx;
                        // System.out.print("\r" + currBookIdx);
                    }
                }
                PPrk = findStr2[0] - 31;
                PPsk = findStr2[1] - 31;
                line.append(" ").append(decryprt(findStr2));
            }
        } catch (UnknownHostException uhe) {
            log.error("No internet, no bible to fetch >> " + uhe);
            // throw new RuntimeException(uhe);
        } catch (Exception e) {
            addVerse(line.toString(), currBookIdx, PPrk, PPsk);
        }
    }

    private void addVerse(String line, int currBookIdx, int PPrk, int PPsk) {
        String txt = line.trim().replaceAll(" ", "");
        torTxt.append(suffix(txt));
        totalLetters += txt.length();
        Pasuk pasuk = new Pasuk(new StringBuilder(bookHeb[currBookIdx]).reverse().toString(), currBookIdx + 1, PPrk, PPsk, line.trim(), totalLetters);
        verses.add(pasuk);
        ++totalVerses;
    }

    public static void getHebChar(StringBuilder s, int i) { // DOS Hebrew/ code page 862 - Aleph is 128. Now it"s unicode
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

    public static String suffix(String txt) {
        txt = txt.replaceAll( "ך", "כ");
        txt = txt.replaceAll( "ם", "מ");
        txt = txt.replaceAll( "ן", "נ");
        txt = txt.replaceAll( "ף", "פ");
        txt = txt.replaceAll( "ץ", "צ");
        return txt;
    }

    public static void main(String args[]) {
        new Repo().init();
    }

}
