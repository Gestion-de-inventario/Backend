package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.model.DatosPersonales;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReniecResponseExternalDTO;
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

    private final String TOKEN = "";

    @Override
    public Optional<DatosPersonales> consultarPorDni(String dni) {

        String url = "https://api.apis.net.pe/v1/dni?numero=" + dni;

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer " + TOKEN);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ReniecResponseExternalDTO> responseEntity = restTemplate.exchange(
              url,
              HttpMethod.GET,
              entity,
              ReniecResponseExternalDTO.class
            );

            ReniecResponseExternalDTO response = responseEntity.getBody();


            if (response != null && response.getNumeroDocumento() != null) {

                String apellidosCompletos = response.getApellidoPaterno() + " " + response.getApellidoMaterno();

                return Optional.of(new DatosPersonales(
                   response.getNumeroDocumento(),
                   response.getNombres(),
                   apellidosCompletos
                ));
            }
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error consltando RENIEC: " + e.getMessage());
        }

        return Optional.empty();
    }
}
