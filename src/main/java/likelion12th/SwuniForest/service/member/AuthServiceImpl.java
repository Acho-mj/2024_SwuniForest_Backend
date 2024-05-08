package likelion12th.SwuniForest.service.member;

import likelion12th.SwuniForest.jwt.TokenProvider;
import likelion12th.SwuniForest.service.member.domain.Member;
import likelion12th.SwuniForest.service.member.domain.dto.CustomUserInfoDto;
import likelion12th.SwuniForest.service.member.domain.dto.LoginDto;
import likelion12th.SwuniForest.service.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final TokenProvider tokenProvider; // = jwtUtil
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public String login(LoginDto loginDto) {
        String studentNum = loginDto.getStudentNum();
        String password = loginDto.getPassword();
        Optional<Member> optionalMember = memberRepository.findByStudentNum(studentNum);

        // optionalMember가 존재하지 않는 경우 예외 발생
        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다."));

        if (member.getUsername() == null) {
            if (!password.equals(member.getPassword())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            return createAccessToken(member);
        } else {
            // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
            if (!encoder.matches(password, member.getPassword())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            return createAccessToken(member);
        }

    }

    private String createAccessToken(Member member) {
        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.builder()
                .studentNum(member.getStudentNum())
                .major(member.getMajor())
                .role(member.getRole())
                .build();

        return tokenProvider.createAccessToken(customUserInfoDto);
    }

}
