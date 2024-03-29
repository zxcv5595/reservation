# reservation
식당 예약 프로젝트 (각 모듈을 user, owner, customer, kiosk로 나누어 개발 MSA)

✅ Mission 1
- [x] 공통 인증 구현  - user 모듈
- [x] 매장의 점장은 예약 서비스 앱에 상점을 등록한다.(매장 명, 상점위치, 상점 설명) - owner 모듈
- [x] 매장을 등록하기 위해서는 파트너 회원 가입이 되어야 한다.(따로, 승인 조건은 없으며 가입 후 바로 이용 가능) - owner 모듈

✅ Mission 2
- [x] 매장 이용자는 앱을 통해서 매장을 검색하고 상세 정보를 확인한다. - costomer 모듈
- [x] 매장의 상세 정보를 보고, 예약을 진행한다. (예약을 진행하기 위해서는 회원 가입이 필수적으로 이루어 져야 한다. - costomer 모듈

✅ Mission 3
- [x] 서비스를 통해서 예약한 이후에, 예약 10분전에 도착하여 키오스크를 통해서 방문 확인을 진행한다. - kiosk 모듈
- [x] 예약 및 사용 이후에 리뷰를 작성할 수 있다. - costomer 모듈
- [x] 서비스 이용 중 애로사항 발생 - 점장은 승인/예약 거절을 할 수 있다. - owner 모듈

# 테스트 코드 (Mickito, WebMvcTest를 이용한, 유닛테스트 수행)
when, given, then 방식으로 작성 혹은 Act and Assert

<img src="https://github.com/zxcv5595/reservation/assets/109198584/4dda90cc-2cba-4f36-a26c-866e246f2eea"  width="400" height="200"/>



<img src="https://github.com/zxcv5595/reservation/assets/109198584/314e5883-f31f-47cf-acf9-2cfbb014d034"  width="400" height="200"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/913d2a25-e30e-4a07-bdf5-bf3e9d54c876"  width="400" height="600"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/fd6a2820-af44-4e92-b885-51b46dbd6e0a"  width="400" height="200"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/607eb888-820b-485a-9cca-d46131a43605"  width="400" height="200"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/76d584db-fc7b-4b7c-a14a-86bf4264144d"  width="400" height="200"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/d0fbbc88-7d09-4658-9bd2-52f70487dd94"  width="400" height="200"/>


<img src="https://github.com/zxcv5595/reservation/assets/109198584/aa1ffbb3-8058-4427-9afe-fb9d990c1cc2"  width="400" height="200"/>


# ERD(각 entity FetchMode- Lazy, 양방향관계는 entity graph를 사용하여 쿼리 조회, 단방향은 OneToOne, ManyToOne을 사용)
<img src="https://github.com/zxcv5595/reservation/assets/109198584/b18f2bf2-fa9c-4f15-9a15-04d3154020ba"  width="700" height="600"/>

# API docs (Spring Rest docs)
Swagger 대신, Spring Rest docs을 사용하였으며, 테스트 코드를 작성하여 각 모듈 target/snippets에 docs 파일로 저장되게끔 자동화

# Spring Security
- Jwt 토큰 발행
- owner 모듈: 등록을 제외한 기능은 권한 필요 @PreAuthorize("hasRole('OWNER')")
- 회원가입, 로그인 기능은 permitAll(), 나머지 기능은 authenticated()로 로그인 필요.
