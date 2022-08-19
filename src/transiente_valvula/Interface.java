package transiente_valvula;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Interface extends JDialog {

	private static final long serialVersionUID = -7217280580778981376L;
	
	Entrada entrada;
	Calculo calculo;
//	Memorial memorial;
	DecimalFormat df4 = new DecimalFormat("0.0000");
	
	public Interface(Entrada entrada) {
		
		setResizable(false);
		
		this.entrada = entrada;
	//	this.calculo = calculo;
		
		XYDataset dadosPressao = criarDatasetPressao();
		XYDataset dadosVazao = criarDatasetVazao();
		
		JFreeChart graficoPressao = ChartFactory.createXYLineChart(("Transiente na Válvula" + "- Pressão"),
				"Tempo (s)", "Pressão (mca)", dadosPressao, PlotOrientation.VERTICAL, true, true, false);
		
		JFreeChart graficoVazao = ChartFactory.createXYLineChart(("Transiente na Válvula" + "- Vazão"),
				"Tempo (s)", "Vazão (m³/s)", dadosVazao, PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plotPressao = graficoPressao.getXYPlot();
		XYPlot plotVazao = graficoVazao.getXYPlot();
		
		AbstractRenderer r1 = (AbstractRenderer) plotPressao.getRenderer(0);
		
		r1.setSeriesPaint(0, Color.blue);
	//	r1.setSeriesPaint(1, Color.red);
		r1.setSeriesStroke(0, new BasicStroke(2));
		r1.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f,
				new float[] { 100.0f, 100.0f }, 0.0f));
		
		AbstractRenderer r2 = (AbstractRenderer) plotVazao.getRenderer(0);
		
		r2.setSeriesPaint(0, Color.blue);
	//	r2.setSeriesPaint(1, Color.red);
		r2.setSeriesStroke(0, new BasicStroke(2));
		r2.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f,
				new float[] { 100.0f, 100.0f }, 0.0f));
		
		plotPressao.setBackgroundPaint(Color.white);

		plotPressao.setRangeGridlinesVisible(true);
		plotPressao.setRangeGridlinePaint(Color.GRAY);

		plotPressao.setDomainGridlinesVisible(true);
		plotPressao.setDomainGridlinePaint(Color.GRAY);
		
		plotVazao.setBackgroundPaint(Color.white);

		plotVazao.setRangeGridlinesVisible(true);
		plotVazao.setRangeGridlinePaint(Color.GRAY);

		plotVazao.setDomainGridlinesVisible(true);
		plotVazao.setDomainGridlinePaint(Color.GRAY);
		
		graficoPressao.getLegend().setFrame(BlockBorder.NONE);
		
		graficoVazao.getLegend().setFrame(BlockBorder.NONE);
		
		setTitle("Modelagem do trasiente hidr\\u00C1ulico devido ao fechamento r\\u00C1pido da v\\u00C1lvula de jusante");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/icone/images.png")));
		
		getContentPane().setLayout(null);
		
		ChartPanel painelPressao = new ChartPanel(graficoPressao);
		painelPressao.setBounds(20, 50, 940, 210);
		getContentPane().add(painelPressao);
		
		painelPressao.setZoomAroundAnchor(true);
		painelPressao.setRefreshBuffer(true);
		painelPressao.setMouseWheelEnabled(true);
		painelPressao.setMouseZoomable(true);
		painelPressao.setBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.BLACK, null));
		painelPressao.setDisplayToolTips(true);
		painelPressao.setLayout(new GridLayout(1, 0, 0, 0));
		
		ChartPanel painelVazao = new ChartPanel(graficoVazao);
		painelVazao.setBounds(20, 260, 940, 210);
		getContentPane().add(painelVazao);

		painelVazao.setZoomAroundAnchor(true);
		painelVazao.setRefreshBuffer(true);
		painelVazao.setMouseWheelEnabled(true);
		painelVazao.setMouseZoomable(true);
		painelVazao.setBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.BLACK, null));
		painelVazao.setDisplayToolTips(true);
		painelVazao.setLayout(new GridLayout(1, 0, 0, 0));
		
/*		JButton button = new JButton("Memorial");
		button.setBounds(10, 11, 89, 23);
		getContentPane().add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					memorial = new Memorial(entrada, calculo); // criar construtor
				} catch (BadElementException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); */
		
		setSize(1000, 720);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		int width = 550; /* Width of the image */
		int height = 250; /* Height of the image */
		File caminhoPressao = new File("C:\\transiente_valvula\\Pressao.png");
		File caminhoVazao = new File("C:\\transiente_valvula\\Vazao.png");
		
		try {
			if (caminhoPressao.isFile() && caminhoVazao.isFile()) {
				caminhoPressao.delete();
				caminhoVazao.delete();
			}
			ChartUtilities.saveChartAsPNG(caminhoPressao, graficoPressao, width, height);
			ChartUtilities.saveChartAsPNG(caminhoVazao, graficoVazao, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private XYDataset criarDatasetPressao() {
		
		XYSeriesCollection pressao = new XYSeriesCollection();
		
		XYSeries seriesPressao = new XYSeries("Pressão");
		
		for (double i = 0.0; i < calculo.getPressao().size(); i = i + calculo.getDt()) {
			
			double[] dadosTemp = calculo.getPressao().get(i);
			
			seriesPressao.add(i, dadosTemp[1]);
		}
		
		pressao.addSeries(seriesPressao);
		
		return pressao;
	}
	
	private XYDataset criarDatasetVazao() {
		
		XYSeriesCollection vazao = new XYSeriesCollection();
		
		XYSeries seriesVazao = new XYSeries("Vazão");
		
		for (double i = 0.0; i < calculo.getVazao().size(); i = i + calculo.getDt()) {
			
			double[] dadosTemp = calculo.getVazao().get(i);
			
			seriesVazao.add(i, dadosTemp[2]);
		}
		
		vazao.addSeries(seriesVazao);

		return vazao;
	}
}
