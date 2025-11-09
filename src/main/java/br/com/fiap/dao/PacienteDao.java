package br.com.fiap.dao;

import br.com.fiap.models.Paciente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por realizar operações de persistência relacionadas à entidade {@link Paciente}.
 * Inclui métodos de CRUD e consultas por CPF.
 */
@ApplicationScoped
public class PacienteDao {

    @Inject
    DataSource dataSource;

    /**
     * Cadastra um novo paciente no banco de dados.
     * O ID é gerado automaticamente com base no maior valor existente na tabela.
     */
    public void cadastrarPaciente(Paciente paciente) {
        String sqlGetMax = "SELECT NVL(MAX(id_pac), 0) + 1 AS next_id FROM PACIENTE";
        String sqlInsert = """
            INSERT INTO PACIENTE 
            (id_pac, nome_pac, idade_pac, nivel_tec, tipo_atendimento, cpf_pac, senha_pac)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement psGetId = conexao.prepareStatement(sqlGetMax);
             ResultSet rs = psGetId.executeQuery()) {

            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt("next_id");
            }

            try (PreparedStatement ps = conexao.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setString(2, paciente.getNome());
                ps.setInt(3, paciente.getIdade());
                ps.setInt(4, paciente.getNivelTecnico());
                ps.setString(5, paciente.getTipoAtendimento());
                ps.setString(6, paciente.getCpf());
                ps.setString(7, paciente.getSenha());

                ps.executeUpdate();
                paciente.setId(nextId);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar paciente: " + e.getMessage());
            throw new RuntimeException("Erro ao cadastrar paciente", e);
        }
    }

    /**
     * Retorna uma lista de todos os pacientes cadastrados no banco de dados.
     */
    public List<Paciente> listarPacientes() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM PACIENTE ORDER BY nome_pac";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id_pac"));
                p.setNome(rs.getString("nome_pac"));
                p.setIdade(rs.getInt("idade_pac"));
                p.setNivelTecnico(rs.getInt("nivel_tec"));
                p.setTipoAtendimento(rs.getString("tipo_atendimento"));
                p.setCpf(rs.getString("cpf_pac"));
                p.setSenha(rs.getString("senha_pac"));
                lista.add(p);
            }

            System.out.println(lista.size() + " pacientes listados.");

        } catch (SQLException e) {
            System.err.println("Erro ao listar pacientes: " + e.getMessage());
            throw new RuntimeException("Erro ao listar pacientes", e);
        }

        return lista;
    }

    /**
     * Busca um paciente pelo seu identificador único (ID).
     */
    public Paciente buscarPorId(int id) {
        Paciente paciente = null;
        String sql = "SELECT * FROM PACIENTE WHERE id_pac = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setId(rs.getInt("id_pac"));
                    paciente.setNome(rs.getString("nome_pac"));
                    paciente.setIdade(rs.getInt("idade_pac"));
                    paciente.setNivelTecnico(rs.getInt("nivel_tec"));
                    paciente.setTipoAtendimento(rs.getString("tipo_atendimento"));
                    paciente.setCpf(rs.getString("cpf_pac"));
                    paciente.setSenha(rs.getString("senha_pac"));

                    System.out.println("Paciente encontrado: " + paciente.getNome());
                } else {
                    System.out.println("Paciente não encontrado com ID: " + id);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar paciente por ID: " + id, e);
        }

        return paciente;
    }

    /**
     * Atualiza os dados de um paciente existente no banco.
     */
    public Paciente atualizarPaciente(Paciente paciente) {
        if (paciente.getId() == null || paciente.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido para atualização.");
        }

        String sql = """
            UPDATE PACIENTE 
            SET nome_pac = ?, idade_pac = ?, nivel_tec = ?, tipo_atendimento = ?, cpf_pac = ?, senha_pac = ?
            WHERE id_pac = ?
        """;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, paciente.getNome());
            ps.setInt(2, paciente.getIdade());
            ps.setInt(3, paciente.getNivelTecnico());
            ps.setString(4, paciente.getTipoAtendimento());
            ps.setString(5, paciente.getCpf());
            ps.setString(6, paciente.getSenha());
            ps.setInt(7, paciente.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Paciente atualizado com sucesso! ID: " + paciente.getId());
            } else {
                System.out.println("Nenhum paciente encontrado para atualização. ID: " + paciente.getId());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar paciente", e);
        }
        return paciente;
    }

    /**
     * Exclui um paciente do banco de dados pelo seu ID.
     */
    public void excluirPaciente(int id) {
        String sql = "DELETE FROM PACIENTE WHERE id_pac = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Paciente excluído com sucesso. ID: " + id);
            } else {
                System.out.println("Nenhum paciente encontrado para exclusão. ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir paciente: " + e.getMessage());
            throw new RuntimeException("Erro ao excluir paciente", e);
        }
    }

    /**
     * Busca um paciente pelo CPF.
     */
    public Paciente buscarPorCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido. Deve conter exatamente 11 números.");
        }

        Paciente paciente = null;
        String sql = "SELECT * FROM PACIENTE WHERE cpf_pac = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, cpf);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setId(rs.getInt("id_pac"));
                    paciente.setNome(rs.getString("nome_pac"));
                    paciente.setIdade(rs.getInt("idade_pac"));
                    paciente.setNivelTecnico(rs.getInt("nivel_tec"));
                    paciente.setTipoAtendimento(rs.getString("tipo_atendimento"));
                    paciente.setCpf(rs.getString("cpf_pac"));
                    paciente.setSenha(rs.getString("senha_pac"));

                    System.out.println("Paciente encontrado com CPF: " + cpf);
                } else {
                    System.out.println("Nenhum paciente encontrado com CPF: " + cpf);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar paciente: " + cpf, e);
        }

        return paciente;
    }
}
