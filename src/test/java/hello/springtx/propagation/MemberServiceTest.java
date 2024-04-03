package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

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
	 * memberService @Transactional:OFF
	 * memberRepository @Transactional:ON
	 * logRepository @Transactional:ON
	 */

	@Test
	void outerTxOff_success() {
		//given
		String username = "outerTxOff_success";
		//when
		service.joinV1(username);
		//then: 모든 데이터가 정상 저장된다.
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isPresent());
	}

	@Test
	void outerTxOff_fail() {
		//given
		String username = "로그예외_outerTxOff_fail";
		//when
		assertThatThrownBy(() -> service.joinV1(username))
				.isInstanceOf(RuntimeException.class);

		//then: 로그 리포지토리는 롤백된다.
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService @Transactional:ON
	 * memberRepository @Transactional:OFF
	 * logRepository @Transactional:OFF
	 */
	@Test
	void singleTx() {
		//given
		String username = "singleTx";
		//when
		service.joinV1(username);

		//then: 모든 데이터가 정상 저장된다.
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService @Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository @Transactional:ON
	 */
	@Test
	void outer_TxOn_success() {
		//given
		String username = "outer_TxOn_success";
		//when
		service.joinV1(username);

		//then: 모든 데이터가 정상 저장된다.
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService @Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository @Transactional:ON Exception
	 */
	@Test
	void outer_TxOn_fail() {
		//given
		String username = "로그예외_outer_TxOn_success";
		//when
		assertThatThrownBy(() -> service.joinV1(username))
				.isInstanceOf(RuntimeException.class);
		//then: 모든 데이터가 롤백된다.
		Assertions.assertTrue(memberRepository.find(username).isEmpty());
		Assertions.assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService @Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository @Transactional:ON Exception
	 */
	@Test
	void recoverException_fail() {
		//given
		String username = "로그예외_recoverException_fail";
		//when
		assertThatThrownBy(() -> service.joinV2(username))
				.isInstanceOf(UnexpectedRollbackException.class);
		//then: 모든 데이터가 롤백된다.
		Assertions.assertTrue(memberRepository.find(username).isEmpty());
		Assertions.assertTrue(logRepository.find(username).isEmpty());
	}

	@Test
	void recoverException_success() {
		//given
		String username = "로그예외_recoverException_success";
		//when
		service.joinV2(username);
		//then: member 저장, log 롤백
		Assertions.assertTrue(memberRepository.find(username).isPresent());
		Assertions.assertTrue(logRepository.find(username).isEmpty());
	}
}