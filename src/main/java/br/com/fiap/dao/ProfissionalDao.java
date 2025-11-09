package br.com.fiap.dao;

import br.com.fiap.models.Profissional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por realizar operações de persistência relacionadas à entidade {@link Profissional}.
 * Inclui métodos de CRUD e busca por CRM.
 */
@ApplicationScoped
public class ProfissionalDao {

    @Inject
    DataSource dataSource;

    /**
     * Cadastra um novo profissional no banco de dados.
     * O ID é gerado de forma aleatória.
     */
    public void cadastrarProfissional(Profissional profissional) {
        String sql = """
            INSERT INTO PROFISSIONAL
            (id_profissional, nome_profissional, especialidade_profissional, tipo_atend, crm_profissional)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            int novoId = (int) (Math.random() * 9999) + 1;
            profissional.setId(novoId);

            int crmLimitado = profissional.getCrm() % 1_000_000;

            ps.setInt(1, profissional.getId());
            ps.setString(2, profissional.getNome());
            ps.setString(3, profissional.getEspecialidade());
            ps.setString(4, profissional.getTipoAtendimento());
            ps.setInt(5, crmLimitado);

            ps.executeUpdate();

            System.out.println("Profissional cadastrado com sucesso! ID: " + novoId);

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar profissional: " + e.getMessage());
            throw new RuntimeException("Erro ao cadastrar profissional", e);
        }
    }

    /**
     * Retorna uma lista de todos os profissionais cadastrados no banco de dados.
     */
    public List<Profissional> listarProfissionais() {
        List<Profissional> lista = new ArrayList<>();
        String sql = "SELECT * FROM PROFISSIONAL ORDER BY nome_profissional";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Profissional p = new Profissional();
                p.setId(rs.getInt("id_profissional"));
                p.setNome(rs.getString("nome_profissional"));
                p.setEspecialidade(rs.getString("especialidade_profissional"));
                p.setTipoAtendimento(rs.getString("tipo_atend"));
                p.setCrm(rs.getInt("crm_profissional"));
                lista.add(p);
            }

            System.out.println(lista.size() + " profissionais listados.");

        } catch (SQLException e) {
            System.err.println("Erro ao listar profissionais: " + e.getMessage());
            throw new RuntimeException("Erro ao listar profissionais", e);
        }

        return lista;
    }

    /**
     * Busca um profissional pelo seu identificador único (ID).
     */
    public Profissional buscarPorId(int id) {
        Profissional profissional = null;
        String sql = "SELECT * FROM PROFISSIONAL WHERE id_profissional = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    profissional = new Profissional();
                    profissional.setId(rs.getInt("id_profissional"));
                    profissional.setNome(rs.getString("nome_profissional"));
                    profissional.setEspecialidade(rs.getString("especialidade_profissional"));
                    profissional.setTipoAtendimento(rs.getString("tipo_atend"));
                    profissional.setCrm(rs.getInt("crm_profissional"));

                    System.out.println("Profissional encontrado: " + profissional.getNome());
                } else {
                    System.out.println("Profissional não encontrado com ID: " + id);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar profissional por ID: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar profissional por ID: " + id, e);
        }

        return profissional;
    }

    /**
     * Atualiza os dados de um profissional existente no banco.
     */
    public Profissional atualizarProfissional(Profissional profissional) {
        if (profissional.getId() == null || profissional.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido para atualização.");
        }

        String sql = """
            UPDATE PROFISSIONAL
            SET nome_profissional = ?, especialidade_profissional = ?, tipo_atend = ?, crm_profissional = ?
            WHERE id_profissional = ?
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, profissional.getNome());
            ps.setString(2, profissional.getEspecialidade());
            ps.setString(3, profissional.getTipoAtendimento());
            ps.setInt(4, profissional.getCrm());
            ps.setInt(5, profissional.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Profissional atualizado com sucesso! ID: " + profissional.getId());
            } else {
                System.out.println("Nenhum profissional encontrado para atualização. ID: " + profissional.getId());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar profissional: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar profissional", e);
        }

        return profissional;
    }

    /**
     * Exclui um profissional do banco de dados pelo seu ID.
     */
    public void excluirProfissional(int id) {
        String sql = "DELETE FROM PROFISSIONAL WHERE id_profissional = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Profissional excluído com sucesso. ID: " + id);
            } else {
                System.out.println("Nenhum profissional encontrado para exclusão. ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir profissional: " + e.getMessage());
            throw new RuntimeException("Erro ao excluir profissional", e);
        }
    }

    /**
     * Busca um profissional pelo seu CRM.
     */
    public Profissional buscarPorCrm(int crm) {
        if (crm <= 0) {
            throw new IllegalArgumentException("CRM inválido. Deve ser um número positivo.");
        }

        Profissional profissional = null;
        String sql = "SELECT * FROM PROFISSIONAL WHERE crm_profissional = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, crm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    profissional = new Profissional();
                    profissional.setId(rs.getInt("id_profissional"));
                    profissional.setNome(rs.getString("nome_profissional"));
                    profissional.setEspecialidade(rs.getString("especialidade_profissional"));
                    profissional.setTipoAtendimento(rs.getString("tipo_atend"));
                    profissional.setCrm(rs.getInt("crm_profissional"));

                    System.out.println("Profissional encontrado com CRM: " + crm);
                } else {
                    System.out.println("Nenhum profissional encontrado com CRM: " + crm);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar profissional por CRM: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar profissional por CRM: " + crm, e);
        }

        return profissional;
    }
}
