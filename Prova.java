import java.time.LocalDate;

public class Prova {
    private int codigo;
    private Disciplina disciplina;
    private Questao[] questoes;
    private LocalDate dataDeCriacao;
    private String semestre;
    private Usuario usuario;
    private Professor professor;

    public Prova(int codigo, Disciplina disciplina, String semestre, Usuario usuario, Professor professor) {
        this.codigo = codigo;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.usuario = usuario;
        this.professor = professor;
        this.questoes = new Questao[0];
        this.dataDeCriacao = LocalDate.now(); // data automática
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        if (codigo > 0) {
            this.codigo = codigo;
        }
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }

    public Questao[] getQuestoes() {
        return questoes;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        if (semestre != null && !semestre.isEmpty()) {
            this.semestre = semestre;
        }
    }

    public LocalDate getDataDeCriacao() {
        return dataDeCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuario = usuario;
        }
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        if (professor != null) {
            this.professor = professor;
        }
    }

    public void addQuestao(Questao questao) {
        if (questao != null) {
            Questao[] questoesAtualizadas = new Questao[questoes.length + 1];

            // copia as questões antigas para o novo vetor
            for (int i = 0; i < questoes.length; i++) {
                questoesAtualizadas[i] = questoes[i];
            }

            // adiciona a nova questão na última posição
            questoesAtualizadas[questoes.length] = questao;

            // atualiza o vetor principal
            questoes = questoesAtualizadas;
        }
    }

    public void removerQuestao(Questao questao) {
        int posicao = -1;

        // procura a posição da questão que será removida
        for (int i = 0; i < questoes.length; i++) {
            if (questoes[i] == questao) {
                posicao = i;
            }
        }

        if (posicao != -1) {
            Questao[] questoesAtualizadas = new Questao[questoes.length - 1];
            int j = 0;

            // copia todas as questões, menos a que será removida
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