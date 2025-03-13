INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`,`tipo_usuario`)
VALUES 
(0, 0, NOW(), NOW(), NULL, '12345678901', 'usuario1@example.com', 'usuario1', 'Primeiro', 'senha123', 'Sobrenome', '123456789', 0x123456, 1),
(0, 0, NOW(), NOW(), NULL, '12345678902', 'usuario2@example.com', 'usuario2', 'Primeiro', 'senha123', 'Sobrenome', '123456789', 0x123456, 1),
(0, 0, NOW(), NOW(), NULL, '12345678903', 'usuario3@example.com', 'usuario3', 'Primeiro', 'senha123', 'Sobrenome', '123456789', 0x123456, 2),
(0, 0, NOW(), NOW(), NULL, '12345678904', 'usuario4@example.com', 'usuario4', 'Primeiro', 'senha123', 'Sobrenome', '123456789', 0x123456, 2),
(0, 0, NOW(), NOW(), NULL, '12345678905', 'usuario5@example.com', 'usuario5', 'Primeiro', 'senha123', 'Sobrenome', '123456789', 0x123456, 2);

INSERT INTO `mydb`.`endereco` (`data_atualizacao`,`data_criacao`, `cep`, `cidade`, `estado`, `logradouro`, `numero`)
VALUES 
(NOW(), NOW(), '12345000', 'Cidade1', 'Estado1', 'Rua A', '100'),
(NOW(), NOW(),'12345001', 'Cidade2', 'Estado2', 'Rua B', '101'),
(NOW(), NOW(),'12345002', 'Cidade3', 'Estado3', 'Rua C', '102'),
(NOW(), NOW(),'12345003', 'Cidade4', 'Estado4', 'Rua D', '103'),
(NOW(), NOW(),'12345004', 'Cidade5', 'Estado5', 'Rua E', '104');

INSERT INTO `mydb`.`aluno` (`dt_nasc`, `endereco_id`, `id`, `descricao`, `escolaridade`)
VALUES 
('2000-01-01', 1, 1, 'Aluno dedicado', 'Ensino Médio'),
('2001-02-02', 2, 2, 'Estudante universitário', 'Ensino Superior'),
('2002-03-03', 3, 3, 'Aluno de curso técnico', 'Curso Técnico'),
('2003-04-04', 4, 4, 'Aluno de pós-graduação', 'Pós-Graduação'),
('2004-05-05', 5, 5, 'Aluno em mestrado', 'Mestrado');

INSERT INTO `mydb`.`curso` (`total_atividades`, `total_atividades_do_aluno`, `nome`)
VALUES 
(10, 8, 'Curso de Matemática'),
(15, 10, 'Curso de Física'),
(20, 15, 'Curso de Química'),
(25, 20, 'Curso de Biologia'),
(30, 25, 'Curso de História');

INSERT INTO `mydb`.`aluno_curso` (`aluno_id`, `curso_id`)
VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO `mydb`.`dados_empresa` (`data_atualizacao`, `endereco_id`, `cnpj`, `nome_empresa`, `setor_industria`)
VALUES 
(NOW(), 1, '12345678000101', 'Empresa 1', 'Tecnologia'),
(NOW(), 2, '12345678000102', 'Empresa 2', 'Educação'),
(NOW(), 3, '12345678000103', 'Empresa 3', 'Saúde'),
(NOW(), 4, '12345678000104', 'Empresa 4', 'Alimentício'),
(NOW(), 5, '12345678000105', 'Empresa 5', 'Comércio');

INSERT INTO `mydb`.`meta_estudo_semana` (`horas_total`, `aluno_id`)
VALUES 
(10, 1),
(15, 2),
(20, 3),
(25, 4),
(30, 5);

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES 
(8.5, 10, 1, 1, '2024-01-01', 'Prova 1'),
(9.0, 10, 2, 2, '2024-02-01', 'Prova 2'),
(7.5, 10, 3, 3, '2024-03-01', 'Prova 3'),
(8.0, 10, 4, 4, '2024-04-01', 'Prova 4'),
(9.5, 10, 5, 5, '2024-05-01', 'Prova 5');

INSERT INTO `mydb`.`recrutador` (`empresa_id`, `id`, `cargo_usuario`, `favoritos_json`, `interessados_json`)
VALUES 
(1, 1, 'Gerente de RH', NULL, NULL),
(2, 2, 'Coordenador de Recrutamento', NULL, NULL),
(3, 3, 'Especialista de Seleção', NULL, NULL),
(4, 4, 'Analista de Recrutamento', NULL, NULL),
(5, 5, 'Recrutador Júnior', NULL, NULL);

INSERT INTO `mydb`.`tempo_estudo` (`ativado`, `meta_atingida`, `meta_estudo_semana_id`, `nome_dia`, `qtd_tempo_estudo`)
VALUES 
(1, 0, 1, 'Segunda-feira', '2'),
(1, 1, 2, 'Terça-feira', '3'),
(0, 0, 3, 'Quarta-feira', '1'),
(1, 1, 4, 'Quinta-feira', '4'),
(0, 0, 5, 'Sexta-feira', '5');

INSERT INTO `mydb`.`tempo_sessao` (`qtd_tempo_sessao`, `aluno_id`, `meta_estudo_semana_id`, `dia_sessao`)
VALUES 
(1.5, 1, 1, '2024-01-01'),
(2.0, 2, 2, '2024-01-02'),
(1.0, 3, 3, '2024-01-03'),
(2.5, 4, 4, '2024-01-04'),
(3.0, 5, 5, '2024-01-05');
