public class QuestaoMultiplaEscolha{
    private int codigo;
    private String tipo;
    private String enunciado;
    private String gabarito;
    private Disciplina disciplina;
    private String assunto;
    private int nivelDificuldade;
    private String[] alternativas = new String[0];


    //construtor
    public QuestaoMultiplaEscolha(int codigo, String tipo, String enunciado, String gabarito, Disciplina disciplina, String assunto, int nivelDificuldade) {
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
    public String[] getAlternativas() { return alternativas; }

    //setters
    public void setcodigo(int codigo) {this.codigo = codigo;}
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public void setGabarito(String gabarito) { this.gabarito = gabarito; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    public void setNivelDificuldade(int nivelDificuldade) { this.nivelDificuldade = nivelDificuldade; }

    public void addAlternativa(String alternativa){
        String[] novoArray = new String[alternativas.length + 1]; // cria um novo Array com o tamanho +1

        for (int i = 0; i < alternativas.length; i++){ //copia todos os elementos do antigo array pro novo
            novoArray[i] = alternativas[i];  
        }

        novoArray[alternativas.length] = alternativa; // coloca no array novo a nova alternativa

        alternativas = novoArray; // substitui o antigo pelo o novo
    }
    public void removeAlternativa(String alternativa){
        String[] novoArray = new String[alternativas.length -1]; // cria um array com o tamanho -1

        // copia todos os elementos, menos o que irá ser retirado!
        int j = 0; // j serve para nao deixar buracos no array
        for (int i = 0; i < alternativas.length; i++){
            if(!alternativas[i].equals(alternativa)){
                novoArray[j] = alternativas[i];
                j++;
            }
        }

        alternativas = novoArray; // substitui o antigo pelo o novo
    }
}
