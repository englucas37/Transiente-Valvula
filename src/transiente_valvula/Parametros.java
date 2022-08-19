package transiente_valvula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Parametros {

	protected List<String> listaTrechos = new ArrayList<>();
	protected List<String> listaDadosValvula = new ArrayList<>();
	
	public String getEndereco() {
		return endereco;
	}
	
	protected String endereco;
	protected Map<String,Double> mapaComprimentos = new HashMap<>();
	protected Map<String,Double> mapaDiametros = new HashMap<>();
	protected Map<String,Double> mapaRi = new HashMap<>();
	protected Map<String,Double> mapaRo = new HashMap<>();
	protected Map<String,Double> mapaEspessuras = new HashMap<>();
	protected Map<String,Double> mapaMElasticidade = new HashMap<>();
	protected Map<String,Double> mapaRugosidades = new HashMap<>();
	protected Map<String,Double> mapaCoefPoisson = new HashMap<>();
	protected Map<String,Double> mapaNumeroSubtrechos = new HashMap<>();
	protected Map<String,Double> mapaFatorAtrito = new HashMap<>();
	protected Map<Double,Double> pressaoPermanente = new TreeMap<>();
	protected Map<Double,Double> vazaoPermanente = new TreeMap<>();
	protected Map<Double,Map<double[],double[]>> pressaoTransiente = new TreeMap<>();
	protected Map<Double,Map<double[],double[]>> vazaoTransiente = new TreeMap<>();
	protected double vazao;
	protected double Em;
	protected double tc;
	protected double Cd;
	protected double DH;
	protected int divisoes_menor_trecho;
	protected double tempo_simulacao;
	protected double courant = 1;
	protected double Lacumulado;

}
