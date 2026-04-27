public class Disciplina {
    private int codigo;
    private String nome;
    private String[] assuntos;

    public Disciplina(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.assuntos = new String[0];
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        if (codigo > 0) {
            this.codigo = codigo;
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null && !nome.isEmpty()) {
            this.nome = nome;
        }
    }

    public String[] getAssuntos() {
        return assuntos;
    }

    public void setAssuntos(String[] assuntos) {
        if (assuntos != null) {
            this.assuntos = assuntos;
        }
    }

    public void adicionarAssunto(String assunto) {
        if (assunto != null && !assunto.isEmpty()) {
            String[] assuntosAtualizados = new String[assuntos.length + 1];

            // copia os assuntos antigos para o novo vetor
            for (int i = 0; i < assuntos.length; i++) {
                assuntosAtualizados[i] = assuntos[i];
            }

            // adiciona o novo assunto na última posição
            assuntosAtualizados[assuntos.length] = assunto;

            // atualiza o vetor principal
            assuntos = assuntosAtualizados;
        }
    }

    public void removerAssunto(String assunto) {
        int posicao = -1;

        // procura a posição do assunto que será removido
        for (int i = 0; i < assuntos.length; i++) {
            if (assuntos[i].equals(assunto)) {
                posicao = i;
            }
        }

        if (posicao != -1) {
            String[] assuntosAtualizados = new String[assuntos.length - 1];
            int j = 0;

            // copia todos os assuntos, menos o que será removido
            for (int i = 0; i < assuntos.length; i++) {
                if (i != posicao) {
                    assuntosAtualizados[j] = assuntos[i];
                    j++;
                }
            }

            assuntos = assuntosAtualizados;
        }
    }
}