package transiente_valvula;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Entrada extends Parametros {

	public Entrada(String endereco) {

		String linha = "";
		this.endereco = endereco;
//		endereco = "C:\\transiente_valvula\\Entrada.txt";

		try {
			FileReader arquivo = new FileReader(endereco);

			BufferedReader lerArquivo = new BufferedReader(arquivo);
			boolean continuar = true;

			while (continuar) {

				try {

					linha = lerArquivo.readLine().trim();
					cabecalho(linha, lerArquivo);

				} catch (Exception e) {
					// TODO: handle exception
					continuar = false;
				}

			}

			gestorDeDados();

		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("Endereço não encontrado.");
			e.printStackTrace();
		}

	}

	protected void cabecalho(String linha, BufferedReader lerArquivo) {

		if (linha.trim().toUpperCase().equalsIgnoreCase("[TRECHOS]")) {
			lerTrechos(linha, lerArquivo);
		}

		if (linha.trim().toUpperCase().equalsIgnoreCase("[DADOS VALVULA]")) {
			lerDadosValvula(linha, lerArquivo);
		}

	}

	protected void lerTrechos(String linha, BufferedReader lerArquivo) {

		try {
			linha = lerArquivo.readLine();
			while (!"".equals(linha = lerArquivo.readLine().trim())) {
				listaTrechos.add(linha);
				System.out.println(linha);
			}

		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("Problema na leitura dos dados dos trechos.");
			e.printStackTrace();
		}

	}

	protected void lerDadosValvula(String linha, BufferedReader lerArquivo) {

		try {
			linha = lerArquivo.readLine();
			while (!"".equals(linha = lerArquivo.readLine().trim())) {
				listaDadosValvula.add(linha);
				System.out.println(linha);
			}

		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("Problema na leitura dos dados da válvula.");
			e.printStackTrace();
		}

	}

	public void gestorDeDados() {

		int cont = 1;

		for (String linha : listaTrechos) {
			
			String[] splitTrechos = linha.split("\\s+");
			String trechoString = String.valueOf(cont);

			double comprimento = Double.valueOf(splitTrechos[0]);
			mapaComprimentos.put(trechoString, comprimento);

			double diametro = Double.valueOf(splitTrechos[1]);
			mapaDiametros.put(trechoString, diametro);

			double espessura = Double.valueOf(splitTrechos[2]);
			mapaEspessuras.put(trechoString, espessura);
			
			mapaRi.put(trechoString, mapaDiametros.get(trechoString) / 2);
			mapaRo.put(trechoString, mapaRi.get(trechoString) + mapaEspessuras.get(trechoString));

			double elasticidade = Double.valueOf(splitTrechos[3]);
			mapaMElasticidade.put(trechoString, elasticidade);

			double rugosidade = Double.valueOf(splitTrechos[4]);
			mapaRugosidades.put(trechoString, rugosidade);

			double poisson = Double.valueOf(splitTrechos[5]);
			mapaCoefPoisson.put(trechoString, poisson);

			cont++;
		}

		int i = 1;
		
		for (String linha : listaDadosValvula) {

			String[] splitTrechos = linha.split("\\s+");

			switch(i) {

			case 1:
				vazao = Double.valueOf(splitTrechos[1]);
				break;

			case 2:
				Em = Double.valueOf(splitTrechos[1]);
				break;

			case 3:
				tc = Double.valueOf(splitTrechos[1]);
				break;

			case 4:
				Cd = Double.valueOf(splitTrechos[1]);
				break;

			case 5:
				DH = Double.valueOf(splitTrechos[1]);
				break;
				
			case 6:
				divisoes_menor_trecho = Integer.valueOf(splitTrechos[3]);
				break;
				
			case 7:
				tempo_simulacao = Double.valueOf(splitTrechos[3]);
				break;
			
			}
			i++;
		}
	}

}
