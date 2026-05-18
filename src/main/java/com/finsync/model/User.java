  package com.finsync.model;

  import org.springframework.data.annotation.Id;
  import org.springframework.data.relational.core.mapping.Table;

  @Table("users")
  public class User {

      @Id
      private Integer id;
      private String email;
      private String password;
      private String role;
      private boolean verified;
      private String verificationToken;

      public User() {}

      public User(String email, String password, String role) {
          this.email = email;
          this.password = password;
          this.role = role;
      }

      public Integer getId() { return id; }
      public void setId(Integer id) { this.id = id; }

      public String getEmail() { return email; }
      public void setEmail(String email) { this.email = email; }

      public String getPassword() { return password; }
      public void setPassword(String password) { this.password = password; }

      public String getRole() { return role; }
      public void setRole(String role) { this.role = role; }

      public boolean isVerified() { return verified; }
      public void setVerified(boolean verified) { this.verified = verified; }

      public String getVerificationToken() { return verificationToken; }
      public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
  }