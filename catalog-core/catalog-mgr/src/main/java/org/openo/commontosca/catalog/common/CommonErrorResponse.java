/**
 * Copyright 2016 [ZTE] and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.catalog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 00164331
 * 
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonErrorResponse {

    private String code;

    private String message;

    /**
     * @param message2
     * @return
     */
    public static Object failure(String message) {
        return message;
    }

    public CommonErrorResponse(String message) {
        super();
        this.message = message;
    }

}