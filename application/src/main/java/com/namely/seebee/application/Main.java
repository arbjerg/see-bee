/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
