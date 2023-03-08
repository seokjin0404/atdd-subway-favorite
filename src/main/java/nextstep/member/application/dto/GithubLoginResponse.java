package nextstep.member.application.dto;

public class GithubLoginResponse {
    private String accessToken;

    public GithubLoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccesToken() {
        return accessToken;
    }
}
