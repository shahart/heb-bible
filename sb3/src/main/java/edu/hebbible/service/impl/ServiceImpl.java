package edu.hebbible.service.impl;

import edu.gematria.Calc;
import edu.hebbible.model.Psukim;
import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import edu.hebbible.service.Svc;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

// import java.net.URLDecoder;
import java.util.*;

@Service
public class ServiceImpl implements Svc {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    @Autowired
    protected Repo repo;

    DynamoDbClient dynamodb;

    @Value("${spring.profiles.active:prod}")
    String profile;

//    @Autowired
//    DynamoDbTemplate dynamoDbTemplate;

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

//        if (dynamoDbTemplate != null) {
//            PageIterable<Psukim> pageIterable = dynamoDbTemplate.scanAll(Psukim.class);
//            if (pageIterable != null) {
//                System.out.println("---psukim---");
//                for (Psukim psukim : pageIterable.items()) {
//                    // System.out.println(psukim);
//                }
//            }
//        }
    }

    @Override
    public List<Pasuk> psukim(String name, boolean containsName, boolean withDups) {
        putItemInTable(name);

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
        log.debug("Gim: {}", Calc.calc(name));
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

    public void putItemInTable(String name) {
        if (dynamodb != null) {
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("name", AttributeValue.builder().s(name).build());
            itemValues.put("extra", AttributeValue.builder().s("containsName-" + false).build());
            itemValues.put("feature", AttributeValue.builder().s("Pasuk").build());
            itemValues.put("date", AttributeValue.builder().s("2024-11-19").build());

//        Psukim psukim = new Psukim();
//        psukim.setName(name);

            try {
//            dynamoDbTemplate.save(psukim);
                PutItemRequest request = PutItemRequest.builder().tableName("psukim").item(itemValues).build();
                PutItemResponse response = // findBugs: Medium: Dead store
                        dynamodb.putItem(request);
                log.info(response.responseMetadata().requestId());
            } catch (Exception e) {
                log.warn("putItemInTable. " + e);
            }
        }
    }

    public static String engTx(String arg) {
        return arg; // URLDecoder.decode(arg);
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
