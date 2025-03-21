package sn.groupeisi.projetgestionprofesseurs.entities;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Email {

    private final String username = "diallomadina222@gmail.com"; // Remplacez par votre email
    private final String password = "puuq wjrl mvyt mdqq"; // Remplacez par votre mot de passe ou mot de passe d'application

    public String envoyerEmailProfesseur(String destinataire, String nomProfesseur, String nomCours,
                                         String jour, String heureDebut, String heureFin, String salle) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Attribution d'un nouveau cours - SAKKU SCHOOL");

            String contenu = "Bonjour " + nomProfesseur + ",\n\n" +
                    "Nous vous informons que vous avez été assigné(e) à un nouveau cours:\n\n" +
                    "Nom du cours: " + nomCours + "\n" +
                    "Jour: " + jour + "\n" +
                    "Horaire: " + heureDebut + " - " + heureFin + "\n" +
                    "Salle: " + salle + "\n\n" +
                    "Nous vous remercions pour votre collaboration.\n\n" +
                    "Cordialement,\n" +
                    "Administration - SAKKU SCHOOL";

            message.setText(contenu);
            Transport.send(message);

            System.out.println("Email envoyé avec succès à " + destinataire);
        return contenu;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        return "";
    }
}