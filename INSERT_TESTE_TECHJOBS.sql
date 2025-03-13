
INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`, `tipo_usuario`)
VALUES (b'1', b'0', NOW(), NOW(), NULL, '12345678901', 'usuario1@example.com', 'usuario1', 'João', 'senha1', 'Silva', '11987654321', "https://images.pexels.com/photos/1310522/pexels-photo-1310522.jpeg?cs=srgb&dl=pexels-george-dolgikh-551816-1310522.jpg&fm=jpg", 1);

INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`, `tipo_usuario`)
VALUES (b'0', b'0', NOW(), NOW(), NULL, '23456789012', 'usuario5@example.com', 'usuario2', 'Maria', 'senha2', 'Souza', '11912345678', "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_HFQbK0SjP6lVSn7FUknx5MVcXFb5GOW0sA&s", 2);

INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`, `tipo_usuario`)
VALUES (b'1', b'0', NOW(), NOW(), NULL, '98765432109', 'usuario2@example.com', 'usuario2', 'Maria', 'senha2', 'Oliveira', '11987654322', 'https://images.pexels.com/photos/1036623/pexels-photo-1036623.jpeg?cs=srgb&dl=pexels-brooke-cagle-1036623.jpg&fm=jpg', 2);

INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`, `tipo_usuario`)
VALUES (b'1', b'0', NOW(), NOW(), NULL, '15975348620', 'usuario3@example.com', 'usuario3', 'Pedro', 'senha3', 'Santos', '11987654323', 'https://images.pexels.com/photos/1183266/pexels-photo-1183266.jpeg?cs=srgb&dl=pexels-johnny-edgardo-p%C3%A9rez-rodr%C3%ADguez-1183266.jpg&fm=jpg', 1);

INSERT INTO `mydb`.`usuario` (`autenticado`, `deletado`, `data_atualizacao`, `data_criacao`, `data_deletado`, `cpf`, `email`, `nome_usuario`, `primeiro_nome`, `senha`, `sobrenome`, `telefone`, `imagem_perfil`, `tipo_usuario`)
VALUES (b'1', b'0', NOW(), NOW(), NULL, '74185296350', 'usuario4@example.com', 'usuario4', 'Ana', 'senha4', 'Costa', '11987654324', 'https://images.pexels.com/photos/4587187/pexels-photo-4587187.jpeg?cs=srgb&dl=pexels-cottonbro-4587187.jpg&fm=jpg', 2);

INSERT INTO `mydb`.`endereco` (`data_atualizacao`, `data_criacao`, `deletado`, `cep`, `cidade`, `estado`, `logradouro`, `numero`)
VALUES (NOW(), NOW(), b'0', '12345-678', 'São Paulo', 'SP', 'Rua Exemplo', '100');

INSERT INTO `mydb`.`endereco` (`data_atualizacao`, `data_criacao`, `deletado`, `cep`, `cidade`, `estado`, `logradouro`, `numero`)
VALUES (NOW(), NOW(), b'0', '87654-321', 'Rio de Janeiro', 'RJ', 'Avenida Teste', '200');

INSERT INTO `mydb`.`aluno` (`dt_nasc`, `endereco_id`, `id`, `descricao`, `escolaridade`)
VALUES ('1990-01-01', 1, 1, 'Aluno do ensino médio', 'Ensino Médio Completo');

INSERT INTO `mydb`.`aluno` (`dt_nasc`, `endereco_id`, `id`, `descricao`, `escolaridade`)
VALUES ('1995-05-15', 2, 2, 'Aluno de graduação', 'Ensino Superior Incompleto');

INSERT INTO `mydb`.`aluno` (`dt_nasc`, `endereco_id`, `id`, `descricao`, `escolaridade`)
VALUES ('1990-01-01', 1, 3, 'Aluno do ensino médio', 'Ensino Médio Completo');

INSERT INTO `mydb`.`aluno` (`dt_nasc`, `endereco_id`, `id`, `descricao`, `escolaridade`)
VALUES ('1995-05-15', 2, 4, 'Aluno de graduação', 'Ensino Superior Incompleto');

INSERT INTO `mydb`.`curso` (`total_atividades`, `total_atividades_do_aluno`, `nome`, `categoria`)
VALUES (10, 5, 'Curso de Programação', 'Programação');

INSERT INTO `mydb`.`curso` (`total_atividades`, `total_atividades_do_aluno`, `nome`, `categoria`)
VALUES (8, 3, 'Curso de Matemática', 'Programação');

INSERT INTO `mydb`.`aluno_curso` (`aluno_id`, `curso_id`)
VALUES (1, 1);

INSERT INTO `mydb`.`aluno_curso` (`aluno_id`, `curso_id`)
VALUES (2, 2);

INSERT INTO `mydb`.`dados_empresa` (`data_atualizacao`, `endereco_id`, `cnpj`, `nome_empresa`, `setor_industria`)
VALUES (NOW(), 1, '12345678000101', 'Empresa Exemplo', 'Tecnologia');

INSERT INTO `mydb`.`dados_empresa` (`data_atualizacao`, `endereco_id`, `cnpj`, `nome_empresa`, `setor_industria`)
VALUES (NOW(), 2, '23456789000102', 'Empresa Teste', 'Educação');

INSERT INTO `mydb`.`meta_estudo_semana` (`horas_total`, `aluno_id`)
VALUES (10, 1);

INSERT INTO `mydb`.`meta_estudo_semana` (`horas_total`, `aluno_id`)
VALUES (15, 2);

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (9.5, 10, 1, 1, '2024-09-07', 'Atividade 1');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (8.0, 10, 2, 2, '2024-09-07', 'Atividade 2');

INSERT INTO `mydb`.`recrutador` (`empresa_id`, `id`, `cargo_usuario`, `favoritos_json`, `interessados_json`)
VALUES (1, 1, 'Gerente de RH', NULL, NULL);

INSERT INTO `mydb`.`recrutador` (`empresa_id`, `id`, `cargo_usuario`, `favoritos_json`, `interessados_json`)
VALUES (2, 2, 'Coordenador de Seleção', NULL, NULL);

INSERT INTO `mydb`.`tempo_estudo` (`ativado`, `meta_atingida`, `meta_estudo_semana_id`, `nome_dia`, `qtd_tempo_estudo`)
VALUES (b'1', b'0', 1, 'Segunda-feira', '2h');

INSERT INTO `mydb`.`tempo_estudo` (`ativado`, `meta_atingida`, `meta_estudo_semana_id`, `nome_dia`, `qtd_tempo_estudo`)
VALUES (b'1', b'1', 2, 'Terça-feira', '3h');

INSERT INTO `mydb`.`tempo_sessao` (`qtd_tempo_sessao`, `aluno_id`, `meta_estudo_semana_id`, `dia_sessao`)
VALUES (2.5, 1, 1, '2024-09-07');

INSERT INTO `mydb`.`tempo_sessao` (`qtd_tempo_sessao`, `aluno_id`, `meta_estudo_semana_id`, `dia_sessao`)
VALUES (3.0, 2, 2, '2024-09-07');

INSERT INTO `mydb`.`curso` (`total_atividades`, `total_atividades_do_aluno`, `nome`, `categoria`)
VALUES (10, 5, 'Introdução à Programação', 'Programação');

INSERT INTO `mydb`.`curso` (`total_atividades`, `total_atividades_do_aluno`, `nome`, `categoria`)
VALUES (8, 4, 'Banco de Dados Relacional', 'Programação');

INSERT INTO `mydb`.`aluno_curso` (`aluno_id`, `curso_id`)
VALUES (1, 2); -- Supondo que o curso com id 2 já exista

INSERT INTO `mydb`.`aluno_curso` (`aluno_id`, `curso_id`)
VALUES (1, 3); -- Supondo que o curso com id 3 já exista

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (9.0, 10.0, 1, 2, '2024-09-05', 'Atividade 1 - Introdução à Programação');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (8.5, 9.0, 1, 2, '2024-09-10', 'Atividade 2 - Introdução à Programação');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (7.5, 8.0, 1, 3, '2024-09-12', 'Atividade 1 - Banco de Dados Relacional');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (9.2, 9.5, 1, 3, '2024-09-18', 'Atividade 2 - Banco de Dados Relacional');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (8.8, 9.0, 1, 2, '2024-09-20', 'Atividade 3 - Introdução à Programação');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (NULL, 9.0, 1, 2, '2024-09-20', 'Atividade 4 - Fundamentos de Algoritmos');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (NULL, 9.0, 1, 2, '2024-09-20', 'Atividade 5 - Fundamentos de Estruturas de Dados');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (8.5, 8.5, 1, 3, '2024-09-07', 'Atividade 1 - Estruturas de Dados');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (7.0, 7.0, 2, 1, '2024-09-06', 'Atividade 2 - Programação Orientada a Objetos');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (NULL, 9.0, 3, 4, '2024-09-05', 'Atividade 3 - Redes de Computadores');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (10.0, 10.0, 1, 2, '2024-09-08', 'Atividade 4 - Banco de Dados');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (NULL, 6.0, 1, 3, '2024-09-09', 'Atividade 5 - Engenharia de Software');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (2.0, 7.0, 2, 2, '2024-09-12', 'Atividade 6 - Sistemas Operacionais');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (5.0, 5.0, 2, 3, '2024-09-13', 'Atividade 7 - Desenvolvimento Web');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (3.0, 9.0, 1, 1, '2024-09-14', 'Atividade 8 - Segurança da Informação');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (2.0, 6.0, 1, 2, '2024-09-11', 'Atividade 9 - Inteligência Artificial');

INSERT INTO `mydb`.`pontuacao` (`nota_aluno`, `nota_atividade`, `aluno_id`, `curso_id`, `data_entrega`, `nome_atividade`)
VALUES (5.0, 10.0, 1, 3, '2024-09-10', 'Atividade 10 - Machine Learning');

SELECT * from pontuacao where aluno_id = 2;
