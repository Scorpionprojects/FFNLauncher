/*
 * FFNLauncher
 * Copyright (C) 2013 Abel Hoogeveen <http://www.sigmacoders.nl>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.ffnmaster.mclauncher;

import java.util.EventObject;

public class StatusChangeEvent extends EventObject {
    
    private static final long serialVersionUID = 9166371811989025905L;
    private String message;

    public StatusChangeEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

}
