package com.romamoura.gestorfinanceiro.excessoes;

public class RegraNegocioExcecao extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RegraNegocioExcecao(String msg) {
		super(msg);
	}

}
