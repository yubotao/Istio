package com.yubotao.istiotwo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: yubt
 * @Description:
 * @Date: Created in 17:28 2018/5/9
 * @Modified By:
 */
@RestController
public class IstioTwoController {
    private static final String RESPONSE_STRING_FORMAT = "two from '%s': %d";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String HOSTNAME =parseContainerIdFromHostname(
            System.getenv().getOrDefault("HOSTNAME", "unknown")
    );

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for enabling timeout
     */
    private boolean timeout = false;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    @RequestMapping("/")
    public ResponseEntity<String> getTwo(@RequestHeader("User-Agent") String userAgent){
        logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
        if (misbehave){
            count = 0;
            logger.info(String.format("Misbehaving %d", count));
            return ResponseEntity.ok(String.format("recommendation misbehavior from '%s'\n", HOSTNAME));
        }else {
            count++;
            if (timeout){
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, HOSTNAME, count));
        }
    }

}
