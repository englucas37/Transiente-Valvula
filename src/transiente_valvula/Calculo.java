package transiente_valvula;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Calculo extends Entrada {

	private double menor_trecho;
	private double Dx, Dt; // discretização espacial e temporal
	private Map<String, Double> mapaCeleridade = new HashMap<>();
	final double pho = 1000; // massa específica da água
	final double Kagua = 219 * 1E7; // módulo de elasticidade da água
	final double ni = 1E-06; // viscosidade cinemática da água
	final double G = 9.81; // aceleração da gravidade
	private double phi; // ancoragem da tubulação
	private double numeroPassoTempo;
	public int contK; // contagem do passo de tempo
	private double area, Ca, R, Cn, Cp, Hp, Qp; // variáveis do transiente
	private double porcFechamento, Cv, Cdvariavel, Av, tau; // variáveis da válvula
	double[] distancia, tempo;

	public Calculo(String endereco) {

		super(endereco);

		menor_trecho = Collections.min(mapaComprimentos.values());
		Dx = menor_trecho / divisoes_menor_trecho;

		calcCeleridade();
		calcNumeroSubtrechos();

		for (int i = 1; i <= mapaComprimentos.size(); i++) {

			double L = mapaComprimentos.get(String.valueOf(i));
			Lacumulado = Lacumulado + L;
		}

		numeroPassoTempo = Math.round(tempo_simulacao / Dt);

		calcPermanente();
		calcTransiente();

	}

	public void calcCeleridade() {

		int cont = 1;

		for (String linha : listaTrechos) {

			String trechoString = String.valueOf(cont);
			phi = 2 * (1 + mapaCoefPoisson.get(trechoString))
					* (Math.pow(mapaRo.get(trechoString) / 1000, 2) + Math.pow(mapaRi.get(trechoString) / 1000, 2))
					/ (Math.pow(mapaRo.get(trechoString) / 1000, 2) - Math.pow(mapaRi.get(trechoString) / 1000, 2))
					- (2 * mapaCoefPoisson.get(trechoString) * Math.pow(mapaRi.get(trechoString) / 1000, 2))
							/ (Math.pow(mapaRo.get(trechoString) / 1000, 2)
									- Math.pow(mapaRi.get(trechoString) / 1000, 2));
			double celeridade = Math.sqrt((Kagua * mapaMElasticidade.get(trechoString) * 1E9)
					/ (pho * mapaMElasticidade.get(trechoString) * 1E9 + pho * Kagua * phi));
			mapaCeleridade.put(trechoString, celeridade);
			cont++;
			Dt = Dx / mapaCeleridade.get(trechoString);
		}
	}

	public void calcNumeroSubtrechos() {

		int cont = 1;

		for (String linha : listaTrechos) {

			String trechoString = String.valueOf(cont);
			double numeroSubtrechos = Math
					.round(courant * mapaComprimentos.get(trechoString) / (mapaCeleridade.get(trechoString) * Dt));
			mapaNumeroSubtrechos.put(trechoString, numeroSubtrechos);
			cont++;
		}
	}

	public void calcPermanente() {

		int cont = 1;

		for (String linha : listaTrechos) {

			String trechoString = String.valueOf(cont);
			double Rey = 4 * vazao / (Math.PI * mapaDiametros.get(trechoString) / 1000 * ni);
			double f = 0.25
					/ Math.pow(Math.log10(mapaRugosidades.get(trechoString) / (3.7 * mapaDiametros.get(trechoString))
							+ 5.74 / (Math.pow(Rey, 0.9))), 2);
			mapaFatorAtrito.put(trechoString, f);
			cont++;
		}

		int cont2 = 1;
		int contador = 1;
		double[] vetorPermanente = new double[2];

		for (double i = Dx; i <= Lacumulado; i += Dx) {

			String trechoString = String.valueOf(cont2);
			double f = mapaFatorAtrito.get(trechoString);

			if (contador == 1) {

				double inicio = 0;
				vetorPermanente[0] = DH;
				vetorPermanente[1] = vazao;
				pressaoPermanente.put(inicio, vetorPermanente[0]);
				vazaoPermanente.put(inicio, vetorPermanente[1]);
			}

			vetorPermanente[0] = DH
					- 0.0827 * f * contador * Dx * vazao * vazao / Math.pow(mapaDiametros.get(trechoString) / 1000, 5);
			vetorPermanente[1] = vazao;
			pressaoPermanente.put(i, vetorPermanente[0]);
			vazaoPermanente.put(i, vetorPermanente[1]);
			contador++;
		}
	}

	public void calcTransiente() {

		calcPermanente();
		tempo = new double[(int) numeroPassoTempo];
		distancia = new double[(int) (Lacumulado / Dx + 1)];
		double[] vetorPressaoRes = new double[(int) numeroPassoTempo];
		double[] vetorVazaoRes = new double[(int) numeroPassoTempo];
		Map<double[], double[]> Hres = new HashMap<>();
		Map<double[], double[]> Qres = new HashMap<>();

		int cont = 0;
		int cont2 = 0;

		for (double i = 0.0; i < tempo_simulacao; i += Dt) {

			tempo[cont] = i;
			cont++;
		}

		for (double i = 0; i <= Lacumulado; i += Dx) {

			distancia[cont2] = i;
			cont2++;
		}

		int cont3 = 0;
		int cont4 = 0;

		// Seção do reservatório (carga) -> durante todo o transiente
		if (distancia[cont4] == 0) {

			for (double x = 0; x <= tempo_simulacao; x += Dt) {

				vetorPressaoRes[cont3] = pressaoPermanente.get(distancia[cont4]);
				cont3++;
			}

			vetorVazaoRes[0] = vazaoPermanente.get(distancia[cont4]);

			Hres.put(tempo, vetorPressaoRes);
			Qres.put(tempo, vetorVazaoRes);
			pressaoTransiente.put(distancia[cont4], Hres);
			vazaoTransiente.put(distancia[cont4], Qres);
			cont4++;
		}

		// Seções intermediárias e da válvula -> apenas primeiro valor do permanente
		while (cont4 < distancia.length) {

			Map<double[], double[]> Hsecoes = new HashMap<>();
			Map<double[], double[]> Qsecoes = new HashMap<>();
			double[] vetorPressaoSecoes = new double[(int) numeroPassoTempo];
			double[] vetorVazaoSecoes = new double[(int) numeroPassoTempo];
			vetorPressaoSecoes[0] = pressaoPermanente.get(distancia[cont4]);
			vetorVazaoSecoes[0] = vazaoPermanente.get(distancia[cont4]);
			Hsecoes.put(tempo, vetorPressaoSecoes);
			Qsecoes.put(tempo, vetorVazaoSecoes);
			pressaoTransiente.put(distancia[cont4], Hsecoes);
			vazaoTransiente.put(distancia[cont4], Qsecoes);
			cont4++;
		}

		area = Math.PI * Math.pow(mapaDiametros.get(String.valueOf(1)) / 1000, 2) / 4;

		Ca = G * area / mapaCeleridade.get(String.valueOf(1));
		R = mapaFatorAtrito.get(String.valueOf(1)) / (2 * mapaDiametros.get(String.valueOf(1)) / 1000 * area);

		int cont5 = 0;

		// Seção do reservatório (vazão) -> segundo passo de tempo
		if (distancia[cont5] == 0) {

			Cn = vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[cont5]
					- Ca * pressaoTransiente.get(distancia[cont5 + 1]).get(tempo)[cont5]
					- R * Dt * vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[cont5]
							* Math.abs(vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[cont5]);
			Qp = Cn + Ca * Hres.get(tempo)[cont5];
			Map<double[], double[]> Qsecoes = new HashMap<>();
			vetorVazaoRes[cont5 + 1] = Qp;
			Qsecoes.put(tempo, vetorVazaoRes);

			vazaoTransiente.put(distancia[cont5], Qsecoes);
			cont5++;
		}

		// Seções intermediárias -> segundo passo de tempo
		while (distancia[cont5] > 0 && cont5 < distancia.length - 1) {

			Cn = vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[0]
					- Ca * pressaoTransiente.get(distancia[cont5 + 1]).get(tempo)[0]
					- R * Dt * vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[0]
							* Math.abs(vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[0]);
			Cp = vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
					+ Ca * pressaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
					- R * Dt * vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
							* Math.abs(vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]);
			Qp = 0.5 * (Cn + Cp);
			Hp = (Qp - Cn) / Ca;

			Map<double[], double[]> Hsecoes = new HashMap<>();
			Map<double[], double[]> Qsecoes = new HashMap<>();
			double[] vetorPressaoSecoes = new double[(int) numeroPassoTempo];
			double[] vetorVazaoSecoes = new double[(int) numeroPassoTempo];
			vetorPressaoSecoes[0] = pressaoTransiente.get(distancia[cont5]).get(tempo)[0];
			vetorVazaoSecoes[0] = vazaoTransiente.get(distancia[cont5]).get(tempo)[0];
			vetorPressaoSecoes[1] = Hp;
			vetorVazaoSecoes[1] = Qp;
			Hsecoes.put(tempo, vetorPressaoSecoes);
			Qsecoes.put(tempo, vetorVazaoSecoes);
			pressaoTransiente.put(distancia[cont5], Hsecoes);
			vazaoTransiente.put(distancia[cont5], Qsecoes);
			cont5++;
		}
		
		// Seções da válvula -> segundo passo de tempo
		if (cont5 == distancia.length - 1) {

			Cp = vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
					+ Ca * pressaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
					- R * Dt * vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]
							* Math.abs(vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[0]);

			if (tempo[1] < tc) {
				porcFechamento = tempo[1] / tc;
			} else {
				porcFechamento = 1;
			}
			if (porcFechamento < 1) {
				Cdvariavel = Cd - 0.0349 * Math.pow(porcFechamento, 4) - 0.9053 * Math.pow(porcFechamento, 3)
						+ 1.3027 * Math.pow(porcFechamento, 2) + 0.2692 * porcFechamento;
			} else {
				Cdvariavel = 0;
			}
			if (porcFechamento != 1) {
				Av = 1.4286 * Cdvariavel + 3E-16;
			} else {
				Av = 0;
			}

			tau = (Cdvariavel * Av) / Cd;
			Cv = Math.pow(vazao * tau, 2) / (Ca * pressaoTransiente.get(distancia[cont5]).get(tempo)[0]);
			Qp = -1 / Ca * Cv + Math.sqrt(Math.pow((-1 / Ca * Cv), 2) + 2 * Cv * Cp);
			Hp = (Cp - Qp) / Ca;

			Map<double[], double[]> Hvalv = new HashMap<>();
			Map<double[], double[]> Qvalv = new HashMap<>();
			double[] vetorPressaoValv = new double[(int) numeroPassoTempo];
			double[] vetorVazaoValv = new double[(int) numeroPassoTempo];
			vetorPressaoValv[0] = pressaoTransiente.get(distancia[cont5]).get(tempo)[0];
			vetorVazaoValv[0] = vazaoTransiente.get(distancia[cont5]).get(tempo)[0];
			vetorPressaoValv[1] = Hp;
			vetorVazaoValv[1] = Qp;
			Hvalv.put(tempo, vetorPressaoValv);
			Qvalv.put(tempo, vetorVazaoValv);
			pressaoTransiente.put(distancia[cont5], Hvalv);
			vazaoTransiente.put(distancia[cont5], Qvalv);
		}

		cont5 = 0;
		double[] vetorVazaoReservatorio = new double[(int) numeroPassoTempo];
		
		// Demais passos de tempo
		for (contK = 2; contK < numeroPassoTempo; contK++) {
			
			if (distancia[cont5] == 0) {
				Cn = vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
						- Ca * pressaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
						- R * Dt * vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
								* Math.abs(vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]);
				Qp = Cn + Ca * Hres.get(tempo)[contK - 1];
				
				Map<double[], double[]> Qreservatorio = new HashMap<>();
				
				for (int i = 0; i < contK; i++) {
					vetorVazaoReservatorio[i] = vazaoTransiente.get(distancia[cont5]).get(tempo)[i];
				}
				vetorVazaoReservatorio[contK] = Qp;
				Qreservatorio.put(tempo, vetorVazaoReservatorio);
				vazaoTransiente.put(distancia[cont5], Qreservatorio);
			}
			cont5++;
			
			while (cont5 < distancia.length - 1) {
				
				Cn = vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
						- Ca * pressaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
						- R * Dt * vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]
								* Math.abs(vazaoTransiente.get(distancia[cont5 + 1]).get(tempo)[contK - 1]);
				Cp = vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
						+ Ca * pressaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
						- R * Dt * vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
								* Math.abs(vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]);
				Qp = 0.5 * (Cn + Cp);
				Hp = (Qp - Cn) / Ca;

				Map<double[], double[]> Hsecoes = new HashMap<>();
				Map<double[], double[]> Qsecoes = new HashMap<>();
				double[] vetorPressaoSecoes = new double[(int) numeroPassoTempo];
				double[] vetorVazaoSecoes = new double[(int) numeroPassoTempo];
				
				for (int i = 0; i < contK; i++) {
					vetorPressaoSecoes[i] = pressaoTransiente.get(distancia[cont5]).get(tempo)[i];
					vetorVazaoSecoes[i] = vazaoTransiente.get(distancia[cont5]).get(tempo)[i];
				}
				
				vetorPressaoSecoes[contK] = Hp;
				vetorVazaoSecoes[contK] = Qp;
				Hsecoes.put(tempo, vetorPressaoSecoes);
				Qsecoes.put(tempo, vetorVazaoSecoes);
				pressaoTransiente.put(distancia[cont5], Hsecoes);
				vazaoTransiente.put(distancia[cont5], Qsecoes);
				cont5++;
			}
			
			if (cont5 == distancia.length - 1) {
				
				Cp = vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
						+ Ca * pressaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
						- R * Dt * vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]
								* Math.abs(vazaoTransiente.get(distancia[cont5 - 1]).get(tempo)[contK - 1]);

				if (tempo[contK] < tc) {
					porcFechamento = tempo[contK] / tc;
				} else {
					porcFechamento = 1;
				}
				if (porcFechamento < 1) {
					Cdvariavel = Cd - 0.0349 * Math.pow(porcFechamento, 4) - 0.9053 * Math.pow(porcFechamento, 3)
							+ 1.3027 * Math.pow(porcFechamento, 2) + 0.2692 * porcFechamento;
				} else {
					Cdvariavel = 0;
				}
				if (porcFechamento != 1) {
					Av = 1.4286 * Cdvariavel + 3E-16;
				} else {
					Av = 0;
				}

				tau = (Cdvariavel * Av) / Cd;
				Cv = Math.pow(vazao * tau, 2) / (Ca * pressaoTransiente.get(distancia[cont5]).get(tempo)[0]);
				Qp = -1 / Ca * Cv + Math.sqrt(Math.pow((-1 / Ca * Cv), 2) + 2 * Cv * Cp);
				Hp = (Cp - Qp) / Ca;

				Map<double[], double[]> Hvalv = new HashMap<>();
				Map<double[], double[]> Qvalv = new HashMap<>();
				double[] vetorPressaoValv = new double[(int) numeroPassoTempo];
				double[] vetorVazaoValv = new double[(int) numeroPassoTempo];
				
				for (int i = 0; i < contK; i++) {
					vetorPressaoValv[i] = pressaoTransiente.get(distancia[cont5]).get(tempo)[i];
					vetorVazaoValv[i] = vazaoTransiente.get(distancia[cont5]).get(tempo)[i];
				}
				
				vetorPressaoValv[contK] = Hp;
				vetorVazaoValv[contK] = Qp;
				Hvalv.put(tempo, vetorPressaoValv);
				Qvalv.put(tempo, vetorVazaoValv);
				pressaoTransiente.put(distancia[cont5], Hvalv);
				vazaoTransiente.put(distancia[cont5], Qvalv);
			}
			cont5 = 0;
		}

	}
	
	public double getDt() {
		return Dt;
	}
	
	public Map<double[], double[]> getPressao() {
		int cont = distancia.length - 1;
		return pressaoTransiente.get(distancia[cont]);
	}
	
	public Map<double[], double[]> getVazao() {
		int cont = distancia.length - 1;
		return vazaoTransiente.get(distancia[cont]);
	}

}
