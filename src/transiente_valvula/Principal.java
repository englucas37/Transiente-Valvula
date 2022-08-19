package transiente_valvula;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class Principal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String endereco = "C:\\transiente_valvula\\Entrada.txt";
	//	Entrada entrada = new Entrada(endereco);
	//	Memorial memorial = new Memorial(entrada, null);
		Entrada entrada = new Entrada(endereco);
		Calculo calculo = new Calculo(endereco);
		DecimalFormat df2 = new DecimalFormat("0.0000");
		DecimalFormat df3 = new DecimalFormat("0.0000000");

		
	//	new Interface(calculo);
		
		File arquivo = new File("C:\\transiente_valvula\\Resultado.txt");
		
		if (arquivo.isFile()) {
			arquivo.delete();
		}
		
		try {
			FileWriter escrever = new FileWriter(arquivo);
			BufferedWriter escreverbf = new BufferedWriter(escrever);
			
			escreverbf.write("[RESULTADO DO TRANSIENTE NA SEÇÃO DA VÁLVULA]");
			escreverbf.newLine();
			escreverbf.newLine();
			
			escreverbf.write("t(s)	P(mca)	Q(m³/s)");
			escreverbf.newLine();
			double[] dadosTempo = calculo.tempo;
			
			for (int i = 0; i < calculo.contK; i ++) {
				
				double dadosPressao = calculo.pressaoTransiente.get(calculo.distancia[calculo.distancia.length - 1]).get(calculo.tempo)[i];
				double dadosVazao = calculo.vazaoTransiente.get(calculo.distancia[calculo.distancia.length - 1]).get(calculo.tempo)[i];
				double temp = dadosTempo[i];
				escreverbf.write(df2.format(temp) + "	" + df2.format(dadosPressao) + "	" + df3.format(dadosVazao));
				escreverbf.newLine();
			}
			
			escreverbf.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
