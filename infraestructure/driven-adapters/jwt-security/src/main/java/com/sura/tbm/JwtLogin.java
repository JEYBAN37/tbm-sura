package com.sura.tbm;
import com.sura.model.common.ex.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class JwtLogin {
    private final  JwtUtil jwtUtil;
    public Mono<String>  allowedPass (String user, String pwd){
        if ("usuario".equals(user) && "password".equals(pwd)) {
            String token = jwtUtil.generateToken(user);
            return Mono.just(token);
        }
        return Mono.error(BusinessException.Type.ERROR_CREDEENCIALES_INCORRECTAS.build());
    }
}
