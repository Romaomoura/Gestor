package com.romamoura.gestorfinanceiro.excessoes;

public class ErroAutenticacao extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErroAutenticacao(String msg) {
		super(msg);
	}
}
