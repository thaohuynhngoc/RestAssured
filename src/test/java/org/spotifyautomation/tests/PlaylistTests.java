package org.spotifyautomation.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests {
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    String access_token = "BQD9XWrdJj-hJrmTHrMJOAaCL4O3TJjefFJUOsQ8bLHTyK6AswyisuUaDsSI-9FpoPBPS0H872yOvKKQvApET5wRoeUfnV7b4gEfAuuytsELQbKvAX9Xb5UnjagvPcekWcKmbuBP1UBOXVuRwi6PGakU76T72HpVVtJS-K9YITdlrr_QCcYXGrp_Y1r-gpQI6xuiD-asPmmA1C1wn9BNRr8zUo3M3rF8ihgCKR3dZc0s6l5fvYWDMhJcAHqVarX8Dppd1ZvmQL9lPQbuIH8cPOz2";

    @BeforeClass
    public void beforeClass() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri("https://api.spotify.com")
                .setBasePath("/v1")
                .addHeader("Authorization", "Bearer " + access_token)
                .log(LogDetail.ALL);

        requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    @Test
    void shouldBeAbleToCreatePlaylist() {
        String payload = "{\n" +
                "    \"name\": \"New Rest Playlist\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        String userId = "316ugqqwclaof3kscof6ess2o2ve";

        given(requestSpecification)
                .body(payload)
                .pathParam("userId", userId).
        when()
                .post("/users/{userId}/playlists").
        then()
                .spec(responseSpecification)
                .assertThat()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", equalTo("New Rest Playlist"),
                        "description", equalTo("New playlist description"),
                        "public", equalTo(false));
    }

    @Test
    void shouldBeAbleToGetPlaylist() {
        String playlistId = "7yifSZlk96PGP0miWS6RNs";

        given(requestSpecification)
                .pathParam("playlistId", playlistId).
        when()
                .get("/playlists/{playlistId}").
        then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", equalTo("Updated Playlist Name"),
                        "public", equalTo(true));
    }

    @Test
    void shouldBeAbleToUpdatePlaylist() {
        String payload = "{\n" +
                "    \"name\": \"Update Playlist Name\",\n" +
                "    \"description\": \"Update playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        String playlistId = "7yifSZlk96PGP0miWS6RNs";

        given(requestSpecification)
                .pathParam("playlistId", playlistId)
                .body(payload).
        when()
                .put("/playlists/{playlistId}").
        then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void shouldNotBeAbleToCreatePlaylistWithEmptyName() {
        String payload = "{\n" +
                "    \"name\": \"\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        String userId = "316ugqqwclaof3kscof6ess2o2ve";

        given(requestSpecification)
                .body(payload)
                .pathParam("userId", userId).
        when()
                .post("/users/{userId}/playlists").
        then()
                .spec(responseSpecification)
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error.status", equalTo(400),
                        "error.message", equalTo("Missing required field: name"));
    }

    @Test
    void shouldNotBeAbleToCreatePlaylistWithInvalidToken() {
        String payload = "{\n" +
                "    \"name\": \"New Rest Playlist\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        String userId = "316ugqqwclaof3kscof6ess2o2ve";

        given()
                .baseUri("https://api.spotify.com")
                .basePath("/v1")
                .pathParam("userId", userId)
                .header("Authorization", "Bearer " + "Invalid 1234")
                .contentType(ContentType.JSON)
                .log().all().
        when()
                .post("/users/{userId}/playlists").
        then()
                .spec(responseSpecification)
                .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error.status", equalTo(401),
                        "error.message", equalTo("Invalid access token"));

    }
}
