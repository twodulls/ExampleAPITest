package com.example.domain.example;

import com.example.apiCommon.ExampleCommon;
import com.example.common.BaseTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GetAllTutorialsTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetAllTutorialsTest.class);
    private ExampleCommon exampleCommon;
    private SoftAssertions sa;

    int id;

    @BeforeClass(alwaysRun=true)
    public void setUp(){
        exampleCommon = new ExampleCommon();

        step("튜토리얼 생성");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", "test tutorial");
        jsonObject.addProperty("description", "test description");

        Response response = exampleCommon.createTutorial(gson.toJson(jsonObject));
        assertThat(response.statusCode()).isEqualTo(201);
        JsonPath jsonPath = response.jsonPath();
        id = jsonPath.getInt("id");
        logger.debug("id : {}", id);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpTest(){ sa = new SoftAssertions(); }

    @Test(groups = "positive")
    @Story("Example API - 튜토리얼 전체 조회")
    public void 튜토리얼_전체_조회_확인(){
        step("튜토리얼 전체조회");
        Response response = exampleCommon.getAllTutorials();
        assertThat(response.statusCode()).isEqualTo(200);
        JsonPath jsonPath = response.jsonPath();

        sa.assertThat(jsonPath.getInt("[0].id")).isGreaterThan(0);
        sa.assertThat(jsonPath.getString("[0].title")).isEqualTo("test tutorial");
        sa.assertThat(jsonPath.getString("[0].description")).isEqualTo("test description");
        sa.assertThat(jsonPath.getBoolean("[0].published")).isEqualTo(false);
        sa.assertAll();
    }

    @AfterClass(alwaysRun = true)
    public void teardown(){
        step("테스트 종료 후 삭제");
        Response response = exampleCommon.deleteTutorial(id);
        assertThat(response.statusCode()).isEqualTo(204);
    }
}
