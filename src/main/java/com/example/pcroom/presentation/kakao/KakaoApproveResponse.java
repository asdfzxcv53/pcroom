package com.example.pcroom.presentation.kakao;

import com.example.pcroom.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String sid;

    private String partner_order_id;
    private String partner_user_id;

    private String payment_method_type;

    private Amount amount;
    private CardInfo card_info;

    private String item_name;
    private String item_code;
    private Integer quantity;

    private String created_at;
    private String approved_at;

    private String payload;
}