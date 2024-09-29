package edu.hebbible.service.impl;

import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import edu.hebbible.service.Svc;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

//import java.text.SimpleDateFormat;
import java.util.*;

//@Service
public class ServiceImpl implements Svc {

//    Logger log = LoggerFactory.getLogger(Service.class);

    final static String []bookeng = new String[] {"Genesis","Exodus","Leviticus","Numbers","Deuteronomy","Joshua","Judges","Samuel 1",
            "Samuel 2","Kings 1","Kings 2","Isaiah","Jeremiah","Ezekiel","Hosea","Joel","Amos","Obadiah","Jonah","Micha","Nachum",
            "Habakkuk","Zephaniah","Haggai","Zechariah","Malachi","Psalms","Proverbs","Job","Song of songs","Ruth","Lamentations",
            "Ecclesiastes","Esther","Daniel","Ezra","Nehemiah","Cronicles 1","Cronicles 2"};

//    @Autowired
    Repo repo = new Repo();

    @Override
    public List<Pasuk> psukim(String name, boolean containsName) {

        List<Pasuk> result = new ArrayList<>();
        Collection<Pasuk> psukim = repo.getStore();
        int findings = 0;
        for (Pasuk pasuk: psukim) {
            String line = pasuk.text();
            if ((line.charAt(0) == name.charAt(0) && line.charAt(line.length()-1) == name.charAt(name.length()-1)) || (containsName && line.contains(name))) {
//                log.debug(
//                        bookeng[pasuk.book()] + " " + pasuk.perek() + "-" + pasuk.pasuk() + " -- " +
//                                line);
                ++ findings;
                result.add(pasuk);
            }
        }
        // log.info(findings + " verses total");
        return result;
    }

    @Override
    public int repoSize() {
        return repo.getStore().size();
    }

    @Override
    public String firstPasuk() {
        return repo.getStore().iterator().next().text();
    }

}
