package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class PokemothimProvider : MainAPI() {
    override var mainUrl = "https://pokemothim.net"
    override var name = "Pokémothim (Custom)"
    override var lang = "pt"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val animeList = listOf(
            AnimeSearchResponse(
                "Pokémon: Liga Índigo",
                "/2013/12/episodios.html",
                this.name,
                TvType.Anime,
                "https://justwatch.com"
            )
        )
        return HomePageResponse(listOf(HomePageList("Clássicos", animeList)), hasNext = false)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return listOf(
            AnimeSearchResponse(
                "Pokémon: Liga Índigo",
                "/2013/12/episodios.html",
                this.name,
                TvType.Anime,
                "https://justwatch.com"
            )
        )
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(mainUrl + url).document
        val episodes = mutableListOf<Episode>()
        
        // Captura todos os links de episódios no HTML do site
        document.select("a[href*=/episodios]").forEachIndexed { index, element ->
            val epUrl = element.attr("href")
            val epName = element.text()
            if (epName.contains("Episódio")) {
                episodes.add(Episode(epUrl, epName, episode = index + 1))
            }
        }

        return AnimeLoadResponse(
            "Pokémon: Liga Índigo",
            mainUrl + url,
            this.name,
            TvType.Anime,
            "https://justwatch.com",
            episodes = episodes
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // Redireciona para o link de vídeo final do Blogger ou Drive hospedado na página do episódio
        val document = app.get(data).document
        val iframeUrl = document.select("iframe").attr("src")
        
        if (iframeUrl.isNotEmpty()) {
            callback.invoke(
                ExtractorLink(
                    this.name,
                    "Player Principal",
                    iframeUrl,
                    "",
                    Qualities.P480.value,
                    false
                )
            )
        }
        return true
    }
}
