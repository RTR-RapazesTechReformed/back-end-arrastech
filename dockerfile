# Imagem oficial do RabbitMQ com o plugin de gerenciamento
FROM rabbitmq:3-management

# Porta 5672 -> porta padrão usada pelo RabbitMQ para comunicação AMQP (Advanced Message Queuing Protocol). É a porta pela qual os clientes (aplicações que enviam ou recebem mensagens) se conectam ao servidor RabbitMQ.
# Porta 15672 -> porta usada pelo plugin de gerenciamento do RabbitMQ. É a porta pela qual os usuários se conectam ao servidor RabbitMQ para monitorar e gerenciar o servidor.
EXPOSE 5672 15672

# Comando padrão para iniciar o RabbitMQ
CMD ["rabbitmq-server"]
