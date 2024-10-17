package ru.t1.correction.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FailedTransactionDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("transaction_id")
    private Long originalTransactionId;

    @JsonProperty("account_id")
    private Long accountId;
}
