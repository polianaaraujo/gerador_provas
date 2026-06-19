public class Questao{
    private int codigo;
    private String tipo;
    private String enunciado;
    private String gabarito;
    private Disciplina disciplina;
    private String assunto;
    private int nivelDificuldade;
    private Usuario usuario;
    private Professor professor;


    //construtor
    public Questao(int codigo, String tipo, String enunciado, String gabarito, Disciplina disciplina, String assunto, int nivelDificuldade) {
    setCodigo(codigo);
    setTipo(tipo);
    setEnunciado(enunciado);
    setGabarito(gabarito);
    setDisciplina(disciplina);
    setAssunto(assunto);
    setNivelDificuldade(nivelDificuldade);
}

    //getters
    public int getCodigo() { return codigo; }
    public String getTipo() { return tipo; }
    public String getEnunciado() { return enunciado; }
    public String getGabarito() { return gabarito; }
    public Disciplina getDisciplina() { return disciplina; }
    public String getAssunto() { return assunto; }
    public int getNivelDificuldade() { return nivelDificuldade; }
    public Professor getProfessor() {
        return professor;
    }
    public Usuario getUsuario() {
        return usuario;
    }

    //setters
    public void setCodigo(int codigo) {this.codigo = codigo;}
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public void setGabarito(String gabarito) { this.gabarito = gabarito; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    public void setNivelDificuldade(int nivelDificuldade) { this.nivelDificuldade = nivelDificuldade; }
    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Questao(int codigo, String enunciado, Disciplina disciplina, String assunto, int nivelDificuldade, Usuario usuario, Professor professor) {
        setCodigo(codigo);
        setEnunciado(enunciado);
        setDisciplina(disciplina);
        setAssunto(assunto);
        setNivelDificuldade(nivelDificuldade);
        setUsuario(usuario);
        setProfessor(professor);
    }

}