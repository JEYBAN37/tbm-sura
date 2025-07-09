package com.sura.web.auth;

import com.sura.tbm.JwtLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthService {
    private final JwtLogin jwtLogin;

    @PostMapping("/login")
    public Mono<String> login(@RequestParam String username, @RequestParam String password) {
        return jwtLogin.allowedPass(username,password);
    }
}
