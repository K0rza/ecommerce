# E-Commerce Microservices - Project Context

## 1. Projenin Genel Amacı
Bu proje, production-ready (canlı ortama çıkmaya hazır) standartlarda, olay güdümlü (event-driven) ve mikroservis mimarisine dayalı bir E-Ticaret sistemidir. Temel amaç, sadece çalışan bir kod yazmak değil; "Clean Architecture", "Hexagonal Architecture", "Saga Pattern" ve "Outbox Pattern" gibi ileri düzey yazılım mühendisliği pratiklerini doğru sınırlarla ve prensiplerle uygulamaktır.

## 2. Microservice Mimarisi
Sistem, merkeziyetsiz veri yönetimi (her servisin kendi veritabanı var) ve asenkron iletişim (Kafka) üzerine kuruludur. Servisler arası sıkı bağımlılığı (tight coupling) engellemek için Event-Driven Architecture benimsenmiştir. İstekler sisteme bir API Gateway üzerinden girmektedir.

## 3. Service'ler ve Sorumlulukları
*   **product-service:** Ürün kataloğunun yönetilmesi. Ürünlerin (fiyat, stok, isim vb.) oluşturulması ve invariant (iş kuralı) kontrollerinin yapılması.
*   **inventory-service:** Stok yönetimi. Ürünlerin bilgisiyle değil, yalnızca "hangi ID'li üründen kaç adet stok olduğu" ile ilgilenir (Kendi Bounded Context'i).
*   **order-service:** Müşteri siparişlerinin yönetimi. (Şu an geliştirme aşamasında).
*   **api-gateway:** Dış dünyadan gelen HTTP isteklerinin karşılandığı, yönlendirildiği ve yetkilendirmesinin (Auth) yapıldığı tek giriş kapısı.
*   **discovery-service:** Servislerin birbirini bulmasını sağlayan kayıt defteri.

## 4. API Gateway
Spring Cloud Gateway MVC kullanılarak oluşturulmuştur. Sadece basit bir yönlendirici (router) olmakla kalmayıp, gelen isteklerin `Authorization` başlığını kontrol eden bir `AuthenticationFilter` barındırır. Bu filtre yapısı da Hexagonal mimariye uygun olarak (Domain, UseCase ve Infrastructure) katmanlandırılarak tasarlanmıştır.

## 5. Eureka / Service Discovery
Netflix Eureka kullanılarak `discovery-service` (port: 8761) ayağa kaldırılmıştır. Tüm mikroservisler başlatıldığında kendilerini bu servise register ederler (kaydederler).

## 6. Kafka Kullanımı
Servisler arası iletişimde Publish-Subscribe mantığıyla çalışır.
*   `product-service` yeni ürün yaratıldığında `PRODUCT-CREATED-EVENTS` topic'ine mesaj atar.
*   `inventory-service` bu topic'i dinler (Consumer) ve stok kaydı açar.
*   Kafka mesaj gönderiminde `CompletableFuture` kullanılarak asenkron yapının güvenliği (broker acknowledgement) sağlanmıştır.

## 7. PostgreSQL
Her mikroservisin kendi bağımsız PostgreSQL veritabanı/şeması bulunmaktadır (`product_db`, `inventory_db` vb.). Spring Data JPA ve Hibernate kullanılarak veritabanı işlemleri yürütülmektedir. Veritabanı tabloları (JPA Entity'leri) ile Domain Model nesneleri birbirinden kesin çizgilerle ayrılmıştır.

## 8. Hexagonal Architecture (Ports & Adapters)
Projenin en katı şekilde uygulanan kuralıdır. İş kurallarını (Business Logic) dış dünyadan ve teknolojilerden (Spring, Kafka, PostgreSQL) izole etmek için kullanılmıştır. Dış sistemlerle olan iletişim, Domain/Application katmanında tanımlanan `Port`'lar (Interface) üzerinden ve Infrastructure katmanındaki `Adapter`'lar aracılığıyla sağlanır.

## 9. Domain / Application / Infrastructure Ayrımı
*   **Domain:** Saf Java sınıfları. İçerisinde hiçbir framework bağımlılığı (import org.springframework...) yoktur. Aggregate Root'lar (Örn: Product, Order), Value Object'ler ve Exception'lar buradadır. Veri doğrulamaları (Örn: Fiyat negatif olamaz) nesne yaratılırken (constructor) yapılır.
*   **Application (UseCase):** İş akışının koordine edildiği yer. Port'ları çağırarak Domain nesnelerini yönetir.
*   **Infrastructure:** Spring framework, JPA, REST Controller'lar, Kafka Listener'lar ve konfigurasyonların bulunduğu, teknolojinin yaşadığı en dış katmandır.

## 10. Authentication Tasarımı
Şu an için "mock/simüle edilmiş" bir yetkilendirme (Auth) sistemi vardır. Gerçek bir Identity Management (IAM) veya JWT yapısı yerine, Gateway üzerinde `Authorization: Bearer admin-secret-token` header kontrolü yapılmaktadır. Ancak bu yapı bile kendi içinde Domain ve UseCase mantığına ayrılarak tasarlanmıştır.

## 11. Outbox Pattern
Veri kaybını ve "Dual-Write" (çifte yazma) problemini önlemek için başarıyla uygulanmıştır.
*   Bir işlem olduğunda (Örn: Ürün eklendiğinde), Entity ile fırlatılacak Event (JSON formatında `OutboxEventEntity` olarak) **aynı `@Transactional` blok içinde** atomik olarak kendi veritabanına yazılır.
*   Infrastructure katmanındaki bir `@Scheduled` worker, tablodaki işlenmemiş (`processed=false`) kayıtları okur, Kafka'ya gönderir.
*   Kafka'dan başarı onayı (callback / `.thenRun()`) geldiği anda veritabanındaki kayıt `processed=true` olarak güncellenir.

## 12. Önemli Teknik Kararlar
*   **Transaction Yönetimi Pragmizmi:** Hexagonal mimariyi korumak adına Application katmanına `@Transactional` koymak yerine Decorator Pattern kullanılması tartışılmış, ancak over-engineering (aşırı mühendislik) olmaması için transaction sınırlarının altyapıdaki adaptörler içinde yönetilmesine (veya SRP kurallarına uygun birleştirilmesine) karar verilmiştir.
*   **Saga Pattern Stratejisi:** Mikroservisler arası sipariş senaryosunda (Order -> Inventory -> Payment) Orchestration yerine **Choreography (Koreografi)** seçilmiştir. Bunun sebebi, sistemin loosely coupled (gevşek bağlı) yapısını korumak ve merkezi bir hata noktasını (SPOF) engellemektir.
*   **Idempotency (Eşetkisellik) Kalkanı:** Kafka'nın "At-least-once" doğası gereği duplicate (çift) gelebilecek mesajlara karşı Tüketici (Consumer) tarafında önlem alınmıştır. `inventory-service` tarafında mesajın daha önce işlenip işlenmediği kontrol edilmekte, ayrıca SQL seviyesinde Primary Key (`@Id`) ile race-condition kalkanı kurulmuştur.

## 13. Karşılaştığımız Hatalar ve Nedenleri
*   **Dual-Write Yanılgısı:** Başlangıçta veritabanına kaydederken aynı metot içinde Kafka'ya doğrudan mesaj atılması. Hata: Kafka çökerse mesaj gider ama db rollback olur (veya tam tersi). Outbox ile çözüldü.
*   **Adapter Sorumluluğunun Şişmesi:** `JpaProductRepositoryAndConsistencyAdapter` sınıfının hem outbox hem de product işlerini yapması SRP (Tek sorumluluk) prensibini bozuyordu. Adaptörler bölündü (ProductAdapter ve OutboxAdapter).
*   **Asenkron Loglama Tuzağı:** `CompletableFuture` kullanırken log satırının `.thenRun()` callback'i dışına yazılması sebebiyle logların Kafka yanıtından önce, hatalı state (processed: false) ile konsola basılması.

## 14. Üzerinde Çalışılan ve Öğrenilen Konular (Geliştirici Odakları)
*   Domain Driven Design (DDD) bağlamında servislerin sınırlarının (Bounded Context) belirlenmesi (Örn: Inventory'nin Product invariant'larını bilmemesi).
*   Clean/Hexagonal Architecture prensiplerinden taviz vermeden Spring Framework özelliklerini (özellikle Transaction yönetimini) kullanma sanatının dengesi (Pragmatic Architecture).
*   Asenkron programlama ve Kafka'nın mikroservisler arası veri tutarlılığına (Eventual Consistency) etkisi.

## 15. Henüz Tamamlanmamış veya Şüpheli Tasarımlar
*   **Order Service:** Domain ve Application katmanları tasarlandı, Event'ler oluşturuldu ancak Altyapı (Infrastructure), Repository ve Outbox entegrasyonu henüz bağlanmadı (Geliştirme aşamasında).
*   **Auth Servisi:** Gateway üzerindeki Token kontrolü mock (hardcoded) durumda. Gerçek bir Identity Service (JWT üreten servis) eksik.
*   **Saga Geri Alma (Compensating Transactions):** Sipariş verildiğinde stok yetersizse (`OutOfStockEvent`) siparişin statüsünü `CANCELED`'a çekecek geri dönüş (rollback) mekanizmasının kodlaması henüz tamamlanmadı.

## 16. Bundan Sonra Öğrenilmesi / Çözülmesi Gereken Konular
*   Saga Choreography tam entegrasyonu (Başarısız işlemlerde Compensating Transaction tasarımı ve event'lerin yakalanması).
*   Distributed Tracing (Dağıtık İzleme - Zipkin, Jaeger) ve Correlation ID konseptleri (Saga'daki logları izleyebilmek için).
*   Gerçek bir Security (OAuth2 / JWT) implementasyonu.
*   Circuit Breaker, Retry, Rate Limiter (Resilience4j) kalıplarının derinlemesine incelenmesi (Şu an Feign client'ta basit bir Fallback var ancak detaylandırılması gerek).
