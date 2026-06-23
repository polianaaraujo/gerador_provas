package model.entities;

import java.time.LocalDate;

public class Exam {

    private int codigo;
    private String titulo;
    private Subject disciplina;
    private Question[] questoes = new Question[0];
    private LocalDate dataDeCriacao;
    private String semestre;
    private Teacher professor;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Subject getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Subject disciplina) {
        this.disciplina = disciplina;
    }

    public Question[] getQuestoes() {
        return questoes;
    }

    public void setQuestoes(Question[] questoes) {
        this.questoes = questoes;
    }

    public LocalDate getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(LocalDate dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public Teacher getProfessor() {
        return professor;
    }

    public void setProfessor(Teacher professor) {
        this.professor = professor;
    }

    public void addQuestao(Question questao) {

        Question[] questoesAtualizadas = new Question[questoes.length + 1];

        for (int i = 0; i < questoes.length; i++) {
            questoesAtualizadas[i] = questoes[i];
        }

        questoesAtualizadas[questoes.length] = questao;

        questoes = questoesAtualizadas;
    }

    public void removerQuestao(Question questao) {

        int posicao = -1;

        for (int i = 0; i < questoes.length; i++) {
            if (questoes[i] == questao) {
                posicao = i;
                break;
            }
        }

        if (posicao != -1) {

            Question[] questoesAtualizadas = new Question[questoes.length - 1];

            int j = 0;

            for (int i = 0; i < questoes.length; i++) {

                if (i != posicao) {
                    questoesAtualizadas[j] = questoes[i];
                    j++;
                }
            }

            questoes = questoesAtualizadas;
        }
    }
}