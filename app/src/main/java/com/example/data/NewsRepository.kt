package com.example.data

import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val articleDao: ArticleDao,
    private val commentDao: CommentDao
) {
    val allArticles: Flow<List<ArticleEntity>> = articleDao.getAllArticles()
    val bookmarkedArticles: Flow<List<ArticleEntity>> = articleDao.getBookmarkedArticles()

    fun getArticleById(id: String): Flow<ArticleEntity?> = articleDao.getArticleById(id)
    fun getCommentsForArticle(articleId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForArticle(articleId)

    suspend fun toggleBookmark(articleId: String, currentBookmarked: Boolean) {
        val newBookmarked = !currentBookmarked
        articleDao.updateBookmarkStatus(articleId, newBookmarked, System.currentTimeMillis())
    }

    suspend fun markAsRead(articleId: String) {
        articleDao.updateReadStatus(articleId, true)
    }

    suspend fun addLike(articleId: String) {
        articleDao.incrementLikes(articleId)
    }

    suspend fun addComment(articleId: String, authorName: String, commentText: String) {
        val newComment = CommentEntity(
            articleId = articleId,
            authorName = authorName,
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            commentText = commentText,
            timestamp = "Justo ahora",
            likesCount = 0
        )
        commentDao.insertComment(newComment)
        articleDao.incrementCommentsCount(articleId)
    }

    suspend fun likeComment(commentId: Int) {
        commentDao.likeComment(commentId)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (articleDao.getArticleCount() == 0) {
            val initialArticles = listOf(
                ArticleEntity(
                    id = "motogp-2026-sepang",
                    title = "Revolución Aerodinámica: Ducati desvela el alerón de efecto suelo para romper récords",
                    subtitle = "Los test oficiales de Sepang dejan ver un concepto de difusión ventral que promete ganar 0.4 segundos por vuelta.",
                    content = """
                        En el calor asfixiante del circuito internacional de Sepang, la escudería oficial de Borgo Panigale ha sorprendido al paddock de MotoGP presentando una evolución técnica radical. El nuevo paquete aerodinámico integra deflectores de efecto suelo en la parte inferior del basculante traseros, canalizando el aire directo al difusor.

                        "Buscamos la máxima estabilidad en las frenadas violentas a más de 335 km/h," explicó Gigi Dall'Igna tras la primera jornada de entrenamientos. "La presión aerodinámica inferior mantiene el tren trasero adherido sin perjudicar la velocidad punta en recta."

                        Los cronómetros no mienten: Marc Márquez y Francesco Bagnaia marcaron tiempos por debajo del récord de la pista en su tercera tanda de vueltas consecutiva. Las marcas competidoras ya estudian reclamos técnicos ante la FIM, augurando una temporada 2026 de altísima tensión en las pistas.
                    """.trimIndent(),
                    category = "MotoGP",
                    imageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1000&q=80",
                    authorName = "Carlos Rossi",
                    authorRole = "Especialista MotoGP & Telemetría",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                    publishDate = "Hace 15 min",
                    readTimeMinutes = 5,
                    isBookmarked = true,
                    isRead = false,
                    likesCount = 342,
                    commentsCount = 28,
                    keyHighlights = "Deflectores de efecto suelo canalizan el aire al difusor trasero|Marc Márquez bate el récord del circuito en los test de Sepang|Polémica técnica abierta con los comisarios de la FIM",
                    bikeSpecs = "Motor: 1000cc V4 a 90°|Potencia: +290 CV|Velocidad Máx: 362.4 km/h|Peso Mínimo: 157 kg",
                    savedTimestamp = System.currentTimeMillis() - 10000
                ),
                ArticleEntity(
                    id = "ducati-v4r-review",
                    title = "Ducati Panigale V4 R 2026: La bestia de pista de 240 CV al descubierto",
                    subtitle = "Probamos en el circuito de Jerez la superbike homologada para calle más extrema jamás fabricada.",
                    content = """
                        Apretar el botón de encendido de la Panigale V4 R despierta un rugido gutural que eriza la piel. Con el escape completo Akrapovič de titanio y aceite sintético especial Shell Racing, el motor Desmosedici Stradale R de 998 cc entrega unos escalofriantes 240.5 CV a 16.500 rpm.

                        En el viraje rápido de Crivillé a 210 km/h, el chasis Front Frame recalibrado y las suspensiones Öhlins NPX 25/30 pressurizadas transmiten una confianza absoluta. La electrónica adopta la estrategia Engine Brake Control (EBC) EVO 2, ajustando la retención del motor según el ángulo de inclinación de la moto.

                        Es un instrumento de precisión extrema diseñado para pilotos que buscan sensaciones de carreras reales sin restricciones.
                    """.trimIndent(),
                    category = "Reviews",
                    imageUrl = "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1000&q=80",
                    authorName = "Lucía Fernández",
                    authorRole = "Probadora Senior de Pista",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                    publishDate = "Hoy, 11:20",
                    readTimeMinutes = 8,
                    isBookmarked = true,
                    isRead = true,
                    likesCount = 512,
                    commentsCount = 45,
                    keyHighlights = "240.5 CV a 16.500 rpm con escape de titanio Akrapovič|Sistema EBC EVO 2 optimizado por ángulo de inclinación|Chasis Front Frame ultraligero de magnesio",
                    bikeSpecs = "Cilindrada: 998 cc V4|Potencia: 240.5 CV @ 16,500 rpm|Par Motor: 112 Nm @ 12,250 rpm|Peso en Seco: 167 kg|Precio: 44.000 €",
                    savedTimestamp = System.currentTimeMillis() - 20000
                ),
                ArticleEntity(
                    id = "cafe-racer-aero9",
                    title = "La Cafe Racer Eléctrica 'Aero-9' desafía el diseño con chasis de fibra de basalto",
                    subtitle = "Un taller artesanal de Barcelona combina la silueta retro de los años 60 con propulsión eléctrica de 120 Nm instantáneos.",
                    content = """
                        El mundo de la customización vive una metamorfosis apasionante. El taller 'Apex Customs' ha presentado la Aero-9, una reinterpretación neoretro inspirada en las legendarias carreras de Isle of Man TT pero propulsada por un tren motriz eléctrico de respuesta fulgurante.

                        Su chasis está modelado artesanalmente en fibra de basalto, un material más sostenible que la fibra de carbono tradicional y con mejores propiedades de absorción de vibraciones de alta frecuencia. El paquete de baterías de 14.4 kWh ofrece hasta 220 km de autonomía urbana y recarga rápida al 80% en apenas 25 minutos.
                    """.trimIndent(),
                    category = "Reviews",
                    imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1000&q=80",
                    authorName = "Marc Soler",
                    authorRole = "Editor de Cultura Moto & Custom",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
                    publishDate = "Ayer, 18:45",
                    readTimeMinutes = 4,
                    isBookmarked = false,
                    isRead = false,
                    likesCount = 189,
                    commentsCount = 14,
                    keyHighlights = "Chasis artesanal de fibra de basalto ecológica|Par motor instantáneo de 120 Nm desde 0 rpm|Carga rápida DC al 80% en 25 minutos",
                    bikeSpecs = "Autonomía: 220 km Urbano|Batería: 14.4 kWh Litio-Ion|Par: 120 Nm Instantáneo|Peso: 185 kg",
                    savedTimestamp = 0L
                ),
                ArticleEntity(
                    id = "ktm-890-desert",
                    title = "KTM 890 Adventure R 2026: Prueba extrema de 5.000 km en el Desierto de Atacama",
                    subtitle = "Sometemos a la reina de la navegación off-road al test de resistencia más hostil entre dunas y salares.",
                    content = """
                        Atravesar el salar de Uyuni y coronar los pasos cordilleranos a más de 4.200 metros sobre el nivel del mar es la prueba de fuego para cualquier motocicleta trail de aventura. La KTM 890 Adventure R demostró una solidez impecable frente al polvo extremo y la escasez de oxígeno.

                        El motor bicilíndrico en paralelo LC8c mantiene un empaque ágil gracias al depósito bajo envuelto en las protecciones laterales. El modo Rally permite graduar el control de tracción en 9 niveles sobre la marcha, facilitando derrapes controlados en grava profunda.
                    """.trimIndent(),
                    category = "Industry News",
                    imageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1000&q=80",
                    authorName = "Gabriel Valdés",
                    authorRole = "Fotoperiodista Overland",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80",
                    publishDate = "27 Julio 2026",
                    readTimeMinutes = 7,
                    isBookmarked = true,
                    isRead = false,
                    likesCount = 278,
                    commentsCount = 19,
                    keyHighlights = "5.000 km sin averías en condiciones extremas de altitud y polvo|Suspensiones WP XPLOR Pro de 240 mm de recorrido|Modo Rally con control de deslizamiento de 9 etapas",
                    bikeSpecs = "Motor: 889 cc LC8c Bicilíndrico|Potencia: 105 CV|Depósito: 20 Litros|Recorrido Suspensión: 240 mm",
                    savedTimestamp = System.currentTimeMillis() - 30000
                ),
                ArticleEntity(
                    id = "tech-airbag-dair",
                    title = "Airbags inteligentes de nueva generación: Algoritmos con IA salvan vidas en la carretera",
                    subtitle = "Los sensores inerciales de alta frecuencia anticipan un impacto en 15 milisegundos antes de la colisión física.",
                    content = """
                        La tecnología derivada de la alta competición llega a las prendas urbanas y de turismo. Los nuevos monos y chaquetas equipados con giroscopios de 6 ejes y microprocesadores integrados procesan 1.000 lecturas por segundo para detectar pérdidas de control o colisiones inminentes.

                        En pruebas de simulación de impacto lateral a 50 km/h, el airbag se despliega por completo en tan solo 15 milisegundos, protegiendo cervicales, clavículas, tórax y columna vertebral con una absorción del 85% de la fuerza del choque.
                    """.trimIndent(),
                    category = "Seguridad",
                    imageUrl = "https://images.unsplash.com/photo-1558980664-3a031cf67ea8?auto=format&fit=crop&w=1000&q=80",
                    authorName = "Dra. Elena Ramos",
                    authorRole = "Ingeniera de Biomecánica",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
                    publishDate = "25 Julio 2026",
                    readTimeMinutes = 6,
                    isBookmarked = false,
                    isRead = true,
                    likesCount = 421,
                    commentsCount = 31,
                    keyHighlights = "Tiempo de inflado récord de 15 milisegundos|Reducción de impacto del 85% frente a protecciones rígidas tradicionales|Homologación CE Nivel 2 para espalda y tórax",
                    bikeSpecs = "Tiempo Inflado: 15 ms|Procesador: IMU 6 ejes 1000Hz|Batería: 30 Horas uso continuo",
                    savedTimestamp = 0L
                )
            )
            articleDao.insertArticles(initialArticles)

            val initialComments = listOf(
                CommentEntity(
                    articleId = "motogp-2026-sepang",
                    authorName = "Pablo_R1",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                    commentText = "Impresionante el trabajo aerodinámico de Ducati. Si el difusor inferior pasa la inspección de la FIM, el resto de fábricas va a sufrir mucho en Sepang y Losail.",
                    timestamp = "Hace 10 min",
                    likesCount = 12
                ),
                CommentEntity(
                    articleId = "motogp-2026-sepang",
                    authorName = "Motero_GZ",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80",
                    commentText = "Marc Márquez adaptado al efecto suelo en curva rápida es un espectáculo puro. ¡Qué ganas de que empiece el mundial!",
                    timestamp = "Hace 5 min",
                    likesCount = 8
                ),
                CommentEntity(
                    articleId = "ducati-v4r-review",
                    authorName = "Alejandro_V4",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=150&q=80",
                    commentText = "240 CV para 167 kg en seco... Las cifras de esta Panigale son literalmente de la categoría de Superbikes de WorldSBK.",
                    timestamp = "Hoy, 12:05",
                    likesCount = 15
                )
            )
            commentDao.insertComments(initialComments)
        }
    }
}
