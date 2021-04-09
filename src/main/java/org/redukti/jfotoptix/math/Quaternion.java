/*
The software is ported from Goptical, hence is licensed under the GPL.
Copyright (c) 2021 Dibyendu Majumdar

Original GNU Optical License and Authors are as follows:

      The Goptical library is free software; you can redistribute it
      and/or modify it under the terms of the GNU General Public
      License as published by the Free Software Foundation; either
      version 3 of the License, or (at your option) any later version.

      The Goptical library is distributed in the hope that it will be
      useful, but WITHOUT ANY WARRANTY; without even the implied
      warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
      See the GNU General Public License for more details.

      You should have received a copy of the GNU General Public
      License along with the Goptical library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307 USA

      Copyright (C) 2010-2011 Free Software Foundation, Inc
      Author: Alexandre Becoulet
 */


package org.redukti.jfotoptix.math;

public class Quaternion {
    final double x, y, z, w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    Quaternion (Vector3 a, Vector3 b)
    {
        Vector3 cp = a.crossProduct (b);
        double _x = cp.x ();
        double _y = cp.y ();
        double _z = cp.z ();
        double _w = a.dotProduct(b) + 1.0;
        double n = norm(_x, _y, _z, _w);
        x = _x/n;
        y = _y/n;
        z = _z/n;
        w = _w/n;
    }

    static final double norm (double x, double y, double z, double w)
    {
        return Math.sqrt (x * x + y * y + z * z + w * w);
    }


}
