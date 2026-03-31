package com.example.pcroom.domain;

public enum OrderStatus {
    PENDING,    //대기
    APPROVING,  //주문확정중
    PAID,       //주문성공
    CANCELED,   //주문취소
    FAILED      //주문실패
}
