
public class Corners {
	public float t;
	public float b;
	public float l;
	public float r;
	public int total;
	
	public Corners(float t, float b, float l, float r, int total){
		this.t= t;
		this.b = b;
		this.l = l;
		this.r = r;
		this.total = total;
	}
	public static Corners cornerMerge(Corners first, Corners second){
		Corners result = first; 
		if (second.b < result.b){
			result.b = second.b;
		}
		if (second.t >result.t){
			result.t =second.t;
		}
		if (second.l < result.l){
			result.l = second.l;
		}
		if (second.r > result.r){
			result.r = second.r;
		}
		result.total = first.total + second.total;
		return result; 
	}

}
