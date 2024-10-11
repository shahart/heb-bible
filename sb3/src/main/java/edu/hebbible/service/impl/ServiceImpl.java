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

    private static Logger log = LoggerFactory.getLogger(Service.class);

    @Autowired
    Repo repo;

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
    public List<Pasuk> psukim(String name, boolean containsName) {
        putItemInTable(dynamodb, name);

        List<Pasuk> result = new ArrayList<>();
        Collection<Pasuk> psukim = repo.getStore();
        int findings = 0;
        for (Pasuk pasuk: psukim) {
            String line = pasuk.text();
            if ((line.charAt(0) == name.charAt(0) && line.charAt(line.length()-1) == name.charAt(name.length()-1)) || (containsName && line.contains(name))) {
                if (result.isEmpty() || ! result.getLast().text().equals(pasuk.text())) {
                    log.debug(
                            pasuk.book() + " " + pasuk.perek() + "-" + pasuk.pasuk() + " -- " +
                                    line);
                    ++findings;
                    result.add(new Pasuk(pasuk.book(), pasuk.perek(), pasuk.pasuk(), noName(pasuk.text())));
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
        return repo.getStore().size();
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

    // @SuppressWarnings(value = "SBSC_USE_STRINGBUFFER_CONCATENATION")
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
            output += (idx >= 0 ? heb.charAt(idx) : arg.charAt(i)); // NOSONAR concatenates strings using + in a loop
        }
        log.info(" >> /post " + output);
        return output;
    }

}
