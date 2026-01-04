import java.util.Iterator;

public class Warstwa {
	Neuron [] neurony;
	
	//lista wartości neyronów z ostatniego przejścia wartości
	double [] values;
	
	//lista wartości błędów neyronów z ostatniego przejścia wartości
	double [] delt_err;
	
	int liczba_neuronow;
	
	public Warstwa(){
		neurony=null;
		values=null;
		delt_err=null;
		liczba_neuronow=0;
	}
	public Warstwa(int liczba_wejsc,int liczba_neuronow){
		this.liczba_neuronow=liczba_neuronow;
		neurony=new Neuron[liczba_neuronow];
		values=new double[liczba_neuronow];
		delt_err=new double[liczba_neuronow];
		for(int i=0;i<liczba_neuronow;i++)
			neurony[i]=new Neuron(liczba_wejsc);
	}
	double [] oblicz_wyjscie(double [] wejscia){
		double [] wyjscie=new double[liczba_neuronow];
		for(int i=0;i<liczba_neuronow;i++)
			wyjscie[i]=neurony[i].oblicz_wyjscie(wejscia);
		values=wyjscie;
		return wyjscie;
	}

	//Obliczanie błedu dla warstwy wyjściowej
	
	double[] Get_error(double[] target) {
		double [] errors=new double[liczba_neuronow];
		for(int i=0;i<liczba_neuronow;i++) {
			errors[i]=target[i]-values[i];
		}
		return errors;
	}
	
	//Obliczanie błedu dla warstw urytych
	
	public double[] BackProp(double[] out_delt, double[] values) {
		double [] delt_err_temp=new double [values.length];
		for(int i=0;i<values.length;i++) {
			double err=0;
			for(int j=0;j<out_delt.length;j++) {
				err+=neurony[j].wagi[j+1]*out_delt[j];
			}
			delt_err_temp[i]=err*(values[i]*(1-values[i]));
		}
		return delt_err_temp;
		
	}
	public void update_weights(double[] values,double learningRate) {
		for(int i=0;i<liczba_neuronow;i++) {
				neurony[i].update_weights(values,delt_err[i],learningRate);
				neurony[i].wagi[0]+=delt_err[i]*learningRate;
		}
	}
	
}
