public class QuestaoMultiplaEscolha{
    private int codigo;
    private String tipo;
    private String enunciado;
    private String gabarito;
    private Disciplina disciplina;
    private String assunto;
    private int nivelDificuldade;
    private String[] alternativas = new String[0];

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
