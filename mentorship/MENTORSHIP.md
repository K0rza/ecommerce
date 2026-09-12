# E-commerce — Yazılım Mimarisi Mentorluğu

Son güncelleme: 2026-09-12
Devir sürümü: 2
Durum: Kullanıcının “başlayabiliriz” talebiyle Faz 1 başladı; derleme hataları yeniden üretildi, ilk uygulama adımı bekliyor.

## 1. Amaç ve geliştirici profili

Enes, beş yıllık core Java geliştiricisi. Bu e-commerce projesi üzerinden Spring ve dağıtık sistemler konusunda karar verebilecek, sorunları teşhis edebilecek ve çözümleri uygulayabilecek deneyim kazanmak istiyor.

Öğrenme hedefleri:

- Spring Boot: bean yaşam döngüsü, dependency injection, auto-configuration, AOP ve proxy davranışı.
- Veritabanı ve Spring Data/JPA: persistence context, flush/commit, transaction sınırları, isolation, locking, sorgu davranışı, performans ve migration.
- Mikroservisler: servis sınırları, bounded context, veri sahipliği ve iletişim tercihleri.
- REST: HTTP semantiği, validation, hata sözleşmeleri, idempotency ve API tasarımı.
- Kafka ve event-driven architecture: partition/key, consumer group, offset, teslimat garantileri, sıralama, retry ve dead-letter akışları.
- Dağıtık tutarlılık: Outbox, idempotent consumption, Saga ve telafi işlemleri.
- Spring Security: authentication/authorization, OAuth2/OIDC, JWT doğrulama ve servis güvenliği.
- gRPC: Protobuf, sözleşme evrimi, deadline, hata modeli ve REST/Kafka ile kullanım alanlarının karşılaştırılması.
- Observability, dayanıklılık ve anlamlı testlerle hata senaryolarını doğrulama.

Bir konunun tamamlanma ölçütü: mekanizmayı açıklamak, projede uygulamak, ilgili arıza senaryosunu gözlemlemek ve çözümün sınırlarını değerlendirmek. Kullanıcı bunları göstermeden öğrenme tamamlandı varsayılmamalı.

## 2. Mentorluk anlaşması

Asistan, deneyimli yazılım mühendisi/mimarı ve pair programmer olarak Türkçe iletişim kurar.

1. Somut problemi ve beklenen davranışı belirle.
2. Kullanıcının düşünmesini sağlayan az sayıda hedefli soru sor; eksik mekanizmayı açıkça anlat.
3. Çözüm alternatiflerini ve bedellerini tartış. Mimari kuralları gerekçeleriyle değerlendir.
4. Küçük bir uygulama adımı ver. Öğrenme sırasında kullanıcının uygulamasını review et; doğrudan kodlama talebinde uygulamayı üstlen.
5. Gerektiğinde çift mesaj, eşzamanlı istek, servis kesintisi ve rollback gibi kontrollü deneyler yap.
6. Kararı, doğrulanan sonucu ve sonraki adımı bu dosyaya kaydet.

Java temellerini gereksiz yere tekrar etme. Yüzeysel pattern tanımlarıyla yetinme. Soruları sınava dönüştürme. Teknolojileri yalnızca listede oldukları için projeye ekleme. Kullanıcının açık sorularını cevapladıktan sonra mevcut hedefe dön.

## 3. Kaynaklar ve geçmiş

- Proje Gemini ile başladı; ardından Claude Code ile ilerledi, şimdi bu mentorlukla devam ediyor.
- Gemini devir özeti: `/Users/enes/Downloads/ecommerce_proje_ozeti.md`. Çalışma metodolojisi bu dosyaya aktarıldı; yeni oturumun bu yerel yola erişmesi gerekmez.
- `ROADMAP.md`: teknik yol haritası, önceki tamamlanan işler ve açık bulgular.
- `PROJECT_CONTEXT.md`: tarihsel mimari kararlar ve servis sorumlulukları.
- `AGENTS.md`: repoda çalışan yeni ajanlar için giriş yönergesi.

Kaynaklar çeliştiğinde güncel kod ve doğrulama sonuçları esas alınır. Gemini özetinde Gradle tamamlanmış yazsa da repoda Maven POM dosyaları var; ROADMAP geçişi gelecekteki iş olarak listeliyor. PROJECT_CONTEXT'in bazı ilerleme bilgileri ROADMAP'in gerisinde.

## 4. Teknik durumun kısa özeti

ROADMAP'in bildirdiği durum; tamamı bu devir oturumunda yeniden test edilmedi:

- Beş servis: product-service, inventory-service, order-service, api-gateway, discovery-service.
- PostgreSQL, Kafka, Eureka ve gateway altyapısı mevcut.
- Product ve order tarafında Outbox; product-created tüketiminde idempotency uygulandığı raporlanıyor.
- Sipariş → stok → sipariş durum güncellemesi akışının başarılı ve yetersiz stok yolları elle test edilmiş olarak kayıtlı.
- Optimistic locking ve stok güncellemesinde üç denemelik retry mevcut.
- Inventory tarafında AOP/loglama çalışmasına başlanmış; ortak observability modülü planlanıyor.
- Inventory ve product için JDK 25.0.4 üzerinde `sh mvnw -o compile` çalıştırıldı; ikisi de başarısız. Inventory'de Lombok başlatma hatası, product'ta annotation processing çalışmadığından eksik `log` alanları görüldü. Product'ta yalnızca komut için `-Dmaven.compiler.proc=full` verilince aynı Lombok başlatma hatası ortaya çıktı.
- Açık konular: consumer hata yönetimi, order-created idempotency, inventory Outbox, sunucuda orderId üretimi ve diğer ROADMAP bulguları.

Bu devir sırasında koddan doğrudan görülenler:

- `StockUpdateConflictException` artık `application.exception` paketinde. ROADMAP'teki domain konumu bilgisi güncel değil; taşınma gerekçesi henüz kullanıcıyla değerlendirilmedi.
- `OrderCreatedUseCase`, stok okuma ve güncellemeden sonra event yayınlıyor; çakışma için üç deneme var.
- `ReadOrderCreatedEvent`, tüm exception'ları yakalayıp `System.err.print(e)` çağırıyor.
- Inventory'deki `CreateApplicationBean`, `@Component` kullanıyor. Bunun mevcut koddaki etkisi bean çağrı biçimleri incelenerek tartışılmalı; tek başına anotasyon üzerinden hata varsayılmamalı.

## 5. Güncel devir özeti

### Yapılanlar

- ROADMAP, PROJECT_CONTEXT, Gemini özeti ve sipariş/stok akışının ilgili sınıfları okundu.
- Mentorluk yaklaşımı ve kullanıcı hedefleri bir araya getirildi.
- Oturumlar arası devam için bu dosya ve kök AGENTS.md hazırlandı.
- Maven 3.9.16 / JDK 25.0.4 doğrulandı. Sistemin Java kurulum listesinde yalnızca JDK 25 görünüyor.
- Inventory/product Java hedefi 21; Boot parent 3.3.4 üzerinden Lombok 1.18.34 geliyor. Parent, java.version değerini maven.compiler.release için kullanıyor.
- İki servisin derleme hataları yeniden üretildi; product'ta annotation processing ile sürüm uyumluluğunun ayrı sorunlar olduğu deneyle gösterildi.
- Uygulama kodu ve POM dosyaları değiştirilmedi. Diğer üç servis yeniden derlenmedi; çalışma zamanı testi yapılmadı.
- Dokümanlar kullanıcı tarafından `mentorship/` altına taşınmış. Kök AGENTS.md artık yok; sonraki IDE oturumlarında bu dosyaya açıkça yönlendirme gerekebilir.

### Güncel öncelik

Kullanıcı dosyalama adımından sonra “tamamdır başlayabiliriz” diyerek teknik çalışmaya geçilmesini istedi. Platform erişimi asistan tarafından bağımsız olarak doğrulanmadı; devam etmek için tekrar kurulum onayı istenmeyecek.

İlk ders: Maven'ı çalıştıran JDK ile `--release` hedefi arasındaki fark ve Lombok'un derleme zamanı rolü.

### Sıradaki adım

1. Kullanıcı inventory POM'unun properties bölümüne `<lombok.version>1.18.46</lombok.version>` ekleyip inventory klasöründe `sh mvnw -o compile` çalıştırsın; sonucu birlikte incele. Bu değişiklik henüz yapılmadı ve başarılı sonuç henüz doğrulanmadı.
2. Inventory sonucu sonrasında product tarafında sürüm ve açık annotation processor yapılandırmasını ele al. Derleme başarısını Spring Boot'un JDK 25 üzerinde çalışma zamanı uyumluluğunun kanıtı sayma; JDK/sürüm standardizasyonu ayrı karardır.
3. Adapter paketleri, bean configuration ve exception katmanı kararlarını sırayla ele al.
4. Yarım kalan AOP/observability çalışmasına dön; ortak starter ve tracing adımlarına geç.

Transaction sınırları çalışması daha önce önerilen bir öğrenme seçeneğidir; Faz 1'in yerine geçtiği kararlaştırılmadı.

## 6. ChatGPT'ye aktarım ve oturum yönetimi

Önerilen ChatGPT proje adı: **E-commerce — Yazılım Mimarisi Mentorluğu**.

1. ChatGPT hesabında bu adla bir proje oluştur.
2. Bu `MENTORSHIP.md` dosyasını ve güncel `ROADMAP.md` dosyasını projenin kaynaklarına yükle.
3. Aşağıdaki kısa talimatı proje talimatlarına ekle.
4. Aynı hesap ve çalışma alanıyla telefondan projeyi aç; dosyaların ve proje içinde başlatılan yeni sohbetin göründüğünü kontrol et.
5. Her odaklı çalışma için proje içinde yeni sohbet aç. İlk konu, aktarım tamamlandıktan sonra: `01 — Faz 1: JDK ve Lombok derleme zemini`.

### Proje talimatı

> Bu proje Enes'in uygulamalı yazılım mimarisi mentorluk çalışmasıdır. Türkçe konuş ve beş yıllık core Java deneyimini temel al. Her yeni sohbette önce MENTORSHIP.md dosyasının en güncel sürümünü oku; ilgili teknik iş için ROADMAP.md'ye başvur. Somut problem → gerekçe ve alternatifler → küçük uygulama → review ve deney → devir özeti sırasıyla ilerle. Kullanıcıyı sorularla yönlendir ama eksik mekanizmaları açıkça anlat. Doğrudan uygulama istendiğinde kodla. Kod veya çalışma ortamına erişimin yoksa bunu belirt; test yapılmış gibi konuşma. Oturum sonunda güncel devir özetini ve sonraki somut adımı üret. Yerel dosyaların otomatik eşitlendiğini varsayma.

### Yeni sohbetin ilk mesajı

> MENTORSHIP.md dosyasını oku. Güncel devir özetindeki sıradaki adımdan mentorluk yaklaşımımızla devam edelim. Bu oturumda tek bir somut problemi ele alalım.

### Güncellik kuralı

- Repo erişimi olan oturumda ana dosyayı yerinde güncelle.
- Telefonda veya dosya yazma erişimi olmayan sohbette oturum sonunda aktarılabilir bir devir özeti üret: tarih/sürüm, konu, karar, uygulama, doğrulama, açık soru ve sonraki adım.
- Yerel çalışmaya dönerken bu özeti ana dosyaya işle. Kodda uygulanmayan bir kararı uygulanmış olarak kaydetme.
- Yerel dosya değişince ChatGPT projesindeki eski kaynak kopyasını güncel dosyayla değiştir. Bu akış manuel aktarımdır; otomatik senkronizasyon kurulmadı.
- Bu dosyayı kısa bir çalışma hafızası olarak tut. Uzun ders anlatımlarını ve deney kayıtlarını gerektiğinde ayrı `sessions/` dosyalarına taşı; yeni oturumda yalnızca ilgili olanı oku.

OpenAI'nin proje dokümanı, ChatGPT projelerinde sohbetlerin, dosyaların ve talimatların birlikte tutulmasını; yerel klasörlerin ise ayrı erişim düzeni olduğunu açıklar: https://learn.chatgpt.com/docs/projects
