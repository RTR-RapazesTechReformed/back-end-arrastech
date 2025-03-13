-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb3 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`usuario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`usuario` ;

CREATE TABLE IF NOT EXISTS `mydb`.`usuario` (
                                                `autenticado` BIT(1) NULL DEFAULT NULL,
    `deletado` BIT(1) NOT NULL,
    `data_atualizacao` DATETIME(6) NULL DEFAULT NULL,
    `data_criacao` DATETIME(6) NOT NULL,
    `data_deletado` DATETIME(6) NULL DEFAULT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `cpf` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `nome_usuario` VARCHAR(255) NOT NULL,
    `primeiro_nome` VARCHAR(255) NOT NULL,
    `senha` VARCHAR(255) NOT NULL,
    `sobrenome` VARCHAR(255) NOT NULL,
    `telefone` VARCHAR(255) NOT NULL,
    `imagem_perfil` VARBINARY(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `UK_692bsnqxa8m9fmx7m1yc6hsui` (`cpf` ASC) VISIBLE,
    UNIQUE INDEX `UK_5171l57faosmj8myawaucatdw` (`email` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`endereco`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`endereco` ;

CREATE TABLE IF NOT EXISTS `mydb`.`endereco` (
                                                 `data_atualizacao` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `cep` VARCHAR(255) NOT NULL,
    `cidade` VARCHAR(255) NOT NULL,
    `estado` VARCHAR(255) NOT NULL,
    `logradouro` VARCHAR(255) NOT NULL,
    `numero` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`aluno`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`aluno` ;

CREATE TABLE IF NOT EXISTS `mydb`.`aluno` (
                                              `dt_nasc` DATE NOT NULL,
                                              `endereco_id` BIGINT NULL DEFAULT NULL,
                                              `id` BIGINT NOT NULL,
                                              `descricao` VARCHAR(255) NULL DEFAULT NULL,
    `escolaridade` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKeooy7cntssxbfvyx7odcig95j` (`endereco_id` ASC) VISIBLE,
    CONSTRAINT `FKc8wsngo14dwn23nvgsty37bfx`
    FOREIGN KEY (`id`)
    REFERENCES `mydb`.`usuario` (`id`),
    CONSTRAINT `FKeooy7cntssxbfvyx7odcig95j`
    FOREIGN KEY (`endereco_id`)
    REFERENCES `mydb`.`endereco` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`curso`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`curso` ;

CREATE TABLE IF NOT EXISTS `mydb`.`curso` (
                                              `total_atividades` INT NOT NULL,
                                              `total_atividades_do_aluno` INT NOT NULL,
                                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                                              `nome` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`aluno_curso`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`aluno_curso` ;

CREATE TABLE IF NOT EXISTS `mydb`.`aluno_curso` (
                                                    `aluno_id` BIGINT NOT NULL,
                                                    `curso_id` BIGINT NOT NULL,
                                                    INDEX `FKdj5jni194btne1xo5brdaxtpy` (`curso_id` ASC) VISIBLE,
    INDEX `FKtbnn52vknd9q7tapxfbbymbot` (`aluno_id` ASC) VISIBLE,
    CONSTRAINT `FKdj5jni194btne1xo5brdaxtpy`
    FOREIGN KEY (`curso_id`)
    REFERENCES `mydb`.`curso` (`id`),
    CONSTRAINT `FKtbnn52vknd9q7tapxfbbymbot`
    FOREIGN KEY (`aluno_id`)
    REFERENCES `mydb`.`aluno` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`dados_empresa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`dados_empresa` ;

CREATE TABLE IF NOT EXISTS `mydb`.`dados_empresa` (
                                                      `data_atualizacao` DATETIME(6) NOT NULL,
    `endereco_id` BIGINT NULL DEFAULT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `cnpj` VARCHAR(255) NOT NULL,
    `nome_empresa` VARCHAR(255) NOT NULL,
    `setor_industria` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `UK_5iiv4s6xpowlrfis7ynmb02sj` (`cnpj` ASC) VISIBLE,
    INDEX `FKo45gbmr1ne2lta5wwxt0c4myt` (`endereco_id` ASC) VISIBLE,
    CONSTRAINT `FKo45gbmr1ne2lta5wwxt0c4myt`
    FOREIGN KEY (`endereco_id`)
    REFERENCES `mydb`.`endereco` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`meta_estudo_semana`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`meta_estudo_semana` ;

CREATE TABLE IF NOT EXISTS `mydb`.`meta_estudo_semana` (
                                                           `horas_total` DOUBLE NOT NULL,
                                                           `aluno_id` BIGINT NULL DEFAULT NULL,
                                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                           PRIMARY KEY (`id`),
    INDEX `FKql3xo6ba163s3j3urjdj7feb` (`aluno_id` ASC) VISIBLE,
    CONSTRAINT `FKql3xo6ba163s3j3urjdj7feb`
    FOREIGN KEY (`aluno_id`)
    REFERENCES `mydb`.`aluno` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`pontuacao`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`pontuacao` ;

CREATE TABLE IF NOT EXISTS `mydb`.`pontuacao` (
                                                  `nota_aluno` DOUBLE NOT NULL,
                                                  `nota_atividade` DOUBLE NOT NULL,
                                                  `aluno_id` BIGINT NULL DEFAULT NULL,
                                                  `curso_id` BIGINT NULL DEFAULT NULL,
                                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                  `data_entrega` VARCHAR(255) NOT NULL,
    `nome_atividade` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK9c8ylx7wp8cl5bakvfaswjs04` (`aluno_id` ASC) VISIBLE,
    INDEX `FKgdxtiwh119bqpvksatc74eqcn` (`curso_id` ASC) VISIBLE,
    CONSTRAINT `FK9c8ylx7wp8cl5bakvfaswjs04`
    FOREIGN KEY (`aluno_id`)
    REFERENCES `mydb`.`aluno` (`id`),
    CONSTRAINT `FKgdxtiwh119bqpvksatc74eqcn`
    FOREIGN KEY (`curso_id`)
    REFERENCES `mydb`.`curso` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`recrutador`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`recrutador` ;

CREATE TABLE IF NOT EXISTS `mydb`.`recrutador` (
                                                   `empresa_id` BIGINT NOT NULL,
                                                   `id` BIGINT NOT NULL,
                                                   `cargo_usuario` VARCHAR(255) NOT NULL,
    `favoritos_json` JSON NULL DEFAULT NULL,
    `interessados_json` JSON NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKhkapt1gk0sqhi6njc4murynkg` (`empresa_id` ASC) VISIBLE,
    CONSTRAINT `FKhkapt1gk0sqhi6njc4murynkg`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `mydb`.`dados_empresa` (`id`),
    CONSTRAINT `FKnoniv3jgxtivnwo5tr6fuaoux`
    FOREIGN KEY (`id`)
    REFERENCES `mydb`.`usuario` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`tempo_estudo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`tempo_estudo` ;

CREATE TABLE IF NOT EXISTS `mydb`.`tempo_estudo` (
                                                     `ativado` BIT(1) NOT NULL,
    `meta_atingida` BIT(1) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `meta_estudo_semana_id` BIGINT NULL DEFAULT NULL,
    `nome_dia` VARCHAR(255) NOT NULL,
    `qtd_tempo_estudo` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK5tm5dh9v3793jehwjavctfo4p` (`meta_estudo_semana_id` ASC) VISIBLE,
    CONSTRAINT `FK5tm5dh9v3793jehwjavctfo4p`
    FOREIGN KEY (`meta_estudo_semana_id`)
    REFERENCES `mydb`.`meta_estudo_semana` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`tempo_sessao`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`tempo_sessao` ;

CREATE TABLE IF NOT EXISTS `mydb`.`tempo_sessao` (
                                                     `qtd_tempo_sessao` DOUBLE NOT NULL,
                                                     `aluno_id` BIGINT NULL DEFAULT NULL,
                                                     `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                     `meta_estudo_semana_id` BIGINT NULL DEFAULT NULL,
                                                     `dia_sessao` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKmn6cqce8epfnd5hxa4clratp0` (`aluno_id` ASC) VISIBLE,
    INDEX `FKp1nh2k98c8o4e32vimprkoy2r` (`meta_estudo_semana_id` ASC) VISIBLE,
    CONSTRAINT `FKmn6cqce8epfnd5hxa4clratp0`
    FOREIGN KEY (`aluno_id`)
    REFERENCES `mydb`.`aluno` (`id`),
    CONSTRAINT `FKp1nh2k98c8o4e32vimprkoy2r`
    FOREIGN KEY (`meta_estudo_semana_id`)
    REFERENCES `mydb`.`meta_estudo_semana` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
