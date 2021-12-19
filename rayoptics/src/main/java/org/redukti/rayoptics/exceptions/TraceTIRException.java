package org.redukti.rayoptics.exceptions;

import org.redukti.rayoptics.math.Vector3;

public class TraceTIRException extends RuntimeException {
    public TraceTIRException(Vector3 d_in, Vector3 normal, Double n_in, Double n_out) {
        super();
    }
}
