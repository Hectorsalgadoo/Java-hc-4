package br.com.fiap.dto;

import br.com.fiap.models.Paciente;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Data Transfer Object (DTO) para respostas relacionadas a Paciente.
 * <p>
 * Esta classe é usada para enviar informações de pacientes
 * da aplicação para o cliente (frontend ou API).
 * </p>
 * <p>
 * Possui métodos auxiliares para conversão a partir de entidades do modelo.
 * </p>
 *
 */
@XmlRootElement
public class PacienteResponseDto {

    /** Identificador do paciente */
    @JsonProperty("id_paciente")
    private Integer id;

    /** Nome completo do paciente */
    @JsonProperty("nome_paciente")
    private String nome;

    /** Idade do paciente */
    @JsonProperty("idade_paciente")
    private Integer idade;

    /** Nível técnico do paciente */
    @JsonProperty("nivel_tecnico")
    private Integer nivelTecnico;

    /** Tipo de atendimento do paciente */
    @JsonProperty("tipo_atendimento")
    private String tipoAtendimento;

    /** CPF do paciente */
    @JsonProperty("cpf_paciente")
    private String cpf;

    /** Construtor padrão */
    public PacienteResponseDto() {}

    /**
     * Construtor com todos os campos
     *
     * @param id Identificador do paciente
     * @param nome Nome do paciente
     * @param idade Idade do paciente
     * @param nivelTecnico Nível técnico do paciente
     * @param tipoAtendimento Tipo de atendimento
     * @param cpf CPF do paciente
     */
    public PacienteResponseDto(Integer id, String nome, Integer idade,
                               Integer nivelTecnico, String tipoAtendimento, String cpf) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.nivelTecnico = nivelTecnico;
        this.tipoAtendimento = tipoAtendimento;
        this.cpf = cpf;
    }

    /**
     * Converte uma entidade Paciente em DTO de resposta
     *
     * @param paciente Entidade Paciente
     * @return Instância de PacienteResponseDto ou null se paciente for null
     */
    public static PacienteResponseDto convertToDto(Paciente paciente) {
        if (paciente == null) return null;
        return new PacienteResponseDto(
                paciente.getId(),
                paciente.getNome(),
                paciente.getIdade(),
                paciente.getNivelTecnico(),
                paciente.getTipoAtendimento(),
                paciente.getCpf()
        );
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

    @Override
    public String toString() {
        return "PacienteResponseDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", nivelTecnico=" + nivelTecnico +
                ", tipoAtendimento='" + tipoAtendimento + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
