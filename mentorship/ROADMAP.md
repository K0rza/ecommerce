# E-Ticaret Mikroservis Projesi — Durum ve Yol Haritası

> Son güncelleme: 2026-09-12
>
> Bu dosya projenin **nerede olduğunu** ve **nereye gittiğini** anlatır.
> Mimarinin ne olduğu (servis sorumlulukları, teknoloji seçimleri) `PROJECT_CONTEXT.md` dosyasındadır.
> Buradaki tüm "durum" bilgileri koda bakılarak doğrulanmıştır, dokümandan kopyalanmamıştır.

---

## 1. Projenin amacı

Bu proje çalışan bir e-ticaret uygulaması üretmek için değil, **ileri düzey yazılım mimarisi pratiklerini derinlemesine öğrenmek** için yazılıyor. Hedeflenen konular:

- Mikroservis mimarisi ve servis sınırları (Bounded Context)
- Clean Architecture / Hexagonal Architecture ve katman disiplini
- Domain-Driven Design
- Event-Driven Architecture, Kafka
- Outbox Pattern (dual-write problemi)
- Saga Pattern (Choreography) ve telafi işlemleri
- Eşzamanlılık kontrolü (optimistic locking)
- Observability: loglama, AOP, distributed tracing
- Dayanıklılık: retry, circuit breaker, DLQ, idempotency

---

## 2. Şimdiye kadar tamamlananlar

### 2.1 Temel altyapı

- Beş servis: `product-service`, `inventory-service`, `order-service`, `api-gateway`, `discovery-service`
- Netflix Eureka ile service discovery (port 8761)
- Her servis kendi PostgreSQL veritabanına sahip (`product_db`, `inventory_db`, `order_db`)
- Kafka ile asenkron, publish-subscribe iletişim
- `api-gateway` üzerinde `AuthenticationFilter` (şimdilik mock token kontrolü)
- `product-service` üzerinde Resilience4j Circuit Breaker (`InventoryCircuitBreaker`)

### 2.2 Product → Inventory akışı

- `product-service` yeni ürün yaratıldığında `PRODUCT-CREATED-EVENTS` topic'ine yayın yapar
- Dual-write problemi **Outbox Pattern** ile çözüldü: ürün kaydı ve outbox satırı tek transaction'da yazılır, ayrı bir zamanlanmış görev Kafka'ya gönderir
- `inventory-service` bu topic'i dinler ve **idempotent** olarak tüketir (`eventId` kontrolü ile aynı olay iki kez işlenmez)

### 2.3 order-service'in inşası

- **Domain katmanı:** `Order`, `OrderRequest`, `ORDER_STATUS`, invariant kontrolleri ve domain exception'ları
- **Application katmanı:** `CreateOrderUseCase`, `OrderSuccessfullyCreatedUseCase`, `OrderOutOfStockUseCase` ve portlar
- **Outbox:** `OrderDto` (gerçek sipariş) ve `OrderOutboxDto` (outbox satırı) tek `@Transactional` blok içinde birlikte yazılıyor (`OrderCreationAdapter`), `OutboxPublisher` saniyede bir gönderilmemiş satırları Kafka'ya basıyor
- **Konfigürasyon:** datasource, Kafka consumer, Eureka, port 8082

### 2.4 Saga (Choreography) — uçtan uca çalışıyor

```
[HTTP POST /api/orders]
        │
        ▼
  order-service ──(outbox)──▶ topic: order-created
                                      │
                                      ▼
                             inventory-service
                             stok kontrolü + düşüm
                                      │
                    ┌─────────────────┴─────────────────┐
                    ▼                                   ▼
      topic: order-created-successfully      topic: order-out-of-stock
                    │                                   │
                    └─────────────────┬─────────────────┘
                                      ▼
                                order-service
                          sipariş durumu güncellenir
                         (COMPLETED / CANCELED)
```

Her iki yol da `order-service/request.http` üzerinden elle test edilerek doğrulandı.

### 2.5 Eşzamanlılık ve tutarlılık

- `ProductDto` üzerinde `@Version` ile optimistic locking
- `RepositoryAdapter.updateStock()` `saveAndFlush()` kullanıyor ve `ObjectOptimisticLockingFailureException`'ı `StockUpdateConflictException`'a çeviriyor (framework sızıntısını engellemek için)
- `OrderCreatedUseCase` çakışma durumunda 3 kez yeniden deniyor (stoğu tekrar okuyup yeniden hesaplayarak)
- **Persist-before-publish** sırası: önce stok kalıcı hale getiriliyor, sonra event yayınlanıyor

### 2.6 İsimlendirme ve katman düzeltmeleri

- order-service'te tutarlı bir şema oturtuldu: `*Port` (arayüz) / `*Adapter` (port implementasyonu) / `*JpaRepository` (Spring Data arayüzü). Önceki durumda üç farklı katmanda "JpaOrder...Adapter" adlı üç ayrı sınıf vardı.
- `RepositoryPort` `domain.port` → `application.port` taşındı (port bir use-case ihtiyacıdır, domain'in değil)
- `OutOfStockException` `application.exception` → `domain.exception` taşındı (domain, application'a bağımlı olmamalı)
- Dört serviste `application.usecase` → `application.usecases` normalizasyonu

### 2.7 Loglama ve AOP

- Ara adım olarak `Logger` portu + `ApplicationLogger` adapter'ı yazıldı (kaldırılması planlanıyor, bkz. 4.2)
- `inventory-service`'e `spring-boot-starter-aop` ve `LoggingAspect` eklendi
- Öğrenilen konular: pointcut söz dizimi, Spring AOP'nin proxy tabanlı çalışması, self-invocation tuzağı, around advice'ın `proceed()` sonucunu döndürme zorunluluğu

### 2.8 Şema yönetimi

- `product` tablosunda `version` sütunu eksikti (entity'ye `@Version` eklendiğinde tablo güncellenmemişti); `ALTER TABLE` ile eklendi
- `inventory-service`'e `ddl-auto: validate` eklendi → şema/entity uyuşmazlığı artık runtime'da değil, **açılışta** yakalanıyor

---

## 3. Güncel durum

### 3.1 Servis envanteri

| Servis | Spring Boot | Java | Port | Veritabanı | Kafka rolü |
|---|---|---|---|---|---|
| order-service | 4.1.1 | 25 | 8082 | order_db | Producer + Consumer |
| inventory-service | 3.3.4 | 21 | 8081 | inventory_db | Producer + Consumer |
| product-service | 3.3.4 | 21 | 9001 | product_db | Producer |
| api-gateway | 3.3.4 | 21 | 8080 | — | — |
| discovery-service | 3.3.4 | 21 | 8761 | — | — |

### 3.2 Kafka topic'leri

| Topic | Üreten | Tüketen |
|---|---|---|
| `PRODUCT-CREATED-EVENTS` | product-service | inventory-service |
| `order-created` | order-service | inventory-service |
| `order-created-successfully` | inventory-service | order-service |
| `order-out-of-stock` | inventory-service | order-service |

### 3.3 Derlenme durumu (doğrulandı)

| Servis | `mvnw -o compile` |
|---|---|
| order-service | ✅ |
| api-gateway | ✅ |
| discovery-service | ✅ |
| inventory-service | ❌ |
| product-service | ❌ |

**2026-09-12 mentorluk doğrulaması:** Maven 3.9.16, aktif JDK 25.0.4. Inventory ve product, Boot 3.3.4 üzerinden Lombok 1.18.34 alıyor; Java hedefleri 21. `sh mvnw -o compile` ile inventory'de `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN` yeniden üretildi. Product'ta açık annotation processor yapılandırması bulunmadığından önce eksik `log` alanları görülüyor; yalnızca deney komutuna `-Dmaven.compiler.proc=full` eklenince aynı Lombok başlatma hatası ortaya çıkıyor. Product için sürüm ve annotation processor yapılandırması ayrı ayrı ele alınmalı. Diğer üç servisin tablodaki sonuçları önceki kayıttır; bu mentorluk adımında yeniden derlenmediler.

---

## 4. Açık bulgular

### 4.1 Kritik — Saga'yı sessizce bozanlar

| # | Bulgu | Nerede |
|---|---|---|
| 1 | **`orderId` client'tan geliyor.** `save()` var olan id ile update yapar; aynı id ile ikinci POST eski siparişi sessizce ezer. Id sunucu tarafında üretilmeli. | `OrderController`, `OrderRequest`, `OrderDto` |
| 2 | **Çakışma tükendiğinde Saga çıkmaza giriyor.** 3. denemede fırlatılan `StockUpdateConflictException` dıştaki `catch (OutOfStockException)` tarafından yakalanmıyor, consumer'da yutuluyor; sipariş sonsuza dek `PENDING` kalıyor. Terminal state garantisi yok. | `OrderCreatedUseCase` |
| 3 | **Consumer tüm exception'ları yutuyor** (`System.err.print(e)`). Deserialization hatası da iş hatası da aynı deliğe gidiyor, offset commit ediliyor, mesaj kayboluyor. DLQ/retry yok. | `ReadOrderCreatedEvent` |
| 4 | **`order-created` tüketiminde idempotency yok.** Outbox at-least-once çalışır (ack dönmeden aynı satır tekrar gönderilebilir), dolayısıyla çift teslimat teorik değil; aynı mesaj iki kez gelirse stok iki kez düşer. Ürün tarafında `eventId` ile bu mekanizma var, order tarafında yok. | `ReadOrderCreatedEvent` |
| 5 | **Olmayan ürün ile stoğu biten ürün ayırt edilemiyor.** `getStock()` bulunamayan ürün için `0` dönüyor → sipariş "stok yok" diye iptal ediliyor. Ayrıca `updateStock()` ürün yoksa sessizce yeni ürün **yaratıyor**. | `RepositoryAdapter` |

### 4.2 Mimari

| # | Bulgu |
|---|---|
| 6 | **Sürüm kayması:** order-service Boot 4.1.1 / Java 25, diğer dört servis Boot 3.3.4 / Java 21. Bilinçli bir karar olmalı. |
| 7 | **`StockUpdateConflictException` `domain.exception`'da.** Bunu infrastructure fırlatıyor, application yakalıyor ve anlamı "optimistic locking çakışması" — yani teknik bir arıza modu, iş kuralı değil. Domain'e mi ait? (Aynı soru `IllegalEventIdempotent` için de geçerli.) **Karar verilmedi.** |
| 8 | **Adapter paketleri tutarsız:** order-service `infrastructure.adapters` + `infrastructure.persistence.adapter`, inventory-service `infrastructure.repository.adapter` + `infrastructure.kafka.adapter`. Ortak bir pointcut yazmayı imkânsız kılıyor. |
| 9 | **Bean factory'ler "lite mode":** `CreateApplicationBean` `@Component`, `BeanFactory` `@Component`. `@Configuration` olmalı — aksi halde bir `@Bean` metodundan diğeri çağrıldığında singleton garantisi yok. |
| 10 | **order-service'te aspect yok.** Elle yazılmış loglar silindi ama yerine aspect gelmedi; servis şu an gözlemlenebilirlikten yoksun. `Logger` portu ve use case'lerdeki manuel loglar da hâlâ duruyor (kaldırılması planlanıyor). |
| 11 | **api-gateway sadece product-service'e route ediyor.** order-service ve inventory-service gateway üzerinden erişilebilir değil; testler doğrudan 8082'ye gidiyor. |

### 4.3 İşletim ve kod kalitesi

| # | Bulgu |
|---|---|
| 12 | **Lombok/JDK uyumsuzluğu iki servisi derlenemez hâlde bırakıyor** (bkz. 3.3). Çok modüllü build'e geçmeden önce çözülmeli. |
| 13 | Outbox tablosundaki gönderilmiş satırlar hiç temizlenmiyor → tablo sonsuza dek büyüyor. |
| 14 | `kafkaTemplate.send(...)` future'ında `.exceptionally()` yok; gönderim hatası hiç loglanmıyor (satır `published` işaretlenmediği için retry doğru çalışıyor, ama sessiz). |
| 15 | `OrderCreatedEventPublisherAdapter`: `"...".formatted(getSimpleName(), dto)` — format string'de tek `%s`, iki argüman; `dto` loga hiç düşmüyor. |
| 16 | Veritabanı kullanıcı adı/parolası `application.yml` içinde açık metin ve repo'da versiyonlanmış. |
| 17 | `OrderController` `void` dönüyor (201/Location yok) ve domain exception'ları 500'e dönüşüyor. inventory-service'te `Rfc7808Service` (RFC 7807 Problem Details) varken order-service'te karşılığı yok. |
| 18 | Topic isimlendirmesi tutarsız: `PRODUCT-CREATED-EVENTS` (BÜYÜK-KEBAB) vs `order-created` (küçük-kebab). |
| 19 | `Order` validasyonunda `customerId.isEmpty()` null gelirse NPE atar; `orderId < 0` kontrolü 0'a izin veriyor ama mesaj "0'dan büyük olmalı" diyor. |

---

## 5. Yol haritası

### Faz 1 — Zemin temizliği (sıradaki iş)

1. Lombok sürümünü `1.18.46` olarak ez (inventory-service, product-service); product-service için açık annotation processor yapılandırmasını da ekle → beş servisin de komut satırından derlenmesini sağla. İlk mentorluk adımı inventory değişikliğini kullanıcının uygulaması; henüz tamamlanmadı.
2. Adapter paketlerini tek şemaya oturt (bulgu 8)
3. Bean factory'leri `@Configuration` yap (bulgu 9)
4. Bulgu 7'ye karar ver: `StockUpdateConflictException` hangi katmana ait?

### Faz 2 — Observability

5. **Ortak starter modülü:** `ecommerce-observability` adında bir Maven modülü — içinde `LoggingAspect`, `@NoLogging` anotasyonu ve bir `@AutoConfiguration` sınıfı; `AutoConfiguration.imports` ile kaydedilir. Bağımlılığı ekleyen her servis hiçbir kod yazmadan loglamayı kazanır. Kopyala-yapıştırın alternatifi budur.
   - Katmanlı pointcut haritası: giriş noktaları (controller, `@KafkaListener`) INFO; use case'ler INFO; dış adapter'lar DEBUG; domain hiç
   - Pointcut'ın gerçekten eşleştiğini doğrulayan ArchUnit testi (sessiz eşleşmeme tuzağına karşı)
6. `Logger` portunu kaldır, kalan iş logları için SLF4J'i doğrudan kullan
7. **Correlation ID'yi elle kur:** MDC + Kafka header'ları ile bir isteğin izini HTTP → Kafka → DB boyunca taşı
8. **Micrometer Tracing + OpenTelemetry + Jaeger/Tempo:** elle yazdığını endüstri standardıyla değiştir; bir siparişin tüm yolculuğunu tek bir waterfall diyagramında gör
9. Kafka topic'lerini gözlemlemek için Redpanda Console / AKHQ
10. Structured JSON loglama + Loki/Grafana ile trace id üzerinden servisler arası arama

### Faz 3 — Dayanıklılık

11. **Kafka error handling:** `DefaultErrorHandler` + retry/backoff + Dead Letter Topic (bulgu 2 ve 3'ün gerçek çözümü)
12. **Idempotent consumption** `order-created` için (bulgu 4)
13. **inventory-service'e Outbox:** persist-then-publish arasında çökme hâlinde mesaj kaybını engelle (order-service'te çözülen problemin aynısı)
14. Sunucu tarafında `orderId` üretimi (bulgu 1)
15. Outbox tablosu temizliği (bulgu 13)

### Faz 4 — Baştan beri planlanan konular

16. **Maven → Gradle geçişi** (kişisel öğrenme hedefi)
17. **Gradle convention plugin:** yeni servisleri doğrudan hedef mimaride iskeletleyen bir eklenti (Faz 2'deki starter modülü bunun Maven'daki ön provası)
18. **Hexagonal → Clean Architecture değerlendirmesi:** "hexagonal ile yazdık ama clean architecture daha uygun olurdu" fikrinin somut yapısal farklarla masaya yatırılması — bu soru hâlâ açık
19. **Gerçek JWT/OAuth2 kimlik doğrulama** (gateway'deki mock kontrolün yerine)
20. Resilience4j'nin daha derin kullanımı (bulkhead, rate limiter, retry politikaları)
21. Şema yönetimi için Flyway/Liquibase — `ddl-auto: validate` + versiyonlanmış migration script'leri
22. Test stratejisi: Testcontainers ile entegrasyon testleri, ArchUnit ile mimari testler

---

## 6. Kalıcı dersler

Bu bölüm proje boyunca tekrar eden hata kalıplarını ve varılan prensipleri tutar.

**Tekrar eden hata kalıpları**
- Yeni infrastructure sınıfları Spring bean anotasyonu olmadan geliyor (`@Component`/`@Service`) — order-service'te tek başına üç kez yaşandı
- `String.formatted()` yanlış kullanımı: yer tutucu ve argüman sayısı uyuşmuyor, veri sessizce kayboluyor. SLF4J'in `{}` yer tutucusu hem bu hatayı imkânsız kılar hem de log seviyesi kapalıyken string'i hiç kurmaz
- Sessiz arıza modları: yanlış pointcut hiç eşleşmez ama hata da vermez; yanlış paket adı derlenir ama beklenen yere gitmez

**Yerleşen prensipler**
- **Aspect şeffaf olmalıdır:** gözlemler, davranışı değiştirmez. Logla ve yeniden fırlat; `finally` içinde `return` etme
- **Mekanik izler aspect'e, iş anlamı taşıyan loglar koda.** Aspect metot adını ve argümanları verir, o çağrının ne anlama geldiğini veremez
- **Paket, sınıfın bir özelliğidir; klasör sadece dosyanın durduğu yer.** Pointcut'lar, import'lar ve tooling pakete bakar
- **Mimari tutarlılık estetik bir tercih değil, otomasyonun ön koşuludur.** Kesişen bir kural ancak tutarlı bir yapı üzerine kurulabilir
- **Hata, iş sonucuna dönüştürülmemelidir.** "Ürün bulunamadı"nın "stok yok"a dönüşmesi gibi
- Şema uyuşmazlığı runtime'da değil açılışta yakalanmalı (`ddl-auto: validate`)
