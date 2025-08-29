package org.demo.demo.services;

import org.demo.demo.dao.UtilisateurDAO;
import org.demo.demo.entities.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final UtilisateurDAO utilisateurDAO;

    public EmployeeService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    public boolean addEmployee(String username, String password, String role) {
        // Validation des données
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }

        // Validation du format email @capgemini.com
        if (!username.matches("^[A-Za-z0-9._%+-]+@capgemini\\.com$")) {
            throw new IllegalArgumentException("L'adresse e-mail doit se terminer par @capgemini.com");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        // Vérifier si l'utilisateur existe déjà
        Optional<Utilisateur> existingUser = utilisateurDAO.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce nom existe déjà");
        }

        // Hachage sécurisé du mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Créer l'utilisateur
        Utilisateur newUser = new Utilisateur(0, username, hashedPassword, role != null ? role : "user");

        // Sauvegarder en base (appel correct de la méthode)
        return utilisateurDAO.save(newUser);
    }

    public boolean addEmployee(String username, String password) {
        return addEmployee(username, password, "user");
    }

    public List<Utilisateur> getAllEmployees() {
        return utilisateurDAO.getAllUtilisateurs();
    }

    public boolean deleteEmployee(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return utilisateurDAO.deleteUtilisateur(userId);
    }

    public boolean deleteEmployeeByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }

        Optional<Utilisateur> user = utilisateurDAO.findByUsername(username);
        if (user.isPresent()) {
            return utilisateurDAO.deleteUtilisateur(user.get().getId());
        }

        return false; // Utilisateur non trouvé
    }

    public boolean updateEmployeePassword(String username, String newPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        Optional<Utilisateur> userOpt = utilisateurDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPasswordHash(hashedPassword);

            return utilisateurDAO.updateUtilisateur(user);
        }

        return false; // Utilisateur non trouvé
    }

    public boolean updateEmployee(String oldUsername, String newUsername, String newPassword) {
        if (oldUsername == null || oldUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ancien nom d'utilisateur ne peut pas être vide");
        }

        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau nom d'utilisateur ne peut pas être vide");
        }

        // Validation du format email @capgemini.com pour le nouveau nom
        if (!newUsername.matches("^[A-Za-z0-9._%+-]+@capgemini\\.com$")) {
            throw new IllegalArgumentException("L'adresse e-mail doit se terminer par @capgemini.com");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        // Vérifier si l'ancien utilisateur existe
        Optional<Utilisateur> userOpt = utilisateurDAO.findByUsername(oldUsername);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        // Si le nom d'utilisateur change, vérifier qu'il n'existe pas déjà
        if (!oldUsername.equals(newUsername)) {
            Optional<Utilisateur> existingUser = utilisateurDAO.findByUsername(newUsername);
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("Un utilisateur avec ce nom existe déjà");
            }
        }

        // Mettre à jour l'utilisateur
        Utilisateur user = userOpt.get();
        user.setUsername(newUsername);
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);

        return utilisateurDAO.updateUtilisateur(user);
    }

    public Optional<Utilisateur> findEmployeeByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return utilisateurDAO.findByUsername(username);
    }

    public int getEmployeeCount() {
        return getAllEmployees().size();
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !utilisateurDAO.findByUsername(username).isPresent();
    }
}
