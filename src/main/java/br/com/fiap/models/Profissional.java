package br.com.fiap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um profissional de saúde no sistema.
 * Um profissional possui identificador, nome, especialidade,
 * tipo de atendimento e número de CRM.
 */
@XmlRootElement
public class Profissional {

    /** Identificador único do profissional. */
    @JsonProperty("id_profissional")
    private Integer id;

    /** Nome completo do profissional. */
    @JsonProperty("nome_profissional")
    @NotNull(message = "Nome do profissional é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String nome;

    /** Especialidade médica do profissional (ex: pediatria, ortopedia). */
    @JsonProperty("especialidade_profissional")
    @NotNull(message = "Especialidade é obrigatória")
    @Size(min = 2, max = 30, message = "A especialidade deve ter entre 2 e 30 caracteres")
    private String especialidade;

    /** Tipo de atendimento realizado pelo profissional (ex: presencial, online). */
    @JsonProperty("tipo_atendimento")
    @NotNull(message = "Tipo de atendimento é obrigatório")
    @Size(min = 2, max = 20, message = "O tipo de atendimento deve ter entre 2 e 20 caracteres")
    private String tipoAtendimento;

    /** Número de registro profissional (CRM). */
    @JsonProperty("crm_profissional")
    @NotNull(message = "CRM é obrigatório")
    @Min(value = 1, message = "O CRM deve ser maior que zero")
    @Digits(integer = 6, fraction = 0, message = "O CRM deve conter até 6 dígitos")
    private Integer crm;

    /**
     * Construtor padrão.
     */
    public Profissional() {}

    /**
     * Construtor completo com todos os atributos.
     */
    public Profissional(Integer id, String nome, String especialidade, String tipoAtendimento, Integer crm) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.tipoAtendimento = tipoAtendimento;
        this.crm = crm;
    }


    /** @return o ID do profissional */
    public Integer getId() { return id; }

    /** @param id define o ID do profissional */
    public void setId(Integer id) { this.id = id; }

    /** @return o nome do profissional */
    public String getNome() { return nome; }

    /** @param nome define o nome do profissional */
    public void setNome(String nome) { this.nome = nome; }

    /** @return a especialidade do profissional */
    public String getEspecialidade() { return especialidade; }

    /** @param especialidade define a especialidade do profissional */
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    /** @return o tipo de atendimento do profissional */
    public String getTipoAtendimento() { return tipoAtendimento; }

    /** @param tipoAtendimento define o tipo de atendimento do profissional */
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    /** @return o CRM do profissional */
    public Integer getCrm() { return crm; }

    /** @param crm define o CRM do profissional */
    public void setCrm(Integer crm) { this.crm = crm; }


    /**
     * Verifica se o CRM é válido (maior que zero e com até 6 dígitos).
     */
    public boolean isCrmValido() {
        return crm != null && crm > 0 && String.valueOf(crm).length() <= 6;
    }

    /**
     * Limpa espaços extras nos campos de texto (nome, especialidade e tipo de atendimento).
     */
    public void limparDados() {
        if (nome != null) nome = nome.trim();
        if (especialidade != null) especialidade = especialidade.trim();
        if (tipoAtendimento != null) tipoAtendimento = tipoAtendimento.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Profissional)) return false;
        Profissional other = (Profissional) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Profissional{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", tipoAtendimento='" + tipoAtendimento + '\'' +
                ", crm=" + crm +
                '}';
    }
}
