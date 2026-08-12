## 주제

자유 게시판

## 핵심 기능

- 로그인
- 게시글 작성
- 댓글 작성
    - depth = 1
- 게시글 추천 상호작용

## 도메인 설계

- user
    - id
    - name
    - created_at
- post
    - id
    - user_id
    - title
    - content
    - good
    - updated_at
    - created_at
- comment
    - id
    - user_id
    - post_id
    - content
    - good
    - created_at

## 유스케이스

- 로그인
- 로그아웃

---

- 게시글 목록 조회
- 게시글 단일 조회
- 게시글 작성
- 게시글 수정
- 게시글 삭제

---

- 댓글 작성
- 댓글 삭제

---

- 게시글 추천
- 댓글 추천

## API

- POST /login
- POST /logout
- GET /posts
- GET /posts/:id
- POST /posts
- PUT /posts/:id
- DELETE /posts/:id
- GET /posts/:id/comments
- POST /comments
- DELETE /comments/:id
- PUT /posts/:id/good
- PUT /comments/:id/good