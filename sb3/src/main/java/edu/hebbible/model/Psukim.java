package edu.hebbible.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Psukim extends Major {

    private boolean containsName;

    public boolean isContainsName() {
        return containsName;
    }

    public void setContainsName(boolean containsName) {
        this.containsName = containsName;
    }
}
