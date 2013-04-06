FilmeUtils é uma ferramenta para baixar filmes e séries com legendas.  

Download: https://docs.google.com/file/d/0B0QLHCMQb769VGNtOG5kalpTOFk/edit?usp=sharing   

Do que você precisa:  
 - Java 1.6 ou maior instalado. - www.java.com/  
 - Um cliente de torrent qualquer, recomendo o qbittorrent (http://www.qbittorrent.org/).  

![Screenshot da gui](https://raw.github.com/beothorn/FilmeUtils/master/gui.png)  

Comandos  

Sem argumentos  
	Abre a gui.  

-h ou --help  
	Mostra a ajuda  

-lt <termo da procura da legenda>  \[-r <regex para arquivos de legenda>\] \[-d <diretório de destino>\]  
	Procura e faz download do pacote de legendas,  
	aplica a regex nas legendas do arquivo e tenta pegar o torrent das legendas  
	que dão match. Copia as legendas que tem torrents para o diretório de  
	destino. Se o destino não for especificado, usa-se o que estiver no   
	HOME/.filmeUtils/subtitlefolder  
	O termo de procura não é uma regex.    
	Ex:  
	java -jar filmeUtils.jar -lt "game of" -r ".*720.*" -d "/home/foo/Downloads"  

-l <termo da procura da legenda>  \[-r <regex para arquivos de legenda>\] \[-d <diretório de destino>\]  
	Procura e faz download do pacote de legendas,   
	aplica a regex nas legendas do pacote e copia as legendas que dão match  
	para o destino. Se o destino não for especificado, usa-se o que estiver no  
	HOME/.filmeUtils/subtitlefolder  
	O termo de procura não é uma regex.
	Ex:  
	java -jar filmeUtils.jar -l "game of" -r ".*720.*" -d "/home/foo/Downloads"     

-t <termo da procura do torrent>  
	Procura e faz download do torrent com mais seeds.  
	O termo de procura não é uma regex.      
	Ex:  
	java -jar filmeUtils.jar -t "game of S01E01"  

-n  \[-r <regex para pacote de legenda>\[:regex para legenda\]\] \[-d <diretório de destino>\]  
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


-f \[arquivo de regex\] \[-d <diretório de destino>\]  
	Procura nas legendas adicionadas recentemente os pacotes de legenda que dão  
	match com as regex no arquivo passado. Para os pacotes de legenda que dão  
	match, aplica a segunda regex nos arquivos de legenda e faz download da    
	legenda e do torrent. Copia as legendas para o caminho de destion. Se o  
	destino não for	especificado, usa-se o que estiver  
	no HOME/.filmeUtils/subtitlefolder   
	Se não for passado o caminho do arquivo de regex,  
	usa-se o arquivo padrão em HOME/.filmeUtils/downloadThis  
	Formato do arquivo de regex  
	<regex para pacote de legendas>\[:regex para legenda\]  
	ex:  
	.*meu.*seriado.*so.*em.*hd.*:720  
	.*meu.*seriado.*qqer.*resolucao.*  

-p <termo da procura>  
	Somente lista os pacotes de legendas que batem com a procura e suas legendas.  
	O termo de procura não é uma regex.   
	
-auto
	Procura nas legendas adicionadas recentemente as legendas que dão match  
	com as regex no arquivo HOME/.filmeUtils/downloadThis . Procura o torrent  
	para essas legendas, se encontrar, baixa o torrent para o diretório  
	configurado em HOME/.filmeUtils/subtitlefolder . Depois adiciona o nome  
	do pacote de legendas no arquivo HOME/.filmeUtils/alreadyDownloaded . Se  
	um torrent/legenda já foi pego, ele não faz o download.
