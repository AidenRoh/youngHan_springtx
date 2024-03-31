package hello.springtx.order;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderServiceTest {

	@Autowired
	OrderService service;
	@Autowired
	OrderRepository repository;

	@Test
	void order() throws NotEnoughMoneyException {
		//given
		Order order = new Order();
		order.setUsername("정상");
		//when
		service.order(order);
		//then
		Order findOrder = repository.findById(order.getId()).get();
		assertThat(findOrder.getPayStatus()).isEqualTo("완료");
	}

	@Test
	void runtimeException() throws NotEnoughMoneyException {
		//given
		Order order = new Order();
		order.setUsername("예외");
		//when
		assertThatThrownBy(() -> service.order(order))
				.isInstanceOf(RuntimeException.class);
		//then
		Optional<Order> orderOptional = repository.findById(order.getId());
		assertThat(orderOptional.isEmpty()).isTrue();
	}

	@Test
	void checkedException() throws NotEnoughMoneyException {
		//given
		Order order = new Order();
		order.setUsername("잔고 부족");
		//when
		try {
			service.order(order);
		} catch (NotEnoughMoneyException e) {
			log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
		}
		//then
		Order findOrder = repository.findById(order.getId()).get();
		assertThat(findOrder.getPayStatus()).isEqualTo("대기");
	}
}