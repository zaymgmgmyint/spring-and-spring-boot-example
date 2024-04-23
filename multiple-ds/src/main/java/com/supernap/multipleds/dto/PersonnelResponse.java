package com.supernap.multipleds.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelResponse implements Serializable {

	private static final long serialVersionUID = -4650284089692626395L;

	private String code;

	// TODO add data object

	private String msg;

	private Integer result;

	private Boolean success;
}
