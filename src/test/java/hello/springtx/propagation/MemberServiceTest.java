package hello.springtx.propagation;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService service;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	LogRepository logRepository;

	/**
	 * memberService @Transactional:OFF memberRepository @Transactional:ON logRepository @Transactional:ON
	 */

	@Test
	void outerTxoff_success() {
		//given
		String username = "outerTxOff_success";
		//when
		service.joinV1(username);
		//then: 모든 데이터가 정상 저장된다.
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isPresent());
	}
}