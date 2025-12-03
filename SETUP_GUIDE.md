# Invoice Approval System - Setup Guide

Bu doküman, Invoice Approval System projesini kurmak ve çalıştırmak için adım adım talimatlar içermektedir.

## Gereksinimler

1. **Java Development Kit (JDK)**: Version 17 veya üzeri
2. **Apache Maven**: Version 3.6 veya üzeri
3. **Apache ActiveMQ**: Version 5.18.0 veya üzeri
4. **Jakarta EE Uygulama Sunucusu**:
   - Payara Server 6.x veya üzeri
   - WildFly 27.x veya üzeri
   - GlassFish 7.x veya üzeri

## Kurulum Adımları

### 1. Projeyi İndirin ve Derleyin

```bash
# Proje dizinine gidin
cd invoice-approval-system

# Projeyi derleyin
mvn clean install
```

Bu komut tüm modülleri derleyecek ve WAR dosyasını oluşturacaktır.

### 2. ActiveMQ Kurulumu ve Başlatma

#### Seçenek A: Standalone ActiveMQ

1. ActiveMQ'yu indirin:
   - https://activemq.apache.org/downloads adresinden ActiveMQ 5.18.0 veya üzeri sürümü indirin

2. ActiveMQ'yu çalıştırın:
   ```bash
   # Windows
   cd apache-activemq-5.18.0\bin\win64
   activemq.bat start
   
   # Linux/Mac
   cd apache-activemq-5.18.0/bin
   ./activemq start
   ```

3. ActiveMQ Admin Console'a erişin:
   - http://localhost:8161/admin
   - Varsayılan kullanıcı adı: `admin`
   - Varsayılan şifre: `admin`

#### Seçenek B: Embedded ActiveMQ (Geliştirme için)

Uygulama içinde embedded broker kullanmak için `ActiveMQConfig.java` dosyasını düzenleyebilirsiniz.

### 3. Veritabanı Yapılandırması

Proje varsayılan olarak H2 in-memory veritabanı kullanmaktadır. Bu geliştirme için yeterlidir.

Farklı bir veritabanı kullanmak isterseniz:
- `invoice-approval-data/src/main/resources/META-INF/persistence.xml` dosyasını düzenleyin
- JDBC driver bağımlılığını `pom.xml` dosyasına ekleyin

### 4. Uygulama Sunucusuna Deploy Etme

#### Payara Server ile

1. Payara Server'ı indirin ve başlatın:
   ```bash
   # Payara Server'ı başlatın
   ./asadmin start-domain
   ```

2. WAR dosyasını deploy edin:
   ```bash
   # Otomatik deploy (autodeploy klasörüne kopyalayın)
   cp invoice-approval-web/target/invoice-approval-system.war \
      $PAYARA_HOME/glassfish/domains/domain1/autodeploy/
   
   # Veya asadmin ile deploy edin
   ./asadmin deploy invoice-approval-web/target/invoice-approval-system.war
   ```

3. Uygulamaya erişin:
   - http://localhost:8080/invoice-approval-system/

#### WildFly ile

1. WildFly'ı başlatın:
   ```bash
   ./standalone.sh  # Linux/Mac
   standalone.bat   # Windows
   ```

2. WAR dosyasını deploy edin:
   ```bash
   # Deployments klasörüne kopyalayın
   cp invoice-approval-web/target/invoice-approval-system.war \
      $WILDFLY_HOME/standalone/deployments/
   ```

3. Uygulamaya erişin:
   - http://localhost:8080/invoice-approval-system/

### 5. SOAP Servislerini Test Etme

#### WSDL URL'si

SOAP servislerinin WSDL tanımına şu adresten erişebilirsiniz:
```
http://localhost:8080/invoice-approval-system/services/InvoiceWebService?wsdl
```

#### Postman ile Test

1. Postman'i açın
2. Yeni bir request oluşturun:
   - Method: `POST`
   - URL: `http://localhost:8080/invoice-approval-system/services/InvoiceWebService`
   - Headers:
     - `Content-Type: application/soap+xml`
   - Body: Raw XML formatında SOAP request gönderin

#### Örnek SOAP Request (createInvoice)

```xml
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
               xmlns:api="http://api.approval.invoice.com/">
  <soap:Body>
    <api:createInvoice>
      <api:createInvoiceRequest>
        <api:invoiceNumber>INV-2024-001</api:invoiceNumber>
        <api:vendorName>Acme Corporation</api:vendorName>
        <api:amount>1500.00</api:amount>
        <api:createdBy>John Doe</api:createdBy>
        <api:description>Monthly service invoice</api:description>
      </api:createInvoiceRequest>
    </api:createInvoice>
  </soap:Body>
</soap:Envelope>
```

#### SoapUI ile Test

1. SoapUI'yi açın
2. Yeni bir SOAP projesi oluşturun
3. WSDL URL'sini girin:
   ```
   http://localhost:8080/invoice-approval-system/services/InvoiceWebService?wsdl
   ```
4. SoapUI otomatik olarak servisleri ve operasyonları oluşturacaktır

## Yapılandırma Dosyaları

### ActiveMQ Yapılandırması

ActiveMQ bağlantı ayarları:
- Dosya: `invoice-approval-messaging/src/main/java/com/invoice/approval/messaging/config/ActiveMQConfig.java`
- Varsayılan Broker URL: `tcp://localhost:61616`
- Varsayılan Queue: `invoice.approval.queue`

### Veritabanı Yapılandırması

JPA persistence ayarları:
- Dosya: `invoice-approval-data/src/main/resources/META-INF/persistence.xml`
- Persistence Unit: `invoice-approval-pu`

### Web Uygulaması Yapılandırması

SOAP servis yapılandırması:
- Dosya: `invoice-approval-web/src/main/webapp/WEB-INF/web.xml`
- Dosya: `invoice-approval-web/src/main/webapp/WEB-INF/sun-jaxws.xml`

## Sorun Giderme

### ActiveMQ Bağlantı Hatası

**Sorun**: "Failed to establish ActiveMQ connection"

**Çözüm**:
1. ActiveMQ broker'ın çalıştığından emin olun
2. Port 61616'nın açık olduğunu kontrol edin
3. Firewall ayarlarını kontrol edin
4. `ActiveMQConfig.java` dosyasındaki broker URL'ini kontrol edin

### SOAP Servis Bulunamıyor

**Sorun**: 404 veya servis bulunamıyor hatası

**Çözüm**:
1. WAR dosyasının doğru deploy edildiğinden emin olun
2. Uygulama sunucusu loglarını kontrol edin
3. `web.xml` ve `sun-jaxws.xml` dosyalarının doğru yapılandırıldığını kontrol edin
4. JAX-WS bağımlılıklarının dahil olduğundan emin olun

### Veritabanı Bağlantı Hatası

**Sorun**: "Unable to acquire JDBC Connection"

**Çözüm**:
1. H2 veritabanı bağımlılığının dahil olduğundan emin olun
2. `persistence.xml` dosyasındaki JDBC URL'ini kontrol edin
3. EntityManager factory'nin doğru persistence unit'i kullandığını kontrol edin

### CDI Injection Hatası

**Sorun**: NullPointerException veya injection hatası

**Çözüm**:
1. `beans.xml` dosyasının `WEB-INF` klasöründe olduğundan emin olun
2. Bean discovery mode'un `all` olarak ayarlandığını kontrol edin
3. Sınıfların `@ApplicationScoped` veya uygun scope annotation'ına sahip olduğunu kontrol edin

## Geliştirme Ortamı

### IDE Önerileri

- **IntelliJ IDEA**: Jakarta EE desteği ile
- **Eclipse**: Jakarta EE Tools ile
- **VS Code**: Java Extension Pack ile

### Maven Komutları

```bash
# Tüm projeyi derle
mvn clean install

# Sadece testleri çalıştır
mvn test

# WAR dosyasını oluştur
mvn clean package

# Belirli bir modülü derle
cd invoice-approval-data
mvn clean install
```

## Sonraki Adımlar

1. ✅ Projeyi derleyin
2. ✅ ActiveMQ'yu başlatın
3. ✅ Uygulamayı deploy edin
4. ✅ SOAP servislerini test edin
5. ✅ ActiveMQ queue'larını izleyin
6. ✅ Veritabanı verilerini kontrol edin

## Ek Kaynaklar

- [Jakarta EE Documentation](https://jakarta.ee/specifications/)
- [Apache ActiveMQ Documentation](https://activemq.apache.org/getting-started)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JAX-WS Documentation](https://javaee.github.io/jax-ws-spec/)

