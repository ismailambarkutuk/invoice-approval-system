# Docker Deployment Guide

Bu doküman, Invoice Approval System'i Docker ile çalıştırmak için gerekli adımları içerir.

## Hızlı Başlangıç

### 1. Docker Compose ile Çalıştırma

```bash
# Tüm servisleri başlat (ActiveMQ + Application)
docker-compose up -d

# Logları izle
docker-compose logs -f

# Servisleri durdur
docker-compose down
```

### 2. Servis Erişim Bilgileri

- **Application**: http://localhost:8080/invoice-approval-system/
- **SOAP WSDL**: http://localhost:8080/invoice-approval-system/services/InvoiceWebService?wsdl
- **ActiveMQ Console**: http://localhost:8161 (admin/admin)
- **Payara Admin**: http://localhost:4848

## Docker Compose Servisleri

### ActiveMQ Artemis
- **Port**: 61616 (JMS), 8161 (Web Console)
- **Kullanıcı**: admin
- **Şifre**: admin
- **Container Name**: invoice-approval-activemq

### Invoice Application
- **Port**: 8080 (Application), 4848 (Admin)
- **Container Name**: invoice-approval-app
- **Base Image**: Payara Server 6.2024.1

## Manuel Build ve Run

### Sadece Application Build

```bash
docker build -t invoice-approval-app .
```

### Sadece Application Run

```bash
docker run -d \
  --name invoice-app \
  -p 8080:8080 \
  -p 4848:4848 \
  -e ACTIVE_MQ_BROKER_URL=tcp://host.docker.internal:61616 \
  invoice-approval-app
```

### Sadece ActiveMQ Run

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  -e AMQ_USER=admin \
  -e AMQ_PASSWORD=admin \
  apache/activemq-artemis:latest
```

## Environment Variables

### Application Container

- `ACTIVE_MQ_BROKER_URL`: ActiveMQ broker URL (varsayılan: `tcp://localhost:61616`)
  - Docker Compose içinde: `tcp://activemq:61616`
  - Local development: `tcp://localhost:61616`

## Troubleshooting

### ActiveMQ Bağlantı Hatası

**Sorun**: Application ActiveMQ'ya bağlanamıyor

**Çözüm**:
1. ActiveMQ container'ının çalıştığını kontrol edin:
   ```bash
   docker ps | grep activemq
   ```

2. Network bağlantısını kontrol edin:
   ```bash
   docker network inspect invoice-approval-system_invoice-network
   ```

3. Environment variable'ı kontrol edin:
   ```bash
   docker exec invoice-approval-app env | grep ACTIVE_MQ
   ```

### Port Çakışması

**Sorun**: Port zaten kullanılıyor

**Çözüm**: `docker-compose.yml` dosyasındaki port numaralarını değiştirin:
```yaml
ports:
  - "8081:8080"  # Application port'u 8081'e değiştir
  - "61617:61616"  # ActiveMQ port'u 61617'ye değiştir
```

### Logları İnceleme

```bash
# Application logları
docker logs invoice-approval-app -f

# ActiveMQ logları
docker logs invoice-approval-activemq -f

# Tüm servislerin logları
docker-compose logs -f
```

## Development Workflow

### Hot Reload için Volume Mount

`docker-compose.override.yml` dosyası oluşturun:

```yaml
version: '3.8'

services:
  invoice-app:
    volumes:
      - ./invoice-approval-web/target:/opt/payara/deployments
```

Bu sayede WAR dosyasını yeniden build ettiğinizde otomatik olarak redeploy olur.

### Database Persistence

H2 database'i kalıcı hale getirmek için volume ekleyin:

```yaml
services:
  invoice-app:
    volumes:
      - ./data:/opt/payara/glassfish/domains/domain1/databases
```

## Production Deployment

Production için şu değişiklikleri yapın:

1. **Environment Variables**: Hassas bilgileri secrets olarak yönetin
2. **Resource Limits**: Container'lara memory ve CPU limitleri ekleyin
3. **Health Checks**: Daha detaylı health check'ler ekleyin
4. **Logging**: Centralized logging (ELK, Splunk, etc.) entegre edin
5. **Monitoring**: Prometheus/Grafana ile monitoring ekleyin

## Cleanup

```bash
# Container'ları durdur ve sil
docker-compose down

# Image'ları da sil
docker-compose down --rmi all

# Volume'ları da sil
docker-compose down -v
```
