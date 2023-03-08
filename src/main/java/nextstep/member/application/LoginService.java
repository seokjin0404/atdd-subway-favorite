package nextstep.member.application;

import nextstep.Exception.NoSuchMemberException;
import nextstep.member.MemberNotFoundException;
import nextstep.member.application.dto.*;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private MemberRepository memberRepository;
    private JwtTokenProvider jwtTokenProvider;
    private GithubClient githubClient;

    public LoginService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, GithubClient githubClient) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.githubClient = githubClient;
    }

    public TokenResponse createToken(TokenRequest tokenRequest) {
        Member member = memberRepository.findByEmail(
                        tokenRequest.getEmail())
                .orElseThrow(() ->
                        new MemberNotFoundException("존재하지 않는 아이디입니다.")
                );
        validatePassword(tokenRequest, member);

        return new TokenResponse(
                jwtTokenProvider.createToken(
                        member.getEmail(), member.getRoles()
                )
        );
    }

    private void validatePassword(TokenRequest tokenRequest, Member member) {
        if (!member.getPassword().equals(tokenRequest.getPassword())) {
            throw new MemberNotFoundException("잘못된 비밀번호 입니다.");
        }
    }

    public GithubLoginResponse githubLogin(GithubLoginRequest githubLoginRequest) {
        String accessTokenFromGithub = githubClient.getAccessTokenFromGithub(githubLoginRequest.getCode());
        GithubProfileResponse githubProfileFromGithub = githubClient.getGithubProfileFromGithub(accessTokenFromGithub);

        Member member = memberRepository.findByEmail(githubProfileFromGithub.getEmail()).orElseThrow(NoSuchMemberException::new);
        return new GithubLoginResponse(
                jwtTokenProvider.createToken(
                        member.getEmail(),
                        member.getRoles()
                )
        );
    }
}
