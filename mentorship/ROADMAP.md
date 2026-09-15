# E-Ticaret Mikroservis Projesi — Hedef Mimari ve Yol Haritası

> Son güncelleme: 2026-09-15 (sürüm 2 — kapsam genişletildi)
>
> Bu dosya projenin **nereye gittiğini** anlatır: hedef mimari, servis sınırları, API yüzeyi ve faz sırası.
> Çalışma anlaşması ve öğretim yöntemi [MENTORSHIP.md](MENTORSHIP.md) içindedir.
> [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) tarihsel bağlamdır (1 Eylül), güncel sayma.
>
> Bu sürümdeki "mevcut durum" bilgileri 2026-09-15'te koda ve çalışan container'lara bakılarak doğrulandı.

---

## 1. Programın amacı

İki hedef aynı anda yürür:

1. **Gerçekçi bir e-ticaret backend'i** — müşteri yolculuğunun (katalog → sepet → sipariş → ödeme → kargo → bildirim → iade) anlamlı bir bölümünü gerçekten çalıştıran bir sistem. Oyuncak CRUD değil; para, stok rezervasyonu, dış sağlayıcı arızası ve geç gelen webhook gibi gerçek problemleri olan bir sistem.
2. **Senior Java backend yetkinliği** — bu sistemi kurarken alınan kararları, uygulanan mekanizmaları ve yaşanan arızaları bir görüşmede kendi deneyimi olarak anlatabilmek.

Ölçüt şudur: *"projede var"* yeterli değil. Kanıt, aradan zaman geçtikten sonra **kodu kopyalamadan yeniden kurabilmek** ve **bedelini savunabilmektir**. Yetkinlik düzeyleri ve hatırlama ritmi MENTORSHIP.md bölüm 3-4'tedir.

---

## 2. Mevcut durum — 2026-09-15 (doğrulanmış)

### 2.1 Servisler

| Servis | Port | Boot / Java | Veritabanı | Dış dünyaya açık API |
|---|---|---|---|---|
| api-gateway | 8080 | 4.1.1 / 25 | — | yalnızca `/api/products/**` route'u |
| discovery-service (Eureka) | 8761 | 4.1.1 / 25 | — | — |
| product-service | 9001 | 4.1.1 / 25 | product_db | 2 endpoint |
| inventory-service | 8081 | 4.1.1 / 25 | inventory_db | **0 endpoint** (yalnızca Kafka) |
| order-service | 8082 | 4.1.1 / 25 | order_db | 1 endpoint |

Sürüm kayması bitti: beş servis de Boot 4.1.1 / JDK 25. Eski Lombok/JDK derleme engeli geçmişte kaldı.

### 2.2 Bütün API yüzeyi — toplam 3 endpoint

```
POST /products          product-service
GET  /products/{id}     product-service   (+ Feign ile inventory'den stok, CB arkasında)
POST /api/orders        order-service
```

Bu, projenin en büyük gerçekçilik açığıdır. Sepet yok, ödeme yok, sipariş listeleme yok, iptal yok, kimlik yok.

### 2.3 Çalışan akışlar

- **Product → Inventory:** product-service outbox ile `PRODUCT-CREATED-EVENTS` yayınlar, inventory idempotent tüketir.
- **Saga (choreography), uçtan uca:** `POST /api/orders` → outbox → `order-created` → inventory stok düşer → `order-created-successfully` / `order-out-of-stock` → order-service durumu günceller. Kullanıcı tarafından elle doğrulandı.
- **Eşzamanlılık:** `ProductDto` üzerinde `@Version`; `StockUpdateConflictException`; retry decorator (3 deneme) transaction decorator'ının dışında.
- **Inventory çağrı zinciri:** listener → retry decorator → Spring transaction proxy → transactional decorator → `OrderCreatedUseCase`. `@Primary` + `@Qualifier` ile bağlandı.

### 2.4 Altyapıda duran ama kullanılmayanlar

- **Redis** container (`ecomm-redis`, 6379) ayakta ama **hiçbir servis kullanmıyor** — kodda tek bir referans yok.
- **Resilience4j** yalnızca product-service'te, tek `@CircuitBreaker` anotasyonu (`InventoryCircuitBreaker`, paketi `infrastructure.exception` — yanlış yer). Fallback servis arızasını "stok 0" diye sunuyor; bu yanlış fallback kalıbının kendisi bir ders konusu.
- **api-gateway** yalnızca product'a route ediyor; `AuthenticationFilter` sabit token karşılaştırması yapıyor.

### 2.5 Şema yönetimi

Migration altyapısı yok. Tablolar elle `CREATE TABLE` / `ALTER TABLE` ile açılıyor; `ddl-auto: validate` sayesinde uyuşmazlık açılışta yakalanıyor ama düzeltme elle yapılıyor. **15 Eylül akşamı iki serviste peş peşe bu yüzden açılış hatası alındı** (inventory'de eksik tablo, order'da entity'nin beklediği sequence'in olmaması). Flyway artık "ileride" değil, Faz 1 işi.

---

## 3. Hedef mimari

### 3.1 Servis envanteri ve gerekçeleri

Her yeni özellik ayrı servis olmak zorunda değildir. Aşağıdaki tablo her kutuyu **bounded context, veri sahipliği, bağımsız yaşam döngüsü ve öğrettiği mekanizma** ile gerekçelendirir.

| Servis | Durum | Veri sahipliği | Neden ayrı servis | Öğrettiği ana konu |
|---|---|---|---|---|
| **product-service** (katalog) | var | Ürün, SKU, fiyat, kategori | Farklı okuma profili (çok okuma/az yazma), bağımsız ölçeklenir | Cache-aside, pagination, gRPC toplu sorgu |
| **inventory-service** | var | Stok, **rezervasyon** | Katalogdan farklı tutarlılık ihtiyacı; stok yazma yoğun ve çakışmalı | Optimistic locking, rezervasyon + TTL, reconciliation |
| **order-service** | var | Sipariş, sipariş satırı, tutar snapshot'ı | Sipariş yaşam döngüsünün sahibi; saga'nın başlatıcısı | Saga, outbox, idempotent checkout, durum makinesi |
| **payment-service** | **yeni — Faz 3** | Ödeme niyeti, authorization, capture, refund | Para; ayrı güvenlik/denetim sınırı, PSP'ye tek bağlanma noktası | Idempotency key, webhook out-of-order, compensation, Resilience4j'nin gerçek kullanımı |
| **notification-service** | **yeni — Faz 4** | Gönderim kaydı, şablon | Tamamen asenkron, hiçbir senkron çağrının yolunda değil; ayrı ölçeklenir | **RabbitMQ iş kuyruğu**, DLX, manual ack, prefetch |
| **cart-service** | **yeni — Faz 5** | Sepet (Redis) | Kısa ömürlü, yüksek trafikli, ilişkisel DB'ye ait olmayan veri | Redis'i **birincil depo** olarak kullanmak, TTL, checkout'ta fiyat doğrulama |
| **identity (Keycloak + resource server)** | **yeni — Faz 6** | Kullanıcı, rol, token | Hazır OIDC sağlayıcı kullanılacak; kendi JWT'sini yazmak yanlış dersi öğretir | OAuth2/OIDC, Spring Security resource server, sahiplik kontrolü |
| **shipping-service** | **yeni — Faz 7** | Sevkiyat, takip numarası, teslim durumu | Uzun süren süreç; dış kargo firması entegrasyonu | Saga'nın ödemeden sonraki devamı, durum makinesi, geç event |
| search / review / recommendation | **kapsam dışı** | — | Yüksek altyapı maliyeti, düşük yeni ders değeri | (gerekirse en sonda laboratuvar) |

**Ayrı servis yapılmayacaklar — bilinçli karar:**

- **Fiyatlandırma/kampanya:** product-service içinde ayrı bir modül/bounded context olarak kalır. Ayrı servis, ders değeri eklemeden bir dağıtık çağrı daha ekler. gRPC dersi de burada verilecek (toplu fiyat sorgusu).
- **İade (return/refund):** ayrı servis değil; order + payment + shipping arasında bir **saga** olarak modellenir. Zaten öğretmek istediğimiz şey tam olarak budur.

### 3.2 Teknoloji yerleşimi — hangisi nerede ve neden

Bunlar aynı sınıftan şeyler değil; "protokoller" diye tek torbaya koyma.

| Teknoloji | Nerede kullanılacak | Neden orada |
|---|---|---|
| **REST** | Gateway arkasındaki tüm dış API | İstemci çeşitliliği, cache edilebilirlik, HTTP semantiği |
| **Kafka** | Servisler arası **event backbone**: `order-created`, `stock-reserved`, `payment-authorized`, `order-shipped` | Kalıcı log, replay, çoklu bağımsız tüketici, partition ile sıralama |
| **RabbitMQ** | notification-service **iş kuyruğu** | Rekabet eden tüketiciler, per-message ack, DLX ile zehirli mesaj, prefetch ile akış kontrolü. Kafka'nın log semantiğiyle **karşılaştırmalı** öğretilecek |
| **gRPC** | order → product toplu fiyat/ürün sorgusu (checkout'ta N ürün) | Tipli sözleşme, tek çağrıda toplu veri, deadline propagation. Mevcut Feign `InventoryClient` ile karşılaştırılacak |
| **Redis** | 1) cart-service birincil depo, 2) katalog cache-aside, 3) gateway rate limiter | Üç farklı kullanım, üç farklı ders (TTL/stampede/token bucket) |
| **Resilience4j** | payment-service → PSP adapter'ı | Gerçek dış bağımlılık: timeout bütçesi, retry+jitter, CB, bulkhead. Mevcut yanlış fallback burada düzeltilecek |
| **Keycloak** | OIDC sağlayıcı | Gerçek token akışı; servisler resource server olur |
| **Kubernetes** | Faz 10'da Eureka'nın yerine | Service/DNS ile keşif, probe, rollout, HPA. **İş tutarlılığı çözümü değildir** |

### 3.3 Hedef Kafka topic'leri

| Topic | Üreten | Tüketen | Anahtar |
|---|---|---|---|
| `product-created` | product | inventory | productId |
| `order-created` | order | inventory, notification | orderId |
| `stock-reserved` | inventory | order, payment | orderId |
| `stock-rejected` | inventory | order, notification | orderId |
| `payment-authorized` | payment | order, inventory, shipping | orderId |
| `payment-failed` | payment | order, inventory, notification | orderId |
| `order-shipped` | shipping | order, notification | orderId |
| `order-cancelled` | order | inventory, payment, notification | orderId |

Anahtar olarak `orderId` kullanılması tesadüf değil: aynı siparişin event'lerinin aynı partition'da, dolayısıyla **sıralı** işlenmesini sağlar. Bu bir ders konusudur.

Mevcut `PRODUCT-CREATED-EVENTS` (BÜYÜK-KEBAB) adı küçük-kebab'a normalize edilecek.

---

## 4. Hedef API yüzeyi

Her endpoint bir ders taşır — sağ sütun o dersi gösterir. Hepsi aynı anda yazılmayacak; ilgili fazda gelecek.

### 4.1 Katalog — product-service

| Endpoint | Ders |
|---|---|
| `POST /products` | Validation, Problem Details (RFC 7807), 201 + Location |
| `GET /products/{id}` | ETag / optimistic concurrency, cache-aside |
| `GET /products?page&size&category&q` | Pagination, projection, N+1'den kaçınma |
| `PATCH /products/{id}` | Kısmi güncelleme, `If-Match` ile çakışma kontrolü |
| `POST /products/{id}/price` | Fiyat değişikliği event'i; sipariş tutarının snapshot olması gerektiği dersi |
| *(gRPC)* `CatalogService.GetProducts(ids)` | Toplu iç sorgu, deadline |

### 4.2 Stok — inventory-service

| Endpoint | Ders |
|---|---|
| `GET /inventory/{sku}` | "Ürün yok" ile "stok 0" farkı (mevcut bulgu 5) |
| `POST /inventory/reservations` | **Rezervasyon modeli** — iki fazlı iş akışı |
| `POST /inventory/reservations/{id}/commit` | Ödeme başarılı → rezervasyonu kesinleştir |
| `DELETE /inventory/reservations/{id}` | Telafi (compensation) işlemi |
| `POST /inventory/{sku}/adjust` | Admin yetkisi, denetim kaydı |

### 4.3 Sepet — cart-service

| Endpoint | Ders |
|---|---|
| `POST /carts` | Misafir vs kullanıcı sepeti, Redis TTL |
| `GET /carts/{id}` | Bayat fiyat problemi |
| `POST /carts/{id}/items` | Idempotent ekleme |
| `PATCH /carts/{id}/items/{sku}` / `DELETE` | Kısmi güncelleme |
| `POST /carts/{id}/checkout` | **Checkout'ta fiyat/stok yeniden doğrulama** — sepetteki fiyata güvenilmez |

### 4.4 Sipariş — order-service

| Endpoint | Ders |
|---|---|
| `POST /orders` + `Idempotency-Key` header | **Idempotent checkout** — istemci retry'ı ikinci sipariş yaratmamalı (mevcut bulgu 1'in gerçek çözümü) |
| `GET /orders/{id}` | Async işi senkron GET ile takip ettirme |
| `GET /orders?page&size&status` | Sahiplik kontrolü: başkasının siparişini okuyamama (403) |
| `POST /orders/{id}/cancel` | Saga telafi zinciri: stok iade + ödeme iadesi |
| `GET /orders/{id}/timeline` | **CQRS projection** — saga adımlarının okunabilir hâli |

### 4.5 Ödeme — payment-service

| Endpoint | Ders |
|---|---|
| `POST /payments` (authorize) | Idempotency key, para tipi (`BigDecimal`/Money, asla `double`) |
| `POST /payments/{id}/capture` | Authorization ile capture ayrımı |
| `POST /payments/{id}/refund` | Telafi işlemi, kısmi iade |
| `GET /payments/{id}` | Durum sorgulama |
| `POST /webhooks/psp` | **Geç ve sırasız gelen callback** — iptal edilmiş siparişe gelen ödeme onayı |

### 4.6 Kimlik — Keycloak + servisler

| Endpoint | Ders |
|---|---|
| Keycloak token endpoint | OAuth2 password/authorization code akışı |
| `GET /me` | JWT claim'lerinden kullanıcı |
| `GET/POST /me/addresses` | Sahiplik kontrolü |

Gateway tarafı: tüm route'lar, rate limiting, timeout bütçesi, CORS, 401 vs 403 ayrımı.

---

## 5. Fazlar

> Sıra keyfi değil: her faz bir öncekinin açtığı kapıyı kullanır. Rezervasyon olmadan ödeme anlamsız; ödeme olmadan bildirim ve iade senaryosu yok.
>
> **Faz 6 (kimlik/gateway) bağımsızdır** — tempo değiştirmek istendiğinde araya alınabilir.

### Faz 0 — Aktif iş: stok akışını sağlamlaştır

Devam eden konu. MENTORSHIP.md bölüm 5'teki "sıradaki somut ders" burada.

1. **Atomik idempotency:** `OrderRepositoryPort.tryRegisterEvent(int eventId)` — `INSERT ... ON CONFLICT DO NOTHING`, etkilenen satır sayısı → boolean. Mevcut `findById` + `save` check-then-act yarışını kapatır.
2. Sonuç kalıcılığı: başarı / yetersiz stok sonuçlarının yazılması.
3. **inventory-service'e outbox** — persist-then-publish arasındaki çökmede mesaj kaybını kapatır (order-service'te çözülen problemin aynısı).
4. **Kafka error handling:** `DefaultErrorHandler` + backoff + Dead Letter Topic. Şu anki "tüm exception'ları yut" davranışını bitirir (bulgu 2, 3).

### Faz 1 — Zemin: şema, sözleşme, bean hijyeni

5. **Flyway** — beş serviste versiyonlanmış migration. `ddl-auto: validate` korunur. *(15 Eylül'deki iki açılış hatasının kalıcı çözümü.)*
6. Sunucu tarafında `orderId` üretimi + `Idempotency-Key` desteği (bulgu 1).
7. Tüm servislerde RFC 7807 Problem Details; `OrderController` `void` dönmeyi bırakır, 201 + Location (bulgu 17).
8. Bean factory'ler `@Configuration` olur (bulgu 9); adapter paketleri ortak şemaya oturur (bulgu 8).
9. `StockUpdateConflictException` hangi katmana ait — karara bağlanır (bulgu 7).
10. Outbox tablosu temizliği (bulgu 13); `kafkaTemplate.send` hata loglaması (bulgu 14).

### Faz 2 — Stok rezervasyonu (gerçekçilik sıçraması)

11. Stok **düşürme** yerine **rezervasyon** modeli: `reserve` (TTL ile) → `commit` / `release`.
12. Süresi dolan rezervasyonların toplanması: zamanlanmış reconciliation işi.
13. Saga'nın yeniden modellenmesi: `order-created` → `stock-reserved` → (ödeme) → `commit`.

**Müşteri senaryosu:** *Son ürün için iki müşteri aynı anda ödeme ekranında. Biri ödemeyi yarıda bırakıyor. Stok ne zaman serbest kalır?*

### Faz 3 — payment-service + saga orchestration

14. Yeni servis: ödeme niyeti, authorize/capture/refund, kendi DB'si, kendi outbox'ı.
15. Sahte PSP adapter'ı (gecikme ve arıza enjekte edilebilir) + **Resilience4j doğru kullanım**: timeout bütçesi → retry (jitter'lı) → circuit breaker → bulkhead. Mevcut yanlış fallback düzeltilir.
16. **Idempotency key** ile çift çekim önleme.
17. **Webhook:** geç, sırasız, tekrarlı callback'lerin işlenmesi.
18. **Orchestration saga** — mevcut choreography ile karşılaştırmalı. Sipariş iptali/iade zinciri burada kurulur.

**Müşteri senaryosu:** *Ödeme yanıtı gelmedi ama para çekilmiş olabilir. Sipariş ne durumda kalmalı?*

### Faz 4 — notification-service + RabbitMQ

19. Yeni servis: RabbitMQ tüketicisi, e-posta/SMS gönderim kaydı, şablon.
20. Exchange/routing key tasarımı, **prefetch**, **manual ack**, publisher confirms.
21. **DLX** ile zehirli mesaj; requeue döngüsünün teşhisi.
22. Kafka ile RabbitMQ'nun aynı problem üzerinden karşılaştırılması: neden bildirim Kafka topic'i değil de kuyruk?

### Faz 5 — cart-service + Redis

23. Yeni servis: Redis birincil depo, TTL, misafir/kullanıcı sepeti birleştirme.
24. `POST /carts/{id}/checkout` → fiyat ve stok yeniden doğrulama → order-service'e devir.
25. Katalogda **cache-aside**: TTL, invalidation, cache stampede.

### Faz 6 — Kimlik ve gerçek gateway *(araya alınabilir)*

26. Keycloak; servisler OAuth2 resource server olur; mock token filtresi kaldırılır.
27. Sahiplik/rol kontrolü: 401 ile 403 ayrımı; başkasının siparişini okuyamama.
28. Gateway olgunlaşır: tüm route'lar, **Redis tabanlı rate limiting**, timeout bütçesi, CORS, hata normalizasyonu.

### Faz 7 — shipping-service + gRPC

29. Yeni servis: sevkiyat oluşturma, takip, teslim event'i; saga'nın ödeme sonrası devamı.
30. **gRPC**: order → product toplu ürün/fiyat sorgusu. Aynı application portuna hem REST hem gRPC adapter'ı bağlanır — hexagonal'in asıl sınavı budur.
31. Deadline propagation, status kodları, sürüm uyumu.

### Faz 8 — Observability (uçtan uca)

32. Ortak `ecommerce-observability` starter modülü: `LoggingAspect`, `@NoLogging`, `@AutoConfiguration`.
33. Correlation ID elle: MDC + Kafka/AMQP header'ları.
34. Micrometer Tracing + OpenTelemetry + Jaeger/Tempo — elle yazılanın yerine.
35. Structured JSON log + Loki/Grafana; Kafka için Redpanda Console/AKHQ.
36. SLI/SLO: outbox lag, consumer lag, ödeme başarı oranı.

### Faz 9 — Hexagonal → Clean Architecture geçişi

37. Önce **somut yapısal fark** listesi çıkarılır: içeri yönelen bağımlılıklar, use case giriş/çıkış modelleri, interface adapter katmanı, composition root sınırı. Paket adı değiştirmek geçiş sayılmaz.
38. Tek bir servis üzerinde uygulanır (aday: payment-service, en yeni ve en temiz), sonra diğerlerine yayılır.
39. **ArchUnit** ile bağımlılık kuralları teste bağlanır.

### Faz 10 — Maven → Gradle + Kubernetes

40. Gradle Kotlin DSL, toolchain, dependency scope, BOM, annotation processor, bootJar, CI eşdeğerliği.
41. Version catalog + **convention plugin** — yeni servisler hedef mimaride doğar.
42. Container image'ları; Kubernetes Deployment/Service/Ingress; **Eureka yerine Service/DNS**; probe, rollout, HPA, ConfigMap/Secret, graceful shutdown.

### Faz 11 — İleri EDA laboratuvarı

43. Polling outbox ile **CDC/Debezium** karşılaştırması.
44. Sınırlı **event sourcing** denemesi (tek aggregate): event store ile outbox farkı, rebuild, schema evolution maliyeti.
45. `GET /orders/{id}/timeline` için CQRS projection.

---

## 6. Sürekli yürüyen işler

Bunlar bir faza ait değil, her fazın içinde yürür:

- **Test stratejisi:** her fazda o fazın kritik davranışı için az sayıda anlamlı test — Testcontainers (DB/Kafka/Rabbit), ArchUnit (mimari kurallar), contract test (OpenAPI/protobuf). Uzun test listeleri dersin yerine geçmez.
- **Aralıklı hatırlama:** oturum başında bir eski konu sorusu; 2-3 oturum sonra aynı kavramı başka problemde uygulama; dönüm noktalarında kodu kopyalamadan yeniden kurma. Ritim MENTORSHIP.md bölüm 3'te.
- **Resilience4j özel tekrar planı:** MENTORSHIP.md bölüm 3'te — kullanıcı bu konuyu hatırlamadığını bildirdi, Faz 3'te sıfırdan yeniden kurulacak.
- **Senior görüşme provası:** dönüm noktalarında 10-15 dakikalık tasarım/arıza değerlendirmesi.
- **Güvenlik hijyeni:** DB parolaları şu an `application.yml` içinde açık metin ve repo'da versiyonlu (bulgu 16) — Faz 6/10'da config/secret yönetimine bağlanır.

---

## 7. Açık bulgular

Faz planına bağlanmış hâlleri parantez içinde.

### 7.1 Kritik — Saga'yı sessizce bozanlar

| # | Bulgu | Nerede | Plan |
|---|---|---|---|
| 1 | `orderId` client'tan geliyor; aynı id ile ikinci POST eski siparişi sessizce ezer | `OrderController`, `OrderDto` | Faz 1.6 |
| 2 | Çakışma tükendiğinde `StockUpdateConflictException` dıştaki `catch (OutOfStockException)` tarafından yakalanmıyor; sipariş sonsuza dek `PENDING` | `OrderCreatedUseCase` | Faz 0.4 |
| 3 | Consumer tüm exception'ları yutuyor (`System.err.print`); offset commit ediliyor, mesaj kayboluyor | `ReadOrderCreatedEvent` | Faz 0.4 |
| 4 | `order-created` tüketiminde idempotency yok; çift teslimatta stok iki kez düşer | `ReadOrderCreatedEvent` | Faz 0.1 |
| 5 | Olmayan ürün ile stoğu biten ürün ayırt edilemiyor; `updateStock()` ürün yoksa sessizce yeni ürün yaratıyor | `RepositoryAdapter` | Faz 2 |

### 7.2 Mimari

| # | Bulgu | Plan |
|---|---|---|
| 7 | `StockUpdateConflictException` `domain.exception`'da ama teknik bir arıza modu — hangi katman? **Karar verilmedi** | Faz 1.9 |
| 8 | Adapter paketleri servisler arasında tutarsız; ortak pointcut yazmayı imkânsız kılıyor | Faz 1.8 |
| 9 | Bean factory'ler "lite mode" (`@Component`); `@Configuration` olmalı | Faz 1.8 |
| 10 | order-service'te aspect yok; elle loglar silindi, yerine bir şey gelmedi | Faz 8.32 |
| 11 | api-gateway yalnızca product-service'e route ediyor | Faz 6.28 |
| 20 | Resilience4j `infrastructure.exception` paketinde ve fallback arızayı "stok 0" diye sunuyor | Faz 3.15 |
| 21 | Redis container ayakta ama hiçbir yerde kullanılmıyor | Faz 5 |

### 7.3 İşletim ve kod kalitesi

| # | Bulgu | Plan |
|---|---|---|
| 13 | Outbox tablosundaki gönderilmiş satırlar hiç temizlenmiyor | Faz 1.10 |
| 14 | `kafkaTemplate.send(...)` future'ında `.exceptionally()` yok; gönderim hatası sessiz | Faz 1.10 |
| 15 | `OrderCreatedEventPublisherAdapter`: format string'de tek `%s`, iki argüman — `dto` loga düşmüyor | Faz 1 |
| 16 | DB kullanıcı adı/parolası `application.yml` içinde açık metin ve repo'da versiyonlu | Faz 6/10 |
| 17 | `OrderController` `void` dönüyor; domain exception'ları 500'e dönüşüyor | Faz 1.7 |
| 18 | Topic isimlendirmesi tutarsız: `PRODUCT-CREATED-EVENTS` vs `order-created` | Faz 1 |
| 19 | `Order` validasyonunda `customerId.isEmpty()` NPE riski; `orderId < 0` kontrolü 0'a izin veriyor | Faz 1 |
| 22 | Migration altyapısı yok; şema elle yönetiliyor | Faz 1.5 |

**Çözülenler:** bulgu 6 (sürüm kayması) ve 12 (Lombok/JDK) — beş servis de Boot 4.1.1 / JDK 25, derleniyor.

---

## 8. Kalıcı dersler

Proje boyunca tekrar eden hata kalıpları ve varılan prensipler.

**Tekrar eden hata kalıpları**

- Yeni infrastructure sınıfları Spring bean anotasyonu olmadan geliyor — order-service'te tek başına üç kez.
- `String.formatted()` yanlış kullanımı: yer tutucu/argüman sayısı uyuşmuyor, veri sessizce kayboluyor. SLF4J'in `{}` biçimi bu hatayı imkânsız kılar.
- **Sessiz arıza modları:** yanlış pointcut hiç eşleşmez ama hata da vermez; yanlış fallback arızayı geçerli iş sonucuna çevirir; proxy'lenmemiş nesnedeki `@Transactional` hiçbir şey yapmaz.
- **Kod ile şema birbirinden kayıyor:** entity değişiyor, tablo değişmiyor (veya tersi). 15 Eylül'de iki serviste peş peşe yaşandı.

**Yerleşen prensipler**

- **Aspect şeffaf olmalıdır:** gözlemler, davranışı değiştirmez. Logla ve yeniden fırlat.
- **Mekanik izler aspect'e, iş anlamı taşıyan loglar koda.**
- **Paket, sınıfın bir özelliğidir;** klasör sadece dosyanın durduğu yer.
- **Mimari tutarlılık estetik değil, otomasyonun ön koşuludur.**
- **Hata, iş sonucuna dönüştürülmemelidir.** "Ürün bulunamadı"nın "stok yok"a dönüşmesi gibi.
- **Şema uyuşmazlığı runtime'da değil açılışta yakalanmalı** (`ddl-auto: validate`) — ve düzeltmesi elle değil, migration ile yapılmalı.
- **Decorator'da parametre sardığın katmandır;** `@Primary` dışarıya, `@Qualifier` içeriye bakar. `new` ile kurulan nesne container'a uğramaz, proxy'lenmez.

---

## 9. Sözlük

Bu belgelerde geçen kısaltmalar. Farklı asistanlar ve platformlar arasında devir yapılırken belirsizlik kalmasın diye burada toplandı.

| Kısaltma | Açılımı | Bu projede ne demek |
|---|---|---|
| **PSP** | Payment Service Provider | Ödeme sağlayıcı (Stripe/iyzico benzeri). Faz 3'te sahte bir adapter ile taklit edilecek |
| **CB** | Circuit Breaker | Resilience4j devre kesici. Mevcut tek kullanım: product-service `InventoryCircuitBreaker` |
| **DLT** | Dead Letter Topic | Kafka'da işlenemeyen mesajların atıldığı ayrı topic |
| **DLX** | Dead Letter Exchange | RabbitMQ karşılığı; reddedilen/süresi dolan mesajları yönlendirir |
| **TTL** | Time To Live | Bir kaydın kendiliğinden geçersizleşme süresi (rezervasyon, sepet, cache) |
| **CDC** | Change Data Capture | DB transaction log'unu okuyarak event üretme (Debezium). Faz 11'de polling outbox ile karşılaştırılacak |
| **CQRS** | Command Query Responsibility Segregation | Yazma modeli ile okuma modelini ayırma. Burada: `GET /orders/{id}/timeline` için ayrı projection |
| **MDC** | Mapped Diagnostic Context | SLF4J'nin thread'e bağlı log bağlamı; correlation ID taşımak için |
| **SLI / SLO** | Service Level Indicator / Objective | Ölçülen gösterge ve hedefi (ör. outbox gecikmesi < 5 sn) |
| **HPA** | Horizontal Pod Autoscaler | Kubernetes'te yüke göre pod sayısını ayarlayan mekanizma |
| **OIDC** | OpenID Connect | OAuth2 üzerine kurulu kimlik katmanı; Keycloak bunu sağlayacak |
| **IAM** | Identity and Access Management | Kimlik ve yetki yönetimi |
| **SKU** | Stock Keeping Unit | Stok takip birimi; bir ürün varyantının benzersiz kodu |
| **ETag** | Entity Tag | Kaynağın sürümünü taşıyan HTTP başlığı; `If-Match` ile çakışma kontrolü |
| **BOM** | Bill of Materials | Bağımlılık sürümlerini merkezî yöneten Maven/Gradle yapısı |
| **DSL** | Domain Specific Language | Burada: Gradle'ın Kotlin tabanlı yapılandırma dili |
| **SPOF** | Single Point of Failure | Tek arıza noktası; choreography tercihinin gerekçelerinden biri |
| **DDD** | Domain-Driven Design | Alan odaklı tasarım; bounded context kavramının kaynağı |
| **RFC 7807** | Problem Details for HTTP APIs | Standart hata gövdesi biçimi |
