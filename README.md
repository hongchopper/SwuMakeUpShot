# 화안 (SwuMakeUpShot)

화장품 **전성분표를 사진으로 찍으면** 주의 성분, 알레르기 유발 성분, 좋은 성분을 자동으로 찾아 알려주는 Android 앱입니다.
분석한 화장품은 이름과 사진으로 저장해 두고 언제든 다시 확인할 수 있습니다.

## 주요 기능

- **전성분 촬영 / 불러오기**: CameraX로 바로 촬영하거나 갤러리에서 사진을 선택
- **한글 OCR**: Google ML Kit 한국어 텍스트 인식으로 성분 목록 추출
- **성분 분석**: 추출한 성분을 내장 DB와 대조해 세 가지로 분류
  - 🔴 **주의 성분** (671종)
  - 🟠 **알레르기 유발 성분** (34종)
  - 🔵 **좋은 성분** (58종)
  - 한글명뿐 아니라 영문명으로 적힌 성분도 매칭 (주의·알레르기 성분)
- **분석 결과 저장**: 화장품 이름을 입력해 결과와 사진을 함께 저장
- **내 화장품 목록**: 저장한 화장품을 목록으로 보고, 눌러서 상세 결과 확인 · 길게 눌러 삭제

## 사용 흐름

```
인트로(IntroActivity)
   └─▶ 내 화장품 목록(MainActivity)
          ├─ 항목 터치 ─▶ 상세 결과(DetailActivity)
          └─ 카메라 버튼 ─▶ 사진 선택/촬영(textDetector ─ cameraX)
                              └─ 분석 ─▶ 분석 결과·저장(SubActivity)
```

## 기술 스택

| 구분 | 사용 기술 |
| --- | --- |
| 언어 | Java 8 |
| 플랫폼 | Android (minSdk 23 / targetSdk 33) |
| 빌드 | Gradle, Android Gradle Plugin 7.3.1 |
| 카메라 | AndroidX CameraX |
| 텍스트 인식 | Google ML Kit Text Recognition (Korean) |
| 데이터 저장 | SQLite (assets에 포함된 DB를 기기로 복사해 사용) |
| UI | AppCompat, Material Components, RecyclerView |

## 프로젝트 구조

```
SwuMakeUpShot/
├── app/
│   └── src/main/
│       ├── java/com/example/swumakeupshot/
│       │   ├── IntroActivity.java      # 스플래시 화면
│       │   ├── MainActivity.java       # 저장된 화장품 목록
│       │   ├── textDetector.java       # 이미지 촬영/선택 화면
│       │   ├── cameraX.java            # CameraX 촬영 화면
│       │   ├── SubActivity.java        # OCR + 성분 분석 + 저장
│       │   ├── DetailActivity.java     # 저장된 화장품 상세 결과
│       │   ├── DataBaseHelper(2).java  # SQLite 헬퍼 (성분 DB / 분석 결과 DB)
│       │   ├── DataAdapter(2).java     # DB 조회 어댑터
│       │   ├── Adapter.java, ListItem.java  # 목록(RecyclerView) 어댑터·모델
│       │   └── PermissionSupport.java  # 런타임 권한 처리
│       ├── assets/
│       │   ├── cosmetic_file.db        # 성분 기준 DB
│       │   └── anal_cosmetic.db        # 분석 결과 저장 DB
│       └── res/                        # 레이아웃, 아이콘 등 리소스
└── caution_cosmetic/                   # DB 원본 파일 (DB Browser for SQLite 프로젝트 포함)
```

## 데이터베이스

**`cosmetic_file.db`** — 성분 기준 데이터 (컬럼: `name`, `comment`, `english`)

| 테이블 | 내용 | 개수 |
| --- | --- | --- |
| `caution_ingredients_table` | 주의 성분과 설명 | 671 |
| `allergy_ingredients_table` | 알레르기 유발 성분과 설명 | 34 |
| `good_ingredients_table` | 좋은 성분과 설명 | 58 |

**`anal_cosmetic.db`** — 사용자가 저장한 분석 결과

| 테이블 | 내용 |
| --- | --- |
| `anal_total` | 화장품 이름, 성분 종류별 개수, 사진 URI |
| `anal_cautious` / `anal_allergy` / `anal_good` | 화장품별로 검출된 성분 목록 |

## 실행 방법

1. 저장소를 클론합니다.
   ```bash
   git clone https://github.com/hongchopper/SwuMakeUpShot.git
   ```
2. Android Studio에서 프로젝트 폴더를 엽니다.
3. Gradle Sync가 끝나면 실제 기기(카메라 필요) 또는 에뮬레이터에서 `app`을 실행합니다.

> 앱은 카메라와 저장소 접근 권한을 요청합니다. 촬영한 사진은 `DCIM/swumakeupshot` 폴더에 저장됩니다.

## 참고

- 성분 판정은 앱에 포함된 DB와 OCR로 읽은 텍스트가 정확히 일치하는지로 이뤄지므로, 사진이 흐리거나 성분명이 잘려 인식되면 결과가 누락될 수 있습니다.
- 분석 결과는 참고용이며, 피부 상태에 대한 판단은 전문가와 상담하세요.
