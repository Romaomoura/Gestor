package com.romamoura.gestorfinanceiro.modelos.entidades;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lancamento")
@Builder /* Caso haja a necessidade de incluir um dado mockado para teste essa anotação do lombok facilita a inclusão */
@Data /* Essa anotação criar os métodos getters, setters, construtores, hashcodeEquals e toStrings */
@NoArgsConstructor
@AllArgsConstructor
public class Lancamento {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="descricao")
	private String descricao;
	
	@Column(name="mes")
	private Integer mes;
	
	@Column(name="ano")
	private Integer ano;
	
	@ManyToOne /* Tipo de relacionamento */
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	@Column(name="valor")
	private BigDecimal valor;

	/* @Convert(converter = DateToLocalDateConverter.class) --no curso foi usado o : @Converter = converter = Jsr310JpaConverters.LocalDateConverter.class */
	@Column(name = "data_cadastro")
	private Calendar dataCadastro; /* se usar o tipo LocalDate precisará da conversão */
	
	
	@Column(name = "tipo")
	@Enumerated(value = EnumType.STRING)
	private TipoLancamento tipo;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private StatusLancamento status;

}
