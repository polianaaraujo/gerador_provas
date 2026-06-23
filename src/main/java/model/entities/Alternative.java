package model.entities;

public class Alternative {
    
    private int id;
    private String texto;
    private boolean correta; 
    private Question questao; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isCorreta() {
        return correta;
    }

    public void setCorreta(boolean correta) {
        this.correta = correta;
    }

    public Question getQuestao() {
        return questao;
    }

    public void setQuestao(Question questao) {
        this.questao = questao;
    }
}