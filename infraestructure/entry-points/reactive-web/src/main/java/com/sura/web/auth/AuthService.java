package com.sura.web.auth;

import com.sura.tbm.JwtLogin;
import com.sura.tbm.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthService {
    private final JwtLogin jwtLogin;

    @Operation(summary = "SERVICIO PARA OBTENER TOKEN",
            description = "Servicio que retorna el Token de Acceso",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping("/login")
    public Mono<TokenDto> login(@RequestParam String username, @RequestParam String password) {
        return jwtLogin.allowedPass(username,password);
    }
}
