/*
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2019 Bruce Mayhew
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * Getting Source ==============
 *
 * Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository for free software projects.
 */

package org.owasp.webgoat.challenges.challenge5;

import lombok.extern.slf4j.Slf4j;
import org.owasp.webgoat.assignments.AssignmentEndpoint;
import org.owasp.webgoat.assignments.AttackResult;
import org.owasp.webgoat.challenges.Flag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@Slf4j
public class Assignment5 extends AssignmentEndpoint {

    private final DataSource dataSource;

    public Assignment5(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/challenge/5")
    @ResponseBody
    public AttackResult login(@RequestParam String username, @RequestParam String password) throws SQLException {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return failed(this).feedback("required4").build();
        }
        if (!"Larry".equals(username)) {
            return failed(this).feedback("user.not.larry").feedbackArgs(username).build();
        }
        PreparedStatement statement = null;

        try (var connection = dataSource.getConnection()) {
            String query = "select password from challenge_users where userid = ? and password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return success(this).feedback("challenge.solved").feedbackArgs(Flag.FLAGS.get(5)).build();
            } else {
                return failed(this).feedback("challenge.close").build();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
