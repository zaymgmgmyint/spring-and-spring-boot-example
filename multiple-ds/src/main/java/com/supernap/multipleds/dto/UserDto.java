package com.supernap.multipleds.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

	private static final long serialVersionUID = 7671113765860327445L;

	private String userId;

	private String name;

	private String userCardNumber;

	private String pinNumber;

	private String imageId;

	private String created;

	private String picture;

	private String phoneNumber;

}
