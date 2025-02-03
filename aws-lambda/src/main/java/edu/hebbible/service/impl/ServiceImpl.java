package edu.hebbible.service.impl;

import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import edu.hebbible.service.Svc;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.text.SimpleDateFormat;
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

    DynamoDbClient dynamodb;

    // from .aws/cred file
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            "access-key-id",
            "secret-access-key");

    @Override
    public List<Pasuk> psukim(String name, boolean containsName) {
        if (dynamodb == null) {
            dynamodb = DynamoDbClient.builder().region(Region.EU_NORTH_1)
                 // .credentialsProvider(StaticCredentialsProvider.create(awsCreds)) //
                .build();
        }
        putItemInTable(dynamodb, name, "containsName-" + containsName, "Pasuk");

        List<Pasuk> result = new ArrayList<>();
        Collection<Pasuk> psukim = repo.getStore();
        if (psukim.size() <= 1) {
            throw new IllegalStateException("Unable to fetch bible");
        }
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

    public static void putItemInTable(DynamoDbClient dynamodb, String name, String extraInfo, String tableName) {
        if (dynamodb != null) {
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("feature", AttributeValue.builder().s(getTableName(tableName)).build());
            itemValues.put("name", AttributeValue.builder().s(name).build());
            itemValues.put("extra", AttributeValue.builder().s(extraInfo).build());
            itemValues.put("date", AttributeValue.builder().s(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).build());
            // todo? userAgent user-agent
            try {
                PutItemRequest request = PutItemRequest.builder().tableName("psukim").item(itemValues).build();
                PutItemResponse response = dynamodb.putItem(request);
                // log.info( response.responseMetadata().requestId());
            } catch (Exception e) {
                System.err.println("putItemInTable failed. " + e);
            }
        }
    }

    private static String getTableName(String tableName) {
        switch (tableName) {
            case "Read", "Pasuk", "Dilug", "Find", "Gim": return tableName;
            default: throw new IllegalArgumentException("wrong feature: " + tableName);
        }
    }

    @Override
    public void logMoreFeatures(String name, String extra, String tableName) {
        if (dynamodb == null) {
            dynamodb = DynamoDbClient.builder().region(Region.EU_NORTH_1)
                     // .credentialsProvider(StaticCredentialsProvider.create(awsCreds)) //
                    .build();
        }
        putItemInTable(dynamodb, name, extra, tableName);
    }
}
