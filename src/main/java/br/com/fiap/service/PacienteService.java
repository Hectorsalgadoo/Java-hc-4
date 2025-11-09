package br.com.fiap.service;

import br.com.fiap.dao.PacienteDao;
import br.com.fiap.dto.PacienteRequestDto;
import br.com.fiap.dto.PacienteResponseDto;
import br.com.fiap.models.Paciente;
import br.com.fiap.security.PasswordHash;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações de Paciente.
 * Fornece métodos para listar, buscar, cadastrar, atualizar e excluir pacientes.
 */
@ApplicationScoped
public class PacienteService {

    @Inject
    private PacienteDao pacienteDao;

    /**
     * Lista todos os pacientes cadastrados.
     *
     * @return Lista de {@link PacienteResponseDto} contendo os pacientes.
     * @throws RuntimeException Em caso de erro interno ao listar pacientes.
     */
    public List<PacienteResponseDto> listar() {
        try {
            List<Paciente> pacientes = pacienteDao.listarPacientes();
            return pacientes.stream()
                    .map(PacienteResponseDto::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao listar pacientes: " + e.getMessage());
            throw new RuntimeException("Erro interno ao listar pacientes.", e);
        }
    }

    /**
     * Busca um paciente pelo seu ID.
     *
     * @param id ID do paciente.
     * @return {@link PacienteResponseDto} correspondente ao paciente.
     * @throws IllegalArgumentException Caso o ID seja menor ou igual a zero.
     * @throws NotFoundException Caso o paciente não seja encontrado.
     * @throws RuntimeException Em caso de erro interno.
     */
    public PacienteResponseDto buscarPorId(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("O ID do paciente deve ser maior que zero.");
            }

            Paciente paciente = pacienteDao.buscarPorId(id);
            if (paciente == null) {
                throw new NotFoundException("Paciente não encontrado com ID: " + id);
            }

            return PacienteResponseDto.convertToDto(paciente);

        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
            throw new RuntimeException("Erro interno ao buscar paciente.", e);
        }
    }

    /**
     * Busca um paciente pelo seu CPF.
     *
     * @param cpf CPF do paciente (apenas números, 11 dígitos).
     * @return {@link PacienteResponseDto} ou null se não encontrado.
     * @throws IllegalArgumentException Caso o CPF seja inválido.
     * @throws RuntimeException Em caso de erro interno.
     */
    public PacienteResponseDto buscarPorCpf(String cpf) {
        try {
            if (cpf == null || !cpf.matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos numéricos.");
            }

            Paciente paciente = pacienteDao.buscarPorCpf(cpf);
            if (paciente == null) {
                return null;
            }

            return PacienteResponseDto.convertToDto(paciente);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por CPF: " + e.getMessage());
            throw new RuntimeException("Erro interno ao buscar paciente por CPF.", e);
        }
    }

    /**
     * Busca um paciente pelo CPF retornando o objeto completo {@link Paciente}.
     *
     * @param cpf CPF do paciente.
     * @return {@link Paciente} ou null se não encontrado.
     */
    public Paciente buscarPorCpfRaw(String cpf) {
        return pacienteDao.buscarPorCpf(cpf);
    }

    /**
     * Cadastra um novo paciente.
     *
     * @param pacienteDto DTO contendo os dados do paciente.
     * @return {@link PacienteResponseDto} com os dados do paciente cadastrado.
     * @throws IllegalArgumentException Caso dados sejam inválidos ou CPF já cadastrado.
     * @throws RuntimeException Em caso de erro interno.
     */
    public PacienteResponseDto cadastrar(PacienteRequestDto pacienteDto) {
        try {
            if (pacienteDto == null) {
                throw new IllegalArgumentException("Os dados do paciente não podem ser nulos.");
            }

            if (pacienteDto.getNome() != null) pacienteDto.setNome(pacienteDto.getNome().trim());
            if (pacienteDto.getCpf() != null) pacienteDto.setCpf(pacienteDto.getCpf().replaceAll("[^0-9]", ""));

            StringBuilder erros = new StringBuilder();

            if (pacienteDto.getNome() == null || pacienteDto.getNome().length() < 2) {
                erros.append("Nome inválido (mínimo 2 caracteres).\n");
            }
            if (pacienteDto.getCpf() == null || !pacienteDto.getCpf().matches("\\d{11}")) {
                erros.append("CPF deve conter exatamente 11 números.\n");
            }
            if (pacienteDto.getIdade() == null || pacienteDto.getIdade() < 0 || pacienteDto.getIdade() > 120) {
                erros.append("Idade deve estar entre 0 e 120.\n");
            }
            if (pacienteDto.getNivelTecnico() == null || pacienteDto.getNivelTecnico() < 0 || pacienteDto.getNivelTecnico() > 10) {
                erros.append("Nível técnico deve estar entre 0 e 10.\n");
            }
            if (pacienteDto.getTipoAtendimento() == null || pacienteDto.getTipoAtendimento().isBlank()) {
                erros.append("Tipo de atendimento é obrigatório.\n");
            }
            if (pacienteDto.getSenha() == null || pacienteDto.getSenha().length() < 6 || pacienteDto.getSenha().length() > 8) {
                erros.append("Senha deve ter entre 6 e 8 caracteres.\n");
            }

            if (!erros.isEmpty()) throw new IllegalArgumentException(erros.toString().trim());

            Paciente existente = pacienteDao.buscarPorCpf(pacienteDto.getCpf());
            if (existente != null) throw new IllegalArgumentException("CPF já cadastrado.");

            String senhaCriptografada = PasswordHash.hashPassword(pacienteDto.getSenha());
            pacienteDto.setSenha(senhaCriptografada);

            Paciente novo = new Paciente();
            novo.setNome(pacienteDto.getNome());
            novo.setCpf(pacienteDto.getCpf());
            novo.setIdade(pacienteDto.getIdade());
            novo.setNivelTecnico(pacienteDto.getNivelTecnico());
            novo.setTipoAtendimento(pacienteDto.getTipoAtendimento());
            novo.setSenha(pacienteDto.getSenha());

            pacienteDao.cadastrarPaciente(novo);

            return PacienteResponseDto.convertToDto(novo);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar paciente: " + e.getMessage());
            throw new RuntimeException("Erro interno ao cadastrar paciente.", e);
        }
    }

    /**
     * Atualiza um paciente existente.
     *
     * @param id  ID do paciente.
     * @param dto DTO com os dados a serem atualizados.
     * @return {@link Paciente} atualizado, ou null se não encontrado.
     * @throws IllegalArgumentException Caso ID ou dados sejam inválidos.
     * @throws RuntimeException Em caso de erro interno.
     */
    public Paciente atualizar(Integer id, PacienteRequestDto dto) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID do paciente deve ser maior que zero.");
            }

            if (dto == null) {
                throw new IllegalArgumentException("Os dados do paciente não podem ser nulos.");
            }

            dto.cleanData();

            Paciente existente = pacienteDao.buscarPorId(id);
            if (existente == null) {
                System.out.println("Paciente não encontrado com ID: " + id);
                return null;
            }

            if (dto.getNome() != null) existente.setNome(dto.getNome());
            if (dto.getIdade() != null) existente.setIdade(dto.getIdade());
            if (dto.getNivelTecnico() != null) existente.setNivelTecnico(dto.getNivelTecnico());
            if (dto.getTipoAtendimento() != null) existente.setTipoAtendimento(dto.getTipoAtendimento());
            if (dto.getCpf() != null) existente.setCpf(dto.getCpf());

            if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
                String senhaCriptografada = PasswordHash.hashPassword(dto.getSenha());
                existente.setSenha(senhaCriptografada);
            }

            existente.limparDados();

            Paciente atualizado = pacienteDao.atualizarPaciente(existente);
            System.out.println("Paciente atualizado com sucesso: " + atualizado);
            return atualizado;

        } catch (Exception e) {
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro interno ao atualizar paciente.");
        }
    }

    /**
     * Exclui um paciente pelo seu ID.
     *
     * @param id ID do paciente.
     * @throws IllegalArgumentException Caso o ID seja inválido.
     * @throws NotFoundException Caso o paciente não seja encontrado.
     * @throws RuntimeException Em caso de erro interno ou se houver dependências relacionadas.
     */
    public void excluir(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("O ID do paciente deve ser maior que zero.");
            }

            Paciente paciente = pacienteDao.buscarPorId(id);
            if (paciente == null) {
                throw new NotFoundException("Paciente não encontrado com ID: " + id);
            }

            pacienteDao.excluirPaciente(id);

        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException("Não é possível excluir o paciente: há registros relacionados.", e);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao excluir paciente: " + e.getMessage());
            throw new RuntimeException("Erro interno ao excluir paciente.", e);
        }
    }
}
