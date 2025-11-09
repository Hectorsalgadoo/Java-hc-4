package br.com.fiap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para requisições relacionadas a Profissional.
 * <p>
 * Esta classe é usada para receber informações de profissionais
 * da aplicação a partir do cliente (frontend ou API).
 * </p>
 * <p>
 * Possui métodos auxiliares para validação e limpeza de dados.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfissionalRequestDto {

    /** Identificador do profissional */
    @JsonProperty("id_profissional")
    private Integer id;

    /** Nome do profissional */
    @JsonProperty("nome_profissional")
    @NotNull(message = "Nome é obrigatório")
    @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres")
    private String nome;

    /** Especialidade do profissional */
    @JsonProperty("especialidade_profissional")
    @NotNull(message = "Especialidade é obrigatória")
    @Size(min = 2, max = 50, message = "A especialidade deve ter entre 2 e 50 caracteres")
    private String especialidade;

    /** Tipo de atendimento prestado pelo profissional */
    @JsonProperty("tipo_atend")
    @NotNull(message = "Tipo de atendimento é obrigatório")
    @Size(min = 2, max = 30, message = "O tipo de atendimento deve ter entre 2 e 30 caracteres")
    private String tipoAtendimento;

    /** CRM do profissional */
    @JsonProperty("crm_profissional")
    @NotNull(message = "CRM é obrigatório")
    @Digits(integer = 5, fraction = 0, message = "O CRM deve ter no máximo 5 dígitos")
    private Integer crm;

    /** Construtor padrão */
    public ProfissionalRequestDto() {}

    /**
     * Construtor com todos os campos obrigatórios
     *
     * @param nome Nome do profissional
     * @param especialidade Especialidade do profissional
     * @param tipoAtendimento Tipo de atendimento
     * @param crm CRM do profissional
     */
    public ProfissionalRequestDto(String nome, String especialidade, String tipoAtendimento, Integer crm) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.tipoAtendimento = tipoAtendimento;
        this.crm = crm;
    }

    /**
     * Verifica se o DTO possui um ID definido
     * @return true se o ID não for nulo
     */
    public boolean possuiId() {
        return id != null;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public Integer getCrm() { return crm; }
    public void setCrm(Integer crm) { this.crm = crm; }

    /**
     * Limpa espaços desnecessários dos campos de texto
     */
    public void cleanData() {
        if (this.nome != null) this.nome = this.nome.trim();
        if (this.especialidade != null) this.especialidade = this.especialidade.trim();
        if (this.tipoAtendimento != null) this.tipoAtendimento = this.tipoAtendimento.trim();
    }

    /**
     * Valida se todos os campos obrigatórios estão preenchidos
     * @return true se todos os campos obrigatórios forem válidos
     */
    public boolean isValid() {
        return nome != null && !nome.trim().isEmpty()
                && especialidade != null && !especialidade.trim().isEmpty()
                && tipoAtendimento != null && !tipoAtendimento.trim().isEmpty()
                && crm != null;
    }
}
