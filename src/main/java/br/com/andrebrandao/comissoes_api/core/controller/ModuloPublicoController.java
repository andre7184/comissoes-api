// src/main/java/br/com/andrebrandao/comissoes_api/core/controller/ModuloPublicoController.java
package br.com.andrebrandao.comissoes_api.core.controller; // <-- Pacote CORRETO

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.core.dto.ModuloCatalogoDTO;
import br.com.andrebrandao.comissoes_api.core.service.ModuloService;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para endpoints PÚBLICOS relacionados a Módulos (Catálogo).
 */
@RestController
@RequestMapping("/api/modulos") // URL base: /api/modulos
@RequiredArgsConstructor
public class ModuloPublicoController {

    private final ModuloService moduloService;

    /**
     * Endpoint PÚBLICO para listar o catálogo de módulos disponíveis para venda.
     * Mapeado para: GET /api/modulos/catalogo
     *
     * @return Lista de ModuloCatalogoDTO.
     */
    @GetMapping("/catalogo")
    public List<ModuloCatalogoDTO> listarCatalogoPublico() {
        return moduloService.listarCatalogoPublico();
    }
}