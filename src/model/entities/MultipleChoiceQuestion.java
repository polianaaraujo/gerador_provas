package src.model.entities;

public class MultipleChoiceQuestion {
    private int codigo;
    private String tipo;
    private String enunciado;
    private String gabarito;
    private Object disciplina;
    private String assunto;
    private int nivelDificuldade;
    private String[] alternativas = new String[0];

    public int getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getGabarito() {
        return gabarito;
    }

    public Object getDisciplina() {
        return disciplina;
    }

    public String getAssunto() {
        return assunto;
    }

    public int getNivelDificuldade() {
        return nivelDificuldade;
    }

    public String[] getAlternativas() {
        return alternativas;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setGabarito(String gabarito) {
        this.gabarito = gabarito;
    }

    public void setDisciplina(Object disciplina) {
        this.disciplina = disciplina;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public void setNivelDificuldade(int nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
    }

    public void setAlternativas(String[] alternativas) {
        this.alternativas = alternativas;
    }

    public void addAlternativa(String alternativa) {
        String[] novoArray = new String[alternativas.length + 1];

        for (int i = 0; i < alternativas.length; i++) {
            novoArray[i] = alternativas[i];
        }

        novoArray[alternativas.length] = alternativa;

        alternativas = novoArray;
    }

    public void removeAlternativa(String alternativa) {
        String[] novoArray = new String[alternativas.length - 1];

        int j = 0;

        for (int i = 0; i < alternativas.length; i++) {
            if (!alternativas[i].equals(alternativa)) {
                novoArray[j] = alternativas[i];
                j++;
            }
        }

        alternativas = novoArray;
    }
}