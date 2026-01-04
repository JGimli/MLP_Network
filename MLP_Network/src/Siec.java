
public class Siec {
	Warstwa [] warstwy;
	int liczba_warstw;

	public Siec(){
		warstwy=null;
		this.liczba_warstw=0;
	}
	public Siec(int liczba_wejsc,int liczba_warstw,int [] lnww){
		this.liczba_warstw=liczba_warstw;
		warstwy=new Warstwa[liczba_warstw];
		for(int i=0;i<liczba_warstw;i++)
			warstwy[i]=new Warstwa((i==0)?liczba_wejsc:lnww[i-1],lnww[i]);
	}
	double [] oblicz_wyjscie(double [] wejscia){
		double [] wyjscie=null;
		for(int i=0;i<liczba_warstw;i++)
			wejscia = wyjscie = warstwy[i].oblicz_wyjscie(wejscia);
		return wyjscie;
	}
	void back_popag(double[] target,double[] in_values,double learningRate) {
		double []out_err=warstwy[liczba_warstw-1].Get_error(target);
		double []pred_sigmoid=sigmoid_der(warstwy[liczba_warstw-1].values);
		
		double[]out_delt=delt(out_err,pred_sigmoid);
		
		
		warstwy[liczba_warstw-1].delt_err=out_delt;
		
		
		for(int i=liczba_warstw-1;i>=1;i--) {
			warstwy[i-1].delt_err=warstwy[i].BackProp(warstwy[i].delt_err,warstwy[i-1].values);
		}
		
		
		for(int i=liczba_warstw-1;i>=1;i--) {
			warstwy[i].update_weights(warstwy[i-1].values,learningRate);
		}
		warstwy[0].update_weights(in_values,learningRate);
		
		
	}
	private double[] delt(double[] out_err, double[] pred_sigmoid) {
		double [] arr=new double[out_err.length];
		for(int i=0;i<arr.length;i++) {
			arr[i]=out_err[i]*pred_sigmoid[i];
		}
		return arr;
	}
	double [] sigmoid_der(double[] arr) {
		double temp[]=arr.clone();
		for(int i=0;i<arr.length;i++) {
			temp[i]=arr[i]*(1-arr[i]);
		}
		return temp;
	}
	
}
