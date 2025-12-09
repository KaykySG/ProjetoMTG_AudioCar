package com.vpk.backapimtgaudiocar.service.compat;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import java.util.List;

public interface CompatValidator {
    List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx);
}
