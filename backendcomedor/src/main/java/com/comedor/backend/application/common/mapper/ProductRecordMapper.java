package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRecordMapper {

    public Record toDomain(RegistroProductoRequestDTO requestDTO)
    {
        Record record = new Record();

        Product product = new Product();
        product.setId(requestDTO.getProductoId());
        record.setProduct(product);

        record.setAmount(requestDTO.getAmount());
        record.setProductSource(requestDTO.getProductSource());
        record.setUnitPrice(requestDTO.getUnitPrice());
        return record;
    }

    public RegistroProductoResponseDTO toDto(Record record)
    {
        RegistroProductoResponseDTO responseDTO = new RegistroProductoResponseDTO();
        if(record.getProduct() != null)
        {
            responseDTO.setProductoId(record.getProduct().getId());
            responseDTO.setProductName(record.getProduct().getName());
            responseDTO.setProductCategory(record.getProduct().getCategory().getName());
            responseDTO.setProductUnit(record.getProduct().getUnit());
        }
        responseDTO.setSourceProduct(record.getProductSource());
        responseDTO.setAmount(record.getAmount());
        responseDTO.setSpentAmount(record.getAmount().multiply(record.getUnitPrice()));
        return responseDTO;
    }

    public List<RegistroProductoResponseDTO> toListDto (List<Record> records)
    {
       return records.stream().map(this::toDto).toList();
    }
}
