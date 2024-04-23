package com.supernap.multipleds.dto;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelDto implements Serializable {

	private static final long serialVersionUID = 5830052962207041024L;

	private Long id;

	private String userId;

	private String name;

	private String faceId;

	private String cardNoId;

	private String phoneNo;

	private String address;

	private String tag;

	private String permissionType;

	private Integer dataSynced;

	private Date syncedDatetime;

	private Integer type;

}
