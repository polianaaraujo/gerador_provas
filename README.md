# Gerador de Provas

Um sistema de gerenciamento acadêmico desenvolvido para facilitar a criação, organização e estruturação de provas. O software permite o cadastro de disciplinas, gerenciamento de professores e administradores, e a manutenção de um banco de questões dinâmico (múltipla escolha e discursivas) para a geração automatizada de exames.

##  Tecnologias Utilizadas

* **Linguagem:** Java
* **Interface Gráfica:** JavaFX (com FXML e Scene Builder)
* **Banco de Dados:** MySQL
* **Arquitetura:** Padrão MVC / MVP com Data Access Object (DAO) para persistência.
* **Controle de Versão:** Git e GitHub

## Pré-requisitos

Para executar o projeto localmente, você precisará instalar as seguintes ferramentas:

* **Java Development Kit (JDK):** Versão 17 ou superior.
* **Banco de Dados:** MySQL Server rodando localmente (porta padrão `3306`).
* **IDE:** IntelliJ IDEA, Eclipse ou similar com suporte a projetos JavaFX / Maven.

##  Tutorial de Execução

Siga os passos abaixo para configurar o ambiente e rodar o sistema na sua máquina:

### 1. Clonar o Repositório
Abra o seu terminal e clone o projeto:
```bash
git clone [https://github.com/seu-usuario/nome-do-repositorio.git](https://github.com/seu-usuario/nome-do-repositorio.git)
cd nome-do-repositorio
```


###  Criação do Banco de Dados e Tabelas (SQL)

Execute o script abaixo no seu cliente SQL (como o DBeaver) para criar o esquema do banco de dados com todas as chaves estrangeiras, restrições e índices configurados:

```sql
CREATE DATABASE gerenciador_provas;
USE DATABASE gerenciador_provas;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. CRIAÇÃO DAS TABELAS CONFIGURADAS
CREATE TABLE IF NOT EXISTS user (
    id_user   INT          NOT NULL AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(150) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL,
    status    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_user       PRIMARY KEY (id_user),
    CONSTRAINT uq_user_email UNIQUE      (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS admin (
    admin_id  INT NOT NULL,
    CONSTRAINT pk_admin         PRIMARY KEY (admin_id),
    CONSTRAINT fk_admin_user    FOREIGN KEY (admin_id) REFERENCES user (id_user) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS teacher (
    teacher_id          INT         NOT NULL,
    registration_number VARCHAR(50) NOT NULL,
    CONSTRAINT pk_teacher       PRIMARY KEY (teacher_id),
    CONSTRAINT fk_teacher_user  FOREIGN KEY (teacher_id) REFERENCES user (id_user) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS subjects (
    id_subject INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(150) NOT NULL,
    code       VARCHAR(20)  NOT NULL,
    topics     TEXT,
    teacher_id INT,
    CONSTRAINT pk_subjects          PRIMARY KEY (id_subject),
    CONSTRAINT uq_subjects_code     UNIQUE      (code),
    CONSTRAINT fk_subjects_teacher  FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS question (
    question_id   INT          NOT NULL AUTO_INCREMENT,
    question_type ENUM('MULTIPLE_CHOICE', 'DISCURSIVE') NOT NULL,
    statement     TEXT         NOT NULL,
    answer_key    TEXT         NOT NULL,
    topic         VARCHAR(150) NOT NULL,
    subject_id    INT          NOT NULL,
    difficulty    VARCHAR(50)  NOT NULL,
    alt_a         VARCHAR(500),
    alt_b         VARCHAR(500),
    alt_c         VARCHAR(500),
    alt_d         VARCHAR(500),
    expected_lines INT,
    teacher_id    INT,
    CONSTRAINT pk_question          PRIMARY KEY (question_id),
    CONSTRAINT fk_question_subject  FOREIGN KEY (subject_id) REFERENCES subjects (id_subject) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_question_teacher  FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS exams (
    exam_id       INT         NOT NULL AUTO_INCREMENT,
    creation_date DATE,
    semester      VARCHAR(10) NOT NULL,
    subject_id    INT         NOT NULL,
    teacher_id    INT         NOT NULL,
    CONSTRAINT pk_exams          PRIMARY KEY (exam_id),
    CONSTRAINT fk_exams_subject  FOREIGN KEY (subject_id) REFERENCES subjects (id_subject) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_exams_teacher  FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS exam_question (
    exam_id     INT NOT NULL,
    question_id INT NOT NULL,
    CONSTRAINT pk_exam_question         PRIMARY KEY (exam_id, question_id),
    CONSTRAINT fk_eq_exam               FOREIGN KEY (exam_id) REFERENCES exams (exam_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_eq_question           FOREIGN KEY (question_id) REFERENCES question (question_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- 2. CRIAÇÃO DOS ÍNDICES PARA OTIMIZAÇÃO DE BUSCAS
CREATE INDEX idx_question_subject    ON question (subject_id);
CREATE INDEX idx_question_difficulty ON question (difficulty);
CREATE INDEX idx_question_teacher    ON question (teacher_id);
CREATE INDEX idx_exams_subject       ON exams (subject_id);
CREATE INDEX idx_exams_semester      ON exams (semester);
CREATE INDEX idx_exams_teacher       ON exams (teacher_id);
CREATE INDEX idx_subjects_teacher    ON subjects (teacher_id);
```
###  Configuração do Banco de Dados 
```java
public class ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/gerenciador-provas (ou a sua rota)";
    private static final String USER = "root"; 
    private static final String PASSWORD = "sua_senha_aqui";

