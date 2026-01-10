# denodo-api

> **api.denodo.kr/api/**
>
> Java 21 · Spring Boot 3.5.9 · External Tomcat 10.1.49 · Nginx 1.24.0 · MariaDB 10.3.39

---

## 📌 프로젝트 개요

본 프로젝트는 **Public / Private 망이 분리된 AWS EC2 아키텍처** 기반에서
Spring Boot 3.5.9 애플리케이션을 **외장 Tomcat 10**에 배포하고,
Nginx를 통한 **TLS 종료 + Reverse Proxy** 구조로 운영되는 백엔드 API 서버입니다.

- 외부 접근은 **443(HTTPS)** 만 허용
- 내부 WAS 포트 **8080 비공개**
- DB는 **Private Subnet** 에 위치
- Bastion + SSH Port Forwarding 기반 운영

---

## 🧱 전체 아키텍처 요약

```
[ Client ]
    |
  HTTPS :443
    |
[ Nginx (Public EC2) ]
    |
  127.0.0.1:8080
    |
[ Tomcat 10 (Spring Boot WAR) ]
    |
  Private IP:3306
    |
[ MariaDB (Private EC2) ]
```

---

# 1️⃣ Private 망 EC2 (DB 서버)

## 🔐 기본 정보

| 항목 | 내용 |
|----|----|
| OS | Ubuntu 24.04 LTS |
| DB | MariaDB 10.3.39 |
| Network | Private Subnet |
| 접근 방식 | Bastion EC2 경유 SSH |

---

## 🔑 접속 방법

### ✅ Bastion → Private DB 서버 SSH

```bash
ssh -i /home/ubuntu/.ssh/unenc_myec2key.pem ubuntu@10.0.2.46
```

---

## 🔄 DB 포트 연결 확인

### ▶ Bastion 에서 TCP 연결 확인

```bash
nc -vz 10.0.2.46 3306
```

### ▶ DB 서버에서 세션 확인

```bash
ss -ntp | grep :3306
```

`ESTABLISHED` 상태 확인 시 내부 통신 정상

---

# 2️⃣ Public 망 EC2 (WAS / Proxy 서버)

## 🌐 구성 정보

| 항목 | 내용 |
|----|----|
| OS | Ubuntu 24.04 LTS |
| JDK | OpenJDK 21 |
| WAS | Tomcat 10.1.49 (External) |
| Framework | Spring Boot 3.5.9 (WAR) |
| Proxy | Nginx 1.24.0 |
| Domain | api.denodo.kr |

---

## 🔒 네트워크 / 보안 정책

- 8080 ❌ 외부 차단
- 80 → 8080 ❌ (미사용)
- 443 ✅ HTTPS 허용
- TLS 종료 지점: **Nginx**
- HSTS + Security Header 적용

---

## 🧩 사용 포트 정리

| 포트 | 용도 |
|----|----|
| 443 | External HTTPS |
| 8080 | Internal Tomcat (127.0.0.1) |
| 3306 | Private DB |

---

## 🛠️ WAS / Proxy 운영 Shell

### ▶ Tomcat 상태 확인 / 재시작

```bash
sudo systemctl status tomcat
sudo systemctl start tomcat
sudo systemctl stop tomcat
sudo systemctl restart tomcat
```

### ▶ Nginx 제어

```bash
sudo systemctl status nginx
sudo systemctl start nginx
sudo systemctl stop nginx
sudo systemctl reload nginx
```

---

## ⚙️ Server 설정 파일

### ▶ Tomcat 설정

```bash
vi /opt/tomcat/current/bin/setenv.sh
vi /opt/tomcat/current/conf/Catalina/localhost/api.xml

vi /opt/tomcat/current/conf/server.xml
```

### ▶ Nginx 설정

```bash
sudo vi /etc/nginx/sites-available/denodo.conf
```

---

## 📜 로그 확인

### ▶ Tomcat 로그

```bash
tail -f -n 1000 /opt/tomcat/current/logs/catalina.out

ps -ef | grep tomcat
ss -lntp | grep 8080
```

### ▶ Nginx 로그

```bash
tail -f -n 1000 /var/log/nginx/access.log
```

---

## 🧪 API 테스트

### ▶ Local

```bash
curl -i http://localhost:8080/api/db/test | head -n 20
curl -i http://localhost/api/db/test
```

### ▶ External

```bash
curl -i https://api.denodo.kr/api/db/test
```

---

## 🔐 SSH 포트 포워딩 (Local PC → Private DB)
### Local 개발시 로컬 IDE 에서 DB 연결 방법

### ▶ PowerShell 기준 (Windows)

```powershell
ssh -i D:\dev\aws\unenc_myec2key.pem -N -L 3307:10.0.2.46:3306 ubuntu@43.203.97.101
```

### ▶ 포트 확인 (새 창)

```powershell
netstat -ano | findstr 3307
```

---

# 📍 Git / 프로젝트 관리

## ▶ 초기 Git 설정

```bash
git init
git remote add origin https://github.com/denodo1/denodo-api.git
git config --global user.name "JAEHEE"
git config --global user.email "denodo1@gmail.com"
```

## ▶ 최초 Push

```bash
git add .
git commit -m "initial commit"
git branch -M main
git push -u origin main
```

---

# 📄 참고 사항

- Spring Boot는 **내장 Tomcat 미사용**
- 모든 트래픽은 Nginx → 127.0.0.1:8080 경유
- DB 직접 접근 금지 (Private Subnet)
- Bastion + SSH Tunnel 필수

---

## ✨ Author

- **denodo / JAEHEE**
- Backend / Infrastructure / Cloud Architecture

---

> 🚀 실무 기준 Public / Private 망 분리 + 외장 Tomcat + TLS 종료 아키텍처 예제

