package com.yubotao.istioone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @Auther: yubt
 * @Description:
 * @Date: Created in 16:55 2018/5/9
 * @Modified By:
 */
@RestController
public class IstioOneController {
    private static final String RESPONSE_STRING_FORMAT = "one => %s\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    @Value("${two.api.url:http://one:8080}")
    private String remoteURL;

    public IstioOneController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    public ResponseEntity<String> getOne(@RequestHeader("User-Agent") String userAgent){
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.USER_AGENT,userAgent);
            ResponseEntity<String> responseEntity = restTemplate.exchange(remoteURL,HttpMethod.GET,new HttpEntity<>(httpHeaders),String.class);
            String response = responseEntity.getBody();
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT,response.trim()));
        }catch (HttpStatusCodeException ex){
            logger.warn("Exception trying to get the response from preference service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT,
                            String.format("%d %s", ex.getRawStatusCode(), createHttpErrorResponseString(ex))));
        }catch (RestClientException ex){
            logger.warn("Exception trying to get the response from preference service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getMessage()));
        }
    }

    private String createHttpErrorResponseString(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString().trim();
        if (responseBody.startsWith("null")) {
            return ex.getStatusCode().getReasonPhrase();
        }
        return responseBody;
    }


}
