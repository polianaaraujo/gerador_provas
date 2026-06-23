package model.entities;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int codigo;
    private QuestionType tipo; 
    private String enunciado;
    private String gabarito;
    private Object disciplina; 
    private String assunto;
    private int nivelDificuldade;
    private Teacher professor;
    private List<Alternative> alternativas = new ArrayList<>(); 

    // Getters e Setters atualizados
    public QuestionType getTipo() {
        return tipo;
    }

    public void setTipo(QuestionType tipo) {
        this.tipo = tipo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getGabarito() {
        return gabarito;
    }

    public void setGabarito(String gabarito) {
        this.gabarito = gabarito;
    }

    public Object getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Object disciplina) {
        this.disciplina = disciplina;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public int getNivelDificuldade() {
        return nivelDificuldade;
    }

    public void setNivelDificuldade(int nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
    }

    public Teacher getProfessor() {
        return professor;
    }

    public void setProfessor(Teacher professor) {
        this.professor = professor;
    }
    public List<Alternative> getAlternativas() {
        return alternativas;
    }
    public void setAlternativas(List<Alternative> alternativas) {
        this.alternativas = alternativas;
    }
}