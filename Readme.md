PT/BR:
Programa desenvolvido para realizar o cálculo do transiente hidráulico devido ao fechamento de uma válvula em um sistema Reservatório de montante > Tubulação > Válvula de jusante

O programa realiza a leitura de um arquivo de texto entitulado "Entrada.txt" (localizado em uma pasta com o endereço C:\transiente_valvula), conforme modelo disponibilizado

Obs.1: respeitar os espaços definidos em "Entrada.txt"

Obs.2: quanto maiores o número de divisões do menor trecho e o tempo de simulação, maior será o tempo de processamento do programa

O programa realiza o processamento do cálculo a partir das equações de Chaudhry(2014) - Applied Hydraulic Transients - e grava os resultados em um arquivo de texto entitulado Resultado.txt em três colunas (tempo, pressão e vazão), referentes à seção da válvula

O programa foi desenvolvido com a versão 8.211 da JDK e deve ser executado a partir da IDE Eclipe

EN/US:
Program developed for calculating hydraulic transients due to valve closure in a setup of Upstream Reservoir > Pipeline > Downstream valve.

The program reads a text file named "Entrada.txt" (located on C:\transiente_valvula), according to the available model

Obs.1: do not change the blank spaces defined on "Entrada.txt"

Obs.2: the bigger the divisions of the smaller branch and the simulation time, the bigger the processing time of the program

The program uses the equations of Chaudhry(2014) - Applied Hydraulic transients for obtaining the results and these are recorded on a text file named "Resultado.txt" in three columns (time, pressure and flow rate), regarding the valve section

The program has been developed with JDK version 8.211 and must be executed from Eclipse IDE