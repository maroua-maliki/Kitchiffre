package org.demo.demo.services;

import org.demo.demo.dao.UtilisateurDAO;
import org.demo.demo.entities.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UtilisateurDAO userDAO;

    public AuthService(UtilisateurDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<Utilisateur> login(String username, String password) {
        Optional<Utilisateur> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            System.out.println("Utilisateur trouvé: " + user.getUsername());
            System.out.println("Hash DB: " + user.getPasswordHash());
            boolean passwordOk = BCrypt.checkpw(password, user.getPasswordHash());
            System.out.println("Mot de passe correct? " + passwordOk);
            if (passwordOk) {
                return userOpt;
            }
        } else {
            System.out.println("Utilisateur non trouvé avec username: " + username);
        }
        return Optional.empty();
    }


    public boolean register(String username, String password, String role) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Utilisateur user = new Utilisateur(0, username, hashedPassword, role);
        return userDAO.save(user);
    }
}
