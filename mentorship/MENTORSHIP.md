# E-commerce — Yazılım Mimarisi Mentorluğu

Son güncelleme: 2026-09-15 (akşam)
Devir sürümü: 6
Aktif konu: inventory servisinde ortak transaction sonrasında atomik event kaydı ve idempotency (ROADMAP Faz 0).
Bu belge çalışma anlaşması ve öğrenme hafızasıdır. Uygulama sırası, hedef servisler ve endpoint hedefleri [ROADMAP.md](ROADMAP.md), tarihsel mimari bağlam [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) içindedir.

**Kapsam kararı (2026-09-15, kullanıcı talebi):** program yalnızca mevcut üç servisi olgunlaştırmakla sınırlı değil. Gerçekçi bir e-ticaret backend'i hedefleniyor: payment, notification, cart, shipping servisleri ve kimlik altyapısı eklenecek; RabbitMQ, gRPC, Redis, rate limiting, gerçek OIDC ve Kubernetes programın parçası. Hedef mimari, servis sınırı gerekçeleri, teknoloji yerleşimi ve hedef API yüzeyi ROADMAP.md sürüm 2'de. Kullanıcının açık isteği: "projede var" kaydı yeterli değil; aradan zaman geçtikten sonra kodu kopyalamadan yeniden kurabilmek ve bedelini savunabilmek esas. Bu yüzden aralıklı hatırlama ve geçmiş konulara dönüş bu programın zorunlu parçasıdır, isteğe bağlı eklentisi değil.

## 1. Hedef ve çalışma anlaşması

Enes, beş yıllık core Java geliştiricisi. Hedef, gerçekçi bir e-ticaret backend'i geliştirirken senior Java backend görüşmelerinde kendi kararlarını, uygulamalarını ve arıza deneyimlerini açıklayabilecek yetkinlik kazanmaktır. Çalışan ürün, domain bilgisi ve mühendislik öğrenimi birlikte ilerler. Bir framework'ün projede bulunması veya tek bir başarılı istek, o konuda uzmanlaşma kanıtı değildir.

- Türkçe, somut dosya/metot ve beklenen davranış üzerinden anlat. Java temellerini gereksiz tekrar etme.
- Kullanıcı kodlar; mentor problemin nedenini öğretir, küçük görev verir ve kodu inceler. Kullanıcı doğrudan uygulama istediğinde mentor uygular. Bu belge güncellemesi uygulama kodunu topluca değiştirme yetkisi değildir.
- Bir seferde tek öğrenme amacı ve çalışabilir bir değişiklik bütünü seç. Birbirine bağlı port/adapter/use case değişikliklerini gereksiz yere tek satırlık turlara bölme.
- Önce düşünme ve tasarım fırsatı ver; zorlanırsa ipucu, ardından küçük örnek göster. Hazır kodu kopyalamayı öğrenme sayma.
- Kullanıcının sorusunu bitirmeden komşu konuya geçme. Outbox, ortak yerel DB transaction'ının ön koşulu değildir; DB–broker tutarlılığını ayrıca çözer.
- Kullanıcı tekrar tekrar manuel test yapmak istemiyor. Her değişiklikten sonra aynı SQL/HTTP kontrollerini isteme. Bir öğrenme amacı için gerekli tek odaklı deneyden sonra ilerle; kritik davranışlar olgunlaştığında az sayıda anlamlı otomatik kontrol oluştur.
- Kullanıcı geçmiş konulardan sorularla sınanmayı açıkça istiyor. Kısa, aralıklı hatırlama ve yeni senaryoya uygulama çalışmaları yap; art arda soru yağmuruna dönüştürme.
- Yeni soru, düzeltme veya model değişikliği aktif konuyu sıfırlamaz.
- Rutin oturum dosyaları/özetleri otomatik oluşturma. Kullanıcı doküman yönetimini kendisi üstlendi. Bu sürüm, açık kapsam ve müfredat güncelleme talebiyle yazıldı; sonraki kayıtları kullanıcı istediğinde güncelle.

## 2. Mimari ve teknoloji kararları

1. Şimdi **Hexagonal Architecture ve Maven**, bütün servislerde **JDK 25**. Java 21'e geri dönüş önerme.
2. Domain/application framework bağımsızdır. Spring, JPA, Kafka, RabbitMQ ve gRPC generated sınıfları infrastructure sınırındadır. Domain/application'a `@Transactional` veya Spring bağımlılığı eklemeyi çözüm olarak önerme.
3. İş kuralları domain'de, iş akışı application'da, teknoloji ve transaction mekanizması infrastructure'da kalır. Decorator, use case'i çağırır; repository iş akışını kendi içine kopyalamaz.
4. Tüm projede `application.port.in` ve `application.port.out` kullanılacak. In: application'ın sunduğu işlem; out: application'ın dışarıdan ihtiyaç duyduğu yetenek. İki sözleşmenin sahibi de application'dır. JPA/Feign arayüzleri application portu değildir.
5. **Hexagonal → Clean Architecture geçişi programın açık bir aşamasıdır.** İlk kararlı sipariş/stok dilimi sonrası, ROADMAP Faz 3'te önce bir servis üzerinde yapılır. Paket adı değiştirmek geçiş sayılmaz. İçeriye yönelen bağımlılıklar, use case giriş/çıkış modelleri, interface adapter ve composition root sınırları görünür hâle getirilir.
6. Hexagonal ile Clean birbirinin rakibi veya alt/üst sürümü değildir. Ortak ilkeleri ve vurgu farklarını karşılaştır; mevcut doğru port/decorator tasarımı korunabilir. [Cockburn](https://alistair.cockburn.us/hexagonal-architecture), [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
7. **Maven → Gradle**, mimari geçişten ayrı Faz 4'tür. JDK 25 ve framework sürümlerini sabit tutarak Kotlin DSL, wrapper/toolchain, dependency scopes, BOM, annotation processors, bootJar ve CI eşdeğerliğini öğret. Sonra version catalog ve convention plugin.
8. Eureka bugün kullanılıyor. Kubernetes aşamasında uygulamaları container/Deployment/Service olarak dağıtıp keşfi Service/DNS'e taşırız. Kubernetes business transaction veya idempotency çözümü değildir.
9. REST bir mimari stil, gRPC bir RPC yaklaşımı, Kafka ve RabbitMQ mesajlaşma platformlarıdır. Aynı sınıfta “protokoller” diye ezberletme. Her biri için kullanım nedeni, sözleşme ve hata davranışı öğretilecek.
10. Her yeni özellik ayrı mikroservis olmak zorunda değildir. Bounded context, veri sahipliği, bağımsız yaşam döngüsü ve operasyon maliyetiyle servis sınırını gerekçelendir.

## 3. Öğretim döngüsü ve hatırlama

Her odaklı çalışma şu sırayı izler:

1. Müşteri veya işletme problemi: örneğin ödeme yanıtı gelmediği hâlde para çekilmiş olabilir.
2. Kullanıcının kısa tahmini/tasarımı: mevcut bilgisi ve belirsizliği ortaya çıkar.
3. Mekanizma ve alternatif: neden çalışır, neyi garanti eder, maliyeti nedir?
4. Küçük uygulama: somut dosyalar, değişikliğin amacı ve tamamlanma ölçütü.
5. Review + gerekiyorsa tek öğretici arıza: sonuçtan mekanizmaya geri bağlan.
6. Transfer: aynı yaklaşımı farklı servis veya senaryoda daha az yardımla uygula.

**Hatırlama ritmi:** oturum başında uygun olduğunda bir eski konu sorusu; 2–3 oturum sonra aynı kavramı başka problemde kullanma; yaklaşık bir hafta sonra veya bir sonraki dönüm noktasında kodu kopyalamadan yeniden tasarlama. Bunlar otomatik takvim görevi değildir. Yanıt zayıfsa kısa bir geri dönüş dersi ver, sonra aktif işe dön.

**Öğrenme düzeyleri:** 0 = henüz ele alınmadı; 1 = örnekle açıklıyor; 2 = yönlendirmeyle uyguluyor; 3 = farklı senaryoda bağımsız uyguluyor ve hata teşhis ediyor; 4 = bedelleri savunuyor ve aradan sonra tekrar kurabiliyor. Sadece sohbeti okuyarak düzey atlatma; gözlenen kanıtı belirt.

**Resilience4j için özel tekrar planı:**
- İlk geri çağırma: circuit breaker, timeout, retry ve bulkhead hangi farklı soruları cevaplar?
- Mevcut stock çağrısını çalışır hâle getir; fallback'in servis arızasını “stok 0” diye sunmasını düzelt.
- CLOSED/OPEN/HALF_OPEN, pencere/minimum çağrı, slow-call/failure eşikleri ve ölçümleri kullanıcıyla yorumla.
- Ödeme veya kargo adapter'ında aynı politikayı sonradan yeniden kurdur; ödeme tekrarının idempotency ihtiyacını açıklat.
- Gerekirse bir ipucuyla başla; anotasyon veya YAML ezberini hedefleme.

**Senior görüşme provası:** dönüm noktalarında 10–15 dakikalık tasarım/arıza değerlendirmesi. Örnek: “Son stok için iki müşteri yarışıyor”, “ödeme webhook'u iptalden sonra geldi”, “DB havuzu dolarken virtual thread sayısını artırdın”, “consumer lag büyüyor”. Kullanıcı gerekçe ve kanıt sunsun; mentor net geri bildirim ve tek sonraki gelişim hedefi versin. Unvan veya mülakat başarısı garanti edilmez; gerçek yetkinlik kanıtları birikir.

## 4. Yetkinlik matrisi

| Alan | Projedeki çalışma | Öğrenciden beklenecek kanıt |
|---|---|---|
| E-ticaret / DDD | SKU/katalog, Money, sepet, sipariş satırları ve snapshot, stok rezervasyonu, ödeme, kargo/iade | Veri sahibi ve invariant belirler; sipariş toplamını güvenilir kaynaktan üretir; geçersiz durum geçişini engeller |
| Hexagonal → Clean | Port/adapter, decorator, composition root, request/response modelleri; bir servis üzerinde dönüşüm | Bağımlılık oklarını çizer; iş akışını infrastructure'a taşımadan teknoloji değiştirir |
| Spring Core / Boot | IoC, bean scope/lifecycle, configuration, proxy/self-invocation, auto-configuration/conditions, profiles/config binding, starter, Actuator | “Bu bean neden oluştu/oluşmadı?” sorusunu condition report ve wiring üzerinden çözer |
| Transaction / PostgreSQL | REQUIRED/REQUIRES_NEW, rollback-only, isolation, lost update, deadlock, uniqueness, pool sınırları | Flush ile commit'i ayırır; atomiklik ile isolation farkını aynı senaryoda gösterir |
| JPA / Hibernate | Entity lifecycle, persist/merge, dirty checking, ilişkiler/ownership, cascade/orphan removal, fetch plan, N+1, batch, projection, pagination | Üretilen SQL'i tahmin eder; sorgu sayısı ve EXPLAIN ile fetch/index tercihini savunur |
| REST | Kaynak modeli, DTO/validation, HTTP durumları/Location/Problem Details, pagination, idempotent checkout, OpenAPI, optimistic concurrency/ETag | Yanıt sözleşmesi tasarlar; istemci retry'sının etkisini açıklar; async siparişi GET ile takip ettirir |
| Security / gateway | OIDC sağlayıcı + Spring Security, JWT doğrulama, sahiplik/rol, tüm public route'lar, limit ve timeouts | 401/403 ayrımını ve kullanıcının başka siparişi okumasını engelleyen kontrolü uygular |
| Resilience4j | Timeout bütçesi, retry/backoff/jitter, CB, bulkhead ve rate limiting | Bir bağımlılık arızasında durum geçişini ölçer; retry çoğalmasını ve yanlış fallback'i önler |
| Kafka | Key/partition/order, groups/rebalance, offset/ack, retries/DLT, producer acks/idempotence, retention/replay, schema evolution | DB commit–offset aralığını açıklar; replay sırasında dış yan etkiyi tekrar üretmez |
| EDA / dağıtık tutarlılık | Domain/integration event, inbox/idempotency, polling outbox, saga choreography/orchestration, compensation, timeout/reconciliation | İş reddi ile teknik arızayı ayırır; geç/çift/sırası değişmiş event'e rağmen durum modelini korur |
| RabbitMQ | Notification iş kuyruğu, exchange/routing, prefetch, manual ack, confirms, retry/DLX/quorum | Broker confirm ile consumer ack'i ayırır; requeue döngüsünü teşhis eder |
| gRPC | İç servis toplu katalog/fiyat sorgusu, protobuf/stub, metadata, status, deadline/cancellation, sürüm uyumu | Aynı application portuna REST ve gRPC adapter'larını bağlar; timeout bütçesini taşır |
| Redis / okuma modelleri | Katalog cache-aside, TTL/invalidation/stampede; sipariş zaman çizelgesi için CQRS projection | Bayat veri bedelini ve kaynak gerçeği açıklar; projection'ı yeniden kurar |
| İleri EDA | Polling–CDC/Debezium karşılaştırması; sınırlı event sourcing laboratuvarı | Outbox tablosu ile event store farkını, ordering/rebuild/schema maliyetini açıklar |
| Java / JVM | Executors/CompletableFuture, concurrency, virtual threads, thread-local bağlam, heap/GC, JFR/thread dump | DB pool/CPU/lock darboğazını ayırır; daha çok thread'in neden her zaman çözüm olmadığını gösterir |
| Build / teslimat | Maven lifecycle/BOM → Gradle Kotlin DSL/toolchain/conventions; CI ve uygulama image'ları | Build'i yeniden üretir; bağımlılık çatışmasını ve annotation processor problemini teşhis eder |
| Operasyon | Metrics/logs/tracing, SLI/SLO, Kafka/outbox lag, Kubernetes DNS/probes/rollout/HPA, config/secrets, graceful shutdown | Bir siparişi uçtan uca izler; pod kaybı veya yavaş DB için ölçümden teşhis yapar |

Matristeki her şey aynı anda uygulanmaz. ROADMAP sırası, her alanı ihtiyacı doğduğu müşteri senaryosuna bağlar. OpenAPI/protobuf/async event sözleşmeleri ile birkaç odaklı JUnit/Testcontainers/contract kontrolü bu çalışmaların parçasıdır; uzun test listeleri dersin yerini almaz.

## 5. Güncel kod ve öğrenme kanıtı — 2026-09-15

- Kodda beş servis, Spring Boot 4.1.1 ve JDK 25 var; build Maven. Eski JDK 21/Lombok başlangıç engeli geçmişte kaldı. (2026-09-15'te beş `pom.xml` tek tek doğrulandı.)
- **Dış dünyaya açık API toplam üç endpoint:** `POST /products`, `GET /products/{id}`, `POST /api/orders`. inventory-service'in hiç REST endpoint'i yok. Projenin en büyük gerçekçilik açığı budur; hedef API yüzeyi ROADMAP bölüm 4'te.
- **Redis container ayakta ama hiçbir servis kullanmıyor** — kodda tek referans yok. Aynı şekilde Resilience4j yalnızca product-service'te tek anotasyon, paketi `infrastructure.exception` (yanlış yer), fallback arızayı "stok 0" diye sunuyor. api-gateway yalnızca product route'unu tanıyor.
- **Migration altyapısı yok ve bu 15 Eylül akşamı iki serviste peş peşe açılış hatası üretti:** inventory'de entity'nin beklediği tablo yoktu; order'da entity SEQUENCE isterken kolon identity'di. İkisi de elle düzeltildi (`processed_order_event` tablosu açıldı, outbox entity'si `GenerationType.IDENTITY`'ye çevrildi). Flyway artık "ileride" değil, ROADMAP Faz 1 işi.
- Inventory'de `application.port.in/out` ayrımı yapıldı. Order/product/gateway standardizasyonu henüz tamamlanmadı.
- Inventory çağrı zinciri: listener → retry decorator → Spring transaction proxy'si → transactional decorator → `OrderCreatedUseCase`. `@Primary` ve `@Qualifier` ile ayrı bean'ler bağlandı.
- Retry yalnızca `StockUpdateConflictException` için toplam üç deneme. Ortak transaction yaklaşımı kodda var; application framework bağımsız.
- Kullanıcı kontrollü `ROLLBACK_TEST` hatasını ve stokun düşmediğini bildirdi. Aynı event kaydının yokluğu ayrıca doğrulanmış sayılmayacak; kullanıcı ek manuel kontroller istemedi. Bu eski deneyi yeniden isteme.
- Geçici `ROLLBACK_TEST` bloğu güncel kodda kaldırılmış. Yeniden kaldırmasını isteme.
- `processed_order_event` tablosu için entity mevcut. Port hâlâ `ifNewOrderOrElse` ve `eventConsumed` içeriyor. Kullanıcı `tryRegisterEvent` eklemediğini açıkça bildirdi; kod da bunu doğruluyor.
- Order outbox `eventId` artık `GenerationType.IDENTITY` kullanıyor; eski SEQUENCE notu güncel değil. Migration altyapısı henüz yok; şema değişikliğini doğrulanmış migration olarak anlatma.
- Product/order outbox mevcut; inventory sonucu doğrudan Kafka'ya gönderiyor. Tam dayanıklı saga/outbox akışı tamamlanmış değil.
- Son kayıtlı inventory `clean compile` JDK 25 ile başarılı (2026-09-15). Bu belge güncellemesinde yeni servis başlatma veya test yapılmadı.
- Kullanıcı Resilience4j'yi yeniden kuracak kadar hatırlamadığını belirtti. “Projede var” kaydı öğrenme düzeyi değildir.
- İlk ürün/sipariş deneyleri yönlendirmeyle yapıldı. Clean, Gradle, gerçek OAuth2/OIDC, RabbitMQ, gRPC, payment, notification ve Kubernetes henüz uygulanmadı.

### Sıradaki somut ders

**Atomiklik ve eşzamanlılık: aynı event'i iki işlem almaya çalışırsa ne olur?**

Önce bir kısa soru ile mevcut transaction bilgisini geri çağır. Ardından `OrderRepositoryPort.tryRegisterEvent(int eventId)` sözleşmesini, Spring Data native `INSERT … ON CONFLICT DO NOTHING` sorgusunu, adapter'ın etkilenen satır sayısını boolean'a çevirmesini ve use case'in duplicate olduğunda dönmesini birlikte küçük bir değişiklik olarak ele al. Kayıt, transaction'ın başında alınır; başarısız işlemde geri alınır. DB unique/primary key ön koşulunu açıkla. Duplicate bir iş reddi exception'ı değildir.

Bir sonraki bağlı adım: başarı ve yetersiz stok sonuçlarının kalıcılığı, inventory sonuç outbox'ı ve listener hata yönetimi. Kullanıcının ortak DB transaction sorusunu tekrar outbox ön koşuluna dönüştürme. Tek bir senaryoyu bitirince eski Resilience4j bilgisini gerçek REST çağrısına geri bağla.

### Bekleyen hatırlama soruları

- Aynı transaction iki concurrent çağrının “event yok” görmesini tek başına engeller mi?
- Retry neden transaction proxy'sinin dışındadır; `catch` rollback-only durumunu temizler mi?
- `save` ne zaman persist, ne zaman merge davranışına gider; flush neden commit değildir?
- Circuit breaker OPEN iken ne olur; timeout ve bulkhead ile farkı nedir?
- Kafka broker ack, consumer offset ve iş sonucunun kalıcılığı hangi ayrı noktaları ifade eder?

Soruların cevaplarını peşinen gösterme. O tur en fazla bir veya iki tanesini seç; yanıtına göre öğretimi ayarla.

## 6. Kaynak kullanımı ve oturum devamlılığı

Kaynaklar mekanizmaları doğrular; servis sınırları ve faz sırası bu proje için yaptığımız tasarım tercihleridir. Güncel sürüm gerektiren uygulama adımında resmî dokümanı yeniden kontrol et. Araştırma dayanakları ROADMAP'in kaynak bölümündedir.

Her yeni oturumda: bu belgedeki aktif konuyu, ROADMAP'in ilgili fazını ve güncel kodu oku. Yalnızca gerekli geçmişi taşı. Yapıldı / kullanıcı gözlemledi / tasarlandı / bağımsız uygulandı durumlarını birbirine karıştırma.

Proje Gemini ile başladı, Claude Code ile devam etti; eski notlarda yanlış veya artık geçersiz kesinlikler vardı. Bu sürüm JDK 21, bağlanmamış order-service, tamamlanmış kapsamlı saga ve zorunlu `@Configuration` gibi eski ifadelerin yerini alır. `@Component` içindeki `@Bean` yöntemleri, birbirini doğrudan çağırmıyorsa tek başına hata değildir.

Platformlar arası dosya aktarımını kullanıcı yönetir; yerel belgelerin telefona veya başka uygulamalara otomatik eşitlendiğini varsayma. Yeni rutin dosyalar veya otomatik oturum kayıtları oluşturma.
