package likelion12th.SwuniForest.presentation.controller.stamp;

import likelion12th.SwuniForest.service.member.MemberService;
import likelion12th.SwuniForest.service.member.domain.Member;
import likelion12th.SwuniForest.service.member.domain.dto.MemberResDto;
import likelion12th.SwuniForest.service.member.repository.MemberRepository;
import likelion12th.SwuniForest.service.stamp.CertificationService;
import likelion12th.SwuniForest.service.stamp.domain.Certification;
import likelion12th.SwuniForest.service.stamp.domain.dto.CertificationDto;
import likelion12th.SwuniForest.service.stamp.domain.dto.StampDto;
import likelion12th.SwuniForest.service.stamp.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CertificationController {
    private final CertificationRepository certificationRepository;

    private final CertificationService certificationService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 모든 코드 리스트 반환
    @GetMapping("/all")
    public ResponseEntity<List<CertificationDto>> getCodeList(){
        return ResponseEntity.ok(certificationService.getCodeList());
    }


    // 관리자별 코드 반환 - 사용  X
    @GetMapping("/{depNumber}")
    public ResponseEntity<CertificationDto> getDepCode(@PathVariable Long depNumber) {
        try {
            certificationService.getDepCode(depNumber);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(certificationService.getDepCode(depNumber));
    }

    // 관리자별 코드 반환 - 로그인 중인 관리자 (이걸로 사용)
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @GetMapping()
    public ResponseEntity<CertificationDto> getDepCode() {
        MemberResDto memberResDto = memberService.getMyMemberInfo();
        Member member =  memberRepository.findByStudentNum(memberResDto.getStudentNum()).get();
        Long depNumber = getDepNum(member.getStudentNum());
        try {
            certificationService.getDepCode(depNumber);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(certificationService.getDepCode(depNumber));
    }

    public Long getDepNum(String studentNum) {
        // "dep"를 제외한 부분
        String depNumString = studentNum.substring(3);
        Long depNum = Long.parseLong(depNumString);
        return depNum;
    }

}
