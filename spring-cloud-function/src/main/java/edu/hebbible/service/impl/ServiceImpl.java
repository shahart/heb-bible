package edu.hebbible.service.impl;

import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import edu.hebbible.service.Svc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceImpl implements Svc {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    @Autowired
    protected Repo repo;

    @Override
    public List<Pasuk> psukim(String name, boolean containsName, boolean withDups) {
        if (withDups) {
            log.warn("withDups: on");
        }

        initRepo();
        List<Pasuk> result = new ArrayList<>();
        List<Pasuk> psukim = repo.getStore();
        int findings = 0;
        for (Pasuk pasuk: psukim) {
            String line = pasuk.text();
            if ((line.charAt(0) == name.charAt(0) && line.charAt(line.length()-1) == name.charAt(name.length()-1)) || (containsName && line.contains(name))) {
                if (result.isEmpty() || (withDups || ! result.getLast().text().equals(pasuk.text()))) {
                    log.debug(
                            pasuk.book() + " " + pasuk.perek() + "-" + pasuk.pasuk() + " -- " +
                                    line);
                    ++findings;
                    result.add(new Pasuk(pasuk.book(), pasuk.bookNo(), pasuk.perek(), pasuk.pasuk(), noName(pasuk.text()), pasuk.cntLetter()));
                }
            }
        }
        log.info(findings + " verses total");
        return result;
    }

    private static String noName(String orig) {
        return orig.replaceAll("יהוה", "ה'");
    }

    public static String engTx(String arg) {
        // todo solve the encoding stuff, from %D7%A9%D7%97%D7%A8= to שחר
        if (! arg.endsWith("=")) {
            return arg;
        }
        String eng = "qwertyuiopasdfghjkl;'zxcvbnm,./'";
        String heb =  "/'קראטוןםפשדגכעיחלךף,זסבהנמצתץ.'";
        var output = "";
        for (int i = 0; i < arg.length()-1; ++ i) {
            var idx = eng.indexOf(arg.charAt(i));
            output += (idx >= 0 ? heb.charAt(idx) : arg.charAt(i));
        }
        return output;
    }

    private void initRepo() {
        repo.init();
    }

}