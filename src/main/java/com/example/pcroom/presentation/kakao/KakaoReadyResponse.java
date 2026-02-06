package com.example.pcroom.presentation.kakao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KakaoReadyResponse {
    private String tid;
    private String next_redirect_app_url;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String created_at;
}
