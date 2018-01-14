package com.namely.seebee.application;

import java.util.ArrayList;
import java.util.List;
import com.namely.seebee.repository.Repository;

/**
 *
 * @author Per Minborg
 */
public class Main {

    public static void main(String[] args) {
        final Repository app = Repository.builder()
            .provide(String.class).applying(b -> "Tryggve")
            .provide(List.class).getting(ArrayList::new)
            .provide(Integer.class).with(1)
            .provide(Integer.class).with(2)
            .build();

        System.out.println("TypeMapper components");
        app.stream(Integer.class)
            .forEach(System.out::println);

    }

}
