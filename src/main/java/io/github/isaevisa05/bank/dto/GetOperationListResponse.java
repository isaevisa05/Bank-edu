package io.github.isaevisa05.bank.dto;

import io.github.isaevisa05.bank.entity.History;
import lombok.Data;

import java.util.List;

@Data
public class GetOperationListResponse {

    private List<History> result;
    private String error;
}
