Programa desenvolvido para realizar o cálculo do transiente hidráulico devido ao fechamento de uma válvula em um sistema Reservatório de montante > Tubulação > Válvula de jusante
O programa realiza a leitura de um arquivo de texto entitulado Entrada.txt (localizado em uma pasta com o endereço C:\transiente_valvula), conforme modelo disponibilizado. 
Obs.1: respeitar os espaços definidos
Obs.2: quanto maior o número de divisões do menor trecho e o tempo de simulação, maior será o tempo de processamento do programa
O programa realiza o processamento do cálculo a partir das equações de Chaudhry(2014) - Applied Hydraulic Transients - e grava os resultados em um arquivo de texto entitulado Resultado.txt em três colunas (tempo, pressão e vazão), referentes à seção da válvula
O programa foi desenvolvido com a versão 8.211 da JDK e deve ser executado a partir da IDE Eclipe