package com.sura.tbm;


import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import com.sura.usecase.gastoxviaje.ConsultarGastosxViajeUseCase;
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
}
