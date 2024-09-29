package edu.hebbible;

import java.util.List;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;

public class Handler {

    Svc svc = new ServiceImpl();

    public String handleRequest(Input input) {
        String name = hebTx(input.name);
        List<Pasuk> result = svc.psukim(name, false /* input.containsName */);
        return "Total Psukim: " + result.size(); // + name + svc.repoSize() /* 23_203 */ + svc.firstPasuk();
    }

    public static class Input {
        public String name;
    }

    // todo fix encoding
    public static String hebTx(String input) {
        final String eng = "qwertyuiop[]asdfghjkl;'zxcvbnm,./'";
        final String heb = "/'קראטוןםפ][שדגכעיחלךף,זסבהנמצתץ.'";
        String output = "";
        for (int i = 0; i < input.length(); ++ i) {
            int idx = heb.indexOf(input.charAt(i));
            output += (idx >= 0 ? eng.charAt(idx) : input.charAt(i));
        }
        return output;
    }

}