package com.example.pcroom.presentation.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Amount {
    private Integer total;
    private Integer tax_free;
    private Integer vat;
    private Integer point;
    private Integer discount;
    private Integer greenDeposit;
}
