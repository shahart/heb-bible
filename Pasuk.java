// package edu.hebbible;

import java.io.*;
import java.net.URL;

// TODO refactor, for now simple migration from pasuk.pas (kind of, as it was lost).
// Even the var names are the same style of 2002.

public class Pasuk {

    final static String []bookeng = new String[] {"Genesis","Exodus","Leviticus","Numbers","Deuteronomy","Joshua","Judges","Samuel 1",
            "Samuel 2","Kings 1","Kings 2","Isaiah","Jeremiah","Ezekiel","Hosea","Joel","Amos","Obadiah","Jonah","Micha","Nachum",
            "Habakkuk","Zephaniah","Haggai","Zechariah","Malachi","Psalms","Proverbs","Job","Song of songs","Ruth","Lamentations",
            "Ecclesiastes","Esther","Daniel","Ezra","Nehemiah","Cronicles 1","Cronicles 2"};

    public static void main(String[] args) throws Exception {
        // todo apache-commons-cli: and -containsName
        if (args.length == 0 || args[0].length() <= 1) {
            System.err.println("Invalid name");
            System.exit(1);
        }
        Pasuk pasuk = new Pasuk();
        pasuk.findPsukim(args[0]);
    }

    public void findPsukim(String args) throws Exception {
        boolean containsName = false;
        int findings = 0;
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
                        && (line.length()>=1)) {
                    if ((line.charAt(1) == args.charAt(0) && line.charAt(line.length()-1) == args.charAt(args.length()-1)) || (containsName && line.indexOf(args) >= 0)) {
                        System.out.println(
                                bookeng[currBookIdx] + " " + PPrk + "-" + PPsk + " -- " +
                                        line);
                        ++ findings;
                    }
                    if (findStr2[0] - 31 == 1 && findStr2[1] - 31 == 1 && findStr2[1] - 31 != PPsk) {
                        ++ currBookIdx;
                    }
                    line = new StringBuilder();
                    ++EndFile;
                }
                PPrk = findStr2[0] - 31;
                PPsk = findStr2[1] - 31;
                line.append(" ").append(decryprt(findStr2));
            }
        } catch (EOFException ignored) {
        }
        System.out.println(EndFile + " verses total. \nTime taken (mSec): " + (System.currentTimeMillis() - ts) + ". \nTotal Psukim: " + findings);
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