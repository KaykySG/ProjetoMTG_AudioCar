package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.BalancoAudioDTO;
import com.vpk.backapimtgaudiocar.dto.ConfiguracaoDTO;
import com.vpk.backapimtgaudiocar.dto.ConfiguracaoRequestDTO;
import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.*;
import com.vpk.backapimtgaudiocar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    @Autowired
    private ConfiguracaoCompatibilidade compatibilidade;

    public List<ConfiguracaoDTO> listarTodas() {
        return configuracaoRepository.findAll()
                .stream()
                .map(ConfiguracaoDTO::new)
                .toList();
    }


    public Optional<ConfiguracaoDTO> buscarPorId(UUID id) {
        return configuracaoRepository.findById(id)
                .map(ConfiguracaoDTO::new);
    }

    public Configuracao salvar(Configuracao configuracao) {
        return configuracaoRepository.save(configuracao);
    }

    public void deletar(UUID id) {
        configuracaoRepository.deleteById(id);
    }

    public double calcularOrcamentoTotal(UUID id) {
        return configuracaoRepository.findById(id).map(cfg -> {
            double total = 0;
            total += cfg.getSubwoofers().stream().mapToDouble(s -> s.getPreco() != null ? s.getPreco() : 0).sum();
            total += cfg.getAltoFalantes().stream().mapToDouble(a -> a.getPreco() != null ? a.getPreco() : 0).sum();
            total += cfg.getModulos().stream().mapToDouble(m -> m.getPreco() != null ? m.getPreco() : 0).sum();
            total += cfg.getCrossovers().stream().mapToDouble(c -> c.getPreco() != null ? c.getPreco() : 0).sum();
            return total;
        }).orElse(0.0);
    }

    public double calcularOrcamentoTotalInterno(Configuracao cfg) {
        double total = 0;
        total += cfg.getSubwoofers().stream().mapToDouble(s -> s.getPreco() != null ? s.getPreco() : 0).sum();
        total += cfg.getAltoFalantes().stream().mapToDouble(a -> a.getPreco() != null ? a.getPreco() : 0).sum();
        total += cfg.getModulos().stream().mapToDouble(m -> m.getPreco() != null ? m.getPreco() : 0).sum();
        total += cfg.getCrossovers().stream().mapToDouble(c -> c.getPreco() != null ? c.getPreco() : 0).sum();
        return total;
    }

    public double calcularConsumoTotal(UUID id) {
        return configuracaoRepository.findById(id).map(cfg -> {
            return cfg.getModulos().stream().mapToDouble(ModuloAmplificador::getPotenciaPorCanalRms).sum();
        }).orElse(0.0);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AltoFalanteRepository altoFalanteRepository;

    @Autowired
    private SubwooferRepository subwooferRepository;

    @Autowired
    private ModuloAmplificadorRepository moduloRepository;

    @Autowired
    private CrossoverRepository crossoverRepository;

    public Configuracao salvarComRelacionamentos(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = new Configuracao();
        configuracao.setNomeConfiguracao(dto.getNome());
        configuracao.setVeiculo(dto.getVeiculo()); // ← novo
        configuracao.setRelatorioPdf(dto.getRelatorioPdf()); // ← novo

        usuarioRepository.findById(dto.getUsuarioId()).ifPresent(configuracao::setUsuario);

        // Alto-falantes
        if (dto.getAltoFalanteIds() != null) {
            dto.getAltoFalanteIds().forEach(id ->
                    altoFalanteRepository.findById(id).ifPresent(configuracao.getAltoFalantes()::add)
            );
        }

        // Subwoofers
        if (dto.getSubwooferIds() != null) {
            dto.getSubwooferIds().forEach(id ->
                    subwooferRepository.findById(id).ifPresent(configuracao.getSubwoofers()::add)
            );
        }

        // Módulos
        if (dto.getModuloIds() != null) {
            dto.getModuloIds().forEach(id ->
                    moduloRepository.findById(id).ifPresent(configuracao.getModulos()::add)
            );
        }

        // Crossovers
        if (dto.getCrossoverIds() != null) {
            dto.getCrossoverIds().forEach(id ->
                    crossoverRepository.findById(id).ifPresent(configuracao.getCrossovers()::add)
            );
        }

        // Calcular orcamento total e setar
        double orcamento = calcularOrcamentoTotalInterno(configuracao); // ← novo
        configuracao.setOrcamentoTotal(orcamento); // ← novo

        return configuracaoRepository.save(configuracao);
    }


    private ModuloAmplificador cloneModulo(ModuloAmplificador original) {
        ModuloAmplificador clone = new ModuloAmplificador();
        clone.setTipo(original.getTipo());
        clone.setPotenciaPorCanalRms(original.getPotenciaPorCanalRms());
        clone.setImpedanciaMinimaOhms(original.getImpedanciaMinimaOhms());
        clone.setPreco(original.getPreco());
        return clone;
    }

    private AltoFalante cloneAltoFalante(AltoFalante original) {
        AltoFalante clone = new AltoFalante();
        clone.setModelo(original.getModelo());
        clone.setPotenciaRmsW(original.getPotenciaRmsW());
        clone.setImpedanciaOhms(original.getImpedanciaOhms());
        clone.setFaixaFrequenciaHz(original.getFaixaFrequenciaHz());
        clone.setPreco(original.getPreco());
        return clone;
    }

    private Subwoofer cloneSubwoofer(Subwoofer original) {
        Subwoofer clone = new Subwoofer();
        clone.setModelo(original.getModelo());
        clone.setPotenciaRmsW(original.getPotenciaRmsW());
        clone.setImpedanciaOhms(original.getImpedanciaOhms());
        clone.setFaixaFrequenciaHz(original.getFaixaFrequenciaHz());
        clone.setPreco(original.getPreco());
        return clone;
    }

    private Crossover cloneCrossover(Crossover original) {
        Crossover clone = new Crossover();
        clone.setTipo(original.getTipo());
        clone.setFrequenciasCorteHz(original.getFrequenciasCorteHz());
        clone.setPreco(original.getPreco());
        return clone;
    }



    public Configuracao montarConfiguracaoSemSalvar(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = new Configuracao();
        configuracao.setNomeConfiguracao(dto.getNome());
        configuracao.setVeiculo(dto.getVeiculo());
        configuracao.setRelatorioPdf(dto.getRelatorioPdf());

        usuarioRepository.findById(dto.getUsuarioId()).ifPresent(configuracao::setUsuario);

        if (dto.getAltoFalanteIds() != null) {
            dto.getAltoFalanteIds().forEach(id ->
                    altoFalanteRepository.findById(id).ifPresent(af -> configuracao.getAltoFalantes().add(cloneAltoFalante(af)))
            );
        }
        if (dto.getSubwooferIds() != null) {
            dto.getSubwooferIds().forEach(id ->
                    subwooferRepository.findById(id).ifPresent(sw -> configuracao.getSubwoofers().add(cloneSubwoofer(sw)))
            );
        }
        if (dto.getModuloIds() != null) {
            dto.getModuloIds().forEach(id ->
                    moduloRepository.findById(id).ifPresent(mod -> configuracao.getModulos().add(cloneModulo(mod)))
            );
        }
        if (dto.getCrossoverIds() != null) {
            dto.getCrossoverIds().forEach(id ->
                    crossoverRepository.findById(id).ifPresent(cross -> configuracao.getCrossovers().add(cloneCrossover(cross)))
            );
        }

        return configuracao;
    }


    public List<ValidacaoCompatibilidadeDTO> validarCompatibilidadeInterna(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = montarConfiguracaoSemSalvar(dto);
        return compatibilidade.validarCompatibilidade(configuracao);
    }

    public BalancoAudioDTO calcularBalancoAudio(ConfiguracaoRequestDTO dto) {
        Configuracao cfg = montarConfiguracaoSemSalvar(dto);

        int totalGrave = cfg.getSubwoofers().stream()
                .filter(s -> s.getPotenciaRmsW() != null)
                .mapToInt(Subwoofer::getPotenciaRmsW)
                .sum();

        int totalVoz = cfg.getAltoFalantes().stream()
                .filter(a -> a.getPotenciaRmsW() != null)
                .mapToInt(AltoFalante::getPotenciaRmsW)
                .sum();

        double potenciaTotalModulos = cfg.getModulos().stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null)
                .mapToDouble(ModuloAmplificador::getPotenciaPorCanalRms)
                .sum();


        int somaTotal = totalGrave + totalVoz;

        System.out.println("\n\n\n\n\nsomatotal"+somaTotal+"\n\n\n\n\n\n\n");

        double percGrave = somaTotal > 0 ? (totalGrave * 100.0) / somaTotal : 0;
        double percVoz = somaTotal > 0 ? (totalVoz * 100.0) / somaTotal : 0;

        BalancoAudioDTO resposta = new BalancoAudioDTO();
        resposta.setPotenciaGraveTotal(totalGrave);
        resposta.setPotenciaVozTotal(totalVoz);
        resposta.setPercentualGrave(percGrave);
        resposta.setPercentualVoz(percVoz);
        resposta.setConsumo(potenciaTotalModulos);

        return resposta;
    }
}