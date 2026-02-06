package com.example.pcroom.presentation.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoCancelRequest {
    private String cid;
    private String tid;
    private Integer cancel_amount;
    private Integer cancel_tax_free_amount;

    public KakaoCancelRequest() {}
    public KakaoCancelRequest(String cid, String tid, Integer cancel_amount, Integer cancel_tax_free_amount) {
        this.cid = cid;
        this.tid = tid;
        this.cancel_amount = cancel_amount;
        this.cancel_tax_free_amount = cancel_tax_free_amount;
    }
}
