package techForAll.techPoints

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TechPointsApplication

fun main(args: Array<String>) {
	runApplication<TechPointsApplication>(*args)
}
