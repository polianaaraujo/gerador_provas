package model.services;

import java.util.List;

import model.DAO.AlternativeDAO;
import model.entities.Alternative;

public class AlternativeService {

    private AlternativeDAO alternativeDAO;

    public AlternativeService(AlternativeDAO alternativeDAO) {
        this.alternativeDAO = alternativeDAO;
    }

    public Alternative inserir(Alternative alternativa) {
        validarAlternativa(alternativa);
        return alternativeDAO.inserir(alternativa);
    }

    public List<Alternative> listarPorQuestao(int idQuestao) {
        if (idQuestao <= 0) {
            throw new IllegalArgumentException("Erro: O ID da questão fornecido é inválido.");
        }
        return alternativeDAO.listarPorQuestao(idQuestao);
    }

    public void atualizar(Alternative alternativa) {
        if (alternativa.getId() <= 0) {
            throw new IllegalArgumentException("Erro: ID da alternativa inválido para atualização.");
        }
        validarAlternativa(alternativa);
        alternativeDAO.atualizar(alternativa);
    }

    public void deletar(int idAlternativa) {
        if (idAlternativa <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        alternativeDAO.deletar(idAlternativa);
    }

    private void validarAlternativa(Alternative alternativa) {
        if (alternativa == null) {
            throw new IllegalArgumentException("Erro: A alternativa não pode ser nula.");
        }
        if (alternativa.getTexto() == null || alternativa.getTexto().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O texto da alternativa não pode estar vazio.");
        }
        if (alternativa.getQuestao() == null || alternativa.getQuestao().getCodigo() <= 0) {
            throw new IllegalArgumentException("Erro: A alternativa precisa estar vinculada a uma questão válida.");
        }
    }
}