package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.valueobjects.DniResult;
import org.pe.nextcar.iam.interfaces.rest.resources.DniDataResource;

public class DniDataResourceFromEntityAssembler {
    public static DniDataResource toResourceFrom(DniResult result) {
        return new DniDataResource(
                result.dni(),
                result.nombres(),
                result.apellidoPaterno(),
                result.apellidoMaterno()
        );
    }
}
