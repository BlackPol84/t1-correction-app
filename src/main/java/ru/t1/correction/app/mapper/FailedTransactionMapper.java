package ru.t1.correction.app.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.t1.correction.app.model.FailedTransaction;
import ru.t1.correction.app.model.dto.FailedTransactionDto;

@Mapper(componentModel = "spring")
public interface FailedTransactionMapper {

    FailedTransactionDto toDto(FailedTransaction transaction);

    @InheritInverseConfiguration
    FailedTransaction toEntity(FailedTransactionDto dto);
}
