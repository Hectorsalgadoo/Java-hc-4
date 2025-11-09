package br.com.fiap.dto;

import br.com.fiap.models.Consulta;

/**
 * Data Transfer Object (DTO) para respostas relacionadas à entidade {@link Consulta}.
 *
 * <p>Usado para enviar dados de consulta via API, como JSON de resposta. Contém os campos essenciais
 * da consulta, incluindo ID, tipo, data e motivo.</p>
 */
public class ConsultaResponseDto {

    /**
     * Identificador da consulta.
     */
    private int id_consulta;

    /**
     * Tipo da consulta.
     */
    private String tipo_consulta;

    /**
     * Data da consulta em formato String.
     */
    private String data_consulta;

    /**
     * Motivo da consulta.
     */
    private String motivo_consulta;

    /**
     * Construtor padrão.
     */
    public ConsultaResponseDto() {
    }

    /**
     * Construtor que inicializa todos os campos do DTO.
     *
     * @param id_consulta ID da consulta.
     * @param tipo_consulta Tipo da consulta.
     * @param data_consulta Data da consulta.
     * @param motivo_consulta Motivo da consulta.
     */
    public ConsultaResponseDto(int id_consulta, String tipo_consulta, String data_consulta, String motivo_consulta) {
        this.id_consulta = id_consulta;
        this.tipo_consulta = tipo_consulta;
        this.data_consulta = data_consulta;
        this.motivo_consulta = motivo_consulta;
    }

    /**
     * Converte uma entidade {@link Consulta} em um {@link ConsultaResponseDto}.
     *
     * @param consulta Objeto {@link Consulta} a ser convertido.
     * @return Um {@link ConsultaResponseDto} preenchido com os dados da consulta.
     */
    public static ConsultaResponseDto convertToDto(Consulta consulta) {
        ConsultaResponseDto dto = new ConsultaResponseDto();
        dto.setId_consulta(consulta.getIdConsulta());
        dto.setTipo_consulta(consulta.getTipoConsulta());
        dto.setData_consulta(
                consulta.getDataConsulta() != null ? consulta.getDataConsulta().toString() : null
        );
        dto.setMotivo_consulta(consulta.getMotivoConsulta());
        return dto;
    }

    public int getId_consulta() {
        return id_consulta;
    }

    public void setId_consulta(int id_consulta) {
        this.id_consulta = id_consulta;
    }

    public String getTipo_consulta() {
        return tipo_consulta;
    }

    public void setTipo_consulta(String tipo_consulta) {
        this.tipo_consulta = tipo_consulta;
    }

    public String getData_consulta() {
        return data_consulta;
    }

    public void setData_consulta(String data_consulta) {
        this.data_consulta = data_consulta;
    }

    public String getMotivo_consulta() {
        return motivo_consulta;
    }

    public void setMotivo_consulta(String motivo_consulta) {
        this.motivo_consulta = motivo_consulta;
    }

    /**
     * Retorna o ID da consulta.
     *
     * @return ID da consulta.
     */
    public int getId() {
        return id_consulta;
    }

    @Override
    public String toString() {
        return "ConsultaResponseDto{" +
                "id_consulta=" + id_consulta +
                ", tipo_consulta='" + tipo_consulta + '\'' +
                ", data_consulta='" + data_consulta + '\'' +
                ", motivo_consulta='" + motivo_consulta + '\'' +
                '}';
    }
}
