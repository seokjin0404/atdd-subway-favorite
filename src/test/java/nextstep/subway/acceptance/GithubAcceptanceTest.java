package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.DataLoader;
import nextstep.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.MemberSteps.토큰으로_내_정보_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class GithubAcceptanceTest {

    private static final String PASSWORD = "password";
    private static final Integer AGE = 20;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    public void setUpUser() {
        dataLoader.addMember(new Member(GithubResponses.사용자1.getEmail(),PASSWORD,AGE));
        dataLoader.loadData();
    }

    /**
     * Given 회원 가입한다.
     * When Github 로그인을 요청한다.
     * Then AccessToken을 응답받는다.
     */
    @DisplayName("깃헙 로그인 요청시 AccessToken을 응답받는다.")
    @Test
    void githubAuth() {
        //Given
        ExtractableResponse<Response> response = 깃헙_로그인_요청(GithubResponses.사용자1.getCode());

        //Then
        assertThat(response.jsonPath().getString("accessToken")).isNotBlank();
    }

    /**
     * Given 회원가입 되어 있지 않은 유저가 로그인을 요청한다.
     * When 회원 가입을 진행한다.
     * Then AccessToken을 응답받는다.
     */
    @DisplayName("회원가입 되어 있지 않은 유저가 로그인을 요청 시, 회원 가입을 진행하고 AccessToken을 응답받는다.")
    @Test
    void githubAuth2() {
        //Given
        String accessToken = 깃헙_로그인_요청(GithubResponses.사용자2.getCode()).jsonPath().getString("accessToken");

        //When
        ExtractableResponse<Response> response = 토큰으로_내_정보_조회_요청(accessToken);

        //Then
        assertThat(response.jsonPath().getString("email")).isEqualTo(GithubResponses.사용자2.getEmail());
    }

    private static ExtractableResponse<Response> 깃헙_로그인_요청(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/login/github")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();
        return response;
    }
}
