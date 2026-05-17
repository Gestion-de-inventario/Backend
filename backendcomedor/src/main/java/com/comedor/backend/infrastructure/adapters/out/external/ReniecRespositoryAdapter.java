package com.comedor.backend.infrastructure.adapters.out.external;

import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReniecResponseExternalDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class ReniecRespositoryAdapter implements ReniecPort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${RENIEC_TOKEN}")
    private String token;

    @Override
    public Optional<PersonalDataReniec> consultarPorDni(String dni) {

        String url = "https://api.decolecta.com/v1/reniec/dni?numero=" + dni;

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer " + token);
            headers.set("Content-Type","application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ReniecResponseExternalDTO> responseEntity = restTemplate.exchange(
              url,
              HttpMethod.GET,
              entity,
              ReniecResponseExternalDTO.class
            );

            ReniecResponseExternalDTO response = responseEntity.getBody();


            if (response != null && response.getDocument_number() != null) {

                String apellidosCompletos = response.getFirst_last_name() + " " + response.getSecond_last_name();

                return Optional.of(new PersonalDataReniec(
                   response.getDocument_number(),
                   response.getFirst_name(),
                   apellidosCompletos
                ));
            }
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error consultando RENIEC: " + e.getMessage());
        }

        return Optional.empty();
    }
}
