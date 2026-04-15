package com.example.apiCommon;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class ExampleCommon {

    //튜토리얼 생성
    public Response createTutorial(String reqBody){
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .when()
                .body(reqBody)
                .post("/api/tutorials")
                .then().extract().response();
        return response;
    }

    //튜토리얼 전체 조회
    public Response getAllTutorials(){
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/tutorials")
                .then().extract().response();
        return response;
    }

    //튜토리얼 상세 조회
    public Response getTutorialById(int id){
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .pathParam("id", id)
                .when()
                .get("/api/tutorials/{id}")
                .then().extract().response();
        return response;
    }

    //튜토리얼 수정
    public Response updateTutorial(String reqBody, int id){
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .pathParam("id", id)
                .when()
                .body(reqBody)
                .get("/api/tutorials/{id}")
                .then().extract().response();
        return response;
    }

    //튜토리얼 삭제
    public Response deleteTutorial(int id){
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .pathParam("id", id)
                .when()
                .delete("/api/tutorials/{id}")
                .then().extract().response();
        return response;
    }
}
