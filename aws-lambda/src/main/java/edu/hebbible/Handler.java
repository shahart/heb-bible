package edu.hebbible;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import edu.hebbible.model.Pasuk;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.google.gson.Gson;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;

public class Handler implements RequestHandler<Map<String, Object>, String> {

    Svc svc = new ServiceImpl();

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger log = context.getLogger();
        // logGitProps(log);
        // log.log("Input: " + event); // like {version=2.0, routeKey=$default, rawPath=/, rawQueryString=, headers={sec-fetch-mode=cors, referer=https://shahart.github.io/, content-length=41, x-amzn-tls-version=TLSv1.3, sec-fetch-site=cross-site, x-forwarded-proto=https, accept-language=en-GB,en;q=0.5, origin=https://shahart.github.io, x-forwarded-port=443, x-forwarded-for=85.65.151.214, priority=u=0, accept=*/*, x-amzn-tls-cipher-suite=TLS_AES_128_GCM_SHA256, x-amzn-trace-id=Root=1-66e5f76e-135536990aa8f26c111e892d, host=z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws, content-type=application/json, accept-encoding=gzip, deflate, br, zstd, user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0, sec-fetch-dest=empty}, requestContext={accountId=anonymous, apiId=z4r74tvfwdi3wywr4aegh4f3di0zhhuo, domainName=z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws, domainPrefix=z4r74tvfwdi3wywr4aegh4f3di0zhhuo, http={method=POST, path=/, protocol=HTTP/1.1, sourceIp=85.65.151.214, userAgent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0}, requestId=2d9d8528-7915-42ea-92ac-b1d2aded4abd, routeKey=$default, stage=$default, time=14/Sep/2024:20:51:58 +0000, timeEpoch=1726347118274}, body={"name":"אברהם","containsName":true}, isBase64Encoded=false}
        // log.log(svc.repoSize() + " verses total");
        Gson gson = new Gson();
        Map<String, Object> request = gson.fromJson(gson.toJson(event), Map.class);
        try {
            Map<String, String> bodyParams = new HashMap<>();
            if (request.containsKey("body")) {
                request = gson.fromJson((String) request.get("body"), Map.class);
            }
            bodyParams.put("name", request.get("name").toString());
            bodyParams.put("extra", request.getOrDefault("extra", "").toString());
            String feature = request.getOrDefault("type", "").toString();
            if (feature.equals("Pasuk")) {
                String extra = bodyParams.getOrDefault("extra", "containsName-false");
                List<Pasuk> result = svc.psukim( // findPsukim(log,
                        bodyParams.get("name"), Boolean.parseBoolean(extra.substring("containsName-".length())));
                if (!result.isEmpty()) log.log("1st: " + result.iterator().next(), LogLevel.INFO);
                // System.err.println(result.iterator().next());
                log.log("Total Psukim: " + result.size()); // + " 1st: " + result.subList(0, 1));
                return "Total Psukim: " + result.size();
            }
            else {
                svc.logMoreFeatures(bodyParams.get("name"), request.getOrDefault("extra", "").toString(), feature);
                return feature;
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    // reads the result of the git prop in the build.gradle
    private void logGitProps(LambdaLogger log) {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties");
            if (is != null) {
                props.load(is);
            }
            log.log("\"git.commit.id.abbrev: " + props.get("git.commit.id.abbrev"));
        } catch (IOException e) {
            log.log("ERROR get the git props " + e.getMessage());
        }
    }

}