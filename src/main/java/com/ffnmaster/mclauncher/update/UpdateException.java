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

package com.ffnmaster.mclauncher.update;

public class UpdateException extends Exception {

    private static final long serialVersionUID = -3541423850696660199L;
    
    public UpdateException() {
    }
    
    public UpdateException(String message) {
        super(message);
    }
    
    public UpdateException(String message, Throwable t) {
        super(message, t);
    }

}
