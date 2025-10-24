package br.com.andrebrandao.comissoes_api.core.exception; // Ajuste o package se necessário

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException; // Importa a exceção específica

import java.util.HashMap;
import java.util.Map;

/**
 * Captura exceções específicas lançadas pelos Controllers ou Services
 * e retorna respostas HTTP personalizadas.
 */
@ControllerAdvice // 1. Marca esta classe como um handler global de exceções
public class GlobalExceptionHandler {

    /**
     * Captura especificamente a EntityNotFoundException.
     * * @param ex A exceção capturada.
     * @param request O contexto da requisição web.
     * @return Uma ResponseEntity com status 400 (Bad Request) e uma mensagem de erro.
     */
    @ExceptionHandler(EntityNotFoundException.class) // 2. Diz qual exceção este método trata
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        
        // 3. Monta uma mensagem de erro clara para o frontend
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.NOT_FOUND.value()); // 4. Define o status como 400
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // 5. Usa a mensagem da exceção (ex: "Um ou mais IDs...")
        body.put("path", request.getDescription(false).replace("uri=", "")); // Opcional: mostra a URL

        // 6. Retorna a resposta com o status 400
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND); 
        
        // Alternativa: Se preferir retornar 404 (Not Found) em vez de 400:
        // body.put("status", HttpStatus.NOT_FOUND.value());
        // body.put("error", "Not Found");
        // return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Você pode adicionar outros métodos @ExceptionHandler aqui para tratar outras exceções
    // (ex: DataIntegrityViolationException, etc.)
}