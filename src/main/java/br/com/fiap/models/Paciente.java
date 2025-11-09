package br.com.fiap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um paciente no sistema.
 * Um paciente possui identificador, nome, idade, nível técnico, tipo de atendimento,
 * CPF e senha.
 */
@XmlRootElement
public class Paciente {

    /** Identificador único do paciente. */
    @JsonProperty("id_paciente")
    private Integer id;

    /** Nome completo do paciente. */
    @JsonProperty("nome_paciente")
    @NotNull(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String nome;

    /** Idade do paciente. */
    @JsonProperty("idade_paciente")
    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "A idade mínima é 0")
    @Max(value = 120, message = "A idade máxima é 120")
    private Integer idade;

    /** Nível técnico do paciente (0 a 10). */
    @JsonProperty("nivel_tecnico")
    @NotNull(message = "Nível técnico é obrigatório")
    @Min(value = 0, message = "O nível técnico deve ser no mínimo 0")
    @Max(value = 10, message = "O nível técnico deve ser no máximo 10")
    private Integer nivelTecnico;

    /** Tipo de atendimento do paciente. */
    @JsonProperty("tipo_atendimento")
    @NotNull(message = "Tipo de atendimento é obrigatório")
    @Size(min = 2, max = 30, message = "O tipo de atendimento deve ter entre 2 e 30 caracteres")
    private String tipoAtendimento;

    /** CPF do paciente (11 números). */
    @JsonProperty("cpf_paciente")
    @NotNull(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 números")
    private String cpf;

    /** Senha do paciente (6 a 8 caracteres). */
    @JsonProperty("senha_paciente")
    @NotNull(message = "Senha é obrigatória")
    @Size(min = 6, max = 8, message = "A senha deve ter entre 6 e 8 caracteres")
    private String senha;

    /**
     * Construtor padrão.
     */
    public Paciente() {}

    /**
     * Construtor completo com todos os atributos.
     */
    public Paciente(Integer id, String nome, Integer idade, Integer nivelTecnico,
                    String tipoAtendimento, String cpf, String senha) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.nivelTecnico = nivelTecnico;
        this.tipoAtendimento = tipoAtendimento;
        this.cpf = cpf;
        this.senha = senha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public Integer getNivelTecnico() { return nivelTecnico; }
    public void setNivelTecnico(Integer nivelTecnico) { this.nivelTecnico = nivelTecnico; }

    public String getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    /**
     * Verifica se o CPF do paciente é válido (contém 11 números).
     *
     * @return true se o CPF for válido, false caso contrário
     */
    public boolean isCpfValido() {
        return cpf != null && cpf.matches("\\d{11}");
    }

    /**
     * Limpa espaços em branco do nome e tipo de atendimento,
     * e remove caracteres não numéricos do CPF.
     */
    public void limparDados() {
        if (nome != null) nome = nome.trim();
        if (tipoAtendimento != null) tipoAtendimento = tipoAtendimento.trim();
        if (cpf != null) cpf = cpf.replaceAll("[^\\d]", "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Paciente)) return false;
        Paciente other = (Paciente) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", nivelTecnico=" + nivelTecnico +
                ", tipoAtendimento='" + tipoAtendimento + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
