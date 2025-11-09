package br.com.fiap.dto;

import br.com.fiap.models.Paciente;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Data Transfer Object (DTO) para requisições relacionadas a Paciente.
 * <p>
 * Esta classe é usada para receber e validar dados de entrada para criação
 * ou atualização de pacientes na aplicação.
 * </p>
 * <p>
 * Possui validações usando {@link jakarta.validation.constraints} para garantir
 * integridade dos dados, além de métodos auxiliares para limpar e validar campos.
 * </p>
 *
 * @author Hector
 */
@XmlRootElement
public class PacienteRequestDto {

    /** Identificador do paciente */
    @JsonProperty("id_paciente")
    private Integer id;

    /** Nome completo do paciente */
    @JsonProperty("nome_paciente")
    @NotNull(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String nome;

    /** Idade do paciente */
    @JsonProperty("idade_paciente")
    @NotNull(message = "Idade é obrigatória")
    @Min(0)
    @Max(120)
    private Integer idade;

    /** Nível técnico do paciente */
    @JsonProperty("nivel_tecnico")
    @NotNull(message = "Nível técnico é obrigatório")
    @Min(0)
    @Max(10)
    private Integer nivelTecnico;

    /** Tipo de atendimento do paciente */
    @JsonProperty("tipo_atendimento")
    @NotNull(message = "Tipo de atendimento é obrigatório")
    @Size(min = 2, max = 30)
    private String tipoAtendimento;

    /** CPF do paciente (apenas números, 11 dígitos) */
    @JsonProperty("cpf_paciente")
    @NotNull(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 números")
    private String cpf;

    /** Senha do paciente (entre 6 e 8 caracteres) */
    @JsonProperty("senha_paciente")
    @NotNull(message = "Senha é obrigatória")
    @Size(min = 6, max = 8, message = "A senha deve ter entre 6 e 8 caracteres")
    private String senha;

    /** Retorna o ID do paciente */
    public Integer getId() { return id; }

    /** Define o ID do paciente */
    public void setId(Integer id) { this.id = id; }

    /** Retorna o nome do paciente */
    public String getNome() { return nome; }

    /** Define o nome do paciente */
    public void setNome(String nome) { this.nome = nome; }

    /** Retorna a idade do paciente */
    public Integer getIdade() { return idade; }

    /** Define a idade do paciente */
    public void setIdade(Integer idade) { this.idade = idade; }

    /** Retorna o nível técnico do paciente */
    public Integer getNivelTecnico() { return nivelTecnico; }

    /** Define o nível técnico do paciente */
    public void setNivelTecnico(Integer nivelTecnico) { this.nivelTecnico = nivelTecnico; }

    /** Retorna o tipo de atendimento do paciente */
    public String getTipoAtendimento() { return tipoAtendimento; }

    /** Define o tipo de atendimento do paciente */
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    /** Retorna o CPF do paciente */
    public String getCpf() { return cpf; }

    /** Define o CPF do paciente */
    public void setCpf(String cpf) { this.cpf = cpf; }

    /** Retorna a senha do paciente */
    public String getSenha() { return senha; }

    /** Define a senha do paciente */
    public void setSenha(String senha) { this.senha = senha; }

    /**
     * Valida os campos obrigatórios do DTO.
     *
     * @return true se todos os campos estiverem válidos, false caso contrário
     */
    public boolean isValid() {
        if (nome == null || nome.trim().length() < 2) return false;
        if (cpf == null || !cpf.matches("\\d{11}")) return false;
        if (idade == null || idade < 0 || idade > 120) return false;
        if (nivelTecnico == null || nivelTecnico < 0 || nivelTecnico > 10) return false;
        if (tipoAtendimento == null || tipoAtendimento.trim().isEmpty()) return false;
        if (senha == null || senha.length() < 6 || senha.length() > 8) return false;
        return true;
    }

    /**
     * Limpa os dados de entrada do DTO, removendo espaços em branco
     * desnecessários e caracteres inválidos.
     */
    public void cleanData() {
        if (nome != null) nome = nome.trim();
        if (cpf != null) cpf = cpf.replaceAll("\\D", "");
        if (tipoAtendimento != null) tipoAtendimento = tipoAtendimento.trim();
    }
}
