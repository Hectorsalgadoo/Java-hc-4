package br.com.fiap.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma Consulta médica ou atendimento de um paciente.
 * Uma consulta possui um identificador, tipo, data, motivo e uma lista de profissionais envolvidos.
 */
public class Consulta {

    /** Identificador único da consulta. */
    private Integer idConsulta;

    /** Tipo da consulta (ex.: "Consulta Geral", "Retorno", "Exame"). */
    private String tipoConsulta;

    /** Data em que a consulta ocorreu ou ocorrerá. */
    private LocalDate dataConsulta;

    /** Motivo ou descrição do motivo da consulta. */
    private String motivoConsulta;

    /** Lista de profissionais associados à consulta. */
    private List<Profissional> profissionais = new ArrayList<>();

    /**
     * Construtor padrão.
     */
    public Consulta() {
    }

    /**
     * Construtor com todos os atributos principais.
     */
    public Consulta(Integer idConsulta, String tipoConsulta, LocalDate dataConsulta, String motivoConsulta) {
        this.idConsulta = idConsulta;
        this.tipoConsulta = tipoConsulta;
        this.dataConsulta = dataConsulta;
        this.motivoConsulta = motivoConsulta;
    }

    /**
     * Retorna o identificador da consulta.
     *
     * @return idConsulta
     */
    public Integer getIdConsulta() {
        return idConsulta;
    }

    /**
     * Define o identificador da consulta.
     *
     * @param idConsulta Identificador da consulta.
     */
    public void setIdConsulta(Integer idConsulta) {
        this.idConsulta = idConsulta;
    }

    /**
     * Retorna o tipo da consulta.
     *
     * @return tipoConsulta
     */
    public String getTipoConsulta() {
        return tipoConsulta;
    }

    /**
     * Define o tipo da consulta.
     */
    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    /**
     * Retorna a data da consulta.
     */
    public LocalDate getDataConsulta() {
        return dataConsulta;
    }

    /**
     * Define a data da consulta.
     */
    public void setDataConsulta(LocalDate dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    /**
     * Retorna o motivo da consulta.
     */
    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    /**
     * Define o motivo da consulta.
     */
    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    /**
     * Retorna a lista de profissionais associados à consulta.
     */
    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    /**
     * Define a lista de profissionais associados à consulta.
     */
    public void setProfissionais(List<Profissional> profissionais) {
        this.profissionais = profissionais;
    }

    /**
     * Adiciona um profissional à lista de profissionais da consulta,
     * garantindo que não haja duplicatas.
     */
    public void adicionarProfissional(Profissional profissional) {
        if (profissional != null && !profissionais.contains(profissional)) {
            profissionais.add(profissional);
        }
    }

    /**
     * Remove um profissional da lista de profissionais da consulta.
     */
    public void removerProfissional(Profissional profissional) {
        profissionais.remove(profissional);
    }

    /**
     * Retorna uma representação em string da consulta,
     * incluindo seus atributos e profissionais associados.
     */
    @Override
    public String toString() {
        return "Consulta{" +
                "idConsulta=" + idConsulta +
                ", tipoConsulta='" + tipoConsulta + '\'' +
                ", dataConsulta=" + dataConsulta +
                ", motivoConsulta='" + motivoConsulta + '\'' +
                ", profissionais=" + profissionais +
                '}';
    }
}
