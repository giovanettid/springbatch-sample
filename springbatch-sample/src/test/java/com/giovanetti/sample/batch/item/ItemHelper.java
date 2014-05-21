package com.giovanetti.sample.batch.item;

import java.util.Arrays;
import java.util.List;

public class ItemHelper {

    public static List<User> listOf2UsersMapFromDB() {
        User user1 = new User();
        user1.setId("1");
        user1.setPrenom("prenom1");
        user1.setNom("nom1");
        User user2 = new User();
        user2.setId("2");
        user2.setPrenom("prenom2");
        user2.setNom("nom2");
        return Arrays.asList(user1, user2);
    }
}
