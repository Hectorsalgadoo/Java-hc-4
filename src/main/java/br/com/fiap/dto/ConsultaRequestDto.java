package br.com.fiap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) para requisições relacionadas à entidade {@link br.com.fiap.models.Consulta}.
 *
 * <p>Usado para receber dados de entrada (request) via API, incluindo a serialização/deserialização JSON.</p>
 *
 * <p>Possui campos para id, tipo de consulta, data da consulta (como string) e motivo da consulta.
 * Também inclui um campo {@code dataConsultaLocal} do tipo {@link LocalDate} para uso interno na aplicação.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaRequestDto {

    @JsonProperty("id_consulta")
    private Integer id;

    @JsonProperty("tipo_consulta")
    private String tipoConsulta;

    @JsonProperty("data_consulta")
    private String dataConsulta;

    @JsonProperty("motivo_consulta")
    private String motivoConsulta;

    /**
     * Representação da data da consulta como {@link LocalDate}.
     * Usado internamente para conversões e validações.
     */
    private LocalDate dataConsultaLocal;

    /**
     * Construtor padrão.
     */
    public ConsultaRequestDto() {}

    /**
     * Construtor que inicializa os campos essenciais da consulta.
     *
     * @param tipoConsulta Tipo da consulta.
     * @param dataConsulta Data da consulta no formato string.
     * @param motivoConsulta Motivo da consulta.
     */
    public ConsultaRequestDto(String tipoConsulta, String dataConsulta, String motivoConsulta) {
        this.tipoConsulta = tipoConsulta;
        this.dataConsulta = dataConsulta;
        this.motivoConsulta = motivoConsulta;
    }

    /**
     * Retorna a data da consulta como {@link LocalDate}.
     *
     * @return {@link LocalDate} da consulta.
     */
    public LocalDate getDataConsultaLocal() {
        return dataConsultaLocal;
    }

    /**
     * Define a data da consulta como {@link LocalDate}.
     *
     * @param dataConsultaLocal Data da consulta.
     */
    public void setDataConsultaLocal(LocalDate dataConsultaLocal) {
        this.dataConsultaLocal = dataConsultaLocal;
    }

    /**
     * Verifica se o DTO possui um ID definido.
     *
     * @return {@code true} se o ID não for nulo, {@code false} caso contrário.
     */
    public boolean possuiId() {
        return id != null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    /**
     * Limpa os campos de espaços em branco no início e fim da string.
     * Deve ser chamado antes de validações ou persistência.
     */
    public void cleanData() {
        if (this.tipoConsulta != null) {
            this.tipoConsulta = this.tipoConsulta.trim();
        }
        if (this.motivoConsulta != null) {
            this.motivoConsulta = this.motivoConsulta.trim();
        }
        if (this.dataConsulta != null) {
            this.dataConsulta = this.dataConsulta.trim();
        }
    }

    /**
     * Verifica se todos os campos obrigatórios possuem valores válidos.
     *
     * @return {@code true} se todos os campos essenciais estiverem preenchidos, {@code false} caso contrário.
     */
    public boolean isValid() {
        return tipoConsulta != null && !tipoConsulta.trim().isEmpty()
                && dataConsulta != null && !dataConsulta.trim().isEmpty()
                && motivoConsulta != null && !motivoConsulta.trim().isEmpty();
    }
}
