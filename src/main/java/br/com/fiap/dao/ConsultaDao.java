package br.com.fiap.dao;

import br.com.fiap.models.Consulta;
import br.com.fiap.models.Profissional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por realizar operações de persistência relacionadas à entidade {@link Consulta}.
 * Inclui métodos de CRUD e o gerenciamento da relação N:N entre {@link Consulta} e {@link Profissional}.
 */
@ApplicationScoped
public class ConsultaDao {

    @Inject
    DataSource dataSource;

    /**
     * Cadastra uma nova consulta no banco de dados.
     * Também realiza o vínculo com os profissionais informados.
     */
    public void cadastrarConsulta(Consulta consulta) {
        int proximoId = gerarProximoIdConsulta();

        String sql = """
            INSERT INTO CONSULTA (id_consulta, tipo_consulta, data_consulta, motivo_consulta)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"id_consulta"})) {

            ps.setInt(1, proximoId);
            ps.setString(2, consulta.getTipoConsulta());
            ps.setDate(3, Date.valueOf(consulta.getDataConsulta()));
            ps.setString(4, consulta.getMotivoConsulta());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    consulta.setIdConsulta(rs.getInt(1));
                }
            }

            // Vincula profissionais à consulta
            if (consulta.getProfissionais() != null && !consulta.getProfissionais().isEmpty()) {
                for (Profissional p : consulta.getProfissionais()) {
                    vincularProfissional(consulta.getIdConsulta(), p.getId());
                }
            }

            System.out.println("Consulta cadastrada com sucesso! ID: " + consulta.getIdConsulta());

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar consulta: " + e.getMessage());
            throw new RuntimeException("Erro ao cadastrar consulta", e);
        }
    }

    /**
     * Gera o próximo ID disponível para uma nova consulta.
     */
    private int gerarProximoIdConsulta() {
        String sql = "SELECT NVL(MAX(id_consulta), 0) + 1 AS proximo_id FROM CONSULTA";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("proximo_id");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao gerar próximo ID da consulta: " + e.getMessage());
        }

        return 1;
    }

    /**
     * Retorna uma lista de todas as consultas cadastradas no banco de dados,
     * incluindo os profissionais vinculados a cada uma.
     */
    public List<Consulta> listarConsultas() {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM CONSULTA ORDER BY data_consulta DESC";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Consulta c = new Consulta();
                c.setIdConsulta(rs.getInt("id_consulta"));
                c.setTipoConsulta(rs.getString("tipo_consulta"));
                c.setDataConsulta(rs.getDate("data_consulta").toLocalDate());
                c.setMotivoConsulta(rs.getString("motivo_consulta"));
                c.setProfissionais(buscarProfissionaisDaConsulta(c.getIdConsulta()));
                lista.add(c);
            }

            System.out.println(lista.size() + " consultas listadas.");

        } catch (SQLException e) {
            System.err.println("Erro ao listar consultas: " + e.getMessage());
            throw new RuntimeException("Erro ao listar consultas", e);
        }

        return lista;
    }

    /**
     * Busca uma consulta pelo seu identificador.
     */
    public Consulta buscarPorId(int id) {
        Consulta consulta = null;
        String sql = "SELECT * FROM CONSULTA WHERE id_consulta = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    consulta = new Consulta();
                    consulta.setIdConsulta(rs.getInt("id_consulta"));
                    consulta.setTipoConsulta(rs.getString("tipo_consulta"));
                    consulta.setDataConsulta(rs.getDate("data_consulta").toLocalDate());
                    consulta.setMotivoConsulta(rs.getString("motivo_consulta"));
                    consulta.setProfissionais(buscarProfissionaisDaConsulta(id));

                    System.out.println("Consulta encontrada: " + consulta.getTipoConsulta());
                } else {
                    System.out.println("Consulta não encontrada com ID: " + id);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar consulta por ID: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar consulta por ID: " + id, e);
        }

        return consulta;
    }

    /**
     * Atualiza os dados de uma consulta existente.
     * Também atualiza os vínculos de profissionais associados.
     */
    public Consulta atualizarConsulta(Consulta consulta) {
        if (consulta.getIdConsulta() == null || consulta.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("ID inválido para atualização.");
        }

        String sql = """
            UPDATE CONSULTA 
            SET tipo_consulta = ?, data_consulta = ?, motivo_consulta = ?
            WHERE id_consulta = ?
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, consulta.getTipoConsulta());
            ps.setDate(2, Date.valueOf(consulta.getDataConsulta()));
            ps.setString(3, consulta.getMotivoConsulta());
            ps.setInt(4, consulta.getIdConsulta());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Consulta atualizada com sucesso! ID: " + consulta.getIdConsulta());

                // Atualiza vínculos
                desvincularTodosProfissionais(consulta.getIdConsulta());
                if (consulta.getProfissionais() != null && !consulta.getProfissionais().isEmpty()) {
                    for (Profissional p : consulta.getProfissionais()) {
                        vincularProfissional(consulta.getIdConsulta(), p.getId());
                    }
                }

            } else {
                System.out.println("Nenhuma consulta encontrada para atualização. ID: " + consulta.getIdConsulta());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar consulta", e);
        }
        return consulta;
    }

    /**
     * Exclui uma consulta e remove seus vínculos com profissionais.
     */
    public void excluirConsulta(int id) {
        String sql = "DELETE FROM CONSULTA WHERE id_consulta = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            desvincularTodosProfissionais(id);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Consulta excluída com sucesso. ID: " + id);
            } else {
                System.out.println("Nenhuma consulta encontrada para exclusão. ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir consulta: " + e.getMessage());
            throw new RuntimeException("Erro ao excluir consulta", e);
        }
    }


    /**
     * Cria um vínculo entre um profissional e uma consulta.
     */
    private void vincularProfissional(int idConsulta, int idProfissional) {
        String sql = "INSERT INTO CONSULTA_PROFIS (fk_consulta, fk_profis) VALUES (?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ps.setInt(2, idProfissional);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao vincular profissional à consulta: " + e.getMessage());
        }
    }

    /**
     * Remove todos os vínculos entre profissionais e uma consulta.
     */
    private void desvincularTodosProfissionais(int idConsulta) {
        String sql = "DELETE FROM CONSULTA_PROFIS WHERE fk_consulta = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao desvincular profissionais da consulta: " + e.getMessage());
        }
    }

    /**
     * Busca todos os profissionais associados a uma consulta específica.
     */
    private List<Profissional> buscarProfissionaisDaConsulta(int idConsulta) {
        List<Profissional> lista = new ArrayList<>();

        String sql = """
            SELECT p.id_profissional, p.nome_profissional, p.especialidade_profissional, 
                   p.tipo_atend, p.crm_profissional
            FROM PROFISSIONAL p
            JOIN CONSULTA_PROFIS cp ON p.id_profissional = cp.fk_profis
            WHERE cp.fk_consulta = ?
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Profissional p = new Profissional();
                    p.setId(rs.getInt("id_profissional"));
                    p.setNome(rs.getString("nome_profissional"));
                    p.setEspecialidade(rs.getString("especialidade_profissional"));
                    p.setTipoAtendimento(rs.getString("tipo_atend"));
                    p.setCrm(rs.getInt("crm_profissional"));
                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar profissionais da consulta: " + e.getMessage());
        }

        return lista;
    }
}
