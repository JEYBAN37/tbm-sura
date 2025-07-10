package com.sura.tbm;

import com.sura.jwt.JwtUtil;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import com.sura.usecase.gastoxviaje.ConsultarGastosxViajeUseCase;
import com.sura.usecase.gastoxviaje.GenerarReporteMensualUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("sura.tbm")
public class UseCaseConfig {
    @Bean
    public ConsultarGastosxViajeUseCase consultarGastosxViajeUseCase (GastoxViajeRepository gastoxViajeRepository){
        return  new ConsultarGastosxViajeUseCase(gastoxViajeRepository);
    }

    @Bean
    public JwtUtil jwtUtil (@Value("${constant.keysecret}") String key, @Value("${constant.vigenciaTokenMinutos}") Long time){
        return  new JwtUtil(time,key);
    }

    @Bean
    public GenerarReporteMensualUseCase generarReporteMensualUseCase (GastoxViajeRepository gastoxViajeRepository){
        return new GenerarReporteMensualUseCase(gastoxViajeRepository);
    }

}
