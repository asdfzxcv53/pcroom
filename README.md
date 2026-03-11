# PC Room Management Server

Spring Boot 기반 **PC방 관리 시스템 백엔드 서버**입니다.  
좌석 로그인, 이용 시간 관리, 상품 주문 및 카카오페이 결제 기능을 제공하며  
실제 서비스 환경을 고려하여 **인증 성능 개선과 결제 동시성 문제 해결**을 중심으로 설계했습니다.

---

# 📌 Project Overview

PC방에서는 다음과 같은 기능이 필요합니다.

- 좌석 단위 로그인
- 이용 시간 관리 및 충전
- 상품 주문
- 결제 시스템
- 관리자 주문 관리

본 프로젝트에서는 이러한 기능을 구현하면서 다음과 같은 **실제 서비스 환경에서 발생할 수 있는 문제 해결**을 목표로 했습니다.

- 인증 요청 증가에 따른 **성능 문제**
- 결제 승인 / 취소 동시 요청에 따른 **Race Condition 문제**
- 외부 결제 API 호출 시 **안정적인 예외 처리**

---

# 🏗 Architecture


<img width="689" height="437" alt="image" src="https://github.com/user-attachments/assets/084790df-8fa8-4778-9ca0-44cc4907aae1" />


## 구성 요소

| 구성요소 | 역할 |
|--------|------|
| Spring Boot | REST API 서버 |
| MySQL | 사용자, 좌석, 주문 데이터 관리 |
| Redis | RefreshToken 저장 |
| KakaoPay API | 결제 처리 |

---

# 🛠 Tech Stack

## Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA

## Database
- MySQL
- Redis

## External API
- KakaoPay API

## Tools
- Git
- Postman

---

# 🚀 주요 기능

## 1️⃣ JWT 기반 로그인 시스템

- AccessToken / RefreshToken 기반 인증
- Redis를 활용한 RefreshToken 관리
- 인증 요청 증가에 대응하기 위한 성능 개선 구조 설계

---

## 2️⃣ 좌석 로그인 및 이용 시간 관리

PC방 특성에 맞게 **좌석 단위 로그인 시스템**을 구현했습니다.

### 주요 기능

- 좌석 로그인
- 좌석 이용 시간 차감
- 이용 시간 충전
- 좌석 사용 기록 관리

---

## 3️⃣ 주문 및 결제 시스템

사용자는 PC방에서 상품을 주문하고 결제를 진행할 수 있습니다.

### 주요 기능

- 주문 생성
- 주문 상태 관리
- 카카오페이 결제 요청
- 결제 승인 및 취소 처리

---

# 📚 주요 문제 해결

## 1️⃣ RefreshToken 조회 성능 개선

PC방 시스템은 여러 가맹점에서 동시에 로그인 요청이 발생할 수 있어  
**RefreshToken 조회 TPS가 높아질 가능성**이 있다고 판단했습니다.

MySQL에 RefreshToken을 저장했을 경우 성능 테스트 결과 다음과 같은 문제가 발생했습니다.

| 저장소 | TPS | p95 응답시간 |
|------|------|--------------|
| MySQL | 1500 TPS | 100ms |

DB 조회가 인증 요청 증가 시 병목이 될 수 있다고 판단했습니다.

이를 해결하기 위해 **Redis를 RefreshToken 저장소로 사용하도록 구조를 변경했습니다.**

### 결과

| 저장소 | TPS | p95 응답시간 |
|------|------|--------------|
| Redis | 1500 TPS | 2ms |

Redis 도입을 통해 인증 조회 성능을 크게 개선할 수 있었습니다.
