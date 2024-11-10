package edu.hebbible.service.impl;

import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import edu.hebbible.service.Svc;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class ServiceImpl implements Svc {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    @Autowired
    protected Repo repo;

    DynamoDbClient dynamodb;

    @Value("${spring.profiles.active:prod}")
    String profile;

    @PostConstruct
    public void initDb() {
        if (! "test".equalsIgnoreCase(profile)) {
            // from .aws/cred file
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                    "access-key-id",
                    "secret-access-key");

            dynamodb = DynamoDbClient.builder().region(Region.EU_NORTH_1)
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        }
    }

    @Override
    public List<Pasuk> psukim(String name, boolean containsName, boolean withDups) {
        putItemInTable(dynamodb, name);

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


    @Override
    public int repoSize() {
        initRepo();
        return repo.getTotalVerses();
    }

    public static void putItemInTable(DynamoDbClient dynamodb, String name) {
        if (dynamodb != null) {
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("name", AttributeValue.builder().s(name).build());
            try {
                PutItemRequest request = PutItemRequest.builder().tableName("psukim").item(itemValues).build();
                // PutItemResponse response = // findBugs: Medium: Dead store
                dynamodb.putItem(request);
                // log.info( response.responseMetadata().requestId());
            } catch (Exception e) {
                log.warn("putItemInTable. " + e);
            }
        }
    }

    public static String engTx(String arg) {
        // todo solve the encoding stuff
        if (! (arg.endsWith("=") && arg.startsWith("%"))) {
            return arg;
        }
        String eng = "%D7%90%D7%91%D7%92%D7%93%D7%94%D7%95%D7%96%D7%97%D7%98%D7%99%D7%9A%D7%9B%D7%9C%D7%9D%D7%9E%D7%9F%D7%A0%D7%A1%D7%A2%D7%A3%D7%A4%D7%A5%D7%A6%D7%A7%D7%A8%D7%A9%D7%AA=";
        String heb =  "אבגדהוזחטיךכלםמןנסעףפץצקרשת";
        var output = "";
        for (int i = 0; i < arg.length()/6; ++ i) { // 6 = "%D7%90".length
            var idx = eng.indexOf(arg.substring(i*6, (i+1)*6));
            output += (idx >= 0 ? heb.charAt(idx/6) : arg.charAt(i));
        }
        log.info(" >> /post " + output);
        return output;
    }

    int indVrsRange(int cntLtr, int indLowVrs, int indHigVrs) {
        if (indLowVrs == indHigVrs) {
            return indLowVrs;
        }
        else {
            int indMidVrs = (int)(Math.ceil((indLowVrs + indHigVrs) / 2.0));
            if (cntLtr < repo.getStore().get(indMidVrs).cntLetter()) {
                return indVrsRange(cntLtr, indLowVrs, indMidVrs-1);
            }
            else {
                return indVrsRange(cntLtr, indMidVrs, indHigVrs);
            }
        }
    }

    @Override
    public int dilugim(String target, int skipMin, int skipMax) {
        initRepo();
        boolean match;
        int iSkip, targetLen, lastInd, found = 0;
        target = target.replaceAll(" ", "");
        target = Repo.suffix(target);
        targetLen = target.length();
        for (iSkip = skipMin; iSkip <= skipMax; ++ iSkip) {
            lastInd = repo.getTotalLetters() - (targetLen - 1) * iSkip;
            for (int j = 0; j < lastInd; j ++) { // loop on bible
                match = true;
                for (int k = 0; k < targetLen; k ++) { // loop on target
                    if (repo.getTorTxt().charAt(j + k * iSkip) != target.charAt(k)) {
                        match = false;
                        break;
                    }
                }
                if (!match) {
                    match = true;
                    for (int k = 0; k < targetLen; k ++) { // loop on target
                        if (repo.getTorTxt().charAt(j + k * iSkip) != target.charAt(targetLen-k-1)) {
                            match = false;
                            break;
                        }
                    }
                }
                if (match) {
                    var iVrs = indVrsRange(j, 0, repo.getTotalVerses()-1);
                    String txt = repo.getTorTxt().substring(j, j+ (target.length()) * (iSkip));
                    while (repo.getStore().get(iVrs).cntLetter() < j) {
                        ++ iVrs;
                    }
                    log.info("new dilug of {} from {} {}", iSkip, j, repo.getStore().get(iVrs));
                    for (int i = 0; i < target.length(); ++i) {
                        log.info(txt.substring(i*iSkip, (i+1)*iSkip));
                    }
                    if (++ found >= 50) {
                        log.warn("Too many results");
                        return found;
                    }
                }
            }
        }
        log.info("{} findings", found);
        return found;
    }

    private void initRepo() {
        repo.init();
    }

}
