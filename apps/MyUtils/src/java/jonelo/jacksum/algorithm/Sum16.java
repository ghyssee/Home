/******************************************************************************
 *
 * Jacksum version 1.7.0 - checksum utility in Java
 * Copyright (C) 2001-2006 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: jonelo@jonelo.de
 *
 *****************************************************************************/

package jonelo.jacksum.algorithm;

public class Sum16 extends Sum8 {
    
    public Sum16() {
        super();
        value = 0;
    }
    
    public long getValue() {
        return value % 0x10000; // 2^16
    }
    
    public byte[] getByteArray() {
        long val = getValue();
        return new byte[]
        {(byte)((val>>8)&0xff),
         (byte)(val&0xff)};
    }

}

