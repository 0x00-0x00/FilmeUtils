FilmeUtils
====================

FilmeUtils baixa legendas de séries do legendas.tv e respectivos torrents.   

[Download v4.1.5](https://github.com/beothorn/FilmeUtils/releases/download/filmeUtils-4.1.5/filmeUtils.jar)
=============


Do que você precisa  
--------------------- 
 - Java 1.8 ou maior instalado. - www.java.com/  
 - Um cliente de torrent qualquer que abra magnet links sem precisar confirmação, recomendo o qbittorrent (http://www.qbittorrent.org/).  

Como funciona  
---------------------
FilmeUtils procura a legenda no legendas.tv e depois o torrent no piratebay/bitsnoop/rarbg .  
Funciona melhor com legendas novas. Usando a linha de comando -auto e um agendador de tarefas
é possível colocar o filmeUtils pra fazer o download do seriado assim que sair a legenda.

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
	java -jar filmeUtils.jar -lt "Pioneer One" -r ".*720.*" -d "/home/foo/Downloads"  

-l &lt;termo da procura da legenda&gt;  [-r &lt;regex para arquivos de legenda&gt;] [-d &lt;diretório de destino&gt;]  
	Procura e faz download do pacote de legendas,   
	aplica a regex nas legendas do pacote e copia as legendas que dão match  
	para o destino. Se o destino não for especificado, usa-se o que estiver no  
	HOME/.filmeUtils/subtitlefolder  
	O termo de procura não é uma regex.
	Ex:  
	java -jar filmeUtils.jar -l "Pioneer One" -r ".*720.*" -d "/home/foo/Downloads"     

-t &lt;termo da procura do torrent&gt;  
	Procura e faz download do torrent com mais seeds.  
	O termo de procura não é uma regex.      
	Ex:  
	java -jar filmeUtils.jar -t "Pioneer One S01E01"  

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
	java -jar filmeUtils.jar -n -r ".*Pioneer.*One.*:.*720.*" -d "/home/foo/Downloads"  


-f <diretório com arquivos de configuração> [saveToFile]
	Procura nas legendas adicionadas recentemente as legendas que dão match  
	com as regex no arquivo [diretório com arquivos de configuração]/downloadThis . Procura o torrent  
	para essas legendas, se encontrar, baixa o torrent para o diretório  
	configurado em [diretório com arquivos de configuração]/subtitlefolder . Depois adiciona o nome  
	do pacote de legendas no arquivo [diretório com arquivos de configuração]/alreadyDownloaded . Se  
	um torrent/legenda já foi pego, ele não faz o download.   
	Formato do arquivo de regex  
	&lt;regex para pacote de legendas&gt;[:regex para legenda]  
	ex:  
	.*meu.*seriado.*so.*em.*hd.*:.*720.*|.*1080.*  
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
