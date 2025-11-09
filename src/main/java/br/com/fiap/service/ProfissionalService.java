package br.com.fiap.service;

import br.com.fiap.dao.ProfissionalDao;
import br.com.fiap.dto.ProfissionalRequestDto;
import br.com.fiap.dto.ProfissionalResponseDto;
import br.com.fiap.models.Profissional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações de Profissional.
 * Fornece métodos para listar, buscar, cadastrar, atualizar e excluir profissionais.
 */
@ApplicationScoped
public class ProfissionalService {

    @Inject
    private ProfissionalDao profissionalDao;

    /**
     * Lista todos os profissionais cadastrados.
     *
     * @return Lista de {@link ProfissionalResponseDto}.
     * @throws RuntimeException Em caso de erro interno.
     */
    public List<ProfissionalResponseDto> listar() {
        try {
            List<Profissional> profissionais = profissionalDao.listarProfissionais();
            return profissionais.stream()
                    .map(ProfissionalResponseDto::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao listar profissionais: " + e.getMessage());
            throw new RuntimeException("Erro interno ao listar profissionais.", e);
        }
    }

    /**
     * Busca um profissional pelo ID.
     *
     * @param id ID do profissional.
     * @return {@link ProfissionalResponseDto} correspondente.
     * @throws IllegalArgumentException Caso o ID seja inválido.
     * @throws NotFoundException Caso não exista profissional com o ID informado.
     * @throws RuntimeException Em caso de erro interno.
     */
    public ProfissionalResponseDto buscarPorId(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID do profissional deve ser maior que zero.");
            }

            Profissional profissional = profissionalDao.buscarPorId(id);
            if (profissional == null) {
                throw new NotFoundException("Profissional não encontrado com ID: " + id);
            }

            return ProfissionalResponseDto.convertToDto(profissional);
        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissional: " + e.getMessage());
            throw new RuntimeException("Erro interno ao buscar profissional.", e);
        }
    }

    /**
     * Busca um profissional pelo CRM.
     *
     * @param crm Número do CRM do profissional.
     * @return {@link ProfissionalResponseDto} ou null se não encontrado.
     * @throws IllegalArgumentException Caso o CRM seja inválido.
     * @throws RuntimeException Em caso de erro interno.
     */
    public ProfissionalResponseDto buscarPorCrm(Integer crm) {
        try {
            if (crm == null || crm <= 0) {
                throw new IllegalArgumentException("CRM deve ser um número positivo.");
            }

            Profissional profissional = profissionalDao.buscarPorCrm(crm);
            if (profissional == null) {
                return null;
            }

            return ProfissionalResponseDto.convertToDto(profissional);
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissional por CRM: " + e.getMessage());
            throw new RuntimeException("Erro interno ao buscar profissional por CRM.", e);
        }
    }

    /**
     * Cadastra um novo profissional.
     *
     * @param dto DTO com os dados do profissional.
     * @return {@link ProfissionalResponseDto} com os dados cadastrados.
     * @throws IllegalArgumentException Caso os dados sejam inválidos ou CRM já exista.
     * @throws RuntimeException Em caso de erro interno.
     */
    public ProfissionalResponseDto cadastrar(ProfissionalRequestDto dto) {
        try {
            if (dto == null) {
                throw new IllegalArgumentException("Os dados do profissional não podem ser nulos.");
            }

            dto.cleanData();

            StringBuilder erros = new StringBuilder();
            if (dto.getNome() == null || dto.getNome().trim().length() < 2) {
                erros.append("Nome do profissional inválido (mínimo 2 caracteres).\n");
            }
            if (dto.getEspecialidade() == null || dto.getEspecialidade().isBlank()) {
                erros.append("Especialidade é obrigatória.\n");
            }
            if (dto.getTipoAtendimento() == null || dto.getTipoAtendimento().isBlank()) {
                erros.append("Tipo de atendimento é obrigatório.\n");
            }
            if (dto.getCrm() == null || dto.getCrm() <= 0) {
                erros.append("CRM deve ser um número positivo.\n");
            }

            if (!erros.isEmpty()) {
                throw new IllegalArgumentException(erros.toString().trim());
            }

            Profissional existente = profissionalDao.buscarPorCrm(dto.getCrm());
            if (existente != null) {
                throw new IllegalArgumentException("Já existe um profissional com esse CRM.");
            }

            Profissional novo = new Profissional();
            novo.setNome(dto.getNome());
            novo.setEspecialidade(dto.getEspecialidade());
            novo.setTipoAtendimento(dto.getTipoAtendimento());
            novo.setCrm(dto.getCrm());

            profissionalDao.cadastrarProfissional(novo);

            return ProfissionalResponseDto.convertToDto(novo);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar profissional: " + e.getMessage());
            throw new RuntimeException("Erro interno ao cadastrar profissional.", e);
        }
    }

    /**
     * Atualiza um profissional existente.
     *
     * @param id  ID do profissional.
     * @param dto DTO com os dados a atualizar.
     * @throws IllegalArgumentException Caso ID ou dados sejam inválidos.
     * @throws NotFoundException Caso o profissional não exista.
     * @throws RuntimeException Em caso de erro interno.
     */
    public void atualizar(Integer id, ProfissionalRequestDto dto) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID do profissional deve ser maior que zero.");
            }

            if (dto == null) {
                throw new IllegalArgumentException("Os dados do profissional não podem ser nulos.");
            }

            dto.cleanData();

            Profissional existente = profissionalDao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Profissional não encontrado com ID: " + id);
            }

            if (dto.getNome() != null) existente.setNome(dto.getNome());
            if (dto.getTipoAtendimento() != null) existente.setTipoAtendimento(dto.getTipoAtendimento());
            if (dto.getCrm() != null) existente.setCrm(dto.getCrm());

            profissionalDao.atualizarProfissional(existente);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar profissional: " + e.getMessage());
            throw new RuntimeException("Erro interno ao atualizar profissional.", e);
        }
    }

    /**
     * Exclui um profissional pelo ID.
     *
     * @param id ID do profissional.
     * @throws IllegalArgumentException Caso ID seja inválido.
     * @throws NotFoundException Caso profissional não exista.
     * @throws RuntimeException Em caso de erro interno ou se houver dependências relacionadas.
     */
    public void excluir(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID do profissional deve ser maior que zero.");
            }

            Profissional profissional = profissionalDao.buscarPorId(id);
            if (profissional == null) {
                throw new NotFoundException("Profissional não encontrado com ID: " + id);
            }

            profissionalDao.excluirProfissional(id);
        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException("Não é possível excluir o profissional: há registros relacionados.", e);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao excluir profissional: " + e.getMessage());
            throw new RuntimeException("Erro interno ao excluir profissional.", e);
        }
    }
}
