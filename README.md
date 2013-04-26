FilmeUtils
====================

FilmeUtils baixa filmes e séries do legendas.tv  

[Download v3.110](https://www.dropbox.com/s/vtsa54ervupiqhm/filmeUtils.jar)
=============


Do que você precisa  
--------------------- 
 - Java 1.6 ou maior instalado. - www.java.com/  
 - Um cliente de torrent qualquer, recomendo o qbittorrent (http://www.qbittorrent.org/).  

Como funciona  
---------------------
FilmeUtils procura a legenda no legendas.tv e depois o torrent no piratebay/bitsnoop/rarbg .  
Funciona melhor com legendas novas. Usando a linha de comando -auto e um agendador de taefas
é possível colocar o filmeUtils pra fazer o download do seriado assim que sai a legenda.  

GUI  
---------------------

![Screenshot da gui](https://raw.github.com/beothorn/FilmeUtils/master/gui.png "GUI")  

Comandos  
---------------------

<pre><code>
Sem argumentos  
	Abre a gui.  

-h ou --help  
	Mostra a ajuda  

-lt &lt;termo da procura da legenda&gt;  [-r &lt;regex para arquivos de legenda&gt;] [-d &lt;diretório de destino&gt;]  
	Procura e faz download do pacote de legendas,  
	aplica a regex nas legendas do arquivo e tenta pegar o torrent das legendas  
	que dão match. Copia as legendas que tem torrents para o diretório de  
	destino. Se o destino não for especificado, usa-se o que estiver no   
	HOME/.filmeUtils/subtitlefolder  
	O termo de procura não é uma regex.    
	Ex:  
	java -jar filmeUtils.jar -lt "game of" -r ".*720.*" -d "/home/foo/Downloads"  

-l &lt;termo da procura da legenda&gt;  [-r &lt;regex para arquivos de legenda&gt;] [-d &lt;diretório de destino&gt;]  
	Procura e faz download do pacote de legendas,   
	aplica a regex nas legendas do pacote e copia as legendas que dão match  
	para o destino. Se o destino não for especificado, usa-se o que estiver no  
	HOME/.filmeUtils/subtitlefolder  
	O termo de procura não é uma regex.
	Ex:  
	java -jar filmeUtils.jar -l "game of" -r ".*720.*" -d "/home/foo/Downloads"     

-t &lt;termo da procura do torrent&gt;  
	Procura e faz download do torrent com mais seeds.  
	O termo de procura não é uma regex.      
	Ex:  
	java -jar filmeUtils.jar -t "game of S01E01"  

-n  [-r &lt;regex para pacote de legenda&gt;[:regex para legenda]] [-d &lt;diretório de destino&gt;]  
	Se não for passado uma regex, mostra a lista legendas adicionadas  
	recentemente. Se for passada a regex, faz download do pacote de legendas  
	que dá match,  
	e se for passada a segunda parte da regex com ":" aplica regex nos arquivos  
	de legendas. Tenta pegar o torrent das legendas que derão match e copia as  
	legendas que tem torrents para o diretório de destino. Se o destino não for  
	especificado, usa-se o que estiver no HOME/.filmeUtils/subtitlefolder    
	Ex:  
	java -jar filmeUtils.jar -n  
		Lista os pacotes de legendas novos
	java -jar filmeUtils.jar -n -r ".*game.*of.*:.*720.*" -d "/home/foo/Downloads"  


-f [arquivo de regex] [-d &lt;diretório de destino&gt;]  
	Procura nas legendas adicionadas recentemente os pacotes de legenda que dão  
	match com as regex no arquivo passado. Para os pacotes de legenda que dão  
	match, aplica a segunda regex nos arquivos de legenda e faz download da    
	legenda e do torrent. Copia as legendas para o caminho de destion. Se o  
	destino não for	especificado, usa-se o que estiver  
	no HOME/.filmeUtils/subtitlefolder   
	Se não for passado o caminho do arquivo de regex,  
	usa-se o arquivo padrão em HOME/.filmeUtils/downloadThis  
	Formato do arquivo de regex  
	&lt;regex para pacote de legendas&gt;[:regex para legenda]  
	ex:  
	.*meu.*seriado.*so.*em.*hd.*:720  
	.*meu.*seriado.*qqer.*resolucao.*  

-p &lt;termo da procura&gt;  
	Somente lista os pacotes de legendas que batem com a procura e suas legendas.  
	O termo de procura não é uma regex.   
	
-auto
	Procura nas legendas adicionadas recentemente as legendas que dão match  
	com as regex no arquivo HOME/.filmeUtils/downloadThis . Procura o torrent  
	para essas legendas, se encontrar, baixa o torrent para o diretório  
	configurado em HOME/.filmeUtils/subtitlefolder . Depois adiciona o nome  
	do pacote de legendas no arquivo HOME/.filmeUtils/alreadyDownloaded . Se  
	um torrent/legenda já foi pego, ele não faz o download.
</code></pre>
