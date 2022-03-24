package br.ufes.edu.compiladores.code;

public class Word {
    
	private byte[] bytes;

	// Cria e retorna uma word a partir do inteiro passado.
	// Apesar do inteiro ter sinal, é interpretado como se
	// fosse um uint32.
	public static Word fromInt(int value) {
		Word word = new Word();
		word.bytes = new byte[] {
						(byte)(value >>> 24),
						(byte)(value >>> 16),
						(byte)(value >>> 8 ),
						(byte)(value >>> 0 )
		          	 };
		return word;
	}
	
	// Cria e retorna uma word a partir do float passado.
	public static Word fromFloat(float value) {
		int intBits = Float.floatToIntBits(value);
		return fromInt(intBits);
	}
	
	// Interpreta a word como um valor inteiro.
	public int toInt() {
		return ((bytes[0] & 0xFF) << 24) | 
	           ((bytes[1] & 0xFF) << 16) | 
	           ((bytes[2] & 0xFF) << 8 ) | 
	           ((bytes[3] & 0xFF) << 0 );
	}
	
	// Interpreta a word como um valor float.
	public float toFloat() {
		int intBits = this.toInt();
		return Float.intBitsToFloat(intBits);
	}
}
