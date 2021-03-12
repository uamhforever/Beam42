package org.redukti.jfotoptix.data;

import org.redukti.jfotoptix.math.Vector2;
import org.redukti.jfotoptix.util.ArrayIndex2D;

import java.util.ArrayList;

import static org.redukti.jfotoptix.util.MathUtils.square;

public class Interpolated1d extends DiscreteSetBase {

    static final class PolyS {
        double a, b, c, d;

        public PolyS(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    ArrayList<PolyS> _poly;

    void set_interpolation(Interpolation i) {
    }

    @Override
    public double interpolate(double x) {
        return 0;
    }

    @Override
    public double interpolate(double x, int deriv) {
        return 0;
    }

    void compute_cubic_2nd_deriv(cubic_2nd_deriv_init_e de, int n, double dd[],
                                 double d0, double dn) {
        // double eq[n][3];
        double[] eq = new double[n * 3];
        ArrayIndex2D idx = new ArrayIndex2D(n, 3);

        // first and last tridiag system equations
        switch (de) {
            case Cubic2ndDerivQuadratic:
                dd[0] = dd[n - 1] = 0.0;
                eq[idx.i(0, 1)] = eq[idx.i(n - 1, 1)] = 1.0;
                eq[idx.i(1, 0)] = eq[idx.i(n - 2, 2)] = -1.0;
                break;

            case Cubic2ndDerivFirst: {
                // first derivative is prescribed for first and last point
                double x0 = super.get_x_interval(0);
                double xn = super.get_x_interval(n - 2);

                dd[0] = (super.get_y_value(1) - super.get_y_value(0)) / x0 - d0;
                dd[n - 1]
                        = dn - (super.get_y_value(n - 1) - super.get_y_value(n - 2)) / xn;
                eq[idx.i(0, 1)] = x0 / 3.0;
                eq[idx.i(1, 0)] = x0 / 6.0;
                eq[idx.i(n - 2, 2)] = xn / 6.0;
                eq[idx.i(n - 1, 1)] = xn / 3.0;
                break;
            }

            case Cubic2ndDerivSecond:
                // second derivative is prescribed for first and last point
                dd[0] = d0;
                dd[n - 1] = dn;
                eq[idx.i(1, 0)] = eq[idx.i(n - 2, 2)] = 0.0;
                eq[idx.i(0, 1)] = eq[idx.i(n - 1, 1)] = 1.0;
                break;
        }

        int i;

        // middle tridiag system equations
        for (i = 1; i < (int) n - 1; i++) {
            eq[idx.i(i - 1, 2)] = super.get_x_interval(i - 1) / 6.0;
            eq[idx.i(i, 1)] = super.get_x_interval(i - 1, i + 1) / 3.0;
            eq[idx.i(i + 1, 0)] = super.get_x_interval(i) / 6.0;
            dd[i] = (super.get_y_value(i + 1) - super.get_y_value(i))
                    / super.get_x_interval(i)
                    - (super.get_y_value(i) - super.get_y_value(i - 1))
                    / super.get_x_interval(i - 1);
        }

        // solve tridiag system
        // forward substitution
        for (i = 1; i < (int) n; i++) {
            double f = eq[idx.i(i - 1, 2)] / eq[idx.i(i - 1, 1)];
            eq[idx.i(i, 1)] -= f * eq[idx.i(i, 0)];
            dd[i] -= f * dd[i - 1];
        }

        // backward substitution
        double k = 0;
        for (i = n - 1; i >= 0; i--) {
            double ddi = (dd[i] - k) / eq[idx.i(i, 1)];
            dd[i] = ddi;
            k = eq[idx.i(i, 0)] * ddi;
        }
    }

    void set_linear_poly(PolyS p, double p1x, double p1y,
                         double p2x, double p2y) {
        p.a = 0.0;
        p.b = 0.0;
        p.c = (p1y - p2y) / (p1x - p2x);
        p.d = (p2x * p1y - p1x * p2y) / (p2x - p1x);
    }

    void set_linear_poly(PolyS p, double p1x, double p1y,
                         double d1) {
        p.a = 0.0;
        p.b = 0.0;
        p.c = d1;
        p.d = p1y - d1 * p1x;
    }

    void set_quadratic_poly(PolyS p, double p1x, double p1y,
                            double p2x, double p2y, double p3x,
                            double p3y) {
        double n = ((p2x - p1x) * (p3x - p1x) * (p3x - p2x));

        p.a = 0.0;

        p.b = (p3y * (p2x - p1x) + p2y * (p1x - p3x) + p1y * (p3x - p2x)) / n;

        double p1x2 = square(p1x);
        double p2x2 = square(p2x);
        double p3x2 = square(p3x);

        p.c = (p3y * (p1x2 - p2x2) + p2y * (p3x2 - p1x2) + p1y * (p2x2 - p3x2)) / n;

        p.d = (p3y * (p1x * p2x2 - p2x * p1x2) + p2y * (p3x * p1x2 - p1x * p3x2)
                + p1y * (p2x * p3x2 - p3x * p2x2))
                / n;
    }

    void set_cubic_poly(PolyS p, double p1x, double p1y,
                        double p2x, double p2y, double d1, double d2) {
  /*
    a=-(-2*y2+2*y1+(d2+d1)*x2+(-d2-d1)*x1)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3),
    b=(-3*x2*y2+x1*((d2-d1)*x2-3*y2)+(3*x2+3*x1)*y1+(d2+2*d1)*x2^2+(-2*d2-d1)*x1^2)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3),
    c=-(x1*((2*d2+d1)*x2^2-6*x2*y2)+6*x1*x2*y1+d1*x2^3+(-d2-2*d1)*x1^2*x2-d2*x1^3)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3),
    d=(x1^2*((d2-d1)*x2^2-3*x2*y2)+x1^3*(y2-d2*x2)+(3*x1*x2^2-x2^3)*y1+d1*x1*x2^3)/(-x2^3+3*x1*x2^2-3*x1^2*x2+x1^3)
  */
        double x1 = p1x;
        double x2 = p2x;
        double y1 = p1y;
        double y2 = p2y;

        // FIXME simplify

        p.a = -(2. * y1 - 2. * y2 + (d2 + d1) * x2 - (d2 + d1) * x1)
                / (3. * x1 * x2 * x2 - x2 * x2 * x2 - 3. * x1 * x1 * x2
                + x1 * x1 * x1);

        p.b = (x1 * ((d2 - d1) * x2 - 3. * y2) - 3. * x2 * y2
                + (3. * x2 + 3. * x1) * y1 + (d2 + 2. * d1) * x2 * x2
                - (2. * d2 + d1) * x1 * x1)
                / (3. * x1 * x2 * x2 - x2 * x2 * x2 - 3. * x1 * x1 * x2
                + x1 * x1 * x1);

        p.c = -(x1 * ((2. * d2 + d1) * x2 * x2 - 6. * x2 * y2) + 6. * x1 * x2 * y1
                + d1 * x2 * x2 * x2 - (d2 + 2. * d1) * x1 * x1 * x2
                - d2 * x1 * x1 * x1)
                / (3. * x1 * x2 * x2 - x2 * x2 * x2 - 3. * x1 * x1 * x2
                + x1 * x1 * x1);

        p.d = (x1 * x1 * ((d2 - d1) * x2 * x2 - 3. * x2 * y2)
                + x1 * x1 * x1 * (y2 - d2 * x2)
                + (3. * x1 * x2 * x2 - x2 * x2 * x2) * y1 + d1 * x1 * x2 * x2 * x2)
                / (3. * x1 * x2 * x2 - x2 * x2 * x2 - 3. * x1 * x1 * x2
                + x1 * x1 * x1);
    }

    void set_cubic_poly2(PolyS p, double p1x, double p1y,
                         double p2x, double p2y, double dd1,
                         double dd2) {
  /*
    a=(dd1-dd2)/(6*x1-6*x2);
    b=(dd2*x1-dd1*x2)/(2*x1-2*x2);
    c=(-6*y2+6*y1+(dd2+2*dd1)*x2^2+(2*dd1-2*dd2)*x1*x2+(-2*dd2-dd1)*x1^2)/(6*x1-6*x2);
    d=-(x1*((dd2+2*dd1)*x2^2-6*y2)+6*x2*y1+(-2*dd2-dd1)*x1^2*x2)/(6*x1-6*x2);
  */

        // FIXME simplify

        p.a = (dd1 - dd2) / (6. * p1x - 6. * p2x);

        p.b = (dd2 * p1x - dd1 * p2x) / (2. * p1x - 2. * p2x);

        p.c = (6. * p1y - 6. * p2y + (dd2 + 2. * dd1) * p2x * p2x
                + (2. * dd1 - 2. * dd2) * p1x * p2x - (2. * dd2 + dd1) * p1x * p1x)
                / (6. * p1x - 6. * p2x);

        p.d = -(p1x * ((dd2 + 2. * dd1) * p2x * p2x - 6. * p2y) + 6. * p2x * p1y
                - (2. * dd2 + dd1) * p1x * p1x * p2x)
                / (6. * p1x - 6. * p2x);
    }

    void set_quadratic_poly(PolyS p, double px, double py,
                            double d, double dd) {
        p.a = 0;
        p.b = dd / 2.0;
        p.c = -px * dd + d;
        p.d = 0.5 * px * px * dd - px * d + py;
    }

    double interpolate_nearest(int d, double x) {
        switch (d) {
            case (0):
                return super.get_y_value(super.get_nearest(x));

            default:
                return 0.0;
        }
    }

    double interpolate_linear(int d, double x) {
        int di = super.get_interval(x);

        if (di == 0)
            di++;
        else if (di == super.get_count())
            di--;

        switch (d) {
            case (0): {
                double mu
                        = (x - super.get_x_value(di - 1)) / (super.get_x_interval(di - 1));

                return super.get_y_value(di - 1) * (1.0 - mu) + super.get_y_value(di) * mu;
            }

            case (1): {
                return (super.get_y_value(di) - super.get_y_value(di - 1))
                        / (super.get_x_interval(di - 1));
            }

            default: {
                return 0.0;
            }
        }
    }

    public double interpolate_quadratic(int d, double x) {
        PolyS p = _poly.get(super.get_nearest(x));

        switch (d) {
            case (0):
                return x * (p.b * x + p.c) + p.d;

            case (1):
                return 2.0 * p.b * x + p.c;

            case (2):
                return 2.0 * p.b;

            default:
                return 0.0;
        }
    }

    double update_quadratic(int d, double x) {
        ArrayList<PolyS> poly = _poly;

        if (super.get_count() < 3)
            throw new IllegalStateException("data set doesn't contains enough data");

        // FIXME allocate PolyS objects
        poly.ensureCapacity(super.get_count());

        set_linear_poly(poly.get(0), super.get_x_value(0), super.get_y_value(0),
                super.get_x_value(1), super.get_y_value(1));

        int i;

        for (i = 1; i < super.get_count() - 1; i++) {
            double p1x = (super.get_x_value(i - 1) + super.get_x_value(i)) / 2.0;
            double p1y = (super.get_y_value(i - 1) + super.get_y_value(i)) / 2.0;

            double p3x = (super.get_x_value(i) + super.get_x_value(i + 1)) / 2.0;
            double p3y = (super.get_y_value(i) + super.get_y_value(i + 1)) / 2.0;

            set_quadratic_poly(poly.get(i), p1x, p1y, super.get_x_value(i),
                    super.get_y_value(i), p3x, p3y);
        }

        set_linear_poly(poly.get(i), super.get_x_value(i - 1), super.get_y_value(i - 1),
                super.get_x_value(i), super.get_y_value(i));

        // FIXME
        //this_->_interpolate = &Interpolate1d::interpolate_quadratic;

        return interpolate_quadratic(d, x);
    }

    double interpolate_cubic(int d, double x) {
        PolyS p = _poly.get(super.get_interval(x));

        switch (d) {
            case (0):
                return ((p.a * x + p.b) * x + p.c) * x + p.d;

            case (1):
                return (3.0 * p.a * x + 2.0 * p.b) * x + p.c;

            case (2):
                return 6.0 * p.a * x + 2.0 * p.b;

            case (3):
                return 6.0 * p.a;

            default:
                return 0.0;
        }
    }

    double update_cubic_simple(int d, double x) {
        ArrayList<PolyS> poly = this._poly;

        int n = super.get_count();

        if (n < 4)
            throw new IllegalStateException("data set doesn't contains enough data");

        // FIXME
        poly.ensureCapacity(n + 1);

        Vector2 vm1 = new Vector2(super.get_x_value(0), super.get_y_value(0));
        Vector2 vm2 = vm1;
        Vector2 v = new Vector2(super.get_x_value(1), super.get_y_value(1));
        Vector2 vp1 = new Vector2(super.get_x_value(2), super.get_y_value(2));

        double d1 = (v.y() - vm1.y()) / (v.x() - vm1.x());
        double d2 = (vp1.y() - vm1.y()) / (vp1.x() - vm1.x());

        // extrapolation
        set_linear_poly(poly.get(0), vm1.x(), vm1.y(), d1);

        // first segment
        set_cubic_poly(poly.get(10), vm1.x(), vm1.y(), v.x(), v.y(), d1, d2);

        for (int i = 2; i < n - 1; i++) {
            vm2 = vm1;
            vm1 = v;
            v = vp1;
            vp1 = new Vector2(super.get_x_value(i + 1), super.get_y_value(i + 1));

            d1 = d2;
            d2 = (vp1.y() - vm1.y()) / (vp1.x() - vm1.x());

            set_cubic_poly(poly.get(i), vm1.x(), vm1.y(), v.x(), v.y(), d1, d2);
        }

        d1 = d2;
        d2 = (vp1.y() - v.y()) / (vp1.x() - v.x());

        // last segment
        set_cubic_poly(poly.get(n - 1), v.x(), v.y(), vp1.x(), vp1.y(), d1, d2);

        // extrapolation
        set_linear_poly(poly.get(n), vp1.x(), vp1.y(), d2);

        return interpolate_cubic(d, x);
    }

    double update_cubic (int d, double x)
    {
        ArrayList<PolyS> poly = this._poly;

        int n = super.get_count ();

        if (n < 4)
            throw new IllegalStateException ("data set doesn't contains enough data");

        // FIXME
        poly.ensureCapacity (n + 1);

        double d0
                = (super.get_y_value (1) - super.get_y_value (0)) / super.get_x_interval (0);
        double dn = (super.get_y_value (n - 1) - super.get_y_value (n - 2))
              / super.get_x_interval (n - 2);
        double[] dd = new double[n];

        compute_cubic_2nd_deriv(cubic_2nd_deriv_init_e.Cubic2ndDerivFirst, n, dd, d0, dn);

        set_linear_poly (poly.get(0), super.get_x_value (0), super.get_y_value (0), d0);

        for (int i = 1; i < n; i++)
            set_cubic_poly2 (poly.get(i), super.get_x_value (i - 1), super.get_y_value (i - 1),
            super.get_x_value (i), super.get_y_value (i), dd[i - 1], dd[i]);

        set_linear_poly (poly.get(n), super.get_x_value (n - 1), super.get_y_value (n - 1),
            dn);

        return interpolate_cubic (d, x);
    }

}
