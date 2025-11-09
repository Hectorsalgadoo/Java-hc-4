package br.com.fiap.dto;

import br.com.fiap.models.Profissional;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) para respostas de Profissional.
 */
public class ProfissionalResponseDto {

    @JsonProperty("id_profissional")
    private int idProfissional;

    @JsonProperty("nome_profissional")
    private String nomeProfissional;

    @JsonProperty("especialidade_profissional")
    private String especialidadeProfissional;

    @JsonProperty("tipo_atend")
    private String tipoAtend;

    @JsonProperty("crm_profissional")
    private int crmProfissional;

    /** Construtor padrão */
    public ProfissionalResponseDto() {}

    /** Construtor com todos os campos */
    public ProfissionalResponseDto(int idProfissional, String nomeProfissional, String especialidadeProfissional,
                                   String tipoAtend, int crmProfissional) {
        this.idProfissional = idProfissional;
        this.nomeProfissional = nomeProfissional;
        this.especialidadeProfissional = especialidadeProfissional;
        this.tipoAtend = tipoAtend;
        this.crmProfissional = crmProfissional;
    }

    /** Converte um objeto Profissional em DTO */
    public static ProfissionalResponseDto convertToDto(Profissional profissional) {
        if (profissional == null) return null;
        return new ProfissionalResponseDto(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEspecialidade(),
                profissional.getTipoAtendimento(),
                profissional.getCrm()
        );
    }

    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }

    public String getNomeProfissional() { return nomeProfissional; }
    public void setNomeProfissional(String nomeProfissional) { this.nomeProfissional = nomeProfissional; }

    public String getEspecialidadeProfissional() { return especialidadeProfissional; }
    public void setEspecialidadeProfissional(String especialidadeProfissional) { this.especialidadeProfissional = especialidadeProfissional; }

    public String getTipoAtend() { return tipoAtend; }
    public void setTipoAtend(String tipoAtend) { this.tipoAtend = tipoAtend; }

    public int getCrmProfissional() { return crmProfissional; }
    public void setCrmProfissional(int crmProfissional) { this.crmProfissional = crmProfissional; }

    @Override
    public String toString() {
        return "ProfissionalResponseDto{" +
                "idProfissional=" + idProfissional +
                ", nomeProfissional='" + nomeProfissional + '\'' +
                ", especialidadeProfissional='" + especialidadeProfissional + '\'' +
                ", tipoAtend='" + tipoAtend + '\'' +
                ", crmProfissional=" + crmProfissional +
                '}';
    }
}
