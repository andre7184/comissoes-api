package br.com.andrebrandao.comissoes_api.core.service;

import java.util.List;
import org.springframework.stereotype.Service;

import br.com.andrebrandao.comissoes_api.core.dto.ModuloRequestDTO;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.core.model.ModuloStatus;
import br.com.andrebrandao.comissoes_api.core.repository.ModuloRepository;
import br.com.andrebrandao.comissoes_api.core.dto.ModuloCatalogoDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço com a lógica de negócio para a entidade Modulo.
 * É o "cérebro" do CRUD de Módulos.
 */
@Service // 1. Marca como um Serviço do Spring
@RequiredArgsConstructor // 2. Lombok: Injeta as dependências via construtor
public class ModuloService {

    private final ModuloRepository moduloRepository; // 3. Injeta o repositório

    /**
     * Lista todos os módulos cadastrados no sistema.
     *
     * @return Lista de entidades Modulo.
     */
    public List<Modulo> listarTodos() {
        return moduloRepository.findAll();
    }

    /**
     * Busca um módulo específico pelo seu ID.
     *
     * @param id O ID do módulo.
     * @return A entidade Modulo.
     * @throws EntityNotFoundException se o módulo não for encontrado.
     */
    public Modulo buscarPorId(Long id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Módulo não encontrado com o ID: " + id));
    }

    /**
     * Cria um novo Módulo no banco de dados.
     *
     * @param dto O DTO com os dados do novo módulo.
     * @return A entidade Modulo que foi salva (com o ID gerado).
     */
    public Modulo criar(ModuloRequestDTO dto) {
        // 1. Cria uma nova entidade Modulo (vazia)
        Modulo novoModulo = new Modulo();
        
        // 2. Transfere os dados do DTO para a Entidade
        novoModulo.setNome(dto.getNome());
        novoModulo.setChave(dto.getChave());
        novoModulo.setStatus(dto.getStatus());
        novoModulo.setDescricaoCurta(dto.getDescricaoCurta());
        novoModulo.setPrecoMensal(dto.getPrecoMensal());
        novoModulo.setPadrao(dto.getIsPadrao());

        // 3. Salva a nova entidade no banco e a retorna
        return moduloRepository.save(novoModulo);
    }

    /**
     * Atualiza um Módulo existente no banco de dados.
     *
     * @param id  O ID do módulo a ser atualizado.
     * @param dto O DTO com os novos dados.
     * @return A entidade Modulo que foi atualizada.
     * @throws EntityNotFoundException se o módulo não for encontrado.
     */
    public Modulo atualizar(Long id, ModuloRequestDTO dto) {
        // 1. Primeiro, busca o módulo no banco para garantir que ele existe
        Modulo moduloExistente = buscarPorId(id); // Reutiliza nosso método de
                                                  // busca

        // 2. Atualiza os campos da entidade *existente* com os dados do DTO
        moduloExistente.setNome(dto.getNome());
        moduloExistente.setChave(dto.getChave());
        moduloExistente.setStatus(dto.getStatus());
        moduloExistente.setDescricaoCurta(dto.getDescricaoCurta());
        moduloExistente.setPrecoMensal(dto.getPrecoMensal());
        moduloExistente.setPadrao(dto.getIsPadrao());

        // 3. Salva a entidade atualizada (o JPA/Hibernate entende que é um
        // UPDATE)
        return moduloRepository.save(moduloExistente);
    }

    // Você pode adicionar um método de "delete" aqui se quiser
    // public void deletar(Long id) {
    //     Modulo moduloExistente = buscarPorId(id); // Garante que existe antes de
    //                                              // deletar
    //     moduloRepository.delete(moduloExistente);
    // }
    
    /**
     * Lista todos os módulos que estão prontos para serem vendidos
     * (status = PRONTO_PARA_PRODUCAO).
     * Usado pelo painel do Super Admin para "vender" módulos a uma empresa.
     *
     * @return Lista de módulos disponíveis para venda.
     */
    public List<Modulo> listarDisponiveisParaVenda() {
        // 1. Chama o novo método do repositório
        // 2. Filtra especificamente pelo status PRONTO_PARA_PRODUCAO
        return moduloRepository.findByStatus(ModuloStatus.PRONTO_PARA_PRODUCAO);
    }

    /**
     * Lista os módulos disponíveis para o público (catálogo).
     * Retorna apenas módulos com status PRONTO_PARA_PRODUCAO e mapeados
     * para o DTO simplificado ModuloCatalogoDTO.
     *
     * @return Lista de ModuloCatalogoDTO.
     */
    public List<ModuloCatalogoDTO> listarCatalogoPublico() {
        // 1. Busca os módulos PRONTO_PARA_PRODUCAO usando o método existente no repositório
        List<Modulo> modulosDisponiveis = moduloRepository.findByStatus(ModuloStatus.PRONTO_PARA_PRODUCAO);

        // 2. Mapeia a lista de entidades para a lista de DTOs do catálogo
        return modulosDisponiveis.stream()
                .map(ModuloCatalogoDTO::fromEntity) // Usa o método de fábrica do DTO
                .toList();
    }
}