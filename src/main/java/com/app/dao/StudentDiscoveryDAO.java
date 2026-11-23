package com.app.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.app.dto.StudentDiscoveryDTO;

@Repository
public class StudentDiscoveryDAO {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<StudentDiscoveryDTO> findStudents(
            String currentUserId) {

        String sql = "SELECT " +
                "u.id AS userId, " +
                "u.user_name AS userName, " +
                "u.full_name AS fullName, " +
                "p.program AS program, " +
                "p.major AS major, " +
                "p.academic_year AS academicYear, " +
                "p.bio AS bio, " +
                "p.profile_image_url AS profileImageUrl, " +
                "COUNT(DISTINCT mine.interest_id) AS sharedCount " +
                "FROM tb_users u " +
                "INNER JOIN tb_student_profile p " +
                "ON p.user_id = u.id " +
                "LEFT JOIN tb_student_interests theirs " +
                "ON theirs.user_id = u.id " +
                "LEFT JOIN tb_student_interests mine " +
                "ON mine.interest_id = theirs.interest_id " +
                "AND mine.user_id = :currentUserId " +
                "WHERE u.id <> :currentUserId " +
                "AND u.deleted_on IS NULL " +
                "AND UPPER(u.status) = 'ACTIVE' " +
                "GROUP BY u.id, u.user_name, u.full_name, " +
                "p.program, p.major, p.academic_year, " +
                "p.bio, p.profile_image_url " +
                "ORDER BY sharedCount DESC, u.full_name ASC " +
                "LIMIT 50";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("currentUserId", currentUserId);

        List<StudentDiscoveryDTO> students = jdbcTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> {

                    StudentDiscoveryDTO dto = new StudentDiscoveryDTO();

                    dto.setUserId(
                            rs.getString("userId"));
                    dto.setUserName(
                            rs.getString("userName"));
                    dto.setFullName(
                            rs.getString("fullName"));
                    dto.setProgram(
                            rs.getString("program"));
                    dto.setMajor(
                            rs.getString("major"));
                    dto.setAcademicYear(
                            rs.getString("academicYear"));
                    dto.setBio(
                            rs.getString("bio"));
                    dto.setProfileImageUrl(
                            rs.getString("profileImageUrl"));
                    dto.setSharedInterestCount(
                            rs.getInt("sharedCount"));

                    return dto;
                });

        if (students.isEmpty()) {
            return students;
        }

        List<String> ids = new ArrayList<>();

        Map<String, StudentDiscoveryDTO> studentMap = new HashMap<>();

        for (StudentDiscoveryDTO student : students) {
            ids.add(student.getUserId());
            studentMap.put(student.getUserId(), student);
        }

        String interestsSql = "SELECT DISTINCT si.user_id AS userId, " +
                "i.name AS interestName " +
                "FROM tb_student_interests si " +
                "INNER JOIN tb_interests i " +
                "ON i.id = si.interest_id " +
                "INNER JOIN tb_student_interests mine " +
                "ON mine.interest_id = si.interest_id " +
                "WHERE mine.user_id = :currentUserId " +
                "AND si.user_id IN (:studentIds) " +
                "ORDER BY si.user_id, i.name";

        MapSqlParameterSource interestParams = new MapSqlParameterSource()
                .addValue("currentUserId", currentUserId)
                .addValue("studentIds", ids);

        jdbcTemplate.query(
                interestsSql,
                interestParams,
                rs -> {
                    String userId = rs.getString("userId");

                    StudentDiscoveryDTO student = studentMap.get(userId);

                    if (student != null) {
                        student.getSharedInterests().add(
                                rs.getString("interestName"));
                    }
                });

        return students;
    }
}