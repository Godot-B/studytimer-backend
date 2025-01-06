package umc.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import umc.hackathon.service.TimerService;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class HackathonApplication {

	public static void main(String[] args) {
		/*ApplicationContext context = */SpringApplication.run(HackathonApplication.class, args);
/*
		TimerService timerService = context.getBean(TimerService.class);

		// 스케줄러 로직 직접 실행
		System.out.println("스케줄러 직접 호출 시작");
		timerService.createNewDatePlan();
		System.out.println("스케줄러 직접 호출 완료");*/
	}

}
