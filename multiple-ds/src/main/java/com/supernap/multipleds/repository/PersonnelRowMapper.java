package com.supernap.multipleds.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.supernap.multipleds.dto.PersonnelDto;

public class PersonnelRowMapper implements RowMapper<PersonnelDto> {

	@Override
	public PersonnelDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		PersonnelDto entity = new PersonnelDto();
		entity.setId(rs.getLong("id"));
		entity.setUserId(rs.getString("user_id"));
		entity.setCardNoId(rs.getString("card_no_id"));
		entity.setFaceId(rs.getString("face_id"));
		entity.setPhoneNo(rs.getString("phone_no"));
		entity.setAddress(rs.getString("address"));
		entity.setTag(rs.getString("tag"));
		entity.setDataSynced(rs.getInt("data_synced"));
		entity.setSyncedDatetime(rs.getDate("synced_datetime"));
		entity.setType(rs.getInt("type"));

		return entity;
	}

}
