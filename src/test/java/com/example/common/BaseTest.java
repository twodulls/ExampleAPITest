package com.example.common;

import com.example.testdata.TestData;
import com.example.testdata.TestDataManager;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    public static String BASEURL;
    public static String ENV;
    public static RequestSpecification REQUESTSPEC;
    protected static TestData testData;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.debug("BaseTest의 beforeSuite");
        String env = System.getProperty("env");

        /**
         * dev 혹은 it 환경으로 테스트 수행하기 위해서는 env 파일을 환경에 맞게 세팅해야 한다.
         */
        if (env == null) env = "dev";
        logger.debug("env: : {}", env);

        ENV = env;
        this.testData = TestDataManager.getInstance(env).getData();

        /*
         * RestAssured Insecure 설정
         */
        RestAssured.config = RestAssuredConfig.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                .encoderConfig(EncoderConfig.encoderConfig()
                        .defaultContentCharset("UTF-8")
                        .appendDefaultContentCharsetToContentTypeIfUndefined(true))
                .decoderConfig(RestAssured.config().getDecoderConfig()
                        .defaultContentCharset("UTF-8"));

        RestAssured.urlEncodingEnabled = false;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext ctx) {
        logger.debug("BaseTest의 beforeClass");
        BASEURL = testData.getBaseUrl();
        logger.debug("BASEURL : {}", BASEURL);

        //suite xml 에서 suite name 을 읽어옴
        String suiteName = ctx.getCurrentXmlTest().getSuite().getName();
        logger.debug("suite name : {}", suiteName);

        RequestSpecBuilder builder = new RequestSpecBuilder();
        RestAssured.baseURI = BASEURL;
        builder.setBaseUri(RestAssured.baseURI);
        /**
         * ReqeustSpecBuilder에 Bearer token 과 기본 header 값이 추가 될 경우 아래와 같은 코드를 사용한다.
         * builder.addHeader("Authorization", "Bearer " + token);
         *         builder.addHeader("x-org-id", ORGID);
         *         builder.addHeader("x-zone-id", ZONEID);
         *         builder.addHeader("x-account-id", ACCOUNTID);
         *         builder.addHeader("x-user-id", USERID);
         *         builder.addHeader("x-user-name", USERNAME);
         */

        REQUESTSPEC = builder.build();

        /**
         * Allure Report setting
         */
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Suite Name", suiteName)
                        .put("BASEURL", BASEURL).build(),
                System.getProperty("user.dir") + "/build/allure-results/");
    }

}
