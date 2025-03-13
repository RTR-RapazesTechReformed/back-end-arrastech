package techForAll.techPoints.moodle

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MoodleSyncProducer(
    private val rabbitTemplate: RabbitTemplate
) {

     // @Scheduled(cron = "0 * * * * *") // Testar funcionamento
    // Agendamento para meia-noite
    //@Scheduled(cron = "0 0 0 * * *")
    fun enviarMensagemDeSincronizacao() {
        val mensagem = "Iniciar sincronização do Moodle"
        rabbitTemplate.convertAndSend("moodleSyncQueue", mensagem)
        println("Mensagem enviada para sincronização: $mensagem")
    }
}